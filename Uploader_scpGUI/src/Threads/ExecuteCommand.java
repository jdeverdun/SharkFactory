package Threads;


import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import modeles.Cluster;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import IO.HTMLparser;
import Main.ClassManagement;
import Main.MainViewer;

/**
 * Class to communicate with cluster
 * (sending commands)
 * @author Jérémy DEVERDUN
 *
 */
public class ExecuteCommand {
  private int length=0;
  private Session session;
  private Cluster clust;
  
  /**
   * Initialize connexion to cluster
   */
  public ExecuteCommand() {
	  try{
		  clust=ClassManagement.clusterlist.getdefaultCluster();
	      String user=clust.getUser();
	      String host=clust.getIp();

	      JSch jsch=new JSch();
	      session=jsch.getSession(user, host, 22);

	      // username and password will be given via UserInfo interface.
	      UserInfo ui=new MyUserInfo();
	      session.setUserInfo(ui);

         System.out.println("Getting session");
          System.out.println("session is ::::"+session.getHost());
          Properties config = new java.util.Properties();
          config.put("StrictHostKeyChecking", "no");
          session.setConfig(config);
          session.connect(30000);
	  }
      catch (Exception e)
	      { e.printStackTrace();
	      if(clust==null) System.exit(1);
		 ClassManagement.LogsPanel.addText("Error with ssh command");
	  }
    
  }	
  
  /**
   * Reset ssh connexion
   */
  public void resetSSHConnexion(){
	  try{
		  clust=ClassManagement.clusterlist.getdefaultCluster();
	      String user=clust.getUser();
	      String host=clust.getIp();

	      JSch jsch=new JSch();
	      session=jsch.getSession(user, host, 22);

	      // username and password will be given via UserInfo interface.
	      UserInfo ui=new MyUserInfo();
	      session.setUserInfo(ui);

         System.out.println("Getting session");
          System.out.println("session is ::::"+session.getHost());
          // username and password will be given via UserInfo interface.
          Properties config = new java.util.Properties();
          config.put("StrictHostKeyChecking", "no");
          session.setConfig(config);
          session.connect(30000);
          
	  }
      catch (Exception e)
	      { e.printStackTrace();
	      if(clust==null) System.exit(1);
		 ClassManagement.LogsPanel.addText("Error with ssh command");
	  }
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
   * @return
   */
  public Cluster getClust() {
	return clust;
}

  /**
   * 
   * @param clust
   */
	public void setClust(Cluster clust) {
		this.clust = clust;
	}

	/**
	 * Execute command "commande" on the cluster
	 * @param commande
	 * @return
	 */
	public String shell(String commande){
		  Channel channel;
		try {
			channel = session.openChannel("exec");
			((ChannelExec)channel).setCommand(commande);

		      channel.setInputStream(null);
		      ((ChannelExec)channel).setErrStream(System.err);
	
		      InputStream in=channel.getInputStream();
		      
		      channel.connect(3*1000);
		      byte[] tmp=new byte[1024];
		      String liste="";
		      while(true){
		        while(in.available()>0){
		          int i=in.read(tmp, 0, 1024);
		          if(i<0)break;
		          liste=liste+new String(tmp, 0, i);
		        }
		        if(channel.isClosed()){
		          System.out.println("exit-status: "+channel.getExitStatus());
		          if(channel.getExitStatus()!=0) throw new Exception("ssh command failed"); 
		          break;
		        }
		        try{Thread.sleep(1000);}catch(Exception ee){}
		      }
		      channel.disconnect();
		      return liste;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			if(e.getMessage().split("not opened").length>1){
				ClassManagement.LogsPanel.addText("Channel is closed");
				ClassManagement.LogsPanel.addText("Reconnecting ... ");
				resetSSHConnexion();
				return shell(commande);
			}
			return null;
		}
	      
	}
	
	/**
	 * 
	 * @return
	 */
  public Session getSession() {
	return session;
  }

  /**
   * 
   * @param session
   */
public void setSession(Session session) {
	this.session = session;
}


/**
 * 
 */
public void disconnect(){
      session.disconnect();
  }
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
	    	Object[] ob={passwordField}; 
	        int result=JOptionPane.showConfirmDialog(null, ob, message,
	                                                 JOptionPane.OK_CANCEL_OPTION);
	        if(result==JOptionPane.OK_OPTION){
	          passwd=passwordField.getText();
	          ClassManagement.pass=passwd;
	          return true;
	        }
	        else{ 
	          return false; 
	        }
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
   * @param input
   * @return
   * @throws Exception
   */
  public static String streamToString(InputStream input)throws Exception 
  { String output = ""; while(input.available()>0) { output += ((char)(input.read())); } return output; }


/**
 * 
 * @param charset
 * @return
 * @throws IOException
 */
  public static OutputStream stringToStream(String charset) throws IOException{

      byte[] bytes = charset.getBytes();
      InputStream is = null;
      OutputStream os = null;
      try {
          is = new ByteArrayInputStream(charset.getBytes("UTF-8"));
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }
      int numRead;

        while ( (numRead = is.read(bytes) ) >= 0) {
            os.write(bytes, 0, numRead);
        }

      return os;   
  }
}