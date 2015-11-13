package org.erc.qmm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.text.MessageFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.erc.qmm.config.Queue;
import org.erc.qmm.i18n.Messages;
import org.erc.qmm.monitor.PollEvent;
import org.erc.qmm.monitor.PollListener;
import org.erc.qmm.monitor.QueueMonitor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.RangeType;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class MonitorPanel extends JPanel {

	private static final long serialVersionUID = 1181783867179893568L;

	private long totalEnqueued = 0;
	
	private long totalDequeued = 0;
	
	private TimeSeriesCollection range;
	
	private JLabel alertLabel;
	
	private JLabel itemsInputLabel;
	
	private JLabel itemsOutputLabel;
	
	private JFreeChart chart;
	
	private QueueMonitor monitor;

	private long startTime=0;
	private long inputPerSecond = 0;
	private long outputPerSecond = 0;
	private long maxOutPerSecond =0;
	private long maxInPerSecond = 0;
	private long lastTime = 0;
	
	/**
	 * Create the panel.
	 */
	public MonitorPanel() {

		range = new TimeSeriesCollection(new TimeSeries(Messages.getString("MonitorPanel.depth"))); //$NON-NLS-1$
		range.addSeries(new TimeSeries(Messages.getString("MonitorPanel.enqueued"))); //$NON-NLS-1$
		range.addSeries(new TimeSeries(Messages.getString("MonitorPanel.processed"))); //$NON-NLS-1$
		
		DateAxis xAxis = new DateAxis(Messages.getString("MonitorPanel.time")); //$NON-NLS-1$
		xAxis.setAutoRange(true);
		xAxis.setAutoRangeMinimumSize(100);
		xAxis.setMinimumDate(new Date());
		//xAxis.setDefaultAutoRange(new DateRange(0,10000000));
		NumberAxis yAxis = new NumberAxis(Messages.getString("MonitorPanel.messages")); //$NON-NLS-1$
		yAxis.setAutoRange(true);
		yAxis.setMinorTickCount(1);
		yAxis.setRangeType(RangeType.POSITIVE); 
		yAxis.setAutoRangeIncludesZero(true);
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		XYPlot plot = new XYPlot(range, xAxis,yAxis,new StandardXYItemRenderer());
		plot.setBackgroundPaint(Color.black);
		plot.setDomainGridlinePaint(Color.green);
		plot.setRangeGridlinePaint(Color.green);	
		plot.setNoDataMessage(Messages.getString("MonitorPanel.collecting_data")); //$NON-NLS-1$

		chart = new JFreeChart(plot);
		chart.setBorderVisible(false);

		plot.setBackgroundPaint(Color.black);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		final ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(60000.0); // 60 seconds

		setLayout(new BorderLayout());
		
		ChartPanel chartPanel = new ChartPanel(chart);
		add(chartPanel);
		
		JPanel alertPanel = new JPanel(new FlowLayout());
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

	private void add(int depth, int processed, int enqueued){
		Millisecond now = new Millisecond();

		totalEnqueued += enqueued;
		totalDequeued += processed;
		
		if(lastTime <1){
			lastTime = startTime;
		}
		
		long time = (System.currentTimeMillis() - lastTime) / 1000;
		
		inputPerSecond  = (enqueued / time);
		outputPerSecond = (processed / time);
		
		if(inputPerSecond>maxInPerSecond){
			maxInPerSecond = inputPerSecond;
		}
		if(outputPerSecond>maxOutPerSecond){
			maxOutPerSecond = outputPerSecond;
		}
		lastTime = System.currentTimeMillis();
		
		itemsInputLabel.setText(MessageFormat.format(Messages.getString("MonitorPanel.totalinfoInput"),totalEnqueued,inputPerSecond,maxInPerSecond)); //$NON-NLS-1$
		itemsOutputLabel.setText(MessageFormat.format(Messages.getString("MonitorPanel.totalinfoOutput"),totalDequeued,outputPerSecond,maxOutPerSecond)); //$NON-NLS-1$
		
		
		range.getSeries(0).add(now,depth);
		range.getSeries(1).add(now,enqueued);
		range.getSeries(2).add(now,processed);
		
	}
	
	private void setAlarm(boolean on){
		if (on){
			alertLabel.setIcon(new ImageIcon(getClass().getResource(Images.ALERT))); //$NON-NLS-1$
			alertLabel.setText(Messages.getString("MonitorPanel.alert_bad")); //$NON-NLS-1$
		}else{
			alertLabel.setIcon(new ImageIcon(getClass().getResource(Images.OK))); //$NON-NLS-1$
			alertLabel.setText(Messages.getString("MonitorPanel.alert_ok")); //$NON-NLS-1$
		}
	
	}

	public void loadWith(Queue queue){		
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
	
	public Queue getQueue(){
		return monitor.getQueue();
	}
}
