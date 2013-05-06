package Main.display;

import javax.swing.JFrame;
import javax.swing.JPanel;

import modeles.Cluster;
import Main.ClassManagement;
import Main.display.style.JTBorderFactory;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 * Class which display list of avaible clusters
 * @author Jérémy DEVERDUN
 *
 */
public class ClusterSelect extends JPanel{
	private JFrame frame;
	private Cluster cl;
	private JTextField txtName;
	private JTextField txtHost;
	private JTextField txtUser;
	private JTextField txtDir;
	private JRadioButton rdbtnDefault;
	private JPanel panel_1;
	private boolean lock;
	
	/**
	 * 
	 * @param l
	 */
	public ClusterSelect(Cluster l){
		this.lock=true;
		this.setCl(l);
	}
	
	/**
	 * Set cluster
	 * @param cl
	 */
	public void setCl(final Cluster cl) {
		this.cl = cl;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		panel_1 = new JPanel();
		panel_1.setBorder(JTBorderFactory.createTitleBorder("Cluster #"+cl.getID()));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		add(panel_1, gbc_panel_1);
		panel_1.setLayout(new MigLayout("", "[][71.00][grow][][][][][][][][][][][][]", "[22.00][21.00][24.00][]"));
		ImageIcon icon=new ImageIcon(TextEditor.class.getResource("/images/edit.png"));
		Image img = icon.getImage();  
		Image newimg = img.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH);  
		icon = new ImageIcon(newimg); 
		
		JLabel lblName = new JLabel("Name :");
		panel_1.add(lblName, "cell 1 0,alignx trailing");
		
		txtName = new JTextField();
		txtName.setEditable(false);
		txtName.setText(cl.getName());
		panel_1.add(txtName, "cell 2 0,growx");
		txtName.setColumns(10);
		
		rdbtnDefault = new JRadioButton("Default");
		rdbtnDefault.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	ClassManagement.MainWindow.getSelectCluster().setTMPdefaultCluster(cl);
            	ClassManagement.MainWindow.repaintClusterList();
            	
            }
        }); 
		panel_1.add(rdbtnDefault, "cell 13 0");
		final JButton btnEdit = new JButton(icon);
		btnEdit.addActionListener(new ActionListener() {
			 
	            public void actionPerformed(ActionEvent e)
	            {
	            	if(lock){
		            	ImageIcon icon=new ImageIcon(TextEditor.class.getResource("/images/ok.png"));
		        		Image img = icon.getImage();  
		        		Image newimg = img.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH);  
		        		icon = new ImageIcon(newimg); 
		            	btnEdit.setIcon(icon);
		            	txtName.setEditable(true);
	            		txtHost.setEditable(true);
	            		txtUser.setEditable(true);
	            		txtDir.setEditable(true);
		            	lock=false;
	            	}else{
	            		ImageIcon icon=new ImageIcon(TextEditor.class.getResource("/images/edit.png"));
		        		Image img = icon.getImage();  
		        		Image newimg = img.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH);  
		        		icon = new ImageIcon(newimg); 
		            	btnEdit.setIcon(icon);
	            		txtName.setEditable(false);
	            		txtHost.setEditable(false);
	            		txtUser.setEditable(false);
	            		txtDir.setEditable(false);
	            		cl.setName(txtName.getText());
	            		cl.setIp(txtHost.getText());
	            		cl.setUser(txtUser.getText());
	            		cl.setRemoteDirectory(txtDir.getText());
	            		
	            		lock=true;
	            	}
	            }
	        }); 
		
		panel_1.add(btnEdit, "cell 14 0");
		
		JLabel lblHostname = new JLabel("Hostname : ");
		panel_1.add(lblHostname, "cell 1 1,alignx trailing");
		
		txtHost = new JTextField();
		txtHost.setEditable(false);
		txtHost.setText(cl.getIp());
		panel_1.add(txtHost, "cell 2 1,growx");
		txtHost.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username : ");
		panel_1.add(lblUsername, "cell 1 2,alignx trailing");
		
		txtUser = new JTextField();
		txtUser.setEditable(false);
		txtUser.setText(cl.getUser());
		panel_1.add(txtUser, "cell 2 2,growx");
		txtUser.setColumns(10);
		
		JLabel lblRemoteDirectory = new JLabel("Remote directory : ");
		panel_1.add(lblRemoteDirectory, "cell 1 3,alignx trailing");
		
		txtDir = new JTextField();
		txtDir.setEditable(false);
		txtDir.setText(cl.getRemoteDirectory());
		panel_1.add(txtDir, "cell 2 3,growx");
		txtDir.setColumns(10);
		
	}
	
	/**
	 * 
	 * @return
	 */
	public Cluster getCl() {
		return cl;
	}
	
	/**
	 * Display GUI
	 */
	public void createAndShowGUI(){
		JFrame frame=new JFrame("Cluster selection");
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(400,400);
		frame.show();
	}
	
	/**
	 * 
	 */
	public void setDefault(){
		rdbtnDefault.setSelected(true);
		panel_1.setBorder(JTBorderFactory.createTitleBorder("Cluster #"+cl.getID()+" - Default"));
	}
	
	/**
	 * Reset view to default
	 */
	public void reset() {
		rdbtnDefault.setSelected(false);
		panel_1.setBorder(JTBorderFactory.createTitleBorder("Cluster #"+cl.getID()));
	}
}
