package org.erc.qmm.mq;

import com.ibm.mq.MQMessage;
import java.io.IOException;
import java.util.Date;

import org.erc.qmm.util.Log;

/**
 * The Class JMQMessage.
 */
public class JMQMessage extends MQMessage  {

	private static Log log = Log.getLog(JMQMessage.class);
	
    /** The position. */
    private int position;
    
    /** The queue. */
    private JMQQueue queue;
    
    /** The displayable contents. */
    private String displayableContents;
    
    /** The data. */
    private byte data[];

    /**
     * Instantiates a new JMQ message.
     *
     * @param jmqqueue the jmqqueue
     * @param i the i
     */
    public JMQMessage(JMQQueue jmqqueue, int i) {
        displayableContents = null;
        data = null;
        queue = jmqqueue;
        position = i;
    }

    /**
     * Instantiates a new JMQ message.
     */
    public JMQMessage() {
        displayableContents = null;
        data = null;
        queue = null;
        position = 0;
    }

    /**
     * Gets the message type.
     *
     * @return the message type
     */
    public int getMessageType() {
        return super.messageType;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public int getPriority() {
        return super.priority;
    }

    /**
     * Gets the message id.
     *
     * @return the message id
     */
    public byte[] getMessageId() {
        return super.messageId;
    }

    /**
     * Gets the correlation id.
     *
     * @return the correlation id
     */
    public byte[] getCorrelationId() {
        return super.correlationId;
    }

    /**
     * Gets the backout count.
     *
     * @return the backout count
     */
    public int getBackoutCount() {
        return super.backoutCount;
    }

    /**
     * Gets the reply to queue.
     *
     * @return the reply to queue
     */
    public String getReplyToQueue() {
        return super.replyToQueueName;
    }

    /**
     * Gets the reply to queue manager.
     *
     * @return the reply to queue manager
     */
    public String getReplyToQueueManager() {
        return super.replyToQueueManagerName;
    }

    /**
     * Gets the put date time.
     *
     * @return the put date time
     */
    public String getPutDateTime() {
        Date date = super.putDateTime.getTime();
        return date.toString();
    }

    /**
     * Gets the data.
     *
     * @return the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public byte[] getData() throws IOException {
        if(data == null) {
            data = new byte[getDataLength()];
            readFully(data);
        }
        return data;
    }

    /**
     * Gets the message contents.
     *
     * @return the message contents
     */
    public String getMessageContents() {
        if(displayableContents == null){
	        StringBuffer stringbuffer = new StringBuffer();
	        try {
	        	stringbuffer.append("Type.......................").append(getMessageType()).append("\r\n");
	        	stringbuffer.append("Priority...................").append(getPriority()).append("\r\n");
	        	stringbuffer.append("MessageId..................").append(getMessageId()).append("\r\n");
	        	stringbuffer.append("MessageId (if IBM EBCDIC)..").append(new String(getMessageId(), "Cp500")).append("\r\n");
	        	stringbuffer.append("CorrelationId..............").append(getCorrelationId()).append("\r\n");
	        	stringbuffer.append("PutDateTime................").append(getPutDateTime()).append("\r\n");
	        	stringbuffer.append("BackoutCount...............").append(getBackoutCount()).append("\r\n");
	        	stringbuffer.append("ReplyToQueue...............").append(getReplyToQueue()).append("\r\n");
	        	stringbuffer.append("ReplyToQueueManager........").append(getReplyToQueueManager()).append("\r\n");
	        	stringbuffer.append("Data length................").append(data.length).append("\r\n");
	        } catch(Exception ex) {
	        	log.error(ex);
	        	stringbuffer.append("ERROR!!! ");
	            stringbuffer.append(ex.getMessage()).append("\r\n");
	        }
	        displayableContents = stringbuffer.toString();
	    }
        return displayableContents;
    }

    /**
     * Gets the queue.
     *
     * @return the queue
     */
    public JMQQueue getQueue() {
        return queue;
    }

    /**
     * Sets the queue.
     *
     * @param jmqqueue the new queue
     */
    public void setQueue(JMQQueue jmqqueue) {
        queue = jmqqueue;
    }

    /**
     * Sets the position.
     *
     * @param i the new position
     */
    public void setPosition(int i) {
        position = i;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	String s;
        try {
            s = position + " : " + getMessageId() + " (" + super.originalLength + ")";
        } catch(Exception exception) {
            exception.printStackTrace();
            s = "--ERROR--";
        }
        return s;
    }

}