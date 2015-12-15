package org.erc.qmm;

import javax.swing.JFrame;

import java.awt.Toolkit;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingWorker;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JScrollPane;

import org.erc.qmm.explorer.Explorer;
import org.erc.qmm.explorer.MessageDataModel;
import org.erc.qmm.i18n.Messages;
import org.erc.qmm.mq.JMQMessage;
import org.erc.qmm.mq.JMQQueue;
import org.erc.qmm.mq.MessageReadedListener;
import org.erc.qmm.util.Log;


/**
 * The Class ExplorerWindow.
 */
public class ExplorerWindow extends JFrame {

	private static Log log = Log.getLog(ExplorerWindow.class);
			
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4239197809052439387L;

	/** The queue. */
	private JMQQueue queue;
	
	/** The table. */
	private JTable table;
	
	/** The lb status. */
	private JLabel lbStatus;
	
	/** The pb status. */
	private JProgressBar pbStatus;
	
	/** The model. */
	private MessageDataModel model;
	
	/**
	 * Create the application.
     * @wbp.parser.constructor
     */
	public ExplorerWindow() {
		initialize();
	}

	/**
	 * Instantiates a new explorer window.
	 *
	 * @param queue the queue
	 */
	public ExplorerWindow(JMQQueue queue) {
		this();
		this.queue = queue;
		loadMessages();
	}

	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
	
		setTitle(Messages.getString("ExplorerWindow.title")); //$NON-NLS-1$
		setIconImage(Toolkit.getDefaultToolkit().getImage(ExplorerWindow.class.getResource(Images.SEARCH))); //$NON-NLS-1$
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu(Messages.getString("ExplorerWindow.file")); //$NON-NLS-1$
		menuBar.add(mnFile);
		
		JMenuItem mntmClose = new JMenuItem(Messages.getString("ExplorerWindow.close")); //$NON-NLS-1$
		mnFile.add(mntmClose);
		mnFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		JMenu mnActions = new JMenu(Messages.getString("ExplorerWindow.actions")); //$NON-NLS-1$
		menuBar.add(mnActions);
		
		JMenuItem mntmReload = new JMenuItem(Messages.getString("ExplorerWindow.reload")); //$NON-NLS-1$
		mnActions.add(mntmReload);
		mntmReload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.clearMessages();
				loadMessages();
			}
		});
		
		JMenuItem mntmViewSelected = new JMenuItem(Messages.getString("ExplorerWindow.viewSelected")); //$NON-NLS-1$
		mnActions.add(mntmViewSelected);
		mntmViewSelected.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.clearMessages();
				loadMessages();
			}
		});
		
		model = new MessageDataModel();
		
		addJTable();
		
		JPanel panelStatusBar = new JPanel();
		panelStatusBar.setBorder(new EmptyBorder(1, 5, 1, 5));
		getContentPane().add(panelStatusBar, BorderLayout.SOUTH);
		panelStatusBar.setLayout(new BoxLayout(panelStatusBar, BoxLayout.X_AXIS));
		
		lbStatus = new JLabel(Messages.getString("ExplorerWindow.defaultStatus")); //$NON-NLS-1$
		panelStatusBar.add(lbStatus);
		
		pbStatus = new JProgressBar();
		pbStatus.setIndeterminate(true);
		panelStatusBar.add(pbStatus);
	}
	
	
	/**
	 * Adds the j table.
	 */
	private void addJTable(){
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		table.setModel(model);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				try {
					int selectedPos = table.getSelectedRow();
					if(selectedPos>-1){
						JMQMessage message = model.getItem(selectedPos);
						if(message!=null){
							QueueItemWindow dialog = new QueueItemWindow(message);
							dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
							dialog.setVisible(true);
						}
					}
				} catch (Exception ex) {
					log.error(ex);
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
	
	
	/**
	 * Load messages.
	 */
	private void loadMessages(){
		
		log.debug("Loading messages");
		
		SwingWorker<List<JMQMessage>,Object> poller = new SwingWorker<List<JMQMessage>,Object>(){
			@Override
			protected List<JMQMessage> doInBackground() throws Exception {
				lbStatus.setVisible(true);
				pbStatus.setIndeterminate(true);
				pbStatus.setVisible(true);
				lbStatus.setText(Messages.getString("ExplorerWindow.loading_start")); //$NON-NLS-1$
				final Explorer explorer = new Explorer(queue);
				explorer.addMessageReadedListener(new MessageReadedListener() {
					public void messageReaded(JMQMessage message) {
						int max = explorer.getDepth();
						int cur = message.getPosition();
						lbStatus.setText(MessageFormat.format(Messages.getString("ExplorerWindow.loading_messages"), cur,max)); //$NON-NLS-1$
						pbStatus.setIndeterminate(false);
						pbStatus.setValue(cur);
						pbStatus.setMaximum(max);
						model.addMessage(message);
					}
				});
				return explorer.readAll();
			}
			
			@Override
			protected void done() {
				super.done();
				log.debug("Done loading messages");
				lbStatus.setVisible(true);
				pbStatus.setVisible(false);
				try {
					List<JMQMessage> messages = get();
					lbStatus.setText(messages.size()  + Messages.getString("ExplorerWindow.loaded_messages")); //$NON-NLS-1$
				} catch (InterruptedException e) {
					log.error(e);
				} catch (ExecutionException e) {
					log.error(e);
				}
			}
			
		};
		poller.execute();
	}

}
