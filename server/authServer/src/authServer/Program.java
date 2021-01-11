package authServer;

import java.io.IOException;

public class Program {

    public static void main(String args[]) throws IOException, ClassNotFoundException{
    	AuthServer serverSocket = new AuthServer(9876);
    	serverSocket.startServer();
    }
}
