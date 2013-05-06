package modeles;

import java.util.ArrayList;

import IO.FileParser;
import Main.ClassManagement;


/**
 * List which contains only clusters
 * @author Jérémy DEVERDUN
 *
 * @param <Cluster>
 */
public class ClusterList<Cluster> extends ArrayList<Cluster>{
	private Cluster defaultCluster;
	
	/**
	 * 
	 */
 	public ClusterList(){
		super();
		this.defaultCluster=null;
	}
 	
 	/**
 	 * 
 	 * @param d
 	 */
 	public ClusterList(Cluster d){
		super();
		this.defaultCluster=d;
	}
 	
 	/**
 	 * info on the clusters contains in the list
 	 */
	public String toString(){
		String res="";
		for(Cluster c:this){
			res+="#\n"+c.toString();
		}
		return res;
	}
	
	/**
	 * Save cluster list to a file
	 * @param n
	 */
	public void save(String n){
		FileParser.writeText(this.toString(), n);
	}
	
	/**
	 * Set default cluster
	 * @param c
	 */
	public void setdefaultCluster(Cluster c) {
		this.defaultCluster = c;
	}
	
	/**
	 * 
	 * @return
	 */
	public Cluster getdefaultCluster() {
		return defaultCluster;
	}
}
