package Main.display;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
 
 
/**
 * Display image in a panel
 * @author Jérémy DEVERDUN
 *
 */
public class ImagePanel extends JPanel implements Serializable {
    Image image = null;
   
    /**
     * 
     * @param image
     */
    public ImagePanel(Image image) {
        this.image = image;
        
    }
    
    /**
     * 
     */
    public ImagePanel() {
       
    }
    
    /**
     * 
     * @param image
     */
    public void setImage(Image image){
        this.image = image;
        this.repaint();
    }
   
    /**
     * 
     * @param image
     * @return
     */
    public Image getImage(Image image){
        return image;
    }
   
    /**
     * 
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g); //paint background
        if (image != null) { //there is a picture: draw it
            int height = this.getSize().height;
            int width = this.getSize().width;      
            g.drawImage(image,0,0, width, height, this);
        }
    }
}