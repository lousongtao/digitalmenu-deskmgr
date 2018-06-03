package com.shuishou.deskmgr.ui.dishconfig;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.beans.DishConfig;

public class DishConfigRadioButton extends JPanel implements DishConfigIFC{
	private DishConfigDialog parent;
	private DishConfig config;
	private JRadioButton rb = new JRadioButton();
	private JLabel label = new JLabel();
	
	private static final Color COLOR_CHOOSE = Color.green;
	private static final Color COLOR_UNCHOOSE = new Color(240, 240, 240);
	
	public DishConfigRadioButton(DishConfigDialog parent, DishConfig config){
		this.config = config;
		this.parent = parent;
		initUI();
	}
	
	private void initUI(){
		this.setPreferredSize(new Dimension(200, 50));
		this.setMaximumSize(new Dimension(200, 50));
		label.setOpaque(true);
		rb.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				if (rb.isSelected())
					label.setBackground(COLOR_CHOOSE);
				else 
					label.setBackground(COLOR_UNCHOOSE);
				parent.onChooseChange();
			}});
		label.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				if (!rb.isSelected())
					rb.setSelected(true);
			}
		});
		label.setBorder(BorderFactory.createLineBorder(Color.gray));
		String txt = config.getFirstLanguageName();
		if (config.getPrice() != 0){
			txt += "$" + config.getPrice();
		}
		label.setText(txt);
		label.setFont(ConstantValue.FONT_20BOLD);
		this.setLayout(new BorderLayout());
		add(rb, BorderLayout.WEST);
		add(label, BorderLayout.CENTER);
	}
	
	public boolean isSelected(){
		return rb.isSelected();
	}
	public void setSelected(boolean b){
		rb.setSelected(b);
	}
	
	public void setText(String t){
		label.setText(t);
	}
	
	public JLabel getLabel(){
		return label;
	}
	
	public JRadioButton getRadioButton(){
		return rb;
	}
	
	@Override
	public DishConfig getDishConfig() {
		return config;
	}
}
