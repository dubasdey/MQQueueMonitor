package org.erc.qmm.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class QueueListDataModel extends AbstractListModel<Queue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3504381659399672144L;
	private List<Queue> queues;
	
	public synchronized void reload() throws SAXException, IOException, ParserConfigurationException{
		Config config = new Config();
		queues = config.loadQueues();
		if(queues!=null && queues.size()>0){
			fireContentsChanged(this, 0, queues.size()-1);
		}
	}
	
	public synchronized void save(){
		Config config = new Config();
		try {
			config.saveQueues(queues);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public int getSize() {
		return queues!=null?queues.size():0;
	}

	@Override
	public Queue getElementAt(int index) {
		if(queues!=null && queues.size()> index){
			return queues.get(index);
		}
		return null;
	}
	
	public void remove(Queue queue){
		if(queues!=null){
			queues.remove(queue);
			fireContentsChanged(this, 0, queues.size()-1);
		}	
	}
	public void add(Queue queue){
		if(queues==null){
			queues = new ArrayList<Queue>();
		}	
		if(queue!=null){
			queues.add(queue);
			fireContentsChanged(this, 0, queues.size()-1);
		}
	}	
	public void remove(int queuePos){
		if(queues!=null){
			queues.remove(queuePos);
			fireContentsChanged(this, 0, queues.size()-1);
		}	
	}

}
