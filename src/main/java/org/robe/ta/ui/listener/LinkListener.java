package org.robe.ta.ui.listener;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.atomation.jbrowser.interfaces.BrowserAdapter;

public class LinkListener extends BrowserAdapter 
{
        private final Log log = LogFactory.getLog(LinkListener.class);
        private JTextField telField;
        private JComboBox<String> orgField;
        
        public LinkListener(JTextField telField, JComboBox<String> orgField)
        {
                this.telField = telField;
                this.orgField = orgField;
        }
        
        @Override
        public boolean beforeOpen(String uri) 
        {
                try 
                {
                        uri = URLDecoder.decode(uri, "UTF-8");
                } 
                catch (UnsupportedEncodingException e) 
                {
                        log.error(e);
                }
                
                if (uri.startsWith("call:add_tel")) 
                {
                        final String decodedURI = uri;
                        SwingUtilities.invokeLater(new Runnable() 
                        {       
                                @Override
                                public void run() 
                                {
                                        telField.setText(decodedURI.replaceFirst("call:add_tel", ""));
                                }
                        });
                        
            return false;
                }
                else if(uri.startsWith("call:show_org"))
                {
                        final String decodedURI = uri;
                        SwingUtilities.invokeLater(new Runnable() 
                        {       
                                @Override
                                public void run() 
                                {
                                        String[] ent = decodedURI.replaceFirst("call:show_org", "").split(";");
                                        if(ent.length != 2)
                                                return;
                                        
                                        String org = ent[1];
                                        String tel = ent[0];
                                        
                                        telField.setText(tel);
                                        orgField.setSelectedItem(org);
                                }
                        });
                        
            return false;
                }

                return super.beforeOpen(uri);
        }
}