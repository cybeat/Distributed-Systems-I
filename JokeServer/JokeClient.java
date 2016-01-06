import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**--------------------------------------------------------

1. @author Prashant Padmashali @Date 27, September 2015

2. Java version used, if not the official version for the class:
	@Filename JokeClient.java
	@version 1.8.0_60 
	@IDE_Version Version: Neon Milestone 1 (4.6.0M1) Build id: 20150820-1211

3. Precise command-line compilation examples / instructions:

	@set path="<drive>:\<path>\Java\jdk\bin"
	@set classpath=%classpath%;.; 
	javac JokeClient.java
	java JokeClient

4. Precise examples / instructions to run this program:

> javac JokeServer.java 
> java JokeServer
> javac JokeClient.java
> java JokeClient
> javac JokeClientAdmin.java
> java JokeClientAdmin

All acceptable commands are displayed on the various consoles.

This runs across machines, in which case you have to pass the IP address of
the server to the clients. For exmaple, if the server is running at
140.192.1.22 then you would type:

> java JokeClient 140.192.1.22
> java JokeClientAdmin 140.192.1.22

5. List of files needed for running the program.

 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java

5. Notes:

The Program is a joke Client which is built with the help of InetClient.java also the socket implementation of inetserver and inetclient is used as reference
(http://condor.depaul.edu/elliott/435/hw/programs/inet/InetPDF-C.pdf)the Jokeclient is the end user application to which the 
joke or proverb is sent according to the set mode of the server, By default the server is in joke mode


----------------------------------------------------------**/
@SuppressWarnings("unused")

public class JokeClient {

	//code moved to server during code revision 
	
//private static ArrayList<String> jokes; 
//private static ArrayList<String> jokesCopy;

	/**
	 * @param args
	 */
	public static void main (String args[]) throws IOException {
		// TODO Auto-generated method stub
		String serverName; 		//@serverName- localhost name of the system
		//InetAddress machine=InetAddress.getLocalHost();
		if (args.length < 1) serverName = "localhost"; 
		else serverName = args[0];
		
		System.out.println("Prashant's Joke client \n");
		System.out.println("Using Server "+serverName+", port 4141"); 
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		
		//code moved to server during code revision 
		//jokes = new ArrayList<String>(5);
		//jokesCopy = new ArrayList<String>(5);

		System.out.println("Please enter your name. ");
		System.out.flush();
		String name;  // storing user entered name
		name=in.readLine();
		
		try{
			String looper;
			// loop for next joke or proverb or to quit. depending on user input
			do{ 
			System.out.println("enter 'quit' to close the application or Hit 'enter' key to proceed");
			System.out.flush();
			looper=in.readLine();
			if(!(looper.equals("quit")))
				getRemoteAddress(name,serverName);
			}while (!(looper.equals("quit")));
			System.out.println("Cancelled by user request.");
		}catch(IOException x) {x.printStackTrace();}
	}
	
	/**
	 * @sock- variable is for establishing a new socket connection.
	 * @toServer- variable is to send the user input domain to server.
	 * @fromServer- variable is to get back the output from the server as IP of the domain.
	**/
	
	static void getRemoteAddress( String name, String serverName) {
		Socket sock;		
		BufferedReader fromServer;	
		PrintStream toServer;
		String textFromServer;
		
		//socket connection setup to server at port 4141
		try{ 			
			sock= new Socket(serverName, 4141);
			fromServer=new BufferedReader(new InputStreamReader(sock.getInputStream()));
			toServer=new PrintStream(sock.getOutputStream());
			
			toServer.println(name);
	//		toServer.flush();
			
			//code moved to server during code revision 
			//Remove all the existing data from jokes
			//jokes.clear();

			//display joke or proverb sent from the server
			textFromServer=fromServer.readLine();
			System.out.println(textFromServer);
			
			
			sock.close();
			}catch(IOException x){
			System.out.println("Socket error");
			x.printStackTrace();
		}
	}
	//code moved to server during code revision 
	/**static void removeDuplicates(){
		for (String joke : jokesCopy) {
    		if (jokes.contains(joke)) {
        		jokes.remove(joke);
			}
		}
	}**/
}