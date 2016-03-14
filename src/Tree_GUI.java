import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.*;
import java.util.Date;
import java.util.Enumeration;
import com.toedter.calendar.JDateChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import javax.swing.tree.*;

class DictionaryAnchor {
	public DefaultMutableTreeNode topic;
	public DefaultMutableTreeNode entry;
}

abstract class DictionaryElem {

	public abstract String getType();

	public abstract String getValue();

	public abstract String toString();

}

class DictionaryTopic extends DictionaryElem {
	private String theTopic;

	public DictionaryTopic(String topic) {
		theTopic = topic;
	}

	public String getType() {
		return "Topic";
	}

	public String getValue() {
		return theTopic;
	}

	public String toString() {
		return theTopic;
	}

}

class DictionaryEntry extends DictionaryElem {
	protected String theName;
	protected Date birth;
	protected String photo;
	// protected String theAddress;
	protected String number;
	protected BufferedImage image;
	protected String surname;

	public DictionaryEntry(String surname, String name, Date birth, String number, String photo) throws IOException {
		this.surname = surname;
		theName = name;
		this.birth = birth;
		this.number = number;
		this.photo = photo;
		if (this.photo.equals("")) {
			image = ImageIO.read(new File("C:\\Users\\Alexander\\Documents\\аноним.jpg"));
			this.photo = "C:\\Users\\Alexander\\Documents\\аноним.jpg";
		} else {
			image = ImageIO.read(new File(photo));
		}
	}

	public DictionaryEntry(String completeStr) {
		int delim_index = completeStr.indexOf(" ");
		int length = completeStr.length();

		if (delim_index <= 0) {
			delim_index = length;
		}

		surname = completeStr.substring(0, delim_index);
		// theAddress = completeStr.substring(delim_index);
	}

	public String getType() {
		return "Entry";
	}

	public String getValue() {
		return surname;
	}

	public String getName() {
		return theName;
	}

	public String getSurname() {
		return surname;
	}

	public String toString() {
		return surname;
	}

	public Date getBirth() {
		return birth;
	}

	public String getNumber() {
		return number;
	}

	public boolean imageExists() {
		File file = new File(photo);
		return file.exists();
	}

	public BufferedImage getPhoto() {
		return image;
	}

	public String getPath() {
		return photo;
	}

}

// ----------------------------------------------------------------Ч
class SwingGUI5Model {

	private DefaultTreeModel theModel;
	private static String alphabet = new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	private DefaultMutableTreeNode theRoot;

	public SwingGUI5Model() {
	}

	public TreePath findPerson(String data) {
		TreePath path;
		DictionaryAnchor anchor = new DictionaryAnchor();

		anchor.topic = null;
		anchor.entry = null;

		DictionaryEntry new_entry = new DictionaryEntry(data);

		if (this.findEntry(new_entry, anchor)) {
			TreeNode[] nodes = theModel.getPathToRoot(anchor.entry);
			path = new TreePath(nodes);

		} else {
			// not found
			path = null;
		}

		return path;
	}

	public void deletePerson(DefaultMutableTreeNode selectedNode) {
		if (selectedNode != theRoot) {
			DictionaryElem elem = (DictionaryElem) selectedNode.getUserObject();
			if ("Entry".equals(elem.getType())) {
				theModel.removeNodeFromParent(selectedNode);
			}
		}
	}

	public TreePath insertPerson(String surname, String name, Date birth, String id, String photo) throws IOException {
		TreePath path;
		DictionaryAnchor anchor = new DictionaryAnchor();

		anchor.topic = null;
		anchor.entry = null;

		DictionaryEntry new_entry = new DictionaryEntry(surname, name, birth, id, photo);

		if (this.findEntry(new_entry, anchor)) {
			// found such a person
			TreeNode[] nodes = theModel.getPathToRoot(anchor.entry);
			path = new TreePath(nodes);
		} else {
			// not found - insert the person
			if (anchor.topic != null) {
				// the proper topic has been found
				DefaultMutableTreeNode new_node = new DefaultMutableTreeNode(new_entry);
				new_node.setAllowsChildren(false);
				theModel.insertNodeInto(new_node, anchor.topic, anchor.topic.getChildCount());
				TreeNode[] nodes = theModel.getPathToRoot(new_node);
				path = new TreePath(nodes);
			} else {
				path = null;
			}
		}

		return path;
	}

