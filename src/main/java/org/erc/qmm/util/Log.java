package org.erc.qmm.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

	private Logger log;
	
	private Log(String cname) {
		log = Logger.getLogger( cname );
	} 
	
	public static Log getLog(Class<?> c){
		return new Log(c.getName());
	}
	
	public void debug(String str){
		log.log(Level.FINE, str);
	}
	
	public void error(String str){
		log.log(Level.SEVERE, str);
	}
	
	public void error(String str,Throwable t){
		log.log(Level.SEVERE, str,t);
	}
	
	public void error(Throwable t){
		log.log(Level.SEVERE, t.getMessage(),t);
	}		
}
