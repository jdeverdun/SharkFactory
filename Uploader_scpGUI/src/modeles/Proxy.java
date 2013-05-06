package modeles;

/**
 * Proxy model
 * @author Jérémy DEVERDUN
 *
 */
public class Proxy {
	private String ip;
	private String port;

	/**
	 * 
	 */
	public Proxy(){
		ip="";
		port="";
	}
	
	/**
	 * Proxy with IP and Port
	 * @param i
	 * @param p
	 */
	public Proxy(String i, String p){
		ip=i;
		port=p;
	}
	/**
	 * 
	 * @param i
	 */
	public void setIp(String i){
		ip=i;
	}
	
	/**
	 * 
	 * @param p
	 */
	public void setPort(String p){
		port=p;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getIp(){
		return ip;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPort(){
		return port;
	}

}
