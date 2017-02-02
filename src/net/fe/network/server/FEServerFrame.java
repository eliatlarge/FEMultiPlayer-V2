package net.fe.network.server;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.fe.game.modifier.Modifier;
import net.fe.game.modifier.ModifierList;
import net.fe.game.pick.AllPick;
import net.fe.game.pick.Draft;
import net.fe.game.pick.PickMode;
import net.fe.overworldStage.objective.Objective;

public class FEServerFrame extends JFrame {

	private JPanel mainPanel;
	private JPanel mapPanel;
	private JPanel objectivePanel;
	private JSeparator separator;
	private JLabel modifiersLabel;
	private JPanel modifiersPane;
	private JScrollPane selectedModifiersScrollPane;
	private ModifierJList selectedModifiersList;
	private JPanel buttonsPanel;
	private JScrollPane modifiersScrollPane;
	private ModifierJList modifiersList;
	private JPanel panel;
	private JLabel label_1;
	private JSpinner spnPort;
	private JButton startServer;
	private JButton removeModifierBtn;
	private JButton addModifierBtn;
	private JLabel objLabel;
	private JComboBox<Objective> objComboBox;
	private JLabel lblPickMode;
	private JComboBox<PickMode> pickModeBox;
	private JLabel mapNameLabel;
	private JComboBox<String> mapSelectionBox;
	private JLabel label;
	private JSpinner maxUnitsSpinner;
	
	private FEServerProperties properties;

