package org.erc.qmm.mq;

import java.util.EventListener;

/**
 * The listener interface for receiving messageReaded events.
 * The class that is interested in processing a messageReaded
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addMessageReadedListener<code> method. When
 * the messageReaded event occurs, that object's appropriate
 * method is invoked.
 *
 * @see MessageReadedEvent
 */
public interface MessageReadedListener extends EventListener {

	/**
	 * Message readed.
	 *
	 * @param message the message
	 */
	void messageReaded(JMQMessage message);
}
