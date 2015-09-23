package org.erc.qmm.explorer;

import java.util.EventListener;

public interface MessageReadedListener extends EventListener {

	void messageReaded(JMQMessage message);
}
