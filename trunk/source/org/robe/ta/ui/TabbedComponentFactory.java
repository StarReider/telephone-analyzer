package org.robe.ta.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robe.ta.data.DataProvider;

import ru.atomation.jbrowser.impl.JBrowserCanvas;
import ru.atomation.jbrowser.impl.JBrowserComponent;
import ru.atomation.jbrowser.impl.JComponentFactory;

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
		public JBrowserComponent<Canvas> createBrowser(JBrowserComponent<?> parent, boolean attachOnCreation,long flags, boolean displayable) 
			
		{
				final JBrowserComponent<Canvas> browser = (JBrowserComponent<Canvas>) super
					.createBrowser(parent, attachOnCreation, flags, displayable);
				
				if(parent != null)
				{
					panel.remove(parent.getComponent());
					panel.add(browser.getComponent(), BorderLayout.CENTER);
				}
			
				return browser;
		}
	}