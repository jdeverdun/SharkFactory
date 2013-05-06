package Main.display;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.JFrame;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import modeles.Cluster;
import modeles.ClusterList;

import IO.FileParser;
import Main.ClassManagement;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import net.miginfocom.swing.MigLayout;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JSlider;
import javax.swing.SpinnerNumberModel;

/**
 * Options panel, let the user choose the best options
 * with his system
 * @author Jérémy DEVERDUN
 *
 */
public class SetupWindow extends JPanel {
	private JPanel qpanel;
	private JPanel clusterpan;
	private JFrame frame;
	private JTabbedPane tabbedPane;
	private GridBagConstraints c ;
	private JTextField tfnom;
	private JTextField textFieldVO;
	private JTextField textFieldJobName;
	private JTextField textFieldlfn;
	private JTextField textFieldSRM;
	private JTextField textFieldguid;
	
	/**
	 * Note : argument "s" became useless
	 * @param s
	 */
    public SetupWindow(int s) {
        super(new GridLayout(1,0));
    	this.setPreferredSize( new Dimension(300,300) ) ;
        c = new GridBagConstraints();

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.white);

        qpanel=new JPanel();
        qpanel.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][][]"));
        
        JLabel lblClusterType = new JLabel("Cluster type : ");
        qpanel.add(lblClusterType, "cell 0 0,alignx trailing");
        
        final JComboBox comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel(new String[] {"JDL (LPTA)", "CMD (HPC@LR)"}));
        comboBox.setSelectedIndex(1);
        comboBox.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		if(comboBox.getSelectedIndex()==1){
        			textFieldVO.setEnabled(false);
        			textFieldSRM.setEnabled(false);
        			textFieldguid.setEnabled(false);
        			textFieldlfn.setEnabled(false);
        		}else{
        			if(comboBox.getSelectedIndex()==0){
        				textFieldVO.setEnabled(true);
            			textFieldSRM.setEnabled(true);
            			textFieldguid.setEnabled(true);
            			textFieldlfn.setEnabled(true);
        			}
        		}
        	}
        });
        qpanel.add(comboBox, "cell 1 0,growx");
        
        JLabel lblVirtualOrganisation = new JLabel("Virtual Organisation : ");
        qpanel.add(lblVirtualOrganisation, "cell 0 1,alignx trailing");
        
        textFieldVO = new JTextField(ClassManagement.vo);
        qpanel.add(textFieldVO, "cell 1 1,growx");
        textFieldVO.setColumns(10);
        
        JLabel lblJobPrefixe = new JLabel("Jobs names : ");
        qpanel.add(lblJobPrefixe, "cell 0 2,alignx trailing");
        
        textFieldJobName = new JTextField(ClassManagement.jdlname);
        qpanel.add(textFieldJobName, "cell 1 2,growx");
        textFieldJobName.setColumns(10);
        
        JLabel lblLfnFileName = new JLabel("LFN file name : ");
        qpanel.add(lblLfnFileName, "cell 0 3,alignx trailing");
        
        textFieldlfn = new JTextField(ClassManagement.lfn_file_list);
        qpanel.add(textFieldlfn, "cell 1 3,growx");
        textFieldlfn.setColumns(10);
        
        JLabel lblSrmFileName = new JLabel("SRM file name : ");
        qpanel.add(lblSrmFileName, "cell 0 4,alignx trailing");
        
        textFieldSRM = new JTextField(ClassManagement.srm_url);
        qpanel.add(textFieldSRM, "cell 1 4,growx");
        textFieldSRM.setColumns(10);
        
        JLabel lblGuidFileName = new JLabel("GUID file name : ");
        qpanel.add(lblGuidFileName, "cell 0 5,alignx trailing");
        
        textFieldguid = new JTextField(ClassManagement.guid_file_list);
        qpanel.add(textFieldguid, "cell 1 5,growx");
        textFieldguid.setColumns(10);
        
        JLabel lblNumberOfJobs = new JLabel("Memory to allocate : ");
        qpanel.add(lblNumberOfJobs, "cell 0 6,alignx right");
        
        final JSpinner spinner = new JSpinner();
        spinner.setModel(new SpinnerNumberModel(new Integer(ClassManagement.MAXMEMORY), null, null, new Integer(128)));
        spinner.setPreferredSize(new Dimension(40, 20));
        qpanel.add(spinner, "flowx,cell 1 6");
        
        JLabel lblMinAutonumber = new JLabel("(def = 4096 [Mo])");
        qpanel.add(lblMinAutonumber, "cell 1 6");
        
        JLabel lblMaxThreads = new JLabel("Max Threads : ");
        qpanel.add(lblMaxThreads, "cell 0 7,alignx right");
        
        final JSpinner spinner_1 = new JSpinner();
        spinner_1.setMinimumSize(new Dimension(40, 20));
        spinner_1.setValue(ClassManagement.IJmaxThreads);
        qpanel.add(spinner_1, "flowx,cell 1 7");
        
        JButton btnSave = new JButton("Save");
        qpanel.add(btnSave, "cell 0 8,alignx right");
        btnSave.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		if(ClassManagement.clusterlist.getdefaultCluster()==null){
        			JOptionPane.showMessageDialog(frame, "Please add a cluster first (\"Cluster\" tab).");
        		}else{
        			ClassManagement.clusterlist.save(ClassManagement.installDir+"/clusters.conf");
	        		String conf="";
	        		if(comboBox.getSelectedIndex()==1){
	        			ClassManagement.isHPCLR=true;
	        			ClassManagement.isLPTA=false;
	        			ClassManagement.type_cluster="CMD";
	        		}else{
	        			if(comboBox.getSelectedIndex()==0){
	        				ClassManagement.isHPCLR=false;
	            			ClassManagement.isLPTA=true;
	            			ClassManagement.type_cluster="JDL";
	        			}
	        		}
	        		ClassManagement.guid_file_list=textFieldguid.getText();
	        		ClassManagement.lfn_file_list=textFieldlfn.getText();
	        		ClassManagement.srm_url=textFieldSRM.getText();
	        		ClassManagement.MAXMEMORY=(Integer)spinner.getValue();
	        		ClassManagement.jdlname=textFieldJobName.getText();
	        		ClassManagement.vo=textFieldVO.getText();
	        		ClassManagement.IJmaxThreads=(Integer)spinner_1.getValue();
	        		ClassManagement.saveConf();
	        		frame.dispose();
        		}
        	}
        });
        JButton btnCancel = new JButton("Close");
        btnCancel.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		frame.dispose();
        	}
        });
        qpanel.add(btnCancel, "cell 1 8,alignx left");
        clusterpan=new JPanel();
        textFieldVO.setEnabled(false);
		textFieldSRM.setEnabled(false);
		textFieldguid.setEnabled(false);
		textFieldlfn.setEnabled(false);
		Runtime runtime = Runtime.getRuntime();
        
        int nrOfProcessors = runtime.availableProcessors();
		JLabel lblrec = new JLabel("(Rec. "+nrOfProcessors+")");
		qpanel.add(lblrec, "cell 1 7");
		
		
        
        initializeClusterPan(s);
        
    }

    /**
     * s = selected panel
     * @param s
     */
	private void initializeClusterPan(int s) {
		clusterpan.setLayout(new MigLayout("", "[30.00][83px][126px]", "[20px][20px][20px][20px][23px]"));
		
		JLabel lblName = new JLabel("Name");
		clusterpan.add(lblName, "cell 1 0,alignx center,aligny center");
		
		tfnom = new JTextField();
		clusterpan.add(tfnom, "cell 2 0,alignx center,aligny center");
		tfnom.setColumns(15);
		JLabel lab1=new JLabel("Hostname");
		clusterpan.add(lab1, "cell 1 1,alignx center,aligny center");
		final JTextField jt1=new JTextField();
		jt1.setColumns(15);
		clusterpan.add(jt1, "cell 2 1,alignx center,aligny center");
		tabbedPane.addTab("Global", null, qpanel, null);
        tabbedPane.addTab("Cluster", null, clusterpan, BorderLayout.CENTER);
        JLabel lab2=new JLabel("User");
        clusterpan.add(lab2, "cell 1 2,alignx center,aligny center");
        final JTextField jt2=new JTextField();
        jt2.setColumns(15);
        clusterpan.add(jt2, "cell 2 2,alignx center,aligny center");
        JLabel lab3=new JLabel("Remote directory");
        clusterpan.add(lab3, "cell 1 3,alignx center,aligny center");
        final JTextField jt3=new JTextField();
        jt3.setColumns(15);
        clusterpan.add(jt3, "cell 2 3,alignx center,aligny center");
        JButton jb2=new JButton("Close");
        jb2.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		frame.dispose();
        	}
        });
        JButton jb1=new JButton("Add to list");
        jb1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		if(!jt1.getText().equals("") && !jt2.getText().equals("") && !jt3.getText().equals("")){
        			if(ClassManagement.clusterlist==null) ClassManagement.clusterlist=new ClusterList<Cluster>();	
        			Cluster clust=new Cluster(ClassManagement.clusterlist.size()+1,tfnom.getText(),jt1.getText(),jt2.getText(),jt3.getText());
        			ClassManagement.clusterlist.add(clust);	
        			ClassManagement.clusterlist.setdefaultCluster(clust);
	        		tfnom.setText("");
	        		jt1.setText("");
	        		jt2.setText("");
	        		jt3.setText("");
        		}else{
        			JOptionPane.showMessageDialog(frame, "You must fill all field.");
        		}
        	}
        });
        clusterpan.add(jb1, "cell 1 4,alignx right,aligny center");
        
        JButton btnEditList = new JButton("Select cluster");
        btnEditList.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		JListFrame jl=new JListFrame("Cluster Selection");
        		ClassManagement.MainWindow.setSelectCluster(jl);
        		jl.setVisible(true);
        	}
        });
        clusterpan.add(btnEditList, "flowx,cell 2 4");
        clusterpan.add(jb2, "cell 2 4,alignx center,aligny center");
        tabbedPane.setSelectedIndex(s-1);
       

        add(tabbedPane);
	}



    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the 
     * event-dispatching thread.
     */
    public void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("Setup");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400,400);
        frame.getContentPane().setLayout(new BorderLayout());
        //Create and set up the content pane.
        this.setOpaque(true); //content panes must be opaque
        frame.setContentPane(this);
        Toolkit kit = Toolkit.getDefaultToolkit();
    	Dimension dim = kit.getScreenSize();
    	frame.setLocation(dim.width/2-400/2, dim.height/2-400/2);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
