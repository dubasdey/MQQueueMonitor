package org.erc.qmm.mq.agent;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;

/**
 * The Class Parameter.
 */
public abstract class Parameter extends ParameterHeader implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6007216400435655914L;
	
	/**
	 * Next parameter.
	 *
	 * @param message the message
	 * @return the parameter
	 * @throws MQException the MQ exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Parameter nextParameter(MQMessage message) throws MQException, IOException {
		message.writeBytes("");
		int pos = message.getDataOffset();
		int type = message.readInt();
		message.seek(pos);

		switch (type) {
		case 3:
			return new ParameterInt(message);
		case 5:
			return new ParameterIntArray(message);
		case 4:
			return new ParameterString(message);
		case 6:
		case 9:
		case 23:
		case 25:
		case 20:
		case 13:
		case 14:
		case 15:
		case 7:
		case 8:
		case 10:
		case 11:
		case 12:
		case 16:
		case 17:
		case 18:
		case 19:
		case 21:
		case 22:
		case 24:
		}
		throw new MQException(2, 3013, message);
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public abstract int getType();

	/**
	 * Gets the parameter.
	 *
	 * @return the parameter
	 */
	public abstract int getParameter();

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public abstract Object getValue();

	/**
	 * Gets the string value.
	 *
	 * @return the string value
	 */
	public abstract String getStringValue();

	/**
	 * Sets the value.
	 *
	 * @param paramObject the new value
	 * @throws ClassCastException the class cast exception
	 */
	public abstract void setValue(Object paramObject) throws ClassCastException;

	/**
	 * Gets the string length.
	 *
	 * @param string the string
	 * @param characterSet the character set
	 * @return the string length
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	protected static int getStringLength(String string, int characterSet) throws UnsupportedEncodingException {
		if (string == null) {
			return 0;
		}
		return CCSID.convert(string, characterSet).length;
	}

	/**
	 * Write string.
	 *
	 * @param message the message
	 * @param string the string
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected static int writeString(MQMessage message, String string) throws IOException {
		if (string == null) {
			return 0;
		}
		int pos = message.getDataOffset();
		message.writeString(string);
		return message.getDataOffset() - pos;
	}

}