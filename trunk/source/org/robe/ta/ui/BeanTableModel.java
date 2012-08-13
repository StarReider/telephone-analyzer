package org.robe.ta.ui;

import java.math.BigInteger;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.robe.ta.data.jpa.Telephone;

public class BeanTableModel extends AbstractTableModel 
{
    	private List<Telephone> beans;
    	int i = 1;
    	
    	public List<Telephone> getBeans() 
    	{
			return beans;
		}

    	public BeanTableModel(List<Telephone> beans)
    	{
    		this.beans = beans;
    	}

		@Override
		public Class<?> getColumnClass(int columnIndex) 
		{
			switch(columnIndex)
			{
				case 0:
					return Integer.class;
				case 1:
					return BigInteger.class;
				case 2:
				case 3:
					return String.class;
			}
			return null;
		}

		@Override
		public int getColumnCount() 
		{
			return 4;
		}

		@Override
		public String getColumnName(int columnIndex) 
		{
        	switch(columnIndex)
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

		@Override
		public int getRowCount() 
		{
			return beans.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) 
		{
			Telephone bean = beans.get(rowIndex);
			switch (columnIndex) 
			{
				case 0:
					return bean.getId();
				case 1:
					return bean.getTelephone();
				case 2:
					return bean.getName();
				case 3:
					return bean.getDescription();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) 
		{
        	if(columnIndex > 0)
                return true;
        	else
        		return false;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
		{
			Telephone bean = beans.get(rowIndex);
			switch (columnIndex) 
			{
				case 1:
					bean.setTelephone((BigInteger) aValue);
					break;
				case 2:
					bean.setName((String) aValue);
					break;
				case 3:
					bean.setDescription((String) aValue);
			}
            fireTableCellUpdated(rowIndex, columnIndex);
		}
		
        public void insertRow(Object bean) 
        {
        	beans.add((Telephone) bean);
        	fireTableRowsInserted(beans.size(), beans.size());
        }
}