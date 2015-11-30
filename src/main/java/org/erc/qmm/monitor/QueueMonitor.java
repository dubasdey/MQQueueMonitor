package org.erc.qmm.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.erc.qmm.config.QueueConfig;
import org.erc.qmm.mq.JMQQueue;


/**
 * The Class QueueMonitor.
 */
public class QueueMonitor extends Thread {

	/** The listeners. */
	private List<PollListener> listeners;
	
	/** The event. */
	private PollEvent event;
	
	/** The queue. */
	private JMQQueue queue;
	
	private boolean initied = false;
	
	/**
	 * Instantiates a new queue monitor.
	 *
	 * @param queue the queue
	 * @throws MQException 
	 */
	public QueueMonitor(QueueConfig queue) throws Exception{
		super();
		setName("QueueMonitor-" + queue.getName());
		setDaemon(true);
		this.queue = new JMQQueue(queue);
		this.listeners = new ArrayList<PollListener>();
		this.event =new PollEvent();
	}
	
	/**
	 * Emit event.
	 */
	private void emitEvent(){
		if(listeners!=null && !listeners.isEmpty()){
			for(PollListener listener:listeners){
				listener.action(event);
			}
		}
	}
	
	/**
	 * Check queue data.
	 */
	private void checkQueueData(){
		
		// First stat is dropped (data before monitor starts resetting)
		Map<Integer,Object> items = queue.fetchStats();
		if(initied){
			for (Entry<Integer,Object> item:items.entrySet()){
				switch(item.getKey()){
				case JMQQueue.MQIA_CURRENT_Q_DEPTH:
					 event.setDepth((Integer) item.getValue());
			         break;
				case JMQQueue.MQIA_MAX_Q_DEPTH:
					 event.setMaxDepth( (Integer) item.getValue());
			         break;
				case JMQQueue.MQIA_MSG_ENQ_COUNT:
					 event.setEnqueued((Integer) item.getValue());
			         break;
				case JMQQueue.MQIA_MSG_DEQ_COUNT:
					 event.setDequeued((Integer) item.getValue());
			         break;
				}
			}
			emitEvent();	
		}
		initied = true;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		super.run();
		while (true) {
			checkQueueData();
			try {
				Thread.sleep(queue.getConfig().getPollTime() * 1000); // sleep for x seconds
			} catch (InterruptedException e) {
				System.out.println("ERROR: The monitor has been interrupted, exit...");
				break;
			}
		}
	}
	
	/**
	 * Adds the poll listener.
	 *
	 * @param listener the listener
	 */
	public void addPollListener(PollListener listener){
		listeners.add(listener);
	}

	/**
	 * Gets the queue.
	 *
	 * @return the queue
	 */
	public JMQQueue getQueue() {
		return queue;
	}	
}
