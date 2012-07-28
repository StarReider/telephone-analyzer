package org.robe.ta.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

	@SuppressWarnings("serial") 
	public class CloasableTab extends JPanel 
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