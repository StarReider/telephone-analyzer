package org.robe.ta.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robe.ta.Telephone;
import org.robe.ta.data.DataFacade;

public class ThirdTabPanel extends JPanel 
{
    private final Log log;
    
	private BeanTableModel model;
	private DataFacade dataFacade;
   
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable DataBaseTable;
    private javax.swing.JButton DoQuery;

	public ThirdTabPanel(DataFacade dataFacade) 
	{
		log = LogFactory.getLog(ThirdTabPanel.class); 
		this.dataFacade = dataFacade;
		
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
        
        but.addActionListener(new ActionListener() 
        {	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(e.getSource() == but)
				{
					model.insertRow(new String[4]);
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
        	List<Telephone> beans = dataFacade.getAllTelephones();

            model = new BeanTableModel(beans);
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
						BigInteger telephoneNumber = DataBaseTable.getValueAt(row, 3) != null ? new BigInteger((String)DataBaseTable.getValueAt(row, 3)) : null;
					try 
					{ 
						dataFacade.updateTelephone(id, telephone, name, description);
					   
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
							dataFacade.insertEmptyRow();
						   
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
}