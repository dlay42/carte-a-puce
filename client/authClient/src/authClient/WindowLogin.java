package authClient;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WindowLogin {

	private Smartcard card;
	private JFrame frame;
	private JTextField textFieldLogin;
	private JPasswordField passwordField;

	private String userName;
	private String userLastName;
	
	private String salt2;

	private AuthClient serverConnection;
	private WindowServerEventListener serverEventListener;

	/**
	 * Create the application.
	 */
	public WindowLogin(Smartcard readCard) {
		card = readCard;
		serverConnection = new AuthClient("LoginAuthClient");
		serverConnection.start();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frame.setVisible(true);
					serverEventListener = new WindowServerEventListener("WindowServerEventListener");
					serverEventListener.start();
					card.checkCardReader();
					card.checkSmartcard();
					card.connectCard();
					String[] parsedData = card.readOnCard().split(";");
					if (parsedData.length >= 2) {
						userName = parsedData[0];
						userLastName = parsedData[1];

						/***
						 * Send userName and userLastName to server
						 */
						serverConnection.setMessage("HEL1" + ";" + userName + ";" + userLastName);

					}					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	class WindowServerEventListener extends Thread { 

		public WindowServerEventListener (String s) {
				super(s);
				this.setDaemon(true);
			}
			
		public void run() { 
			String flag = "";
			String data = "";
			String[] parsedData;
			try {
				while(true) {
					Thread.sleep(250);
					if (serverConnection != null && !serverConnection.getLastResponse().isEmpty()) {
						parsedData = serverConnection.getLastResponse().split(";");
						flag = parsedData[0];
						data = parsedData[1];
						switch(flag) {
							/***
							* HELLO : get SALT 2
							*/
							case "HEL1":
								salt2 = data;
								break;
								/***
								* LOG1 : Auth result
								*/
							case "LOG1":
								/*
								if (data.equals("OK")) {
									classLogger("Access granted");
								} else if (data.equals("KO")) {
									classLogger("Access denied");
								}
								*/
							default:
								//
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(12, 12, 416, 31);
		frame.getContentPane().add(panel);
		
		JLabel lblTitle = new JLabel("Se connecter");
		lblTitle.setFont(new Font("Dialog", Font.BOLD, 20));
		panel.add(lblTitle);
		
		JLabel lblLogin = new JLabel("Login");
		lblLogin.setBounds(189, 64, 45, 25);
		frame.getContentPane().add(lblLogin);
		
		textFieldLogin = new JTextField();
		textFieldLogin.setBounds(154, 101, 114, 19);
		frame.getContentPane().add(textFieldLogin);
		textFieldLogin.setColumns(10);
		
		JLabel lblPassword = new JLabel("Mot de passe");
		lblPassword.setBounds(164, 132, 104, 15);
		frame.getContentPane().add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(154, 169, 114, 19);
		frame.getContentPane().add(passwordField);
		
		JButton btnLogin = new JButton("Se connecter");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String 	userLogin = textFieldLogin.getText(),
						userPassword = passwordField.getText();
				
				/*** TODO
				 * PBDKF2
				 */
				String rch = String.valueOf((userLogin + userPassword).hashCode());
				
				classLogger("Before derivation : " + rch);
				for (int i = 0; i < 100; i++) {
					rch = String.valueOf((rch + salt2).hashCode());
				}
				classLogger("After derivation : " + rch);

				/*** /!\ CHEAT
				 * Conceptuellement nous voulions stocker le token de session directement
				 * la session utilisateur. Par faute de temps, nous n'avons pas pu implémenter
				 * cette fonctionnalité. Nous avons ainsi stocké les token sur la DB (ce qu'il ne faut
				 * pas faire) pendant ce temps.
				 * Or, nous ne pouvons donc plus faire le lien entre le token session et l'utilisateur
				 * (côté serveur) pour recalculer le rch. Nous trichons donc en attendant en repassant
				 * les données "QUI JE SUIS"
				 */
				 /***
				  * WHO AM I ?
				  */
				serverConnection.setMessage("LOG1;" + rch + ';' + userName + ';' + userLastName);

			}
		});
		btnLogin.setBounds(154, 209, 127, 25);
		frame.getContentPane().add(btnLogin);
	}
	
    private void classLogger(String msg) {
    	System.out.println("[WindowLogin]: " + msg);
    }
}
