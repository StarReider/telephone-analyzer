package org.robe.ta.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robe.ta.data.DataProvider;
import org.robe.ta.data.jpa.Telephone;

public class LinkController extends MouseAdapter implements MouseMotionListener 
{
	private JFrame frame;
	private final Log log;
	private JPopupMenu p;
	private JTextField telephoneField;
	private JTextField organizationField;
	private JTextField descrField;
	private Map<String, Integer> attrs;
	
	public LinkController(final JFrame frame2, Map<String, Integer> attrsArr, final JTextPane textArea, final DataProvider dataFacade) 
	{
		frame = frame2;
		log = LogFactory.getLog(LinkController.class); 
		attrs = attrsArr;
		p = new JPopupMenu();
		
		telephoneField = new JTextField(""); telephoneField.setEditable(false);
		JLabel telephoneLabel = new JLabel("Telephone");
		organizationField = new JTextField();
		JLabel organizationLabel = new JLabel("Organization");
		descrField = new JTextField();
		JLabel descrLabel = new JLabel("Description");
		JButton addButton = new JButton("Add");
		
		addButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				SimpleAttributeSet attrs2 = new SimpleAttributeSet();
			    StyleConstants.setForeground(attrs2, Color.GREEN);
			    
			    Set<Entry<String, Integer>> en = attrs.entrySet();
			    
			    for(Entry<String, Integer> entry : en)
			    {
			    	if(entry.getKey().equals(telephoneField.getText()))
			    	{
			    		Telephone telephone = new Telephone();
			    		String group = telephoneField.getText();
			    		String rep_group = group.replaceAll("[^\\d]", "");
		            	if(rep_group.length() == 11)
		            		rep_group = rep_group.substring(1);

			    		telephone.setTelephone(new BigInteger(rep_group));
			    		telephone.setDescription(descrField.getText());
			    		telephone.setName(organizationField.getText());
			    		
			    		try 
			    		{
							dataFacade.saveTelephone(telephone);
				    		
				    		((StyledDocument) textArea.getDocument()).setCharacterAttributes(entry.getValue(), entry.getKey().length(), attrs2, true);
						} 
			    		catch (Exception e) 
			    		{
			    			JOptionPane.showMessageDialog(frame2, "Telephone can not be saved!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE, null);
						}
			    	}
			    }
			}
		});
		
		p.add(telephoneLabel);
		p.add(telephoneField);
		p.add(organizationLabel);
		p.add(organizationField);
		p.add(descrLabel);
		p.add(descrField);
		p.add(addButton);
	}
	
	public void mouseMoved(MouseEvent ev) 
    {
		JTextPane editor = (JTextPane) ev.getSource();    
		if (editor.isEditable()) 
		{
			Point pt = new Point(ev.getX(), ev.getY());
			int pos = editor.viewToModel(pt);
			if (pos >= 0) 
			{      
				log.info("pos=" + pos);
				Document doc = editor.getDocument();
				StyledDocument hdoc = (StyledDocument) doc;
				Element element = hdoc.getCharacterElement(pos);
          
				if(element.getAttributes().getAttribute("telephone") != null)
				{
					log.info("telephone attr=" + element.getAttributes().getAttribute("telephone"));
					telephoneField.setText((String) element.getAttributes().getAttribute("telephone"));
					p.show(frame, ev.getX(), ev.getY());
				}
				else
				{
					p.setVisible(false);
				}       
			}
			else
			{
				p.setVisible(false);
			}
		}
    }
}