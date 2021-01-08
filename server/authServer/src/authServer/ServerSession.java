package authServer;

import java.nio.charset.Charset;
import java.util.Random;

/*** TODO
 * To implement
 */
class ServerSession extends Thread { 

	/***
	 * SALT2
	 */
	private String sessionToken;
	
	public ServerSession (String s) {
		super(s);
		this.setDaemon(true);
		sessionToken = generateSessionToken();
	}
		
	public void run() { 
		try {

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
}
