import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;

/**--------------------------------------------------------

1. @author Prashant Padmashali @Date 27, September 2015

2. Java version used, if not the official version for the class:
	@Filename MyWebServer.java
	@version 1.8.0_60 
	@IDE_Version Version: Neon Milestone 1 (4.6.0M1) Build id: 20150820-1211

3. Precise command-line compilation examples / instructions:

	@set path="<drive>:\<path>\Java\jdk\bin"
	@set classpath=%classpath%;.; 
	javac MyWebServer.java
	java MyWebServer

4. Precise examples / instructions to run this program:

>javac MyWebServer.java
>java MyWebServer




5. List of files needed for running the program.

 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java

5. Notes:
The Program is a Web Server which is built with the help of InetServer.java and also with the code-snippet
provided my Professor. Elliott's in "http://condor.depaul.edu/elliott/435/hw/programs/program-webserver.html"
http://condor.depaul.edu/elliott/435/hw/programs/mywebserver/ReadFiles.java
also the socket implementation of JokeServer and inetserver is used as reference(http://condor.depaul.edu/elliott/435/hw/programs/inet/InetPDF-C.pdf)
to setup the client and server.
The program makes your computer a small webserver which is capable of handling multiple content types, adding two numbers.
It also has security measures to prevent directory traversal and xss attacks also the webserver provides security headers.

System.out.println has been used with every out.println so that all the actions can be logged both request and response.

----------------------------------------------------------**/
public class MyWebServer {

	/**
	 *controlSwitch	variable makes sure that all the input from client is being continuously received.
	 *port variable is the port number in which the server will be running and client will be listening.
	 */
	public static boolean controlSwitch = true;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int q_len = 6;	/* Number of requests for OpSys to queue */
		int port = 2540;
		Socket sock;	//socket connection setup

		ServerSocket servsock = new ServerSocket(port, q_len);
		System.out.println("Prashant's Web Server starting up, listening at port 2540 \n");
		while (controlSwitch) {
			sock = servsock.accept();	//to accept a new connection from client browser and following new upcoming connections
			if (controlSwitch)			//worker is assigned to the new accepted connection
				new Worker(sock).start();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException ex) {
			}

		}
		servsock.close();
	}

}

//the worker which will handle client request
class Worker extends Thread {
	/**
	 * @rootDirectory - to store the root directory.
	 * @currentDirectory - to know the location of the path of the opened file.
	 */
	private static File rootDirectory;
	private static String currentDirectory = new String();
	Socket sock;

	Worker(Socket s) {
		sock = s;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		PrintStream out;
		BufferedReader in;

		try {
			setRootDirectory();
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintStream(sock.getOutputStream());
			
/*			if(WebServer.controlSwitch!=true){
				System.out.println("Listener is now shutting down as per client request.");
			}
			out.println("Server is shutting down. Bye!Bye!");
			} 
		else {
*/
			/**
			 * @httprequest -to store the requested httprequest from the browser.
 			*/
			String httprequest = "";
			while (in.ready() || httprequest.length() == 0)
				httprequest += (char) in.read();

			System.out.println(httprequest);
			/**
			 * 
			 * to Send the Response to the browser for the request made
			 * To check whether the request is for cgi.
			 */
			if (!httprequest.contains("cgi?")) {
				httpResponse(httprequest, out);
			} else
				httpResponsecgi(httprequest, out);

			sock.close();
		} catch (Exception ioe) {
			System.out.println(ioe);
		}
	}

	/*
	 * private static void HttpRequest(String req){ 
	 * String filename;
	 * String check;
	 * public HttpRequest(String req) { 
	 * String[] splitter=req.split("\n");
	 * splitter= splitter[0].split(" "); filename =splitter[1];
	 * if(filename.equals(" ")){ return Directory(out, httprequest); } 
	 * else if
	 * (filename.equals(filename)){ return httpFile(out, httprequest); } 
	 * else
	 * return error(); System.out.println(splitter[0]); }
	 */
	/*
	 * String fileExt(String fileext){ 
	 * int ext=fileext.indexOf("."); String
	 * fext=fileext.substring(ext,fileext.length());
	 * if (fext.equalsIgnoreCase(".html")) 
	 * return "text/html"; else if
	 * (fext.equalsIgnoreCase(".png")) 
	 * return "image/png"; else if
	 * (fext.equalsIgnoreCase(".jpg") || fext.equalsIgnoreCase(".jpeg")) 
	 * return
	 * "image/jpeg"; else if (fext.equalsIgnoreCase(".gif")) 
	 * return "image/gif";
	 * else if (fext.equalsIgnoreCase(".txt")) 
	 * return "text/plain"; 
	 * else 
	 * return
	 * "Invalid file"; }
	 */

