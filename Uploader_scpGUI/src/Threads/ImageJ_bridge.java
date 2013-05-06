package Threads;
import ij.*;
import java.io.File;
import Main.ClassManagement;
import Main.MainViewer;

/**
 * Class interacting with IJ to do the preprocession
 * @author Jérémy DEVERDUN
 *
 */
public class ImageJ_bridge extends Thread{
	private File directory=new File("none");
	private MainViewer mv;
	private String chemin;
	private String tranche;
	private String localpath;
	private String savePath;
	public static boolean MEDFILT=false;
	
	/**
	 * 
	 */
	public ImageJ_bridge(){
		super();
	}
	
	/**
	 * 
	 * @param d
	 */
	public ImageJ_bridge(File d){
		super();
		directory=d;
	}
	
	/**
	 * Launch process for a folder
	 */
	public void run(){
		chemin=directory.getAbsolutePath();
		tranche=directory.getName();
		savePath=chemin;
		localpath=ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+File.separator+"images";
		mv=ClassManagement.MainWindow;
		if(ClassManagement.wholegland) wholeProcess();
		else partProcess();
	}
	
	/**
	 * IJ processing for isolated stacks
	 */
	private void partProcess() {
		savePath=directory.getAbsolutePath().substring(0,directory.getAbsolutePath().lastIndexOf(File.separator));
		String pref=directory.getName().substring(0, directory.getName().lastIndexOf("."));
		if(!mv.getCancelled().contains((String)pref)){
			ImagePlus imp=null;
			imp=new ImagePlus(savePath+File.separator+directory.getName());
			mv.getTable().setValueAt("Processing", mv.getRowForDir().get(pref), 1);
			ij.WindowManager.setTempCurrentImage(imp);
			File tmp=new File(savePath+File.separator+directory.getName());
			IJ.saveAs("Tiff", (localpath+File.separator+tmp.getName().substring(0, tmp.getName().lastIndexOf("."))+"_chan1_stack.tif"));
		    IJ.run("Bandpass Filter...", "filter_large=10 filter_small=3 suppress=None tolerance=5 autoscale saturate process");
		    ij.WindowManager.setTempCurrentImage(imp);
			IJ.run("RGB Color");
			IJ.saveAs("Tiff", savePath+File.separator+pref+"_chan1_stack_fft_rgb.tif");
			tmp=new File(savePath+File.separator+pref+"_chan1_stack_fft_rgb.tif");
			(new File(localpath,tmp.getName())).delete();
			boolean success = tmp.renameTo(new File(localpath,tmp.getName()));
		    if (!success) {
		        // File was not successfully moved
		    	ClassManagement.LogsPanel.addText("Error with "+tmp.getName());
		    }
		    imp=new ImagePlus(localpath+File.separator+pref+"_chan1_stack.tif");
		    ij.WindowManager.setTempCurrentImage(imp);
			//IJ.open(localpath+File.separator+tranche+"_chan"+channel+"_stack.tif");
			IJ.run("RGB Color");
			IJ.saveAs("Tiff", savePath+File.separator+pref+"_chan1_stack_rgb.tif");
			tmp=new File(savePath+File.separator+pref+"_chan1_stack_rgb.tif");
			(new File(localpath,tmp.getName())).delete();
			success = tmp.renameTo(new File(localpath,tmp.getName()));
		    if (!success) {
		        // File was not successfully moved
		    	ClassManagement.LogsPanel.addText("Error with "+tmp.getName());
		    }
			mv.getTable().setValueAt("Ready for tarring.", mv.getRowForDir().get(pref), 1);
			ClassManagement.LogsPanel.addText("Stack "+pref+" done.");
			imp.flush();
		}
	}
	
