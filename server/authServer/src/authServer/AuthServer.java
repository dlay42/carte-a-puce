package authServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Random;

/*** TODO
 * Taken from https://www.journaldev.com/741/
 * Change for SSL Sockets
 */
public class AuthServer extends Thread {
	private static AuthDatabase dbConnection;
    private static ServerSocket server;
    private static int port = 9876;
    
    /***
     * Message flag
     * Taille : 4
     */
    private String flag;
    private String data;

	public AuthServer (String s) {
		super(s);
		this.setDaemon(true);
		dbConnection = new AuthDatabase();
	}
		
	public void run() { 
		
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			classLogger("Waiting for the client request");
			String[] parsedData;
			String 	rcvName = "",
					rcvLastName = "",
					rcvSalt1 = "",
					rcvRch = "";
			while(true){
	            //creating socket and waiting for client connection (bloquant)
	            Socket socket = server.accept();
	            
	            // Open listening stream
	            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	            String message = (String) ois.readObject();

	            // Open writing stream
	            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	            
	            classLogger("Received '" + message + "' from client");
	            if (message.length() > 4) {
	            	
	                flag = message.substring(0, 4);
	                
	                // Parse data
	                data = message.substring(4);
	                parsedData = data.split(";");
	                
		            switch(flag) {
	            	/***
	            	 * Hello:
	            	 * HEL1 + <NOM>;<PRENOM>
	            	 * TODO: add biometric histo.
	            	 */
	            	case "HEL1":
	            		rcvName = parsedData[1];
	            		rcvLastName = parsedData[2];
	            		break;
	            	/***
	            	 * Registration:
	            	 * REG1 + <NOM>;<PRENOM>;<SALT1>
	            	 */
	            	case "REG1":
	            		rcvName = parsedData[1];
	            		rcvLastName = parsedData[2];
	            		rcvSalt1 = parsedData[3];
	            		
	            		if (dbConnection.insertUserRow(rcvName, rcvLastName, rcvSalt1) == 0) {
	            			Thread.sleep(100);
	            			classLogger("Send response to client : REG1;OK");
	            			oos.writeObject("REG1;OK");
	            		} else {
	            			Thread.sleep(100);
	            			classLogger("Send response to client : REG1;KO");
	            			oos.writeObject("REG1;KO");
	            		}
	            		break;
	            	/***
	            	 * Login phase #1:
	            	 * LOG1 + <H(SALT2 + SALT1)>
	            	 */
	            	case "LOG1":
	            		rcvRch = parsedData[1];
	            		break;
	            		
	            	default:
	            		//
		            }
	            }
	            
	            

	            // Cleanup
	            ois.close();
	            oos.close();
	            socket.close();
	            if(message.equalsIgnoreCase("exit"))
	            	break;
	        }
			
			classLogger("Socket server terminated.");
	        server.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	} 

	/***
	 * Taken from https://www.baeldung.com/java-random-string
	 */
	private String generateSessionToken() {
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));
        return String.valueOf(generatedString.hashCode());
	}
	
    private void classLogger(String msg) {
    	System.out.println("[AuthServer]: " + msg);
    }
    
}
