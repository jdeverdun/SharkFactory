package Main.display;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.beans.*;
import java.util.Random;


/**
 * 
 * @author Jérémy DEVERDUN
 *
 */
public class ProgressBar_status extends JPanel
                             implements ActionListener, 
                                        PropertyChangeListener {

    private JProgressBar progressBar;
    private JButton startButton;
    private JTextArea taskOutput;
    private JPanel panel;

    
    /**
     * Clustom progressbar
     */
    public ProgressBar_status() {
        super(new BorderLayout());



        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        panel = new JPanel();
        panel.add(progressBar);

        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }
    
    /**
     * 
     * @param indeter
     */
    public ProgressBar_status(boolean indeter) {
        super(new BorderLayout());



        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(indeter);
        progressBar.setVisible(true);
        panel = new JPanel();
        panel.add(progressBar);

        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }

    /**
     * Invoked when task's progress property changes.
     */
    public void setStatus(int s) {
            progressBar.setValue(s);
    }
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

