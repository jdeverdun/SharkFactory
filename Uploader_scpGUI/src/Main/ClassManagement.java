package Main;

import java.io.File;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.LinkedList;

import com.Matlab.server.MatlabServer;
import com.java.modeles.ImagePlusExtended;


import modeles.Cluster;
import modeles.ClusterList;
import modeles.Job;

import IO.FileParser;
import Main.display.JLogEditorPane;
import Main.display.Terminal;
import Main.list.TarList;
import Main.list.UploadList;
import Threads.ExecuteCommand;

/**
 * Bridge between all classes 
 * global variable
 * @author Jérémy DEVERDUN
 *
 */
public class ClassManagement {
	public static final boolean DEBUG = true;
	public static int MAXMEMORY = 10000;
	public static String matlabserverDirectory = "C:\\Users\\Mobilette\\Documents\\Stage2010-2011\\MATLAB mcode\\programme\\nuclei_segmentation\\etape_3\\daemon\\matlabengine\\distrib\\matlabengine.exe";
	public static boolean isnewsession = true;
	public static int IJmaxThreads = 2;
	public static modeles.Session Session = null;
	public static MainViewer MainWindow=null;
	public static JLogEditorPane LogsPanel=null;
	public static Terminal Terminal=null;
	public static String pathsave="C:\\Users\\mobilette\\Desktop\\";
	public static Proxy PROXY=null;
	public static int downloadcount=0;
	public static boolean Uploading=false;
	public static UploadList listDownload=null;
	public static String user="molinof";
	public static String pass="33dpsdt";
	public static String host="login.hpc-lr.univ-montp2.fr";
	public static String[] extension={"_chan1_stack_rgb.tif","_chan1_stack_fft_rgb.tif","_chan2_stack_fft_rgb.tif"};
	public static boolean step1=false;
	public static String lfn_file_list="lfn_list.txt";
	public static String guid_file_list="srm_list.txt";
	public static String vo="vo.msfg.fr";
	public static int nbofjobs=6;
	public static boolean archiving=false;
	public static String srm_url="srm_url.txt";
	public static boolean full=false;
	public static String jdlname="uploadedjob";
	public static LinkedList<Job> done=new LinkedList<Job>();
	public static LinkedList<Job> failed=new LinkedList<Job>();
	public static LinkedList<Job> running=new LinkedList<Job>();
	public static LinkedList<Job> waiting=new LinkedList<Job>();
	public static ClusterList<Cluster> clusterlist;
	public static ExecuteCommand ssh=null;
	public static boolean isHPCLR=true;
	public static boolean isLPTA=false;
	public static File installDir=null;
	public static TarList listTar=null;
	public static boolean suspend=true;
	public static boolean killAllThread=false;
	public static String type_cluster="";
	public static ArrayList<String> jobscriptsent=null;
	public static boolean wholegland=true;
	public static boolean MatlabServerIsRunning=false;
	public static MatlabServer matlabserver=null;
	public static ImagePlusExtended currentImage=null;
	
	
	
	/**
	 * Save configuration
	 */
	public static void saveConf(){
		String conf="";
		if(isHPCLR){
			conf+="type_cluster=CMD\n";
		}else{
			if(isLPTA){
    			conf+="type_cluster=JDL\n";
			}
		}
		conf+="defaultcluster="+ClassManagement.clusterlist.getdefaultCluster().getID()+"\n";
		conf+="jdlname="+ClassManagement.jdlname+"\n";
		conf+="maxmemory="+ClassManagement.MAXMEMORY+"\n";
		conf+="vo="+ClassManagement.vo+"\n";
		conf+="lfnfile="+ClassManagement.lfn_file_list+"\n";
		conf+="srm_url="+ClassManagement.srm_url+"\n";
		conf+="guidfile="+ClassManagement.guid_file_list+"\n";
		conf+="maxthread="+ClassManagement.IJmaxThreads+"\n";
		FileParser.writeText(conf, ClassManagement.installDir+"/params.conf");
	}



}
