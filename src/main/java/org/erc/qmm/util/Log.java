package org.erc.qmm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Log {

	private static final String loggerName ="./config/logging.properties";
	
	
	private Logger log;
	
	private Log(String cname) {
		log = Logger.getLogger( cname );
	} 
	
	public static Log getLog(Class<?> c){
		return new Log(c.getName());
	}
	
	public static void configureLogger(){
		File configLogFile = new File(loggerName);
		if (configLogFile.exists()){
			try {
				LogManager.getLogManager().readConfiguration(new FileInputStream(loggerName));
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void debug(String str){
		log.log(Level.FINE, str);
	}
	
	public void debug(String str,Object... params){
		if(log.isLoggable(Level.FINE)){
			if(params!=null){
				str = MessageFormat.format(str, params);
			}
			log.log(Level.FINE, str);
		}
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
