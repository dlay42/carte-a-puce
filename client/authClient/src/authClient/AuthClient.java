package authClient;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	
	private String message;
	private String response;
	private String lastResponse;

	public AuthClient (String s) {
		super(s);
		this.setDaemon(true);
		message = "";
		response = "";
		lastResponse = "";
	}
			
	public void run() { 
		try {
	        //get the localhost IP address, if server is running on some other IP, you need to use that
	        InetAddress host = InetAddress.getLocalHost();
	        Socket socket = null;
	        ObjectOutputStream oos = null;
	        ObjectInputStream ois = null;
	        
            while (true) {
            	Thread.sleep(100);
            	if (!message.isEmpty()) {
                    //establish socket connection to server
                    socket = new Socket(host.getHostName(), 9876);
                	
                    //write to socket using ObjectOutputStream
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);
                    
                    //read the server response message
                    ois = new ObjectInputStream(socket.getInputStream());
                    response = (String) ois.readObject();

                    // Cleanup
                    ois.close();
                    oos.close();
                    Thread.sleep(100);
                    message = "";
            	}
            	
            	if (!response.isEmpty()) {
            		classLogger("Response received from server : " + response);
            		Thread.sleep(100);
            		lastResponse = response;
            		response = "";
            	}
            }
	        
		} catch (Exception e) {
			//
		}
	}
	
	public void classLogger(String msg) {
		System.out.println("[AuthClient]: " + msg);
	}

	public void setMessage(String msg) {
		message = msg;
	}
	
	public String getLastResponse() {
		return lastResponse;
	}
	
}
