package authServer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/***
 * AuthServerUtils.class
 * @author dlay
 * Static class with utility functions used for computation tasks
 */
public class AuthServerUtils {

    static public String byteArrayToString(byte[] byteTab){
	    String returnString = "";
    	for (byte c : byteTab) {
	    	returnString += String.format("%02X ", c);
	    }
	    return returnString;
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
