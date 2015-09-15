import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


public class InetClient {

	/**
	 * @author Prashant Padmashali
	 * CSC 435 InetClient
	 * Date		12 September 2016
	 * Java 	Version 1.7.0_79
	 * IDE 		Eclipse 3.8.1	
	 * 
	 * This is a client program which takes inputs as URL's(DNS) and gives back the IP address 
	 * mapped to the particular domain
	 * 
	 * ******Execution Steps in command line:******
	 * 
	 * set path="c:\programfiles\Java\jdk\bin"
	 * set classpath=%classpath%;.;
	 * javac InetClient.java
	 * java InetClient
	 */
	
	public static void main(String[] args) {
		
		String serverName;
		if(args.length <1) serverName ="localhost";
		else serverName =args[0];
		System.out.println("Prashant's Inet Client \n");
		System.out.println("Using Server "+serverName+" ,port:1567");
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		try {
			//user input received 
			String name;
			do{
				System.out.println("Enter a hostname/IP address or 'quit' to end: ");
				System.out.flush();
				name=in.readLine();
				if(name.indexOf("quit")< 0)
					
					getRemoteAddress(name,serverName);
			}while (name.indexOf("quit") < 0);
			System.out.println ("Cancelled by user request.");
		} catch(IOException x) {x.printStackTrace();}
	}
	
	//to convert the IP address to xxx.xxx.xxx.xxx readable format
	static String toText (byte ip[]) {
		StringBuffer result =new StringBuffer();
		for (int i=0; i<ip.length; ++i) {
			if (i>0)result.append(".");
			result.append(0xff & ip[i]);
	}
		return result.toString();
	}
	
	/**to check the URL's DNS address and display the IP address
	 * sock variable is for establishing a new socket connection.
	 * toServer variable is to send the user input domain to server.
	 * fromServer variable is to get back the output from the server as IP of the domain.
	**/
		static void getRemoteAddress( String name, String serverName) {
			Socket sock;
			BufferedReader fromServer;
			PrintStream toServer;
			String textFromServer;
		//socket connection setup
			try{
				sock =new Socket(serverName, 1567);
				fromServer=new BufferedReader(new InputStreamReader(sock.getInputStream()));
				toServer =new PrintStream(sock.getOutputStream());
				toServer.println(name);toServer.flush();
				for(int i=1; i<=3;i++){
					textFromServer =fromServer.readLine();
					if (textFromServer != null) System.out.println(textFromServer); //output from the worker thread for the user given domain
				}
				sock.close();
			}catch (IOException x) {
				System.out.println("socket error");
				x.printStackTrace();
			}
		}
	}

	

