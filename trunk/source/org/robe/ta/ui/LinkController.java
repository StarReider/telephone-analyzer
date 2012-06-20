package org.robe.ta.ui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;

public class LinkController extends MouseAdapter 
                            implements MouseMotionListener 
                            {
	private JFrame frame;
  public LinkController(JFrame frame2) {
	  frame = frame2;
		// TODO Auto-generated constructor stub
	}
public void mouseMoved(MouseEvent ev) 
  {
    JTextPane editor = (JTextPane) ev.getSource();    
    if (editor.isEditable()) 
    {
      Point pt = new Point(ev.getX(), ev.getY());
      int pos = editor.viewToModel(pt);
      if (pos >= 0) {                    
        Document doc = editor.getDocument();
  	  JPopupMenu p = new JPopupMenu();
  	  JLabel ll = new JLabel("?");
	  p.add(ll);
          StyledDocument hdoc = 
            (StyledDocument) doc;
          Element e = hdoc.getCharacterElement(pos);
          if(e.getAttributes().getAttribute("telephone") != null)
          {
        	  ll.setText((String) e.getAttributes().getAttribute("telephone"));
        	  p.show(frame, ev.getX(), ev.getY());
          }
          else
          {
        	  p.setVisible(false);
          }
        
      } 
    }
  }
  /**
   * Called for a mouse click event.
   * If the component is read-only (ie a browser) then 
   * the clicked event is used to drive an attempt to
   * follow the reference specified by a link.
   *
   * @param e the mouse event
   * @see MouseListener#mouseClicked
   */
  public void mouseClicked(MouseEvent e) {
    JTextPane editor = (JTextPane) e.getSource();
            
    if (! editor.isEditable()) {
      Point pt = new Point(e.getX(), e.getY());
      int pos = editor.viewToModel(pt);
      if (pos >= 0) {
        // get the element at the pos
        // check if the elemnt has the HREF
        // attribute defined
        // if so notify the HyperLinkListeners
      }
    }
  }
}