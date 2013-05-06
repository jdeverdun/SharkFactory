package Main.display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Main.ClassManagement;
import Main.display.style.JTBorderFactory;
import Threads.ExecuteCommand;

import modeles.Cluster;


/**
 * Select default cluster etc.
 * @author Jérémy DEVERDUN
 *
 */
public class JListFrame extends JFrame {
	private JPanel jp;
	private JScrollPane jscroll;
	private ArrayList<ClusterSelect> list;
	private Cluster defaultclust;
	
	/**
	 * 
	 * @param s
	 */
	public JListFrame(String s){
		super(s);
		defaultclust=ClassManagement.clusterlist.getdefaultCluster();
		list=new ArrayList<ClusterSelect>();
		initializeButtons();
		
	}
	
	/**
	 * 
	 */
	public void initializeButtons(){
		jp=new JPanel(new GridBagLayout());
		GridBagConstraints cb = new GridBagConstraints();
		
		cb.gridx=0;
		cb.gridy=0;
		JButton jbsave=new JButton("Save");
		int count=1;
		
		jp.add(jbsave,cb);
		jbsave.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	ClassManagement.clusterlist.save(ClassManagement.installDir+"/clusters.conf");
            	ClassManagement.clusterlist.setdefaultCluster(defaultclust);
            	ClassManagement.Session.setCluster(defaultclust);
            	if(!ClassManagement.ssh.getClust().equals(defaultclust)){
            		ClassManagement.ssh.disconnect();
            		ClassManagement.ssh=new ExecuteCommand();
            		ClassManagement.saveConf();
            		ClassManagement.LogsPanel.addText("Connexion to "+defaultclust.getName()+" ... Success.");
            		ClassManagement.MainWindow.getLblHost().setText(ClassManagement.clusterlist.getdefaultCluster().getName());
            	}
            	close();
            }
        }); 
		
		Cluster cldef=defaultclust;
		for(Cluster c:ClassManagement.clusterlist){
			cb.gridx=0;
			cb.gridy=count++;
			
			ClusterSelect cl=new ClusterSelect(c);
			list.add(cl);
			if(c.equals(cldef)) cl.setDefault();
			
			jp.add(cl,cb);
		}
		
		jscroll=new JScrollPane(jp);
		this.add(jscroll);
		this.setSize(460,400);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension dim = kit.getScreenSize();
		this.setLocation(dim.width/2-450/2, dim.height/2-400/2);
	}
	
	/**
	 * 
	 */
	protected void close() {
		this.dispose();
	}
	
	/**
	 * 
	 */
	public void repaint(){
		patch_cluster_list();
	}
	
	/**
	 * 
	 */
	private void patch_cluster_list() {
		Cluster cldef=defaultclust;
		for(ClusterSelect cs:list){
			cs.reset();
			if(cs.getCl().equals(cldef)) cs.setDefault();
			
		}
	}
	
	/**
	 * 
	 * @param cl
	 */
	public void setTMPdefaultCluster(Cluster cl) {
		defaultclust=cl;
	}
	
	
}
