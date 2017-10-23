package com.shuishou.deskmgr.ui.components;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

public class NumberKeyboard extends JDialog implements ActionListener, FocusListener{
	public static final int SHOWPOSITION_LEFT = 0;
	public static final int SHOWPOSITION_RIGHT = 1;
	public static final int SHOWPOSITION_TOP = 2;
	public static final int SHOWPOSITION_BOTTOM = 3;
	private int showPosition = SHOWPOSITION_LEFT;
	private int width = 300;
	private int height = 300;
	private JButton btn0 = new JButton("0");
	private JButton btn1 = new JButton("1");
	private JButton btn2 = new JButton("2");
	private JButton btn3 = new JButton("3");
	private JButton btn4 = new JButton("4");
	private JButton btn5 = new JButton("5");
	private JButton btn6 = new JButton("6");
	private JButton btn7 = new JButton("7");
	private JButton btn8 = new JButton("8");
	private JButton btn9 = new JButton("9");
	private JButton btnBack = new JButton("<");
	private JButton btnDot = new JButton(".");
	
	private Dialog parent;
	private JTextField comp;
	
	public NumberKeyboard(Dialog parent, JTextField comp){
		super(parent, false);
		setUndecorated(true);// remove title bar
		this.parent = parent;
		this.comp = comp;
		initUI();
		this.setSize(new Dimension(width, height));
	}
	
	public NumberKeyboard(Dialog parent, JTextField comp, int showPosition){
		super(parent, false);
		setUndecorated(true);// remove title bar
		this.parent = parent;
		this.comp = comp;
		initUI();
		this.setSize(new Dimension(width, height));
		this.showPosition = showPosition;
		
	}
	
	public void setVisible(boolean b) {
		if (b){
			if (showPosition == SHOWPOSITION_LEFT)
				this.setLocation((int) (comp.getLocationOnScreen().getX() - width),
						(int) (comp.getLocationOnScreen().getY() - height / 2));
			else if (showPosition == SHOWPOSITION_RIGHT)
				this.setLocation((int) (comp.getLocationOnScreen().getX() + comp.getWidth()),
						(int) (comp.getLocationOnScreen().getY() - height / 2));
			else if (showPosition == SHOWPOSITION_TOP)
				this.setLocation((int) comp.getLocationOnScreen().getX(),
						(int) (comp.getLocationOnScreen().getY() - height));
			else if (showPosition == SHOWPOSITION_BOTTOM)
				this.setLocation((int) comp.getLocationOnScreen().getX(),
						(int) (comp.getLocationOnScreen().getY() + comp.getHeight()));
		}
        super.setVisible(b);
    }
	
	private void initUI(){
		Container c = this.getContentPane();
		c.setLayout(new GridLayout(4,3,5,5));
		c.add(btn1);
		c.add(btn2);
		c.add(btn3);
		c.add(btn4);
		c.add(btn5);
		c.add(btn6);
		c.add(btn7);
		c.add(btn8);
		c.add(btn9);
		c.add(btnBack);
		c.add(btn0);
		c.add(btnDot);
		
		btn1.addActionListener(this);
		btn2.addActionListener(this);
		btn3.addActionListener(this);
		btn4.addActionListener(this);
		btn5.addActionListener(this);
		btn6.addActionListener(this);
		btn7.addActionListener(this);
		btn8.addActionListener(this);
		btn9.addActionListener(this);
		btn0.addActionListener(this);
		btnBack.addActionListener(this);
		btnDot.addActionListener(this);
		
		btn1.addFocusListener(this);
		btn2.addFocusListener(this);
		btn3.addFocusListener(this);
		btn4.addFocusListener(this);
		btn5.addFocusListener(this);
		btn6.addFocusListener(this);
		btn7.addFocusListener(this);
		btn8.addFocusListener(this);
		btn9.addFocusListener(this);
		btn0.addFocusListener(this);
		btnBack.addFocusListener(this);
		btnDot.addFocusListener(this);
		this.addFocusListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn1){
			comp.setText(comp.getText() + "1");
		} else if (e.getSource() == btn2){
			comp.setText(comp.getText() + "2");
		} else if (e.getSource() == btn3){
			comp.setText(comp.getText() + "3");
		} else if (e.getSource() == btn4){
			comp.setText(comp.getText() + "4");
		} else if (e.getSource() == btn5){
			comp.setText(comp.getText() + "5");
		} else if (e.getSource() == btn6){
			comp.setText(comp.getText() + "6");
		} else if (e.getSource() == btn7){
			comp.setText(comp.getText() + "7");
		} else if (e.getSource() == btn8){
			comp.setText(comp.getText() + "8");
		} else if (e.getSource() == btn9){
			comp.setText(comp.getText() + "9");
		} else if (e.getSource() == btn0){
			comp.setText(comp.getText() + "0");
		} else if (e.getSource() == btnBack){
			if (comp.getText() != null && comp.getText().length() > 0){
				String s = comp.getText();
				comp.setText(s.substring(0, s.length() - 2));
			}
		} else if (e.getSource() == btnDot){
			comp.setText(comp.getText() + ".");
		}
		System.out.println("keyboard isGainFocused " + isGainFocused());
	}
	
	public void hideDot(){
		btnDot.setVisible(false);
	}
	
	public boolean isGainFocused(){
		if (this.hasFocus())
			return true;
		if (btn1.hasFocus())
			return true;
		if (btn2.hasFocus())
			return true;
		if (btn3.hasFocus())
			return true;
		if (btn4.hasFocus())
			return true;
		if (btn5.hasFocus())
			return true;
		if (btn6.hasFocus())
			return true;
		if (btn7.hasFocus())
			return true;
		if (btn8.hasFocus())
			return true;
		if (btn9.hasFocus())
			return true;
		if (btn0.hasFocus())
			return true;
		if (btnBack.hasFocus())
			return true;
		if (btnDot.hasFocus())
			return true;
		return false;
	}

	@Override
	public void focusGained(FocusEvent e) {
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (!isGainFocused())
			this.setVisible(false);
	}
}
