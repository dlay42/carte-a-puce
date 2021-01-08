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
			String	sessionToken = "";
			String  userId = "";
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
						* HEL1 + <PRENOM>;<NOM>
						* TODO: add biometric histo.
						* -> Received `whoami` info. 
						* -> Generate session token
						* -> Associate `whoami`and token in DB
						* (add database entry in `auth_session` table)	
						*/
						case "HEL1":
							rcvName = parsedData[1];
							rcvLastName = parsedData[2];

							for (int i = 0; i < 1000; i++) {
								sessionToken += generateSessionToken();
							}
							
							// Get userId
							userId = dbConnection.userExistsNameLastName(rcvName, rcvLastName);

							if (!userId.isEmpty()) {
								if (dbConnection.insertSessionRow(sessionToken, userId) == 0) {
									Thread.sleep(100);
									classLogger("Send response to client : HEL1;" + sessionToken);
									oos.writeObject("HEL1;" + sessionToken);
								} else {
									Thread.sleep(100);
									classLogger("Send response to client : HEL1;KO");
									oos.writeObject("HEL1;KO");
								}
							}

							break;
						/***
						* Registration:
						* REG1 + <PRENOM>;<NOM>;<SALT1>
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
						* LOG1 + </DBDKF2/ H(SALT2 + SALT1)><NAME(provisoire)>;<LAST_NAME(provisoire)>
						*/
						case "LOG1":
							rcvRch = parsedData[1];
							rcvName = parsedData[2];
							rcvLastName = parsedData[3];
							
							// Get userId
							userId = dbConnection.userExistsNameLastName(rcvName, rcvLastName);
							
							if (!userId.isEmpty()) {
								String sessionTokenFound = dbConnection.getTokenUserId(userId);
								String hashPassword = dbConnection.getHashPasswordUserId(userId);
								String recomputedRch = String.valueOf(hashPassword.hashCode());
								/***
								 * Compute and compare with rch and grant or deny access
								 */				
								classLogger("Before derivation (recompute) : " + recomputedRch);
								for (int i = 0; i < 10; i++) {
									recomputedRch = String.valueOf((recomputedRch + sessionTokenFound).hashCode());
								}
								classLogger("After derivation (recompute) : " + recomputedRch);

								classLogger("Compare : " + recomputedRch + " and " + rcvRch);
								if (recomputedRch.equals(rcvRch)) {
									Thread.sleep(100);
									classLogger("Send response to client : LOG1;OK");
									oos.writeObject("LOG1;OK");
								} else {
									Thread.sleep(100);
									classLogger("Send response to client : LOG1;KO");
									oos.writeObject("LOG1;KO");
								}
								
							}
							
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
