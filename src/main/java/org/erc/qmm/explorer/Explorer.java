package org.erc.qmm.explorer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.SwingUtilities;

import org.erc.qmm.config.Queue;

import com.ibm.mq.MQException;

/**
 * The Class Explorer.
 */
public class Explorer {

	/** The queue. */
	private Queue queue;

	/** The depth. */
	private int depth;
	
	/** The listeners. */
	private List<MessageReadedListener> listeners;
	
	/**
	 * Instantiates a new explorer.
	 *
	 * @param queue the queue
	 */
	public Explorer (Queue queue){
		this.queue = queue;
		listeners =new ArrayList<MessageReadedListener>();
	}
	
	/**
	 * Read all.
	 *
	 * @return the list
	 */
	public List<JMQMessage> readAll(){
		List<JMQMessage> messages = new ArrayList<JMQMessage>();
		
		Properties connectionProperties = new Properties();
		connectionProperties.setProperty("HOST", queue.getHost());
		connectionProperties.setProperty("PORT", String.valueOf(queue.getPort()));
		connectionProperties.setProperty("CHANNEL", queue.getChannel());
		connectionProperties.setProperty("QMNAME", queue.getManager());
		
		//connectionProperties.setProperty("USER_ID", value);
		//connectionProperties.setProperty("PASSWORD", value);
		
		try {
			JMQConnection connection = new JMQConnection(connectionProperties);
			JMQManager manager = new JMQManager(queue.getManager(), connection);
			JMQQueue mqQueue = manager.getQueue(queue.getName());
			depth = mqQueue.getCurrentDepth();
			mqQueue.addMessageReadedListener(new MessageReadedListener() {
				public void messageReaded(final JMQMessage message) {
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								fireMessageReaded(message);
							}
						});
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			});
			mqQueue.refresh();
			messages = mqQueue.getAllJMQMessages();
		} catch (MQException e) {
			e.printStackTrace();
		}
		return messages;
	}
	
	/**
	 * Gets the depth.
	 *
	 * @return the depth
	 */
	public int getDepth(){
		return depth;
	}
	
    /**
     * Fire message readed.
     *
     * @param message the message
     */
    protected void fireMessageReaded(JMQMessage message){
    	for(MessageReadedListener listener: listeners){
    		listener.messageReaded(message);
    	}
    }
    
    /**
     * Adds the message readed listener.
     *
     * @param listener the listener
     */
    public void addMessageReadedListener(MessageReadedListener listener){
    	this.listeners.add(listener);
    }	
	
}
