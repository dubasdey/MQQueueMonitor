package org.erc.qmm;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;

import org.erc.qmm.mq.JMQMessage;
import org.erc.qmm.util.Log;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * The Class QueueItemWindow.
 */
public class QueueItemWindow extends JFrame {

	private static Log log = Log.getLog(QueueItemWindow.class);
			
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 151118672093734401L;

	/** The content panel. */
	private final JPanel contentPanel = new JPanel();

	/** The text pane. */
	private JTextPane textPane;

	/**
	 * Create the dialog.
	 * @wbp.parser.constructor
	 */
	public QueueItemWindow() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(QueueItemWindow.class.getResource(Images.MESSAGE)));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		textPane = new JTextPane();
		textPane.setContentType("text/plain");
		textPane.setEditable(false);
		contentPanel.add(new JScrollPane(textPane));

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			
		JButton cancelButton = new JButton("Close");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		cancelButton.setActionCommand("Close");
		buttonPane.add(cancelButton);
	}
	
	/**
	 * Instantiates a new queue item window.
	 *
	 * @param message the message
	 */
	public QueueItemWindow(JMQMessage message) {
		this();
		try {
			setTitle(MessageFormat.format("Queue Item {0} {1}",message.getPosition(), new String(message.getCorrelationId()) ));
			textPane.setText(message.getMessageContents() + "\r\n\r\n\r\n" + new String(message.getData()));
		} catch (IOException e) {
			log.error(e);
		}
	}

}
