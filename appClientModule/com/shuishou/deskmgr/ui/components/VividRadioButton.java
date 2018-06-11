package com.shuishou.deskmgr.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.shuishou.deskmgr.ConstantValue;

/**
 * 提供一个选择可以高亮显示的RadioButton控件
 * 同时增大文本区域, 使得触摸屏点击更容易
 * @author Administrator
 *
 */
public class VividRadioButton extends JPanel{
	private static final Color COLOR_CHOOSE = Color.green;
	private static final Color COLOR_UNCHOOSE = new Color(240, 240, 240);
	
	private JRadioButton rb = new JRadioButton();
	private JLabel label = new JLabel();
	
	public VividRadioButton(String text){
		label.setText(text);
		initUI();
	}
	
	public VividRadioButton(String text, boolean isSelected){
		label.setText(text);
		initUI();
		rb.setSelected(isSelected);
	}
	
	private void initUI(){
//		this.setPreferredSize(new Dimension(70, 30));
//		this.setMinimumSize(new Dimension(70, 30));
		label.setOpaque(true);
		rb.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				if (rb.isSelected())
					label.setBackground(COLOR_CHOOSE);
				else 
					label.setBackground(COLOR_UNCHOOSE);
			}});
		label.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				if (!rb.isSelected())
					rb.setSelected(true);
			}
		});
		label.setBorder(BorderFactory.createLineBorder(Color.gray));
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
}
