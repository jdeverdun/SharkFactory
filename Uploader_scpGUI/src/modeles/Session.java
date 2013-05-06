package modeles;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JOptionPane;

import IO.FileParser;
import Main.ClassManagement;


/**
 * Session model
 * @author Jérémy DEVERDUN
 *
 */
public class Session {
	private String id;
	private Cluster cluster;
	private int lastStep;
	private String directory="";
	private String logfile;
	private HashMap<String,String> jobid;
	private boolean whole;
	
	
	/**
	 * 
	 */
	public Session(){
		DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssZ");
		Date date = new Date();
		this.id=dateFormat.format(date);
		this.setCluster(null);
		this.lastStep=0;
		jobid=new HashMap<String,String>();
	}
	
	/**
	 * Session from file f
	 * @param f
	 */
	public Session(File f){
		jobid=new HashMap<String,String>();
		setSessionFromFile(f);
	}

	
	/**
	 * 
	 * @param s
	 */
	public Session(String s) {
		logfile=s;
		jobid=new HashMap<String,String>();
		DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssZ");
		Date date = new Date();
		this.id=dateFormat.format(date);
		this.setCluster(null);
		this.lastStep=0;
		this.addLog("session="+this.id);
	}
	
	/**
	 * 
	 * @param f
	 */
	public void setSessionFromFile(File f) {
		String text=FileParser.getTextFromFile(f.getAbsolutePath());
		String[] conf=(text.split("\n"));
		for(int i=1;i<conf.length;i++){
			try {
				String name=conf[i].split("=")[0];
				String value=conf[i].split("=")[1];
				switch(i){
					case 1:
						this.id=value;break;
					case 2:
						this.directory=value;
						if(!directory.equals(""))
							ClassManagement.MainWindow.setLink(this.directory);
						break;
					case 3:
						if(value.equals("true")){
							this.whole=true;
							ClassManagement.wholegland=true;
						}
						else{
							this.whole=false;
							ClassManagement.wholegland=false;
						};break;
					default:
						jobid.put(name,value);
						break;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		File tmp=new File(ClassManagement.installDir+File.separator+this.id);
		if(!tmp.exists()){
			JOptionPane.showMessageDialog(null, "Where is the directory from this session ? Will launch new one ...");
			this.createDir();
		}
	}
	
	/**
	 * 
	 * @param s
	 */
	public void addLog(String s){
		FileParser.appendText("\n"+s, ClassManagement.installDir+File.separator+"session.log");
	}
	
	/**
	 * Became useless
	 * @param lastStep
	 */
	public void setLastStep(int lastStep) {
		this.lastStep = lastStep;
		this.addLog("@@"+lastStep+"@@");
	}


	/**
	 * 
	 * @return
	 */
	public int getLastStep() {
		return lastStep;
	}


	/**
	 * 
	 * @param cluster
	 */
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	/**
	 * 
	 * @return
	 */
	public Cluster getCluster() {
		return cluster;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}
	/**
	 * 
	 * @param directory
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDirectory() {
		return directory;
	}
	
	/**
	 * Create temporary directory for the session
	 */
	public void createDir() {
		String sessdir=ClassManagement.installDir+File.separator+this.getId();
		File f=new File(sessdir);
		f.mkdir();
		f=new File(sessdir+File.separator+"images");
		f.mkdir();
		f=new File(sessdir+File.separator+"archives");
		f.mkdir();
		f=new File(sessdir+File.separator+"jobs");
		f.mkdir();
		f=new File(sessdir+File.separator+"results");
		f.mkdir();
	}
	
	/**
	 * Save session to file "file" and rename temporary directory
	 * @param file
	 */
	public void saveAs(File file) {
		File f=new File(""+this.id);
		ClassManagement.isnewsession=false;
		if(file.getName().split(".").length>0) this.id=file.getName().substring(0, file.getName().lastIndexOf("."));
		else this.id=file.getName();
		f.renameTo(new File(ClassManagement.installDir+File.separator+this.id));
		save(file);
	}
	
	/**
	 * Save session file
	 * @param file
	 */
	public void save(File file) {
		String res="\n"+"session="+file.getName()+"\ndirectory="+this.getDirectory();
		res+="\nwhole="+this.whole;
		for(String e:jobid.keySet()){
			res+="\n"+e+"="+jobid.get(e);
		}
		FileParser.writeText(res, file.getAbsolutePath()+".session");
	}
	
	/**
	 * Add ID of a submitted job
	 * @param s
	 */
	public void addID(String[] s) {
		ClassManagement.MainWindow.getBtnCanceljob().setEnabled(true);
		ClassManagement.MainWindow.getBtnDownload().setEnabled(true);
		ClassManagement.MainWindow.getBtnRefreshjob().setEnabled(true);
		ClassManagement.MainWindow.getBtnLaunchfailed().setEnabled(true);
		System.out.println(s[0]+"="+s[1]);
		jobid.put(s[0],s[1]);
		//addLog(s[0]+"="+s[1]);
		try{
        	
        	ClassManagement.Session.saveOnCluster();
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	/**
	 * Clear ID from job submitted
	 * @param i
	 */
	public void clearThisID(String i){
		jobid.remove(i);
	}
	
	/**
	 * Clear all ID of submitted jobs
	 */
	public void clearAllID(){
		jobid.clear();
		ClassManagement.MainWindow.getBtnCanceljob().setEnabled(false);
		ClassManagement.MainWindow.getBtnDownload().setEnabled(false);
		ClassManagement.MainWindow.getBtnRefreshjob().setEnabled(false);
		ClassManagement.MainWindow.getBtnLaunchfailed().setEnabled(false);
	}
	
	/**
	 * Informations on session
	 */
	public String toString(){
		String res="\n"+"session="+this.id+"\ndirectory="+this.getDirectory();
		for(String e:jobid.keySet()){
			res+="\n"+e+"="+jobid.get(e);
		}
		return res;
	}
	
	/**
	 * Wholegland process or isolated stacks
	 * @param wholegland
	 */
	public void setWhole(boolean wholegland) {
		this.whole=wholegland;
	}
	
	/**
	 * Get ID of job linked to image with prefixe "nom"
	 * @param nom
	 * @return
	 */
	public String getIDForPref(String nom) {
		return jobid.get(nom);
	}
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String, String> getJobid() {
		return jobid;
	}
	
	/**
	 * 
	 * @param jobid
	 */
	public void setJobid(HashMap<String, String> jobid) {
		this.jobid = jobid;
	}
	
	/**
	 * Get prefixe of the image processed in job "nom"
	 * @param nom
	 * @return
	 */
	public String getPrefForID(String nom) {
		for(String e:jobid.keySet()){
			if(jobid.get(e).equals(nom)) return e;
		}
		return null;
	}

	public void saveOnCluster() {
		String res="\n"+"session="+getId()+"\ndirectory="+this.getDirectory();
		res+="\nwhole="+this.whole;
		for(String e:jobid.keySet()){
			res+="\n"+e+"="+jobid.get(e);
		}
		ClassManagement.ssh.shell("rm "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/"+getId()+".session");
		System.out.println("echo \""+res+"\"> "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/"+getId()+".session");
		ClassManagement.ssh.shell("echo \""+res+"\"> "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/"+getId()+".session");
	}
}
