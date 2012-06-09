package org.robe.ta;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.browser.MozillaExecutor;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;

import ru.atomation.jbrowser.impl.JBrowserBuilder;
import ru.atomation.jbrowser.impl.JBrowserCanvas;
import ru.atomation.jbrowser.impl.JBrowserComponent;
import ru.atomation.jbrowser.impl.JComponentFactory;
import ru.atomation.jbrowser.interfaces.BrowserAdapter;

public class TelephoneAnalyzer 
{
    private static final Log log; 
    private static String VERSION; 
    private static SQLHelper sqlHelper;
	
    static
    {
    	log = LogFactory.getLog("org.robe.ta.TelephoneAnalyzer"); 
 
    	Properties prop = new Properties();
    	try 
    	{
			prop.load(new FileReader("version.txt"));
			VERSION = (String) prop.get("version");
		} 
    	catch (Exception e) 
		{
    		VERSION = null;
    		log.warn(e);
		}
    }
    
    public TelephoneAnalyzer()
    {
    	log.info("TelephoneAnalyzer v." + VERSION);
    	log.info("Initialize db subsystem starting...");
    	
    	try 
    	{
			sqlHelper = new SQLHelper();
		} 
    	catch (Exception e) 
		{
    		JOptionPane.showMessageDialog(null, e.getMessage());
    		System.exit(-1);
		}
    	
    	log.info("Initialize db subsystem done");
    	
    	log.info("Initialize GUI subsystem starting...");
    	try 
    	{
			createGUI();
		} 
    	catch (Exception e) 
    	{
    		JOptionPane.showMessageDialog(null, e.getMessage());
    		System.exit(-1);
		}
    	log.info("Initialize GUI subsystem done");
    }
    
