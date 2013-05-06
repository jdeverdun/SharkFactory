package Main.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import Main.ClassManagement;
import Threads.ExecuteCommand;
import java.awt.Dimension;


/**
 * Terminal panel, will allow the user to
 * communicate with the cluster
 * @author Jérémy DEVERDUN
 *
 */
public class Terminal extends JPanel{
	private Graphics g;
	private JTextArea logs;
	private String texte;
	private JTextField command;
	
	/**
	 * Initialize termine with specified dimension
	 * @param width
	 * @param height
	 */
	public Terminal(int width, int height){
		setMinimumSize(new Dimension(200, 400));
		texte="";
		ClassManagement.Terminal=this;
		setSize(getPreferredSize());
    	//this.setBackground(Color.black);
		//this.setLayout(new BorderLayout());
    	logs=new JTextArea(25, 24);
    	logs.setLineWrap(true);
    	logs.setEditable(false);
    	logs.setAutoscrolls(true);
    	command=new JTextField(24);
    	command.setEditable(true);
    	command.addKeyListener(new KeyAdapter() {
    		public void keyPressed(KeyEvent k) {
    			if(k.getKeyChar() == KeyEvent.VK_ENTER) {
    				addText(">>"+command.getText());
    				ClassManagement.Terminal.addText(ClassManagement.ssh.shell(command.getText()));
    				command.setText("");
    			}
    		}
    	});
    	JScrollPane scrollPane = new JScrollPane(logs);
    	scrollPane.setMinimumSize(new Dimension(200, 400));
    	this.add(scrollPane,BorderLayout.CENTER);
    	this.add(command,BorderLayout.SOUTH);
	}
	
	/**
	 * 
	 */
	public void paintComponent(Graphics g) {
    	super.paintComponent(g);
	}
	
	/**
	 * Add text to terminal
	 * @param t
	 */
	public void addText(String t){
		logs.append("\n"+t);
	}
}
