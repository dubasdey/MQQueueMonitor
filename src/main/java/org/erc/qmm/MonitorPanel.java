package org.erc.qmm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.erc.qmm.config.QueueConfig;
import org.erc.qmm.i18n.Messages;
import org.erc.qmm.monitor.PollEvent;
import org.erc.qmm.monitor.PollListener;
import org.erc.qmm.monitor.QueueMonitor;
import org.erc.qmm.mq.JMQQueue;
import org.erc.qmm.util.GraphicUtils;
import org.erc.qmm.util.Log;

/**
 * The Class MonitorPanel.
 */
public class MonitorPanel extends JPanel {

	private static Log log = Log.getLog(MonitorPanel.class);
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1181783867179893568L;

	/** The total enqueued. */
	private long totalEnqueued = 0;
	
	/** The total dequeued. */
	private long totalDequeued = 0;

	/** The alert label. */
	private JLabel alertLabel;
	
	/** The items input label. */
	private JLabel itemsInputLabel;
	
	/** The items output label. */
	private JLabel itemsOutputLabel;
	
	/** The chart. */
	private GraphPanel chart;
	
	/** The monitor. */
	private QueueMonitor monitor;

	/** The start time. */
	private long startTime=0;
	
	/** The input per second. */
	private long inputPerSecond = 0;
	
	/** The output per second. */
	private long outputPerSecond = 0;
	
	/** The max out per second. */
	private long maxOutPerSecond =0;
	
	/** The max in per second. */
	private long maxInPerSecond = 0;
	
	/** The last time. */
	private long lastTime = 0;
	
	/**
	 * Create the panel.
	 */
	public MonitorPanel() {
		chart = new GraphPanel();
		setLayout(new BorderLayout());
		add(chart);
		
		JPanel leyendPanel = new JPanel(new FlowLayout());
		JPanel alertPanel = new JPanel(new FlowLayout());
		
		JLabel inLabel = new JLabel(GraphicUtils.createColorIcon(Color.GREEN));
		inLabel.setText(Messages.getString("MonitorPanel.enqueued")); //$NON-NLS-1$
		leyendPanel.add(inLabel);
		
		JLabel outLabel = new JLabel(GraphicUtils.createColorIcon(Color.RED));
		outLabel.setText(Messages.getString("MonitorPanel.processed")); //$NON-NLS-1$
		leyendPanel.add(outLabel);
		
		JLabel depthLabel  = new JLabel(GraphicUtils.createColorIcon(Color.blue));
		depthLabel.setText(Messages.getString("MonitorPanel.messages")); //$NON-NLS-1$
		leyendPanel.add(depthLabel);
		
		leyendPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.gray) );
		
		alertPanel.add(leyendPanel);
		
		
		alertLabel = new JLabel(new ImageIcon(getClass().getResource(Images.OK))); //$NON-NLS-1$
		alertLabel.setText(Messages.getString("MonitorPanel.alert_ok")); //$NON-NLS-1$
		alertPanel.add(alertLabel);
		
		itemsInputLabel = new JLabel(new ImageIcon( getClass().getResource("/org/erc/qmm/img/small/receive.png"))); //$NON-NLS-1$
		itemsInputLabel.setText(MessageFormat.format(Messages.getString("MonitorPanel.totalinfoInput"), 0,0,0)); //$NON-NLS-1$
		alertPanel.add(itemsInputLabel);
		
		itemsOutputLabel = new JLabel(new ImageIcon( getClass().getResource("/org/erc/qmm/img/small/send.png"))); //$NON-NLS-1$
		itemsOutputLabel.setText(MessageFormat.format(Messages.getString("MonitorPanel.totalinfoOutput"), 0,0,0)); //$NON-NLS-1$
		alertPanel.add(itemsOutputLabel);
        add(alertPanel, BorderLayout.SOUTH);
        
	}

	public MonitorPanel(QueueConfig queue) throws Exception {
		this();
		log.debug("Loading monitor for Queue");
		loadWith(queue);
	}
	
	/**
	 * Adds the.
	 *
	 * @param depth the depth
	 * @param processed the processed
	 * @param enqueued the enqueued
	 */
	private void add(int depth, int processed, int enqueued){
		log.debug("Added IN:{0} OUT:{1} DEPTH:{2}",enqueued,processed,depth);
		
		totalEnqueued += enqueued;
		totalDequeued += processed;
		if(lastTime <1){
			lastTime = startTime;
		}
		long time = (System.currentTimeMillis() - lastTime) / 1000;
		lastTime = System.currentTimeMillis();
		
		if(time>0){
			inputPerSecond  = (enqueued / time);
			outputPerSecond = (processed / time);
		
			if(inputPerSecond>maxInPerSecond){
				maxInPerSecond = inputPerSecond;
				log.debug("New max input:{0}",maxInPerSecond);
			}
			if(outputPerSecond>maxOutPerSecond){
				maxOutPerSecond = outputPerSecond;
				log.debug("New max output:{0}",maxOutPerSecond);
			}
			itemsInputLabel.setText(MessageFormat.format(Messages.getString("MonitorPanel.totalinfoInput"),totalEnqueued,inputPerSecond,maxInPerSecond)); //$NON-NLS-1$
			itemsOutputLabel.setText(MessageFormat.format(Messages.getString("MonitorPanel.totalinfoOutput"),totalDequeued,outputPerSecond,maxOutPerSecond)); //$NON-NLS-1$
			chart.addScore(enqueued,processed,depth);
		}
	}
	
	/**
	 * Sets the alarm.
	 *
	 * @param on the new alarm
	 */
	private void setAlarm(boolean on){
		if (on){
			log.debug("Alarm activated");
			alertLabel.setIcon(new ImageIcon(getClass().getResource(Images.ALERT))); //$NON-NLS-1$
			alertLabel.setText(Messages.getString("MonitorPanel.alert_bad")); //$NON-NLS-1$
		}else{
			log.debug("Alarm deactivated");
			alertLabel.setIcon(new ImageIcon(getClass().getResource(Images.OK))); //$NON-NLS-1$
			alertLabel.setText(Messages.getString("MonitorPanel.alert_ok")); //$NON-NLS-1$
		}
	}

	/**
	 * Load with.
	 *
	 * @param queue the queue
	 * @throws Exception 
	 */
	public void loadWith(QueueConfig queue) throws Exception{		
		startTime = System.currentTimeMillis();
		monitor = new QueueMonitor(queue);
		monitor.addPollListener(new PollListener() {
			@Override
			public void action(PollEvent e) {
				add(e.getDepth(),e.getDequeued(), e.getEnqueued());
				if (e.getMaxDepth() * 0.9 <e.getDepth()){
					setAlarm(true);
				}else{
					setAlarm(false);
				}
			}
		});
		monitor.start();
	}
	
	/**
	 * Gets the queue.
	 *
	 * @return the queue
	 */
	public JMQQueue getQueue(){
		return monitor.getQueue();
	}
	
	public void dispose(){
		monitor.stopMonitoring();
	}
}
