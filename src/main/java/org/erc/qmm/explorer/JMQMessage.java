package org.erc.qmm.explorer;

import com.ibm.mq.MQMessage;
import java.io.IOException;
import java.util.Date;

public class JMQMessage extends MQMessage  {

    private int position;
    private JMQQueue queue;
    private String displayableContents;
    private byte data[];

    public JMQMessage(JMQQueue jmqqueue, int i) {
        displayableContents = null;
        data = null;
        queue = jmqqueue;
        position = i;
    }

    public JMQMessage() {
        displayableContents = null;
        data = null;
        queue = null;
        position = 0;
    }

    public int getMessageType() {
        return super.messageType;
    }

    public int getPriority() {
        return super.priority;
    }

    public byte[] getMessageId() {
        return super.messageId;
    }

    public byte[] getCorrelationId() {
        return super.correlationId;
    }

    public int getBackoutCount() {
        return super.backoutCount;
    }

    public String getReplyToQueue() {
        return super.replyToQueueName;
    }

    public String getReplyToQueueManager() {
        return super.replyToQueueManagerName;
    }

    public String getPutDateTime() {
        Date date = super.putDateTime.getTime();
        return date.toString();
    }

    public byte[] getData() throws IOException {
        if(data == null) {
            data = new byte[getDataLength()];
            readFully(data);
        }
        return data;
    }

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
	        	stringbuffer.append("ERROR!!! ");
	            stringbuffer.append(ex.getMessage()).append("\r\n");
	        }
	        displayableContents = stringbuffer.toString();
	    }
        return displayableContents;
    }

    public JMQQueue getQueue() {
        return queue;
    }

    public void setQueue(JMQQueue jmqqueue) {
        queue = jmqqueue;
    }

    public void setPosition(int i) {
        position = i;
    }

    public int getPosition() {
        return position;
    }

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