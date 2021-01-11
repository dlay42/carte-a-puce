package authClient;

import java.awt.EventQueue;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/***
 * Program.class
 * @author dlay
 * Main auth. client program launching home page interface
 */
public class Program {
	public static void main(String[] args) throws NoSuchAlgorithmException{
		WindowHome homeWindow = new WindowHome();
	}
}