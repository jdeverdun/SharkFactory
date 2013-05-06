package IO;

import ij.IJ;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import Main.ClassManagement;
import Main.display.LoadingFrame;

/**
 * Archiving class
 * @author Jérémy DEVERDUN
 *
 */
public class TarParser {
	public static Thread threadTar;
	private static final int BUFFER_SIZE = 1024;
	
	/**
	 * Archiving files listed on filePaths in saveAs
	 * @param filePaths
	 * @param saveAs
	 * @return
	 * @throws Exception
	 */
	public static File createTarFile(String[] filePaths, String saveAs) throws Exception{
		OutputStream out;
		try{
		    File tarFile = new File(saveAs);
		    out = new FileOutputStream(tarFile);
		    
		    TarArchiveOutputStream aos = (TarArchiveOutputStream) new ArchiveStreamFactory().createArchiveOutputStream("tar", out);
		    aos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
		    for(String filePath : filePaths){
		        File file = new File(filePath);
		        TarArchiveEntry entry = new TarArchiveEntry(file.getName());
		        entry.setSize(file.length());
		        aos.putArchiveEntry(entry);
		        IOUtils.copy(new FileInputStream(file), aos);
		        aos.closeArchiveEntry();
		    }
		    aos.finish();
		    aos.close();
		    out.close();
		    return new File(saveAs);
		}catch(Exception e){
			
			return null;
		}


	    
	}
	
