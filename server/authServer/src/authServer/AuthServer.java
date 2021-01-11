package authServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*** TODO
 * Taken from https://www.journaldev.com/741/
 * Change for SSL Sockets
 */
public class AuthServer {
    private static ServerSocket server;
    private static int port;

    // Client session to handle
    private ArrayList<ServerSession> clientSessions = new ArrayList<>();
    private ExecutorService threadExecutors = Executors.newFixedThreadPool(250);
    
	public AuthServer (int argPort) {
		port = argPort;
	}
	
	public void startServer() throws IOException {
		server = new ServerSocket(port);
		while (true) {
			classLogger("Starting server on port " + port + " - waiting for connections...");
			Socket newClient = server.accept();
			classLogger("A client has reached the server");
			ServerSession clientSession = new ServerSession(newClient);
			clientSessions.add(clientSession);
			
			threadExecutors.execute(clientSession);
		}
	}
		
    private void classLogger(String msg) {
    	System.out.println("[AuthServer]: " + msg);
    }
}
