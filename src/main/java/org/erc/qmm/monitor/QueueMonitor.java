package org.erc.qmm.monitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.erc.qmm.config.Queue;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.pcf.CMQC;
import com.ibm.mq.pcf.CMQCFC;
import com.ibm.mq.pcf.MQCFH;
import com.ibm.mq.pcf.MQCFST;
import com.ibm.mq.pcf.PCFAgent;
import com.ibm.mq.pcf.PCFException;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFParameter;

public class QueueMonitor extends Thread{

	private List<PollListener> listeners;
	
	private PollEvent event;
	
	private Queue queue;
	
	public QueueMonitor(Queue queue){
		super();
		setName("QueueMonitor-" + queue.getName());
		setDaemon(true);
		this.queue = queue;
		this.listeners = new ArrayList<PollListener>();
		this.event =new PollEvent();
	}
	
	private void emitEvent(){
		if(listeners!=null && !listeners.isEmpty()){
			for(PollListener listener:listeners){
				listener.action(event);
			}
		}
	}
	
	private void fetch(MQMessage[] responses) throws MQException, IOException{
		MQCFH cfh;
		for (int i = 0; i < responses.length; i++) {
            cfh = new MQCFH (responses [i]); 
            if (cfh.reason == 0){
                for (int j = 0; j < cfh.parameterCount; j++) { 
   					 PCFParameter p = PCFParameter.nextParameter(responses [i]);
				     int parm = p.getParameter();
				     switch (parm) {
				         case CMQC.MQIA_CURRENT_Q_DEPTH:
				        	 event.setDepth((Integer) p.getValue());
				         break;
				         case CMQC.MQIA_MAX_Q_DEPTH:
				        	 event.setMaxDepth( (Integer) p.getValue());
				         break;
				         case CMQC.MQIA_MSG_ENQ_COUNT:
				        	 event.setEnqueued((Integer) p.getValue());
				         break;
				         case CMQC.MQIA_MSG_DEQ_COUNT:
				        	 event.setDequeued((Integer) p.getValue());
				         break;
				     }
                }
            }
		}   
	}
	
	
	private void checkQueueData(){
		try {
			PCFAgent agentNode = new PCFAgent(queue.getHost(), queue.getPort(), queue.getChannel());
			fetch(agentNode.send(CMQCFC.MQCMD_INQUIRE_Q, new PCFParameter[]{ new MQCFST(CMQC.MQCA_Q_NAME, queue.getName())}));
			fetch(agentNode.send(CMQCFC.MQCMD_RESET_Q_STATS, new PCFParameter[]{ new MQCFST(CMQC.MQCA_Q_NAME,queue.getName())}));
			emitEvent();
			
			if(agentNode!=null){
				try {
					agentNode.disconnect();
				} catch (MQException e) { /* Ignored*/}
				agentNode = null;
			}
		} catch (PCFException pcfe) {
			System.out.println("PCFException caught " + pcfe);
			if (pcfe.exceptionSource instanceof PCFMessage[]){
				PCFMessage[] msgs = (PCFMessage[]) pcfe.exceptionSource;
				for (int i = 0; i < msgs.length; i++) {
					System.out.println(msgs[i]);
				}
			}
		} catch (MQException mqe) {
			System.out.println("ERROR: MQException caught" + mqe);
		} catch (IOException ioe) {
			System.out.println("ERROR: IOException caught" + ioe);
		}
	}
	
	@Override
	public void run() {
		super.run();
		while (true) {
			checkQueueData();
			try {
				Thread.sleep(queue.getPollTime()); // sleep for 30 seconds
			} catch (InterruptedException e) {
				System.out.println("ERROR: The monitor has been interrupted, exit...");
				break;
			}
		}
	}
	
	public void addPollListener(PollListener listener){
		listeners.add(listener);
	}

	public Queue getQueue() {
		return queue;
	}

}
