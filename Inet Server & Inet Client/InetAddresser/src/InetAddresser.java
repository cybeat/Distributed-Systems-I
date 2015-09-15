import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class InetAddresser {

	/**
	 * @author Prashant Padmashali
	 * CSC 435 InetAddresser
	 * Java 	Version 1.7.0_79
	 * IDE 		Eclipse 3.8.1	
	 * Standalone program of the client and server
	 * ******Execution Steps in command line:******
	 * 
	 * set path="c:\programfiles\Java\jdk\bin"
	 * set classpath=%classpath%;.;
	 * javac InetAddresser.java
	 * java InetAddresser
	 */
	
	public static void main(String[] args) {
		
		//calling the method to print the IP address of the user given DNS
		printLocalAddress();
		
		//User input data.
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		try{
			String name;
			do {
				System.out.println("Enter hostname/IP address or 'quit' to end ");
				System.out.flush();
				name=in.readLine();
				if (name.indexOf("quit") < 0)
					printRemoteAddress(name);
			}while (name.indexOf("quit") < 0);
			System.out.println("exit");
		}catch (IOException x){x.printStackTrace();}
	}
	
	//to print the local system host-name and the assigned IP address 
	static void printLocalAddress () {
		try {
			System.out.println("Prashant's INET addresser program\n");
			InetAddress me = InetAddress.getLocalHost();
			System.out.println("My local name is: " +me.getHostName ());
			System.out.println("My local IP address is:" + toText(me.getAddress ()));
		}catch(UnknownHostException x) {
				System.out.println ("I appear to be unknown to myself, Firewall?:");
				x.printStackTrace();
			}
		}
	
	//to check the URL's DNS address and display the IP address 
		static void printRemoteAddress(String name) {
			try {
				System.out.println("Looking up "+name+"...");
				InetAddress machine =InetAddress.getByName(name);
				System.out.println("Hostname :" +machine.getHostName());
				System.out.println("Host IP :" +toText(machine.getAddress()));
			}catch (UnknownHostException x) {
				System.out.println("failed to lookup" +name);
			}
		}
		
		//to convert the IP address to xxx.xxx.xxx.xxx readable format
		static String toText (byte ip[]) {
			StringBuffer result=new StringBuffer();
			for(int i=0;i<ip.length; ++i) {
				if (i>0) result.append(".");
				result.append(0xff & ip[i]);
			}
			return result.toString();
			}
		}
