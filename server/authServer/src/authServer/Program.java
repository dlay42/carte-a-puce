package authServer;

import java.io.IOException;

public class Program {

    public static void main(String args[]) throws IOException, ClassNotFoundException{
    	AuthServer serverSocket = new AuthServer("AuthServer");
    	serverSocket.start();
    	
    	while(true) {
    		
    	}
    }
}
