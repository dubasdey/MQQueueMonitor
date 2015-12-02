package org.erc.qmm.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.xml.parsers.ParserConfigurationException;

import org.erc.qmm.util.Log;
import org.xml.sax.SAXException;

/**
 * The Class QueueListDataModel.
 */
public class QueueListDataModel extends AbstractListModel<QueueConfig> {

	private static Log log = Log.getLog(QueueListDataModel.class);
			
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3504381659399672144L;
	
	/** The queues. */
	private List<QueueConfig> queues;
	
	/**
	 * Reload.
	 *
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public synchronized void reload() throws SAXException, IOException, ParserConfigurationException{
		ConfigManager config = new ConfigManager();
		queues = config.loadQueues();
		if(queues!=null && queues.size()>0){
			fireContentsChanged(this, 0, queues.size()-1);
		}
	}
	
	/**
	 * Save.
	 */
	public synchronized void save(){
		ConfigManager config = new ConfigManager();
		try {
			config.saveQueues(queues);
		} catch (IOException e) {
			log.error(e);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		return queues!=null?queues.size():0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public QueueConfig getElementAt(int index) {
		if(queues!=null && queues.size()> index){
			return queues.get(index);
		}
		return null;
	}
	
	/**
	 * Removes the.
	 *
	 * @param queue the queue
	 */
	public void remove(QueueConfig queue){
		if(queues!=null){
			queues.remove(queue);
			fireContentsChanged(this, 0, queues.size()-1);
		}	
	}
	
	/**
	 * Adds the.
	 *
	 * @param queue the queue
	 */
	public void add(QueueConfig queue){
		if(queues==null){
			queues = new ArrayList<QueueConfig>();
		}	
		if(queue!=null){
			queues.add(queue);
			fireContentsChanged(this, 0, queues.size()-1);
		}
	}	
	
	/**
	 * Removes the.
	 *
	 * @param queuePos the queue pos
	 */
	public void remove(int queuePos){
		if(queues!=null){
			queues.remove(queuePos);
			fireContentsChanged(this, 0, queues.size()-1);
		}	
	}

}
