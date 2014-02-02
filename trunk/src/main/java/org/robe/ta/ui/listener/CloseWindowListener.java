/**
 * 
 */
package org.robe.ta.ui.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robe.ta.data.DataProvider;

/**
 * @author Roman
 *
 */
public class CloseWindowListener extends WindowAdapter 
{
        private final static Log log;
        private final DataProvider dataFacade;
        
        public CloseWindowListener(DataProvider dataFacade)
        {
                this.dataFacade = dataFacade;
        }
        
        static
        {
                log = LogFactory.getLog(CloseWindowListener.class);
        }
        
        @Override
        public void windowClosing(WindowEvent e) 
        {
                try 
                {
                        dataFacade.close();
                } 
                catch (Exception exp) 
                {
                        log.error(exp);
                        JOptionPane.showMessageDialog(null, exp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(-1);
                }
                System.exit(0);
        }
}