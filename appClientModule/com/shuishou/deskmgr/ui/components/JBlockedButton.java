package com.shuishou.deskmgr.ui.components;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * to prevent double click
 * @author Administrator
 *
 */
public class JBlockedButton extends IconButton{

	private int blocktime = 2000;

    public JBlockedButton(String text, String iconfile) {
        super(text, iconfile);
        setMultiClickThreshhold(blocktime);
    }
}
