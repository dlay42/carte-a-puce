package authClient;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Random;

/*** TODO
 * Taken from https://www.journaldev.com/741/
 * Change for SSL Sockets
 */
public class AuthClient extends Thread {
	
	// Server infos.
	private Socket clientSocket;
	private String serverAddr;
	private int serverPort;
	
	// Client/server communication canals
	BufferedReader inputStream;
	PrintWriter outputStream;

	private String message;
	private String response;
	private String lastResponse;

	// Session data
	private String sessionToken;
	
	// GETTERS & SETTERS
	public void setSessionToken(String token) {
		sessionToken = token;
	}
	
	public String getSessionToken() {
		return sessionToken;
	}
	
	public void setMessage(String msg) {
		message = msg;
	}
	
	public String getLastResponse() {
		return lastResponse;
	}
	
	public void setLastResponse(String msg) {
		lastResponse = msg;
	}
	
	// CONSTRUCTOR
	public AuthClient (String s, String argServerAddr, int argServerPort) {
		super(s);
		this.setDaemon(true);
		message = "";
		response = "";
		lastResponse = "";

		try {
			clientSocket = new Socket(argServerAddr, argServerPort);
			if (clientSocket != null)
				classLogger("Connection opened at server : " + argServerAddr + ":" + argServerPort);
			
			inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// THREAD IMPLEM.
	public void run() { 
		try {    
            while (true) {
            	Thread.sleep(250);
            	if (!message.isEmpty()) {
            		classLogger("Send message to client : " + message);
					// Write message when available
					outputStream.println(message);
                    
					// Read response
					try {
						response = inputStream.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}

                    message = "";
            	}
            	
            	if (!response.isEmpty()) {
            		classLogger("Response received from server : " + response);
            		lastResponse = response;
            		response = "";
            	}
            }
		} catch (Exception e) {
			//
		}
	}
	
	// UTILS
	public void classLogger(String msg) {
		System.out.println("[AuthClient]: " + msg);
	}
}
