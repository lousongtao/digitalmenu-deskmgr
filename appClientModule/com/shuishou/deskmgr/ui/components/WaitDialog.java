package com.shuishou.deskmgr.ui.components;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * 客户端界面的耗时操作, 如访问服务器, 需要使用该类停止用户的连续点击操作.
 * 同时也保证用户无法对同一按钮进行多次点击事件.
 * 该类提供一个work接口, 子类通过实现该方法, 执行耗时操作. 该方法结束后, 无论正确与否以及是否有异常,都要保证这个dialog能够正常关闭.
 * @author Administrator
 *
 */
public abstract class WaitDialog extends JDialog{

	private JLabel lbWait = new JLabel();
	private Object returnResult;
	public WaitDialog(Window parent, String text){
		super(parent);
		init();
		lbWait.setText(text);
		this.setSize(new Dimension(200, 100));
		this.setLocation((int)(parent.getWidth() / 2 - this.getWidth() /2 + parent.getLocation().getX()), 
				(int)(parent.getHeight() / 2 - this.getHeight() / 2 + parent.getLocation().getY()));
		setVisible(true);
	}
	
	public WaitDialog(){
		super();
		init();
		setVisible(true);
	}
	
	private void init(){
		setUndecorated(true);
		setModal(true);
		Container c = this.getContentPane();
		c.add(lbWait);
		runWork();
	}
	
	private void runWork(){
		new Thread(){
			public void run(){
				try{
					returnResult = work();
				} finally{
					WaitDialog.this.setVisible(false);
				}
			}
		}.start();
	}
	
	public abstract Object work();
	
	public Object getReturnResult(){
		return returnResult;
	}
	
	public static void main(String[] args) {
		JButton btn = new JButton("btn");
		final JFrame f = new JFrame(); 
		f.setSize(500, 500);
		f.add(btn);
		btn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				WaitDialog dlg = new WaitDialog(f, "Posting data..."){
					public Object work(){
						try {
							Thread.sleep(4000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}
				};
			}
			
		});
		
		f.setVisible(true);
	}
}