	private static String getFileExtension(File file) {
		/**
		 * @ext -to store the requested file's extension. 
		 * In reference:http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/io/Files.html#getFileExtension%28java.lang.String%29
		 * http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/io/Files.html#line.769
		 * the above mentioned URL has the method which from a java library(guava), to get the file extension
		 */
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}

	private static String contentType(File ext) {
		/**
		 * @ext- it is used as the returned value from the getFileExtension, which the File Extension for the requested file.
		 * This method helps us to define various content type  responses.
		 * the once required for the this MyWebServer Assignment are html,txt and wml
		 */
//		 File fileext; fileName=fileext.toString();
		 
		if (getFileExtension(ext).equals("html") || getFileExtension(ext).equals("htm")) { // check html file type
			return "text/html";																
		} else if (getFileExtension(ext).equals("txt") || getFileExtension(ext).equals("text")) { // check text file type
			return "text/plain";					
		} else if (getFileExtension(ext).equals("gif")) { // check gif image file type 
			return "image/gif";
		} else if (getFileExtension(ext).equals("png")) { //  check png image file type
			return "image/png";
		} else if (getFileExtension(ext).equals("jpg") || getFileExtension(ext).equals("jpeg")) { // check jpeg image file type type
			return "image/jpeg";
		} else if (getFileExtension(ext).equals("ico")) { //  check the favicon and icon file type
			return "image/x-icon";
		} else if (getFileExtension(ext).equals("wml")|| getFileExtension(ext).equals("xml")|| getFileExtension(ext).equals("xhtml")) { // check xml and wml file type
			return "text/vnd.wap.wml";
		} else if (getFileExtension(ext).equals("class")) {
			return "application/octet-stream";
		}else if (getFileExtension(ext).equals("java")) { // check java file type
				return "text/x-java-source,java";
		} else
			return "invalid file";
	}

	@SuppressWarnings("resource")
	private static void httpResponse(String httprequest, PrintStream out) throws Exception {
		/**
		 * @carriage- carriage return / linefeeds.
		 * @filename- to store the filename of the requested file from browser.
		 * This method sends the response to the request from the browser, it sends files, and can open to sub directories 
		 * recursively and access the files. 
		 */
		String carriage = "\r\n";
		String filename = "";
		StringTokenizer st = new StringTokenizer(httprequest);
//		String path="/WebServer/src/webserver";
		try {
			
//			 String[] splitter=httprequest.split(" "); filename=splitter[1];
			 
			if (st.hasMoreTokens() && st.nextToken().equalsIgnoreCase("GET") && st.hasMoreElements())
				filename = st.nextToken();
			else
				throw new FileNotFoundException();
			System.out.println("\n");

			StringBuilder header = new StringBuilder();
			StringBuilder body = new StringBuilder();

			/**
			 * if the filename has / which is the root directory the method called will print the directory tree 
			 */
			if (filename.endsWith("/"))
				httpResponseFile(filename, out);//calling to display files ,tree
			while (filename.indexOf("/") == 0)
				filename = filename.substring(1);
			filename = filename.replace('/', File.separator.charAt(0));
			InputStream readfile = new FileInputStream(filename);

			/**
			 * to avoid directory traversal security attack
			 */
			if (filename.indexOf("..") >= 0 || filename.indexOf(':') >= 0 || filename.indexOf('|') >= 0)
				throw new FileNotFoundException();
			System.out.println("\n");

			String bodyString = body.toString();
//			int bodySize = bodyString.getBytes().length;

			/**
			 * @file- variable type file, has the name of the file to be sent
			 * @head- response headers which are sent to the browser with every response 
			 * these headers have few security policy headers implemented.  
			 */
			File file = new File(filename);
			String[] head = { "HTTP/1.1 200 OK " + carriage, 
					"Server: Prashant's Server/1.0 " + carriage,
					"Content-Type:" + contentType(file) + carriage,
					"Content-Length: " + file.length()+100 + " " + carriage, 
					"X-XSS-Protection: 1; mode=block " + carriage,
					"X-Content-Type-Options: nosniff" + carriage, 
					"Connection: close " + carriage,
					carriage + " " + carriage };

			/**
			* to print the response header
			*/
			header.append(head[0]);
			header.append(head[1]);
			header.append(head[2]);
			header.append(head[3]);
			header.append(head[4]);
			header.append(head[5]);
			header.append(head[6]);
			header.append(head[7]);

			String headerString = header.toString();
			String directoryString = headerString.concat(bodyString);
			System.out.println("\n");
			System.out.println(directoryString);
			out.println(directoryString);

			/**
			 * to display the file in the browser.		
			 */
			byte[] a = new byte[4096];
			int n;
			while ((n = readfile.read(a)) > 0)
				out.write(a, 0, n);

			readfile.close();
			out.close();
		} catch (FileNotFoundException fne) {
			//fne.printStackTrace();
			//System.out.println("404 File Not Found");
		}
	}

