package com.shuishou.deskmgr.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.beans.Indent;

public class DeskCell extends JPanel {
	
//	private Color colorUnselect = new Color(132,227,247);
	private Color colorUnselect = new Color(201,255,255);
	private Color colorSelect = new Color(209,210,255);
	
	private JLabel lbDeskNo = new JLabel();
	
	private JLabel lbCustomerAmount = new JLabel();
	
	private JLabel lbPrice = new JLabel();
	
	private JLabel lbMergeTo = new JLabel();
	
	private JLabel lbStartTime = new JLabel();
	
	private boolean isSelected = false;
	
	private Desk desk;
	
	private Indent indent;
	
	private int lastIndentId;//record the last indent on this table, to provide print ticket for customer

	private MainFrame mainFrame;
	public DeskCell(MainFrame mainFrame, Desk desk){
		this.desk = desk;
		this.mainFrame = mainFrame;
		initUI();
	}
	
	private void initUI(){
		this.setLayout(new GridLayout(0, 1));
		lbDeskNo.setFont(ConstantValue.FONT_15BOLD);
		lbDeskNo.setText(desk.getName());
		this.setBorder(BorderFactory.createLineBorder(Color.gray));
		setBackground(colorUnselect);
		this.add(lbDeskNo);
		
		this.add(lbCustomerAmount);
		this.add(lbPrice);
		this.add(lbStartTime);
		this.add(lbMergeTo);
		
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				//unselect all first, then select this
				for(DeskCell dc : mainFrame.getDeskcellList()){
					dc.setSelected(false);
//					dc.setBackground(Color.white);
					dc.setBackground(colorUnselect);
				}
				isSelected = true;
				DeskCell.this.setBackground(colorSelect);
			}
		});
		this.setPreferredSize(new Dimension(ConstantValue.TABLECELL_WIDTH, ConstantValue.TABLECELL_HEIGHT));
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
			lbPrice.setText(Messages.getString("DeskCell.Price") + indent.getFormatTotalPrice()); //$NON-NLS-1$
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
		else{
			desk.setMergeTo(mergeTo);
			lbMergeTo.setText(Messages.getString("DeskCell.MergeTo")+ mergeTo); //$NON-NLS-1$
		}
		
	}
}
