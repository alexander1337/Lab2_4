package TableGUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.TreeSelectionModel;

public class TableGUI extends JFrame {
	private int current;
	private UIManager.LookAndFeelInfo installedLF[];
	JPanel labels = new JPanel(new GridLayout(4, 1));
	JPanel panel = new JPanel(new GridLayout(1, 2));
	JPanel form = new JPanel(new BorderLayout());
	JPanel textFields = new JPanel(new GridLayout(4, 1));
	JButton insertButton = new JButton("Insert Person");
	JButton deleteButton = new JButton("Delete");
	JButton findButton = new JButton("Find");
	JButton editButton = new JButton("Edit Current Node");
	JButton changeLookFeelButton = new JButton("change Look & Feel");
	JTable table = new JTable();
	TableGUI() {
		setSize(800,200);
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Horns&Hoof");
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		labels.add(new JLabel("Full name:"));
		textFields.add(new JTextField());
		labels.add(new JLabel("Date of Birth:"));
		textFields.add(new JTextField());
		labels.add(new JLabel("ID:"));
		textFields.add(new JTextField());
		labels.add(new JLabel("Photo:"));
		textFields.add(new JTextField());
		form.add(labels, "West");
		form.add(textFields, "Center");
		panel.add(table);
		panel.add(form);
		add(panel, "Center");
		JPanel panelButton = new JPanel();

		
		insertButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame insertForm = new JFrame();
				JPanel insertPanel = new JPanel();
				JPanel labelsPanel = new JPanel();
				JPanel textFieldsPanel = new JPanel();
				insertPanel.setLayout(new BorderLayout());
				labelsPanel.setLayout(new GridLayout(4,1));
				textFieldsPanel.setLayout(new GridLayout(4,1));
				labelsPanel.add(new JLabel("Full name:"));
				textFieldsPanel.add(new JTextField());
				labelsPanel.add(new JLabel("Date of Birth:"));
				textFieldsPanel.add(new JTextField());
				labelsPanel.add(new JLabel("ID:"));
				textFieldsPanel.add(new JTextField());
				labelsPanel.add(new JLabel("Photo:"));
				textFieldsPanel.add(new JTextField());
				insertPanel.add(labelsPanel, "West");
				insertPanel.add(textFieldsPanel, "Center");
				insertPanel.add(new JButton("OK"), "South");
				insertForm.add(insertPanel);
				insertForm.setSize(400, 200);
				insertForm.setVisible(true);
								
							
			}
		});

	
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});

		
		findButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});

		
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		editButton.setEnabled(false);

		
		changeLookFeelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				current++;
				if (current > installedLF.length - 1) {
					current = 0;
				}

				System.out.println("New Current Look&Feel:" + current);
				System.out.println("New Current Look&Feel Class:" + installedLF[current].getClassName());

				try {
					UIManager.setLookAndFeel(installedLF[current].getClassName());
				} catch (Exception ex) {
					System.out.println("exception");
				}
			}
		});

		panelButton.add(insertButton);
		panelButton.add(deleteButton);
		panelButton.add(findButton);
		panelButton.add(editButton);
		panelButton.add(changeLookFeelButton);
		add(panelButton, "South");
		installedLF = UIManager.getInstalledLookAndFeels();
		current = 0;
		try {
			UIManager.setLookAndFeel(installedLF[current].getClassName());
		} catch (Exception ex) {
			System.out.println("Exception 1");
		}
		setVisible(true);
		
	}

	public static void main(String[] args) {
		new TableGUI();
	}
}