	/**
	 * Create tar.gz archive
	 * @param ins
	 * @param o
	 */
	public static void createTarGz(String[] ins, String o) {
		File out=new File(o);
        check(ins);
        TarArchiveOutputStream taos = null;
        try {
            taos = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(out)));
            addFilesToArchive(ins, taos);
        } catch (IOException ioe) {
        	System.out.println(ioe);
        } finally {
            closeQuietly(taos);
        }
    }

	/**
	 * 
	 * @param ins
	 */
    private static void check(String[] ins) {
        for (String f : ins) {
        	File file=new File(f);
            if (!file.canRead()) {
                System.out.println("The file \"" + file.getName() + "\" cannot be read.");
            }
        }
    }

    /**
     * Add files to selected archive
     * @param ins
     * @param tos
     * @throws IOException
     */
    private static void addFilesToArchive(String[] ins, TarArchiveOutputStream tos) throws IOException {
        for (String in : ins) {
        	System.out.println(in);
            addFileToArchive(new File(in), tos);
        }
    }

    /**
     * Add one file to selected archive
     * @param in
     * @param out
     * @throws IOException
     */
    private static void addFileToArchive(File in, TarArchiveOutputStream out) throws IOException {
        TarArchiveEntry entry = new TarArchiveEntry(in.getName());
        entry.setSize(in.length());
        out.putArchiveEntry(entry);
        copy(new FileInputStream(in), out);
        out.closeArchiveEntry();
    }

    /**
     * 
     * @param in
     * @param out
     * @throws IOException
     */
    private static void copy(InputStream in, OutputStream out) throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = in.read(buffer))) {
                out.write(buffer, 0, n);
            }
        } finally {
            closeQuietly(in);
        }
    }

    /**
     * 
     * @param in
     */
    private static void closeQuietly(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ioe) {
                // Silent catch
            }
        }
    }

    /**
     * 
     * @param out
     */
    private static void closeQuietly(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException ioe) {
                // Silent catch
            }
        }
    }
    
    /**
     * Became useless
     * @return
     */
	public static boolean tarByJobCluster(){
		ClassManagement.archiving=true;
		final LoadingFrame lf=new LoadingFrame("Archiving, please wait..",false);
		Thread monThread = new Thread() {
	         public void run() {
				String[] files=(new File(ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+"/images")).list();//listefichiers.split("\n");
				HashMap<String,String> prefixe=new HashMap<String,String>();
				for(int i=0;i<files.length;i++){
					String p=files[i].split("/")[files[i].split("/").length-1];
					prefixe.put(p.split("_")[0]+"_"+p.split("_")[1], p.split("_")[0]+"_"+p.split("_")[1]);
				}
				int imByJob=1;
				if(ClassManagement.nbofjobs==0) imByJob=1;
				else imByJob=(int) Math.ceil((double)prefixe.size()/(double)ClassManagement.nbofjobs);
				ClassManagement.LogsPanel.addText(imByJob+" groups of images will be integrated in each archive.");
				int suffixe=1;
				String toTar="tar -zcvf "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"archive2SE/archive_images_"+suffixe+".tar.gz -C "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"images/";
				Set<String> keys=prefixe.keySet();
				int count=0;
				String[] extension=ClassManagement.extension;
				int count2=1;
				int l=keys.size();
				int c=0;
				for(String e:keys){//int i=0;i<prefixe.size();i++){
					c++;
					for(int i=0;i<extension.length;i++)
						toTar=toTar+" "+e+extension[i];
					if((count==imByJob-1 || imByJob==1) && count2<ClassManagement.nbofjobs){
						count2++;
						suffixe++;
						count=0;
						System.out.println(toTar+" &");
						ClassManagement.ssh.shell(toTar);
						ClassManagement.LogsPanel.addText(toTar);
						toTar="tar -zcvf "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"archive2SE/archive_images_"+suffixe+".tar.gz -C "+ClassManagement.clusterlist.getdefaultCluster().getRemoteDirectory()+"images/";
					}else{
						count++;
					}
					lf.setStatus((c/l)*100);
				}
				System.out.println(toTar +" &");
				ClassManagement.LogsPanel.addText(toTar);
				ClassManagement.ssh.shell(toTar);
				ClassManagement.archiving=false;
				lf.dispose();
				//this.stop();
	         }
		};
		monThread.start();
		return true;
	}
	
	
	/**
	 * Archiving local images
	 * @param p
	 * @return
	 */
	public static boolean tarLocal(String p){
		final String pref=p;
		ClassManagement.archiving=true;
		if((ClassManagement.jobscriptsent.contains(pref))){
			ClassManagement.archiving=false;
			String archivename=ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+File.separator+"archives"+File.separator+"archive_images_"+pref+".tar.gz";
			ClassManagement.MainWindow.getTable().setValueAt("Ready for upload", ClassManagement.MainWindow.getRowForDir().get(pref), 1);
			ClassManagement.MainWindow.getUploadList().add(archivename,pref);
			return true;
		}
		threadTar = new Thread() {
	         public void run() {
	        	ClassManagement.MainWindow.getTable().setValueAt("Tarring", ClassManagement.MainWindow.getRowForDir().get(pref), 1);
	        	String localdir=ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+File.separator+"images";
				String[] extension=ClassManagement.extension;
				String[] totar=null;
				if(ClassManagement.wholegland) totar=new String[extension.length];
				else totar=new String[extension.length-1];
				String archivename = "";
				archivename=ClassManagement.installDir+File.separator+ClassManagement.Session.getId()+File.separator+"archives"+File.separator+"archive_images_"+pref+".tar.gz";
				if(!(ClassManagement.jobscriptsent.contains(pref))){
					for(int f=0;f<extension.length;f++) 
						if(ClassManagement.wholegland || (!ClassManagement.wholegland && f<(extension.length-1))){
							totar[f]=(localdir+File.separator+pref+extension[f]);
						}
					try {
						createTarGz(totar, archivename);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				ClassManagement.archiving=false;
				ClassManagement.MainWindow.getTable().setValueAt("Ready for upload", ClassManagement.MainWindow.getRowForDir().get(pref), 1);
				ClassManagement.MainWindow.getUploadList().add(archivename,pref);
	         }
		};
		threadTar.setPriority(Thread.MAX_PRIORITY);
		threadTar.start();
		return true;
	}

}
