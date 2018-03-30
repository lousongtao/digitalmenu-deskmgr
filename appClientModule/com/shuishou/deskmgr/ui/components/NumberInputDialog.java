package com.shuishou.deskmgr.ui.components;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.shuishou.deskmgr.Messages;

public class NumberInputDialog extends JDialog{
	public int inputInteger;
	public double inputDouble; 
	public boolean isConfirm = false;
	private NumberTextField txt;
	private final boolean allowDouble;
	public NumberInputDialog(Dialog parent, String title, String message, final boolean allowDouble){
		super(parent, title,true);
		this.allowDouble = allowDouble;
		JButton btnConfirm = new JButton(Messages.getString("ConfirmDialog"));
		JButton btnClose = new JButton(Messages.getString("CloseDialog"));
		btnConfirm.setPreferredSize(new Dimension(150, 50));
		btnClose.setPreferredSize(new Dimension(150, 50));
		txt = new NumberTextField(this, allowDouble, NumberKeyboard.SHOWPOSITION_RIGHT);
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(new JLabel(message), new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		c.add(txt, 				   new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		c.add(btnConfirm, 			new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		c.add(btnClose, 			new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		btnConfirm.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (txt.getText() == null || txt.getText().length() == 0)
					return;
				isConfirm = true;
				inputDouble = Double.parseDouble(txt.getText());
				inputInteger = (int)inputDouble;
				setVisible(false);
			}
		});
		this.setSize(new Dimension(400, 180));
		this.setLocation((int)(parent.getWidth() / 2 - this.getWidth() /2 + parent.getLocation().getX()), 
				(int)(parent.getHeight() / 2 - this.getHeight() / 2 + parent.getLocation().getY()));
	}
}
