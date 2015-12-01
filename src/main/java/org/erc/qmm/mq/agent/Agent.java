package org.erc.qmm.mq.agent;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.erc.qmm.mq.MQUtils;

import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

/**
 * The Class Agent.
 */
public class Agent {
	
	/** The Constant defaultCharacterSet. */
	public static final int defaultCharacterSet = 0;
	
	
	/** The pmo. */
	private final MQPutMessageOptions pmo = new MQPutMessageOptions();
	
	/** The gmo. */
	private final MQGetMessageOptions gmo = new MQGetMessageOptions();

	/** The Constant modelQueueName. */
	private static final String modelQueueName = "SYSTEM.DEFAULT.MODEL.QUEUE";
	
	/** The Constant expiryTime. */
	private static final int expiryTime = 300;
	
	/** The Constant waitInterval. */
	private static final int waitInterval = 30000;
	
	/** The Constant encoding. */
	private static final int encoding = 273;
	
	/** The qmanager. */
	private MQQueueManager qmanager;
	
	/** The admin queue. */
	private MQQueue adminQueue;
	
	/** The reply queue. */
	private MQQueue replyQueue;

	/** The qmanager_level. */
	private int qmanager_level;
	
	/** The qmanager_platform. */
	private int qmanager_platform;

	/**
	 * Instantiates a new agent.
	 *
	 * @param host the host
	 * @param port the port
	 * @param channel the channel
	 * @param manager the manager
	 * @throws MQException the MQ exception
	 */
	public Agent(String host, int port, String channel,String manager) throws MQException {
		pmo.options = 128;
		gmo.options = 24577;
		gmo.waitInterval = waitInterval;
		qmanager = MQUtils.buildManager(host,port,channel,manager);
		open();
	}

	/**
	 * Open.
	 *
	 * @throws MQException the MQ exception
	 */
	protected synchronized void open() throws MQException {
		adminQueue = qmanager.accessQueue(qmanager.getCommandInputQueueName(), 8208, null, "", "mqm");
		replyQueue = qmanager.accessQueue(modelQueueName, 8196, "", null, "mqm");
		replyQueue.closeOptions = 2;
		getBasicQmgrInfo(qmanager, true);
	}

	/**
	 * Gets the basic qmgr info.
	 *
	 * @param qmgr the qmgr
	 * @param tryBacklevel the try backlevel
	 * @return the basic qmgr info
	 * @throws MQException the MQ exception
	 */
	private void getBasicQmgrInfo(MQQueueManager qmgr, boolean tryBacklevel) throws MQException {
		int type = 16;
		int version = 3;

		if (tryBacklevel) {
			type = 1;
			version = 1;
		}

		try {
			MQMessage message = setRequestMQMD(new MQMessage());
			ParameterMessage.write(message, 2, 1, type, version);
			ParameterIntArray.write(message, 1001, new int[] { 31, 32, 2015 });
			
			adminQueue.put(message, pmo);
			
			message.messageId = null;
			message.encoding = encoding;
			message.characterSet = defaultCharacterSet;
			replyQueue.get(message, gmo);

			ParameterMessage cfh = new ParameterMessage(message);

			if (cfh.reason == 0) {
				int parameterCount = cfh.parameterCount;
				while (parameterCount-- > 0) {
					Parameter p = Parameter.nextParameter(message);

					switch (p.getParameter()) {
					case 31:
						qmanager_level = ((ParameterInt)p).value;
						break;
					case 32:
						qmanager_platform = ((ParameterInt)p).value;
						break;
					}
				}
			} else if (((cfh.reason == 3001) ||  (cfh.reason == 3003)) &&  (tryBacklevel)) {
				getBasicQmgrInfo(qmanager, false);
			} else {
				throw new MQException(cfh.compCode, cfh.reason, this);
			}
		} catch (IOException e) {
			throw new MQException(2, 2033, this); 
		}  
	} 
	
	/**
	 * Disconnect.
	 *
	 * @throws MQException the MQ exception
	 */
	public synchronized void disconnect() throws MQException { 
		if(adminQueue!=null && adminQueue.isOpen()){
			adminQueue.close();
		}
		if(replyQueue!=null && replyQueue.isOpen()){
			replyQueue.close();
		}
		if(qmanager!=null){
			qmanager.disconnect();		
		}
	}
	
	/**
	 * Send.
	 *
	 * @param command the command
	 * @param parameters the parameters
	 * @return the list
	 * @throws MQException the MQ exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public synchronized List<MQMessage> send(int command, Parameter[] parameters) throws MQException, IOException { 
			
		if (adminQueue == null) {
			throw new MQException(2, 6124, this);
		}

		MQMessage message = setRequestMQMD(new MQMessage());

		int count = parameters == null ? 0 : parameters.length;
		AgentResponseTracker tracker;
		
		if (qmanager_platform == 1) {
			tracker = new AgentResponseTracker(AgentResponseTracker.TYPE_390);
			ParameterMessage.write(message, command, count, 16, 3);
		} else {
			tracker = new AgentResponseTracker(AgentResponseTracker.TYPE_DEFAULT);
			int version = 1;
			for (int i = 0; (i < count) && (version < 3); i++){
				version = Math.max(version, parameters[i].getHeaderVersion());
			}
			ParameterMessage.write(message, command, count, 1, version);
		}

		for (int i = 0; i < count; i++) {
			parameters[i].write(message);
		}

		adminQueue.put(message, pmo);

		byte[] correlationId = message.correlationId;
		List<MQMessage> responseMessages = new Vector<MQMessage>();
		do {
			message = new MQMessage();
			message.correlationId = correlationId;
			message.encoding = encoding;
			message.characterSet = defaultCharacterSet;
			replyQueue.get(message, gmo);
			responseMessages.add(message);
		}while (!tracker.isLast(message));
		
		return responseMessages; 
	}

	/**
	 * Sets the request mqmd.
	 *
	 * @param message the message
	 * @return the MQ message
	 * @throws MQException the MQ exception
	 */
	protected MQMessage setRequestMQMD(MQMessage message) throws MQException {
		if (qmanager_level < 500){
			message.setVersion(1);
		}
		message.messageType = 1;
		message.expiry = expiryTime;
		message.report = 64;
		message.feedback = 0;
		message.format = "MQADMIN ";
		message.encoding = encoding;
		message.characterSet = defaultCharacterSet;
		message.replyToQueueName = replyQueue.name;
		message.replyToQueueManagerName = "";
		message.persistence = 0;
		return message;
	}

}