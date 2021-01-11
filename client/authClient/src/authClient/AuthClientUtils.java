package authClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/***
 * AuthClientUtils.class
 * @author dlay
 * Static class with utility functions used for computation tasks
 */
public class AuthClientUtils {

	// Taken from http://boraji.com/how-to-convert-hex-to-ascii-in-java
	public static String hexToString(String hex) {
		hex = hex.replaceAll("\\s+","");
        if(hex.length()%2!=0){
           System.err.println("Invalid hex string.");
           return "";
        }
        
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < hex.length(); i = i + 2) {
           // Step-1 Split the hex string into two character group
           String s = hex.substring(i, i + 2);
           // Step-2 Convert the each character group into integer using valueOf method
           int n = Integer.valueOf(s, 16);
           // Step-3 Cast the integer value to char
           builder.append((char)n);
        }

        return builder.toString();
	}
	
    static public String byteArrayToString(byte[] byteTab){
	    String returnString = "";
    	for (byte c : byteTab) {
	    	returnString += String.format("%02X ", c);
	    }
	    return returnString;
	}
	
	public static String byteToHexString(byte b) {
		return String.format("%02X", b);
	}
	
	// SHA-256 signature
	// Taken from https://www.baeldung.com/sha-256-hashing-java
	public static String sha256Signature(String phrase) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(phrase.getBytes(StandardCharsets.UTF_8));
			return byteArrayToString(encodedhash).replaceAll("\\s+","");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

}
