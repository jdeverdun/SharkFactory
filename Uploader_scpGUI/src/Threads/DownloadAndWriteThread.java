package Threads;


import ij.IJ;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import modeles.Cluster;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import IO.FileParser;
import IO.HTMLparser;
import Main.ClassManagement;
import Main.MainViewer;

/**
 * Class for sending files to cluster (scp)
 * @author Jérémy DEVERDUN
 *
 */
public class DownloadAndWriteThread extends Thread{
  private int length=0;
  private JProgressBar jp;
  private String prefixe;
  
  /**
   * 
   */
  public void run() {
	  FileInputStream fis=null;
	  try{
		  
		  ClassManagement.Uploading=true;
		  Cluster clust=ClassManagement.clusterlist.getdefaultCluster();
		  MainViewer mv=ClassManagement.MainWindow;
		  String lfile=HTMLparser.URL;
		  String user=clust.getUser();
	      String host=clust.getIp();
	      String sub=lfile.substring(lfile.lastIndexOf(File.separator));
	      prefixe=sub.split(".tar")[0].split("images_")[1];
	      //ClassManagement.Session.addLog(lfile.substring(lfile.lastIndexOf(File.separator)));
	      String rfile="";
	      
	      rfile=clust.getRemoteDirectory()+File.separator+"archives/";

	      String command="scp -p -t "+rfile;
	      Channel channel=ClassManagement.ssh.getSession().openChannel("exec");
	      ((ChannelExec)channel).setCommand(command);

	      // get I/O streams for remote scp
	      OutputStream out=channel.getOutputStream();
	      InputStream in=channel.getInputStream();

	      channel.connect();

	      // send "C0644 filesize filename", where filename should not include '/'
	      long filesize=(new File(lfile)).length();
	      command="C0644 "+filesize+" ";
	      jp.setMaximum((int)(filesize));
	      if(lfile.lastIndexOf('/')>0){
	        command+=lfile.substring(lfile.lastIndexOf('/')+1);
	      }
	      else{
	    	  if(IJ.isWindows()){
	    		  if(lfile.lastIndexOf('\\')>0){
	    		        command+=lfile.substring(lfile.lastIndexOf('\\')+1);
	    		  }else{
	    			  command+=lfile;
	    		  }
	    	  }else{
	    		  command+=lfile;
	    	  }
	      }
	      
	      command+="\n";
	      out.write(command.getBytes()); out.flush();
		   // send a content of lfile
	      fis=new FileInputStream(lfile);
	      byte[] buf=new byte[1024];
	      while(true){
	        int len=fis.read(buf, 0, buf.length);
	      if(len<=0) break;
	        out.write(buf, 0, len); 
	        length += len;
      	  	jp.setValue(length);
	      }
	      fis.close();
	      fis=null;
	      // send '\0'
	      buf[0]=0; out.write(buf, 0, 1); out.flush();
	      out.close();

	      channel.disconnect();
	      //session.disconnect();
	      System.out.println(lfile);

		  HTMLparser.SpeedTest.stop();
		  mv.getTable().setValueAt("Done", mv.getRowForDir().get(prefixe), 1);

		  ClassManagement.Uploading=false;
		  ClassManagement.MainWindow.getUploadProgressBar().setValue(ClassManagement.MainWindow.getUploadProgressBar().getValue()+1);
		  FileParser.makeOneJDL("archive_images_"+prefixe+".tar.gz");
		  FileParser.submitOneJob(ClassManagement.jdlname+"_"+prefixe);
	      this.cancel();
	  }
      catch (Exception e)
	      { e.printStackTrace();
		 ClassManagement.LogsPanel.addText("Error...Will skip");
		 ClassManagement.Uploading=false;
		 ClassManagement.listDownload.LoadNext();
		 this.stop();
	  }
    
  }	
  
  /**
   * Cancel upload
   */
  public void cancel(){
		if(!ClassManagement.Uploading){
			ClassManagement.listDownload.LoadNext();
		}
		ClassManagement.Uploading=false;
		this.stop();
  }
  
  /**
   * 
   * @return
   */
  public int getLength(){
	  return length;
  }
  
  /**
   * 
   * @param in
   * @return
   * @throws IOException
   */
  static int checkAck(InputStream in) throws IOException{
	    int b=in.read();
	    // b may be 0 for success,
	    //          1 for error,
	    //          2 for fatal error,
	    //          -1
	    if(b==0) return b;
	    if(b==-1) return b;

	    if(b==1 || b==2){
	      StringBuffer sb=new StringBuffer();
	      int c;
	      do {
		c=in.read();
		sb.append((char)c);
	      }
	      while(c!='\n');
	      if(b==1){ // error
		System.out.print(sb.toString());
	      }
	      if(b==2){ // fatal error
		System.out.print(sb.toString());
	      }
	    }
	    return b;
	  }
  
  /**
   * 
   * @param jp
   */
  public void setJp(JProgressBar jp) {
	this.jp = jp;
}
  
  /**
   * 
   * @return
   */
public JProgressBar getJp() {
	return jp;
}


public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
	    public String getPassword(){ return passwd; }
	    public boolean promptYesNo(String str){
	       return true;
	    }
	  
	    String passwd;
	    JTextField passwordField=(JTextField)new JPasswordField(20);

	    public String getPassphrase(){ return null; }
	    public boolean promptPassphrase(String message){ return true; }
	    public boolean promptPassword(String message){
	      passwd=ClassManagement.pass;
	      return true;
	    }
	    public void showMessage(String message){
	      JOptionPane.showMessageDialog(null, message);
	    }
	    final GridBagConstraints gbc = 
	      new GridBagConstraints(0,0,1,1,1,1,
	                             GridBagConstraints.NORTHWEST,
	                             GridBagConstraints.NONE,
	                             new Insets(0,0,0,0),0,0);
	    private Container panel;
	    public String[] promptKeyboardInteractive(String destination,
	                                              String name,
	                                              String instruction,
	                                              String[] prompt,
	                                              boolean[] echo){
	      panel = new JPanel();
	      panel.setLayout(new GridBagLayout());

	      gbc.weightx = 1.0;
	      gbc.gridwidth = GridBagConstraints.REMAINDER;
	      gbc.gridx = 0;
	      panel.add(new JLabel(instruction), gbc);
	      gbc.gridy++;

	      gbc.gridwidth = GridBagConstraints.RELATIVE;

	      JTextField[] texts=new JTextField[prompt.length];
	      for(int i=0; i<prompt.length; i++){
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.gridx = 0;
	        gbc.weightx = 1;
	        panel.add(new JLabel(prompt[i]),gbc);

	        gbc.gridx = 1;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.weighty = 1;
	        if(echo[i]){
	          texts[i]=new JTextField(20);
	        }
	        else{
	          texts[i]=new JPasswordField(20);
	        }
	        panel.add(texts[i], gbc);
	        gbc.gridy++;
	      }

	      if(JOptionPane.showConfirmDialog(null, panel, 
	                                       destination+": "+name,
	                                       JOptionPane.OK_CANCEL_OPTION,
	                                       JOptionPane.QUESTION_MESSAGE)
	         ==JOptionPane.OK_OPTION){
	        String[] response=new String[prompt.length];
	        for(int i=0; i<prompt.length; i++){
	          response[i]=texts[i].getText();
	        }
		return response;
	      }
	      else{
	        return null;  // cancel
	      }
	    }
	  }

/**
 * 
 * @return
 */
	public String getPrefixe() {
		// TODO Auto-generated method stub
		return prefixe;
	}
}