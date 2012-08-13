package org.robe.ta.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robe.ta.data.DataProvider;
import org.robe.ta.data.jpa.Telephone;

public class ThirdTabPanel extends JPanel 
{
    private final Log log;
    
	private BeanTableModel model;
	private DataProvider dataFacade;
   
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable DataBaseTable;
    private javax.swing.JButton DoQuery;
    private JTextField filterText;
    
	public ThirdTabPanel(DataProvider dataFacade) 
	{
		log = LogFactory.getLog(ThirdTabPanel.class); 
		this.dataFacade = dataFacade;
		
        initComponents();
        
        DataBaseTable.setVisible(false);
        setSize(500,420);
    }
	
	private void newFilter() 
	{
		 RowFilter<BeanTableModel, Object> rf = null;
	        //If current expression doesn't parse, don't update.
	        try 
	        {
	            rf = RowFilter.regexFilter(filterText.getText());
	        } 
	        catch (java.util.regex.PatternSyntaxException e) 
	        {
	            return;
	        }
	        TableRowSorter<BeanTableModel> sorter = (TableRowSorter<BeanTableModel>) DataBaseTable.getRowSorter();
	        
	        sorter.setRowFilter(rf);
	}

    private void initComponents() 
    {
        DataBaseTable = new javax.swing.JTable();
        DataBaseTable.setAutoCreateRowSorter(true);

        JLabel l1 = new JLabel("Filter Text:", SwingConstants.TRAILING);
        filterText = new JTextField(22);

        //Whenever filterText changes, invoke newFilter.
        filterText.getDocument().addDocumentListener
        (
                new DocumentListener() 
                {
                    public void changedUpdate(DocumentEvent e) 
                    {
                        newFilter();
                    }
                    public void insertUpdate(DocumentEvent e) 
                    {
                        newFilter();
                    }
                    public void removeUpdate(DocumentEvent e) 
                    {
                        newFilter();
                    }
                }
         );
        l1.setLabelFor(filterText);
        
        DoQuery = new javax.swing.JButton();
        DoQuery.setActionCommand("do");
        final JButton addButton = new JButton("Add");
        addButton.setActionCommand("add");
        
        setLayout(new BorderLayout());
        
        addButton.addActionListener(new ActionListener() 
        {	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(e.getSource() == addButton)
				{
					model.insertRow(new Telephone());
					//refill table of beans
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
        
        DoQuery.setText("Show All");
        DoQuery.addActionListener(new java.awt.event.ActionListener() 
        {
            public void actionPerformed(java.awt.event.ActionEvent evt) 
            {
            	if(evt.getSource() == DoQuery)
            		QueryTable(evt);
            }
        });
        
        JPanel panel = new JPanel();
        panel.add(l1);
        panel.add(filterText);
        panel.add(DoQuery);
        panel.add(addButton);
        
        add(panel, BorderLayout.SOUTH);
        
    }

    private void QueryTable(java.awt.event.ActionEvent evt) 
    {
        try 
        {
        	List<Telephone> beans = (List<Telephone>) dataFacade.getAllTelephones();

            model = new BeanTableModel(beans);
            model.addTableModelListener(new TableModelListener() 
            {				
				@Override
				public void tableChanged(TableModelEvent e) 
				{					
					if (e.getType() == TableModelEvent.UPDATE)  
					{  
						int row = DataBaseTable.getSelectedRow();
						BeanTableModel tableModel = (BeanTableModel) DataBaseTable.getModel();
						Telephone telephone = tableModel.getBeans().get(row);
						
//						int id = Integer.parseInt((String) DataBaseTable.getValueAt(row, 0));
//						String name = DataBaseTable.getValueAt(row, 1) != null ? (String)DataBaseTable.getValueAt(row, 1) : null;
//						String description = DataBaseTable.getValueAt(row, 2) != null ? (String)DataBaseTable.getValueAt(row, 2) : null;
//						BigInteger telephoneNumber = DataBaseTable.getValueAt(row, 3) != null ? new BigInteger((String)DataBaseTable.getValueAt(row, 3)) : null;
						try 
						{ 
							dataFacade.updateTelephone(telephone);
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
							BeanTableModel tableModel = (BeanTableModel) DataBaseTable.getModel();
							List<Telephone> beans = tableModel.getBeans();
							Telephone telephone = beans.get(beans.size() - 1);
							dataFacade.saveTelephone(telephone);
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