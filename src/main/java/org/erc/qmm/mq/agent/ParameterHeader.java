package org.erc.qmm.mq.agent;

import java.io.IOException;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;

/**
 * The Class ParameterHeader.
 */
public abstract class ParameterHeader {

	/**
	 * Initialize.
	 *
	 * @param paramMQMessage the param mq message
	 * @throws MQException the MQ exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract void initialize(MQMessage paramMQMessage) throws MQException, IOException;

	/**
	 * Write.
	 *
	 * @param paramMQMessage the param mq message
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract int write(MQMessage paramMQMessage) throws IOException;

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public abstract int size();

	/**
	 * Read string.
	 *
	 * @param message the message
	 * @param length the length
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected static String readString(MQMessage message, int length) throws IOException {
		return message.readStringOfByteLength(length);
	}
}