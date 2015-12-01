package org.erc.qmm.mq;

import java.util.Hashtable;

import org.erc.qmm.config.QueueConfig;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;

/**
 * The Class MQUtils.
 */
public abstract class MQUtils {
	
	/**
	 * Builds the manager.
	 *
	 * @param config the config
	 * @return the MQ queue manager
	 * @throws MQException the MQ exception
	 */
	public static MQQueueManager buildManager(QueueConfig config) throws MQException{
		return buildManager(config.getHost(),config.getPort(),config.getChannel(), config.getManager());
	}
	
	/**
	 * Builds the manager.
	 *
	 * @param host the host
	 * @param port the port
	 * @param channel the channel
	 * @param manager the manager
	 * @return the MQ queue manager
	 * @throws MQException the MQ exception
	 */
	public static MQQueueManager buildManager(String host,int port,String channel,String manager) throws MQException{
		Hashtable<String,Object> mqProps = new Hashtable<String,Object>();

		if(host != null){
			mqProps.put("hostname", host);
		}
		if(port >0 ){
			mqProps.put("port",port);
		}
		if(channel != null){
			mqProps.put("channel", channel);
		}
		return new MQQueueManager(manager,mqProps);
	}
}