	private void createGUI() throws Exception
	{    			
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		JFrame frame = new JFrame();
		frame.setTitle("TelephoneAnalyzer" + VERSION);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
		frame.addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent e) 
			{
				try 
				{
					sqlHelper.close();
				} 
				catch (Exception e2) 
				{
					log.error(e2);
				}
				System.exit(0);
			}
		});
		
		frame.setSize((int) (screenSize.getWidth() * 0.75f),
				(int) (screenSize.getHeight() * 0.75f));
		frame.setLocationRelativeTo(null);

		JTabbedPane tabContainer = new JTabbedPane();
		tabContainer.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		JComponentFactory<Canvas> canvasFactory = new TabbedComponentFactory(tabContainer);

		new JBrowserBuilder().setBrowserFactory(canvasFactory)
				.buildBrowserManager();

		frame.getContentPane().add(tabContainer, BorderLayout.CENTER);
		frame.setVisible(true);

		JBrowserComponent<?> browser = canvasFactory.createBrowser();
		//browser.setUrl("http://viaduk-podolsk.ru/auto_sell");
		
		tabContainer.addTab("Second Tab", makeSecondTab());
		
		tabContainer.addTab("Third Tab", makeThirdTab());
	}
	
	@SuppressWarnings("serial")
	protected static class CloasableTab extends JPanel 
	{
		private JLabel label;
		private JButton close;
		private Runnable closeAction;

		public CloasableTab(Runnable closeAction) 
		{
			super();

			this.closeAction = closeAction;

			setOpaque(false);

			label = new JLabel();
			close = new JButton("X");

			close.setBorder(new EmptyBorder(0, 0, 0, 0));
			close.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					if (CloasableTab.this.closeAction != null) 
					{
						CloasableTab.this.closeAction.run();
					}
				}
			});

			GroupLayout groupLayout = new GroupLayout(this);
			setLayout(groupLayout);

			groupLayout.setHorizontalGroup(groupLayout
					.createSequentialGroup()
					.addComponent(label, 100, 100, 100)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(close, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE,
							GroupLayout.PREFERRED_SIZE));

			groupLayout.setVerticalGroup(groupLayout
					.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(label).addComponent(close));
		}

		public JLabel getLabel() 
		{
			return label;
		}
	}

	protected static class TabbedComponentFactory extends JComponentFactory<Canvas>
	{
		protected final JTabbedPane tabContainer;

		public TabbedComponentFactory(JTabbedPane tabContainer) 
		{
			super(JBrowserCanvas.class);
			this.tabContainer = tabContainer;
		}

		@Override
		@SuppressWarnings("unchecked")
		public JBrowserComponent<Canvas> createBrowser
		(
			JBrowserComponent<?> parent, boolean attachOnCreation,long flags, boolean displayable) 
			{
				final JBrowserComponent<Canvas> browser = (JBrowserComponent<Canvas>) super
					.createBrowser(parent, attachOnCreation, flags, displayable);

			// i don`t know why swing request JPanel, Canvas work wrong
			final JPanel browserPanel = createBrowserPanel(browser);
			
			browser.addBrowserListener(new BrowserAdapter() 
			{
				@Override
				public void onSetTitle(String title) 
				{
					int index = tabContainer.indexOfComponent(browserPanel);
					CloasableTab tabComponent = (CloasableTab) tabContainer
							.getTabComponentAt(index);
					tabComponent.getLabel().setText(title);
				}

				@Override
				public void onCloseWindow() 
				{
					int index = tabContainer.indexOfComponent(browserPanel);
					if (index != -1) {
						tabContainer.remove(index);
					}

					if (tabContainer.getTabCount() == 0) {
						System.exit(0);
					}
				}
				
				private void match(String text, nsIDOMNode node)
				{
					if(text == null || node.getNodeType() != nsIDOMNode.TEXT_NODE)
						return;
					String patternString = "((8|\\+[0-9]{1,4})?[\\-\\(]?[0-9]{3,6}[\\-\\)]?[0-9\\-]{5,})";
					
					
					Pattern pattern;
					try 
					{
						pattern = Pattern.compile(patternString);
					} 
					catch (PatternSyntaxException e) 
					{
						JOptionPane.showMessageDialog(null, e.getMessage(), "Regular expression is incorrect", JOptionPane.ERROR_MESSAGE);
						return;
					}
					Matcher matcher = pattern.matcher(text);
					String group = null;
					int start, end;
	  
					while (matcher.find()) 
		            {
		            	group = matcher.group();
		            	start = matcher.start();
		            	end = matcher.end();
		            	log.info("group " + group);
		            	
		            	String rep_group = group.replaceAll("[^\\d]", "");
		            	if(rep_group.length() == 11)
		            		rep_group = rep_group.substring(1);
		            	else if(rep_group.length() != 10)
		            		continue;
		            	
		            	log.info("rep_group " + rep_group);
		            	
		            	boolean isFinded = false;
		            	try 
		            	{						
							isFinded = sqlHelper.isTelephoneExist(rep_group);
						} 
		            	catch (SQLException e) 
						{
		            		log.error(e);
							return;
						}
		            	
		            	if(isFinded)
		            	{
		            		try 
		            		{
		            			String before, after;
		            			int i1  = text.indexOf(group);
		            			before = text.substring(0, i1);
		            			i1 = i1 + group.length();
		            			after = text.substring(i1, text.length());
		            			nsIDOMNode parent = node.getParentNode();
		            			nsIDOMElement span = node.getOwnerDocument().createElement("span");
		            			
		            			span.setAttribute("style", "color:green");
		            			
		            			span.appendChild(node.getOwnerDocument().createTextNode(group));
		            			
		            			Object[] ar = {node, parent, span, node.getOwnerDocument().createTextNode(before), node.getOwnerDocument().createTextNode(after)};
		            			array.add(ar);
		            			
//		            			parent.removeChild(node);
//		            			parent.appendChild(node.getOwnerDocument().createTextNode(before));
//		            			parent.appendChild(span);
//		            			parent.appendChild(node.getOwnerDocument().createTextNode(after));
							} 
		            		catch (Exception e) 
		            		{
		            			log.warn(e);
							}
		            		            		
		            		log.info(group + " was finded");
		            	}
		            	else
		            	{
		            		try 
		            		{
		            			String before, after;
		            			int i1  = text.indexOf(group);
		            			before = text.substring(0, i1);
		            			i1 = i1 + group.length();
		            			after = text.substring(i1, text.length());
		            			
		            			nsIDOMNode parent = node.getParentNode();
		            			nsIDOMElement span = node.getOwnerDocument().createElement("span");
		            			nsIDOMNode textNodeNew = node.getOwnerDocument().createTextNode(group);
		            			span.setAttribute("style", "color:red");
		            			//span.appendChild(node);
		            			span.appendChild(node.getOwnerDocument().createTextNode(group));

		            			Object[] ar = {node, parent, span, node.getOwnerDocument().createTextNode(before), node.getOwnerDocument().createTextNode(after)};
		            			array.add(ar);
		            			
//		            			parent.removeChild(node);
//		            			parent.appendChild(node.getOwnerDocument().createTextNode(before));
//		            			parent.appendChild(span);
//		            			parent.appendChild(node.getOwnerDocument().createTextNode(after));
							} 
		            		catch (Exception e) 
		            		{
		            			log.warn(e);
							}
		            		//t = t.replace(group, "<span style='color:red'>" + group + "</span>");
		            		log.info(group + " wasn't finded");
		            	}
		            }
				}
				
				private void recursiveSearch(nsIDOMNode node)
				{
					if(!node.hasChildNodes())
					{
						match(node.getNodeValue(), node);
						return;
					}

					nsIDOMNodeList children = node.getChildNodes();
					for(int i = 0; i < children.getLength(); i++)
					{
						recursiveSearch(children.item(i));
					}
				}
				
				private ArrayList<Object[]> array;
				
				@Override
				public void onLoadingEnded() 
				{
					 MozillaExecutor.mozAsyncExec(new Runnable() 
					 {    
                         @Override
                         public void run() 
                         {
                        	 System.out.println("doc is loaded");
                        	 nsIDOMDocument document = browser.getWebBrowser()
                                                 		.getContentDOMWindow().getDocument();

                             nsIDOMNode containerDiv = document.getElementsByTagName("body").item(0);
                             
                             
                             
                             if(containerDiv == null)
                            	 return;
                             
                             array = new ArrayList<Object[]>();
                             
                             recursiveSearch(containerDiv);
                             
                             System.out.println("SIZE IS " + array.size());
                             
                             ArrayList<List<Object>> dd = new ArrayList<List<Object>>();
                             for(Object[] ar : array)
                             {
                            	 ArrayList<Object> p = new ArrayList<>();
                            	 p.add(ar);
                            	 for(Object[] ar2 : array)
                            	 {
                            		 if(ar[0].equals(ar2[0]))
                            		 {
                            			 p.add(ar2);
                            		 }
                            	 }
                            	 if(p.size() > 1)
                            		 dd.add(p);
                             }
                             
                             
                             for(Object[] ar : array)
                             {
 		            			nsIDOMNode node = (nsIDOMNode)ar[0];
 		            			nsIDOMNode parent = node.getParentNode();
 		            			
 		            			nsIDOMNode span = (nsIDOMNode)ar[2];
 		            			nsIDOMNode before = (nsIDOMNode)ar[3];
 		            			nsIDOMNode after = (nsIDOMNode)ar[4];
 		            			
 		            			if(node == null || parent == null)
 		            			{
 		            				System.out.println(node.getNodeValue() + " hasn't parent?");
 		            				continue;
 		            			}
 		            			
 		            			parent.removeChild(node);
 		            			parent.appendChild(before);
 		            			parent.appendChild(span);
 		            			parent.appendChild(after);
                             }
                         }
                 });
				}
			});

			CloasableTab cloasableTab = new CloasableTab(
					new Runnable() 
					{
		
						@Override
						public void run() 
						{
							int index = tabContainer.indexOfComponent(browserPanel);
							tabContainer.remove(index);
						}
					}
				);

			tabContainer.addTab("New tab", browserPanel);
			tabContainer.setTabComponentAt(tabContainer.getTabCount() - 1,
					cloasableTab);
			tabContainer.setSelectedIndex(tabContainer.getTabCount() - 1);
			return browser;
		}

		protected JPanel createBrowserPanel(final JBrowserComponent<?> browser) 
		{
			JPanel panel = new JPanel(new BorderLayout());
			
			JPanel panel2 = new JPanel();
			final JTextField addressField = new JTextField();
			JButton goButton = new JButton("Go");
			
			goButton.addActionListener(new ActionListener() 
			{	
				@Override
				public void actionPerformed(ActionEvent e) 
				{					
					browser.setUrl(addressField.getText());
				}
			});
			
			panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));
			panel2.add(addressField);
			panel2.add(goButton);
			
			panel.add(browser.getComponent(), BorderLayout.CENTER);
			panel.add(panel2, BorderLayout.NORTH);
			return panel;
		}
	}

	public static void main(String[] args) throws Exception 
	{		
		SwingUtilities.invokeLater(new Runnable() 
		{			
			@Override
			public void run() 
			{
				new TelephoneAnalyzer();
			}
		});
	}

	private JComponent makeThirdTab() 
	{
		return new ThirdTabPanel(sqlHelper);
	}

	private JComponent makeSecondTab() throws Exception 
	{
        JPanel panel = new JPanel(false);
        
        final JTextPane textArea = new JTextPane();
        final JScrollPane scrollPane = new JScrollPane(textArea);
        
        final DefaultHighlighter.DefaultHighlightPainter highlightPainterGreen = 
                new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
        final DefaultHighlighter.DefaultHighlightPainter highlightPainterRed = 
                new DefaultHighlighter.DefaultHighlightPainter(Color.RED);        
                
        JButton button = new JButton("Search");
        button.setActionCommand("search");
        
        final JButton clear_button = new JButton("Clear");
        clear_button.setActionCommand("clear");
        
        clear_button.addActionListener(new ActionListener() 
        {			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				if(arg0.getSource() == clear_button)
				{
					textArea.setText("");
				}
			}
		});
        
        
        //panel.setLayout(new GridLayout(0, 2));
        panel.setLayout(new BorderLayout());
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        final JTextField patternField = new JTextField("((8|\\+[0-9]{1,4})?[\\-\\(]?[0-9]{3,6}[\\-\\)]?[0-9\\-]{5,})");
        JLabel label = new JLabel("Pattern ");
        
        panel3.add(button);
        panel3.add(clear_button);               
        
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));
        panel2.add(panel3);       
        panel2.add(label);
        panel2.add(patternField);  
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(panel2, BorderLayout.SOUTH);
        
        button.addActionListener(new ActionListener() 
        {			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				int textLength = textArea.getDocument().getLength();
				String text;
				try 
				{
					text = textArea.getDocument().getText(0, textLength);
				} 
				catch (BadLocationException e1) 
				{
					return;
				}//textArea.getText();
				String patternString = patternField.getText();
				
				log.info("pattern " + patternString);
            	
				Pattern pattern;
				try 
				{
					pattern = Pattern.compile(patternString);
				} 
				catch (PatternSyntaxException e) 
				{
					JOptionPane.showMessageDialog(null, e.getMessage(), "Regular expression is incorrect", JOptionPane.ERROR_MESSAGE);
					return;
				}
				Matcher matcher = pattern.matcher(text);
				String group = null;
				int start, end;
				
				Highlighter hilite = textArea.getHighlighter();
			    Highlighter.Highlight[] hilites = hilite.getHighlights();

			    for (int i = 0; i < hilites.length; i++) 
			    {
			        hilite.removeHighlight(hilites[i]);
			    }
  
				while (matcher.find()) 
	            {
	            	group = matcher.group();
	            	start = matcher.start();
	            	end = matcher.end();
	            	log.info("group=" + group + "; start=" + start + "; end=" + end);
				    
				    log.info("indexof " + group + " is " + text.indexOf(group));
	            	String rep_group = group.replaceAll("[^\\d]", "");
	            	if(rep_group.length() == 11)
	            		rep_group = rep_group.substring(1);
	            	else if(rep_group.length() != 10)
	            		continue;
	            	
	            	log.info("rep_group " + rep_group);
	            	
	            	boolean isFinded = false;
	            	try 
	            	{						
						isFinded = sqlHelper.isTelephoneExist(rep_group);
					} 
	            	catch (SQLException e) 
					{
	            		log.error(e);
						return;
					}
	            	
	            	if(isFinded)
	            	{
	            		try 
	            		{
							textArea.getHighlighter().addHighlight(start, end, 
									highlightPainterGreen);
						} 
	            		catch (BadLocationException e) 
	            		{
	            			log.warn(e);
						}
	            		            		
	            		log.info(group + " was finded");
	            	}
	            	else
	            	{
	            		try 
	            		{
							textArea.getHighlighter().addHighlight(start, end, 
									highlightPainterRed);
						} 
	            		catch (BadLocationException e) 
	            		{
	            			log.warn(e);
						}
	            		log.info(group + " wasn't finded");
	            	}
	            }
			}
		});

        return panel;
	}
}