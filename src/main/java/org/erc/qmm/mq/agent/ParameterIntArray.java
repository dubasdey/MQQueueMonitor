package org.erc.qmm.mq.agent;

import java.io.IOException;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;

/**
 * The Class ParameterIntArray.
 */
public class ParameterIntArray extends Parameter {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4418516414486517571L;
	
	/** The Constant type. */
	public static final int type = 5;
	
	/** The struc length. */
	public int strucLength = 16;
	
	/** The parameter. */
	public int parameter;
	
	/** The count. */
	public int count;
	
	/** The values. */
	public int[] values;

	/**
	 * Write.
	 *
	 * @param message the message
	 * @param parameter the parameter
	 * @param values the values
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static int write(MQMessage message, int parameter, int[] values) throws IOException {
		int count = values == null ? 0 : values.length;
		message.writeInt(5);
		message.writeInt(16 + count * 4);
		message.writeInt(parameter);
		message.writeInt(count);
		for (int i = 0; i < count; i++) {
			message.writeInt(values[i]);
		}
		return 16 + count * 4;
	}

	/**
	 * Instantiates a new parameter int array.
	 */
	public ParameterIntArray() {}

	/**
	 * Instantiates a new parameter int array.
	 *
	 * @param parameter the parameter
	 * @param values the values
	 */
	public ParameterIntArray(int parameter, int[] values) {
		this.parameter = parameter;
		setValues(values);
	}

	/**
	 * Instantiates a new parameter int array.
	 *
	 * @param message the message
	 * @throws MQException the MQ exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ParameterIntArray(MQMessage message) throws MQException, IOException {
		initialize(message);
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.ParameterHeader#initialize(com.ibm.mq.MQMessage)
	 */
	public void initialize(MQMessage message) throws MQException, IOException {
		if (message.readInt() != 5) {
			throw new MQException(2, 3013, message);
		}

		this.strucLength = message.readInt();
		this.parameter = message.readInt();
		this.count = message.readInt();

		if (this.count < 0) {
			throw new MQException(2, 3027, message);
		}

		if (this.strucLength != 16 + this.count * 4) {
			throw new MQException(2, 3028, message);
		}

		this.values = new int[this.count];

		for (int i = 0; i < this.count; i++)
			this.values[i] = message.readInt();
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.ParameterHeader#write(com.ibm.mq.MQMessage)
	 */
	public int write(MQMessage message) throws IOException {
		int count = this.values == null ? 0 : this.values.length;
		message.writeInt(5);
		message.writeInt(this.strucLength);
		message.writeInt(this.parameter);
		message.writeInt(count);
		for (int i = 0; i < count; i++) {
			message.writeInt(this.values[i]);
		}
		return 16 + count * 4;
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
		return 5;
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
		try {
			return this.values.clone();
		} catch (NullPointerException npe) {
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.Parameter#getStringValue()
	 */
	public String getStringValue() {
		if (this.values == null) {
			return "";
		}

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < this.values.length; i++) {
			sb.append(this.values[i]);
			sb.append(' ');
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}

		return new String(sb);
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.Parameter#setValue(java.lang.Object)
	 */
	public void setValue(Object value) throws ClassCastException {
		setValues((int[]) value);
	}

	/**
	 * Sets the values.
	 *
	 * @param values the new values
	 */
	public void setValues(int[] values) {
		int count = values == null ? 0 : values.length;

		this.strucLength = (16 + count * 4);
		this.count = count;
		this.values = values;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if ((obj != null) && ((obj instanceof ParameterIntArray))) {
			ParameterIntArray other = (ParameterIntArray) obj;
			int[] otherValues = other.values;
			int[] values = this.values;

			if ((other.parameter == this.parameter) && (otherValues != null) && (values != null)
					&& (otherValues.length == values.length)) {
				int i = values.length;
				boolean match = true;

				while ((match) && (i-- > 0)) {
					match = otherValues[i] == values[i];
				}

				return match;
			}

			return false;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ParameterIntArray [type: 5");
		sb.append(", strucLength: ").append(this.strucLength);
		sb.append(", parameter: ").append(this.parameter);
		sb.append(", count: ").append(this.count);
		sb.append(", values: ");
		int[] values = this.values;
		if (values != null) {
			sb.append('{');
			if (values.length > 0){
				for (int i = 0; i < values.length; i++) {
					sb.append(values[i]).append(", ");
				}
				sb.setLength(sb.length() - 2);
			}
			sb.append('}');
		} else {
			sb.append("null");
		}
		sb.append(']');
		return new String(sb);
	}
}