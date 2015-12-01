package org.erc.qmm.mq.agent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;

/**
 * The Class ParameterString.
 */
public class ParameterString extends Parameter {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8105177499570292216L;
	
	/** The Constant pads. */
	static final String[] pads = { "", " ", "  ", "   ", "" };
	
	/** The Constant type. */
	public static final int type = 4;
	
	/** The struc length. */
	public int strucLength = 20;
	
	/** The parameter. */
	public int parameter;
	
	/** The coded char set id. */
	public int codedCharSetId;
	
	/** The string length. */
	public int stringLength;
	
	/** The string. */
	public String string;

	/**
	 * Write.
	 *
	 * @param message the message
	 * @param parameter the parameter
	 * @param string the string
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static int write(MQMessage message, int parameter, String string) throws IOException {
		int stringLength = Parameter.getStringLength(string, message.characterSet);
		int padLength = stringLength % 4;

		if (padLength != 0) {
			padLength = 4 - padLength;
		}

		int totalLength = 20 + stringLength + padLength;

		message.writeString("");
		message.writeInt(4);
		message.writeInt(totalLength);
		message.writeInt(parameter);
		message.writeInt(message.characterSet);
		message.writeInt(stringLength);
		message.writeString(string);

		if (padLength != 0) {
			message.writeString(pads[padLength]);
		}

		return totalLength;
	}

	/**
	 * Instantiates a new parameter string.
	 */
	public ParameterString() {
		setString("");
	}

	/**
	 * Instantiates a new parameter string.
	 *
	 * @param parameter the parameter
	 * @param string the string
	 */
	public ParameterString(int parameter, String string) {
		this.parameter = parameter;
		setString(string);
	}

	/**
	 * Instantiates a new parameter string.
	 *
	 * @param message the message
	 * @throws MQException the MQ exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ParameterString(MQMessage message) throws MQException, IOException {
		initialize(message);
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.ParameterHeader#initialize(com.ibm.mq.MQMessage)
	 */
	public void initialize(MQMessage message) throws MQException, IOException {
		int type = message.readInt();

		if (type != 4) {
			throw new MQException(2, 3013, message);
		}

		this.strucLength = message.readInt();
		this.parameter = message.readInt();
		this.codedCharSetId = message.readInt();
		this.stringLength = message.readInt();
		this.string = ParameterHeader.readString(message, this.stringLength);

		if (this.stringLength < 0) {
			throw new MQException(2, 3011, message);
		}

		if (this.strucLength < 20 + this.stringLength) {
			throw new MQException(2, 3010, message);
		}

		int padLength = this.strucLength - 20 - this.stringLength;

		while (padLength-- > 0)
			message.readByte();
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.ParameterHeader#write(com.ibm.mq.MQMessage)
	 */
	public int write(MQMessage message) throws IOException {
		int stringLength = Parameter.getStringLength(this.string, message.characterSet);
		int padLength = stringLength % 4;

		if (padLength != 0) {
			padLength = 4 - padLength;
		}

		message.writeString("");
		message.writeInt(4);
		message.writeInt(this.strucLength);
		message.writeInt(this.parameter);
		message.writeInt(message.characterSet);
		message.writeInt(stringLength);
		message.writeString(this.string);

		if (padLength != 0) {
			message.writeString(pads[padLength]);
		}
		return 20 + stringLength + padLength;
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.ParameterHeader#size()
	 */
	public int size() {
		return this.strucLength;
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.Parameter#getType()
	 */
	public int getType() {
		return 4;
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
		return this.string;
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.Parameter#getStringValue()
	 */
	public String getStringValue() {
		if (this.string == null) {
			return "";
		}
		return this.string;
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.Parameter#setValue(java.lang.Object)
	 */
	public void setValue(Object value) throws ClassCastException {
		setString((String) value);
	}

	/**
	 * Sets the string.
	 *
	 * @param string the new string
	 */
	public void setString(String string) {
		try {
			this.codedCharSetId = Agent.defaultCharacterSet;
			int stringLength = Parameter.getStringLength(string, this.codedCharSetId);
			int padLength = stringLength % 4;
			if (padLength != 0) {
				padLength = 4 - padLength;
			}
			this.strucLength = (20 + stringLength + padLength);
			this.stringLength = stringLength;
			this.string = string;
		} catch (UnsupportedEncodingException e) {
			this.strucLength = 20;
			this.stringLength = 0;
			this.string = new String();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if ((obj != null) && ((obj instanceof ParameterString))) {
			ParameterString other = (ParameterString) obj;
			String otherValue = other.string;
			String value = this.string;
			return (other.parameter == this.parameter) && (otherValue != null) && (value != null) && (otherValue.equals(value));
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getUnqualifiedClassName() + " [type: 4, strucLength: " + this.strucLength + ", parameter: " + this.parameter  + ", codedCharSetId: " + this.codedCharSetId + ", stringLength: " + this.stringLength + ", string: " + this.string + "]";
	}
}