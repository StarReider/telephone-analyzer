package org.robe.ta;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robe.ta.conf.ConfigurationReader;
import org.robe.ta.data.DataFacade;
import org.robe.ta.data.DataFactory;
import org.robe.ta.ui.MainFrame;

public class TelephoneAnalyzer 
{
	private final Log log;
	
	public TelephoneAnalyzer() throws Exception
	{
		log = LogFactory.getLog(TelephoneAnalyzer.class); 
		try 
		{
			init();
		} 
		catch (Exception e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			throw e;
		}
	}

	private void init() throws Exception 
	{
		log.info("Configuring starting...");
		ConfigurationReader configurationReader = new ConfigurationReader();
		log.info("Configuring done");    
		
    	log.info("TelephoneAnalyzer v." + configurationReader.getVersion());
    	
    	log.info("Initialize db subsystem starting...");    	    	
    	DataFactory dataFactory = DataFactory.getInstance();
    	DataFacade dataFacade = dataFactory.getDataFacade(configurationReader.getMode(), configurationReader.getJDBCURL());
    	log.info("Initialize db subsystem done");    	
    	
    	log.info("Initialize GUI subsystem starting...");
		createGUI(dataFacade, configurationReader.getVersion());
    	log.info("Initialize GUI subsystem done");
	}
	
	private void createGUI(final DataFacade dataFacade, final String version) 
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			@Override
			public void run() 
			{
				try
				{
					new MainFrame(dataFacade, version);
				} 
				catch (Exception e) 
				{
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					System.exit(-1);
				}
			}
		});
	}

	public static void main(String[] args) throws Exception 
	{
		new TelephoneAnalyzer();
	}
}