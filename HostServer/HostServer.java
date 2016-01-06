import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * --------------------------------------------------------
 * 
 *		 1. @author Prashant Padmashali @Date 30, October 2015
 * 
 * 		2. Java version used, if not the official version for the class:
 * 
 * 			@Filename HostServer.java
 * 			@version 1.8.0_60
 * 			@IDE_Version Version: Neon Milestone 1 (4.6.0M1) Build id: 20150820-1211
 * 
 *      3. Precise command-line compilation examples / instructions:
 * 
 * 			@set path="<drive>:\<path>\Java\jdk\bin"
 *			@set classpath=%classpath%;.; javac HostServer.java java HostServer
 * 
 *      4. Precise examples / instructions to run this program:
 * 
 *      >javac HostServer.java >java HostServer
 * 
 * 
 *      5. List of files needed for running the program.
 * 
 *      a.HostServer.java b.Webclient(web browser)
 * 
 *      5. Notes: The Program HostServer which is built with code provided my Professor. Elliott's in
 *      "http://condor.depaul.edu/elliott/435/hw/programs/hostserver/HostServer-Reagan.java.txt"
 *      
 *      This Program lets us know how to save the state when the multiple connection are made and also it lets us
 *      go back to the previous connected port without loosing the state... It allows multiple IP's to connect to a server
 *      and the main server knows which client's have been connected.
 *      ----------------------------------------------------------
 **/


public class HostServer {
	/**
	 * This class connects on port 1565 and got every call/message from the client
	 * it increments the ports from 3000
	 */
	public static int NextPort = 3000; // port increment starts from 3000 as it
										// is specified

	public static void main(String[] a) throws IOException {
		int q_len = 6;
		int port = 1565;
		Socket sock;

		@SuppressWarnings("resource")
		ServerSocket servsock = new ServerSocket(port, q_len);
		System.out.println("Prashant's Hostserver port 1565.");
		System.out.println("Connect from 1 to 3 browsers using \"http:\\\\localhost:1565\"\n");
		while (true) {
			NextPort = NextPort + 1;// increment to next port
			sock = servsock.accept();
			System.out.println("Starting AgentListener at port " + NextPort);
			new AgentListener(sock, NextPort).start(); // a new agent is create
														// to receive incoming
														// ports
		}
	}
}

class AgentWorker extends Thread {
	/**
	 * it is called when a new connection is made if x number of clients are
	 * open then x connections are made and the port also increments 3000+
	 */
	Socket sock; // creating a new socket
	agentHolder parentAgentHolder; // it helps to keep the state and the count
									// of the state
	int localPort;

	AgentWorker(Socket s, int prt, agentHolder ah) {
		sock = s;
		localPort = prt;
		parentAgentHolder = ah;
	}

	public void run() {
		PrintStream out = null;
		BufferedReader in = null;
		String NewHost = "localhost"; // servername
		int NewHostMainPort = 1565; // port number
		String buf = ""; // hold the temp ports
		int newPort; // another port for the migration
		Socket clientSock;
		BufferedReader fromHostServer;
		PrintStream toHostServer;

		try {
			out = new PrintStream(sock.getOutputStream());
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			String inLine = in.readLine(); // input from the web client
			StringBuilder htmlString = new StringBuilder();
			System.out.println();
			System.out.println("Request line: " + inLine);
			if (inLine.indexOf("migrate") > -1) { // if input is migrate is
													// checked
				clientSock = new Socket(NewHost, NewHostMainPort); // new socket
																	// is
																	// created
				fromHostServer = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
				toHostServer = new PrintStream(clientSock.getOutputStream());
				toHostServer.println("Please host me. Send my port! [State=" + parentAgentHolder.agentState + "]");
				toHostServer.flush();

				for (;;) { // checking if the port is ready/ open
					buf = fromHostServer.readLine();
					if (buf.indexOf("[Port=") > -1) {
						break;
					}
				}

				/**
				 * taking the port and parse it to a integer and assign to the
				 * newport
				 */
				String tempbuf = buf.substring(buf.indexOf("[Port=") + 6, buf.indexOf("]", buf.indexOf("[Port=")));
				newPort = Integer.parseInt(tempbuf);
				System.out.println("newPort is: " + newPort);

				/**
				 * html data response to send to the web client
				 */
				htmlString.append(AgentListener.sendHTMLheader(newPort, NewHost, inLine));
				htmlString.append("<h3>We are migrating to host " + newPort + "</h3> \n");
				htmlString.append(
						"<h3>View the source of this page to see how the client is informed of the new location.</h3> \n");
				htmlString.append(AgentListener.sendHTMLsubmit());
				System.out.println("Killing parent listening loop.");
				ServerSocket ss = parentAgentHolder.sock; // @ss holds the
															// previously used
															// port and the
															// socket
				// close the port
				ss.close();

				/**
				 * the uri consist or a value ebing sent to the server is
				 * migrate or any input this keeps incrementing for every submit
				 */
			} else if (inLine.indexOf("person") > -1) {
				parentAgentHolder.agentState++;
				htmlString.append(AgentListener.sendHTMLheader(localPort, NewHost, inLine));
				htmlString.append(
						"<h3>We are having a conversation with state   " + parentAgentHolder.agentState + "</h3>\n");
				htmlString.append(AgentListener.sendHTMLsubmit());

			} else {
				htmlString.append(AgentListener.sendHTMLheader(localPort, NewHost, inLine));
				htmlString.append("You have not entered a valid request!\n");
				htmlString.append(AgentListener.sendHTMLsubmit());
			}
			AgentListener.sendHTMLtoStream(htmlString.toString(), out);
			sock.close();
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}
}


class agentHolder {
	/**
	 * this class is used to hold the state of the person and so that when the
	 * port's migrate we can have the state
	 */
	ServerSocket sock;
	int agentState;

