package modeles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenuItem;

import com.Matlab.server.MatlabServer;
import com.java.MatlabClient;
import com.java.io.FunctionParser;
import com.java.modeles.ImageWorker;
import com.java.modeles.Worker;

import IO.FileParser;
import Main.ClassManagement;
import Main.display.MatlabFunctionCall;

/**
 * Class to send command to matlabengine
 * @author Jérémy DEVERDUN
 *
 */
public class MatlabFunction extends JMenuItem {
	private File mfile;
	private String[][] argin;
	private boolean isImageFun;
	private String function;
	private boolean iscancel=false;
	private String[][] argout;


	/**
	 * 
	 */
	public MatlabFunction(){
		super();
	}
	
	/**
	 * 
	 * @param s
	 */
	public MatlabFunction(String s){
		super(s);
	}
	
	/**
	 * 
	 * @param s function
	 * @param f file of the function
	 */
	public MatlabFunction(String s, File f){
		super(s);
		this.setFunction(s);
		this.setMfile(f);
		this.setArgin(null);
		this.setImageFun(false);
		setVarFromFile(f);
		final MatlabFunction cur=this;
		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Thread monThread=new Thread(){
					public void run(){
						setVarFromFile(mfile);
						function=FunctionParser.buildFunction(cur.mfile);
						if(!ClassManagement.MatlabServerIsRunning){
							ClassManagement.LogsPanel.addText("Loading matlabengine");
							MatlabServer ms=new MatlabServer(ClassManagement.installDir.getAbsolutePath());//ClassManagement.matlabserverDirectory);
							ClassManagement.matlabserver=ms;
							ms.start();
							ClassManagement.MatlabServerIsRunning=true;
							try {
								Thread.sleep(10000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						String[][] tmp=argin;
						MatlabFunctionCall mfcall=new MatlabFunctionCall(argin,cur);
						mfcall.createAndShowGUI();
						while(mfcall.isShowing()){
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
						if(!iscancel){
							isImageFun=false;
							if(argin!=null){
								for(String[] e:argin){
									if(e[1].equals("image")){
										isImageFun=true;
										break;
									}
								}
							}
							System.out.println(function);
							MatlabClient mc=new MatlabClient();
							if(isImageFun){
								ImageWorker iw=new ImageWorker(function,getFormattedArgs(),getFormattedArgout(),ClassManagement.currentImage);
								mc.sendImageCommand(iw);
							}else{
								Worker w=new Worker(function,getFormattedArgs(),getFormattedArgout());
								mc.sendSimpleCommand(w);
							}
							mc.close();
						}
						argin=tmp;
					}
				};
				monThread.start();
			}


		});
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isCancel() {
		return iscancel;
	}
	
	/**
	 * 
	 * @param iscancel
	 */
	public void setIscancel(boolean iscancel) {
		this.iscancel = iscancel;
	}
	
	/**
	 * Format the argin to be understood by MATLAB
	 * @return
	 */
	private String[] getFormattedArgs() {
		if(argin==null) return new String[]{""};
		String[] res=new String[argin.length];
		int count=0;
		for(String[] e:argin){
			if(e[1].equals("string") || e[1].equals("file") || e[1].equals("select") ){
				res[count++]="'"+e[0]+"'";
			}else{
				res[count++]=e[0];
			}
			System.out.println(e[1]+"-"+res[count-1]);
		}
		return res;
	}
	
	/**
	 * Format the argout to be understood by MATLAB
	 * @return
	 */
	private String[] getFormattedArgout() {
		if(argout==null) return new String[]{""};
		String[] res=new String[argout.length];
		int count=0;
		for(String[] e:argout){
			res[count++]=""+e[0]+"";
		}
		return res;
	}
	
	/**
	 * Set argin and argout from the mfile "f"
	 * @param f
	 */
	public void setVarFromFile(File f){
		String txt=FileParser.getTextFromFile(f.getAbsolutePath());
		String[] lines=txt.split("\n");
		String header=lines[1].substring(1);
		String[] args=lines[2].substring(1).split("@@");
		String[] argouttmp=lines[3].substring(1).split("@@");
		if(lines[2].split("none").length>1){
			this.argin=null;
		}else{
			this.argin=new String[args.length-2][2];
			for(int i=1;i<args.length-1;i++){
				String[] elem=args[i].split(":");
				this.argin[i-1][0]=elem[1];
				this.argin[i-1][1]=elem[0];
			}
		}
		if(lines[3].split("none").length>1){
			this.argout=null;
		}else{
			this.argout=new String[argouttmp.length-2][2];
			for(int i=1;i<argouttmp.length-1;i++){
				String[] elem=argouttmp[i].split(":");			
				this.argout[i-1][0]=elem[1];
				this.argout[i-1][1]=elem[0];
			}
		}
		this.setToolTipText(header);
		
	}
	
	/**
	 * 
	 * @return
	 */
	public String[][] getArgout() {
		return argout;
	}
	
	/**
	 * 
	 * @param argout
	 */
	public void setArgout(String[][] argout) {
		this.argout = argout;
	}
	
	/**
	 * 
	 * @param mfile
	 */
	public void setMfile(File mfile) {
		this.mfile = mfile;
	}
	
	/**
	 * 
	 * @return
	 */
	public File getMfile() {
		return mfile;
	}
	
	/**
	 * 
	 * @param argin
	 */
	public void setArgin(String[][] argin) {
		this.argin = argin;
	}
	
	/**
	 * 
	 * @return
	 */
	public String[][] getArgin() {
		return this.argin;
	}
	
	/**
	 * 
	 * @param isImageFun
	 */
	public void setImageFun(boolean isImageFun) {
		this.isImageFun = isImageFun;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isImageFun() {
		return isImageFun;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFunction() {
		return function;
	}
	
	/**
	 * 
	 * @param function
	 */
	public void setFunction(String function) {
		this.function = function;
	}
}
