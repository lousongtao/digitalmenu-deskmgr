package com.shuishou.deskmgr.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;


public class StartingWaitDialog extends JDialog {
	public StartingWaitDialog(){
		super((JDialog)null, false);
		initUI();
	}
	
	private void initUI(){
		Container c = this.getContentPane();
		JLabel lb = new JLabel();
		try {
			lb.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/resource/systemstart.png"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		c.setLayout(new BorderLayout());
		c.add(lb, BorderLayout.CENTER);
		setUndecorated(true);
		this.setSize(new Dimension(480, 240));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(screenSize.width / 2 - this.getWidth() /2), (int)(screenSize.height / 2 - this.getHeight() / 2));
	}
	
}