	private static void setRootDirectory() {
		/**
		 * to show which is the root directory for the server
		 */
		try {
			Worker.rootDirectory = new File(".").getCanonicalFile();
			System.out.println("\n");
			System.out.println("Root Directory :" + Worker.rootDirectory.toString());
			System.out.println("\n");
		} catch (IOException ex) {
			System.out.println("\n");
			System.out.println(ex + ": Root directory cannot be set.");
			System.out.println("\n");
		}
	}

	public static String updateDirectory(String root, String file) {
		/**
		 * to show the current directory in which the file is 
		 */
		Worker.currentDirectory = root + file;
		System.out.println("\n");
		System.out.println("Current file :" + file);
		System.out.println("\n");
		System.out.println("Current working directory :" + Worker.currentDirectory);
		System.out.println("\n");
		return Worker.currentDirectory;
	}

	private static void httpResponseFile(String filename, PrintStream out) {
		/**
		 * @filename- is the requested file name
		 * @out- is to send out the print to the client
		 * this method is used to display the directory tree and the files
		 */
		// TODO Auto-generated method stub
		System.out.println("File name in httpResponseFile :" + filename);
		System.out.println("\n");
		updateDirectory(Worker.rootDirectory.toString(), filename);
		StringBuilder header = new StringBuilder();
		StringBuilder body = new StringBuilder();

		File file=new File(Worker.currentDirectory);
		File[] directoryDisplay = file.listFiles();

		body.append("<h3>Directory Tree of Prashant's WebServer Files " + file.toString() + "</h3>\n");
		System.out.println("<h3>Directory Tree of Prashant's WebServer Files " + file.toString() + "</h3>\n");
		
/*		  int filecounter=0; 
		  while(filecounter<directoryDisplay.length){
		  body.append("<a href="+directoryDisplay[filecounter].getName()+" > "+directoryDisplay[filecounter].getName()+" </a> <br>");
		  System.out.println("<a href="+directoryDisplay[filecounter].getName() +" > "+directoryDisplay[filecounter].getName()+" </a>");
		  filecounter++; }
*/
		 /**
		 * directory tree print and check for subdirectory 
		 * reference used @http://condor.depaul.edu/elliott/435/hw/programs/mywebserver/ReadFiles.java
		 */
		int fileCounter = 0;
		while (fileCounter < directoryDisplay.length) {
			File directoryFile = directoryDisplay[fileCounter];
			String directoryNameFile = directoryFile.getName();
			if (directoryNameFile.startsWith(".") == true) {
				fileCounter++;
				continue;
			}
			if (directoryFile.isDirectory() == true) {
				body.append("<a href=\" " + directoryNameFile + "/\">\\ " + directoryNameFile + "</a> <--Directory--> <br>\n");
			}
			if (directoryFile.isFile() == true) { 
				body.append("<a href=\" " + directoryNameFile + "\" > " + directoryNameFile + " </a> <--File--> <br>\n");
			}
			fileCounter++;
		}

		 /**
		 * to have a back button to go back to root directory of the server
		 */
		body.append("<a href=/><b>Go To Home</b></a><br>");

		/**
		 * response headers which is linked to the / or root directory
		 */
		String bodyString = body.toString();
		int bodySize = bodyString.getBytes().length;
		header.append("HTTP/1.1 200 OK \n");
		header.append("Server: Prashant's Server/1.0 \n");
		header.append("Content-Type:text/html \n");
		header.append("Content-Length: " + bodySize + " \n");
		header.append("X-XSS-Protection: 1; mode=block \n");
		header.append("X-Content-Type-Options: nosniff \n");
		header.append("\n");

		String headerString = header.toString();
		String directoryString = headerString.concat(bodyString);
		System.out.println(directoryString+"\n");
		out.println(directoryString);
		out.flush();
	}

