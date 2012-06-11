package org.robe.ta.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class ArrayTableModel extends AbstractTableModel 
{
	private ArrayList<String[]> MyArray;
	
	public ArrayTableModel(ArrayList<String[]> MyArray)
	{
		this.MyArray = MyArray;
	}
        public int getColumnCount() 
        {
            return 4;
        }
        
        public int getRowCount() 
        {
            return MyArray.size();
        }
        
        public String getColumnName(int i) 
        {
        	switch(i)
        	{
        		case 0:
        			return "ID";
        		case 1:
        			return "TELEPHONE";
        		case 2:
        			return "NAME";
        		case 3:
        			return "DESCRIPTION";
        	}
			return null;
        }
        
        public java.lang.Object getValueAt(int row, int column) 
        {
        	String[] data = MyArray.get(row);
        	return data[column];
        }
        
        public Class<? extends Object> getColumnClass(int c) 
        {
            return getValueAt(0, c).getClass();
        }
        
        public boolean isCellEditable(int row, int col) 
        {
        	if(col > 0)
                return true;
        	else
        		return false;
        }
        
        public void setValueAt(Object value, int row, int col) 
        {
        	String[] data = MyArray.get(row);
        	data[col] = (String) value;
            fireTableCellUpdated(row, col);
        }
        
        public void insertRow(String[] value) 
        {
        	MyArray.add(value);
        	fireTableRowsInserted(MyArray.size(), MyArray.size());
        }
    }