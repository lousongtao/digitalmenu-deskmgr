package com.shuishou.deskmgr.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.json.JSONObject;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.UserData;
import com.shuishou.deskmgr.http.HttpUtil;

public class LoginDialog extends JDialog {

	private JTextField tfName = new JTextField();
	private JTextField tfPassword = new JTextField();
	private JButton btnLogin = new JButton(Messages.getString("LoginDialog.LoginButton")); //$NON-NLS-1$
	private String loginURL = "login";
	private MainFrame mainFrame;
	public LoginDialog(MainFrame mainFrame){
		super(mainFrame, Messages.getString("LoginDialog.DialogTitle"), true); //$NON-NLS-1$
		this.mainFrame = mainFrame;
		initUI();
	}
	
	private void initUI(){
		btnLogin.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("username", tfName.getText());
				params.put("password", tfPassword.getText());
				String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + "login", params, "UTF-8");
				JSONObject logResult = new JSONObject(response);
				if ("ok".equals(logResult.getString("result"))){
					LoginDialog.this.setVisible(false);
					mainFrame.setLoginUser(new UserData(logResult.getInt("userId"), logResult.getString("userName")));
					mainFrame.loadCurrentIndentInfo();
					if (mainFrame.getOnDutyUser() == null){
						String msg = Messages.getString("LoginDialog.NoDutyMsg") + tfName.getText(); //$NON-NLS-1$
						if (JOptionPane.showConfirmDialog(mainFrame, msg, Messages.getString("LoginDialog.OnDutyTitle"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){ //$NON-NLS-1$
							mainFrame.doOnDuty(logResult.getInt("userId"));
						}
					}
				}
			}});
		Container c = this.getContentPane();
		
		c.setLayout(new GridLayout(0, 2, 10, 10));
		c.add(new JLabel(Messages.getString("LoginDialog.UserName"))); //$NON-NLS-1$
		c.add(tfName);
		c.add(new JLabel(Messages.getString("LoginDialog.Password"))); //$NON-NLS-1$
		c.add(tfPassword);
		c.add(btnLogin);
		btnLogin.setPreferredSize(new Dimension(150, 40));
		this.setSize(new Dimension(300, 200));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
//		this.setUndecorated(true);
		tfName.setText("admin");
		tfPassword.setText("admin");
	}
}
