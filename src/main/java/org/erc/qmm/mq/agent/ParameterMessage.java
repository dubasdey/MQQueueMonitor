package org.erc.qmm.mq.agent;

import java.io.IOException;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;

/**
 * The Class ParameterMessage.
 */
public class ParameterMessage extends ParameterHeader {
	
	/** The Constant strucLength. */
	public static final int strucLength = 36;
	
	/** The type. */
	public int type = 1;
	
	/** The version. */
	public int version = 1;
	
	/** The command. */
	public int command;
	
	/** The msg seq number. */
	public int msgSeqNumber = 1;
	
	/** The control. */
	public int control = 1;
	
	/** The comp code. */
	public int compCode;
	
	/** The reason. */
	public int reason;
	
	/** The parameter count. */
	public int parameterCount;

	/**
	 * Write.
	 *
	 * @param message the message
	 * @param command the command
	 * @param parameterCount the parameter count
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static int write(MQMessage message, int command, int parameterCount) throws IOException {
		return write(message, command, parameterCount, 1, 1);
	}

	/**
	 * Write.
	 *
	 * @param message the message
	 * @param command the command
	 * @param parameterCount the parameter count
	 * @param type the type
	 * @param version the version
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static int write(MQMessage message, int command, int parameterCount, int type, int version) throws IOException {
		message.writeInt(type);
		message.writeInt(36);
		message.writeInt(version);
		message.writeInt(command);
		message.writeInt(1);
		message.writeInt(1);
		message.writeInt(0);
		message.writeInt(0);
		message.writeInt(parameterCount);
		return 36;
	}

	/**
	 * Instantiates a new parameter message.
	 */
	public ParameterMessage() {}

	/**
	 * Instantiates a new parameter message.
	 *
	 * @param command the command
	 * @param parameterCount the parameter count
	 */
	public ParameterMessage(int command, int parameterCount) {
		this.command = command;
		this.parameterCount = parameterCount;
	}

	/**
	 * Instantiates a new parameter message.
	 *
	 * @param message the message
	 * @throws MQException the MQ exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ParameterMessage(MQMessage message) throws MQException, IOException {
		initialize(message);
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.ParameterHeader#initialize(com.ibm.mq.MQMessage)
	 */
	public void initialize(MQMessage message) throws MQException, IOException {
		this.type = message.readInt();
		switch (this.type) {
		case 1:
		case 2:
		case 7:
		case 8:
		case 10:
		case 12:
		case 16:
		case 17:
		case 18:
		case 19:
		case 21:
		case 22:
			break;
		case 3:
		case 4:
		case 5:
		case 6:
		case 9:
		case 11:
		case 13:
		case 14:
		case 15:
		case 20:
		default:
			throw new MQException(2, 3001, message);
		}

		if (message.readInt() != strucLength) {
			throw new MQException(2, 3002, message);
		}
		this.version = message.readInt();
		this.command = message.readInt();
		this.msgSeqNumber = message.readInt();
		this.control = message.readInt();
		this.compCode = message.readInt();
		this.reason = message.readInt();
		this.parameterCount = message.readInt();
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.ParameterHeader#write(com.ibm.mq.MQMessage)
	 */
	public int write(MQMessage message) throws IOException {
		message.writeInt(this.type);
		message.writeInt(36);
		message.writeInt(this.version);
		message.writeInt(this.command);
		message.writeInt(this.msgSeqNumber);
		message.writeInt(this.control);
		message.writeInt(this.compCode);
		message.writeInt(this.reason);
		message.writeInt(this.parameterCount);
		return 36;
	}

	/* (non-Javadoc)
	 * @see org.erc.qmm.mq.agent.ParameterHeader#size()
	 */
	public int size() {
		return strucLength;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if ((obj != null) && ((obj instanceof ParameterMessage))) {
			ParameterMessage other = (ParameterMessage) obj;
			return (other.type == this.type) && (other.version == this.version) && (other.command == this.command)
					&& (other.msgSeqNumber == this.msgSeqNumber) && (other.control == this.control)
					&& (other.compCode == this.compCode) && (other.reason == this.reason)
					&& (other.parameterCount == this.parameterCount);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"% [type=%s, version=%s, command=%s, msgSeqNumber=%s, control=%s, compCode=%s, reason=%s, parameterCount=%s]",
				getUnqualifiedClassName(), type, version, command, msgSeqNumber, control, compCode, reason, parameterCount);
	}



}