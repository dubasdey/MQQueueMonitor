package org.erc.qmm.mq.agent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;

/**
 * The Class AgentResponseTracker.
 */
class AgentResponseTracker {

	/** The Constant TYPE_DEFAULT. */
	public static final int TYPE_DEFAULT = 0;
	
	/** The Constant TYPE_390. */
	public static final int TYPE_390 = 1;
	
	/** The type. */
	private int type;
	
	/** The cfh. */
	private final ParameterMessage cfh = new ParameterMessage();
	
	/** The set. */
	private final Set<String> set = new HashSet<String>();

	/**
	 * Instantiates a new agent response tracker.
	 *
	 * @param type the type
	 */
	public AgentResponseTracker(int type){
		this.type = type;
	}
	
	/**
	 * Checks if is last.
	 *
	 * @param response the response
	 * @return true, if is last
	 * @throws MQException the MQ exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	boolean isLast(MQMessage response) throws MQException, IOException {
		cfh.initialize(response);
		boolean result = false;
		if(TYPE_390 == type){
			String current = null;
			int count = cfh.parameterCount;
			while (count-- > 0) {
				Parameter p = Parameter.nextParameter(response);
				int id = p.getParameter();
				if (id == 7003) {
					set.add(p.getStringValue());
				} else if (id == 7004) {
					set.add(current = p.getStringValue());
				}
			}
			response.seek(0);
			if ((cfh.control == 1) && (current != null)) {
				set.remove(current);
			}
			result = set.size() == 0;
		}else{
		    response.seek(0);
		    result= cfh.control == 1;
		}
		return result;
	}
}