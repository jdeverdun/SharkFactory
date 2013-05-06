package Main.display;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import IO.FileParser;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;

import jsyntaxpane.DefaultSyntaxKit;


/**
 * File viewer, to edit text file
 * @author Jérémy DEVERDUN
 *
 */
public class TextEditor extends JPanel{
	private String texte;
	private String title;
	private File file;
	private JEditorPane editorPane;
	private JScrollPane scrPane;
	
	/**
	 * f=file to read
	 * @param f
	 */
	public TextEditor(File f){
		file=f;
		this.setTitle(f.getName());
		this.setTexte(FileParser.getTextFromFile((f.getPath())));
	}
	
	/**
	 * 
	 */
	public void paintComponent(Graphics g) {
    	super.paintComponent(g);
	}
	
	/**
	 * 
	 * @param texte
	 */
	public void setTexte(String texte) {
		this.texte = texte;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTexte() {
		return texte;
	}
	
	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Display GUI
	 */
	public void createAndShowGUI(){
		JFrame jf=new JFrame(this.getTitle());
		setLayout(new BorderLayout(0, 0));
		DefaultSyntaxKit.initKit();

		JToolBar toolBar = new JToolBar();
		add(toolBar, BorderLayout.NORTH);
		ImageIcon icon=new ImageIcon(TextEditor.class.getResource("/images/save.png"));
		Image img = icon.getImage();  
		Image newimg = img.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH);  
		icon = new ImageIcon(newimg); 
		JButton btnSave = new JButton(icon);
		btnSave.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	FileParser.writeText(editorPane.getText(), file.getAbsolutePath());
            }
        }); 
		btnSave.setToolTipText("Save current file");
		
		toolBar.add(btnSave);
		JEditorPane editorPane = new JEditorPane();

		add(editorPane, BorderLayout.CENTER);
		editorPane = new JEditorPane();
		scrPane = new JScrollPane(editorPane);
        this.add(scrPane, BorderLayout.CENTER);
        this.doLayout();
        String type;
        try{
        	type=this.title.split("\\.")[1];
        }catch(Exception e){
        	type="txt";
        }
        editorPane.setContentType("text/"+type);
        editorPane.setText(this.getTexte());
		jf.getContentPane().add(this);
		jf.setSize(800, 600);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

}
