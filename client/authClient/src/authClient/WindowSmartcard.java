package authClient;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.SpringLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WindowSmartcard {

	private Smartcard card;
	private JFrame frame;
	private JLabel lblSmartcardReaderStatus;
	private JButton btnRegister;
	private JButton btnLogin;
	
	private SmartcardReaderListener smartcardReaderListener;
	private SmartcardActionEvent smartcardActionEventListener;
	
	private AuthClient serverConnection;
	
	class SmartcardReaderListener extends Thread { 

		public SmartcardReaderListener (String s) {
			super(s);
			this.setDaemon(true);
		}
			
		public void run() { 
			try {
				while(true) {
					if (card != null) {
						switch(card.getStatus()) {
							case READER_MISSING:
								lblSmartcardReaderStatus.setText("Veuillez connecter votre lecteur de carte...");
								break;
							case WAITING:
								lblSmartcardReaderStatus.setText("Veuillez insérer votre carte...");
								break;
							case READING:
								lblSmartcardReaderStatus.setText("Lecture de la carte...");
								lblSmartcardReaderStatus.setVisible(false);
								btnRegister.setVisible(true);
								btnLogin.setVisible(true);

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
	
	class SmartcardActionEvent extends Thread { 

		public SmartcardActionEvent (String s) {
			super(s);
			this.setDaemon(true);
		}
			
		public void run() { 
			try {
				if (card != null) {
					card.checkCardReader();
					card.checkSmartcard();
					card.connectCard();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}
	
	/**
	 * Create the application.
	 */
	public WindowSmartcard() {

		card = new Smartcard();
		//serverConnection = new AuthClient("SmartcardAuthClient");
		//serverConnection.start();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frame.setVisible(true);
					
					smartcardReaderListener = new SmartcardReaderListener("SmartcardReaderListener");
					smartcardReaderListener.start();
					smartcardActionEventListener = new SmartcardActionEvent("SmartcardActionEvent");
					smartcardActionEventListener.start();
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
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		
		JPanel panel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panel, 89, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panel, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, panel, -109, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panel, -10, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(panel);
		
		lblSmartcardReaderStatus = new JLabel("Processing...");
		panel.add(lblSmartcardReaderStatus);
		springLayout.putConstraint(SpringLayout.NORTH, lblSmartcardReaderStatus, 186, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblSmartcardReaderStatus, 20, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblSmartcardReaderStatus, -38, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblSmartcardReaderStatus, 0, SpringLayout.EAST, frame.getContentPane());
		lblSmartcardReaderStatus.setFont(new Font("Dialog", Font.BOLD, 15));
		lblSmartcardReaderStatus.setForeground(new Color(218, 165, 32));
		
		btnRegister = new JButton("S'inscrire");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
				WindowRegister registerWindow = new WindowRegister(card);
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnRegister, 6, SpringLayout.SOUTH, panel);
		springLayout.putConstraint(SpringLayout.WEST, btnRegister, 60, SpringLayout.WEST, frame.getContentPane());
		btnRegister.setVisible(false);
		frame.getContentPane().add(btnRegister);
		
		btnLogin = new JButton("Se connecter");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
				WindowLogin loginWindow = new WindowLogin(card);
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnLogin, 0, SpringLayout.NORTH, btnRegister);
		springLayout.putConstraint(SpringLayout.EAST, btnLogin, -48, SpringLayout.EAST, frame.getContentPane());
		btnLogin.setVisible(false);
		frame.getContentPane().add(btnLogin);
	}
	
    private void classLogger(String msg) {
    	System.out.println("[WindowSmartcard]: " + msg);
    }
}
