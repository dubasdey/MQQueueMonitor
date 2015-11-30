package org.erc.qmm.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.erc.qmm.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The Class Config.
 */
public class Config {

	/**
	 * Instantiates a new config.
	 */
	public Config(){}
	
	/**
	 * Gets the text.
	 *
	 * @param parent the parent
	 * @param tagName the tag name
	 * @return the text
	 */
	private String getText(Element parent, String tagName){
		NodeList list = parent.getElementsByTagName(tagName);
		String content = null;
		if(list!=null && list.getLength()>0){
			Node node = list.item(0);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				content = eElement.getFirstChild().getNodeValue();
			}
		}
		return content;
	}
	
	/**
	 * Gets the int.
	 *
	 * @param parent the parent
	 * @param tagName the tag name
	 * @return the int
	 */
	private int getInt(Element parent, String tagName){
		String str = getText(parent,tagName);
		int val=0;
		if(str!=null && str.length()>0){
			val =Integer.valueOf(str);
		}
		return val;
	}
	
	/**
	 * Load queues.
	 *
	 * @return the list
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public List<QueueConfig> loadQueues() throws SAXException, IOException, ParserConfigurationException{
		List<QueueConfig> items= new ArrayList<QueueConfig>();
		File xmlFile = new File("config/config.xml");
		if(xmlFile.exists()){
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList queuesList = doc.getElementsByTagName("queue");
			for (int i = 0; i < queuesList.getLength(); i++) {
				Node node = queuesList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					QueueConfig queue = new QueueConfig();
					queue.setDesc(eElement.getAttribute("desc"));
					queue.setHost(getText(eElement,"host"));
					queue.setPort(getInt(eElement,"port"));
					queue.setPollTime(getInt(eElement,"poll"));
					queue.setManager(getText(eElement,"manager"));
					queue.setChannel(getText(eElement,"channel"));
					queue.setName(getText(eElement,"name"));
					items.add(queue);
				}
			}
		}
		return items;
	}
	
	/**
	 * Save queues.
	 *
	 * @param queues the queues
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void saveQueues(List<QueueConfig> queues) throws IOException{
		File xmlFile = new File("config/config.xml");
		File configFilder = new File("config");
		
		if(xmlFile.exists()){
			FileUtils.copy(xmlFile, new File("config/config.xml.bak"));
		}
		
		if(!configFilder.exists()){
			configFilder.mkdirs();
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append("<config>\r\n");
		builder.append("\t<queues>\r\n");
		for(QueueConfig queue:queues){
			builder.append("\t\t<queue desc=\"" + queue.getDesc()+"\">\r\n");
			builder.append("\t\t\t<host>").append(queue.getHost()).append("</host>\r\n");
			builder.append("\t\t\t<port>").append(queue.getPort()).append("</port>\r\n");
			builder.append("\t\t\t<channel>").append(queue.getChannel()).append("</channel>\r\n");
			builder.append("\t\t\t<manager>").append(queue.getManager()).append("</manager>\r\n");
			builder.append("\t\t\t<name>").append(queue.getName()).append("</name>\r\n");
			builder.append("\t\t\t<poll>").append(queue.getPollTime()).append("</poll>\r\n");
			builder.append("\t\t</queue>\r\n");
		}
		builder.append("\t</queues>\r\n");
		builder.append("</config>");
		FileUtils.store(xmlFile, builder.toString());
	}
	
	
}


