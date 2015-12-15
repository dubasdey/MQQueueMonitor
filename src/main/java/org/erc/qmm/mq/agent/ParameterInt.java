package org.erc.qmm.mq.agent;

import java.io.IOException;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;

/**
 * The Class ParameterInt.
 */
public class ParameterInt extends Parameter {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5320177474865232455L;
	
	/** The Constant type. */
	public static final int type = 3;
	
	/** The Constant strucLength. */
	public static final int strucLength = 16;
	
	/** The parameter. */
	public int parameter;
	
	/** The value. */
	public int value;

	/**
	 * Write.
	 *
	 * @param message the message
	 * @param parameter the parameter
	 * @param value the value
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static int write(MQMessage message, int parameter, int value) throws IOException {
		message.writeInt(3);
		message.writeInt(16);
		message.writeInt(parameter);
		message.writeInt(value);
		return 16;
	}

	/**
	 * Instantiates a new parameter int.
	 */
	public ParameterInt() {}

	/**
	 * Instantiates a new parameter int.
	 *
	 * @param parameter the parameter
	 * @param value the value
	 */
	public ParameterInt(int parameter, int value) {
		this.parameter = parameter;
		setValue(value);
	}

	/**
	 * Instantiates a new parameter int.
	 *
	 * @param message the message
	 * @throws MQException the MQ exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ParameterInt(MQMessage message) throws MQException, IOException {
		initialize(message);
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.ParameterHeader#initialize(com.ibm.mq.MQMessage)
	 */
	public void initialize(MQMessage message) throws MQException, IOException {
		if (message.readInt() != 3) {
			throw new MQException(2, 3013, message);
		}

		if (message.readInt() != 16) {
			throw new MQException(2, 3009, message);
		}

		this.parameter = message.readInt();
		this.value = message.readInt();
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.ParameterHeader#write(com.ibm.mq.MQMessage)
	 */
	public int write(MQMessage message) throws IOException {
		message.writeInt(3);
		message.writeInt(16);
		message.writeInt(this.parameter);
		message.writeInt(this.value);
		return 16;
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.ParameterHeader#size()
	 */
	public int size() {
		return 16;
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.Parameter#getType()
	 */
	public int getType() {
		return 3;
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.Parameter#getParameter()
	 */
	public int getParameter() {
		return this.parameter;
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.Parameter#getValue()
	 */
	public Object getValue() {
		return new Integer(this.value);
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.Parameter#getStringValue()
	 */
	public String getStringValue() {
		return Integer.toString(this.value);
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.Parameter#setValue(java.lang.Object)
	 */
	public void setValue(Object value) throws ClassCastException {
		setValue((Integer) value);
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(Integer value) {
		try {
			this.value = value.intValue();
		} catch (NullPointerException npe) {
			this.value = 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if ((obj != null) && ((obj instanceof ParameterInt))) {
			ParameterInt other = (ParameterInt) obj;
			return (other.parameter == this.parameter) && (other.value == this.value);
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("ParameterInt [parameter=%s, value=%s]", parameter, value);
	}
}