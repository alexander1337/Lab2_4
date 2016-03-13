import java.awt.event.*;
import java.awt.*;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
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
	protected String birth;
	protected String photo;
	// protected String theAddress;
	protected String id;

	public DictionaryEntry(String name, String birth, String id, String photo) {
		theName = name;
		this.birth = birth;
		this.id = id;
		this.photo = photo;
		// theAddress = address;

	}

	public DictionaryEntry(String completeStr) {
		int delim_index = completeStr.indexOf(" ");
		int length = completeStr.length();

		if (delim_index <= 0) {
			delim_index = length;
		}

		theName = completeStr.substring(0, delim_index);
		// theAddress = completeStr.substring(delim_index);
	}

	public String getType() {
		return "Entry";
	}

	public String getValue() {
		return theName;
	}

	public String toString() {
		return theName;
	}

	public String getBirth() {
		return birth;
	}

	public String getId() {
		return id;
	}

	public String getPhoto() {
		return photo;
	}

}

// ----------------------------------------------------------------—
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

	public TreePath insertPerson(String name, String birth, String id, String photo) {
		TreePath path;
		DictionaryAnchor anchor = new DictionaryAnchor();

		anchor.topic = null;
		anchor.entry = null;

		DictionaryEntry new_entry = new DictionaryEntry(name, birth, id, photo);

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

}

// -----------------------------------------------------------—
class SwingGUI5 extends JFrame implements ActionListener, TreeSelectionListener {
	private SwingGUI5Model theAppModel;

	private JTree theTree;
	private JTextArea theTextArea;
	private JButton insertButton;
	private JButton deleteButton;
	private JButton findButton;
	private JButton editButton;
	private JButton saveButton;
	JTextField nameField;
	JTextField dateField;
	JTextField idField;
	JTextField photoField;

	private JButton changeLookFeelButton;

	private JTextField theTextField;

	private UIManager.LookAndFeelInfo installedLF[];

	private int current;

	protected Component buildGUI() {

		Container contentPane = this.getContentPane();
		// contentPane.setLayout (new FlowLayout());

		theTree = new JTree(theAppModel.buildDefaultTreeStructure());
		// theTree.setEditable(true);
		theTree.addTreeSelectionListener(this);
		int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
		theTree.getSelectionModel().setSelectionMode(mode);
		theTextArea = new JTextArea();
		JPanel labels = new JPanel();
		JPanel panel = new JPanel();
		JPanel form = new JPanel();
		JPanel textFields = new JPanel();
		textFields.setLayout(new GridLayout(4, 1));
		labels.setLayout(new GridLayout(4, 1));
		panel.setLayout(new GridLayout(1, 2));
		form.setLayout(new BorderLayout());

		labels.add(new JLabel("Full name:"));
		textFields.add(nameField = new JTextField());
		nameField.setEditable(false);
		labels.add(new JLabel("Date of Birth:"));
		textFields.add(dateField = new JTextField());
		dateField.setEditable(false);
		labels.add(new JLabel("ID:"));
		textFields.add(idField = new JTextField());
		idField.setEditable(false);
		labels.add(new JLabel("Photo:"));
		textFields.add(photoField = new JTextField());
		photoField.setEditable(false);
		form.add(labels, "West");
		form.add(textFields, "Center");
		panel.add(new JScrollPane(theTree));
		panel.add(form);
		contentPane.add(panel, "Center");
		JPanel panelButton = new JPanel();

		insertButton = new JButton("Insert Person");
		insertButton.addActionListener(this);

		deleteButton = new JButton("Delete");
		deleteButton.addActionListener(this);

		findButton = new JButton("Find");
		findButton.addActionListener(this);
		saveButton = new JButton("Save Changes");
		saveButton.addActionListener(this);
		saveButton.setEnabled(false);
		editButton = new JButton("Edit Current Node");
		editButton.addActionListener(this);
		editButton.setEnabled(true);

		changeLookFeelButton = new JButton("change Look & Feel");
		changeLookFeelButton.addActionListener(this);

		panelButton.add(insertButton);
		panelButton.add(deleteButton);
		panelButton.add(findButton);
		panelButton.add(editButton);
		panelButton.add(changeLookFeelButton);
		panelButton.add(saveButton);
		contentPane.add(panelButton, "South");

		return null;
	}

