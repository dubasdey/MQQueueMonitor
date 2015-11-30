package org.erc.qmm.explorer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.erc.qmm.mq.JMQMessage;
import org.erc.qmm.mq.JMQQueue;
import org.erc.qmm.mq.MessageReadedListener;
import org.erc.qmm.util.Log;

import com.ibm.mq.MQException;

/**
 * The Class Explorer.
 */
public class Explorer {

	private static Log log = Log.getLog(Explorer.class);
	
	/** The queue. */
	private JMQQueue queue;

	/** The depth. */
	private int depth;
	
	/** The listeners. */
	private List<MessageReadedListener> listeners;
	
	/**
	 * Instantiates a new explorer.
	 *
	 * @param queue the queue
	 */
	public Explorer (JMQQueue queue){
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
		
		try {
			depth = queue.getCurrentDepth();
			queue.addMessageReadedListener(new MessageReadedListener() {
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
			queue.refresh();
			messages = queue.getAllJMQMessages();
		} catch (MQException e) {
			log.error(e);
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
