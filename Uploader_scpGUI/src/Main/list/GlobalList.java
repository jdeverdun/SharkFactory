package Main.list;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Generic list
 * Describes attributes of other lists
 * @author Jérémy DEVERDUN
 *
 */
public abstract class GlobalList {
	protected LinkedList<String> EnAttente;
	protected ArrayList<String> finish;
	protected String EnCours;
	protected boolean isPaused=false; 
	
	/**
	 * 
	 */
	public GlobalList(){
		EnAttente=new LinkedList<String>();
		finish=new ArrayList<String>();
		EnCours="";
	}
	
	public abstract void LoadNext();
	public abstract void removeFromWaitingList(String v);
	public abstract void resetStatut();
	public abstract void removeAllWaitingList();
	
	public void setPaused(boolean b) {
		isPaused=b;
	}
}
