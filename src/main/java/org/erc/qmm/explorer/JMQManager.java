package org.erc.qmm.explorer;

import java.util.Collection;
import java.util.Hashtable;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;

public class JMQManager extends MQQueueManager {

    private Hashtable<String,JMQQueue> jmqQueues;

    private JMQConnection jmqconnection;
    
    private String name;
    
    protected JMQManager(String name, JMQConnection jmqconnection) throws MQException {
        super(name, jmqconnection.getProperties());
        this.jmqconnection=jmqconnection;
        this.name = name;
        this.jmqQueues = new Hashtable<String,JMQQueue>();
    }

    public void removeJMQQueue(String s) {
        jmqQueues.remove(s);
    }

    public JMQQueue addJMQQueue(String key) throws MQException {
        JMQQueue jmqqueue = jmqQueues.get(key);
        if(jmqqueue == null) {
        	jmqqueue = new JMQQueue(this, key);
            jmqQueues.put(key, jmqqueue);
        }
        return jmqqueue;
    }

    public JMQConnection getConnection(){
    	return jmqconnection;
    }

    public JMQQueue getQueue(String s) throws MQException {
    	JMQQueue queue = jmqQueues.get(s);
    	if(queue == null){
    		queue = addJMQQueue(s);
    	}
    	return queue;
    }

    public Collection<JMQQueue> getAllQueues() {
        if(jmqQueues == null){
            return null;
        }
        return jmqQueues.values();
    }

    public Collection<String> getAllQueueNames() {
        if(jmqQueues == null){
            return null;
        }
        return jmqQueues.keySet();
    }

    public void disconnect() throws MQException {
        super.disconnect();
    }
    
    public String getName(){
    	return name;
    }
}