package org.erc.qmm.config;

/**
 * The Class Queue.
 */
public class QueueConfig {
	
	/** The desc. */
	private String desc;
	
	/** The host. */
	private String host;
	
	/** The port. */
	private int port;
	
	/** The poll time. */
	private int pollTime;
	
	/** The channel. */
	private String channel;
	
	/** The manager. */
	private String manager;
	
	/** The name. */
	private String name;

	/**
	 * Instantiates a new queue.
	 */
	public QueueConfig(){}
	
	/**
	 * Instantiates a new queue.
	 *
	 * @param desc the desc
	 */
	public QueueConfig(String desc){
		setDesc(desc);
		setPort(1417);
		setPollTime(5000);
	}
	
	
	/**
	 * Gets the desc.
	 *
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * Sets the desc.
	 *
	 * @param desc the new desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the host.
	 *
	 * @param host the new host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * Sets the channel.
	 *
	 * @param channel the new channel
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * Gets the manager.
	 *
	 * @return the manager
	 */
	public String getManager() {
		return manager;
	}

	/**
	 * Sets the manager.
	 *
	 * @param manager the new manager
	 */
	public void setManager(String manager) {
		this.manager = manager;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the poll time.
	 *
	 * @return the poll time
	 */
	public int getPollTime() {
		return pollTime;
	}

	/**
	 * Sets the poll time.
	 *
	 * @param pollTime the new poll time
	 */
	public void setPollTime(int pollTime) {
		this.pollTime = pollTime;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.desc;
	}
	
}