	protected boolean findEntry(DictionaryEntry new_entry, DictionaryAnchor anchor) {

		String firstLetter = new_entry.getValue().substring(0, 1);
		boolean result = false;

		if (anchor == null)
			return false;
		anchor.topic = null;

		Enumeration en = theRoot.children();

		while (en.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();

			DictionaryElem elem = (DictionaryElem) node.getUserObject();
			if ("Topic".equals(elem.getType())) {
				if (firstLetter.equalsIgnoreCase(elem.getValue())) {
					anchor.topic = node;
					break;
				}
			} else {
				break;
			}
		}

		if (anchor.topic != null) {
			en = anchor.topic.children();
			anchor.entry = null;

			while (en.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();

				DictionaryElem elem = (DictionaryElem) node.getUserObject();
				if ("Entry".equals(elem.getType())) {
					if (new_entry.getValue().equalsIgnoreCase(elem.getValue())) {
						anchor.entry = node;
						result = true;
						break;
					}
				}
			}

		}
		return result;
	}

	public void removeNodeFromParent(MutableTreeNode selectedNode) {
		theModel.removeNodeFromParent(selectedNode);
	}

	public TreeNode[] getPathToRoot(MutableTreeNode newNode) {

		return theModel.getPathToRoot(newNode);
	}

	public DefaultTreeModel buildDefaultTreeStructure() {
		theRoot = new DefaultMutableTreeNode("Dictionary");
		theRoot.setAllowsChildren(true);

		for (int i = 0; i < alphabet.length(); i++) {
			DictionaryElem nodeElem = new DictionaryTopic(alphabet.substring(i, i + 1));
			DefaultMutableTreeNode topic = new DefaultMutableTreeNode(nodeElem);
			topic.setAllowsChildren(true);

			theRoot.add(topic);
		}
		theModel = new DefaultTreeModel(theRoot);
		theModel.setAsksAllowsChildren(true);

		return theModel;
	}

	public void clean() {
		DictionaryAnchor anchor = new DictionaryAnchor();
		@SuppressWarnings("rawtypes")
		Enumeration en1 = theRoot.children();
		while (en1.hasMoreElements()) {
			DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) en1.nextElement();

			DictionaryElem elem1 = (DictionaryElem) node1.getUserObject();
			if ("Topic".equals(elem1.getType()))
				anchor.topic = node1;

			if (anchor.topic != null) {
				@SuppressWarnings("rawtypes")
				Enumeration en2 = anchor.topic.children();
				anchor.entry = null;

				while (en2.hasMoreElements()) {
					DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) en2.nextElement();

					DictionaryEntry elem2 = (DictionaryEntry) node2.getUserObject();
					if ("Entry".equals(elem2.getType())) {
						removeNodeFromParent(node2);
					}
				}
			}

		}
	}

}

// -----------------------------------------------------------Ч
class SwingGUI5 extends JFrame implements ActionListener, TreeSelectionListener {
	private SwingGUI5Model theAppModel;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuNew;
	private JMenuItem menuSave;
	private JMenuItem menuLoad;
	private JTree theTree;
	private JTextArea theTextArea;
	private JButton insertButton;
	private JButton deleteButton;
	private JButton findButton;
	private JButton editButton;
	private JButton saveButton;
	JTextField nameField;
	JTextField surnameField;
	JButton explorer;
	JPanel imPanel = new JPanel();
	JPanel info = new JPanel();
	JPanel labels = new JPanel();
	JPanel panel = new JPanel();
	JPanel form = new JPanel();
	JPanel textFields = new JPanel();
	String bufferedPath = new String();
	String defaultPath = "C:\\Users\\Alexander\\Documents\\аноним.jpg";
	private JDateChooser dateChooser;
	JTextField numberField;

	private JButton changeLookFeelButton;

	private JTextField theTextField;

	private UIManager.LookAndFeelInfo installedLF[];

	private int current;

