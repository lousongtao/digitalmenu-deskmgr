package com.shuishou.deskmgr.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.beans.DiscountTemplate;
import com.shuishou.deskmgr.beans.HttpResult;
import com.shuishou.deskmgr.beans.Indent;
import com.shuishou.deskmgr.beans.IndentDetail;
import com.shuishou.deskmgr.beans.PayWay;
import com.shuishou.deskmgr.http.HttpUtil;
import com.shuishou.deskmgr.ui.OpenTableDialog.ChoosedDish;
import com.shuishou.deskmgr.ui.components.IconButton;
import com.shuishou.deskmgr.ui.components.JBlockedButton;
import com.shuishou.deskmgr.ui.components.NumberTextField;

public class CheckoutSplitIndentDialog extends CheckoutDialog{
	private final Logger logger = Logger.getLogger(CheckoutSplitIndentDialog.class.getName());
	private int originIndentId;
	private Indent indentAfterSplit;//分割订单后剩余未支付的部分
	public CheckoutSplitIndentDialog(MainFrame mainFrame,String title, boolean modal, Desk desk, Indent indent, int originIndentId){
		super(mainFrame, title, modal, desk, indent);
		getBtnSplitIndent().setVisible(false);
		getBtnCancelOrder().setVisible(false);
		this.originIndentId = originIndentId;
	}
	
	public void doPay(){
		if (rbPayMember.isSelected()){
			if (tfMember.getText() == null || tfMember.getText().length() == 0){
				JOptionPane.showMessageDialog(mainFrame, Messages.getString("CheckoutDialog.InputNumber")); //$NON-NLS-1$
				return;
			}
		}
		JSONArray ja = new JSONArray();
		for (int i = 0; i< indent.getItems().size(); i++) {
			JSONObject jo = new JSONObject();
			IndentDetail d = indent.getItems().get(i);
			jo.put("dishid", d.getDishId());
			jo.put("amount", d.getAmount());
			if (d.getWeight() > 0)
				jo.put("weight", d.getWeight() + "");
			if (d.getAdditionalRequirements() != null)
				jo.put("additionalRequirements", d.getAdditionalRequirements());
			ja.put(jo);
		}
		
		String url = "indent/splitindentandpay";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId() + "");
		params.put("confirmCode", mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_CONFIRMCODE));
		params.put("originIndentId",originIndentId+"");
		params.put("indents",ja.toString());		
		params.put("paidPrice", discountPrice + "");
		if (rbPayCash.isSelected()){
			params.put("payWay", ConstantValue.INDENT_PAYWAY_CASH);
		} else if (rbPayBankCard.isSelected()){
			params.put("payWay", ConstantValue.INDENT_PAYWAY_BANKCARD);
		} else if (rbPayMember.isSelected()){
			params.put("payWay", ConstantValue.INDENT_PAYWAY_MEMBER);
			params.put("memberCard", tfMember.getText());
		} else {
			for(JRadioButton rb : listRBOtherPayway){
				if (rb.isSelected()){
					params.put("payWay", rb.getText());
					break;
				}
			}
		}
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error("get null from server while pay splited indent. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while pay splited indent. URL = " + url + ", param = "+ params);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();
		HttpResult<Indent> result = gson.fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
		if (!result.success){
			logger.error("return false while pay splited indent. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, "return false while pay splited indent. URL = " + url + ", response = "+response);
			return;
		}
		indentAfterSplit = result.data;
		this.setVisible(false);
		if (rbPayCash.isSelected()){
			mainFrame.doOpenCashdrawer(false);
		}
	}

	public Indent getIndentAfterSplit() {
		return indentAfterSplit;
	}
	
	
}