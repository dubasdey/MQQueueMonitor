package org.erc.qmm.explorer;

import java.util.Hashtable;
import java.util.Properties;

public class JMQConnection {

    private Hashtable<String,Object> mqProps;

    public JMQConnection(Properties properties) {
        if(properties != null) {
            buildMQProps(properties);
        }
    }

    private void buildMQProps(Properties properties) {
        if(properties == null) {
            mqProps = null;
        }else{
	        mqProps = new Hashtable<String,Object>();
	        if(properties.getProperty("HOST") != null){
	            mqProps.put("hostname", properties.getProperty("HOST"));
	        }
	        if(properties.getProperty("PORT") != null && !properties.getProperty("PORT").equals("")){
	            mqProps.put("port", new Integer(Integer.parseInt(properties.getProperty("PORT"))));
	        }
	        if(properties.getProperty("CHANNEL") != null){
	            mqProps.put("channel", properties.getProperty("CHANNEL"));
	        }
	        if(properties.getProperty("USER_ID") != null){
	            mqProps.put("userID", properties.getProperty("USER_ID"));
	        }
	        if(properties.getProperty("PASSWORD") != null){
	            mqProps.put("password", properties.getProperty("PASSWORD"));
	        }
        }
    }

    public Hashtable<String,Object> getProperties() {
        return mqProps;
    }

}