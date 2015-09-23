package org.erc.qmm.explorer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class MessageDataModel extends AbstractTableModel{

	 private static final long serialVersionUID = -3260800977993048780L;

	 public static final int COL_ID = 0;
     public static final int COL_DATE = 1;
     public static final int COL_DATA = 2;

     protected String[] columnNames = new String[]{
    	 "Id",
    	 "Date",
    	 "Data"
     };
     
     protected List<JMQMessage> messages;

     public MessageDataModel() {
         messages = new ArrayList<JMQMessage>();
     }
     
     public void clearMessages(){
    	 if(!messages.isEmpty()){
    		 messages.clear();
    		 fireTableDataChanged();
    	 }
     }
     
     public void addMessage(JMQMessage message){
    	 this.messages.add(message);
    	 fireTableDataChanged();
     }
     
     public void addMessages(List<JMQMessage> messages){
    	 this.messages = messages;
    	 fireTableDataChanged();
     }
     

     public String getColumnName(int column) {
         return columnNames[column];
     }

     public boolean isCellEditable(int row, int column) {
    	 return false;
     }

     @SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int column) {
    	 return String.class;
     }

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

     public void setValueAt(Object value, int row, int column) { }

     public int getRowCount() {
         return messages.size();
     }

     public int getColumnCount() {
         return columnNames.length;
     }

     public JMQMessage getItem(int pos){
    	 return messages.get(pos);
     }
	     
}
