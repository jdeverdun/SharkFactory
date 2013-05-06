package modeles;

import java.lang.reflect.Field;


/**
 * Model of generic cluster
 * @author Jérémy DEVERDUN
 *
 */
public class Cluster {
	private int id;
	private String name;
	private String ip;
	private String user;
	private String remote_directory;
	
	/**
	 * 
	 * @param idt Cluster ID
	 * @param n Name of the cluster
	 * @param i IP of the cluster
	 * @param u User of the cluster
	 * @param r Remote directory in the cluster
	 */
	public Cluster(int idt,String n,String i,String u,String r){
		this.setID(idt);
		this.setName(n);
		this.setIp(i);
		this.setUser(u);
		this.setRemoteDirectory(r);
	}
	
	/**
	 * Instanciate cluster from a file
	 * @param txt
	 */
	public Cluster(String txt){
		String[] row=txt.split("\n");
		for(int i=1;i<row.length;i++){
			String[] champ=row[i].split("=");
			switch(i){
				case 1:
					try{
						this.setID(Integer.parseInt(champ[1]));
					}catch(Exception e){
						this.setID(0);
					};break;
				case 2:
					this.setName(champ[1]);
					break;
				case 3:
					this.setIp(champ[1]);
					break;
				case 4:
					this.setUser(champ[1]);
					break;
				case 5:
					this.setRemoteDirectory(champ[1]);
					break;
					
			}

		}
	}

	/**
	 * 
	 * @param pass
	 */
	public void setRemoteDirectory(String pass) {
		this.remote_directory = pass;
	}

	/**
	 * 
	 * @return
	 */
	public String getRemoteDirectory() {
		return remote_directory;
	}

	
	/**
	 * 
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * 
	 * @return
	 */
	public String getUser() {
		return user;
	}

	/**
	 * 
	 * @param ip
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	
	/**
	 * 
	 * @return
	 */
	public String getIp() {
		return ip;
	}
	
	/**
	 * 
	 * @param n
	 */
	public void setName(String n) {
		this.name = n;
	}

	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * 
	 * @param iD
	 */
	public void setID(int iD) {
		id = iD;
	}

	
	/**
	 * 
	 * @return
	 */
	public int getID() {
		return id;
	}

	
	/**
	 * Info on the cluster
	 */
	public String toString(){
		String res="id="+this.id+"\n" +
				"name="+this.name+"\n" +
				"hostname="+this.ip+"\n" +
				"username="+this.user+"\n" +
				"remote="+this.remote_directory+"\n";
		
		return res;
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public boolean equals(Cluster c){
		if(c==null) return false;
		return this.id==c.getID();
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public boolean equals(int c){
		if(c==0) return false;
		return this.id==c;
	}
}
