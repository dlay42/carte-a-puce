package authClient;

class ServerEventListener extends Thread { 

	private AuthClient serverConnection;
	private Object currentFrame;
	
	// CONSTRUCTOR
	public ServerEventListener (String s, AuthClient argServerConnection, Object argCurrentFrame) {
			super(s);
			this.setDaemon(true);
			
			serverConnection = argServerConnection;
			currentFrame = argCurrentFrame;
		}
	
	// THREAD
	public void run() { 
		String flag = "";
		String data = "";
		String[] parsedData;
		try {
			while(true) {
				Thread.sleep(250);
				if (	serverConnection != null && 
						!serverConnection.getLastResponse().isEmpty()
					) {
					parsedData = serverConnection.getLastResponse().split(";");
					flag = parsedData[0];
					data = parsedData[1];
					
					// Behaviour change according to current frame
					if (currentFrame instanceof WindowSmartcard) {
							/***
							* HELLO : get SALT 2
							*/
							switch(flag) {
								case "HEL1":
									serverConnection.setSessionToken(data);
									// Cleanup
									serverConnection.setLastResponse("");
									break;
								default:
									//
							}
					} else if (currentFrame instanceof WindowRegister) {

					} else if (currentFrame instanceof WindowLogin) {
						/***
						* LOG1 : Auth result
						*/
						switch(flag) {
						case "LOG1":
							if (data.equals("OK")) {
								classLogger("Access granted");
								((WindowLogin) currentFrame).setFrameVisible(false);
								new GrantedAccessApp();
							} else if (data.equals("KO")) {
								classLogger("Access denied");
							}
							
							// Cleanup
							serverConnection.setLastResponse("");
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
	
	// UTILS
	public void classLogger(String msg) {
		System.out.println("[ServerEventListener]: " + msg);
	}
}