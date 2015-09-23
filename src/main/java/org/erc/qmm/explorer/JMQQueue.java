package org.erc.qmm.explorer;

import com.ibm.mq.*;
import java.util.*;

public class JMQQueue extends MQQueue {

    private List<JMQMessage> jmqMessages;
    private MQGetMessageOptions getMsgOpt;
    private JMQManager jmqMgr;

    private List<MessageReadedListener> listeners;
    
    private int limit = 10000;
    
    public JMQQueue(JMQManager jmqmanager, String queuename) throws MQException {
    	this(jmqmanager,queuename,58);
    }
    
    public JMQQueue(JMQManager jmqmanager, String s, int i) throws MQException {
        super(jmqmanager, s, i, null, null, null);
        jmqMgr = jmqmanager;
        listeners = new ArrayList<MessageReadedListener>();
        jmqMessages = new ArrayList<JMQMessage>();
        getMsgOpt = new MQGetMessageOptions();
        getMsgOpt.options = 16;
    }

    public List<JMQMessage> getAllJMQMessages() {
    	return jmqMessages;
    }

    public String getName() {
        return super.name.trim();
    }

    public void remove() throws MQException {
        super.close();
        jmqMgr.removeJMQQueue(getName());
    }

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
    
    protected void fireMessageReaded(JMQMessage message){
    	for(MessageReadedListener listener: listeners){
    		listener.messageReaded(message);
    	}
    }
    
    public void addMessageReadedListener(MessageReadedListener listener){
    	this.listeners.add(listener);
    }

    public void clearMessages() {
        jmqMessages.clear();
        getMsgOpt.options = 16;
    }

    public JMQMessage getJMQMessage(int i) throws MQException {
        JMQMessage jmqmessage = null;
        jmqmessage = new JMQMessage(this, i);
        get(jmqmessage, getMsgOpt);
        getMsgOpt.options = 32;
        jmqMessages.add(jmqmessage);
        return jmqmessage;
    }

    
    public void delete(JMQMessage jmqmessage) throws MQException {
        get(jmqmessage);
        jmqMessages.remove(jmqmessage);
    }

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

    public String getInfo() throws MQException {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("Name: ").append(getName());
        stringbuffer.append("\n");
        stringbuffer.append("QueueManager: ").append(jmqMgr.getName());
        stringbuffer.append("\n");
        stringbuffer.append("CreationDateTime: ").append( getCreationDateTime().getTime() );
        return stringbuffer.toString();
    }

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