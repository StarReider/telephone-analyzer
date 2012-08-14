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
		String org = orgField.getSelectedItem().toString();
		
		if(org == null || org.length() == 0)
		{
			log.warn("Organization is empty.");
			JOptionPane.showMessageDialog(null, "Organization is empty.", "Error", JOptionPane.ERROR_MESSAGE);			
			return;
		}
		
		String tel = telField.getText().replaceAll("[^\\d]", "");
		
		if(tel == null || tel.length() == 0)
		{
			log.warn("Telephone is empty.");
			JOptionPane.showMessageDialog(null, "Telephone is empty.", "Error", JOptionPane.ERROR_MESSAGE);			
			return;
		}
		
		if(tel.length() == 11)
			tel = tel.substring(1);

		try 
		{		
			Telephone telephone = dataFacade.getTelephone(tel);
			if(telephone == null)
			{
				telephone = new Telephone();
				telephone.setName(org);
			
				telephone.setTelephone(new BigInteger(tel));
			
				dataFacade.saveTelephone(telephone);
			}
			else
			{
				telephone.setName(org);
				dataFacade.updateTelephone(telephone);
			}

			telField.setText("");
			orgField.setSelectedItem("");
		} 
		catch (Exception e1) 
		{
			log.error(e1);
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Telephone can not be saved!", JOptionPane.ERROR_MESSAGE);			
		}	
	}
}