	public SwingGUI5(SwingGUI5Model appModel) {
		theAppModel = appModel;

		setTitle("Tree example with model");
		setSize(800, 200);

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
				JButton okButton;
				JTextField nameField;
				JTextField dateField;
				JTextField idField;
				JTextField photoField;
				JFrame insertForm = new JFrame();
				JPanel insertPanel = new JPanel();
				JPanel labelsPanel = new JPanel();
				JPanel textFieldsPanel = new JPanel();
				insertPanel.setLayout(new BorderLayout());
				labelsPanel.setLayout(new GridLayout(4, 1));
				textFieldsPanel.setLayout(new GridLayout(4, 1));
				labelsPanel.add(new JLabel("Full name:"));
				textFieldsPanel.add(nameField = new JTextField());
				labelsPanel.add(new JLabel("Date of Birth:"));
				textFieldsPanel.add(dateField = new JTextField());
				labelsPanel.add(new JLabel("ID:"));
				textFieldsPanel.add(idField = new JTextField());
				labelsPanel.add(new JLabel("Photo:"));
				textFieldsPanel.add(photoField = new JTextField());
				insertPanel.add(labelsPanel, "West");
				insertPanel.add(textFieldsPanel, "Center");
				insertPanel.add(okButton = new JButton("OK"), "South");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// theAppModel.insertPerson(nameField.getText());
						TreePath path = theAppModel.insertPerson(nameField.getText(), dateField.getText(),
								idField.getText(), photoField.getText());
						if (path != null) {
							theTree.scrollPathToVisible(path);
						}
						System.out.println("hi");
						insertForm.setVisible(false);
					}
				});
				insertForm.add(insertPanel);
				insertForm.setSize(400, 200);
				insertForm.setVisible(true);
				// TreePath path = theAppModel.insertPerson(textVal);
				// if (path != null) {
				// theTree.scrollPathToVisible(path);
			}
			if(event.getSource().equals(editButton)){
				idField.setEditable(true);
				photoField.setEditable(true);
				nameField.setEditable(true);
				dateField.setEditable(true);
				saveButton.setEnabled(true);
				editButton.setEnabled(false);
			}
			if(event.getSource().equals(saveButton)){
				if (selectedNode.getParent() != null)
					theAppModel.deletePerson(selectedNode);
				TreePath path = theAppModel.insertPerson(nameField.getText(), dateField.getText(),
						idField.getText(), photoField.getText());
				if (path != null) {
					theTree.scrollPathToVisible(path);
				}
				idField.setEditable(false);
				photoField.setEditable(false);
				nameField.setEditable(false);
				dateField.setEditable(false);
				saveButton.setEnabled(false);
				editButton.setEnabled(true);
				System.out.println("hi");
				
			}
		}
		if (event.getSource().equals(findButton)) {

			TreePath path = theAppModel.findPerson(textVal);
			if (path != null) {
				theTree.scrollPathToVisible(path);
			}
		}

		if (selectedNode == null)
			return;

		if (event.getSource().equals(deleteButton)) {
			if (selectedNode.getParent() != null)
				theAppModel.deletePerson(selectedNode);
			dateField.setText("");
			nameField.setText("");
			idField.setText("");
			photoField.setText("");
			editButton.setEnabled(false);
			
			return;
		}

	}

	public void valueChanged(TreeSelectionEvent event) {
		TreePath path = theTree.getSelectionPath();
		if (path == null)
			return;

		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();

		if (selectedNode != null && selectedNode.getUserObject() instanceof DictionaryEntry) {
			nameField.setText(((DictionaryEntry) selectedNode.getUserObject()).getValue());
			dateField.setText(((DictionaryEntry) selectedNode.getUserObject()).getBirth());
			idField.setText(((DictionaryEntry) selectedNode.getUserObject()).getId());
			photoField.setText(((DictionaryEntry) selectedNode.getUserObject()).getPhoto());
			editButton.setEnabled(true);
			deleteButton.setEnabled(true);
			saveButton.setEnabled(false);
		} else {
			nameField.setText("");
			dateField.setText("");
			idField.setText("");
			photoField.setText("");
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
		}

	}

}

// ------------------------------------------------------------—
public class Tree_GUI {

	public static void main(String[] args) {
		SwingGUI5Model theAppModel = new SwingGUI5Model();
		SwingGUI5 theFrame = new SwingGUI5(theAppModel);
		theFrame.show();

	}
}