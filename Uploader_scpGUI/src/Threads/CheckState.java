package Threads;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JComponent;
import javax.swing.JFrame;

import Main.ClassManagement;
import Main.display.ProgressBar_status;
import javax.swing.Timer;;

/**
 * Became useless
 * 
 * @author J�r�my DEVERDUN
 *
 */
public class CheckState extends Thread{
  private Timer timer;
  private JFrame frame;
  private JComponent newContentPane;
  
  /**
   * 
   */
  public void run() {
	  frame = new JFrame("Tar process");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      //Create and set up the content pane.
      newContentPane = new ProgressBar_status();
      newContentPane.setOpaque(true); //content panes must be opaque
      frame.setContentPane(newContentPane);
      newContentPane.setVisible(true);
      //Display the window.
      frame.pack();
      frame.setVisible(true);
      Timer timer = createTimer(2000);
  }	
  
  /**
   * 
   * @param t
   * @return
   */
  private Timer createTimer (int t)
  {
    // Cr�ation d'une instance de listener 
    // associ�e au timer
      ActionListener speedEstimate = new ActionListener ()
      {
        // M�thode appel�e � chaque tic du timer
        public void actionPerformed (ActionEvent event)
        {
        	if(!ClassManagement.archiving) 	cancel();
        }
      };
      return new Timer (t, speedEstimate);
	}  
  private void cancel(){
	  timer.stop();
	  frame.dispose();
	  newContentPane=null;
	  this.stop();
  }
}