package Threads.monitoring;

import javax.swing.JProgressBar;

import Main.ClassManagement;


/**
 * Class to monitor local load (CPU, JAVA MEM, HD)
 * @author Jérémy DEVERDUN
 *
 */
public class CheckPerformance extends Thread{
	private JProgressBar cpu;
	private JProgressBar mem;
	private JProgressBar disk;
	
	/**
	 * HD load disable because of the time of calcul on endoc
	 */
	public void run() {
		boolean continu=true;
		PerformanceMonitor pm=new PerformanceMonitor();
		while(continu){
			  cpu.setValue((int)Math.round(pm.getCpuUsage()*100));
			  mem.setValue((int)Math.round(pm.getMemoryUsage()*100));
			  disk.setValue(0);
			  //if(ClassManagement.installDir.getAbsolutePath().split("endoc").length==0) 
				 // disk.setValue((int)Math.round(pm.getDiskUsage()*100));
			  try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param cpu
	 */
	public void setCpu(JProgressBar cpu) {
		this.cpu = cpu;
	}
	
	/**
	 * 
	 * @return
	 */
	public JProgressBar getCpu() {
		return cpu;
	}
	
	/**
	 * 
	 * @param mem
	 */
	public void setMem(JProgressBar mem) {
		this.mem = mem;
	}
	
	/**
	 * 
	 * @return
	 */
	public JProgressBar getMem() {
		return mem;
	}
	
	/**
	 * 
	 * @param disk
	 */
	public void setDisk(JProgressBar disk) {
		this.disk = disk;
	}
	
	/**
	 * 
	 * @return
	 */
	public JProgressBar getDisk() {
		return disk;
	}
}
