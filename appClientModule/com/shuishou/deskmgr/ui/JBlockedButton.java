package com.shuishou.deskmgr.ui;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * to prevent double click
 * @author Administrator
 *
 */
public class JBlockedButton extends JButton{

	private int blocktime = 2000;
	public JBlockedButton() {
        super();
        setMultiClickThreshhold(blocktime);
    }

    public JBlockedButton(Icon icon) {
        super(icon);
        setMultiClickThreshhold(blocktime);
    }

    public JBlockedButton(String text) {
        super(text);
        setMultiClickThreshhold(blocktime);
    }

    public JBlockedButton(Action a) {
        super(a);
        setMultiClickThreshhold(blocktime);
    }

    public JBlockedButton(String text, Icon icon) {
        super(text, icon);
        setMultiClickThreshhold(blocktime);
    }
}
