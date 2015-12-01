package org.erc.qmm.mq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.erc.qmm.config.QueueConfig;
import org.erc.qmm.mq.agent.ParameterMessage;
import org.erc.qmm.mq.agent.ParameterString;
import org.erc.qmm.mq.agent.Agent;
import org.erc.qmm.mq.agent.Parameter;
import org.erc.qmm.util.Log;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;


/**
 * The Class JMQQueue.
 */
public class JMQQueue extends MQQueue {

	/** The log. */
	private static Log log = Log.getLog(JMQQueue.class);
	
	 /** The Constant MQIA_CURRENT_Q_DEPTH. */
 	public static final int MQIA_CURRENT_Q_DEPTH = 3;
	 
 	/** The Constant MQIA_MAX_Q_DEPTH. */
 	public static final int MQIA_MAX_Q_DEPTH = 15;
	 
 	/** The Constant MQIA_MSG_ENQ_COUNT. */
 	public static final int MQIA_MSG_ENQ_COUNT = 37;
	 
 	/** The Constant MQIA_MSG_DEQ_COUNT. */
 	public static final int MQIA_MSG_DEQ_COUNT = 38;
	 
 	/** The Constant MQIA_TIME_SINCE_RESET. */
 	public static final int MQIA_TIME_SINCE_RESET = 35;
	 
 	/** The Constant MQCA_Q_NAME. */
 	public static final int MQCA_Q_NAME = 2016;
	 
 	/** The Constant MQCMD_INQUIRE_Q. */
 	public static final int MQCMD_INQUIRE_Q = 13;
	 
 	/** The Constant MQCMD_RESET_Q_STATS. */
 	public static final int MQCMD_RESET_Q_STATS = 17;

	/** The jmq messages. */
	private List<JMQMessage> jmqMessages;

	/** The get msg opt. */
	private MQGetMessageOptions getMsgOpt;

	/** The jmq mgr. */
	private MQQueueManager jmqMgr;

	/** The agent node. */
	private Agent agentNode;

	/** The queue config. */
	private QueueConfig queueConfig;

	/** The listeners. */
	private List<MessageReadedListener> listeners;

	/** The limit. */
	private int limit = 10000;

	/**
	 * Instantiates a new JMQ queue.
	 *
	 * @param config the config
	 * @throws MQException the MQ exception
	 */
	public JMQQueue(QueueConfig config) throws MQException {
		this(MQUtils.buildManager(config),config.getName(),58);
		this.queueConfig = config;
	}


	/**
	 * Instantiates a new JMQ queue.
	 *
	 * @param jmqmanager the jmqmanager
	 * @param queuename the queuename
	 * @param i the i
	 * @throws MQException the MQ exception
	 */
	private JMQQueue(MQQueueManager jmqmanager, String queuename, int i) throws MQException {
		super(jmqmanager, queuename, i, null, null, null);
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
	public QueueConfig getConfig() {
		return queueConfig;
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
			log.error(exception);
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


	/**
	 * Gets the info.
	 *
	 * @return the info
	 * @throws MQException the MQ exception
	 */
	public String getInfo() throws MQException {
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("Name: ").append(queueConfig.getName());
		stringbuffer.append("\n");
		stringbuffer.append("QueueManager: ").append(queueConfig.getManager());
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
			s = queueConfig.getName() + " (currDepth: " + getCurrentDepth() + ")";
		} catch(MQException mqexception) {
			log.error(mqexception);
			s = "ERROR: " + queueConfig.getName();
		}
		return s;
	}

	/**
	 * Fetch stats.
	 *
	 * @return the map
	 */
	public synchronized Map<Integer,Object> fetchStats(){
		Map<Integer,Object> stats = new HashMap<Integer,Object>();
		try {
			
			MQEnvironment.disableTracing();
			MQException.log = null;
			
			if(agentNode==null){
				agentNode = new Agent(queueConfig.getHost(),queueConfig.getPort(),queueConfig.getChannel(),queueConfig.getManager());
			}

			Parameter[] params = new Parameter[]{ new ParameterString(MQCA_Q_NAME, queueConfig.getName())};
			
			// Requerido para obtener profundidades de cola
			exec(agentNode.send(MQCMD_INQUIRE_Q, params),stats);
			
			// Requerido para obtener entrada y salida de mensajes
			exec(agentNode.send(MQCMD_RESET_Q_STATS, params),stats);

		} catch (Exception ex) {
			log.error(ex);
		} 
		return stats;
	}
	
 
	/**
	 * Exec.
	 *
	 * @param responses the responses
	 * @param fetch the fetch
	 * @throws MQException the MQ exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void exec(List<MQMessage> responses,Map<Integer,Object> fetch) throws MQException, IOException{
		ParameterMessage cfh = null;
		for(MQMessage response:responses){
			cfh = new ParameterMessage (response); 
			if (cfh.reason == 0){
				if(fetch!=null){
					for (int j = 0; j < cfh.parameterCount; j++) { 
						Parameter p = Parameter.nextParameter(response);
						fetch.put(p.getParameter(), p.getValue());
					}
				}
			}
		}
	}    

	/**
	 * Disconnect.
	 */
	public void disconnect(){
		if(jmqMgr!=null){
			try {
				jmqMgr.disconnect();
			} catch (MQException e) { /* Ignored*/}
			jmqMgr = null;
		}
		if(agentNode!=null){
			try {
				agentNode.disconnect();
			} catch (MQException e) { /* Ignored*/}
			agentNode = null;
		}
	}

}