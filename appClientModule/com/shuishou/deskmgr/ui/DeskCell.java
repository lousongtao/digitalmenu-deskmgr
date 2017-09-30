package com.shuishou.deskmgr.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.beans.Indent;

public class DeskCell extends JPanel {
	
	
	
	private JLabel lbDeskNo = new JLabel();
	
	private JLabel lbCustomerAmount = new JLabel();
	
	private JLabel lbPrice = new JLabel();
	
	private JLabel lbMergeTo = new JLabel();
	
	private JLabel lbStartTime = new JLabel();
	
	private boolean isSelected = false;
	
	private Desk desk;
	
	private Indent indent;
	
	private int lastIndentId;//record the last indent on this table, to provide print ticket for customer

	public DeskCell(Desk desk){
		this.desk = desk;
		initUI();
	}
	
	private void initUI(){
		this.setLayout(new GridLayout(0, 1));
		lbDeskNo.setFont(ConstantValue.FONT_30BOLD);
		lbDeskNo.setText(desk.getName());
//		lbCustomerAmount.setFont(indentInfoFont);
//		lbPrice.setFont(indentInfoFont);
//		lbStartTime.setFont(indentInfoFont);
		this.setBorder(BorderFactory.createLineBorder(Color.gray));
//		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.add(lbDeskNo);
		
		this.add(lbCustomerAmount);
		this.add(lbPrice);
		this.add(lbStartTime);
		this.add(lbMergeTo);
		
		this.setBackground(Color.white);
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (isSelected){
					isSelected = false;
					DeskCell.this.setBackground(Color.white);
				} else {
					isSelected = true;
					DeskCell.this.setBackground(Color.LIGHT_GRAY);
				}
			}
		});
//		this.setSize(new Dimension(50, 50));
//		
//		this.setMaximumSize(new Dimension(50, 50));
//		this.setMinimumSize(new Dimension(50, 50));
		this.setPreferredSize(new Dimension(150, 150));
	}
	
	public void setIndentInfo(Indent indent){
		this.indent = indent;
		refreshUI();
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Desk getDesk() {
		return desk;
	}

	public void setDesk(Desk desk) {
		this.desk = desk;
	}

	public Indent getIndent() {
		return indent;
	}

	public void setIndent(Indent indent) {
		this.indent = indent;
		refreshUI();
	}
	
	public void refreshUI(){
		if (indent == null ){
			lbCustomerAmount.setText("");
			lbPrice.setText("");
			lbStartTime.setText("");
		} else if (indent.getId() > 0){
			lbCustomerAmount.setText(Messages.getString("DeskCell.CustomerAmount") + indent.getCustomerAmount()); //$NON-NLS-1$
			lbPrice.setText(Messages.getString("DeskCell.Price") + indent.getTotalPrice()); //$NON-NLS-1$
			lbStartTime.setText(ConstantValue.DFHMS.format(indent.getStartTime()));
		} 
	}

	public int getLastIndentId() {
		return lastIndentId;
	}

	public void setLastIndentId(int lastIndentId) {
		this.lastIndentId = lastIndentId;
	}
	
	public void setMergeTo(String mergeTo){
		if (mergeTo == null)
			lbMergeTo.setText("");
		else
			lbMergeTo.setText(Messages.getString("DeskCell.MergeTo")+ mergeTo); //$NON-NLS-1$
	}
}
