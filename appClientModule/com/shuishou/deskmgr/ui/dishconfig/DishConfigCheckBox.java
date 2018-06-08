package com.shuishou.deskmgr.ui.dishconfig;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.beans.DishConfig;

public class DishConfigCheckBox extends JPanel implements DishConfigIFC{
	private DishConfigDialog parent;
	private DishConfig config;
	private JCheckBox cb = new JCheckBox();
	private JLabel label = new JLabel();
	
	private static final Color COLOR_CHOOSE = Color.green;
	private static final Color COLOR_UNCHOOSE = new Color(240, 240, 240);
	
	public DishConfigCheckBox(DishConfigDialog parent, DishConfig config){
		this.config = config;
		this.parent = parent;
		initUI();
	}
	
	private void initUI(){
		this.setPreferredSize(new Dimension(200, 50));
		this.setMaximumSize(new Dimension(200, 50));
		label.setOpaque(true);
		cb.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				if (cb.isSelected())
					label.setBackground(COLOR_CHOOSE);
				else 
					label.setBackground(COLOR_UNCHOOSE);
				parent.onChooseChange();
			}});
		label.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				cb.setSelected(!cb.isSelected());
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
		add(cb, BorderLayout.WEST);
		add(label, BorderLayout.CENTER);
	}
	
	public boolean isSelected(){
		return cb.isSelected();
	}
	public void setSelected(boolean b){
		cb.setSelected(b);
	}
	
	public void setText(String t){
		label.setText(t);
	}
	
	public JLabel getLabel(){
		return label;
	}
	
	public JCheckBox getCheckBox(){
		return cb;
	}
	
	@Override
	public DishConfig getDishConfig() {
		return config;
	}
}