	/**
	 * IJ processing for whole gland
	 */
	private void wholeProcess() {
		if(!mv.getCancelled().contains((String)directory.getName())){
			int nbimg=directory.listFiles().length/2;
			if(directory.listFiles().length<4){
				mv.getTable().setValueAt("Skip (nbimg)", mv.getRowForDir().get(directory.getName()), 1);
				ClassManagement.LogsPanel.addText("Stack "+directory+" skip.");
				return;
			}
			mv.getTable().setValueAt("Processing", mv.getRowForDir().get(directory.getName()), 1);
			ImagePlus imp=null;
			for (int channel=1;channel<3;channel++){
				imp=null;
				mv.getTable().setValueAt("Processing chan "+channel, mv.getRowForDir().get(directory.getName()), 1);
				try{
					System.out.println(chemin+File.separator+tranche+"_z01c"+channel+".TIF");
					imp=new ImagePlus(chemin+File.separator+tranche+"_z01c"+channel+".TIF");
					if(imp.getBitDepth()!=8){
						ij.WindowManager.setTempCurrentImage(imp);
						IJ.run("8-bit");
						ClassManagement.LogsPanel.addText("It's not an 8 bit image!");
					}
					IJ.save(imp, savePath+File.separator+tranche+"_chan"+channel+"_stack.tif");
				}catch(Exception e){
					e.printStackTrace();
				}
				System.out.println(""+(directory.listFiles().length-2));
				ImageStack ims=imp.createEmptyStack();
				ims.addSlice(null, imp.getProcessor(), 0);
				for(int i=2;i<=nbimg;i++){
					try{
						if(i<10){
							imp=new ImagePlus(chemin+File.separator+tranche+"_z0"+i+"c"+channel+".TIF");
						}else{
							imp=new ImagePlus(chemin+File.separator+tranche+"_z"+i+"c"+channel+".TIF");
						}
						if(imp.getBitDepth()!=8){
							ij.WindowManager.setTempCurrentImage(imp);
							IJ.run("8-bit");
						}
						ims.addSlice(null, imp.getProcessor(), i-1);
					}catch(Exception e){
						break;
					}
				}
				imp.setStack("toto",ims);
				ij.WindowManager.setTempCurrentImage(imp);
				if(MEDFILT) IJ.run("Median...", "radius=2 stack");
				IJ.saveAs("Tiff", savePath+File.separator+tranche+"_chan"+channel+"_stack.tif");
				File tmp=new File(savePath+File.separator+tranche+"_chan"+channel+"_stack.tif");
				(new File(localpath,tmp.getName())).delete();
				boolean success = tmp.renameTo(new File(localpath+"/"+tmp.getName()));
			    if (!success) {
			        // File was not successfully moved
			    	ClassManagement.LogsPanel.addText("Error with "+tmp.getName());
			    }
			    IJ.run("Bandpass Filter...", "filter_large=10 filter_small=3 suppress=None tolerance=5 autoscale saturate process");
			    ij.WindowManager.setTempCurrentImage(imp);
				IJ.run("RGB Color");
				IJ.saveAs("Tiff", savePath+File.separator+tranche+"_chan"+channel+"_stack_fft_rgb.tif");
				tmp=new File(savePath+File.separator+tranche+"_chan"+channel+"_stack_fft_rgb.tif");
				(new File(localpath,tmp.getName())).delete();
				success = tmp.renameTo(new File(localpath,tmp.getName()));
			    if (!success) {
			        // File was not successfully moved
			    	ClassManagement.LogsPanel.addText("Error with "+tmp.getName());
			    }
			    if(channel==1){
			    	imp=new ImagePlus(localpath+File.separator+tranche+"_chan"+channel+"_stack.tif");
				    ij.WindowManager.setTempCurrentImage(imp);
					IJ.run("RGB Color");
					IJ.saveAs("Tiff", savePath+File.separator+tranche+"_chan"+channel+"_stack_rgb.tif");
					tmp=new File(savePath+File.separator+tranche+"_chan"+channel+"_stack_rgb.tif");
					(new File(localpath,tmp.getName())).delete();
					success = tmp.renameTo(new File(localpath,tmp.getName()));
				    if (!success) {
				        // File was not successfully moved
				    	ClassManagement.LogsPanel.addText("Error with "+tmp.getName());
				    }
			    }
			    success = (new File(localpath,tranche+"_chan"+channel+"_stack.tif")).delete();
			    if (!success) {
			        // File was not successfully moved
			    	ClassManagement.LogsPanel.addText("Error with "+tmp.getName());
			    }
			}
			mv.getTable().setValueAt("Ready for tarring.", mv.getRowForDir().get(directory.getName()), 1);
			ClassManagement.LogsPanel.addText("Stack "+directory+" done.");
			imp.flush();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public File getDirectory() {
		return directory;
	}
	
	/**
	 * 
	 * @param directory
	 */
	public void setDirectory(File directory) {
		this.directory = directory;
	}
}
