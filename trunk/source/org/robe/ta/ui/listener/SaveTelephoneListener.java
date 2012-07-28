package org.robe.ta.ui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robe.ta.data.DataProvider;
import org.robe.ta.data.jpa.Telephone;

public class SaveTelephoneListener implements ActionListener
{
	private JComboBox<String> orgField;
	private JTextField telField;
	private DataProvider dataFacade;
	
	private final Log log = LogFactory.getLog(SaveTelephoneListener.class);
	
	public SaveTelephoneListener(JComboBox<String> orgField, JTextField telField, DataProvider dataFacade)
	{
		this.orgField = orgField;
		this.telField = telField;
		this.dataFacade = dataFacade;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Telephone telephone = new Telephone();
		telephone.setName(orgField.getSelectedItem().toString());
		
		String tel = telField.getText().replaceAll("[^\\d]", "");
		if(tel.length() == 11)
			tel = tel.substring(1);
		telephone.setTelephone(new BigInteger(tel));
		try 
		{
			dataFacade.createEmptyBean(telephone);
		} 
		catch (Exception e1) 
		{
			log.error(e1);
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Telephone can not be saved!", JOptionPane.ERROR_MESSAGE);			
		}	
	}
}
