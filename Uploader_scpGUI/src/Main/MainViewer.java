package Main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import images.*;
import javax.imageio.ImageIO;
import javax.sound.midi.SysexMessage;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import modeles.Cluster;
import modeles.ClusterList;
import modeles.Session;

import IO.FileParser;
import IO.HTMLparser;
import IO.TarParser;
import Main.display.ClusterSelect;
import Main.display.FileTree;
import Main.display.ImagePanel;
import Main.display.JListFrame;
import Main.display.JLogEditorPane;
import Main.display.LoadingFrame;
import Main.display.Terminal;
import Main.display.MainMenu;
import Main.display.SetupWindow;
import Main.display.style.JTBorderFactory;
import Main.list.TarList;
import Main.list.UploadList;
import Threads.ExecuteCommand;
import Threads.ImageJ_bridge;
import Threads.monitoring.CheckPerformance;
import net.miginfocom.swing.MigLayout;
import javax.swing.JInternalFrame;
import javax.swing.JScrollBar;
import javax.swing.JToolBar;
import java.awt.SystemColor;
import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTextPane;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JDesktopPane;
import javax.swing.border.LineBorder;
import javax.swing.JSplitPane;
import javax.swing.Box;
import javax.swing.JSeparator;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import javax.swing.JEditorPane;



/**
 * Main class of the program
 * @author Jérémy DEVERDUN
 *
 */
public class MainViewer extends JPanel{
	private ArrayList<String> cancelled;
	private Graphics2D g;
	private JListFrame selectClust;
	private JButton addLink;
	private JButton startDownload;
	private JTextField linkurl;
	private UploadList dl;
	private TarList tarlist;
	private JFileChooser chooser;
	private JToolBar toolBar;
	private JTable table;
	private JPanel panel;
	private DefaultTableModel modeltable;
	private JProgressBar CPUprogressBar_3;
	private JProgressBar MemprogressBar_1;
	private JProgressBar DiskprogressBar_2;
	private HashMap<String,Integer> rowForDir;
	private JPanel panel_2;
	private JPanel panel_3;
	private JTabbedPane tabbedPane_2;
	private JPanel panel_6;
	private JTextField textField;
	private JPanel panel_7;
	private JProgressBar IJprogressBar;
	private JProgressBar tarprogressBar;
	private JProgressBar uploadprogressBar;
	private JLabel lblCpu;
	private JLabel lblMem;
	private JLabel lblHd;
	private JLabel lblIj;
	private JLabel lblTar;
	private JLabel lblUp;
	private FileTree filetree;
	private FileTree remotetree;
	private MainMenu mainmenu;
	private JPanel panel_1;
	private JToolBar toolBar_3;
	private JToolBar toolBar_4;
	private JSplitPane splitPane;
	private JSplitPane splitPaneTerm;
	private JSplitPane splitPaneTermLog;
	private JLogEditorPane editorPaneLog;
	private JPanel panel_4;
	private JTextField textFieldpath;
	private JButton button;
	private JScrollPane scrollPane;
	private File[] listSection;
	private boolean ispreprocessing=false;
	private JButton btnUp;
	private JButton btnClearAll;
	private CheckPerformance check;
	private JLabel lblConnectedTo;
	private JLabel lblHost;
	private JButton btnRefreshjob;
	private JButton btnDownload;
	private JButton btnCanceljob;
	private Component horizontalStrut;
	private JButton btnLaunchfailed;
	private ImagePanel imagepan;
	
