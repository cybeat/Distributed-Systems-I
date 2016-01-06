import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**--------------------------------------------------------

1. @author Prashant Padmashali @Date 27, September 2015

2. Java version used, if not the official version for the class:
	
	@Filename JokeAdminClient.java
	@version 1.8.0_60 
	@IDE_Version Version: Neon Milestone 1 (4.6.0M1) Build id: 20150820-1211

3. Precise command-line compilation examples / instructions:

	@set path="<drive>:\<path>\Java\jdk\bin"
	@set classpath=%classpath%;.; 
	javac JokeClientAdmin.java
	java JokeClientAdmin

4. Precise examples / instructions to run this program:

> javac JokeServer.java 
> java JokeServer
> javac JokeClient.java
> java JokeClient
> javac JokeClientAdmin.java
> java JokeClientAdmin

For JokeAdminClient the inputs which can be given to change the mode are 
‘Joke’ or ‘proverb’ or ‘maintenance’ or ‘shutdown’ or ‘quit’

All acceptable commands are displayed on the various consoles.

This runs across machines, in which case you have to pass the IP address of
the server to the clients. For example, if the server is running at
140.192.1.22 then you would type:

> java JokeClient 140.192.1.22
> java JokeClientAdmin 140.192.1.22

5. List of files needed for running the program.

 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java

5. Notes:

The Program is a joke Admin Client which is built with the help of InetClient.java also the socket implementation of inetserver and inetclient is used as reference
(http://condor.depaul.edu/elliott/435/hw/programs/inet/InetPDF-C.pdf)the admin can control the server's behavior to Joke,Proverb and Maintenance Mode. In return to 
which the server will return the output to the client.
----------------------------------------------------------**/
public class JokeAdminClient {

	public static void main(String[] args) throws IOException {
		// @sName- ServerName of the computer.assigned as localhost
		String sName;
		if (args.length<1) sName="localhost";
		else sName=args[0];
		System.out.println("Prashant's Admin Client \n");
		BufferedReader in =new BufferedReader(new InputStreamReader(System.in));
		try{
			String sMode; // to store the mode input from admin
			do{
				
				System.out.println("Enter server mode.\n (1) Joke {Default Mode} \n (2) Proverb \n (3) Maintenance \n (4) 'quit' to end ");
				System.out.flush();
				sMode=in.readLine();
				if (sMode.indexOf("quit")<0) //if quit is typed the admin client closes
				modeControl(sName,sMode);
			}while (sMode.indexOf("quit")<0);
			System.out.println("Cancelled by user request");
		} catch (IOException x) {x.printStackTrace();}
	}
	/**
	 * @sMode-servermode
	 * this is to establish connection between jokeserver and adminclient
	 */
	public static void modeControl(String sName, String sMode) {
		Socket sock;
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
		try{
			sock =new Socket(sName,4242);//connecting to server @port 4242 and servername
			fromServer=new BufferedReader(new InputStreamReader(sock.getInputStream()));
			toServer=new PrintStream(sock.getOutputStream());
			toServer.println(sMode);toServer.flush();//sending the server mode to the Server
			//for(int i=1;i<=3;i++){
			textFromServer=fromServer.readLine();
			if(textFromServer !=null) System.out.println(textFromServer);
		//}
		sock.close();
		}catch (IOException x) {
			System.out.println("socket error");
			x.printStackTrace();
		}
	}
}