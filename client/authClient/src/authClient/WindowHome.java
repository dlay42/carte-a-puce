package authClient;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/***
 * WindowHome.class
 * @author dlay
 * Homepage JFrame
 */
public class WindowHome {

	private JFrame frame;

	// CONSTRUCTOR
	public WindowHome() {
		
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

	// INIT. FRAME
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{92, 250, 0};
		gridBagLayout.rowHeights = new int[]{24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblTitle = new JLabel("Atelier cartes à puces");
		lblTitle.setFont(new Font("Dialog", Font.BOLD, 20));
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.insets = new Insets(0, 0, 5, 0);
		gbc_lblTitle.gridx = 1;
		gbc_lblTitle.gridy = 1;
		frame.getContentPane().add(lblTitle, gbc_lblTitle);
		
		JLabel lblOrganization = new JLabel("CY Cergy Paris Université");
		GridBagConstraints gbc_lblOrganization = new GridBagConstraints();
		gbc_lblOrganization.insets = new Insets(0, 0, 5, 0);
		gbc_lblOrganization.gridx = 1;
		gbc_lblOrganization.gridy = 2;
		frame.getContentPane().add(lblOrganization, gbc_lblOrganization);
		
		Component verticalStrut_1 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_1 = new GridBagConstraints();
		gbc_verticalStrut_1.insets = new Insets(0, 0, 5, 0);
		gbc_verticalStrut_1.gridx = 1;
		gbc_verticalStrut_1.gridy = 3;
		frame.getContentPane().add(verticalStrut_1, gbc_verticalStrut_1);
		
		JLabel lblInfo1 = new JLabel("Auteurs :");
		lblInfo1.setFont(new Font("Dialog", Font.BOLD, 12));
		GridBagConstraints gbc_lblInfo1 = new GridBagConstraints();
		gbc_lblInfo1.insets = new Insets(0, 0, 5, 0);
		gbc_lblInfo1.gridx = 1;
		gbc_lblInfo1.gridy = 4;
		frame.getContentPane().add(lblInfo1, gbc_lblInfo1);
		
		JLabel lblAuthor1 = new JLabel("- D. L.");
		GridBagConstraints gbc_lblAuthor1 = new GridBagConstraints();
		gbc_lblAuthor1.insets = new Insets(0, 0, 5, 0);
		gbc_lblAuthor1.gridx = 1;
		gbc_lblAuthor1.gridy = 5;
		frame.getContentPane().add(lblAuthor1, gbc_lblAuthor1);
		
		JLabel lblAuthor2 = new JLabel("- F. M.");
		GridBagConstraints gbc_lblAuthor2 = new GridBagConstraints();
		gbc_lblAuthor2.insets = new Insets(0, 0, 5, 0);
		gbc_lblAuthor2.gridx = 1;
		gbc_lblAuthor2.gridy = 6;
		frame.getContentPane().add(lblAuthor2, gbc_lblAuthor2);
		
		JLabel lblAuthor3 = new JLabel("- V. M.");
		GridBagConstraints gbc_lblAuthor3 = new GridBagConstraints();
		gbc_lblAuthor3.insets = new Insets(0, 0, 5, 0);
		gbc_lblAuthor3.gridx = 1;
		gbc_lblAuthor3.gridy = 7;
		frame.getContentPane().add(lblAuthor3, gbc_lblAuthor3);
		
		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 0);
		gbc_verticalStrut.gridx = 1;
		gbc_verticalStrut.gridy = 8;
		frame.getContentPane().add(verticalStrut, gbc_verticalStrut);
		
		JButton btnStart = new JButton("START");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
				WindowSmartcard smartcard_reader_window = new WindowSmartcard();
			}
		});
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.insets = new Insets(0, 0, 5, 0);
		gbc_btnStart.gridx = 1;
		gbc_btnStart.gridy = 9;
		frame.getContentPane().add(btnStart, gbc_btnStart);
	}
}
