import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

	/**
	 * @author Prashant Padmashali
	 * CSC 435 InetServer
	 * Date		12 September 2016
	 * Java 	Version 1.7.0_79
	 * IDE 		Eclipse 3.8.1	
	 * This is a server side program which receives inputs from the client program and converts the domain to the IP address
	 * and sends the output to the client, also the program multi-threads the workers to handle new connections
	 * 
	 * ******Execution Steps in command line:******
	 * 
	 * set path="c:\programfiles\Java\jdk\bin"
	 * set classpath=%classpath%;.;
	 * javac InetServer.java
	 * java InetServer
	 */

public class InetServer {
/**
 *controlSwitch	variable makes sure that all the input from client is being continuously received.
 *port variable is the port number in which the server will be running and client will be listening.
 */
	public static boolean controlSwitch =true;
	public static void main(String a[]) throws IOException {
		int q_len =6;
		int port =1567;
		Socket sock;
		
		//initiating a socket to connect to client
		ServerSocket servsock =new ServerSocket(port, q_len);
		System.out.println("Prashant's Inet server starting up, listening at port 1567 \n");
		while(controlSwitch) {
			//to accept a new connection from client and following new connections
			sock=servsock.accept();
			//worker is assigned to the new accepted connection
			if (controlSwitch) 
				new Worker(sock).start();
			
			try{Thread.sleep(10000);} 
			catch(InterruptedException ex){}
		}
	}
}

//Worker thread
	class Worker extends Thread {
		Socket sock;
		Worker (Socket s) {sock =s;}
		public void run() {
			PrintStream out =null;
			BufferedReader in =null;
		//initiating input stream to read and output stream to print on the socket connection 
			try { 
				in=new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out=new PrintStream(sock.getOutputStream());
				if (InetServer.controlSwitch !=true){
					System.out.println("Listener is now shutting down as per client request.");
					out.println("Server is shutting down. Bye!Bye!");
				}
				else try{
					String name; 
					name =in.readLine();//string variable name, which hold the domain value requested by the client
					if (name.indexOf("shutdown")> -1) {
						InetServer.controlSwitch =false;
						System.out.println("Worker a shutdown request");
						out.println("Shutdown request has been noted by worker");
						out.println("Please send final shutdown request to listener");
					}
					else{
						System.out.println("Looking up " +name);
						printRemoteAddress(name, out);
					}
				}catch (IOException x){
					System.out.println("Server read error");
					x.printStackTrace();
					}
				sock.close();
			}catch(IOException ioe) {System.out.println(ioe);}
		}
		//to display the hostname and IP address for the client's request
		static void printRemoteAddress (String name, PrintStream out) {
			try{
				out.println("Looking " +name+ " ....");
				InetAddress machine=InetAddress.getByName(name);
				out.println("Hostname : " +machine.getHostName());
				out.println("Host IP : " +toText (machine.getAddress()));
			}catch(UnknownHostException ex) {
				out.println("Failed in attempt to look up" +name);
			}
		}
		
		//to convert the IP address to xxx.xxx.xxx.xxx readable format
		static String toText(byte ip[]){
			StringBuffer result=new StringBuffer();
			for(int i=0;i<ip.length;++i){
				if(i>0) result.append(".");
			result.append(0xff & ip[i]);
			}
		return result.toString();
	}
}

