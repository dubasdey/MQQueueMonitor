package org.erc.qmm.explorer;

import com.ibm.mq.*;
import java.util.*;

/**
 * The Class JMQQueue.
 */
public class JMQQueue extends MQQueue {

    /** The jmq messages. */
    private List<JMQMessage> jmqMessages;
    
    /** The get msg opt. */
    private MQGetMessageOptions getMsgOpt;
    
    /** The jmq mgr. */
    private JMQManager jmqMgr;

    /** The listeners. */
    private List<MessageReadedListener> listeners;
    
    /** The limit. */
    private int limit = 10000;
    
    /**
     * Instantiates a new JMQ queue.
     *
     * @param jmqmanager the jmqmanager
     * @param queuename the queuename
     * @throws MQException the MQ exception
     */
    public JMQQueue(JMQManager jmqmanager, String queuename) throws MQException {
    	this(jmqmanager,queuename,58);
    }
    
    /**
     * Instantiates a new JMQ queue.
     *
     * @param jmqmanager the jmqmanager
     * @param s the s
     * @param i the i
     * @throws MQException the MQ exception
     */
    public JMQQueue(JMQManager jmqmanager, String s, int i) throws MQException {
        super(jmqmanager, s, i, null, null, null);
        jmqMgr = jmqmanager;
        listeners = new ArrayList<MessageReadedListener>();
        jmqMessages = new ArrayList<JMQMessage>();
        getMsgOpt = new MQGetMessageOptions();
        getMsgOpt.options = 16;
    }

    /**
     * Gets the all jmq messages.
     *
     * @return the all jmq messages
     */
    public List<JMQMessage> getAllJMQMessages() {
    	return jmqMessages;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return super.name.trim();
    }

    /**
     * Removes the.
     *
     * @throws MQException the MQ exception
     */
    public void remove() throws MQException {
        super.close();
        jmqMgr.removeJMQQueue(getName());
    }

    /**
     * Refresh.
     */
    public void refresh() {
        try {
            jmqMessages.clear();
            getMsgOpt.options = 16;
            int i = getCurrentDepth();
            for(int j = 1; j <= i; j++) {
                JMQMessage jmqmessage = new JMQMessage(this, j);
                get(jmqmessage, getMsgOpt);
                getMsgOpt.options = 32;
                jmqMessages.add(jmqmessage);
                fireMessageReaded(jmqmessage);
                if (j>=limit){
                	break;
                }
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        }
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

    /**
     * Clear messages.
     */
    public void clearMessages() {
        jmqMessages.clear();
        getMsgOpt.options = 16;
    }

    /**
     * Gets the JMQ message.
     *
     * @param i the i
     * @return the JMQ message
     * @throws MQException the MQ exception
     */
    public JMQMessage getJMQMessage(int i) throws MQException {
        JMQMessage jmqmessage = null;
        jmqmessage = new JMQMessage(this, i);
        get(jmqmessage, getMsgOpt);
        getMsgOpt.options = 32;
        jmqMessages.add(jmqmessage);
        return jmqmessage;
    }

    
    /**
     * Delete.
     *
     * @param jmqmessage the jmqmessage
     * @throws MQException the MQ exception
     */
    public void delete(JMQMessage jmqmessage) throws MQException {
        get(jmqmessage);
        jmqMessages.remove(jmqmessage);
    }

    /**
     * Adds the.
     *
     * @param jmqmessage the jmqmessage
     * @throws MQException the MQ exception
     */
    public void add(JMQMessage jmqmessage) throws MQException {
        super.put(jmqmessage);
        jmqMessages.add(jmqmessage);
    }

//    public void dumpJMQMessages(OutputStream outputstream) {
//        boolean flag = false;
//        for(JMQMessage jmqmessage :jmqMessages) {
//            try {
//                outputstream.write(jmqmessage.readString(jmqmessage.getMessageLength()).getBytes());
//                outputstream.write("\n".getBytes());
//            } catch(IOException ioexception) {
//                ioexception.printStackTrace();
//            }
//        }
//    }

    /**
 * Gets the info.
 *
 * @return the info
 * @throws MQException the MQ exception
 */
public String getInfo() throws MQException {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("Name: ").append(getName());
        stringbuffer.append("\n");
        stringbuffer.append("QueueManager: ").append(jmqMgr.getName());
        stringbuffer.append("\n");
        stringbuffer.append("CreationDateTime: ").append( getCreationDateTime().getTime() );
        return stringbuffer.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String s;
        try {
        	s = getName() + " (currDepth: " + getCurrentDepth() + ")";
        } catch(MQException mqexception) {
            s = "ERROR: " + getName();
        }
        return s;
    }

}