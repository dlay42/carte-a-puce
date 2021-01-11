package authServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Random;

/***
 * 
 * @author dlay
 * NOTE: Programmer choices
 * - MESSAGES:
 *   - ';' as seperator
 * - FLAGS:
 *   - 4 bytes
 */

class ServerSession implements Runnable { 

	// Client/Server communication settings
	private Socket client;
	private BufferedReader inputStream;
	private PrintWriter outputStream;
	private AuthDatabase dbConnection;
	
	private String clientMessage;
	private String serverResponse;
	
	//Session informations
    /*** TODO
     * Implement 'whoami' info.
     */
	private int[] biometricHistogram;
	// SALT2
	private String sessionToken;
	private String name;
	private String lastName;

	// Constructor
	public ServerSession (Socket clientSocket) throws IOException {
		sessionToken = "";
		client = clientSocket;
		inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
		outputStream = new PrintWriter(client.getOutputStream(), true);
		dbConnection = 	new AuthDatabase("mysql", "localhost", 3306, "authdb", "authsrv", "azerty");
	}

	// Client listener 
	public void run() { 
		/**
		 * Parsed message vars.
		 *  - Message : <FLAG>;<DATA>
		 */
		String[] parsedMessage;
		String 	flag = "",
				data = "";
		String userId = "",
			   sharedPassword = "",
			   rch = "";
		
		try {
			while (true) {
				Thread.sleep(250);

				clientMessage = inputStream.readLine();
				
				if (clientMessage != null) {
					classLogger("Message received : " + clientMessage);
					// Parse input message
					parsedMessage = clientMessage.split(";");
					flag = parsedMessage[0];
					
					/**
					 * Interprets FLAG
					 * FLAGS:
					 * 	HELLO PHASE #1:
	            	 *   - HEL1 + <NOM>;<PRENOM>
		             *  	(TODO: add biometric histo.)
		             *  
		             *  REGISTRATION:
	            	 *   - REG1 + <NOM>;<PRENOM>;<SALT1>
	            	 *   
	            	 *  LOGIN PHASE #1:
	            	 *   - LOG1 + <H(SALT2 + SALT1)>
	            	 */

		            switch(flag) {
	            	case "HEL1":
	            		name = parsedMessage[1];
	            		lastName = parsedMessage[2];
		
						// Get userId
						userId = dbConnection.userExistsNameLastName(name, lastName);

						if (!userId.isEmpty()) {
							for (int i = 0; i < 32; i++) {
								sessionToken += generate8Random();
							}
							classLogger("Send response to client : HEL1;" + sessionToken);
							outputStream.println("HEL1;" + sessionToken);
						} else {
							classLogger("Send response to client : HEL1;KO");
							outputStream.println("HEL1;KO");
						}
						
	            		break;
	            	case "REG1":
	            		name = parsedMessage[1];
	            		lastName = parsedMessage[2];
	            		sharedPassword = parsedMessage[3];
	            		
	            		if (dbConnection.insertUserRow(name, lastName, sharedPassword) == 0) {
	            			classLogger("Send response to client : REG1;OK");
	            			outputStream.println("REG1;OK");
	            		} else {
	            			classLogger("Send response to client : REG1;KO");
	            			outputStream.println("REG1;KO");
	            		}
	            		break;
	            	case "LOG1":
						rch = parsedMessage[1];
						if (!userId.isEmpty()) {
							
							/*** TODO
							 * Add more identity factor such as 'whoami' informations
							 */
							String hashPassword = dbConnection.getHashPasswordNameLastName(name, lastName);
							String recomputedRch = 	hashPassword;
							
							/***
							 * Compute and compare with rch and grant or deny access
							 */
							for (int i = 0; i < 1000; i++) {
								recomputedRch = String.valueOf((recomputedRch + sessionToken).hashCode());
							}
							
							classLogger("Compare : " + recomputedRch + " and " + rch);
							
							if (recomputedRch.equals(rch)) {
								classLogger("Send response to client : LOG1;OK");
								outputStream.println("LOG1;OK");
							} else {
								classLogger("Send response to client : LOG1;KO");
								outputStream.println("LOG1;KO");
							}
						}
						break;
	            	default:
	            		//
		            }
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			/***
			 * Cleanup
			 */
			try {
				inputStream.close();
			} catch (IOException e) {
				//
			}
			outputStream.close();
		}
	}
	
	/***
	 * Taken from https://stackoverflow.com/questions/5392693
	 */
	private String generate8Random() {
		Random random = new Random();
		int n = 10000000 + random.nextInt(900000);
        return String.valueOf(n);
	}
	
    private void classLogger(String msg) {
    	System.out.println("[ServerSession]: " + msg);
    }
}
