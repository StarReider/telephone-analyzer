package org.robe.ta;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robe.ta.conf.ConfigurationReader;
import org.robe.ta.data.DataFactory;
import org.robe.ta.data.DataProvider;
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
                        log.error(e);
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
        DataProvider dataFacade = dataFactory.getDataFacade(configurationReader.getMode(), configurationReader.getJDBCURL());
        log.info("Initialize db subsystem done");       
        
        log.info("Initialize GUI subsystem starting...");
                createGUI(dataFacade, configurationReader);
        log.info("Initialize GUI subsystem done");
        }
        
        private void createGUI(final DataProvider dataFacade, final ConfigurationReader configurationReader) throws Exception 
        {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                SwingUtilities.invokeLater(new Runnable() 
                {
                        @Override
                        public void run() 
                        {
                                try
                                {
                                        new MainFrame(dataFacade, configurationReader);
                                } 
                                catch (Exception e) 
                                {
                                        log.error(e);
                                        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                        }
                });
        }

        public static void main(String[] args) throws Exception 
        {
                new TelephoneAnalyzer();
        }
}