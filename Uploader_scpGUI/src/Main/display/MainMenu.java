package Main.display;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import modeles.MatlabFunction;

import com.java.MatlabClient;
import com.java.modeles.Worker;
import com.jgoodies.forms.layout.FormLayout;

import IO.ExtensionFileFilter;
import IO.FileParser;
import IO.HTMLparser;
import IO.TarParser;
import Main.ClassManagement;
import Threads.ExecuteCommand;


/**
 * Main menu
 * @author Jérémy DEVERDUN
 *
 */
public class MainMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public MainMenu() {
		super();
		initializeMenu();
	}
	
	/**
	 * Initialize menu
	 */
	public void initializeMenu() {

		JMenu menu_file = new JMenu("File");
		JMenuItem menu_file_open = new JMenuItem("Get links from File");
		menu_file_open.setVisible(false);
		JMenuItem menu_file_exit = new JMenuItem("Quit");
		menu_file_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ClassManagement.ssh.disconnect();
				System.exit(0);
			}
		});

		
		JMenu mnFolderToProcess = new JMenu("Data");
		JMenuItem menu_links_remove = new JMenuItem("Remove item");
		menu_links_remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String response = JOptionPane.showInputDialog(null,
						"Accession number of Item (see the left of waiting list)",
						"Remove Item from download list",
						  JOptionPane.QUESTION_MESSAGE);
				ClassManagement.MainWindow.removeItem(response);
				
			}
		});
		menu_file.add(menu_file_open);
		JMenuItem mntmOpen = new JMenuItem("Open session");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				FileParser.loadSession();
			}
		});
		menu_file.add(mntmOpen);
		JMenuItem mntmSave = new JMenuItem("Save session");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser jf=new JFileChooser();
				jf.setCurrentDirectory(new java.io.File("."));
				jf.setDialogTitle("Save as");
				jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				jf.setAcceptAllFileFilterUsed(false);
			    if (jf.showOpenDialog(ClassManagement.MainWindow) == JFileChooser.APPROVE_OPTION) { 
			      System.out.println("getCurrentDirectory(): " 
			         +  jf.getCurrentDirectory());
			      System.out.println("getSelectedFile() : " 
			         +  jf.getSelectedFile());
			      File f=jf.getSelectedFile();
			      System.out.println(f.getAbsolutePath());
			      if(jf.getSelectedFile().getName().split("\\.session").length>0) f=new File(f.getAbsolutePath().split("\\.session")[0]);
			      System.out.println(f.getAbsolutePath());
			      ClassManagement.Session.saveAs(f);
			      ClassManagement.MainWindow.getFiletree().changeTree(new File("."));
			    }else {
			      System.out.println("No Selection ");
			      }
			}
		});
		menu_file.add(mntmSave);
		JMenuItem mntmOptions = new JMenuItem("Options");
		mntmOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				SetupWindow sw=new SetupWindow(1);
				sw.createAndShowGUI();
			}
		});
		menu_file.add(mntmOptions);
		
		menu_file.add(menu_file_exit);
		add(menu_file);
		JMenuItem menu_links_removeALL = new JMenuItem("Remove All");
		menu_links_removeALL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ClassManagement.MainWindow.removeAllItem();
				
			}
		});

		JMenuItem menu_link_VOMS = new JMenuItem("Launch proxy");
		menu_link_VOMS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ClassManagement.ssh.shell("voms-proxy-destroy");
				ClassManagement.ssh.shell("echo \""+ClassManagement.pass+"\" > "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"init.tmp");
				String res=ClassManagement.ssh.shell("voms-proxy-init -pwstdin -valid 36:00 -voms "+ClassManagement.vo+" < "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"init.tmp");
				ClassManagement.ssh.shell("rm "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"init.tmp");
			}
		});
		if(!ClassManagement.isLPTA) menu_link_VOMS.setVisible(false);
		JMenuItem menu_link_folder = new JMenuItem("Folder to process");
		menu_link_folder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ClassManagement.MainWindow.selectFolder();
				File file=ClassManagement.MainWindow.getChooser().getSelectedFile();
				ClassManagement.MainWindow.traitement(file,false);
			}
		});
		mnFolderToProcess.add(menu_link_folder);
		mnFolderToProcess.add(menu_link_VOMS);
		mnFolderToProcess.add(menu_links_remove);
		mnFolderToProcess.add(menu_links_removeALL);
		add(mnFolderToProcess);
		JMenu menu_SE = new JMenu("Storage");
		JMenuItem menu_SE_removeALL = new JMenuItem("Remove all lfn from this session");
		menu_SE_removeALL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				FileParser.deleteAllLfn();
			}
		});
		if(!ClassManagement.isLPTA) menu_SE.setVisible(false);
		JMenuItem menu_SE_archive = new JMenuItem("Generate data archives");
		menu_SE_archive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				TarParser.tarByJobCluster();
			}
		});
		JMenuItem menu_SE_send = new JMenuItem("Send all to SE");
		menu_SE_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				FileParser.send2SE();
			}
		});
		menu_SE.add(menu_SE_archive);
		menu_SE.add(menu_SE_send);
		menu_SE.add(menu_SE_removeALL);
		add(menu_SE);
		
		JMenu menu_Jobs = new JMenu("Jobs");
		JMenuItem menu_Jobs_nbofjob = new JMenuItem("Set number of jobs");
		menu_Jobs_nbofjob.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String response = JOptionPane.showInputDialog(null,
						"Number of jobs",
						"Set",
						  JOptionPane.QUESTION_MESSAGE);
				try{
					int val=Integer.parseInt(response);
					if (val>0 && val<1000)
						ClassManagement.nbofjobs=val;
					ClassManagement.LogsPanel.addText("Number of jobs set to "+val);
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		});
		JMenuItem menu_Jobs_makeJDL = new JMenuItem("Generate job script");
		menu_Jobs_makeJDL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				final LoadingFrame lf=new LoadingFrame("Generating jobs scripts.",true);
				Thread monThread=new Thread(){
					public void run(){
						FileParser.makeJdl();
						lf.dispose();
					}
				};
				monThread.start();
			}
		});
		
		JMenuItem menu_Jobs_launch_failed= new JMenuItem("Launch failed jobs");
		menu_Jobs_launch_failed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ClassManagement.isHPCLR) FileParser.launchFailed();
				else if(ClassManagement.isLPTA) FileParser.findPurgedJobs();
			}
		});
	
		
		menu_Jobs.add(menu_Jobs_nbofjob);
		
		JMenu mnOnejob = new JMenu("One job");
		menu_Jobs.add(mnOnejob);
		
		JMenuItem menu_Jobs_submitOne = new JMenuItem("Submit");
		mnOnejob.add(menu_Jobs_submitOne);
		JMenuItem menu_Jobs_retrieveOneoutput = new JMenuItem("Retrieve results");
		mnOnejob.add(menu_Jobs_retrieveOneoutput);
		JMenuItem menu_Jobs_status= new JMenuItem("Status");
		mnOnejob.add(menu_Jobs_status);
		
		JMenuItem menu_Jobs_stopOne = new JMenuItem("Cancel");
		mnOnejob.add(menu_Jobs_stopOne);
		menu_Jobs_stopOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				SelectBox_Job.createAndShowGUI(FileParser.getRunningJobs(),1);
			}
		});
		menu_Jobs_status.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				SelectBox_Job.createAndShowGUI(FileParser.getRunningJobs(),3);
			}
		});
		menu_Jobs_retrieveOneoutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				SelectBox_Job.createAndShowGUI(FileParser.getRunningJobs(),4);
			}
		});
		menu_Jobs_submitOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				SelectBox_Job.createAndShowGUI(FileParser.getPossibleJobs(),2);
			}
		});
		
		JMenu mnAllJobs = new JMenu("All jobs");
		menu_Jobs.add(mnAllJobs);
		JMenuItem menu_Jobs_submitAll = new JMenuItem("Submit");
		mnAllJobs.add(menu_Jobs_submitAll);
		JMenuItem menu_Jobs_retrieveAlloutput = new JMenuItem("Retrieve results");
		mnAllJobs.add(menu_Jobs_retrieveAlloutput);
		
		JMenuItem menu_Jobs_allstatus= new JMenuItem("Status");
		mnAllJobs.add(menu_Jobs_allstatus);
		
		
		JMenuItem menu_Jobs_stopAll = new JMenuItem("Cancel");
		mnAllJobs.add(menu_Jobs_stopAll);
		menu_Jobs_stopAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Thread monThread=new Thread(){
					public void run(){
						FileParser.cancelAllJobs();
						ClassManagement.Session.clearAllID();
					}
				};
				monThread.start();
				
			}
		});
		menu_Jobs_allstatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ClassManagement.isLPTA) 
					HTMLparser.generateStatusPage();
				else
					FileParser.getAllStatus();
			}
		});
		menu_Jobs_retrieveAlloutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				FileParser.retrieveAllOutput();
			}
		});
		menu_Jobs_submitAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Thread monThread=new Thread(){
					public void run(){
						FileParser.submitAllJobs();
					}
				};
				monThread.start();
				
			}
		});
		menu_Jobs.add(menu_Jobs_makeJDL);
		menu_Jobs.add(menu_Jobs_launch_failed);
		add(menu_Jobs);
		
	/*	JMenu mnPostProcessing = new JMenu("Post processing");
		add(mnPostProcessing);


		File[] listfile=(new File(ClassManagement.installDir+File.separator+"plugins")).listFiles();
		for(File f:listfile){
			if(f.getName().contains(".m")){
				String[] txt=FileParser.getTextFromFile(f.getAbsolutePath()).split("\n");
				if(txt[0].split("@main").length>1){
					System.out.println(f.getName());
					MatlabFunction mf=new MatlabFunction(f.getName().substring(0, f.getName().lastIndexOf(".")), f);
					mnPostProcessing.add(mf);
				}
			}
		}
		JMenuItem mntmCustomcmd = new JMenuItem("Custom");
		mntmCustomcmd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try{
					String response = JOptionPane.showInputDialog(null,
							"Enter your command",
							"Command",
							  JOptionPane.QUESTION_MESSAGE);
					MatlabClient mc=new MatlabClient();
					mc.sendSimpleCommand(new Worker(response,new String[]{""},new String[]{""}));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		JMenuItem mntmClosefigure = new JMenuItem("Close all");
		mntmClosefigure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try{
					MatlabClient mc=new MatlabClient();
					mc.sendSimpleCommand(new Worker("close all;drawnow",new String[]{""},new String[]{""}));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		JMenuItem mntmStopMatlabServer = new JMenuItem("Stop matlab server");
		mntmStopMatlabServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try{
					MatlabClient mc=new MatlabClient();
					mc.sendSimpleCommand(new Worker("exit",new String[]{""},new String[]{""}));
					ClassManagement.matlabserver.stopThread();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		mnPostProcessing.add(mntmCustomcmd);
		mnPostProcessing.add(mntmClosefigure);
		mnPostProcessing.add(mntmStopMatlabServer);*/
	}
}