	public FEServerFrame() {

		setTitle("FEServer");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		getContentPane().setLayout(new BorderLayout(0, 0));
		DefaultListModel<Modifier> selectedModifiersModel = new DefaultListModel<Modifier>();
		// Modifiers
		DefaultListModel<Modifier> unselectedModifiersModel = new DefaultListModel<Modifier>();
		
		for(Modifier mod : ModifierList.values())
			unselectedModifiersModel.addElement(mod);

		mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		mapPanel = new JPanel();
		mainPanel.add(mapPanel);
		mapPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		mapNameLabel = new JLabel("Map: ");
		mapPanel.add(mapNameLabel);

		objectivePanel = new JPanel();
		mainPanel.add(objectivePanel);

		objLabel = new JLabel("Objective: ");
		objectivePanel.add(objLabel);

		objComboBox = new JComboBox<>();
		objectivePanel.add(objComboBox);

		// populate list of FEServer.getMaps()
		mapSelectionBox = new JComboBox<>();
		mapSelectionBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				objComboBox.setModel(new DefaultComboBoxModel<Objective>(FEServer.getMaps().get(mapSelectionBox.getSelectedItem())));
			}
		});
		mapPanel.add(mapSelectionBox);
		mapSelectionBox.setModel(new DefaultComboBoxModel<String>(FEServer.getMaps().keySet().toArray(new String[0])));

		label = new JLabel("Max units: ");
		mapPanel.add(label);

		maxUnitsSpinner = new JSpinner();
		mapPanel.add(maxUnitsSpinner);
		maxUnitsSpinner.setModel(new SpinnerNumberModel(8, 1, 8, 1));

		// Objectives
		ComboBoxModel<Objective> oModel = new DefaultComboBoxModel<>(
				FEServer.getMaps().get(mapSelectionBox.getSelectedItem()));
		objComboBox.setModel(oModel);

		lblPickMode = new JLabel("Pick mode: ");
		objectivePanel.add(lblPickMode);

		// Pick modes
		
		//TODO HARD CODED AAAAAAAAAA
		ComboBoxModel<PickMode> pickModeModel = new DefaultComboBoxModel<>(
				new PickMode[] { new AllPick(), new Draft() });
		pickModeBox = new JComboBox<>();
		pickModeBox.setModel(pickModeModel);
		objectivePanel.add(pickModeBox);

		separator = new JSeparator();
		mainPanel.add(separator);

		modifiersLabel = new JLabel("Modifiers");
		modifiersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(modifiersLabel);

		modifiersPane = new JPanel();
		mainPanel.add(modifiersPane);
		modifiersPane.setLayout(new BoxLayout(modifiersPane, BoxLayout.X_AXIS));

		selectedModifiersScrollPane = new JScrollPane();
		selectedModifiersScrollPane.setPreferredSize(new Dimension(120, 150));
		modifiersPane.add(selectedModifiersScrollPane);

		modifiersScrollPane = new JScrollPane();
		modifiersScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		modifiersScrollPane.setPreferredSize(new Dimension(120, 150));

		modifiersList = new ModifierJList();
		modifiersScrollPane.add(modifiersList);
		modifiersList.setModel(unselectedModifiersModel);
		modifiersScrollPane.setViewportView(modifiersList);

		selectedModifiersList = new ModifierJList();
		selectedModifiersScrollPane.add(selectedModifiersList);
		selectedModifiersList.setModel(selectedModifiersModel);
		selectedModifiersScrollPane.setViewportView(selectedModifiersList);

		buttonsPanel = new JPanel();
		modifiersPane.add(buttonsPanel);

		addModifierBtn = new JButton("<-- Add");
		addModifierBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int index;
				while ((index = modifiersList.getSelectedIndex()) != -1) {
					Modifier o = unselectedModifiersModel.getElementAt(index);
					unselectedModifiersModel.remove(modifiersList.getSelectedIndex());
					selectedModifiersModel.add(0, o);
				}
			}
		});
		buttonsPanel.setLayout(new GridLayout(0, 1, 0, 0));
		buttonsPanel.add(addModifierBtn);

		removeModifierBtn = new JButton("Remove -->");
		removeModifierBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index;
				while ((index = selectedModifiersList.getSelectedIndex()) != -1) {
					Modifier o = selectedModifiersModel.getElementAt(index);
					selectedModifiersModel.remove(selectedModifiersList.getSelectedIndex());
					unselectedModifiersModel.add(0, o);
				}
			}
		});
		buttonsPanel.add(removeModifierBtn);

		modifiersPane.add(modifiersScrollPane);

		panel = new JPanel();
		mainPanel.add(panel);

		label_1 = new JLabel("Port :");
		panel.add(label_1);

		spnPort = new JSpinner();
		spnPort.setModel(new SpinnerNumberModel(FEServer.DEFAULT_PORT, 0, 65535, 1));
		panel.add(spnPort);

		startServer = new JButton("Start server");
		startServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeFrameAndStartServer();
			}
		});
		getContentPane().add(startServer, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public int getPort() {
		return (Integer) spnPort.getValue();
	}

	private void changeFrameAndStartServer() {
		String ipPort = "";
		try {
			ipPort = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e3) {
			e3.printStackTrace();
		}
		if(getPort() != FEServer.DEFAULT_PORT)
			ipPort += ":" + getPort();
		
		JLabel label = new JLabel("Server IP: " + ipPort);
		label.setFont(getFont().deriveFont(20f));
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		getContentPane().add(label, BorderLayout.NORTH);
			
		remove(mainPanel);
		remove(startServer);
		
		JButton btnkickAll = new JButton("Kick all");
		btnkickAll.addActionListener(event -> FEServerProperties.kickAll());
		getContentPane().add(btnkickAll);


		JButton btnCopyClipboard = new JButton("Copy to clipboard");
		
		String thanksJava = ipPort;
		btnCopyClipboard.addActionListener(event -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(thanksJava), null));
		
		getContentPane().add(btnCopyClipboard, BorderLayout.AFTER_LAST_LINE);
		pack();
		
		for (int i = 0; i < selectedModifiersList.getModel().getSize(); i++) 
			FEServerProperties.addModifier(selectedModifiersList.getModel().getElementAt(i));
		
		FEServerProperties.setObjective((Objective) objComboBox.getSelectedItem());
		FEServerProperties.setMap((String) mapSelectionBox.getSelectedItem());
		FEServerProperties.setMaxUnits((Integer) maxUnitsSpinner.getValue());
		FEServerProperties.setPickMode((PickMode) pickModeBox.getSelectedItem());
		
		FEServerProperties.startServer();
	}

	private class ModifierJList extends JList<Modifier> {
	
		private static final long serialVersionUID = 561574462354745569L;
	
		public String getToolTipText(MouseEvent event) {
			Point p = event.getPoint();
			int index = locationToIndex(p);
			String tip = ((Modifier) getModel().getElementAt(index)).getDescription();
			return tip;
		}

}
	
}