	/**
	 * Execute on shutdown
	 * @author Jérémy DEVERDUN
	 *
	 */
	class ShutdownThread extends Thread {
        private ExecuteCommand ec;
        private CheckPerformance cp;
        ShutdownThread(ExecuteCommand conn) {
            this.ec = conn;
        }
        public void run() {
        	try{
        		cp.stop();
        		
        	}catch(Exception e){
        		e.printStackTrace();
        	}
            try {
                if(ClassManagement.isnewsession){
	                File dir=(new File(ClassManagement.installDir+File.separator+ClassManagement.Session.getId()));
					FileParser.deleteDir(dir);
                }else{
                	ClassManagement.Session.save(new File(ClassManagement.installDir+File.separator+ClassManagement.Session.getId()));
                }
                this.ec.disconnect();
            } catch (Exception ex) {
                // log the exception
            	ex.printStackTrace();
            }
            try{
            	
            	//ClassManagement.Session.saveOnCluster();
            	ClassManagement.matlabserver.stopThread();
            }catch(Exception e){
            	e.printStackTrace();
            }
        }
        public void setCp(CheckPerformance c){
            this.cp=c;
        }
    }
	
	
	public MainViewer(){
        try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setAutoscrolls(true);
		setOpaque(false);
		setForeground(SystemColor.scrollbar);
		ShutdownThread shut=new ShutdownThread(ClassManagement.ssh);
	    Runtime.getRuntime().addShutdownHook(shut);
		linkurl=new JTextField(20);
		linkurl.setText("");
	    SetProperties(false); 
		setSize(new Dimension(950, 756));
    	dl=new UploadList();
		ImageIcon icon=new ImageIcon(MainViewer.class.getResource("/images/forward.png"));
		Image img = icon.getImage();  
		Image newimg = img.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);  
		icon = new ImageIcon(newimg); 
		startDownload=new JButton(icon);
		startDownload.setToolTipText("Process data");
		this.setBackground(Color.black);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(900, 600));
		panel.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panel);
		panel.setLayout(new MigLayout("", "[919.00px]", "[][669px,grow][]"));
		
		panel_1 = new JPanel();
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		filetree=new FileTree(ClassManagement.installDir);
//		BufferedImage imgtmp = null;
//		try {
//		    imgtmp = ImageIO.read(new File("C:\\Users\\Mobilette\\Desktop\\shark-picture.jpg"));
//		} catch (IOException e) {
//			System.out.println("ou est l'image");
//		}
//		imagepan=new ImagePanel(imgtmp);
//		
//		imagepan.setBorder(JTBorderFactory.createTitleBorder("Image"));
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				filetree, imagepan);
		splitPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		splitPane.setDividerSize(10);
		splitPane.setDividerLocation(240);
		splitPane.setOneTouchExpandable(true);
		splitPane.setMinimumSize(new Dimension(202, 202));
		splitPane.setPreferredSize(new Dimension(0, 420));

		panel_1.add(splitPane);
		
		filetree.setBorder(JTBorderFactory.createTitleBorder("Local Directory"));
		
		panel_4 = new JPanel();
		filetree.add(panel_4, BorderLayout.NORTH);
		
		textFieldpath = new JTextField((new File(".")).getAbsolutePath());
		textFieldpath.setEditable(false);
		panel_4.add(textFieldpath);
		textFieldpath.setColumns(12);
		ImageIcon icon2=new ImageIcon(MainViewer.class.getResource("/images/folder.png"));
		img = icon2.getImage();  
		newimg = img.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH);  
		icon = new ImageIcon(newimg); 
		button = new JButton(icon);
		panel_4.add(button);
		
		icon2=new ImageIcon(MainViewer.class.getResource("/images/up.png"));
		img = icon2.getImage();  
		newimg = img.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH);  
		icon = new ImageIcon(newimg); 
		btnUp = new JButton(icon);
		btnUp.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		filetree.changeTree((new File(textFieldpath.getText()+File.separator+"..")));
        		try {
					textFieldpath.setText(filetree.getRoot().getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
        		
        	}
        });
		panel_4.add(btnUp);
		button.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		selectFolder();
        		textFieldpath.setText(chooser.getSelectedFile().getAbsolutePath());
        		filetree.changeTree(new File(chooser.getSelectedFile().getAbsolutePath()));
        	}
        });
		
		toolBar_4 = new JToolBar();
		toolBar_4.setPreferredSize(new Dimension(800, 520));

		splitPaneTerm = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				splitPane, toolBar_4);

		
		tabbedPane_2 = new JTabbedPane(JTabbedPane.TOP);
		toolBar_4.add(tabbedPane_2);
		
		table = new JTable(){
			  public boolean isCellEditable(int rowIndex, int colIndex) {
				  return false; //Disallow the editing of any cell
				  }
				  };
		table.setAlignmentX(Component.RIGHT_ALIGNMENT);
		table.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		table.setFillsViewportHeight(true);
		table.setAutoscrolls(false);
		
		table.setPreferredSize(new Dimension(600, 500));
		table.setAutoCreateRowSorter(true);
		modeltable=new DefaultTableModel(
				new Object[100][2] ,
				new String[] {
					"Name", "Status"
				});
		table.setModel(modeltable);
		table.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==KeyEvent.VK_DELETE){
					int row=table.getSelectedRow();
					String pref=(String)table.getValueAt(row, 0);
					table.setValueAt("Removed", row, 1);
					System.out.println("remove : "+pref+"-"+row);
					//modeltable.removeRow(row);
					removeItem(pref);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.setDefaultRenderer(table.getColumnClass(0), renderer);
		table.setDefaultRenderer(table.getColumnClass(1), renderer);
		scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(469, 400));
		tabbedPane_2.addTab("Files Status", null, scrollPane, null);
		
		
		Terminal terminal=new Terminal(5,40);
		ClassManagement.Terminal=terminal;
		terminal.setMinimumSize(new Dimension(300, 450));
		FlowLayout flowLayout = (FlowLayout) terminal.getLayout();
		terminal.setSize(new Dimension(400, 500));
		terminal.setPreferredSize(new Dimension(400, 500));
		panel_6 = new JPanel();
		tabbedPane_2.addTab("Terminal", null, panel_6, null);
		panel_6.setLayout(new BorderLayout(0, 0));
		panel_6.add(terminal,BorderLayout.CENTER);
		
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(600, 20));
		textField.setColumns(10);
		
		panel_3 = new JPanel();
		add(panel_3);
		
		toolBar = new JToolBar();
		panel.add(toolBar, "cell 0 0");
		toolBar.setName("Tools");
		icon2=new ImageIcon(MainViewer.class.getResource("/images/dirprocess.png"));
		img = icon2.getImage();  
		newimg = img.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);  
		icon = new ImageIcon(newimg); 
		addLink=new JButton(icon);
		addLink.setToolTipText("Directory to process");
		toolBar.add(addLink);
		
		toolBar.add(startDownload);

		
		
		icon2=new ImageIcon(MainViewer.class.getResource("/images/canceljob.png"));
		img = icon2.getImage();  
		newimg = img.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);  
		icon = new ImageIcon(newimg); 
		btnCanceljob = new JButton(icon);
		btnCanceljob.setToolTipText("Cancel all jobs");
		btnCanceljob.setEnabled(ClassManagement.Session.getJobid().size()>1);
		btnCanceljob.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ClassManagement.Session.getJobid().size()>1){
					Thread monThread=new Thread(){
						public void run(){
							FileParser.cancelAllJobs();
							ClassManagement.Session.clearAllID();
						}
					};
					monThread.start();
				}
			}
		});
		
		icon2=new ImageIcon(MainViewer.class.getResource("/images/clear.png"));
		img = icon2.getImage();  
		newimg = img.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);  
		icon = new ImageIcon(newimg); 
		btnClearAll=new JButton(icon);
		btnClearAll.setToolTipText("Clear list to process");
		toolBar.add(btnClearAll);
		btnClearAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				clearAll();
			}
		});

		
		toolBar.add(linkurl);
		linkurl.setEditable(false);
		
		linkurl.setSize(200, 100);
		
		horizontalStrut = Box.createHorizontalStrut(20);
		horizontalStrut.setPreferredSize(new Dimension(670, 0));
		toolBar.add(horizontalStrut);
		toolBar.add(btnCanceljob);
		icon2=new ImageIcon(MainViewer.class.getResource("/images/download.png"));
		img = icon2.getImage();  
		newimg = img.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);  
		icon = new ImageIcon(newimg); 
		btnDownload = new JButton(icon);
		btnDownload.setToolTipText("Retrieve all results");
		btnDownload.setEnabled(ClassManagement.Session.getJobid().size()>1);
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ClassManagement.Session.getJobid().size()>1){
					FileParser.retrieveAllOutput();
				}
			}
		});
		toolBar.add(btnDownload);
		icon2=new ImageIcon(MainViewer.class.getResource("/images/refresh.png"));
		img = icon2.getImage();  
		newimg = img.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);  
		icon = new ImageIcon(newimg); 
		btnRefreshjob = new JButton(icon);
		btnRefreshjob.setToolTipText("Update job status");
		btnRefreshjob.setEnabled(ClassManagement.Session.getJobid().size()>1);
		btnRefreshjob.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ClassManagement.Session.getJobid().size()>1){
					final Set<String> ks=ClassManagement.Session.getJobid().keySet();
					Thread monThread=new Thread(){
						public void run(){
							FileParser.getAllStatus();
							/*String status="";
							ClassManagement.done.clear();
							ClassManagement.failed.clear();
							ClassManagement.running.clear();
							ClassManagement.waiting.clear();
							for(String e:ks){
								status=FileParser.getStatus(e);
								table.setValueAt(status, getRowForDir().get(e), 1);
							}*/
						}
					};
					monThread.start();
				}
			}
		});
		
		icon2=new ImageIcon(MainViewer.class.getResource("/images/launchfailed.png"));
		img = icon2.getImage();  
		newimg = img.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);  
		icon = new ImageIcon(newimg); 
		btnLaunchfailed = new JButton(icon);
		btnLaunchfailed.setToolTipText("Launch failed jobs");
		btnLaunchfailed.setEnabled(ClassManagement.Session.getJobid().size()>1);
		btnLaunchfailed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				FileParser.launchFailed();
			}
		});
		toolBar.add(btnLaunchfailed);
		toolBar.add(btnRefreshjob);
		startDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ClassManagement.suspend){
					ImageIcon icon1=new ImageIcon(MainViewer.class.getResource("/images/pause.png"));
					Image img = icon1.getImage();  
					Image newimg = img.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);  
					icon1 = new ImageIcon(newimg); 
					startDownload.setIcon(icon1);
					ClassManagement.suspend=false;
					tarlist.setPaused(false);
					if(!ispreprocessing){
						//Custom button text
						Object[] options = {"Median filter",
						                    "None"};
						int n = JOptionPane.showOptionDialog(null,
						    "Smooth images with",
						    "Question",
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    options,
						    options[1]);
						if(n==0) ImageJ_bridge.MEDFILT=true;
						else ImageJ_bridge.MEDFILT=false;
						preprocessingAndTar(listSection);
					}
				}else{
					ClassManagement.suspend=true;
					tarlist.setPaused(true);
					ImageIcon icon1=new ImageIcon(MainViewer.class.getResource("/images/forward.png"));
					Image img = icon1.getImage();  
					Image newimg = img.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);  
					icon1 = new ImageIcon(newimg); 
					startDownload.setIcon(icon1);
					
				}
			}
		});
		addLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
		    	selectFolder();
				File file=chooser.getSelectedFile();
				traitement(file,false);
			}
		});
		
		splitPaneTermLog= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				splitPaneTerm, panel_3);
		splitPaneTerm.setDividerLocation(230);
		splitPaneTermLog.setPreferredSize(new Dimension(960, 600));
		panel.add(splitPaneTermLog, "flowx,cell 0 1,alignx left,aligny top");
		splitPaneTermLog.setDividerLocation(640);
		
		toolBar_3 = new JToolBar();
		panel.add(toolBar_3, "flowx,cell 0 2");
		toolBar_3.setOrientation(SwingConstants.VERTICAL);
		
		lblConnectedTo = new JLabel("Connected to ");
		panel.add(lblConnectedTo, "cell 0 2");
		
		lblHost = new JLabel(ClassManagement.clusterlist.getdefaultCluster().getName());
		panel.add(lblHost, "cell 0 2");
		panel_3.setLayout(new MigLayout("", "[120px][121px]", "[211px][369px]"));
		
		panel_2 = new JPanel();
		panel_3.add(panel_2, "cell 0 0,alignx left,aligny top");
		panel_2.setBorder(JTBorderFactory.createTitleBorder("Local load"));
		panel_2.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("30px"),},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("160px"),
				RowSpec.decode("bottom:default"),}));
		
		CPUprogressBar_3 = new JProgressBar();
		CPUprogressBar_3.setStringPainted(true);
		panel_2.add(CPUprogressBar_3, "2, 2, left, top");
		CPUprogressBar_3.setPreferredSize(new Dimension(30, 160));
		CPUprogressBar_3.setOrientation(SwingConstants.VERTICAL);
		CPUprogressBar_3.setValue(0);
		MemprogressBar_1 = new JProgressBar();
		MemprogressBar_1.setStringPainted(true);
		panel_2.add(MemprogressBar_1, "4, 2, left, top");
		MemprogressBar_1.setPreferredSize(new Dimension(30, 160));
		MemprogressBar_1.setOrientation(SwingConstants.VERTICAL);
		MemprogressBar_1.setValue(0);
		DiskprogressBar_2 = new JProgressBar();
		DiskprogressBar_2.setValue(0);
		DiskprogressBar_2.setStringPainted(true);
		panel_2.add(DiskprogressBar_2, "6, 2, left, top");
		DiskprogressBar_2.setPreferredSize(new Dimension(30, 160));
		DiskprogressBar_2.setOrientation(SwingConstants.VERTICAL);
		
		lblCpu = new JLabel("CPU");
		lblCpu.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(lblCpu, "2, 3");
		
		lblMem = new JLabel("Mem");
		lblMem.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(lblMem, "4, 3");
		
		lblHd = new JLabel("HD");
		lblHd.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(lblHd, "6, 3");
		panel_7 = new JPanel();
		panel_3.add(panel_7, "cell 0 0,alignx left,aligny top");
		panel_7.setBorder(JTBorderFactory.createTitleBorder("Process progress"));
		panel_7.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("30px"),},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("160px"),
				FormFactory.DEFAULT_ROWSPEC,}));
		
		IJprogressBar = new JProgressBar();
		IJprogressBar.setStringPainted(true);
		IJprogressBar.setPreferredSize(new Dimension(30, 160));
		IJprogressBar.setOrientation(SwingConstants.VERTICAL);
		panel_7.add(IJprogressBar, "2, 2, left, top");
		
		tarprogressBar = new JProgressBar();
		tarprogressBar.setStringPainted(true);
		tarprogressBar.setPreferredSize(new Dimension(30, 160));
		tarprogressBar.setOrientation(SwingConstants.VERTICAL);
		panel_7.add(tarprogressBar, "4, 2, left, top");
		
		uploadprogressBar = new JProgressBar();
		uploadprogressBar.setValue(0);
		uploadprogressBar.setStringPainted(true);
		uploadprogressBar.setPreferredSize(new Dimension(30, 160));
		uploadprogressBar.setOrientation(SwingConstants.VERTICAL);
		panel_7.add(uploadprogressBar, "6, 2, left, top");
		
		lblIj = new JLabel("IJ");
		lblIj.setHorizontalAlignment(SwingConstants.CENTER);
		panel_7.add(lblIj, "2, 3");
		
		lblTar = new JLabel("TAR");
		lblTar.setHorizontalAlignment(SwingConstants.CENTER);
		panel_7.add(lblTar, "4, 3");
		
		lblUp = new JLabel("UP");
		lblUp.setHorizontalAlignment(SwingConstants.CENTER);
		panel_7.add(lblUp, "6, 3");
		editorPaneLog = new JLogEditorPane();
		editorPaneLog.setEditable(false);
		ClassManagement.LogsPanel=editorPaneLog;
		JScrollPane logscroll=new JScrollPane(editorPaneLog);
		logscroll.setBorder(JTBorderFactory.createTitleBorder("Logs"));
		panel_3.add(logscroll, "cell 0 1,grow");
		
		mainmenu=new MainMenu();
		
		// Monitoring daemon
		check=new CheckPerformance();
		check.setCpu(CPUprogressBar_3);
		check.setMem(MemprogressBar_1);
		check.setDisk(DiskprogressBar_2);
		
		shut.setCp(check);
		check.start();
		
		
		
		ClassManagement.LogsPanel.addText("Interface loaded successfully.");
		ClassManagement.LogsPanel.addText("Connexion to "+ClassManagement.clusterlist.getdefaultCluster().getName()+" ... Sucess.");
		
		if(!linkurl.getText().equals("")) traitement(new File(ClassManagement.Session.getDirectory()),true);
	}
	/**
	 * 
	 * @return
	 */
	private JMenuBar getMainMenu() {
		return mainmenu;
	}
	
	/**
	 * Set globals variables 
	 * @param isloaded
	 */
    public void SetProperties(boolean isloaded) {
    	ClassManagement.clusterlist=new ClusterList<Cluster>();
    	cancelled=new ArrayList<String>();
    	tarlist=new TarList();
    	ClassManagement.MainWindow=this;
    	try{
		    String s = MainViewer.class.getResource("MainViewer.class").getFile();
		    s = s.substring(5, s.indexOf("!"));
		    File installDir = new File(s.substring(0, s.lastIndexOf("/")).replaceAll("%20", "\\ "));
		    ClassManagement.installDir=installDir;
	    }catch(Exception e){
	    	ClassManagement.installDir=new File(".");
	    }
	    SetupWindow sw;
	    File f = new File(ClassManagement.installDir+File.separator+"params.conf");
	    if(!f.exists()){
	    	File ftmp = new File(ClassManagement.installDir+File.separator+"clusters.conf");
	    	if(ftmp.exists()){
		    	String[] c=FileParser.getTextFromFile(ClassManagement.installDir+File.separator+"clusters.conf").split("#");
				for(String r:c){
					if(!r.equals("")){
						ClassManagement.clusterlist.add(new Cluster(r));
					}
				}
	    	}
	    	sw=new SetupWindow(1);
	    	sw.createAndShowGUI();
	    	while(sw.isShowing()){
	    		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
	    }else{
	    	String[] c=FileParser.getTextFromFile(ClassManagement.installDir+"/clusters.conf").split("#");
			for(String r:c){
				if(!r.equals("")){
					ClassManagement.clusterlist.add(new Cluster(r));
				}
			}
	    	setConfFromFile(f);
	    }
	    f = new File(ClassManagement.installDir+"/clusters.conf");
    	if(!f.exists()){
	    	sw=new SetupWindow(2);
	    	sw.createAndShowGUI();
	    	while(sw.isShowing()){
	    		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
	    	ClassManagement.clusterlist.save(ClassManagement.installDir+"/clusters.conf");
	    }else{
	    	if(ClassManagement.clusterlist.size()==0){
		    	String[] c=FileParser.getTextFromFile(ClassManagement.installDir+"/clusters.conf").split("#");
				for(String r:c){
					if(!r.equals("")){
						ClassManagement.clusterlist.add(new Cluster(r));
					}
				}
	    	}
	    }
	    f = new File(ClassManagement.installDir+"/session.log");
	    Session session=null;
	    if(isloaded){
	    	session=new Session(new File(ClassManagement.installDir+"/session.log"));
	    }else{
		    if(f.exists()){
		    	boolean r=true;
		    	do{
			    	Object[] options = {"Load session",
		                    "New Session"};
					int n = JOptionPane.showOptionDialog(new JFrame(),
					    "What shall I do for you ?",
					    "Information",
					    JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,
					    options,
					    options[0]);
					if(n==0){
						ClassManagement.isnewsession=false;
						r=FileParser.loadSession();
						if(r) return;
					}else{
						Object[] options2 = {"Whole gland","Isolated stacks"};
						int n2 = JOptionPane.showOptionDialog(new JFrame(),
						    "Type",
						    "Information",
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    options2,
						    options2[0]);
						f.delete();
						if(n2==0) ClassManagement.wholegland=true;
						else if(n2==1) ClassManagement.wholegland=false;
						session=new Session(ClassManagement.installDir+"/session.log");
						session.setWhole(ClassManagement.wholegland);
						session.createDir();
						r=false;
					}
		    	}while(r);
		    }else{
		    	session=new Session(ClassManagement.installDir+"/session.log");
				session.createDir();
		    }
	    }
	    ClassManagement.Session=session;
	    if(ClassManagement.clusterlist.getdefaultCluster()==null){
	    	selectClust=new JListFrame("Select default cluster and save.");
	    	selectClust.show();
	    	while(selectClust.isShowing()){
	    		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
	    }
	    ClassManagement.jobscriptsent=FileParser.retrieveScriptSent();
	    ExecuteCommand ec=new ExecuteCommand();
	    try{
	    	ClassManagement.ssh=ec;
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    System.out.println(ec.shell("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/*.session")+"---"+session.getId()+".session");
	    if(ec.shell("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/*.session").split("//")[1].equals(session.getId()+".session\n")){
      		String content = ec.shell("cat "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/"+session.getId()+".session");
      		new File(ClassManagement.installDir+File.separator+"session.log").delete();
      		FileParser.writeText(content, ClassManagement.installDir+File.separator+"session.log");
			session.setSessionFromFile(new File(ClassManagement.installDir+File.separator+"session.log"));    
			ClassManagement.Session.save(new File(ClassManagement.installDir+File.separator+ClassManagement.Session.getId()));
      	}
    	
	}
    
    /**
     * Set configuration from file
     * @param f
     */
    private void setConfFromFile(File f) {
		String text=FileParser.getTextFromFile(f.getAbsolutePath());
		String[] conf=(text.split("\n"));
		for(int i=0;i<conf.length;i++){
			try {
				String name=conf[i].split("=")[0];
				String value=conf[i].split("=")[1];
				switch(i){
					case 0:
						ClassManagement.isHPCLR=false;
						ClassManagement.isLPTA=false;
						if(value.equals("CMD")) ClassManagement.isHPCLR=true;
						else if(value.equals("JDL")) ClassManagement.isLPTA=true;
						break;
					case 1:
						Cluster ctmp=null;
						for(Cluster c:ClassManagement.clusterlist){
							if(c.getID()==Integer.parseInt(value)){
								ctmp=c;
							}
						}
						ClassManagement.clusterlist.setdefaultCluster(ctmp);
						break;
					case 2:
						ClassManagement.jdlname=value;
						break;
					case 3:
						ClassManagement.MAXMEMORY=Integer.parseInt(value);break;
					case 4:
						ClassManagement.vo=value;break;
					case 5:
						ClassManagement.lfn_file_list=value;break;
					case 6:
						ClassManagement.srm_url=value;break;
					case 7:
						ClassManagement.guid_file_list=value;break;
					case 8:
						ClassManagement.IJmaxThreads=Integer.parseInt(value);break;
				}
				
			} catch (Exception e) {
				System.out.println(e.toString());
			} 
		}
	}
    
    /**
     * Find files to add to TODO list 
     * @param file
     * @param batch
     */
    public void traitement(File file,boolean batch){
    	if(file!=null){
    		final File filef=file;
    		ClassManagement.killAllThread=false;
    		ClassManagement.Session.addLog("dir="+file.getAbsolutePath());
    		ClassManagement.Session.addLog("batch="+batch);
    		ClassManagement.Session.setDirectory(file.getAbsolutePath());
			linkurl.setText(file.toString());
			if(batch) ClassManagement.full=true;
			int nbsections=file.listFiles().length;
			FileParser.clearDirFromStack(file);
			//int foldernb=FileParser.recursiveDirList(file);
			final LoadingFrame lf=new LoadingFrame("Retrieving data", true);
			Thread monThread=new Thread(){
				public void run(){
					listSection=filef.listFiles();
					int row=0;
					File[] listTranche;
					rowForDir=new HashMap<String,Integer>();
					for(int section=0;section<listSection.length;section++){
						if(ClassManagement.wholegland){
							if(listSection[section].isDirectory()){
								listTranche=listSection[section].listFiles();
								for(int t=0;t<listTranche.length;t++){
									if(listTranche[t].isDirectory()){
										if(!rowForDir.containsKey(listTranche[t].getName())){
											rowForDir.put(listTranche[t].getName(),row);
											modeltable.insertRow(row++, new Object[]{listTranche[t].getName(),"Waiting"});
										}
									}
								}
							}
						}else{
							if(listSection[section].isFile()){
								String pref=listSection[section].getName().substring(0, listSection[section].getName().lastIndexOf("."));
								rowForDir.put(pref,row);
								modeltable.insertRow(row++, new Object[]{pref,"Waiting"});
							}
						}
					}
					IJprogressBar.setMaximum(row);
					tarprogressBar.setMaximum(row);
					uploadprogressBar.setMaximum(row);
					table.setPreferredSize(new Dimension(600,table.getRowHeight()*(table.getRowCount()-1)));
					lf.dispose();
					this.stop();
				}
			};
			monThread.start();			
    	}
    }
    
    /**
     * Clear all waiting list
     */
    public void clearAll(){
    	ispreprocessing=false;
    	ClassManagement.killAllThread=true;
    	if(modeltable!=null){ modeltable.setRowCount(1); }
    	if(table !=null)table.setPreferredSize(new Dimension(600,table.getRowHeight()*(table.getRowCount()-1)));
    	if(tarlist!=null) tarlist.removeAllWaitingList();
    	if(dl!=null) dl.removeAllWaitingList();
    	try{
    		HTMLparser.stopThread();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    /**
     * Update upload speed
     * @param s
     * @param r
     * @param jp
     */
	public void setSpeed(int s,int r,JProgressBar jp){
		table.setValueAt(""+(int)(jp.getPercentComplete()*100)+"% @ "+s+" Kb/s", r, 1);
	}
	
	/**
	 * Launching preprocessing to job submission
	 * @param l
	 */
	public void preprocessingAndTar(File[] l){
		boolean continu=true;
		if(!FileParser.CheckRemoteSession()){
			continu=false;
			Object[] options = {"Continue",
		            "Clear remote & continue","Cancel"};
					int n = JOptionPane.showOptionDialog(new JFrame(),
					    "Local and remote sessions don't match (or remote session not found)",
					    "Information",
					    JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,
					    options,
					    options[0]);
			String txt=FileParser.getTextFromFile(ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+".session");
			if(n==0 || n==1) {
				if(n==1){
					FileParser.clearRemoteDirectory();continu=true;
				}
				System.out.println("echo "+txt+" >"+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+
				File.separator+ClassManagement.Session.getId()+".session");
				ClassManagement.ssh.shell("echo "+txt+" >"+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"" +
						""+File.separator+ClassManagement.Session.getId()+".session");		
			}
					
			if(n==0) continu=true;
			else 
				if(n==2){
					ClassManagement.suspend=true;
					ImageIcon icon1=new ImageIcon(MainViewer.class.getResource("/images/start.png"));
					Image img = icon1.getImage();  
					Image newimg = img.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);  
					icon1 = new ImageIcon(newimg); 
					startDownload.setIcon(icon1);
				}	
		}
		if(continu){
	    	final File[] listSection=l;
	    	ispreprocessing=true;
	    	Thread monThread = new Thread() {
		         public void run() {
		        	 int row=0;
		        	 ClassManagement.step1=true;
		        	 String localdir=ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+File.separator+"images";
		        	 if(ClassManagement.wholegland){
		        		 for(int section=0;section<listSection.length;section++){
			 				if(listSection[section].isDirectory() && !cancelled.contains(listSection[section].getName())){
			 					if(!ClassManagement.jobscriptsent.contains((String)listSection[section].getName())){
				 					File rep_section=listSection[section];
				 					ClassManagement.LogsPanel.addText("Directory "+rep_section.getName()+" added to list.");
				 					if(ClassManagement.killAllThread) this.stop();
				 					String[] totar=FileParser.findFiles(rep_section);
				 					String[] extension=ClassManagement.extension;
				 					
				 					for(int i=0;i<totar.length;i++){
				 						if(totar[i]!=null){
				 							tarlist.add(totar[i]);
				 						}
				 					}	 			
			 					}else{
			 						modeltable.insertRow(rowForDir.get(listSection[section]), new Object[]{listSection[section].getName(),"Submitted"});
			 					}
			 				}
		        		 }
		 			}else{
		 				File rep_section=new File(ClassManagement.Session.getDirectory());
	 					ClassManagement.LogsPanel.addText("Directory "+rep_section.getName()+" added to list.");
	 					if(ClassManagement.killAllThread) this.stop();
	 					String[] totar=FileParser.findFiles(rep_section);
	 					String[] extension=ClassManagement.extension;
	 					for(int i=0;i<totar.length;i++){
	 						if(totar[i]!=null){
	 							tarlist.add(totar[i]);
	 						}
	 					}	 	
		 			}
		        	ClassManagement.step1=false;
		         }
			 };
			 monThread.start();
		}
    }
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String, Integer> getRowForDir() {
		return rowForDir;
	}
	
	/**
	 * 
	 * @param rowForDir
	 */
	public void setRowForDir(HashMap<String, Integer> rowForDir) {
		this.rowForDir = rowForDir;
	}
	
	/**
	 * 
	 * @return
	 */
	public UploadList getUploadList() {
		return dl;
	}
	
	/**
	 * 
	 * @param dl
	 */
	public void setUploadList(UploadList dl) {
		this.dl = dl;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getCancelled() {
		return cancelled;
	}
	
	/**
	 * 
	 * @param cancelled
	 */
	public void setCancelled(ArrayList<String> cancelled) {
		this.cancelled = cancelled;
	}
	
	/**
	 * 
	 * @return
	 */
	public JTable getTable() {
		return table;
	}
	
	/**
	 * 
	 * @return
	 */
	public TarList getTarlist() {
		return tarlist;
	}
	
	/**
	 * 
	 * @param tarlist
	 */
	public void setTarlist(TarList tarlist) {
		this.tarlist = tarlist;
	}
	
	/**
	 * 
	 * @param table
	 */
	public void setTable(JTable table) {
		this.table = table;
	}
	
	/**
	 * 
	 * @return
	 */
	public JProgressBar getTarprogressBar() {
		return tarprogressBar;
	}
	
	/**
	 * 
	 * @param tarprogressBar
	 */
	public void setTarprogressBar(JProgressBar tarprogressBar) {
		this.tarprogressBar = tarprogressBar;
	}
	
	/**
	 * 
	 * @return
	 */
	public FileTree getFiletree() {
		return filetree;
	}
	
	/**
	 * 
	 * @param filetree
	 */
	public void setFiletree(FileTree filetree) {
		this.filetree = filetree;
	}
	
	/**
	 * 
	 */
	public void paintComponent(Graphics g) {
	    	super.paintComponent(g);
	}

	/**
	 * Set directory to process
	 * @param url
	 */
	public void setLink(String url){
		this.linkurl.setText(url);
		System.out.println(url);
		System.out.println(linkurl.getText());
	}
	
	/**
	 * 
	 * @return
	 */
	public ImagePanel getImagepan() {
		return imagepan;
	}
	
	/**
	 * 
	 * @param imagepan
	 */
	public void setImagepan(ImagePanel imagepan) {
		this.imagepan = imagepan;
	}
	
	/**
	 * 
	 * @return
	 */
	public JProgressBar getIJProgressBar() {
		return IJprogressBar;
	}
	
	/**
	 * 
	 * @param progressBar_4
	 */
	public void setIJProgressBar(JProgressBar progressBar_4) {
		this.IJprogressBar = progressBar_4;
	}
	
	/**
	 * 
	 * @return
	 */
	public JButton getBtnLaunchfailed() {
		return btnLaunchfailed;
	}
	
	/**
	 * 
	 * @param btnLaunchfailed
	 */
	public void setBtnLaunchfailed(JButton btnLaunchfailed) {
		this.btnLaunchfailed = btnLaunchfailed;
	}
	
	/**
	 * 
	 * @return
	 */
	public JTextField getTextFieldpath() {
		return textFieldpath;
	}
	
	/**
	 * 
	 * @param textFieldpath
	 */
	public void setTextFieldpath(JTextField textFieldpath) {
		this.textFieldpath = textFieldpath;
	}
	
	/**
	 * 
	 * @return
	 */
	public JProgressBar getUploadProgressBar() {
		return uploadprogressBar;
	}
	
	/**
	 * 
	 * @param progressBar_6
	 */
	public void setUploadProgressBar(JProgressBar progressBar_6) {
		this.uploadprogressBar = progressBar_6;
	}

	/**
	 * 
	 * @return
	 */
	public JLabel getLblHost() {
		return lblHost;
	}
	
	/**
	 * 
	 * @param lblHost
	 */
	public void setLblHost(JLabel lblHost) {
		this.lblHost = lblHost;
	}

	/**
	 * Remove one item from waiting list
	 * @param text
	 */
	public void removeItem(String text) {
		if(text!=null){
			cancelled.add(text);
			dl.removeFromWaitingList(text);
			tarlist.removeFromWaitingList(text);
			ClassManagement.LogsPanel.addText(text+" removed.");
		}
	}
	
	/**
	 * Select folder
	 */
	public void selectFolder(){
		chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new java.io.File(textFieldpath.getText()));
	    chooser.setDialogTitle("Select a directory");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);  
	    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
	      System.out.println("getCurrentDirectory(): " 
	         +  chooser.getCurrentDirectory());
	      System.out.println("getSelectedFile() : " 
	         +  chooser.getSelectedFile());
	      }
	    else {
	      System.out.println("No Selection ");
	      }
	}
	
	/**
	 * Remove all items from waiting list
	 */
	public void removeAllItem() {
		dl.removeAllWaitingList();
	}
	
	/**
	 * 
	 */
	public void repaintClusterList() {
		selectClust.repaint();
	}
	
	/**
	 * 
	 * @param jl
	 */
	public void setSelectCluster(JListFrame jl) {
		selectClust=jl;
	}
	
	/**
	 * 
	 * @return
	 */
	public JListFrame getSelectCluster() {
		return selectClust;
	}
	
	/**
	 * 
	 * @return
	 */
	public JFileChooser getChooser() {
		return chooser;
	}
	
	/**
	 * 
	 * @return
	 */
	public JProgressBar getIJProgress() {
		return IJprogressBar;
	}
	
	/**
	 * 
	 * @param v
	 */
	public void setIJProgress(int v) {
		IJprogressBar.setValue(v);
	}
	
	/**
	 * 
	 * @return
	 */
	public JButton getBtnRefreshjob() {
		return btnRefreshjob;
	}
	
	/**
	 * 
	 * @param btnRefreshjob
	 */
	public void setBtnRefreshjob(JButton btnRefreshjob) {
		this.btnRefreshjob = btnRefreshjob;
	}
	
	/**
	 * 
	 * @return
	 */
	public JButton getBtnDownload() {
		return btnDownload;
	}
	
	/**
	 * 
	 * @param btnDownload
	 */
	public void setBtnDownload(JButton btnDownload) {
		this.btnDownload = btnDownload;
	}
	
	/**
	 * 
	 * @return
	 */
	public JButton getBtnCanceljob() {
		return btnCanceljob;
	}
	
	/**
	 * 
	 * @param btnCanceljob
	 */
	public void setBtnCanceljob(JButton btnCanceljob) {
		this.btnCanceljob = btnCanceljob;
	}
	
	/**
	 * MAIN
	 * @param args
	 */
	public static void main(String[] args){
		/*Thread loading=new Thread(){
			public void run(){
				
			}
		};*/
		MainViewer mv=new MainViewer();
		JFrame frame = new JFrame("Shark Factory");
	    int width = 960;
	    int height = 650; 
	    frame.getContentPane().add(mv,BorderLayout.CENTER);

	    frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
	    frame.setJMenuBar(mv.getMainMenu());
	    frame.pack();
	    frame.setSize(width, height);
	    frame.show();
	    java.net.URL url = ClassLoader.getSystemResource("images/icon.jpg");
	    Toolkit kit = Toolkit.getDefaultToolkit();
	    Image img = kit.createImage(url);
        Dimension dim = kit.getScreenSize();
        frame.setLocation(dim.width/2-width/2, dim.height/2-height/2);
	    frame.setIconImage(img);
	}










}
