package IO;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import modeles.Job;

import Main.ClassManagement;
import Main.display.LoadingFrame;
import Main.display.ProgressBar_status;
import Threads.DownloadAndWriteThread;
import Threads.ExecuteCommand;

/**
 * Every functions link to the web (downloading, uploading ...)
 * @author Jérémy DEVERDUN
 *
 */
public class HTMLparser {
	public static String URL;
	public static String dir2save;
	public static Timer Wait46;
	public static Timer SpeedTest;
	public static DownloadAndWriteThread ThreadDownload;
	public static int count;
	public static int countlast=0;
	public static int NBESSAI=0;
	
	/**
	 * Get content of an HTML page at url "u"
	 * @param u
	 * @return
	 * @throws IOException
	 */
	public static final String getHTML(String u) throws IOException{
		 try {
	            // Send the request
	            URL url = new URL(u);
	            URLConnection conn = url.openConnection();
	            conn.setDoOutput(true);
	            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

	            writer.flush();
	            
	            // Get the response
	            String answer = "";
	            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            String line;
	            while ((line = reader.readLine()) != null) {
	                answer+=line;
	            }
	            writer.close();
	            reader.close();
	            return answer;
		 } catch (MalformedURLException ex) {
	            ex.printStackTrace();
	     }
	     return "";
	}

	/**
	 * Retrieve Header for HTML
	 * Useless ATM 
	 * @return
	 */
	public static final String getHeaderStatus(){
		java.net.URL url = ClassLoader.getSystemResource("IO/header.txt");
		String text=FileParser.getTextFromFile(url.getFile());
		return text;
	}
	/**
	 * Retrieve footer for HTML
	 * Useless ATM
	 * @return
	 */
	public static final String getFooterStatus(){
		java.net.URL url = ClassLoader.getSystemResource("IO/footer.txt");
		String text=FileParser.getTextFromFile(url.getFile());
		return text;
	}
	
	/**
	 * Generate status page for jobs
	 * Useless ATM
	 */
	public static final void generateStatusPage(){
		final LoadingFrame lf=new LoadingFrame("Retrieving data, please wait.",true);
		Thread monThread = new Thread() {
         public void run() {
        	 String html=getHeaderStatus();
     		String[] tmp=html.split("@@");
     		html=tmp[0]+ClassLoader.getSystemResource("images/favicon.ico").getPath()+tmp[1]+ClassLoader.getSystemResource("images/bg.gif").getPath()+tmp[2]+ClassLoader.getSystemResource("images/bg_td_on.gif").getPath()+tmp[3]+"" +
     				ClassLoader.getSystemResource("images/bg_th_on.gif").getPath()+tmp[4]+ClassLoader.getSystemResource("images/bg_foot_td_on.gif").getPath()+tmp[5]+"" +
     				ClassLoader.getSystemResource("images/logo_tst.png").getPath()+tmp[6];
     		String[] liste=FileParser.getAllStatus().split("\n");
     		int num=0;
     		ClassManagement.done=new LinkedList<Job>();
     		ClassManagement.failed=new LinkedList<Job>();
     		ClassManagement.running=new LinkedList<Job>();
     		ClassManagement.waiting=new LinkedList<Job>();
     		for(int i=0;i<liste.length;i++){
     			if(liste[i].split("Status info").length>1){
     				String url=liste[i].split("b : h")[1];
     				String state=liste[i+1].split("Status")[1];
     				num++;
     				Job j=new Job(ClassManagement.jdlname+num,"h"+url);
     				if(state.split("Success").length>1){
     					j.setStatus("succeed");
     					ClassManagement.done.push(j);
     				}else{
     					if(state.split("Fai").length>1){
     						j.setStatus("failed");
     						ClassManagement.failed.push(j);
     					}else{
     						if(state.split("Subm").length>1){
     							j.setStatus("pending");
     							ClassManagement.waiting.push(j);
     						}else{
     							if(state.split("unni").length>1){
     								j.setStatus("running");
     								ClassManagement.running.push(j);
     							}
     						}
     					}
     				}
     			}
     		}
     		tmp=html.split("##");
     		html=tmp[0]+ClassManagement.waiting.size()+tmp[1]+ClassManagement.running.size()+tmp[2]+ClassManagement.done.size()+tmp[3]+ClassManagement.failed.size()+tmp[4];
     		for(Job jb:ClassManagement.running){
     			html+=jb.toHTML();
     		}
     		for(Job jb:ClassManagement.done){
     			html+=jb.toHTML();
     		}
     		for(Job jb:ClassManagement.waiting){
     			html+=jb.toHTML();
     		}
     		for(Job jb:ClassManagement.failed){
     			html+=jb.toHTML();
     		}
     		html+=getFooterStatus();
     		File temp;
     		try {
     			temp = File.createTempFile("status",".html");
     			temp.deleteOnExit();
     			FileParser.writeText(html, temp.getAbsolutePath());
     			openURI("file:///"+URLEncoder.encode(temp.getAbsolutePath()));
     		} catch (IOException e) {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     		} 
     		lf.dispose();
     		this.stop();
         }
     };
     monThread.start();
	}


