package org.robe.ta.ui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TestAction extends JFrame {
  private JButton actionButton;
  private JPopupMenu mouseMenu;
  private JPanel imagePanel;
  private GridLayout grid;
  private JLabel textLabel;
  
  public TestAction() {
    actionButton = new JButton();
    mouseMenu = new JPopupMenu();
	
	JButton b1 = new JButton("1");
	JButton b2 = new JButton("2");
	JButton b3 = new JButton("3");
	JButton b4 = new JButton("4");
	JButton b5 = new JButton("5");
	JButton b6 = new JButton("6");
	JButton b7 = new JButton("7");
	JButton b8 = new JButton("8");
	JButton b9 = new JButton("9");
	
	textLabel = new JLabel("Text");
	textLabel.setFont(new Font("Arial", Font.PLAIN+Font.BOLD, 100));
	textLabel.setHorizontalAlignment(JLabel.CENTER);
	add(textLabel);
	
	imagePanel = new JPanel();
	grid = new GridLayout(3,3);
	imagePanel.setLayout(grid);
	
	imagePanel.add(b1);
	imagePanel.add(b2);
	imagePanel.add(b3);
	imagePanel.add(b4);
	imagePanel.add(b5);
	imagePanel.add(b6);
	imagePanel.add(b7);
	imagePanel.add(b8);
	imagePanel.add(b9);
	mouseMenu.add(imagePanel);
	
	for (final Component c : imagePanel.getComponents()) {  
      if (c instanceof JButton) { 	
	    ((JButton) c).setForeground(Color.black); 		
        ((JButton) c).setBackground(Color.white); 	  
        ((JButton) c).setFocusable(false);   
		((JButton) c).addMouseListener(new MouseAdapter() {
		  public void mouseEntered(MouseEvent e) {
		    ((JButton) c).setForeground(Color.red); 
			((JButton) c).setBackground(Color.orange);
		    Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	        ((JButton) c).setCursor(cursor);
		  }
		  public void mouseExited(MouseEvent e) {
		     ((JButton) c).setForeground(Color.black); 
			 ((JButton) c).setBackground(Color.white);
		  }
		});
		((JButton) c).addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent e) {
		    textLabel.setText(((JButton) c).getText());
		    ((JButton) c).setForeground(Color.black); 
			((JButton) c).setBackground(Color.white); 
		    mouseMenu.setVisible(false);
		  }
		});
	  }
	}
	  
	addMouseListener(new MouseAdapter() {
	   public void mouseClicked(final MouseEvent e) {
	     if((e.getModifiers() & e.BUTTON3_MASK) != 0) {
	       int x = e.getX();
	       int y = e.getY();
		   mouseMenu.show(TestAction.this, x, y);	
		 } 
		 if((e.getModifiers() & e.BUTTON1_MASK) != 0) {
		   mouseMenu.setVisible(false);
		 }
	   }
	});
  }
  public static void main(String[] args) {
    TestAction frame = new TestAction();
    frame.setSize(300, 300);	
	frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
	frame.setVisible(true);
  }
}
