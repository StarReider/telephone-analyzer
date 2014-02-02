package org.robe.ta.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robe.ta.conf.ConfigurationReader;
import org.robe.ta.data.DataProvider;
import org.robe.ta.ui.listener.CloseWindowListener;
import org.robe.ta.ui.listener.LinkListener;
import org.robe.ta.ui.listener.SaveTelephoneListener;
import org.robe.ta.ui.listener.SearchListener;

import ru.atomation.jbrowser.impl.JBrowserBuilder;
import ru.atomation.jbrowser.impl.JBrowserComponent;
import ru.atomation.jbrowser.impl.JComponentFactory;
import ru.atomation.jbrowser.interfaces.BrowserAdapter;

public class MainFrame 
{
    private final Log log; 
    private final DataProvider dataFacade;
    private int linkID = 0;
    private Map<String, Integer> attrsArr = new HashMap<String, Integer>();
    private JBrowserComponent<?> browser;
    private BrowserAdapter ba;
    
    public void addHyperlink(Document document, int start, int end, String group, boolean finded) throws BadLocationException 
    {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        attrsArr.put(group, start);
        StyleConstants.setUnderline(attrs, true);
        StyleConstants.setForeground(attrs, finded ? Color.GREEN : Color.RED);
        attrs.addAttribute(HTML.Attribute.ID, new Integer(++linkID));
        attrs.addAttribute(HTML.Attribute.HREF, "hhhhh");
        if(!finded)
                attrs.addAttribute("telephone", group);
        
        ((StyledDocument) document).setCharacterAttributes(start, group.length(), attrs, false);
    }
    
    public MainFrame(final DataProvider dataFacade, ConfigurationReader configurationReader) throws Exception
    {
        this.log = LogFactory.getLog(MainFrame.class); 
        this.dataFacade = dataFacade;
        
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                JFrame frame = new JFrame();
                frame.setTitle("TelephoneAnalyzer" + configurationReader.getVersion());
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
                frame.addWindowListener(new CloseWindowListener(dataFacade));
                
                frame.setSize((int) (screenSize.getWidth() * 0.75f),
                                (int) (screenSize.getHeight() * 0.75f));
                frame.setLocationRelativeTo(null);

                JTabbedPane tabContainer = new JTabbedPane();
                tabContainer.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

                frame.add(tabContainer, BorderLayout.CENTER);
                frame.setVisible(true);

                JPanel panel = new JPanel(new BorderLayout());
                JComponentFactory<Canvas> canvasFactory = new TabbedComponentFactory(tabContainer, dataFacade, panel);
                new JBrowserBuilder().setBrowserFactory(canvasFactory).buildBrowserManager();
                browser = canvasFactory.createBrowser();

                ba = new SearchListener(dataFacade, browser);
                //browser.setUrl("http://viaduk-podolsk.ru/auto_sell");
                browser.addBrowserListener(ba);
                browser.setUrl("about:blank");
                tabContainer.addTab("Browser", makeFirstPanel());
                
                tabContainer.addTab("Text", makeSecondTab(frame));
                
                if(configurationReader.isThird_tab_on())
                        tabContainer.addTab("Administrate", makeThirdTab());
    }

