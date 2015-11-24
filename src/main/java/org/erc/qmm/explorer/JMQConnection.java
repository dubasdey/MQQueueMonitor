package org.erc.qmm.explorer;

import java.util.Hashtable;
import java.util.Properties;

/**
 * The Class JMQConnection.
 */
public class JMQConnection {

    /** The mq props. */
    private Hashtable<String,Object> mqProps;

    /**
     * Instantiates a new JMQ connection.
     *
     * @param properties the properties
     */
    public JMQConnection(Properties properties) {
        if(properties != null) {
            buildMQProps(properties);
        }
    }

    /**
     * Builds the mq props.
     *
     * @param properties the properties
     */
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

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public Hashtable<String,Object> getProperties() {
        return mqProps;
    }

}