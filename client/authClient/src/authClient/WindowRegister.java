package authClient;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WindowRegister {

	private Smartcard card;
	private JFrame frame;
	private JTextField textFieldLastName;
	private JTextField textFieldName;
	private JTextField textFieldLogin;
	private JPasswordField passwordField;
	private JLabel lblTitle;
	private JButton btnSinscire;
	private JButton btnBack;
	private JLabel lblLastName;
	private JLabel lblName;
	private JLabel lblLogin;
	private JLabel lblPassword;

	private AuthClient serverConnection;
	private ServerEventListener serverEventListener;
	
	class ServerEventListener extends Thread { 

		public ServerEventListener (String s) {
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
		            	case "REG1":
							if (data.equals("OK")) {
								textFieldLastName.setVisible(false);
								textFieldName.setVisible(false);
								textFieldLogin.setVisible(false);
								passwordField.setVisible(false);
								btnSinscire.setVisible(false);
								lblLastName.setVisible(false);
								lblName.setVisible(false);
								lblLogin.setVisible(false);
								lblPassword.setVisible(false);
								
								lblTitle.setText("Inscription réussie");
								btnBack.setVisible(true);
							} else if (data.equals("KO")) {
								lblTitle.setText("Inscription échouée");
							}
		            		break;
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
	 * Create the application.
	 */
	public WindowRegister(Smartcard readCard) {
		card = readCard;
		serverConnection = new AuthClient("RegisterAuthClient");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frame.setVisible(true);
					serverConnection.start();
					serverEventListener = new ServerEventListener("ServerEventListener");
					serverEventListener.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(12, 12, 426, 34);
		frame.getContentPane().add(panel_1);
		
		lblTitle = new JLabel("Inscription");
		lblTitle.setFont(new Font("Dialog", Font.BOLD, 20));
		panel_1.add(lblTitle);
		
		JPanel panel = new JPanel();
		panel.setBounds(12, 59, 426, 34);
		frame.getContentPane().add(panel);
		
		lblLastName = new JLabel("Nom");
		panel.add(lblLastName);
		
		textFieldLastName = new JTextField();
		panel.add(textFieldLastName);
		textFieldLastName.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(12, 93, 426, 34);
		frame.getContentPane().add(panel_3);
		
		lblName = new JLabel("Prénom");
		panel_3.add(lblName);
		
		textFieldName = new JTextField();
		textFieldName.setColumns(10);
		panel_3.add(textFieldName);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(12, 125, 426, 34);
		frame.getContentPane().add(panel_4);
		
		lblLogin = new JLabel("Login");
		panel_4.add(lblLogin);
		
		textFieldLogin = new JTextField();
		textFieldLogin.setColumns(10);
		panel_4.add(textFieldLogin);
		
		lblPassword = new JLabel("Mot de passe");
		lblPassword.setBounds(82, 165, 116, 30);
		frame.getContentPane().add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(208, 171, 136, 19);
		frame.getContentPane().add(passwordField);
		
		btnSinscire = new JButton("S'inscire");
		btnSinscire.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String 	userLastName = textFieldLastName.getText(),
						userName = textFieldName.getText(),
						userLogin = textFieldLogin.getText(),
						userPassword = passwordField.getText();

				if (
						!userLastName.isEmpty() && 
						!userName.isEmpty() &&
						!userLogin.isEmpty() &&
						!userPassword.isEmpty()
					) {
					
						/***
						 *  Ecriture sur la carte :
						 *  <NAME>;<LAST_NAME>
						 */
						String toWriteOnCard = userName + ';' + userLastName;
						if (toWriteOnCard.length() <= 64) {
							card.writeOnCard(toWriteOnCard);
							
							/***
							 * Inscription à la base de données
							 * Envoi du SALT1 = H(login+passwd)
							 */
							String salt1 = userLogin + userPassword;
							classLogger("Send '" + "REG1;" + toWriteOnCard + ';' + salt1.hashCode() + "' to server");
							serverConnection.setMessage("REG1;" + toWriteOnCard + ';' + salt1.hashCode());
						}
					
				}
			}
		});
		btnSinscire.setBounds(169, 200, 117, 25);
		frame.getContentPane().add(btnSinscire);
		
		btnBack = new JButton("Retour à la page précédente");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
				WindowSmartcard backHome = new WindowSmartcard();
			}
		});
		btnBack.setBounds(106, 207, 238, 25);
		frame.getContentPane().add(btnBack);
		btnBack.setVisible(false);
	}
	
    private void classLogger(String msg) {
    	System.out.println("[WindowRegister]: " + msg);
    }
}