	/**
	 * Upload specified file to cluster, updating progressbar j
	 * @param u
	 * @param j
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static final void upload(String u,JProgressBar j) throws IOException, InterruptedException{
		ClassManagement.Uploading=true;
		HTMLparser.URL=u;
		ThreadDownload=new DownloadAndWriteThread();
		ThreadDownload.setJp(j);
		Wait46=createTimer(1000,0);	

	}
	
	/**
	 * Check connexion status through proxy p
	 * @param p
	 * @return
	 */
	public static final boolean checkConnexion(Proxy p){
		try {
			String HTML=getHTML("http://www.clubic.com/",p);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Retrieve content of HTML web page through proxy
	 * @param string
	 * @param p
	 * @return
	 * @throws IOException
	 */
	private static String getHTML(String string, Proxy p) throws IOException {
		try {
            // Send the request
            URL url = new URL(string);
            URLConnection conn = url.openConnection(p);
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.flush();
            
            // Get the response
            String answer = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer+=line;
            }
            writer.close();
            reader.close();
            return answer;
	 } catch (MalformedURLException ex) {
            ex.printStackTrace();
     }
     return "";
	}
	
	/**
	 * Wait before launching upload, and updating connexion speed
	 * @param t
	 * @param type
	 * @return
	 */
	private static Timer createTimer (int t, int type)
	  {
	    // Création d'une instance de listener 
	    // associée au timer
		if(type==0){
        	SpeedTest=createTimer(1000,1);
        	SpeedTest.start();
        	ThreadDownload.start();
        	return null;
		}else{
	      ActionListener speedEstimate = new ActionListener ()
	      {
	        // Méthode appelée à chaque tic du timer
	        public void actionPerformed (ActionEvent event)
	        {
	        	count=ThreadDownload.getLength();
	        	//ThreadDownload.getJp().setSpeed(((count-countlast)/2)/1000);
	        	ClassManagement.MainWindow.setSpeed(((count-countlast)/2)/1000, ClassManagement.MainWindow.getRowForDir().get(ThreadDownload.getPrefixe()), ThreadDownload.getJp());
	        	countlast=count;
	        }
	      };
	      return new Timer (t, speedEstimate);
		}
	  }  
	/**
	 * 
	 */
	public static void stopThread(){
		if(ThreadDownload!=null){
			ClassManagement.Uploading=true;
			ThreadDownload.cancel();
		}
		ClassManagement.MainWindow.setSpeed(0, ClassManagement.MainWindow.getRowForDir().get(ThreadDownload.getPrefixe()), ThreadDownload.getJp());
		//ClassManagement.MainWindow.clearEnCours();
		SpeedTest.stop();
	}

	/**
	 * Open URL in a browser
	 * @param url
	 */
	public static void openURI(String url){
		if( !java.awt.Desktop.isDesktopSupported() ) {

            System.err.println( "Desktop is not supported (fatal)" );
            System.exit( 1 );
        }


        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {

            System.err.println( "Desktop doesn't support the browse action (fatal)" );
            System.exit( 1 );
        }


        try {

            java.net.URI uri = new java.net.URI( url );
            desktop.browse( uri );
        }
        catch ( Exception e ) {
        	e.printStackTrace();
            System.err.println( e.getMessage() );
        }
	}
}
