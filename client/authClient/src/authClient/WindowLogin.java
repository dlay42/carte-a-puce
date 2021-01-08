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

	private AuthClient serverConnection;
	
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
				 * Inscription à la base de données
				 * Envoi du SALT1 = H(login+passwd)
				 */
				String salt1 = userLogin + userPassword;
				System.out.println(salt1.hashCode());
			}
		});
		btnLogin.setBounds(154, 209, 127, 25);
		frame.getContentPane().add(btnLogin);
	}
	
    private void classLogger(String msg) {
    	System.out.println("[WindowLogin]: " + msg);
    }
}