	private void saveTo(String fileName) throws IOException {
		File file = new File(fileName);
		file.createNewFile();
		PrintWriter out = new PrintWriter(file.getAbsoluteFile());
		Object o = theTree.getModel().getRoot();
		DictionaryEntry elem;
		DefaultMutableTreeNode node, nodeElem;
		@SuppressWarnings("rawtypes")
		Enumeration en;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		for (int i = 0; i < theTree.getModel().getChildCount(o); i++) {
			node = (DefaultMutableTreeNode) theTree.getModel().getChild(o, i);

			en = node.children(); // get all instances of current
									// node
			while (en.hasMoreElements()) {
				nodeElem = (DefaultMutableTreeNode) en.nextElement();

				elem = (DictionaryEntry) nodeElem.getUserObject(); // get
																	// instance
				out.println(elem.getSurname() + " " + elem.getName() + " " + dateFormat.format(elem.getBirth()) + " "
						+ elem.getNumber() + " " + elem.getPath());
			}

		}
		out.close();
	}

	private void loadFrom(String fileName) throws IOException {
		File file = new File(fileName);

		BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));

		String s;
		while ((s = in.readLine()) != null) {
			SimpleDateFormat format = new SimpleDateFormat();
			format.applyPattern("dd.MM.yyyy");
			try {
				Date tmp = format.parse(s.split(" ")[2]);
				theAppModel.insertPerson(s.split(" ")[0], s.split(" ")[1], tmp, s.split(" ")[3], s.split(" ")[4]);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		in.close();
	}

	protected Component buildGUI() throws IOException {

		Container contentPane = this.getContentPane();
		// contentPane.setLayout (new FlowLayout());
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		menu.setMnemonic(KeyEvent.VK_M);
		menuBar.add(menu);
		menuNew = new JMenuItem("Clear");
		menu.add(menuNew);
		menuNew.setMnemonic(KeyEvent.VK_C);
		menuNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				theAppModel.clean();
			}
		});
		menuSave = new JMenuItem("Save");
		menu.add(menuSave);
		menuSave.setMnemonic(KeyEvent.VK_S);
		menuSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fileName = null;
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select destination");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showSaveDialog(menuSave) == JFileChooser.APPROVE_OPTION) {
					System.out.println("getSelectedFile(): " + chooser.getSelectedFile());
					fileName = chooser.getSelectedFile().toString();
				}

				try {
					saveTo(fileName);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "File error!");
				}
			}
		});
		menuLoad = new JMenuItem("Load");
		menu.add(menuLoad);
		menuLoad.setMnemonic(KeyEvent.VK_L);
		menuLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String fileName = null;
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select destination");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(menuLoad) == JFileChooser.APPROVE_OPTION) {
					fileName = chooser.getSelectedFile().toString();

					try {
						theAppModel.clean();
						loadFrom(fileName);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "File error!");
					}
				}

			}
		});
		this.setJMenuBar(menuBar);
		theTree = new JTree(theAppModel.buildDefaultTreeStructure());
		// theTree.setEditable(true);
		theTree.addTreeSelectionListener(this);
		int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
		theTree.getSelectionModel().setSelectionMode(mode);
		theTextArea = new JTextArea();
		info.setLayout(new FlowLayout(SwingConstants.VERTICAL));
		info.add(imPanel);
		BufferedImage tmp = ImageIO.read(new File(defaultPath));
		JLabel lpic = new JLabel();
		lpic.setIcon(new ImageIcon(tmp.getScaledInstance(128, 128, Image.SCALE_AREA_AVERAGING)));
		imPanel.add(lpic);

		lpic.setHorizontalAlignment(SwingConstants.CENTER);
		info.add(form);
		textFields.setLayout(new GridLayout(5, 1));
		labels.setLayout(new GridLayout(5, 1));
		dateChooser = new JDateChooser();
		panel.setLayout(new GridLayout(1, 2));
		form.setLayout(new BorderLayout());
		labels.add(new JLabel("Surname:"));
		textFields.add(surnameField = new JTextField());
		surnameField.setEditable(false);
		labels.add(new JLabel("Name:"));
		textFields.add(nameField = new JTextField());
		nameField.setEditable(false);

		labels.add(new JLabel("Date of Birth:"));
		textFields.add(dateChooser);
		dateChooser.setEnabled(false);
		labels.add(new JLabel("Number:"));
		textFields.add(numberField = new JFormattedTextField(createFormatter("+7(###)-###-####")));
		// textFields.add(numberField = new JTextField());
		numberField.setEditable(false);
		labels.add(new JLabel("Photo:"));
		textFields.add(explorer = new JButton("Open explorer"));
		explorer.setEnabled(false);
		form.add(labels, "West");
		form.add(textFields, "Center");
		panel.add(new JScrollPane(theTree));
		panel.add(info);
		contentPane.add(panel, "Center");
		JPanel panelButton = new JPanel(new GridLayout(2, 1));
		JPanel panelButUp = new JPanel();
		JPanel panelButDown = new JPanel();
		panelButton.add(panelButUp);
		panelButton.add(panelButDown);
		explorer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Create window for choosing an image
				JFileChooser img_chooser = new JFileChooser();
				img_chooser.setCurrentDirectory(new java.io.File("."));
				img_chooser.setDialogTitle("Select image");
				img_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				img_chooser.setAcceptAllFileFilterUsed(false);

				if (img_chooser.showOpenDialog(explorer) == JFileChooser.APPROVE_OPTION) {
					if (img_chooser.getSelectedFile().toString().contains(".jpg")) {
						bufferedPath = img_chooser.getSelectedFile().toString();
						BufferedImage tmp = null;
						try {
							tmp = ImageIO.read(new File(bufferedPath));
							imPanel.removeAll();
							lpic.setIcon(new ImageIcon(tmp.getScaledInstance(128, 128, Image.SCALE_AREA_AVERAGING)));
							imPanel.add(lpic);
							imPanel.updateUI();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						bufferedPath = "";
						JOptionPane.showMessageDialog(null, "File error!");
					}
				}
			}
		});

		insertButton = new JButton("Insert Person (Alt+I)");
		insertButton.addActionListener(this);
		insertButton.setMnemonic(KeyEvent.VK_I);
		deleteButton = new JButton("Delete");
		deleteButton.addActionListener(this);

		findButton = new JButton("Find (Alt+F)");
		findButton.addActionListener(this);
		findButton.setMnemonic(KeyEvent.VK_F);
		saveButton = new JButton("Save Changes");
		saveButton.addActionListener(this);
		saveButton.setEnabled(false);
		editButton = new JButton("Edit Current Node");
		editButton.addActionListener(this);
		editButton.setEnabled(false);

		changeLookFeelButton = new JButton("change Look & Feel");
		changeLookFeelButton.addActionListener(this);

		panelButUp.add(insertButton);
		panelButUp.add(deleteButton);
		panelButUp.add(findButton);
		panelButUp.add(editButton);
		panelButUp.add(saveButton);
		panelButDown.add(changeLookFeelButton);
		contentPane.add(panelButton, "South");

		return null;
	}

	public SwingGUI5(SwingGUI5Model appModel) throws IOException {
		theAppModel = appModel;

		setTitle("Tree example with model");
		setSize(800, 300);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		this.buildGUI();

		installedLF = UIManager.getInstalledLookAndFeels();
		current = 0;
		try {
			UIManager.setLookAndFeel(installedLF[current].getClassName());
		} catch (Exception ex) {
			System.out.println("Exception 1");
		}
	}

	protected static MaskFormatter createFormatter(String s) {
		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter(s);
		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
			System.exit(-1);
		}
		return formatter;
	}

	public void actionPerformed(ActionEvent event) {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) theTree.getLastSelectedPathComponent();

		String textVal = "";

		if (textVal.equals("")) {
			textVal = "new person";
		}

		if (event.getSource().equals(changeLookFeelButton)) {
			current++;
			if (current > installedLF.length - 1) {
				current = 0;
			}

			System.out.println("New Current Look&Feel:" + current);
			System.out.println("New Current Look&Feel Class:" + installedLF[current].getClassName());

			try {
				UIManager.setLookAndFeel(installedLF[current].getClassName());
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception ex) {
				System.out.println("exception");
			}
		} else {
			if (event.getSource().equals(insertButton)) {
				JDateChooser dateChooser1;
				dateChooser1 = new JDateChooser();
				dateChooser1.setBounds(20, 20, 200, 20);
				dateChooser1.setDateFormatString("dd.MM.yyyy");
				JButton okButton;
				JTextField nameField;
				JTextField surnameField;
				JTextField numberField;
				final JButton insertExplorer = new JButton("Open explorer");
				JFrame insertForm = new JFrame();
				JPanel insertPanel = new JPanel();
				JPanel labelsPanel = new JPanel();
				JPanel textFieldsPanel = new JPanel();
				insertPanel.setLayout(new BorderLayout());
				labelsPanel.setLayout(new GridLayout(5, 1));
				textFieldsPanel.setLayout(new GridLayout(5, 1));
				labelsPanel.add(new JLabel("Surname:"));
				textFieldsPanel.add(surnameField = new JTextField());
				labelsPanel.add(new JLabel("Name:"));
				textFieldsPanel.add(nameField = new JTextField());
				labelsPanel.add(new JLabel("Date of Birth:"));
				textFieldsPanel.add(dateChooser1);
				labelsPanel.add(new JLabel("Number:"));
				textFieldsPanel.add(numberField = new JFormattedTextField(createFormatter("+7(###)-###-####")));
				// textFieldsPanel.add(numberField = new JTextField());
				labelsPanel.add(new JLabel("Photo:"));
				textFieldsPanel.add(insertExplorer);
				insertPanel.add(labelsPanel, "West");
				insertPanel.add(textFieldsPanel, "Center");
				insertPanel.add(okButton = new JButton("OK"), "South");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// theAppModel.insertPerson(nameField.getText());
						if (((nameField.getText().equals(""))) || ((numberField.getText().contains(" ")))
								|| ((nameField.getText().contains(" "))) || ((surnameField.getText().contains(" ")))
								|| ((surnameField.equals(""))) || (dateChooser1.isValid())) {
							JOptionPane.showMessageDialog(null, "Invalid Input!");
						} else {
							TreePath path;
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
							try {
								path = theAppModel.insertPerson(surnameField.getText(), nameField.getText(),
										dateChooser1.getDate(), numberField.getText(), bufferedPath);
								bufferedPath = "";
								if (path != null) {
									theTree.scrollPathToVisible(path);
								}
								System.out.println("hi");
								insertForm.setVisible(false);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}

				});

				insertExplorer.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser img_chooser = new JFileChooser();
						img_chooser.setCurrentDirectory(new java.io.File("."));
						img_chooser.setDialogTitle("Select image");
						img_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						img_chooser.setAcceptAllFileFilterUsed(false);

						if (img_chooser.showOpenDialog(insertExplorer) == JFileChooser.APPROVE_OPTION) {
							if (img_chooser.getSelectedFile().toString().contains(".jpg"))
								bufferedPath = img_chooser.getSelectedFile().toString();
							else {
								bufferedPath = "";
								JOptionPane.showMessageDialog(null, "File error!");
							}
						}

					}
				});
				insertForm.add(insertPanel);
				insertForm.setSize(400, 200);
				insertForm.setVisible(true);

				// TreePath path = theAppModel.insertPerson(textVal);
				// if (path != null) {
				// theTree.scrollPathToVisible(path);
			}
			if (event.getSource().equals(editButton)) {
				numberField.setEditable(true);
				explorer.setEnabled(true);
				nameField.setEditable(true);
				surnameField.setEditable(true);
				dateChooser.setEnabled(true);
				saveButton.setEnabled(true);
				editButton.setEnabled(false);
			}
			if (event.getSource().equals(saveButton)) {
				if (((nameField.getText().equals(""))) || ((numberField.getText().contains(" ")))
						|| ((nameField.getText().contains(" "))) || ((surnameField.getText().contains(" ")))
						|| ((surnameField.equals("")))) {
					JOptionPane.showMessageDialog(null, "Invalid Input!");
				} else {
					if (selectedNode.getParent() != null)
						theAppModel.deletePerson(selectedNode);
					TreePath path;
					try {
						path = theAppModel.insertPerson(surnameField.getText(), nameField.getText(),
								dateChooser.getDate(), numberField.getText(), bufferedPath);
						bufferedPath = "";
						if (path != null) {
							theTree.scrollPathToVisible(path);
						}
						numberField.setEditable(false);
						explorer.setFocusable(false);
						nameField.setEditable(false);
						dateChooser.setEnabled(false);
						saveButton.setEnabled(false);
						surnameField.setEditable(false);
						editButton.setEnabled(true);
						System.out.println("hi");

					} catch (IOException e) {
						e.printStackTrace();

					}
				}
			}
			if (event.getSource().equals(findButton)) {
				JFrame findForm = new JFrame();
				findForm.setVisible(true);
				saveButton.setEnabled(false);
				JTextField findField;
				JButton okButton;
				JPanel findPanel = new JPanel();
				JPanel labelsPanel = new JPanel();
				JPanel textFieldsPanel = new JPanel();
				findPanel.setLayout(new BorderLayout());
				labelsPanel.setLayout(new GridLayout(1, 1));
				textFieldsPanel.setLayout(new GridLayout(1, 1));
				labelsPanel.add(new JLabel("Search:"));
				textFieldsPanel.add(findField = new JTextField());
				findPanel.add(labelsPanel, "West");
				findPanel.add(textFieldsPanel, "Center");
				findPanel.add(okButton = new JButton("OK"), "South");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						TreePath path = theAppModel.findPerson(findField.getText());
						if (path != null) {
							theTree.scrollPathToVisible(path);
							// System.out.println("hello");
							findForm.setVisible(true);
						} else {
							java.awt.Toolkit tk = Toolkit.getDefaultToolkit();
							tk.beep();
						}
					}
				});

				findForm.add(findPanel);
				findForm.setSize(400, 100);
			}

			if (selectedNode == null)
				return;

			if (event.getSource().equals(deleteButton)) {
				if (selectedNode.getParent() != null)
					theAppModel.deletePerson(selectedNode);
				surnameField.setText("");
				nameField.setText("");
				numberField.setText("");
				dateChooser.setDate(null);
				BufferedImage tmp = null;
				try {
					tmp = ImageIO.read(new File(defaultPath));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JLabel lpic = new JLabel();
				lpic.setIcon(new ImageIcon(tmp.getScaledInstance(128, 128, Image.SCALE_AREA_AVERAGING)));
				imPanel.removeAll();
				imPanel.add(lpic);
				imPanel.updateUI();
				editButton.setEnabled(false);
				numberField.setEditable(false);
				explorer.setEnabled(false);
				nameField.setEditable(false);
				dateChooser.setEnabled(false);
				saveButton.setEnabled(false);
				return;
			}

		}
	}

	public void valueChanged(TreeSelectionEvent event) {
		TreePath path = theTree.getSelectionPath();
		if (path == null)
			return;

		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();

		if (selectedNode != null && selectedNode.getUserObject() instanceof DictionaryEntry) {

			imPanel.removeAll();
			surnameField.setText(((DictionaryEntry) selectedNode.getUserObject()).getSurname());
			nameField.setText(((DictionaryEntry) selectedNode.getUserObject()).getName());

			dateChooser.setDate(((DictionaryEntry) selectedNode.getUserObject()).getBirth());

			numberField.setText(((DictionaryEntry) selectedNode.getUserObject()).getNumber());
			JLabel lpic = new JLabel(new ImageIcon(((DictionaryEntry) selectedNode.getUserObject()).getPhoto()
					.getScaledInstance(128, 128, Image.SCALE_AREA_AVERAGING)));
			imPanel.add(lpic);
			imPanel.updateUI();
			editButton.setEnabled(true);
			deleteButton.setEnabled(true);
			saveButton.setEnabled(false);
			numberField.setEditable(false);
			explorer.setEnabled(false);
			nameField.setEditable(false);
			surnameField.setEditable(false);
			dateChooser.setEnabled(false);

		} else {
			nameField.setText("");
			surnameField.setText("");
			dateChooser.setDate(null);
			numberField.setText("");
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
			numberField.setEditable(false);
			explorer.setEnabled(false);
			nameField.setEditable(false);
			surnameField.setEditable(false);
			dateChooser.setEnabled(false);
			BufferedImage tmp = null;
			try {
				tmp = ImageIO.read(new File(defaultPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JLabel lpic = new JLabel();
			lpic.setIcon(new ImageIcon(tmp.getScaledInstance(128, 128, Image.SCALE_AREA_AVERAGING)));
			imPanel.removeAll();
			imPanel.add(lpic);
			imPanel.updateUI();
		}

	}

}

// ------------------------------------------------------------Ч
public class Tree_GUI {

	public static void main(String[] args) throws IOException {
		SwingGUI5Model theAppModel = new SwingGUI5Model();
		SwingGUI5 theFrame = new SwingGUI5(theAppModel);
		theFrame.show();

	}
}