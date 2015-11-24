package org.erc.qmm.explorer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * The Class MessageDataModel.
 */
public class MessageDataModel extends AbstractTableModel{

	 /** The Constant serialVersionUID. */
 	private static final long serialVersionUID = -3260800977993048780L;

	 /** The Constant COL_ID. */
 	public static final int COL_ID = 0;
     
     /** The Constant COL_DATE. */
     public static final int COL_DATE = 1;
     
     /** The Constant COL_DATA. */
     public static final int COL_DATA = 2;

     /** The column names. */
     protected String[] columnNames = new String[]{
    	 "Id",
    	 "Date",
    	 "Data"
     };
     
     /** The messages. */
     protected List<JMQMessage> messages;

     /**
      * Instantiates a new message data model.
      */
     public MessageDataModel() {
         messages = new ArrayList<JMQMessage>();
     }
     
     /**
      * Clear messages.
      */
     public void clearMessages(){
    	 if(!messages.isEmpty()){
    		 messages.clear();
    		 fireTableDataChanged();
    	 }
     }
     
     /**
      * Adds the message.
      *
      * @param message the message
      */
     public void addMessage(JMQMessage message){
    	 this.messages.add(message);
    	 fireTableDataChanged();
     }
     
     /**
      * Adds the messages.
      *
      * @param messages the messages
      */
     public void addMessages(List<JMQMessage> messages){
    	 this.messages = messages;
    	 fireTableDataChanged();
     }
     

     /* (non-Javadoc)
      * @see javax.swing.table.AbstractTableModel#getColumnName(int)
      */
     public String getColumnName(int column) {
         return columnNames[column];
     }

     /* (non-Javadoc)
      * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
      */
     public boolean isCellEditable(int row, int column) {
    	 return false;
     }

     /* (non-Javadoc)
      * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
      */
     @SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int column) {
    	 return String.class;
     }

     /* (non-Javadoc)
      * @see javax.swing.table.TableModel#getValueAt(int, int)
      */
     public Object getValueAt(int row, int column) {
    	 JMQMessage msg = messages.get(row);
         switch (column) {
             case COL_ID:
                return new String(msg.getCorrelationId());
             case COL_DATE:
                return msg.getPutDateTime();
             case COL_DATA:
			try {
				return new String(msg.getData());
			} catch (IOException e) {
			}
             default:
                return new Object();
         }
     }

     /* (non-Javadoc)
      * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
      */
     public void setValueAt(Object value, int row, int column) { }

     /* (non-Javadoc)
      * @see javax.swing.table.TableModel#getRowCount()
      */
     public int getRowCount() {
         return messages.size();
     }

     /* (non-Javadoc)
      * @see javax.swing.table.TableModel#getColumnCount()
      */
     public int getColumnCount() {
         return columnNames.length;
     }

     /**
      * Gets the item.
      *
      * @param pos the pos
      * @return the item
      */
     public JMQMessage getItem(int pos){
    	 return messages.get(pos);
     }
	     
}
