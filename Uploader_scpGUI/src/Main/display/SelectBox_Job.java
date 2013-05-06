package Main.display;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

import IO.FileParser;
import Main.ClassManagement;


/**
 * Create select box used to select job to stop etc...
 * @author Jérémy DEVERDUN
 *
 */
public class SelectBox_Job extends JPanel
                          implements ActionListener {
    JButton jb;
    JButton jb2;
    String toWork;
    JFrame frame;
    int type;
    
    /**
     * liste = list of choice
     * t : type 
     * 		- 1 : cancel
     * 		- 2 : submit
     * 		- 3 : status
     * 		- 4 : retrieve output
     * f : parent frame
     * @param liste
     * @param t
     * @param f
     */
    public SelectBox_Job(String[] liste,int t,JFrame f) {
        super(new BorderLayout());
        frame=f;
        type=t;
        jb=new JButton("OK");
        jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(type==1){
					String[] elem=toWork.split(File.separator);
					String n=elem[elem.length-1].substring(0, elem[elem.length-1].indexOf("."));
					if(ClassManagement.isHPCLR) n=n.substring(n.indexOf("_"));
					FileParser.cancelOneJob(n);
				}else{
					if(type==2){
						String[] elem=toWork.split("/");
						String n=elem[elem.length-1].substring(0, elem[elem.length-1].indexOf("."));
						FileParser.submitOneJob(n);
					}else{
						if(type==3){
							String status=FileParser.getStatus(toWork);
							JOptionPane.showMessageDialog(null, "Status : "+status);
						}else{
							if(type==4){
								String[] elem=toWork.split("/");
								String n=elem[elem.length-1].substring(0, elem[elem.length-1].indexOf("."));
								FileParser.retrieveOneOutput(n);
							}
						}
					}
				}
				frame.dispose();
			}
		});
        jb2=new JButton("Cancel");
        jb2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				frame.dispose();
			}
		});
        String[] petStrings = liste;
        add(jb,BorderLayout.WEST);
        add(jb2,BorderLayout.EAST);

        JComboBox petList = new JComboBox(petStrings);
        petList.setSelectedIndex(0);
        toWork=petStrings[0];
        petList.addActionListener(this);
        add(petList, BorderLayout.PAGE_START);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }
    /** Listens to the combo box. */
    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        String petName = (String)cb.getSelectedItem();
        toWork=petName;
    }



    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI(String[] liste,int t) {
        //Create and set up the window.
        JFrame frame = new JFrame("Select job");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new SelectBox_Job(liste,t,frame);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}