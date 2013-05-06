package Main.list;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import IO.FileParser;
import IO.HTMLparser;
import IO.TarParser;
import Main.ClassManagement;
import Main.MainViewer;


/**
 * List that contains the list of file to upload
 * @author Jérémy DEVERDUN
 *
 */
public class UploadList extends GlobalList {

	private LinkedList<String[]> EnAttente;
	
	/**
	 * 
	 */
	public UploadList(){
		super();
		EnAttente=new LinkedList<String[]>();
		ClassManagement.listDownload=this;
	}
	
	/**
	 * Add file to upload list
	 * @param u
	 * @param p
	 */
	public void add(String u,String p){
		String[] elem=new String[]{u,p};
		if(ClassManagement.Uploading){
			if(EnAttente.size()<1000) EnAttente.add(elem);
			else{
				JFrame errorFrame=new JFrame();
			 	 JOptionPane.showMessageDialog(errorFrame,
						    "Waiting list full.",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				 errorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				 errorFrame.dispose();
				ClassManagement.LogsPanel.addText("Waiting list full");
			}
		}else if(EnAttente.isEmpty()){
			EnCours=u;
			HTMLparser.NBESSAI=0;
			launchUpload(elem);
		}
	}
	
	/**
	 * Load next file to upload
	 */
	public void LoadNext(){
		EnCours="";
		if(!EnAttente.isEmpty()){
			EnCours=EnAttente.peek()[0];
			HTMLparser.NBESSAI=0;
			launchUpload(EnAttente.poll());
		}else{
			if(ClassManagement.MainWindow.getTarlist().isEmpty()){
				if(!ClassManagement.step1 && ClassManagement.full){
					//TarParser.tarByJobCluster();
					if(ClassManagement.isLPTA)
						FileParser.send2SE();
				}
			}else{
				ClassManagement.MainWindow.getTarlist().LoadNext();
			}
			
		}
	}
	
	/**
	 * Launch upload of specified file
	 * u[0]=file 
	 * u[1]=prefixe
	 * @param u
	 */
	public void launchUpload(String[] u){
		while(isPaused){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		JProgressBar jp=new JProgressBar();
		String prefixe=u[1];
		MainViewer mv=ClassManagement.MainWindow;
		if(!(ClassManagement.jobscriptsent.contains(prefixe))){
			try {
				HTMLparser.upload(u[0],jp);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			mv.getTable().setValueAt("Done", mv.getRowForDir().get(prefixe), 1);
			  ClassManagement.Uploading=false;
			  ClassManagement.MainWindow.getUploadProgressBar().setValue(ClassManagement.MainWindow.getUploadProgressBar().getValue()+1);
			  if(!ClassManagement.Uploading){
					ClassManagement.listDownload.LoadNext();
				}
				ClassManagement.Uploading=false;
		}
	}
	
	/**
	 * Became useless
	 * @return
	 */
	public String getFinish(){
		String res="<HTML>";
		finish.add(EnCours);
		if(finish.size()==10) finish.remove(9);
		for(String e:finish){
			String[] url=e.split("/");
			String nom=url[url.length-1];
			if(nom.length()>=47) nom="..."+nom.substring(nom.length()-47);
			res+=nom+"<BR>";
		}
		return res+"</HTML>";
	}
	
	/**
	 * Became useless
	 * @return
	 */
	public String getEnAttente(){
		String res="<HTML>";
		int count=0;
		for(String[] e:EnAttente){
			count++;
			String[] url=e[0].split("/");
			String nom=url[url.length-1];
			if(nom.length()>=67) nom="["+count+"] ..."+nom.substring(nom.length()-67);
			else nom="["+count+"] "+nom+"              ";
			res+=nom+"<BR>";
		}
		return res+"</HTML>";
	}

	
	/**
	 * Remove specified file from waiting list
	 */
	public void removeFromWaitingList(String v) {
		if(!EnAttente.isEmpty()) 
			EnAttente.remove(v);
	}

	/**
	 * 
	 */
	public void resetStatut() {
		EnAttente.addFirst(new String[]{EnCours,""});
		EnCours="";
		ClassManagement.LogsPanel.addText("Link unavaible..Check your connection ");
	}

	/**
	 * 
	 */
	public void removeAllWaitingList() {
		EnAttente.clear();
	}
}
