package org.erc.qmm.monitor;

/**
 * The listener interface for receiving poll events.
 * The class that is interested in processing a poll
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPollListener<code> method. When
 * the poll event occurs, that object's appropriate
 * method is invoked.
 *
 * @see PollEvent
 */
public interface PollListener {

	 /**
 	 * Action.
 	 *
 	 * @param e the e
 	 */
 	void action(PollEvent e);

}
