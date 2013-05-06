package IO;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import modeles.HpclrJob;
import modeles.Job;
import modeles.LptaJob;

import Main.ClassManagement;
import Main.MainViewer;
import Main.display.LoadingFrame;
import Main.display.ProgressBar_status;
import Main.list.UploadList;
import Threads.CheckState;
import Threads.ExecuteCommand;
import Threads.ImageJ_bridge;


/**
 * Any actions that needs to interact with
 * files or cluster
 * @author Jérémy DEVERDUN
 *
 */
public class FileParser {

	
	/**
	 * Look for file to process in "directory"
	 * and begin the preprocessing (IJ treatment)
	 * @param directory
	 * @return
	 */
	public static final String[] findFiles(File directory){
		ImageJ_bridge[] available=new ImageJ_bridge[ClassManagement.IJmaxThreads];
		for(int i=0;i<ClassManagement.IJmaxThreads;i++)
			available[i]=new ImageJ_bridge();
		MainViewer mv=ClassManagement.MainWindow;
		File[] contenu=directory.listFiles();
		String[] atraiter=new String[contenu.length];
		int count=0;
		String prefixe=null;
		boolean continu;
		for(int i=0;i<contenu.length;i++){
			if((ClassManagement.wholegland && !ClassManagement.MainWindow.getCancelled().contains((String)contenu[i].getName())) || (
					!ClassManagement.wholegland && !ClassManagement.MainWindow.getCancelled().contains(((String)contenu[i].getName().substring(0, contenu[i].getName().lastIndexOf(".")))))){
				if((ClassManagement.wholegland && contenu[i].isDirectory() && !contenu[i].isHidden()) || (!ClassManagement.wholegland && contenu[i].isFile())){
					String nomfile="";
					// Look if the file has already been processed
					if(ClassManagement.wholegland){
						prefixe=hasStack(contenu[i],ClassManagement.wholegland);
						nomfile=contenu[i].getName();
					}else{
						prefixe=hasStack(contenu[i],ClassManagement.wholegland);
						nomfile=contenu[i].getName().substring(0, contenu[i].getName().lastIndexOf("."));
					}
					mv.setIJProgress(mv.getIJProgress().getValue()+1);
					boolean wait=true;
					if(prefixe==null){
						// launch preprocessing on parallel threads
						ClassManagement.LogsPanel.addText("Waiting available thread for "+nomfile+".");
						continu=true;
						ImageJ_bridge ib=null;
						// Wait for an avaible thread
						while(continu){
							for(int t=0;t<ClassManagement.IJmaxThreads;t++){
								ib=available[t];
								if(ClassManagement.MainWindow.getCancelled().contains(ib.getDirectory().getName()) ||
										ClassManagement.MainWindow.getCancelled().contains(nomfile)){
									continu=false;
									if(ib.isAlive()) ib.stop();
								}else{
									if(!ib.isAlive()){
										ClassManagement.LogsPanel.addText(nomfile+" on thread "+(t+1)+"/"+(ClassManagement.IJmaxThreads));
										ib=new ImageJ_bridge();
										available[t]=ib;
										continu=false;break;
									};
								}
							}
							if(continu)
								if(ClassManagement.killAllThread){
									for(int t=0;t<ClassManagement.IJmaxThreads;t++){
										try{available[t].stop();}catch(Exception e){};
									}
									return null;
								}
								wait=true;
								while(wait){
									try {
										Thread.sleep(10000);
										if(!ClassManagement.suspend){
											wait=false;
												for(int t=0;t<ClassManagement.IJmaxThreads;t++){
													available[t].resume();
												}
										}else{
											for(int t=0;t<ClassManagement.IJmaxThreads;t++){
												available[t].suspend();
											}
										}
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
						}
						ib.setDirectory(contenu[i]);
						ib.start();
					}else{
						mv.getTable().setValueAt("Ready for tarring.", mv.getRowForDir().get(nomfile), 1);
					}
					
				}
			}
		}
		// on refait la boucle pour être sur que le thread est fini
		count=0;
		continu=true;
		while(continu){
			continu=false;
			for(int t=0;t<ClassManagement.IJmaxThreads;t++){
				if(available[t].isAlive()){
					continu=true;break;
				}
			}
			if(continu)
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
		for(int i=0;i<contenu.length;i++){
			if((ClassManagement.wholegland && !ClassManagement.MainWindow.getCancelled().contains((String)contenu[i].getName())) || (
					!ClassManagement.wholegland && !ClassManagement.MainWindow.getCancelled().contains(((String)contenu[i].getName().substring(0, contenu[i].getName().lastIndexOf(".")))))){
				if(ClassManagement.wholegland && contenu[i].isDirectory() && !contenu[i].isHidden()){
					prefixe=hasStack(contenu[i],ClassManagement.wholegland);
					atraiter[count++]=prefixe;
				}else{
					prefixe=hasStack(contenu[i],ClassManagement.wholegland);
					atraiter[count++]=prefixe;
				}
			}
		}
		
		return atraiter;
	}

	/**
	 * Verify if a stack exists for this directory
	 * @param file
	 * @param wholegland
	 * @return
	 */
	private static String hasStack(File file, boolean wholegland) {
		// TODO Auto-generated method stub
		//File[] contenu=file.listFiles();
		String tranche=file.getName();
		if(!wholegland) tranche=file.getName().substring(0, file.getName().lastIndexOf("."));
		if(wholegland && (new File(ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+
				File.separator+"images"+File.separator+tranche+ClassManagement.extension[ClassManagement.extension.length-1])).exists())
			return tranche;
		else{
			if(!wholegland && (new File(ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+
					File.separator+"images"+File.separator+tranche+ClassManagement.extension[ClassManagement.extension.length-2])).exists())
				return tranche;
		}
		return null;
	}
	
	/**
	 * Load session from a file (extension .session)
	 * @return
	 */
	public static boolean loadSession(){
		JFileChooser jf=new JFileChooser();
		jf.setCurrentDirectory(new java.io.File("."));
		jf.setDialogTitle("Open");
		jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		ExtensionFileFilter sessFilter = new ExtensionFileFilter(null, new String[] { "session" });

	    jf.addChoosableFileFilter(sessFilter);
		jf.setAcceptAllFileFilterUsed(false);

	    if (jf.showOpenDialog(ClassManagement.MainWindow) == JFileChooser.APPROVE_OPTION) { 
	      System.out.println("getCurrentDirectory(): " 
	         +  jf.getCurrentDirectory());
	      System.out.println("getSelectedFile() : " 
	         +  jf.getSelectedFile());
	      ClassManagement.isnewsession=false;
	      try{
	      	ClassManagement.MainWindow.clearAll();
	      }catch(Exception e){}
	      	if(ClassManagement.MainWindow.getFiletree()!=null) ClassManagement.MainWindow.getFiletree().changeTree(new File("."));
      		String orig =jf.getSelectedFile().getAbsolutePath();
	      	String dest = ClassManagement.installDir+File.separator+"session.log";
	      	InputStream in=null;
	      	OutputStream out=null;
			try {
				in = new FileInputStream(orig);
		      	out = new FileOutputStream(dest);
		      	byte[] buf = new byte[1024];
		      	int len;
		      	while ((len = in.read(buf)) > 0) {
		      	   out.write(buf, 0, len);
		      	}
		      	in.close();
		      	out.close(); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ClassManagement.MainWindow.SetProperties(true);
			return true;
	      }
	    else {
	      System.out.println("No Selection ");
	      return false;
	      }
	}
	
	/**
	 * Replace backslash
	 * @param myStr
	 * @return
	 */
	public static String backlashReplace(String myStr){
	    final StringBuilder result = new StringBuilder();
	    final StringCharacterIterator iterator = new StringCharacterIterator(myStr);
	    char character =  iterator.current();
	    while (character != CharacterIterator.DONE ){
	     
	      if (character == '\\') {
	         result.append("\\\\");
	      }
	       else {
	        result.append(character);
	      }

	      
	      character = iterator.next();
	    }
	    return result.toString();
	  }
	
	
	/**
	 * Clear directory "file" from stack 
	 * Note : this is useless at the moment
	 * @param file
	 */
	public static void clearDirFromStack(File file){
		if(true) return; //useless ATM
		final LoadingFrame lf=new LoadingFrame("Cleaning incomplete stacks.",true);
		final File f=file;
		//ClassManagement.ssh.shell("rm -rf /tmp/molino*");
		 Thread monThread = new Thread() {
	         public void run() {
	        	 File[] contenu=f.listFiles();
	        	 if(ClassManagement.wholegland){
		     		for(int j=0;j<contenu.length;j++){
		     			if(contenu[j].isDirectory()){
		     				File[] contenu2=contenu[j].listFiles();
		     				for(int s=0;s<contenu2.length;s++){
		     					if(contenu2[s].isDirectory()){
			     					File[] contenu3=contenu2[s].listFiles();
			     					for(int i=0;i<contenu3.length;i++){
			     						if (contenu3[i].isFile() && contenu3[i].getName().split("_stack").length>1){
			     							contenu3[i].delete();
			     						}
			     					}
		     					}
		     				}
		     			}
		     		}
	        	 }else{
		     			for(int j=0;j<contenu.length;j++){
		     				if(contenu[j].isFile() && contenu[j].getName().split("_stack").length>1){
		     					contenu[j].delete();
		     				}
		     			}
		     	}
				lf.dispose();
				this.stop();
	         }
		 };
		 monThread.start();
		
	}
	
	/**
	 * Look if local session = remote session (on cluster)
	 * @return
	 */
	public static boolean CheckRemoteSession(){
		String res="";
		res=ClassManagement.ssh.shell("cat "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+
				File.separator+ClassManagement.Session.getId()+".session");
		if(res==null) return false;
		if(res.split(ClassManagement.Session.getId()).length>0) return true;
		else return false;
		
	}
	
	/**
	 * Count recursively number of directory in "dir"
	 * @param dir
	 * @return
	 */
	public static int recursiveDirList(File dir){
		int count=0;
		String[] f=dir.list();
		for(int i=0;i<f.length;i++){
			File tmp=(new File(dir.getAbsolutePath()+File.separator+f[i]));
			if(tmp.isDirectory()) count+=1+recursiveDirList(tmp);
		}
		return count;
	}
	
	/**
	 * Retrieve content of file "f"
	 * @param f
	 * @return
	 */
	public static String getTextFromFile(String f) {
		File file = new File(f);
	    int ch;
	    StringBuffer strContent = new StringBuffer("");
	    FileInputStream fin = null;
	    try {
	      fin = new FileInputStream(file);
	      while ((ch = fin.read()) != -1)
	        strContent.append((char) ch);
	      fin.close();
	    } catch (Exception e) {
	      System.out.println(e);
	    }
		return strContent.toString();
	}

	/**
	 * Retrieve all results from cluster to local
	 * @return
	 */
	public static boolean retrieveAllOutput(){
		final LoadingFrame lf=new LoadingFrame("Retrieving data, please wait.",false);
		 Thread monThread = new Thread() {
	         public void run() {
	        	 if(ClassManagement.isLPTA){
		     		String[] jdl=getRunningJobs();
		     		int l=jdl.length;
					for(int i=1;i<jdl.length;i++){
						retrieveOneOutput(jdl[i].substring(0, jdl[i].indexOf(".")));
					}
	        	 }else{
	        		 if(ClassManagement.isHPCLR){
	        			getAllStatus();
	 					LinkedList<Job> ks=ClassManagement.done;//Session.getJobid().keySet();
						for(Job e:ks){
							FileParser.retrieveOneOutput(e.getPrefixe());
						}
	        		 }
	        	 }
				lf.dispose();
	         }
		 };
		 monThread.start();
		return true;
	}
	
	/**
	 * Retrieve one result from cluster to local
	 * @param substring
	 */
	public static void retrieveOneOutput(String substring) {
		// TODO Auto-generated method stub
		//ClassManagement.ssh.shell("rm "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"jobs/"+nom+".jid");
		if(ClassManagement.isLPTA){
			String commande="glite-wms-job-output --noint -i "+substring+".jid";
			//String commande=ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"jobs/launch_one_job.sh "+nom.split("/")[nom.split("/").length-1]+;
			System.out.println(commande);
			String res=ClassManagement.ssh.shell(commande);
			try{
				String[] ligne=res.split("\n");
				System.out.println(res);
				for(int i=0;i<ligne.length;i++){
					if(ligne[i].split("/molino").length>1){
						commande="cp "+ligne[i]+"/* "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/results/";
						System.out.println(commande);
						ClassManagement.ssh.shell(commande);
						break;
					}
				}
			}catch(Exception e){
				System.out.println(e.toString());
				System.out.println("Error while trying to retrieve output.");
			}
		}else{
			if(ClassManagement.isHPCLR){
				final String f=substring;
				JSch jsch = new JSch();
				ClassManagement.LogsPanel.addText("Retrieving "+f+" output");
			    Session session = null;
			    System.out.println("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+File.separator+"results"+File.separator+f+"*");
			    String txt=ClassManagement.ssh.shell("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+File.separator+"results"+File.separator+f+"*");
			    if(txt!=null){
			    	String[] res=txt.split("\n");
				    try {
				        session = jsch.getSession(ClassManagement.clusterlist.getdefaultCluster().getUser(), ClassManagement.clusterlist.getdefaultCluster().getIp());
				        session.setConfig("StrictHostKeyChecking", "no");
				        session.setPassword(ClassManagement.pass);
				        session.connect();
	
				        Channel channel = session.openChannel("sftp");
				        channel.connect();
				        ChannelSftp sftpChannel = (ChannelSftp) channel;
				        for(String e:res){
				        	System.out.println("downloading "+e);
				        	System.out.println("local : "+ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+File.separator+"" +
		        					"results"+File.separator+e.substring(e.lastIndexOf(File.separator)));
				        	sftpChannel.get(e, ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+File.separator+"" +
				        					"results"+File.separator+e.substring(e.lastIndexOf(File.separator)));
				        	
				        }
				        ClassManagement.MainWindow.getTable().setValueAt("Retrieved", ClassManagement.MainWindow.getRowForDir().get(f), 1);
				        sftpChannel.exit();
				        session.disconnect();
				    } catch (Exception e) {
				        System.out.println("erreur"+e.toString());
				    } 
			    }else{
			    	ClassManagement.MainWindow.getTable().setValueAt("not found", ClassManagement.MainWindow.getRowForDir().get(f), 1);
			    }

			}
		}
	}
	
	/**
	 * Recursively deleting directory
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir){
		if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}
	
	/**
	 * Retrieve status of all jobs
	 * @return
	 */
	public static String getAllStatus(){
		if(ClassManagement.isLPTA){
			int nb_jobs=0;
			try{
				nb_jobs=Integer.parseInt(ClassManagement.ssh.shell("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/*jid | wc -l").split("\n")[0]);
			}catch(Exception e){
				e.printStackTrace();
			}
			String ligne=(ClassManagement.ssh.shell(ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/status.sh "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/uploadedjob "+nb_jobs));
			return ligne;
		}else{
			if(ClassManagement.isHPCLR){
				String id="";
				Set<String> jdl=ClassManagement.Session.getJobid().keySet();
				String joblist="";
				for(String nom:jdl){
					joblist+=ClassManagement.Session.getIDForPref(nom)+" ";
				}
				String status=ClassManagement.ssh.shell("llq -u molinof ");//+joblist);
				String[] ligne=null;
				LinkedList<String> availablePref=getPrefixeFromJDL();
				if(!(status.split("no job").length>1)) ligne=status.split("\n");
				if(status!=null && !status.equals("")){
					String s="";
					String jid="";
					String list=ClassManagement.ssh.shell("ls -la "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+File.separator+"results/*gz");
					String nom="";
					ClassManagement.done.clear();
					ClassManagement.waiting.clear();
					ClassManagement.running.clear();
					ClassManagement.failed.clear();
					// Now we add the stack that for some reason doesn't appear on the session file
					String[] listligne=list.split("\n");
					for(int i=0;i<listligne.length;i++){
						String pr=listligne[i].split("_images_")[1].split("_results")[0];
						s="success";
						Job j=new HpclrJob(jid);
						j.setStatus(s);
						j.setPrefixe(pr);
						ClassManagement.done.add(j);
						availablePref.remove(pr);
					}
					for(String locid:jdl){
						nom=ClassManagement.Session.getPrefForID(jid);
						if(list!=null && list.split("tar").length>1){
							if(ClassManagement.DEBUG) System.out.println("locid : "+locid);
							jid=ClassManagement.Session.getIDForPref(locid);
							if(!(list.split(locid).length>1)){
								status="failed";
								Job j=new HpclrJob(jid);
								j.setStatus(status);
								j.setPrefixe(locid);
								ClassManagement.failed.add(j);
								availablePref.remove(locid);
							}
						}
					}
					if(ligne!=null){
						for(int i=2;i<ligne.length-2;i++){
							Pattern p = Pattern.compile(" +");
						    String[] items = p.split(ligne[i]);
							s=items[4];
							s=HpclrJob.StatusFromCode(s);
							jid=ligne[i].split("\\.")[0]+"."+ligne[i].split("\\.")[1];
							nom=ClassManagement.Session.getPrefForID(jid);
							HpclrJob j=new HpclrJob(jid);
							j.setPrefixe(nom);
							if(s.equals("running")){
								ClassManagement.running.add(j);
								ClassManagement.failed.remove(j);
								availablePref.remove(nom);
							}else{
								if(s.equals("waiting")){
									ClassManagement.waiting.add(j);
									ClassManagement.failed.remove(j);
									availablePref.remove(nom);
								}
							}
						}
					}
					
					// Mise à jours du status dans la fenêtre
					for(Job j:ClassManagement.done){
						ClassManagement.MainWindow.getTable().setValueAt("success", ClassManagement.MainWindow.getRowForDir().get(j.getPrefixe()), 1);
					}
					for(Job j:ClassManagement.failed){
						ClassManagement.MainWindow.getTable().setValueAt("failed", ClassManagement.MainWindow.getRowForDir().get(j.getPrefixe()), 1);
					}
					for(Job j:ClassManagement.running){
						ClassManagement.MainWindow.getTable().setValueAt("running", ClassManagement.MainWindow.getRowForDir().get(j.getPrefixe()), 1);
					}
					for(Job j:ClassManagement.waiting){
						ClassManagement.MainWindow.getTable().setValueAt("waiting", ClassManagement.MainWindow.getRowForDir().get(j.getPrefixe()), 1);
					}
					
					// job in Waiting  (not on the cluster) state ....
					for(String j:availablePref){
						ClassManagement.MainWindow.getTable().setValueAt("failed", ClassManagement.MainWindow.getRowForDir().get(j), 1);
						Job job=new Job();
						job.setPrefixe(j);
						ClassManagement.failed.add(job);
					}
					// FOR DEBUG PURPOSE ONLY !!!
					/*for(String p:ClassManagement.MainWindow.getRowForDir().keySet()){
						if(ClassManagement.MainWindow.getTable().getValueAt(ClassManagement.MainWindow.getRowForDir().get(p), 1).equals("Waiting")){
							HpclrJob j=new HpclrJob();
							j.setPrefixe(p);
							ClassManagement.failed.add(j);
						}
					}*/
				}
			}
		}
		return null;
	}
	
	/**
	 * Sending data to Storage Element (LPTA only)
	 * @return
	 */
	public static boolean send2SE(){
		String listefichiers=ClassManagement.ssh.shell("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/archives/");
		String[] files=listefichiers.split("\n");
		String logs="";
		String srmlist="";
		String lfnlist="";
		String srm_url="";
		for(int i=0;i<files.length;i++){
			String nom=files[i].split("/")[files[i].split("/").length-1];
			String tmp=ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"upload.sh "+nom;
			System.out.println(tmp);
			ClassManagement.ssh.shell("rm /home/molino/SENT.log");
			String guid=ClassManagement.ssh.shell(tmp);
			System.out.println(guid);
			logs+="\n\n=====================================================================\nUploading to SE file : "+files[i]+"\n\n";
			logs+=tmp+"\n"+guid;
			ClassManagement.LogsPanel.addText("Uploading to SE file : "+files[i]+"\n"+tmp);
			lfnlist+="lfn:/dpm/msfg.fr/home/"+ClassManagement.vo+"/molino/"+nom+"\n";
			srm_url+=guid;
		}
		try{
		    // Create file 
			String f=ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/logs/logs_SE.txt";
			ClassManagement.ssh.shell("echo \""+logs+"\">"+f);
	    }catch (Exception e){//Catch exception if any
	      System.err.println("Error: " + e.getMessage());
	    }
	    try{
		    // Create file 
			String f=ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/logs/"+ClassManagement.srm_url;
			ClassManagement.ssh.shell("echo \""+srm_url+"\">"+f);
	    }catch (Exception e){//Catch exception if any
	      System.err.println("Error: " + e.getMessage());
	    }
	    try{
		    // Create file 
	    	String f=ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/logs/"+ClassManagement.guid_file_list;
	    	ClassManagement.ssh.shell("echo \""+srmlist+"\">"+f);
	    }catch (Exception e){//Catch exception if any
	      System.err.println("Error: " + e.getMessage());
	    }
	    try{
		    // Create file 
	    	String f=ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/logs/"+ClassManagement.lfn_file_list;
	    	ClassManagement.ssh.shell("echo \""+lfnlist+"\">"+f);
	    }catch (Exception e){//Catch exception if any
	      System.err.println("Error: " + e.getMessage());
	    }
	    ClassManagement.LogsPanel.addText("Files uploaded : "+lfnlist);
		return true;
	}
	
	/**
	 * Delete all LFN files (LPTA only)
	 */
	public static void deleteAllLfn(){
		String chaine="";
		String fichier=ClassManagement.ssh.shell("cat "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/logs/"+ClassManagement.lfn_file_list);
		String[] nom=fichier.split("\n");
		for(int i=0;i<nom.length;i++){
			System.out.println(nom[i]);
			ClassManagement.LogsPanel.addText("Deleting "+nom[i]+" from SE");
			ClassManagement.ssh.shell("lcg-del -a "+nom[i]);
		}
		ClassManagement.ssh.shell("echo \"\">"+ClassManagement.lfn_file_list);
	}

	/**
	 * Clear cluster logs 
	 * Note : unused on the lastest version
	 */
	public static void clearLogs() {
		String chaine="";
		String fichier=ClassManagement.ssh.shell("rm "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/logs/*");
	}
	
	/**
	 * Generate all JDL files for jobs submission
	 * Saving them locally and on the cluster
	 */
	public static void makeJdl(){
		if(ClassManagement.isLPTA){
			String[] lfn=ClassManagement.ssh.shell("cat "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/logs/"+ClassManagement.srm_url).split("\n");
			for(int i=0;i<lfn.length;i++){
				String surl=ClassManagement.ssh.shell("lcg-lr "+lfn[i]);
				String archive=lfn[i].split("/")[lfn[i].split("/").length-1];
				makeOneJDL(lfn[i]);
			}
		}else{
			if(ClassManagement.isHPCLR){
				String[] archive=ClassManagement.ssh.shell("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/archives/*.gz").split("\n");
				for(int i=0;i<archive.length;i++){
					makeOneJDL(archive[i].substring((archive[i].lastIndexOf("/"))+1));
				}
			}
		}
	}
	
	/**
	 * Generate one JDL file for specified image stack "nom" (ex : T1I1)
	 * @param nom
	 */
	public static void makeOneJDL(String nom){
		int type=1;
		if(ClassManagement.wholegland) type=1;
		else type=2;
		if(ClassManagement.isLPTA){
			String surl=ClassManagement.ssh.shell("lcg-lr "+nom);
			String archive=nom.split("/")[nom.split("/").length-1];
			String pref=archive.split("\\.tar")[0].split("images_")[1];
			String jdl="Type = \\\"Job\\\";\n" +
					"JobType = \\\"Normal\\\";\n" +
					"Executable = \\\"run_nuclei.sh\\\";\n" +
					"StdOutput = \\\"molino.out\\\";\n" +
					"StdError = \\\"molino.err\\\";\n" +
					"OutputSandbox = {\\\"molino.err\\\",\\\"molino.out\\\",\\\"archive_images_"+pref+"_results.tar.gz\\\"};\n" +
					"InputSandbox = {\\\"run_nuclei.sh\\\",\\\"nuclei_c\\\",\\\"innerScore.class\\\"};\n" +
					"Arguments = \\\"/swareas/vo.lpta.in2p3.fr/FMolino/MCR/v715 "+surl+" archive_images_"+pref+".tar.gz archive_images_"+pref+".tar.gz "+type+"\\\";\n" +
					"Requirements      = other.GlueCEInfoHostName == \\\"lptace01.msfg.fr\\\" && other.GlueHostMainMemoryRAMAvailable >= "+ClassManagement.MAXMEMORY+"; \n" +
					"RetryCount = 2;	\n" +
					"rank =  (other.GlueCEStateFreeCPUs);\n" +
					"DataAccessProtocol = {\\\"gridftp\\\",\\\"rfio\\\",\\\"gsiftp\\\",\\\"gsidcap\\\",\\\"https\\\"};\n";
			System.out.println(jdl);
			ClassManagement.ssh.shell("echo \""+jdl+"\">"+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/"+ClassManagement.jdlname+"_"+pref+".jdl");
			writeText(jdl, ClassManagement.installDir.getAbsolutePath()+File.separator+ClassManagement.Session.getId()+
					File.separator+"jobs/"+ClassManagement.jdlname+"_"+pref+".cmd");
			ClassManagement.LogsPanel.addText("Job script for "+pref+" created.");
		}else{
			if(ClassManagement.isHPCLR){
				String pref=nom.split("\\.tar")[0].split("images_")[1];
				String jdl="#!/bin/sh \n"+
					"# Script de soumission Loadleveler pour un job sequentiel \n"+
					"# @ job_name = "+ClassManagement.jdlname+"_"+pref+" \n"+
					"# @ output = "+ClassManagement.jdlname+"_"+pref+".out \n"+
					"# @ error  = "+ClassManagement.jdlname+"_"+pref+".err \n"+
					"# @ job_type = serial \n"+
					"# @ wall_clock_limit = 73:00:00,72:55:00 \n"+
					"# @ resources = ConsumableMemory("+ClassManagement.MAXMEMORY+") \n"+
					"# @ queue \n"+
					
					""+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/run_nuclei_c.sh /home/molinof/MCR/v715 "+nom+" "+type;
				System.out.println(jdl);
				ClassManagement.ssh.shell("echo \""+jdl+"\">"+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/"+ClassManagement.jdlname+"_"+pref+".cmd");
				writeText(jdl, ClassManagement.installDir.getAbsolutePath()+File.separator+ClassManagement.Session.getId()+
						File.separator+"jobs/"+ClassManagement.jdlname+"_"+pref+".cmd");
				ClassManagement.LogsPanel.addText("Job script for "+pref+" created.");
			}
		}
	}
	
	/**
	 * Write specified text in specified file
	 * @param text
	 * @param chemin
	 */
	public static void writeText(String text,String chemin){
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(chemin.replaceAll("%20", "\\ "));
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(text);
			  //Close the output stream
			  out.close();
	  }catch (Exception e){//Catch exception if any
		  System.err.println("Error: " + e.getMessage());
	  }
	}
	
	/**
	 * Add specified text to specified file
	 * @param text
	 * @param chemin
	 */
	public static void appendText(String text,String chemin){
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(chemin.replaceAll("%20", "\\ "),true);
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(text);
			  //Close the output stream
			  out.close();
			  fstream.close();
	  }catch (Exception e){//Catch exception if any
		  System.err.println("Error: " + e.getMessage());
	  }
	}
	
	
	/**
	 * Submit all jobs
	 */
	public static void submitAllJobs(){
		String[] jdl=getPossibleJobs();
		for(int i=0;i<jdl.length;i++){
			submitOneJob(jdl[i].substring(0, jdl[i].indexOf(".")));
		}
	}
	
	/**
	 * Submit the job for the stack "nom" (ex : uploadedjob_T1_I1)
	 * @param nom
	 */
	public static void submitOneJob(String nom){
		String commande="";
		if(ClassManagement.isLPTA){
			ClassManagement.ssh.shell("rm "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/"+nom+".jid");
			commande="glite-wms-job-submit -e https://marwms.in2p3.fr:7443/glite_wms_wmproxy_server -r lptace01.msfg.fr:2119/jobmanager-pbs-vo.msfg.fr -a -o "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"" +
					"jobs/"+nom+".jid "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"jobs/"+nom+".jdl";
			System.out.println(commande);
		}else{
			if(ClassManagement.isHPCLR){
				commande="llsubmit "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+File.separator+"jobs/"+nom+".cmd";
				int i=ClassManagement.MainWindow.getRowForDir().get(nom.split("_")[1]+"_"+nom.split("_")[2]);
				if(i>0) ClassManagement.MainWindow.getTable().setValueAt("Submitted", i, 1);
				System.out.println(commande);
			}
		}
		String res=ClassManagement.ssh.shell(commande);
		if(res==null) return;
		System.out.println(res);
		String pref=nom.split("_")[1]+"_"+nom.split("_")[2];
		MainViewer mv=ClassManagement.MainWindow;
		mv.getTable().setValueAt("Submitted", mv.getRowForDir().get(pref), 1);
		ClassManagement.LogsPanel.addText(pref+" submitted.");
		if(ClassManagement.isHPCLR){
			String id=res.split("\"")[1];
			System.out.println(pref+"-"+id);
			ClassManagement.Session.addID(new String[]{pref,id});
		}else{
			if(ClassManagement.isLPTA){
				String id=ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+File.separator+"" +
						"jobs"+File.separator+nom+".jid";
				ClassManagement.Session.addID(new String[]{pref,id});
			}
		}
		System.out.println(res);
	}
	
	/**
	 * Get status for specified image stack
	 * @param nom
	 * @return
	 */
	public static String getStatus(String nom){
		String status="";
		if(ClassManagement.isLPTA){
			String url=ClassManagement.ssh.shell("glite-wms-job-status -i "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+File.separator+"jobs/"+nom+".jid");
			String[] ligne=url.split("\n");
			for(int i=0;i<ligne.length;i++){
				if(ligne[i].split("ucce").length>0){ 
					Job j=new LptaJob(nom.split("_")[1]);
					j.setStatus("success");
					j.setPrefixe(nom.split("_")[1]);
					ClassManagement.done.add(j);
					return "success"; 
				}
				else if(ligne[i].split("code").length>0){  
					Job j=new LptaJob(nom.split("_")[1]);
					j.setStatus("failed");
					j.setPrefixe(nom.split("_")[1]);
					ClassManagement.failed.add(j);
					return "failed";
				}
				else if(ligne[i].split("aitin").length>0){ return "pending";}
			}
			return status;
		}else{
			String id=ClassManagement.Session.getIDForPref(nom);
			if(ClassManagement.MainWindow.getTable().getValueAt(ClassManagement.MainWindow.getRowForDir().get(nom), 1).equals("success")){
				ClassManagement.done.add(new Job(id));
				return "success";
			}
			System.out.println("llq "+id +" | grep \""+id+"\"");
			status=ClassManagement.ssh.shell("llq "+id +" | grep \""+id+"\"");
			System.out.println("status : "+status);
			if(status!=null && !status.equals("")){
				Pattern p = Pattern.compile(" +");
			    String[] items = p.split(status);
				status=items[4];
				System.out.println(status);
				status=HpclrJob.StatusFromCode(status);
			}else{
				String list=ClassManagement.ssh.shell("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+File.separator+"results/"+nom+"_*");
				System.out.println("list : ");
				if(list!=null && list.split(nom).length>1){
					status="success";
					Job j=new HpclrJob(id);
					j.setStatus(status);
					j.setPrefixe(nom);
					ClassManagement.done.add(j);
				}else{
					status="failed";
					Job j=new HpclrJob(id);
					j.setStatus(status);
					j.setPrefixe(nom);
					ClassManagement.failed.add(j);
				}
			}
			return status;
		}
	}

	/**
	 * Retrieve jobs with "Done" status
	 * @return
	 */
	private static LinkedList<Job> getDoneJobs() {
		LinkedList<Job> res=new LinkedList<Job>();
		if(ClassManagement.isHPCLR){
			String inscri="";
			String list=ClassManagement.ssh.shell("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+File.separator+"results/");
			try{
				String[] elem=list.split("\n");
				for(String e:elem){
					String pref=e.split("_chan1")[0];
					if(!(list.split(pref).length>0)){
						Job j=new HpclrJob();
						j.setStatus("succeed");
						j.setId(pref);
						res.add(j);
						inscri+="@@"+pref;
					}
				}
				return res;
			}catch(Exception e){ e.printStackTrace();}
		}else{
			if(ClassManagement.isLPTA){
				String[] poss=getPossibleJobs();
				for(String e:poss){
					String url=ClassManagement.ssh.shell("glite-wms-job-status -i "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/"+e);
					String[] ligne=url.split("\n");
					for(int i=0;i<ligne.length;i++){
						String[] s=ligne[i].split("ucce");
						if(s.length>1){
							Job j=new LptaJob();
							j.setStatus("succeed");
							j.setId(e.split(".jdl")[0]);
						}
					}
				}
				return res;
			}
		}
		return null;
	}

	/**
	 * Cancel every jobs running on the cluster for this session
	 */
	public static void cancelAllJobs() {
		if(ClassManagement.isLPTA){
			String[] jdl=getRunningJobs();
			for(int i=0;i<jdl.length;i++){
				cancelOneJob(jdl[i].substring(0, jdl[i].indexOf(".")));
			}
		}else{
			Set<String> jdl=ClassManagement.Session.getJobid().keySet();
			String joblist="";
			for(String e:jdl){
				joblist+=ClassManagement.Session.getIDForPref(e)+" ";
				//cancelOneJob(ClassManagement.Session.getIDForPref(e));
			}
			ClassManagement.LogsPanel.addText("Cancelling all jobs.");
			System.out.println("JOBLIST : \n"+joblist);
			ClassManagement.ssh.shell("llcancel "+joblist);
		}
	}
	
	/**
	 * Convert Image to BufferedImage
	 * @param image
	 * @return
	 */
	public static BufferedImage toBufferedImage(Image image) {
	    if (image instanceof BufferedImage) {
	        return (BufferedImage)image;
	    }

	    // This code ensures that all the pixels in the image are loaded
	    image = new ImageIcon(image).getImage();

	    // Determine if the image has transparent pixels; for this method's
	    // implementation, see Determining If an Image Has Transparent Pixels
	    boolean hasAlpha = false;

	    // Create a buffered image with a format that's compatible with the screen
	    BufferedImage bimage = null;
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    try {
	        // Determine the type of transparency of the new buffered image
	        int transparency = Transparency.OPAQUE;
	        if (hasAlpha) {
	            transparency = Transparency.BITMASK;
	        }

	        // Create the buffered image
	        GraphicsDevice gs = ge.getDefaultScreenDevice();
	        GraphicsConfiguration gc = gs.getDefaultConfiguration();
	        bimage = gc.createCompatibleImage(
	            image.getWidth(null), image.getHeight(null), transparency);
	    } catch (HeadlessException e) {
	        // The system does not have a screen
	    }

	    if (bimage == null) {
	        // Create a buffered image using the default color model
	        int type = BufferedImage.TYPE_INT_RGB;
	        if (hasAlpha) {
	            type = BufferedImage.TYPE_INT_ARGB;
	        }
	        bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
	    }

	    // Copy image to buffered image
	    Graphics g = bimage.createGraphics();

	    // Paint the image onto the buffered image
	    g.drawImage(image, 0, 0, null);
	    g.dispose();

	    return bimage;
	}

	/**
	 * Cancel specified job (for the image stack "nom")
	 * @param nom
	 */
	public static void cancelOneJob(String nom) {
		ClassManagement.LogsPanel.addText("Cancelling "+nom);
		if(ClassManagement.isLPTA){
			System.out.println(nom);
			String commande="glite-wms-job-cancel --noint -i "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/"+nom+".jid ";
			ClassManagement.ssh.shell(commande);
			System.out.println("rm "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/"+nom+".jid");
			ClassManagement.ssh.shell("rm "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/"+nom+".jid");
			ClassManagement.MainWindow.getTable().setValueAt("Cancelled", ClassManagement.MainWindow.getRowForDir().get(nom.substring(nom.indexOf("_"))), 1);
		}else{
			if(ClassManagement.isHPCLR){
				ClassManagement.ssh.shell("llcancel "+nom);
				String p=ClassManagement.Session.getPrefForID(nom);
				if(p!=null) ClassManagement.MainWindow.getTable().setValueAt("Cancelled", ClassManagement.MainWindow.getRowForDir().get(p), 1);
			}
		}
	}

	/**
	 * Retrieve list of running jobs
	 * @return
	 */
	public static String[] getRunningJobs() {
		String[] jdl=null;
		if(ClassManagement.isLPTA) jdl=ClassManagement.ssh.shell("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/*.jid").split("\n");
		else if(ClassManagement.isHPCLR){
			Set<String> s=ClassManagement.Session.getJobid().keySet();
			jdl=(String[])s.toArray();
		}
		return jdl;
	}

	
	/**
	 * Retrieve list of possible jobs
	 * @return
	 */
	public static String[] getPossibleJobs() {
		String[] jdl=null;
		if(ClassManagement.isLPTA) jdl=ClassManagement.ssh.shell("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/*.jdl").split("\n");
		else if(ClassManagement.isHPCLR){
			jdl=ClassManagement.ssh.shell("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/jobs/*.cmd").split("\n");
			for(int i=0;i<jdl.length;i++){
				jdl[i]=jdl[i].substring(jdl[i].lastIndexOf(File.separator));
			}
		}
		return jdl;
	}
	
	/**
	 * Retrieve list of possible jobs (prefixe)
	 * @return
	 */
	public static LinkedList<String> getPrefixeFromJDL() {
		String[] jdl=getPossibleJobs();
		LinkedList<String> list=new LinkedList<String>();
		for(int i=0;i<jdl.length;i++){
			jdl[i]=jdl[i].substring(0,jdl[i].lastIndexOf("."));
			list.add(jdl[i].split("_")[1]+"_"+jdl[i].split("_")[2]);
		}
		return list;
	}
	

	/**
	 * Launch failed jobs
	 */
	public static void launchFailed() {
		final Set<String> ks=ClassManagement.Session.getJobid().keySet();
		Thread monThread=new Thread(){
			public void run(){
				String status="";
				/*ClassManagement.done.clear();
				ClassManagement.failed.clear();
				ClassManagement.running.clear();
				ClassManagement.waiting.clear();
				for(String e:ks){
					System.out.println(e);
					status=FileParser.getStatus(e);
					ClassManagement.MainWindow.getTable().setValueAt(status, ClassManagement.MainWindow.getRowForDir().get(e), 1);
				}*/
				FileParser.getAllStatus();
				LinkedList<Job> failed=ClassManagement.failed;
				for(Job j:failed){
					submitOneJob(ClassManagement.jdlname+"_"+j.getPrefixe());
				}
			}
		};
		monThread.start();
	}
	
	/**
	 * Find purged jobs (LPTA only)
	 */
	public static void findPurgedJobs(){
		String[] res=ClassManagement.ssh.shell("ls "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"/results").split("\n");
		HashMap<Integer,Integer> liste=new HashMap<Integer,Integer>();
		for(int i=0;i<res.length;i++){
			if(res[i].split("results").length>1)
				liste.put(Integer.parseInt(res[i].split("_")[2]), Integer.parseInt(res[i].split("_")[2]));
		}
		String[] pj=FileParser.getPossibleJobs();
		if(ClassManagement.failed==null) ClassManagement.failed=new LinkedList<Job>();
		for(int i=1;i<=pj.length;i++){
			if(!liste.containsKey(i)) ClassManagement.failed.add(new Job(ClassManagement.jdlname+i));
		}
		launchFailed();
	}

	/**
	 * Retrieve jobs scripts sent to the cluster (LPTA only)
	 * @return
	 */
	public static ArrayList<String> retrieveScriptSent() {
		File f=new File(ClassManagement.installDir.getAbsoluteFile()+File.separator+ClassManagement.Session.getId()+File.separator+"" +
				"jobs");
		String[] list=f.list();
		ArrayList<String> array=new ArrayList<String>();
		if(list!=null){
			for(String e:list){
				try{
					String tmp=e.split("\\.")[0];
					array.add(tmp.split("_")[1]+"_"+tmp.split("_")[2]);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		return array;
	}

	/**
	 * Clear files on the cluster
	 */
	public static void clearRemoteDirectory() {
		ExecuteCommand ec=ClassManagement.ssh;
		String d=File.separator;
		ec.shell("rm "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+d+"archives/*");
		ec.shell("rm "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+d+"results/*");
		ec.shell("rm "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+d+"*session");
		if(ClassManagement.isHPCLR) ec.shell("rm "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+d+"jobs"+d+"*cmd");
		else if(ClassManagement.isLPTA) ec.shell("rm "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+d+"jobs"+d+"*jid");
	}
}
