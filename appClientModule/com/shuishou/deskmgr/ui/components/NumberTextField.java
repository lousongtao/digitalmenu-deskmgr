package com.shuishou.deskmgr.ui.components;

import java.awt.Dialog;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

public class NumberTextField extends JFormattedTextField{

//	private NumberKeyboard keyboard;
	public NumberTextField(Dialog parent, final boolean allowDouble){
		this(parent, allowDouble, NumberKeyboard.SHOWPOSITION_BOTTOM);
	}
			
	public NumberTextField(Dialog parent, final boolean allowDouble, int numPadPosition){
//		keyboard = new NumberKeyboard(parent, this, numPadPosition);
//		if (!allowDouble)
//			keyboard.hideDot();
		addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (allowDouble){
					if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) 
							|| (c == KeyEvent.VK_DELETE) || (c == '.'))) {
						getToolkit().beep();
						e.consume();
					}
					if (c == '.') {
						if (getText() != null && getText().indexOf(".") >= 0) {
							getToolkit().beep();
							e.consume();
						}
					}
				} else {
					if (!((c >= '0') && (c <= '9'))) {
						getToolkit().beep();
						e.consume();
					}
				}
			}
		});
//		addFocusListener(new FocusAdapter(){
//
//			@Override
//			public void focusGained(FocusEvent e) {
//				keyboard.setVisible(true);
//			}});
	}
}
