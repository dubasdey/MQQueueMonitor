package org.erc.qmm.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.erc.qmm.config.QueueConfig;
import org.erc.qmm.mq.JMQQueue;
import org.erc.qmm.util.Log;


/**
 * The Class QueueMonitor.
 */
public class QueueMonitor extends Thread {

	private static Log log = Log.getLog(QueueMonitor.class);
			
	/** The listeners. */
	private List<PollListener> listeners;
	
	/** The event. */
	private PollEvent event;
	
	/** The queue. */
	private JMQQueue queue;
	
	private boolean running = false;
	
	/**
	 * Instantiates a new queue monitor.
	 *
	 * @param queue the queue
	 * @throws MQException 
	 */
	public QueueMonitor(QueueConfig queueConfig) throws Exception{
		super();
		setName("QueueMonitor-" + queueConfig.getName());
		setDaemon(true);
		setPriority(MIN_PRIORITY);
		queue = new JMQQueue(queueConfig);
		listeners = new ArrayList<PollListener>();
		event = new PollEvent();
		running = true;
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
		log.debug("Checking data");
		// First stat is dropped (data before monitor starts resetting)
		Map<Integer,Object> items = queue.fetchStats();
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
		log.debug("Checking data {0}", event);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		super.run();
		
		// Init stats on MQ before loop
		checkQueueData();
		
		// Stat lopp
		while (running) {
			checkQueueData();
			emitEvent();
			try {
				Thread.sleep(queue.getConfig().getPollTime() * 1000); // sleep for x seconds
			} catch (InterruptedException e) {
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
	
	public void stopMonitoring(){
		running = false;
		interrupt();
	}
}
