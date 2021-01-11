package authClient;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import javax.swing.UIManager;
import javax.swing.JLabel;
import java.awt.Font;

public class GrantedAccessApp {

	private JFrame frame;

	// CONSTRUCTOR
	public GrantedAccessApp() {

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
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JPanel panel = new JPanel();
		panel.setBackground(UIManager.getColor("OptionPane.questionDialog.titlePane.shadow"));
		panel.setForeground(UIManager.getColor("OptionPane.questionDialog.titlePane.shadow"));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 15;
		gbc_panel.gridheight = 9;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		frame.getContentPane().add(panel, gbc_panel);
		
		JLabel lblNewLabel = new JLabel("Access granted");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 20));
		lblNewLabel.setBackground(UIManager.getColor("PasswordField.background"));
		panel.add(lblNewLabel);
	}

}
