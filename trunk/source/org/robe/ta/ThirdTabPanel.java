package org.robe.ta;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class ThirdTabPanel extends JPanel 
{
    private static final Log log;
    
	private MyTableModel model;
	private SQLHelper sqlHelper;
    
    private ArrayList<String[]> MyArray;
   
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable DataBaseTable;
    private javax.swing.JButton DoQuery;
    
    static
    {
    	 log = LogFactory.getLog("org.robe.ta.ThirdTabPanel"); 
    }

	public ThirdTabPanel(SQLHelper sqlHelper) 
	{
		this.sqlHelper = sqlHelper;
		MyArray = new ArrayList<String[]>();
		
        initComponents();
        
        DataBaseTable.setVisible(false);
        setSize(500,420);
    }


    private void initComponents() 
    {
        DataBaseTable = new javax.swing.JTable();
        DoQuery = new javax.swing.JButton();
        DoQuery.setActionCommand("do");
        final JButton but = new JButton("Add");
        but.setActionCommand("add");
        
        setLayout(new BorderLayout());
        //setLayout(new GridLayout(0, 2));
        
        but.addActionListener(new ActionListener() 
        {	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(e.getSource() == but)
				{
					model.insertRow(new String[sqlHelper.getNumberOfColumns()]);
					QueryTable(e);
				}
			}
		});
        
        DataBaseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] { ""}
            ) {
              Class[] types = new Class [] { java.lang.Object.class};
              boolean[]canEdit=new boolean[]{false};
                    
              public Class getColumnClass(int columnIndex) 
              {
                    return types [columnIndex];
              }
            
              public boolean isCellEditable(int rowIndex, int columnIndex) 
              {
                    return canEdit [columnIndex];
              }
        });
        
        jScrollPane1 = new JScrollPane(DataBaseTable);
        
        add(jScrollPane1);
        
        DoQuery.setText("Do Query");
        DoQuery.addActionListener(new java.awt.event.ActionListener() 
        {
            public void actionPerformed(java.awt.event.ActionEvent evt) 
            {
            	if(evt.getSource() == DoQuery)
            		QueryTable(evt);
            }
        });
        
        JPanel panel = new JPanel();
        panel.add(DoQuery);
        panel.add(but);
        
        add(panel, BorderLayout.SOUTH);
        
    }

    private void QueryTable(java.awt.event.ActionEvent evt) 
    {
        try 
        {            
        	MyArray.clear();
            sqlHelper.fillArray(MyArray);

            model = new MyTableModel(); 
            model.addTableModelListener(new TableModelListener() 
            {				
				@Override
				public void tableChanged(TableModelEvent e) 
				{					
					if (e.getType() == TableModelEvent.UPDATE)  
					{  
						int row=DataBaseTable.getSelectedRow();  
						int id = Integer.parseInt((String) DataBaseTable.getValueAt(row, 0));
						String name = DataBaseTable.getValueAt(row, 1) != null ? (String)DataBaseTable.getValueAt(row, 1) : null;
						String description = DataBaseTable.getValueAt(row, 2) != null ? (String)DataBaseTable.getValueAt(row, 2) : null;
						BigInteger telephone = DataBaseTable.getValueAt(row, 3) != null ? new BigInteger((String)DataBaseTable.getValueAt(row, 3)) : null;
					try 
					{ 
						sqlHelper.updateTelephone(id, telephone, name, description);
					   
					} 
					catch (Exception ex) 
					{  
					        	log.error(ex);
					 }  
				} 
				else if(e.getType() == TableModelEvent.INSERT)
					{					   
						try 
						{ 
							sqlHelper.insertEmptyRow();
						   
						} 
						catch (Exception ex) 
						{  
							log.error(ex);
						} 
					}
				}
			});
			
            DataBaseTable.setModel(model);
            DataBaseTable.setVisible(true);
        }
        catch(Exception e) 
        {
        	log.error(e);
        }
    }

    class MyTableModel extends AbstractTableModel 
    {
        public int getColumnCount() 
        {
            return sqlHelper.getNumberOfColumns();
        }
        
        public int getRowCount() 
        {
            return MyArray.size();
        }
        
        public String getColumnName(int i) 
        {
            return sqlHelper.getTableName(i);
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
}