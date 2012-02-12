package kylealbert.wolf3dmm.gui;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractListModel;
import java.awt.Dimension;
import javax.swing.border.BevelBorder;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.border.TitledBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.ComponentOrientation;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JToolBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.swing.JSeparator;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import javax.swing.JScrollBar;

public class MainWindow extends JFrame {

	private JPanel mainContentPane;
	
	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setTitle("Kyle's Wolfenstein Map Editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 600);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				processWindowEvent( new WindowEvent(MainWindow.this, WindowEvent.WINDOW_CLOSING) );
			}
		});
		
		JMenuItem mntmNew = new JMenuItem("New Project...");
		mnFile.add(mntmNew);
		
		JMenuItem mntmOpenProject = new JMenuItem("Open Project...");
		mnFile.add(mntmOpenProject);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		mntmExit.setMargin(new Insets(0, 0, 0, 50));
		mnFile.add(mntmExit);
		
		JMenu mnProject = new JMenu("Project");
		menuBar.add(mnProject);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About...");
		mnHelp.add(mntmAbout);
		mainContentPane = new JPanel();
		mainContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainContentPane);
		mainContentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel statusLabel = new JLabel("STATUS");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		mainContentPane.add(statusLabel, BorderLayout.SOUTH);
		
		JPanel objPanel = new JPanel();
		objPanel.setSize(new Dimension(200, 0));
		objPanel.setBorder(new TitledBorder(null, "Map Objects", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainContentPane.add(objPanel, BorderLayout.EAST);
		objPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel toolboxPanel = new JPanel();
		toolboxPanel.setBorder(new TitledBorder(null, "Toolbox", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		objPanel.add(toolboxPanel, BorderLayout.SOUTH);
		toolboxPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnToolBoxPencil = new JButton("P");
		btnToolBoxPencil.setPreferredSize(new Dimension(40, 30));
		toolboxPanel.add(btnToolBoxPencil);
		
		JButton btnS = new JButton("S");
		btnS.setPreferredSize(new Dimension(40, 30));
		btnS.setRequestFocusEnabled(false);
		toolboxPanel.add(btnS);
		
		JButton btnE = new JButton("E");
		btnE.setPreferredSize(new Dimension(40, 30));
		btnE.setRequestFocusEnabled(false);
		toolboxPanel.add(btnE);
		
		JPanel mapPanel = new JPanel();
		mapPanel.setBorder(new TitledBorder(null, "Map View", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainContentPane.add(mapPanel, BorderLayout.CENTER);
		mapPanel.setLayout(new BorderLayout(0, 0));
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		mainContentPane.add(toolBar, BorderLayout.NORTH);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