	private void httpResponsecgi(String httprequest, PrintStream out) {
		/**
		 * @httprequest- it has the requested uri
		 *the method is used to split the values needed to do the addition calculation
		 */
		// TODO Auto-generated method stub
		 System.out.println("\n");

		 String carriage ="\r\n";
		 String filename;

		 /**
		  * splitting to get file name
		  */
		 String[] splitter=httprequest.split(" ");
		 filename=splitter[1];
		 File file=new File(filename);

		 /**
		  * splitting to get various tokens removed and to achieve the three required values
		  * @name -Name entered by the user
		  * @number1 -the 1st number to add
		  * @number2 -the 2nd number to add
		  * @add -total/result of number 1 and number 2
		  */		 
		 StringBuilder header = new StringBuilder();
		 StringBuilder body = new StringBuilder(); 
	
		 String[] cgisplit=filename.split("cgi");
		 	System.out.println(cgisplit[0]+" "+cgisplit[1]+" "+cgisplit[2]); //to check in logs
		 String cgi=cgisplit[2];
		 String[] cgiinfo=cgi.split("&");
		
		 /* String v1=cgiinfo[0];
		 String v2=cgiinfo[1];
		 String v3=cgiinfo[2];*/
		 
		 String[] n0=cgiinfo[0].split("=");
		 String name=n0[1];
		 
		 String[] n1=cgiinfo[1].split("=");
		 String num1=n1[1];
		 
		 String[] n2=cgiinfo[2].split("=");
		 String num2=n2[1];

		 int number1=Integer.parseInt(num1);
		 int number2=Integer.parseInt(num2);
		 int add=number1+number2;

		 /**
		  * this html body append is done to retain the values in the html format and to send new values for different calculations
		  */
		 body.append("<TITLE> CSC435 Prashant for Adding Nummber</TITLE>"+
		 "<BODY>"
		 + "<H1> Addition of Two Numbers </H1> "
		 + "<FORM method='GET' action='/cgi/addnums-fake.cgi'>"+
         "Enter your name and two numbers:"
         + "<INPUT TYPE='text' NAME='person' size=20 value='"+name+"'>"
         + "<P>"+
         "<INPUT TYPE='text' NAME='num1' size=5 value='"+number1+"'> <br>"
         + "<INPUT TYPE='text' NAME='num2' size=5 value='"+number2+"'> <br>"+
         "<INPUT TYPE='submit' VALUE='Submit Numbers'>"
         + "</FORM>");
		 body.append("<br>");
		 
			body.append("Hello "+name+", the addition result of requested number "+number1+" and "+number2+" is "+add+"\n");
			
			System.out.println("name ="+name+" add "+number1+" and "+number2+" = "+add+"\n");
			
			String bodyString = body.toString(); 
			int bodySize = bodyString.getBytes().length;

			/**
			 * response headers
			 */			
			header.append("HTTP/1.1 200 OK"+carriage);
			header.append("Content-Type: text/html"+carriage);
			header.append("Server: Prashant's Server/1.0"+carriage);
			header.append("Content-Length: " + bodySize + " "+carriage);
			header.append("X-XSS-Protection: 1; mode=block "+carriage);
			header.append("X-Content-Type-Options: nosniff "+carriage);
			header.append("\n");
			
			String headerString = header.toString();
			String directoryString = headerString.concat(bodyString);
			System.out.println(directoryString+"\n");
			out.println(directoryString);

	}

}