	agentHolder(ServerSocket s) {
		sock = s;
	}
}


class AgentListener extends Thread {
	/**
	 * this class is to look for ports and sends the response to them whenever a
	 * call is made to 1565
	 */

	Socket sock;
	int localPort;

	AgentListener(Socket As, int prt) {
		sock = As;
		localPort = prt;
	}

	int agentState = 0; // initially state is set to 0

	public void run() {
		BufferedReader in = null;
		PrintStream out = null;
		String NewHost = "localhost"; // servername
		System.out.println("In AgentListener Thread");
		try {
			String buf;
			out = new PrintStream(sock.getOutputStream());
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			buf = in.readLine();
			
			/**
			 * this is to get the state and parsing it like the port above
			 */
			if (buf != null && buf.indexOf("[State=") > -1) {
				String tempbuf = buf.substring(buf.indexOf("[State=") + 7, buf.indexOf("]", buf.indexOf("[State=")));
				agentState = Integer.parseInt(tempbuf);
				System.out.println("agentState is: " + agentState);

			}
			
			/**
			 * this is the initial page which will be shown at localhost:1565
			 */
			System.out.println(buf);
			StringBuilder htmlResponse = new StringBuilder();
			htmlResponse.append(sendHTMLheader(localPort, NewHost, buf));
			htmlResponse.append("Now in Agent Looper starting Agent Listening Loop\n<br />\n");
			htmlResponse.append("[Port=" + localPort + "]<br/>\n");
			htmlResponse.append(sendHTMLsubmit());
			sendHTMLtoStream(htmlResponse.toString(), out);

			/**
			 * creating a new serversocket and saving the state
			 */
			ServerSocket servsock = new ServerSocket(localPort, 2);
			agentHolder agenthold = new agentHolder(servsock);
			agenthold.agentState = agentState;

			while (true) {
				sock = servsock.accept();
				System.out.println("Got a connection to agent at port " + localPort);
				new AgentWorker(sock, localPort, agenthold).start();// new  worker is started when a connection  is received

			}

		} catch (IOException ioe) {
			System.out.println("Either connection failed, or just killed listener loop for agent at port " + localPort);
			System.out.println(ioe);
		}
	}

	/**
	 * 
	 * @param localPort the incremented port is stored here
	 * @param NewHost   is the localhost the server Ip to which it is transferred
	 * @param inLine    the URI which is the GET request
	 */
	static String sendHTMLheader(int localPort, String NewHost, String inLine) {
		StringBuilder htmlString = new StringBuilder();
		htmlString.append("<html><head> </head><body>\n");
		htmlString.append("<h2>This is for submission to PORT " + localPort + " on " + NewHost + "</h2>\n");
		htmlString.append("<h3>You sent: " + inLine + "</h3>");
		htmlString.append("\n<form method=\"GET\" action=\"http://" + NewHost + ":" + localPort + "\">\n");
		htmlString.append("Enter text or <i>migrate</i>:");
		htmlString.append("\n<input type=\"text\" name=\"person\" size=\"20\" value=\"YourTextInput\" /> <p>\n");
		return htmlString.toString();
	}

	static String sendHTMLsubmit() {
		return "<input type=\"submit\" value=\"Submit\"" + "</p>\n</form></body></html>\n";
	}

	/**
	 * response headers are set and sent the content length is total length of
	 * the contents being sent
	 */
	static void sendHTMLtoStream(String html, PrintStream out) {

		out.println("HTTP/1.1 200 OK");
		out.println("Content-Length: " + html.length());
		out.println("Content-Type: text/html");
		out.println("");
		out.println(html);
	}

}
