package Main.display;

import javax.swing.JEditorPane;


/**
 * Log panel
 * @author Jérémy DEVERDUN
 *
 */
public class JLogEditorPane extends JEditorPane{
	private String txt;
	
	/**
	 * 
	 */
	public JLogEditorPane(){
		super();
		txt="";
	}
	
	/**
	 * Add text to log
	 * @param t
	 */
	public void addText(String t) {
		txt=txt+"\n"+t;
		this.setText(txt);
	}

}
