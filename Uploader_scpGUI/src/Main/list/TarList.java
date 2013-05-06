package Main.list;

import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import IO.FileParser;
import IO.HTMLparser;
import IO.TarParser;
import Main.ClassManagement;


/**
 * List files that need to be archived
 * @author Jérémy DEVERDUN
 *
 */
public class TarList extends GlobalList{
	private int num;
	
	/**
	 * 
	 */
	public TarList() {
		super();
		ClassManagement.listTar=this;
		num=1;
	}

	/**
	 * Add file to tar list
	 * @param u
	 */
	public void add(String u) {
		if(ClassManagement.archiving){
			if(EnAttente.size()<1000) EnAttente.add(u);
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
			launchTar(u);
		}
	}

	/**
	 * 
	 */
	@Override
	public void LoadNext() {
		EnCours="";
		if(!EnAttente.isEmpty()){
			EnCours=EnAttente.peek();
			HTMLparser.NBESSAI=0;
			launchTar(EnAttente.poll());
		}
	}

	/**
	 * Remove file from waiting
	 */
	@Override
	public void removeFromWaitingList(String v) {
		// TODO Auto-generated method stub
		if(!EnAttente.isEmpty()){
			EnAttente.contains(v);
			Iterator<String> it=EnAttente.iterator();
			int count=0;
			while(it.hasNext()){
				String e=it.next();
				if(e.equals(v)){ EnAttente.remove(count);break;}
				count++;
			}
		}
	}

	@Override
	public void resetStatut() {	}

	
	/**
	 * Clear all waiting list
	 */
	@Override
	public void removeAllWaitingList() {
		EnAttente.clear();
		EnCours="";
		num=1;
	}
	
	/**
	 * Test if the waiting list is empty
	 * @return
	 */
	public boolean isEmpty(){
		return EnAttente.isEmpty();
	}
	
	/**
	 * Launch the archiving
	 * @param u
	 */
	public void launchTar(String u) {
		while(isPaused){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		TarParser.tarLocal(u);
		ClassManagement.MainWindow.getTarprogressBar().setValue(ClassManagement.MainWindow.getTarprogressBar().getValue()+1);
	}
}
