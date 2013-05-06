package Main.display;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;


/**
 * Basic loading frame
 * @author Jérémy DEVERDUN
 *
 */
public class LoadingFrame extends JFrame{
	private ProgressBar_status newContentPane;
	
	/**
	 * Create basic loading frame
	 * @param title
	 * @param indeter
	 */
	public LoadingFrame(String title,boolean indeter){
		super(title);
	    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
	    //Create and set up the content pane.
	    ProgressBar_status newContentPane = new ProgressBar_status(indeter);
	    newContentPane.setOpaque(true); //content panes must be opaque
	    this.setContentPane(newContentPane);
	    newContentPane.setVisible(true);
	    //Display the window.
	    Toolkit toolkit =  Toolkit.getDefaultToolkit ();
	    Dimension dim = toolkit.getScreenSize();
	    int width=200;
	    int height=40;
	    this.setSize(width, height);
	    this.setLocation(dim.width/2-width/2, dim.height/2-height/2);
	    this.pack();
	    this.setVisible(true);
	}
	public void setStatus(int s){
		newContentPane.setStatus(s);
	}
}
