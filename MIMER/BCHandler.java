import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import com.sun.net.ssl.internal.ssl.Provider;
import java.security.Security;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.thoughtworks.xstream.XStream;

/**
 * --------------------------------------------------------
 * 
 * 1. @author Prashant Padmashali @Date 23, October 2015
 * 
 * 2. Java version used, if not the official version for the class:
 * 
 * @Filename BCHandler.java
 * @version 1.8.0_60
 * @IDE_Version Version: Neon Milestone 1 (4.6.0M1) Build id: 20150820-1211
 * 
 * 3. Precise command-line compilation examples / instructions:
 * 
 * @set path="<drive>:\<path>\Java\jdk\bin"
 * @set classpath=%classpath%;.; javac BCHandler.java java BCHandler
 * 
 * 4. Precise examples / instructions to run this program:
 * 
 *      >javac BCHandler.java >Shim.bat
 * 
 * 
 * 5. List of files needed for running the program.
 * 
 *      a. MyWebServer.java b. mimer-data.xyz c. mimer_call.html d.
 *      checklist.html e. webserver.jks f. bchandler.jks g. bchandler.cert h.
 *      webserver.cert
 * 
 * 6.Notes: The Program is back channel handler which is built with the
 *      help of BCClient.java and Handler.java which is the code-snippet
 *      provided my Professor. Elliott's in
 *      "http://condor.depaul.edu/elliott/435/hw/programs/program-mimer.html"
 *      The Program is a small client application which is used to take a
 *      different mime type input and is parsed to xml. The program uses SSL socket to make the connection with the webserver.
 * 
 *      ----------------------------------------------------------
 **/


class myDataArray {
   int num_lines = 0;
    String[] lines = new String[10];
  }
public class BCHandler {
  /**
 * @XMLfileName - output file with the xml data
 * @toXmlOutputFile - printing the xml to the xmlfilename.
 *  this is the application which is invoked by the shim.bat when .xyz extensions files are open via browser
 */
  private static String XMLfileName = "mimer.output";
  private static PrintWriter toXmlOutputFile;
  private static File xmlFile;
  private static BufferedReader fromMimeDataFile;

  public static void main(String args[]) {
    /**
     * to display the data inside the mime type xyz file.
     */
    String serverName;
    if (args.length < 1) {
      serverName = "localhost";
    } else {
      serverName = args[0];
    }
    try {
      System.out.println("Executing the java application.");
      System.out.flush();
      System.out.println("Prashant's back channel Client.");
      System.out.println("Server name: " + serverName + ", Port: 2540 / 2570");
      
      /**
       * @argone -  the filename of the .xyz file which is passed to shim.bat.
       */
      Properties p = new Properties(System.getProperties());
      String argOne = p.getProperty("firstarg");
      System.out.println("First var is:" + argOne);

      fromMimeDataFile = new BufferedReader(new FileReader(argOne));
      myDataArray da = new myDataArray();
      int i = 0;
      
      // Only allows for five lines of data in input file plus safety:
      while (((da.lines[i++] = fromMimeDataFile.readLine()) != null) && i < 8) {
        System.out.println("Data is: " + da.lines[i - 1]);
      }
      da.num_lines = i - 1;
      System.out.println("i is: " + i + "\n");
      
      /**
       * this is to get the serialized output XML data type
       */
      XStream xstream = new XStream();
      String xml = xstream.toXML(da);
      System.out.println("\n\nHere is the XML version:");
      System.out.println(xml); // The output is in XML format and is done by xstream library
      //
      sendToBC(xml, serverName);

      xmlFile = new File(XMLfileName);
      if (xmlFile.exists() == true && xmlFile.delete() == false) {
        throw new IOException("XML file delete failed.");
      }
      xmlFile = new File(XMLfileName);
      if (xmlFile.createNewFile() == false) {
        throw (IOException) new IOException("XML file creation failed.");
      }
      
      toXmlOutputFile = new PrintWriter(new BufferedWriter(new FileWriter(XMLfileName)));
      toXmlOutputFile.println("First arg to Handler is: " + argOne + "\n");
      toXmlOutputFile.println(xml);
      System.out.println("closing the output file");
      toXmlOutputFile.close();

    } catch (IOException x) {
      x.printStackTrace();
    }
  }

  static void sendToBC(String sendData, String serverName) {
    
    /**
     * this method is to make the back channel connect to the server via SSL Socket
     */
    BufferedReader fromServer = null;
    PrintStream toServer = null;
    String textFromServer;

    try {
      Security.addProvider(new Provider());
    
    /**
     * Making an secured connection SSL to the webserver to send the data back 
     *the setProperty consists of the java keystore and the password
     */

      System.setProperty("javax.net.ssl.trustStore","bchandler.jks"); 
      System.setProperty("javax.net.ssl.trustStorePassword","bchandler");
      SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
      SSLSocket sock = (SSLSocket) sslsocketfactory.createSocket(serverName, 2570);
      
      toServer = new PrintStream(sock.getOutputStream());
      fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      toServer.print(sendData); // data being sent to webserver //data is the file content of .xyz 
      final String newLine = System.getProperty("line.separator");
      toServer.println(newLine + "end_of_xml");
      toServer.flush();
      
      System.out.println("Blocking on acknowledgment from Server... ");
      textFromServer = fromServer.readLine();
      if (textFromServer != null) {
        System.out.println(textFromServer);
      }
      sock.close();

    } catch (IOException x) {
      System.out.println("Socket error.");
      x.printStackTrace();
    }
  }
}