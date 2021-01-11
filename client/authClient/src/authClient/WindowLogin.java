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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.awt.event.ActionEvent;

/***
 * WindowLogin.class
 * @author dlay
 * JFrame implementing login tasks
 */
public class WindowLogin {

	private Smartcard card;
	private JFrame frame;
	private JTextField textFieldLogin;
	private JPasswordField passwordField;

	private String userName;
	private String userLastName;
	
	private String salt2;

	private AuthClient serverConnection;
	private ServerEventListener loginServerEventListener;
	private SmartcardReaderListener loginSmartcardReaderListener;

	// CONSTRUCTOR
	public WindowLogin(Smartcard readCard, AuthClient argServerConnection) {
		card = readCard;
		serverConnection = argServerConnection;
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frame.setVisible(true);
					
					loginServerEventListener = new ServerEventListener("WindowServerEventListener", serverConnection, getSelf());
					loginServerEventListener.start();
					loginSmartcardReaderListener = new SmartcardReaderListener("SmartcardReaderListener", card, getSelf());
					loginSmartcardReaderListener.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// GETTERS & SETTERS
    public WindowLogin getSelf() {
    	return this;
    }
    
    // VISIBILITY
	public void setFrameVisible(boolean visible) {
		if (visible != frame.isVisible())
			frame.setVisible(visible);
	}
	
	// INIT. FRAME
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
				
				String rch = userLogin + userPassword;
				// SHA-256 signature
				rch = AuthClientUtils.sha256Signature(rch);
				
				// DERIAVATION (PBDKF2)
				for (int i = 0; i < 1000; i++) {
					rch = AuthClientUtils.sha256Signature(rch + serverConnection.getSessionToken());
				}
				
				serverConnection.setMessage("LOG1;" + rch);
			}
		});
		btnLogin.setBounds(154, 209, 127, 25);
		frame.getContentPane().add(btnLogin);
	}
	
    private void classLogger(String msg) {
    	System.out.println("[WindowLogin]: " + msg);
    }
}
