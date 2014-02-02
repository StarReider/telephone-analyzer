package org.robe.ta.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robe.ta.data.DataProvider;

import ru.atomation.jbrowser.impl.JBrowserCanvas;
import ru.atomation.jbrowser.impl.JBrowserComponent;
import ru.atomation.jbrowser.impl.JComponentFactory;
import ru.atomation.jbrowser.interfaces.BrowserAdapter;

        public class TabbedComponentFactory extends JComponentFactory<Canvas>
        {
                protected final JTabbedPane tabContainer;
                private final Log log = LogFactory.getLog(TabbedComponentFactory.class);
                private JBrowserComponent<Canvas> browser;
                JPanel panel;

                public TabbedComponentFactory(JTabbedPane tabContainer, DataProvider dataFacade, JPanel panel) 
                {
                        super(JBrowserCanvas.class);
                        this.tabContainer = tabContainer;
                        this.panel = panel;
                }

                @Override
                @SuppressWarnings("unchecked")
                public JBrowserComponent<Canvas> createBrowser(final JBrowserComponent<?> parent, boolean attachOnCreation,long flags, boolean displayable) 
                        
                {
                                final JBrowserComponent<Canvas> browser = (JBrowserComponent<Canvas>) super
                                        .createBrowser(parent, attachOnCreation, flags, displayable);
                                
                                if(parent != null)
                                {
//                                      panel.remove(parent.getComponent());
//                                      panel.add(browser.getComponent(), BorderLayout.CENTER);                                 
                                        
                                        final JFrame frame = new JFrame();
                                        JPanel panel = new JPanel();
                                        frame.add(panel);
                                                                                
                                        log.debug("fake frame is ready");
                                        
                                        browser.addBrowserListener(new BrowserAdapter() 
                                        {
                                                @Override
                                                public void onLoadingEnded() 
                                                {
                                                        if(!browser.getUrl().contains("cian.ru"))
                                                        {
                                                                log.debug("URL is bring fake frame's value");
                                                                //parent.setUrl(browser.getUrl());
                                                        }

                                                        frame.dispose();
                                                        super.onLoadingEnded();
                                                }
                                        });

                                        panel.add(browser.getComponent());
                                        frame.setVisible(true);
                                        
                                        //tabContainer.addTab("", panel); 
                                }
                                
                                return browser;
                }
        }