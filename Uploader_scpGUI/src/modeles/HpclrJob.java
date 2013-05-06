package modeles; 


/**
 * Model of HPC@LR job
 * @author Jérémy DEVERDUN
 *
 */
public class HpclrJob extends Job{
	public HpclrJob(){
		super();
	}

	
	/**
	 * 
	 * @param id
	 */
	public HpclrJob(String id) {
		super(id);
	}

	/**
	 * Convert short status of job to the full status
	 * @param status
	 * @return
	 */
	public static String StatusFromCode(String status) {
		if(status.equals("H")) return "pending";
		if(status.equals("R")) return "running";
		if(status.equals("I")) return "waiting";
		return "Unknow state";
	}
}