        private JPanel makeFirstPanel() throws Exception 
        {
                JPanel panel = new JPanel(new BorderLayout());
                
                JPanel browserPanel = new JPanel();
                final JTextField addressField = new JTextField();
                JButton goButton = new JButton("Go");
                
                goButton.addActionListener(new ActionListener() 
                {       
                        @Override
                        public void actionPerformed(ActionEvent e) 
                        {               
                                browser.removeBrowserListener(ba);
                                browser.setUrl(addressField.getText());
                        }
                });
                
                browserPanel.setLayout(new BoxLayout(browserPanel, BoxLayout.LINE_AXIS));
                browserPanel.add(addressField);
                browserPanel.add(goButton);
                
                JPanel toolsPanel = new JPanel();
                JPanel fieldsPanel = new JPanel();
                
                final JTextField telField = new JTextField();
                telField.setEditable(false);
                final JComboBox<String> orgField = new JComboBox<String>(dataFacade.getAllOrganizations());//new JList<String>(dataFacade.getAllOrganizations());
                orgField.setSelectedItem("");
                fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.PAGE_AXIS));
                JPanel tt = new JPanel();tt.setLayout(new BoxLayout(tt, BoxLayout.LINE_AXIS));
                tt.add(new JLabel("Telephone:")); tt.add(telField).setPreferredSize(new Dimension(10, 5));
                fieldsPanel.add(tt);
                tt = new JPanel();tt.setLayout(new BoxLayout(tt, BoxLayout.LINE_AXIS));
                tt.add(new JLabel("Organization:")); tt.add(orgField);
                fieldsPanel.add(tt);
                
                JButton saveButton = new JButton("Save");
                JButton searchButton = new JButton("Search");
                
                searchButton.addActionListener(new ActionListener() 
                {       
                        @Override
                        public void actionPerformed(ActionEvent arg0) 
                        {
                                browser.addBrowserListener(ba);
                                browser.refresh();
                        }
                });
                
                saveButton.addActionListener(new SaveTelephoneListener(orgField, telField, dataFacade));
                
                toolsPanel.setLayout(new BorderLayout());
                toolsPanel.add(fieldsPanel, BorderLayout.CENTER);
                JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
                buttonPanel.add(saveButton);
                buttonPanel.add(searchButton);
                toolsPanel.add(buttonPanel, BorderLayout.EAST);
                
                JPanel generalPanel = new JPanel();
                generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.PAGE_AXIS));
                
                generalPanel.add(browserPanel);
                generalPanel.add(toolsPanel);
                
                panel.add(browser.getComponent(), BorderLayout.CENTER);
                panel.add(generalPanel, BorderLayout.NORTH);
                
                browser.addBrowserListener(new LinkListener(telField, orgField));
                
                return panel;
        }

        private JComponent makeThirdTab() 
        {
                return new ThirdTabPanel(dataFacade);
        }

        private JComponent makeSecondTab(JFrame frame) throws Exception 
        {
        JPanel panel = new JPanel(false);
        final JTextPane textArea = new JTextPane();
        textArea.setContentType("text/html");
        textArea.addMouseMotionListener(new LinkController(frame, attrsArr, textArea, dataFacade));
        //textArea.setEditable(false);
       
        final JScrollPane scrollPane = new JScrollPane(textArea);
        
        final DefaultHighlighter.DefaultHighlightPainter highlightPainterGreen = 
                new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
        final DefaultHighlighter.DefaultHighlightPainter highlightPainterRed = 
                new DefaultHighlighter.DefaultHighlightPainter(Color.RED);        
                
        JButton searchButton = new JButton("Search");
        searchButton.setActionCommand("search");
        
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
        final JTextField patternField = new JTextField("((8|\\+[0-9]{1,4})?[\\-\\(]?[0-9]{3,6}[\\-\\)]\\s?[0-9\\-]{5,})");
        JLabel label = new JLabel("Pattern ");
        
        panel3.add(searchButton);
        panel3.add(clear_button);               
        
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));
        panel2.add(panel3);       
        panel2.add(label);
        panel2.add(patternField);  
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(panel2, BorderLayout.SOUTH);
        
        searchButton.addActionListener(new ActionListener() 
        {                       
                        @Override
                        public void actionPerformed(ActionEvent arg0) 
                        {
                                String text;
                                int textLength = textArea.getDocument().getLength();
                                try 
                                {
                                        text = textArea.getDocument().getText(0, textLength);
                                } 
                                catch (BadLocationException e1) 
                                {
                                        return;
                                }
                                //text = textArea.getText();
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
                                                isFinded = dataFacade.isTelephoneExist(rep_group);
                                        } 
                        catch (Exception e) 
                                        {
                                log.error(e);
                                                return;
                                        }
                        
                        if(isFinded)
                        {
                                try 
                                {
                                                        //textArea.getHighlighter().addHighlight(start, end, highlightPainterGreen);
                                        //text = text.replace(group, "<span style='color:green'>" + group + "</span>");
                                        addHyperlink(textArea.getDocument(), start, end, group, isFinded);
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
                                                        //textArea.getHighlighter().addHighlight(start, end, highlightPainterRed);
                                        //textArea.getDocument().getText(text.indexOf(group), group.length());
                                        addHyperlink(textArea.getDocument(), start, end, group, isFinded);
                                        //text = text.replace(group, "<span style='color:red'>" + group + "</span>");
                                //textArea.insertComponent(new JButton("x"));
                                        
                                                } 
                                catch (Exception e) 
                                {
                                        log.warn(e);
                                                }
                                log.info(group + " wasn't finded");
                        }
                    }
                //textArea.setText(text);
                        }
                });

        return panel;
        }
}