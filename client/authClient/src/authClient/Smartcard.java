package authClient;

import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class Smartcard {

	/***
	 * Demande du PIN à l'utilisateur non implémenté
	 * pour l'instant
	 */
	private byte[] csc0 = {(byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa};
    private CardTerminal cardReader;
    private Card smartcard;
    private String text=new String();
    private CardChannel channel;
    
	public enum smartcardProcessStatus {
	    READER_MISSING, WAITING, READING
	};
	
    private smartcardProcessStatus status = smartcardProcessStatus.READER_MISSING;
    
    /***
     * Constructor
     */
    public Smartcard() {
    	setStatus(smartcardProcessStatus.READER_MISSING);
    }
    
    public smartcardProcessStatus getStatus() {
    	return status;
    }
    
    public CardChannel getChannel() {
    	return channel;
    }
    
    public byte[]getCSC0() {
    	return csc0;
    }
    
    private void classLogger(String msg) {
    	System.out.println("[Smartcard]: " + msg);
    }
 
    
    private void statusLogger() {
    	switch(status) {
    	case READER_MISSING:
    		classLogger("Please, attach your card reader.");
    		break;
    	case WAITING:
    		classLogger("Please, insert your card.");
    		break;
    	case READING:
    		classLogger("Processing... We are reading your card informations.");
    		break;
    	default: 
    		classLogger("Unknown status.");
    	}
    }
    
    private void setStatus(smartcardProcessStatus newStatus) {
    	status = newStatus;
    	statusLogger();
    }
    
    public List<CardTerminal> getReaders() {
    	try {
    		return TerminalFactory.getDefault().terminals().list();
    	} catch (CardException e) {
    		return null;
    	}
    }
    
    /***
     * Checking methods
     */
    public List<CardTerminal> checkCardReader() {
    	
    	List<CardTerminal> availableReaders;
    	
    	do {
    		availableReaders = getReaders();
    	} while (availableReaders == null);
    	
    	cardReader = availableReaders.get(0);
    	classLogger("Card reader detected : " + cardReader.toString());
    	setStatus(smartcardProcessStatus.WAITING);
    	
    	return availableReaders;
    }
    
    public Card checkSmartcard() {

    	Card currentCard = null;
    	
    	do {
    		try {
				currentCard = cardReader.connect("T=0");
			} catch (CardException e) {
				//
			}
    	} while (currentCard == null);
    	
    	smartcard = currentCard;
    	classLogger("Card detected : " + AuthClientUtils.byteArrayToString(smartcard.getATR().getBytes()));
    	setStatus(smartcardProcessStatus.READING);
    	
    	return smartcard;
    }
    
    public void connectCard() {
    	channel = smartcard.getBasicChannel();
    }
    

/*
    public static void main(String[] args) {
    	Smartcard card = new Smartcard();
    	
    	card.checkCardReader();
    	card.checkSmartcard();

		card.connectCard();

		card.writeOnCard("az;ertyqsdfgh");
		
		System.out.println(card.readOnCard());

    }
*/
    /***
     * Vérifie le premier PIN (CSC0)
     * Si :
     *  - SW1 = 0x63 => Mauvais PIN
     *  - SW1 = 0x90 => PIN correct
     * Taille: 1 mot
     * SW2 = response[1]
     * SW1 = response[0]
     */
    public int verifyCSC0(byte[] pin) {

    	if (pin.length != 4) {
    		return -1;
    	}
    	
    	// Formattage de la commande
		byte[] startCommand = {(byte) 0x00,(byte) 0x20 ,(byte) 0x00 ,(byte) 0x07 ,(byte) 0x04};
		byte[] verifyCommand = new byte[startCommand.length + pin.length];
		System.arraycopy(startCommand, 0, verifyCommand, 0, startCommand.length);
		System.arraycopy(pin, 0, verifyCommand, startCommand.length, pin.length);
		
		// Execution de la commande
        CommandAPDU command = new CommandAPDU(verifyCommand);
        ResponseAPDU response;
		try {
			
			response = channel.transmit(command);
			String test = AuthClientUtils.byteToHexString(response.getBytes()[0]);
			if (AuthClientUtils.byteToHexString(response.getBytes()[0]).equals("90")) {
				classLogger("Correct PIN (CSC0)");
				return 0;
			} else {
				classLogger("Wrong PIN (CSC0)");
				return -1;
			}
		} catch (CardException e) {
			return -1;
		}
    }
    
	/***
	 * Ecriture sur la zone mémoire USER1 :
	 * -> de P2 = 0x1x (avec x \in [0,F] (16 valeurs))
	 * -> 16 mots = 16 * 4 = 64 bytes
	 */
    public int writeOnCard(String toWrite) {

		/***
		 * Devrait toujours être bon
		 * (PIN HARD CODED user prompt non-implémenté)
		 */
		if (verifyCSC0(csc0) == 0) {
			if (toWrite.length() > 64)
				return -1;

        	classLogger("Writing '" + toWrite + "' in USER1...");
			
			/***
			 * Bourrage (override)
			 */
			byte[] stringByteArray = toWrite.getBytes();
			byte[] toWriteByteArray = new byte[64];
			System.arraycopy(stringByteArray, 0, toWriteByteArray, 0, stringByteArray.length);

			/***
			 * POSITION : 16 + x
			 */
			byte p2 = 16;
			int dataCursor = 0, toWriteCursor = 0;
			
	    	
			byte[] startCommand = {(byte) 0x80, (byte) 0xDE, (byte) 0x00, p2, (byte) 0x04};
			byte[] writeCommand = new byte[startCommand.length + 4];
	    	byte[] data = new byte[5];
	    	
	    	CommandAPDU command; 
	    	for (byte i : toWriteByteArray) {

	    		
	    		data[dataCursor] = toWriteByteArray[toWriteCursor];

	    		dataCursor++;
	    		toWriteCursor++;

	    		if (dataCursor % 4 == 0) {
	    			dataCursor = 0;
	    			
	    			// Formattage de la commande
	    			startCommand[3] = p2;
	    			System.arraycopy(startCommand, 0, writeCommand, 0, startCommand.length);
	    			System.arraycopy(data, 0, writeCommand, startCommand.length, data.length - 1);
	    			
	    			// Execution de la commande
	    	    	command = new CommandAPDU(writeCommand);  
	    	        try {
	    	        	classLogger("Writing in USER1 storage at 0x" + AuthClientUtils.byteToHexString(p2) + " : " + AuthClientUtils.byteArrayToString(data));
						channel.transmit(command);
					} catch (CardException e) {
	    	        	classLogger("Error occurred on writing attempt on USER1 at 0x" + AuthClientUtils.byteToHexString(p2));
						return -1;
					}
	    			p2++;
	    		}
	    	}
		}
		
		classLogger("Writing process on USER1 storage successfully achieved");
    	return 0;
    }
    
	/***
	 * Lecture sur la zone mémoire USER1 :
	 * -> de P2 = 0x1x (avec x \in [0,F] (16 valeurs))
	 * -> 16 mots = 16 * 4 = 64 bytes
	 */
    public String readOnCard() {
    	String toReturn = "";
    	
    	/***
    	 * POSITION
    	 */
		/***
		 * Devrait toujours être bon
		 * (PIN HARD CODED user prompt non-implémenté)
		 */
		if (verifyCSC0(csc0) == 0) {
			
			classLogger("Reading USER1 storage...");
			
	    	byte p2 = 16;
	    	byte[] readCommand = {(byte) 0x80,(byte) 0xBE ,(byte) 0x00 ,p2 ,(byte) 0x04};
	    	
	    	CommandAPDU command;
	    	ResponseAPDU response;
	    	
	    	for (int i = 0; i < 16; i++) {

	    		readCommand[3] = p2;
	    		command = new CommandAPDU(readCommand);  
	    		try {
					response = channel.transmit(command);
					toReturn += AuthClientUtils.byteArrayToString(response.getData());
					
	    			classLogger("Reading USER1 storage at 0x" + AuthClientUtils.byteToHexString(p2) + " : " + AuthClientUtils.byteArrayToString(response.getData()));
				} catch (CardException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		p2++;
	    	}
		}
    	
		classLogger("USER1 has been read successfully - received : " + AuthClientUtils.hexToString(toReturn));
    	return AuthClientUtils.hexToString(toReturn);
    }
}
