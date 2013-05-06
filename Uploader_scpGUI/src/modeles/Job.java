package modeles;


/**
 * Generic job model
 * @author Jérémy DEVERDUN
 *
 */
public class Job {
	private String url;
	private String status;
	private String id;
	private String prefixe;
	private Cluster cluster;
	
	
	/**
	 * 
	 */
	public Job(){
		this.url="";
		this.status="unknow";
		this.id="unknow";
		this.setCluster(null);
	}
	
	/**
	 * ID, URL, Status, Cluster 
	 * @param i
	 * @param u
	 * @param s
	 * @param c
	 */
	public Job(String i,String u,String s,Cluster c){
		this.setUrl(u);
		this.setId(i);
		this.setStatus(s);
		this.setCluster(c);
	}
	
	/**
	 * 
	 * @param i
	 * @param u
	 * @param s
	 */
	public Job(String i,String u,String s){
		this.setUrl(u);
		this.setId(i);
		this.setStatus(s);
	}
	
	/**
	 * 
	 * @param i
	 * @param u
	 */
	public Job(String i,String u){
		this.setUrl(u);
		this.setId(i);
		this.status="unknow";
	}
	
	/**
	 * 
	 * @param i
	 */
	public Job(String i){
		this.setId(i);
		this.url="";
		this.status="unknow";
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	
	/**
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		if(status.equals("running") || status.equals("pending") || status.equals("succeed") || status.equals("failed"))
			this.status = status;
		else
			this.status = "unknow";
		
	}
	
	/**
	 * 
	 * @param cluster
	 */
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	
	/**
	 * 
	 * @return
	 */
	public Cluster getCluster() {
		return cluster;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	
	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	
	/**
	 * Useless ATM
	 * @return
	 */
	public String getIcon(){
		if(this.getStatus().equals("running"))
			return ClassLoader.getSystemResource("images/loading.gif").getPath();
		else
			if(this.getStatus().equals("pending"))
				return ClassLoader.getSystemResource("images/pending.png").getPath();
			else
				if(this.getStatus().equals("succeed"))
					return ClassLoader.getSystemResource("images/done_succeed.png").getPath();
				else
					if(this.getStatus().equals("failed"))
						return ClassLoader.getSystemResource("images/done_failed.png").getPath();
		return "";
	}
	
	
	/**
	 * Convert status to HTML code
	 * Note : Useless ATM
	 * @return
	 */
	public String toHTML(){
		String html="<tr><td class=\"off\" align=\"center\">"+this.getId()+"" +
				"</td><td class=\"off\" align=\"center\"><img src=\""+getIcon()+"\" alt="+getIcon()+" vspace=\"20\" width=\"291\" height=\"41\"></td>" +
						"        <td class=\"off\" align=\"center\"><a href=\""+getUrl()+"\"><img src=\""+ClassLoader.getSystemResource("images/Traveler.png").getPath()+"\" alt=\"more\" width=\"30\" height=\"30\"></a></td>" +
								"      </tr>";
		return html;
	}
	
	/**
	 * 
	 * @param prefixe
	 */
	public void setPrefixe(String prefixe) {
		this.prefixe = prefixe;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPrefixe() {
		return prefixe;
	}
	@Override 
	public boolean equals(Object o) {
		return this.prefixe.equals((String)((Job)o).getPrefixe());
	}

}
