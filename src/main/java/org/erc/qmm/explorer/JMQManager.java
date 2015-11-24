package org.erc.qmm.explorer;

import java.util.Collection;
import java.util.Hashtable;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;

/**
 * The Class JMQManager.
 */
public class JMQManager extends MQQueueManager {

    /** The jmq queues. */
    private Hashtable<String,JMQQueue> jmqQueues;

    /** The jmqconnection. */
    private JMQConnection jmqconnection;
    
    /** The name. */
    private String name;
    
    /**
     * Instantiates a new JMQ manager.
     *
     * @param name the name
     * @param jmqconnection the jmqconnection
     * @throws MQException the MQ exception
     */
    protected JMQManager(String name, JMQConnection jmqconnection) throws MQException {
        super(name, jmqconnection.getProperties());
        this.jmqconnection=jmqconnection;
        this.name = name;
        this.jmqQueues = new Hashtable<String,JMQQueue>();
    }

    /**
     * Removes the jmq queue.
     *
     * @param s the s
     */
    public void removeJMQQueue(String s) {
        jmqQueues.remove(s);
    }

    /**
     * Adds the jmq queue.
     *
     * @param key the key
     * @return the JMQ queue
     * @throws MQException the MQ exception
     */
    public JMQQueue addJMQQueue(String key) throws MQException {
        JMQQueue jmqqueue = jmqQueues.get(key);
        if(jmqqueue == null) {
        	jmqqueue = new JMQQueue(this, key);
            jmqQueues.put(key, jmqqueue);
        }
        return jmqqueue;
    }

    /**
     * Gets the connection.
     *
     * @return the connection
     */
    public JMQConnection getConnection(){
    	return jmqconnection;
    }

    /**
     * Gets the queue.
     *
     * @param s the s
     * @return the queue
     * @throws MQException the MQ exception
     */
    public JMQQueue getQueue(String s) throws MQException {
    	JMQQueue queue = jmqQueues.get(s);
    	if(queue == null){
    		queue = addJMQQueue(s);
    	}
    	return queue;
    }

    /**
     * Gets the all queues.
     *
     * @return the all queues
     */
    public Collection<JMQQueue> getAllQueues() {
        if(jmqQueues == null){
            return null;
        }
        return jmqQueues.values();
    }

    /**
     * Gets the all queue names.
     *
     * @return the all queue names
     */
    public Collection<String> getAllQueueNames() {
        if(jmqQueues == null){
            return null;
        }
        return jmqQueues.keySet();
    }

    /* (non-Javadoc)
     * @see com.ibm.mq.MQQueueManager#disconnect()
     */
    public void disconnect() throws MQException {
        super.disconnect();
    }
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName(){
    	return name;
    }
}