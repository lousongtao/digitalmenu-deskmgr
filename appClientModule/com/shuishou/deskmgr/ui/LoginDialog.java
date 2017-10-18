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
				doLogin();
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
	
	
	private void doLogin(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", tfName.getText());
		params.put("password", tfPassword.getText());
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + "login", params, "UTF-8");
		if (response == null){
			JOptionPane.showMessageDialog(mainFrame, "cannot connect with server");
			return;
		}
		JSONObject logResult = new JSONObject(response);
		if ("ok".equals(logResult.getString("result"))){
			/**
			 * while UserA login:
			 * if find currently nobody onduty, then do onduty automatically;
			 * if find there is UserB onduty, then ask if replace UserB:
			 * if choose replace, then UserA onduty & UserB offduty;
			 * if choose cancel, then return to the login dialog
			 */
			UserData loginUser = new UserData();
			loginUser.setId(logResult.getInt("userId"));
			loginUser.setName(logResult.getString("userName"));
			if (mainFrame.getOnDutyUser() == null){
				LoginDialog.this.setVisible(false);
				mainFrame.doOnDuty(loginUser.getId(), false);
			}else if (!mainFrame.getOnDutyUser().getName().equals(loginUser.getName())){
				String msg = Messages.getString("LoginDialog.swiftDuty") + mainFrame.getOnDutyUser().getName(); //$NON-NLS-1$
				Object[] options = { Messages.getString("MainFrame.ShiftWork"),
						Messages.getString("MainFrame.ShiftWorkPrint"), Messages.getString("MainFrame.Cancel") };
				int choose = JOptionPane.showOptionDialog(this, msg, Messages.getString("MainFrame.ShiftWorkTitle"),
						JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (choose == 2){
					return;
				} else if (choose == 1){
					LoginDialog.this.setVisible(false);
					mainFrame.doOnDuty(loginUser.getId(), true);
				} else if (choose == 0){
					LoginDialog.this.setVisible(false);
					mainFrame.doOnDuty(loginUser.getId(), false);
				}
			} else {
				LoginDialog.this.setVisible(false);
			}
		} else if ("invalid_password".equals(logResult.getString("result"))){
			JOptionPane.showMessageDialog(LoginDialog.this, "Password Error");
		}
	}
}
