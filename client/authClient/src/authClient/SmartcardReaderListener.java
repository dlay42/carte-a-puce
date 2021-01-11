package authClient;

/***
 * SmartcardReaderListener.class
 * @author dlay
 * Card reader listener used by different window interfaces
 * behaviours change according to interfaces passed as parameters
 */
class SmartcardReaderListener extends Thread {
	
	private Smartcard card;
	private Object currentFrame;
	private SmartcardActionEvent smartcardActionEventListener;
	class SmartcardActionEvent extends Thread { 
		
		public SmartcardActionEvent (String s) {
			super(s);
			this.setDaemon(true);
		}
			
		public void run() { 
			while (true) {
				card.checkCard();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					//
				}
			}
		}
	}
	
	
	// CONSTRUCTOR
	public SmartcardReaderListener (String s, Smartcard argCard, Object argFrame) {
		super(s);
		this.setDaemon(true);
		card = argCard;
		currentFrame = argFrame;
		
		smartcardActionEventListener = new SmartcardActionEvent("SmartcardActionEvent");
		smartcardActionEventListener.start();
	}
	
	
	// THREAD
	public void run() {
		try {
			while(true) {
				if (card != null) {
					if (currentFrame instanceof WindowSmartcard) {
						switch(card.getStatus()) {
							case READER_MISSING:
								((WindowSmartcard) currentFrame).setLblSmartcardReaderStatusText("Veuillez connecter votre lecteur de carte...");
								((WindowSmartcard) currentFrame).setLblSmartcardReaderStatusVisible(true);
								((WindowSmartcard) currentFrame).setBtnRegisterVisible(false);
								((WindowSmartcard) currentFrame).setBtnLoginVisible(false);
								
								((WindowSmartcard) currentFrame).setFrameVisible(true);
								break;
							case WAITING:
								((WindowSmartcard) currentFrame).setLblSmartcardReaderStatusText("Veuillez insérer votre carte...");
								((WindowSmartcard) currentFrame).setLblSmartcardReaderStatusVisible(true);
								((WindowSmartcard) currentFrame).setBtnRegisterVisible(false);
								((WindowSmartcard) currentFrame).setBtnLoginVisible(false);
								
								((WindowSmartcard) currentFrame).setFrameVisible(true);
								break;
							case READING:
								((WindowSmartcard) currentFrame).setLblSmartcardReaderStatusText("Lecture de la carte...");
								((WindowSmartcard) currentFrame).setLblSmartcardReaderStatusVisible(false);
								((WindowSmartcard) currentFrame).setBtnRegisterVisible(true);
								((WindowSmartcard) currentFrame).setBtnLoginVisible(true);
								break;
							default:
								//
						}
						
						
					} else if (currentFrame instanceof WindowLogin) {
						switch(card.getStatus()) {
							case READER_MISSING:
								((WindowLogin) currentFrame).setFrameVisible(false);
								break;
							case WAITING:
								((WindowLogin) currentFrame).setFrameVisible(false);
								break;
							default:
								//
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
}