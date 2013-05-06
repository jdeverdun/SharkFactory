package Main.display;

import javax.swing.JPanel;

import modeles.MatlabFunction;
import net.miginfocom.swing.MigLayout;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import Main.ClassManagement;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Dialogs box shown when user click 
 * on MATLAB function (user prompt for arguments)
 * @author Jérémy DEVERDUN
 *
 */
public class MatlabFunctionCall extends JPanel{
	private String[][] args;
	private JTextField[] listtxt;
	private MatlabFunction mfun;
	private JFrame frame;
	
	/**
	 * 
	 * @param arguments
	 * @param matlabfun
	 */
	public MatlabFunctionCall(String[][] arguments,MatlabFunction matlabfun){
		if(arguments!=null){
			this.setArgs(arguments);
			listtxt=new JTextField[arguments.length];
			this.mfun=matlabfun;
			this.initButtons();
		}
	}
	
	/**
	 * Arguments selection for MATLAB functions
	 */
	private void initButtons() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{43, 22, 4, 153, 0, 0};
		gridBagLayout.rowHeights = new int[]{36, 20, 23, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		int tot=0;
		for(int i=0;i<this.getArgs().length;i++){
			if(!(this.getArgs()[i][1].equals("image"))){
				JLabel lblArg = new JLabel(this.getArgs()[i][0]);
				GridBagConstraints gbc_lblArg = new GridBagConstraints();
				gbc_lblArg.anchor = GridBagConstraints.WEST;
				gbc_lblArg.insets = new Insets(0, 0, 5, 5);
				gbc_lblArg.gridx = 1;
				gbc_lblArg.gridy = i+1;
				add(lblArg, gbc_lblArg);
				
				JLabel label = new JLabel(":");
				GridBagConstraints gbc_label = new GridBagConstraints();
				gbc_label.anchor = GridBagConstraints.WEST;
				gbc_label.insets = new Insets(0, 0, 5, 5);
				gbc_label.gridx = 2;
				gbc_label.gridy = i+1;
				add(label, gbc_label);
				final JTextField txtArgval = new JTextField();
				txtArgval.setText("");
				if((this.getArgs()[i][1].equals("select"))){
					final String[] petStrings =  this.getArgs()[i][0].split("\\/") ;
					final JComboBox petList = new JComboBox(petStrings);
					final String ty=this.getArgs()[i][1];
					txtArgval.setText(petStrings[0]);
					petList.addActionListener(new ActionListener() {
			        	public void actionPerformed(ActionEvent ae) {
			        		txtArgval.setText(petStrings[petList.getSelectedIndex()]);
			        	}
			        });
					
					GridBagConstraints gbc_fileval = new GridBagConstraints();
					gbc_fileval.anchor = GridBagConstraints.NORTH;
					gbc_fileval.fill = GridBagConstraints.HORIZONTAL;
					gbc_fileval.insets = new Insets(0, 0, 5, 0);
					gbc_fileval.gridx = 3;
					gbc_fileval.gridy = i+1;
					add(petList, gbc_fileval);
					txtArgval.setVisible(false);
				}
				GridBagConstraints gbc_txtArgval = new GridBagConstraints();
				gbc_txtArgval.anchor = GridBagConstraints.NORTH;
				gbc_txtArgval.fill = GridBagConstraints.HORIZONTAL;
				gbc_txtArgval.insets = new Insets(0, 0, 5, 0);
				gbc_txtArgval.gridx = 3;
				gbc_txtArgval.gridy = i+1;
				add(txtArgval, gbc_txtArgval);
				txtArgval.setColumns(10);
				listtxt[i]=txtArgval;
				if((this.getArgs()[i][1].equals("file")) || (this.getArgs()[i][1].equals("directory"))){
					JButton jbselectfile=new JButton("...");
					final String ty=this.getArgs()[i][1];
					if(this.getArgs()[i][0].equals("pathres")){
						txtArgval.setText(ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+File.separator+"results");
					}
					if(this.getArgs()[i][0].equals("pathim")){
						txtArgval.setText(ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+File.separator+"images");
					}
					jbselectfile.addActionListener(new ActionListener() {
			        	public void actionPerformed(ActionEvent ae) {
			        		selectfolder(ty,txtArgval);
			        	}
			        });
					
					GridBagConstraints gbc_fileval = new GridBagConstraints();
					gbc_fileval.anchor = GridBagConstraints.NORTH;
					gbc_fileval.fill = GridBagConstraints.HORIZONTAL;
					gbc_fileval.insets = new Insets(0, 0, 5, 0);
					gbc_fileval.gridx = 4;
					gbc_fileval.gridy = i+1;
					add(jbselectfile, gbc_fileval);
				}
			}
			tot=i;
		}
		JButton btnOk = new JButton("ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String res[][]=new String[getArgs().length][2];
				for(int i=0;i<listtxt.length;i++){
					if((getArgs()[i][1].equals("image"))){
						res[i][0]=getArgs()[i][0];
						res[i][1]=getArgs()[i][1];
					}else{
						res[i][0]=listtxt[i].getText();
						res[i][1]=getArgs()[i][1];
					}
				}
				mfun.setArgin(res);
				frame.dispose();
			}
		});
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnOk.insets = new Insets(0, 0, 0, 5);
		gbc_btnOk.gridx = 1;
		gbc_btnOk.gridy = tot+2;
		add(btnOk, gbc_btnOk);
		
		JButton btnCancel = new JButton("cancel");
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnCancel.gridx = 3;
		gbc_btnCancel.gridy = tot+2;
		add(btnCancel, gbc_btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				mfun.setIscancel(true);
				frame.dispose();
			}
		});
	}
	
	/**
	 * Display dialog box
	 */
	public void createAndShowGUI(){
		if(args!=null){
			frame=new JFrame("Arguments for "+mfun.getMfile().getName());
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
	
	/**
	 * Test whether dialog box is showing or not
	 */
	@Override
	public boolean isShowing(){
		if(args==null) return false;
		return frame.isShowing();
	}
	
	/**
	 * Select file or folder
	 * type = "file" or "folder"
	 * @param type
	 * @param jtextfield
	 */
	public void selectfolder(String type,JTextField jtextfield){
		JFileChooser filechoos = new JFileChooser();
		//filechoos.setCurrentDirectory(new java.io.File(textFieldpath.getText()));
		filechoos.setDialogTitle("Select a "+type);
		if(type.equals("file"))
			filechoos.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		else
			filechoos.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    //
		    // disable the "All files" option.
		    //
		filechoos.setAcceptAllFileFilterUsed(false);
		    //    
		    if (filechoos.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
		      System.out.println("getCurrentDirectory(): " 
		         +  filechoos.getCurrentDirectory());
		      System.out.println("getSelectedFile() : " 
		         +  filechoos.getSelectedFile());
		      jtextfield.setText(filechoos.getSelectedFile().getAbsolutePath());
		      }
		    else {
		      System.out.println("No Selection ");
		      }
	}
	
	/**
	 * 
	 * @param args
	 */
	public void setArgs(String[][] args) {
		this.args = args;
		
	}
	
	/**
	 * 
	 * @return
	 */
	public String[][] getArgs() {
		return args;
	}

}
