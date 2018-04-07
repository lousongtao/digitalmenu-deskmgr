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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.beans.DiscountTemplate;
import com.shuishou.deskmgr.beans.Dish;
import com.shuishou.deskmgr.beans.HttpResult;
import com.shuishou.deskmgr.beans.Indent;
import com.shuishou.deskmgr.beans.IndentDetail;
import com.shuishou.deskmgr.beans.Member;
import com.shuishou.deskmgr.beans.PayWay;
import com.shuishou.deskmgr.http.HttpUtil;
import com.shuishou.deskmgr.printertool.PrintJob;
import com.shuishou.deskmgr.printertool.PrintQueue;
import com.shuishou.deskmgr.ui.components.IconButton;
import com.shuishou.deskmgr.ui.components.JBlockedButton;
import com.shuishou.deskmgr.ui.components.NumberTextField;
import com.shuishou.deskmgr.ui.components.WaitDialog;

public class CheckoutDialog extends JDialog implements ActionListener, DocumentListener, ItemListener{
	private final Logger logger = Logger.getLogger(CheckoutDialog.class.getName());
	protected MainFrame mainFrame;
	protected Desk desk;
	protected Indent indent;
	protected boolean isCancel = false;
	
	protected JLabel lbDiscountPrice = new JLabel();
	protected JRadioButton rbPayCash = new JRadioButton(Messages.getString("CheckoutDialog.Cash")+"   ", true); //$NON-NLS-1$
	protected JRadioButton rbPayBankCard = new JRadioButton(Messages.getString("CheckoutDialog.BandCard"), false); //$NON-NLS-1$
	protected JRadioButton rbPayMember = new JRadioButton(Messages.getString("CheckoutDialog.MemberCard"), false); //$NON-NLS-1$
	protected JRadioButton rbDiscountNon = new JRadioButton(Messages.getString("CheckoutDialog.NoDiscount"), true); //$NON-NLS-1$
	protected JRadioButton rbDiscountTemp = new JRadioButton(Messages.getString("CheckoutDialog.TempDiscount"), false); //$NON-NLS-1$
	protected JRadioButton rbDiscountDirect = new JRadioButton(Messages.getString("CheckoutDialog.DirectDiscount"), false); //$NON-NLS-1$
	protected JLabel lbMemberInfo = new JLabel();
	protected JButton btnQueryMember = new JButton("Query");
	protected JPasswordField tfMemberPwd = new JPasswordField();
	protected ArrayList<JRadioButton> listRBOtherPayway = new ArrayList<>();
	protected NumberTextField tfDiscountAmount = null;
	protected JTextField tfMember = new JTextField();
	protected JBlockedButton btnPay = new JBlockedButton(Messages.getString("CheckoutDialog.PayButton"), "/resource/checkout.png"); //$NON-NLS-1$
	protected JButton btnClose = new JButton(Messages.getString("CloseDialog")); //$NON-NLS-1$
	protected IconButton btnSplitIndent = new IconButton(Messages.getString("CheckoutDialog.SplitIndentButton"), "/resource/splitorder.png"); //$NON-NLS-1$
	protected JButton btnCancelOrder = new JButton(Messages.getString("CheckoutDialog.CancelOrderButton")); //$NON-NLS-1$
	protected NumberTextField numGetCash;
	protected JLabel lbCharge;
	protected double discountPrice = 0;
	
	protected Member member;
	protected List<DiscountTemplateRadioButton> discountTempRadioButtonList = new ArrayList<DiscountTemplateRadioButton>();
	public CheckoutDialog(MainFrame mainFrame,String title, boolean modal, Desk desk, Indent indent){
		super(mainFrame, title, modal);
		this.mainFrame = mainFrame;
		this.desk = desk;
		this.indent = indent;
		discountPrice = indent.getTotalPrice();
		initUI();
	}
	
	private void initUI(){
		JLabel lbDeskNo = new JLabel();
		JLabel lbPrice = new JLabel();
		
		tfDiscountAmount = new NumberTextField(this, true);
		
		numGetCash = new NumberTextField(this, true);
		JLabel lbGetCash = new JLabel(Messages.getString("CheckoutDialog.GetCash"));
		lbCharge = new JLabel();
		
		JPanel pMember = new JPanel(new GridBagLayout());
		pMember.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.MemberCard")));
		pMember.add(rbPayMember, 	new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pMember.add(tfMember, 		new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pMember.add(btnQueryMember, new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		if (Boolean.valueOf(mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_MEMBERMGR_NEEDPASSWORD))){
			JLabel lbPassword = new JLabel(Messages.getString("CheckoutDialog.Password"));
			pMember.add(lbPassword, 	new GridBagConstraints(3, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
			pMember.add(tfMemberPwd, 	new GridBagConstraints(4, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		}
		pMember.add(lbMemberInfo, 	new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pMember.setVisible(MainFrame.functionlist.indexOf(ConstantValue.FUNCTION_MEMBER) >= 0);
		
		JPanel pPayway = new JPanel(new GridBagLayout());
		pPayway.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.PayWay"))); //$NON-NLS-1$
		ButtonGroup bgPayway = new ButtonGroup();
		bgPayway.add(rbPayCash);
		bgPayway.add(rbPayBankCard);
		bgPayway.add(rbPayMember);
		pPayway.add(rbPayCash, 		new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pPayway.add(lbGetCash, 		new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 50, 0, 0), 0, 0));
		pPayway.add(numGetCash, 	new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		pPayway.add(lbCharge, 		new GridBagConstraints(3, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
		pPayway.add(rbPayBankCard, 	new GridBagConstraints(0, 1, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pPayway.add(pMember,		new GridBagConstraints(0, 2, GridBagConstraints.REMAINDER, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		if (!mainFrame.getPaywayList().isEmpty()){
			JPanel pOtherPayway = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
			pOtherPayway.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.OtherPayWay")));
			for (int i = 0; i < mainFrame.getPaywayList().size(); i++) {
				PayWay pw = mainFrame.getPaywayList().get(i);
				JRadioButton rbpw = new JRadioButton(pw.getName());
				bgPayway.add(rbpw);
				pOtherPayway.add(rbpw);
				listRBOtherPayway.add(rbpw);
			}
			pPayway.add(pOtherPayway, new GridBagConstraints(0, 3, 4, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		}
		JPanel pDiscountTemplate = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		pDiscountTemplate.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.DiscountTemplateBorderTitle")));
		if (mainFrame.getDiscountTemplateList().isEmpty()){
			rbDiscountTemp.setEnabled(false);
		} else {
			discountTempRadioButtonList.clear();
			ButtonGroup bg = new ButtonGroup();
			for (int i = 0; i < mainFrame.getDiscountTemplateList().size(); i++) {
				DiscountTemplateRadioButton rb = new DiscountTemplateRadioButton(false, mainFrame.getDiscountTemplateList().get(i));
				rb.addItemListener(new ItemListener(){
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							rbDiscountTemp.setSelected(true);
							calculatePaidPrice();
					    } 
					}
				});
				discountTempRadioButtonList.add(rb);
				bg.add(rb);
				pDiscountTemplate.add(rb);
			}
		}
		
		Dimension dDiscountPrice = tfDiscountAmount.getPreferredSize();
		dDiscountPrice.width = 150;
		tfDiscountAmount.setPreferredSize(dDiscountPrice);
		
		JPanel pDiscount = new JPanel(new GridBagLayout());
		pDiscount.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.BorderDiscount"))); //$NON-NLS-1$
		ButtonGroup bgDiscount = new ButtonGroup();
		bgDiscount.add(rbDiscountNon);
		bgDiscount.add(rbDiscountTemp);
		bgDiscount.add(rbDiscountDirect);
		pDiscount.add(rbDiscountNon, 	new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pDiscount.add(rbDiscountDirect, new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 50, 0, 0), 0, 0));
		pDiscount.add(tfDiscountAmount, new GridBagConstraints(2, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
		pDiscount.add(rbDiscountTemp, 	new GridBagConstraints(0, 1, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		pDiscount.add(pDiscountTemplate,new GridBagConstraints(0, 2, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		
		JPanel pButton = new JPanel(new GridBagLayout());
		btnPay.setPreferredSize(new Dimension(150, 50));
		btnClose.setPreferredSize(new Dimension(150, 50));
		btnCancelOrder.setPreferredSize(new Dimension(150, 50));
		btnSplitIndent.setPreferredSize(new Dimension(150, 50));
		pButton.add(btnPay,			new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		pButton.add(btnSplitIndent,	new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		pButton.add(btnClose,		new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		pButton.add(btnCancelOrder,	new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
		Dimension dPButton = pButton.getPreferredSize();
		dPButton.height = 60;
		pButton.setPreferredSize(dPButton);
		
		lbDeskNo.setFont(ConstantValue.FONT_25BOLD);
		lbPrice.setFont(ConstantValue.FONT_25BOLD);
		lbDiscountPrice.setFont(ConstantValue.FONT_25BOLD);
		lbDeskNo.setText(Messages.getString("CheckoutDialog.TableNo") + desk.getName()); //$NON-NLS-1$
		lbPrice.setText(Messages.getString("CheckoutDialog.Price") + indent.getFormatTotalPrice()); //$NON-NLS-1$
		lbDiscountPrice.setText(Messages.getString("CheckoutDialog.DiscountPrice") + String.format("%.2f", discountPrice)); //$NON-NLS-1$
		
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(lbDeskNo, 		new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(lbPrice, 			new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pPayway, 			new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pDiscount, 		new GridBagConstraints(0, 3, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(lbDiscountPrice, 	new GridBagConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pButton, 			new GridBagConstraints(0, 5, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		this.setSize(new Dimension(750, 700));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
		btnClose.addActionListener(this);
		btnSplitIndent.addActionListener(this);
		btnPay.addActionListener(this);
		btnCancelOrder.addActionListener(this);
		btnQueryMember.addActionListener(this);
		
		numGetCash.getDocument().addDocumentListener(this);
		tfDiscountAmount.getDocument().addDocumentListener(this);
		tfMember.getDocument().addDocumentListener(this);
		
		rbDiscountNon.addItemListener(this);
		rbDiscountTemp.addItemListener(this);
		rbDiscountDirect.addItemListener(this);
		rbPayMember.addItemListener(this);
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == rbDiscountNon){
			if (e.getStateChange() == ItemEvent.SELECTED) {
				calculatePaidPrice();
		    } 
		} else if (e.getSource() == rbDiscountTemp){
			if (e.getStateChange() == ItemEvent.SELECTED) {
				calculatePaidPrice();
		    } 
		} else if (e.getSource() == rbDiscountDirect){
			if (e.getStateChange() == ItemEvent.SELECTED) {
				calculatePaidPrice();
		    } 
		} else if (e.getSource() == rbPayMember){
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				member = null;
				lbMemberInfo.setText("");
				tfMember.setText("");
			}
		} 
		
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		if (e.getDocument() == numGetCash.getDocument()){
			rbPayCash.setSelected(true);
			showChargeText();
		} else if (e.getDocument() == tfDiscountAmount.getDocument()){
			rbDiscountDirect.setSelected(true);
			calculatePaidPrice();
		} else if (e.getDocument() == tfMember.getDocument()){
			if (!rbPayMember.isSelected())
				rbPayMember.setSelected(true);
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (e.getDocument() == numGetCash.getDocument()){
			rbPayCash.setSelected(true);
			showChargeText();
		} else if (e.getDocument() == tfDiscountAmount.getDocument()){
			rbDiscountDirect.setSelected(true);
			calculatePaidPrice();
		} else if (e.getDocument() == tfMember.getDocument()){
			if (!rbPayMember.isSelected())
				rbPayMember.setSelected(true);
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (e.getDocument() == numGetCash.getDocument()){
			rbPayCash.setSelected(true);
			showChargeText();
		} else if (e.getDocument() == tfDiscountAmount.getDocument()){
			rbDiscountDirect.setSelected(true);
			calculatePaidPrice();
		} else if (e.getDocument() == tfMember.getDocument()){
			if (!rbPayMember.isSelected())
				rbPayMember.setSelected(true);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnClose){
			isCancel = true;
			setVisible(false);
		} else if (e.getSource() == btnCancelOrder){
			doCancelOrder();
		} else if (e.getSource() == btnPay){
			doPay();
		} else if (e.getSource() == btnQueryMember){
			doLookforMember();
		} else if (e.getSource() == btnSplitIndent){
			setVisible(false);
			doSplitIndent();
		}
	}
	
	public boolean isCancel(){
		return isCancel;
	}
	private void doLookforMember(){
		lbMemberInfo.setText("");
		member = null;
		if (tfMember.getText() == null || tfMember.getText().length() == 0)
			return;
		String url = "member/querymemberhazily";
		Map<String, String> params = new HashMap<String, String>();
		params.put("key", tfMember.getText());
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server while query member with key. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while query member with key. URL = " + url + ", param = "+ params);
			return;
		}
		Gson gsonTime = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<Member>> result = gsonTime.fromJson(response, new TypeToken<HttpResult<ArrayList<Member>>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while query member with key. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, "return false while query member with key. URL = " + url + ", response = "+response);
			return;
		}
		ArrayList<Member> ms = result.data;
		if (ms == null || ms.isEmpty()){
			JOptionPane.showMessageDialog(this, Messages.getString("CheckoutDialog.NofindMember") + tfMember.getText());
			return;
		} else if (ms.size() == 1){
			member = ms.get(0);
		} else {
			MemberListDialog dlg = new MemberListDialog(mainFrame, ms, 1000, 600);
			dlg.setVisible(true);
			member = dlg.getChoosedMember();
		}
		showMemberInfo(member);
	}
	
	private void showMemberInfo(Member m){
		if (m == null){
			tfMember.setText("");
			lbMemberInfo.setText("");
		} else {
			tfMember.setText(m.getName());
			String memberInfo = Messages.getString("CheckoutDialog.MemberInfo.Name")+ m.getName() + ", " 
				+ Messages.getString("CheckoutDialog.MemberInfo.DiscountRate") + m.getDiscountRate() + ", "
				+ Messages.getString("CheckoutDialog.MemberInfo.Balance") + String.format(ConstantValue.FORMAT_DOUBLE, m.getBalanceMoney());
			if (Boolean.getBoolean(mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_MEMBERMGR_BYSCORE))){
				memberInfo += ", " + Messages.getString("CheckoutDialog.MemberInfo.Score") + String.format(ConstantValue.FORMAT_DOUBLE, m.getScore()); 
			}
			lbMemberInfo.setText(memberInfo);
		}
	}
	
	private void showChargeText(){
		if (!rbPayCash.isSelected())
			return;
		if (numGetCash.getText() == null || numGetCash.getText().length() == 0){
			lbCharge.setText("");
			return;
		}
		double value = Double.parseDouble(numGetCash.getText());
		if (value < discountPrice)
			return;
		lbCharge.setText(Messages.getString("CheckoutDialog.Charge")+" $" + String.format("%.2f", value - discountPrice));
	}
	
	protected DiscountTemplateRadioButton getSelectedDiscountTemplateRadioButton(){
		for (DiscountTemplateRadioButton rb : discountTempRadioButtonList) {
			if (rb.isSelected())
				return rb;
		}
		return null;
	}
	
	private void calculatePaidPrice(){
		if (rbDiscountNon.isSelected()) {
			discountPrice = indent.getTotalPrice();
		} else if(rbDiscountTemp.isSelected()) {
			DiscountTemplateRadioButton rbTemplate = getSelectedDiscountTemplateRadioButton();
			if (rbTemplate == null){
				discountTempRadioButtonList.get(0).setSelected(true);
				rbTemplate = discountTempRadioButtonList.get(0);
			}
			if (rbTemplate.getDiscountTemplate().getType() == ConstantValue.DISCOUNTTYPE_QUANTITY){
				discountPrice = indent.getTotalPrice() + rbTemplate.getDiscountTemplate().getValue();
			} else if (rbTemplate.getDiscountTemplate().getType() == ConstantValue.DISCOUNTTYPE_RATE){
				discountPrice = indent.getTotalPrice() * rbTemplate.getDiscountTemplate().getValue();
			}
			
		} else if (rbDiscountDirect.isSelected()) {
			double dp = 0;
			try{
				dp = Double.parseDouble(tfDiscountAmount.getText());
			}catch(Exception e){}
			discountPrice = indent.getTotalPrice() - dp;
		}
		
		lbDiscountPrice.setText(Messages.getString("CheckoutDialog.DiscountPrice") + new DecimalFormat("0.00").format(discountPrice)); //$NON-NLS-1$
		showChargeText();
	}
	
	public void doPay(){
		if (rbPayMember.isSelected()){
			if (member == null){
				JOptionPane.showMessageDialog(mainFrame, Messages.getString("CheckoutDialog.InputMember")); //$NON-NLS-1$
				return;
			}
			if (Boolean.valueOf(mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_MEMBERMGR_NEEDPASSWORD))
					&& (tfMemberPwd.getText() == null ||tfMemberPwd.getText().length() == 0)){
				JOptionPane.showMessageDialog(mainFrame, Messages.getString("CheckoutDialog.InputMemberPassword")); //$NON-NLS-1$
				return;
			}
		}
		final String url = "indent/dopayindent";
		final Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId() + "");
		params.put("id", indent.getId() + "");
		params.put("operatetype", ConstantValue.INDENT_OPERATIONTYPE_PAY+"");
		params.put("paidPrice", discountPrice + "");
		if (numGetCash.getText() == null || numGetCash.getText().length() ==0){
			params.put("paidCash", "0");
		} else {
			params.put("paidCash", String.format(ConstantValue.FORMAT_DOUBLE, Double.parseDouble(numGetCash.getText())));
		}
		if (rbPayCash.isSelected()){
			params.put("payWay", ConstantValue.INDENT_PAYWAY_CASH);
		} else if (rbPayBankCard.isSelected()){
			params.put("payWay", ConstantValue.INDENT_PAYWAY_BANKCARD);
		} else if (rbPayMember.isSelected()){
			params.put("payWay", ConstantValue.INDENT_PAYWAY_MEMBER);
			params.put("memberCard", member.getMemberCard());
			
			try {
				if (tfMemberPwd.getText() == null || tfMemberPwd.getText().length() == 0)
					params.put("memberPassword", null);
				else 
					params.put("memberPassword", toSHA1(tfMemberPwd.getText().getBytes()));
			} catch (NoSuchAlgorithmException e) {
				JOptionPane.showMessageDialog(mainFrame, e.getMessage()); //$NON-NLS-1$
				return;
			}
		} else {
			for(JRadioButton rb : listRBOtherPayway){
				if (rb.isSelected()){
					params.put("payWay", rb.getText());
					break;
				}
			}
		}
		if (rbDiscountNon.isSelected()){
			params.put("discountTemplate", "");
		} else if (rbDiscountDirect.isSelected()){
			params.put("discountTemplate", tfDiscountAmount.getText());
		} else {
			DiscountTemplateRadioButton rbTemplate = getSelectedDiscountTemplateRadioButton();
			if (rbTemplate == null){
				discountTempRadioButtonList.get(0).setSelected(true);
				rbTemplate = discountTempRadioButtonList.get(0);
			}
			params.put("discountTemplate", String.valueOf(rbTemplate.getDiscountTemplate().getName()));
		}
		
		WaitDialog wdlg = new WaitDialog(this, "Posting data..."){
			public Object work(){
				return HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
			}
		};
		String response = (String)wdlg.getReturnResult();
		HttpResult<Object> result = new Gson().fromJson(response, new TypeToken<HttpResult<Object>>(){}.getType());
//		JSONObject jsonObj = new JSONObject(response);
//		if (!jsonObj.getBoolean("success")){
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("Do checkout failed. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(mainFrame, result.result); //$NON-NLS-1$
		}
		CheckoutDialog.this.setVisible(false);
		
		String change = "0";
		if (rbPayCash.isSelected()){
			double getcash = 0;
			if (numGetCash.getText() != null && numGetCash.getText().length() !=0){
				getcash = Double.parseDouble(numGetCash.getText());
			}
			change = String.format(ConstantValue.FORMAT_DOUBLE, getcash - discountPrice);
		}
		
		//print ticket
		if(result.success){
			double getPay = Double.parseDouble(params.get("paidPrice"));
			if (rbPayCash.isSelected() && numGetCash.getText() != null && numGetCash.getText().length() > 0){
				getPay = Double.parseDouble(numGetCash.getText());
			}
			doPrint(indent, discountPrice, getPay, params.get("payWay"), change);
		}
		
		//clean table
		mainFrame.loadDesks();
		mainFrame.loadCurrentIndentInfo();
		if (rbPayCash.isSelected()){
			mainFrame.doOpenCashdrawer(false);
		}
		
		if (rbPayCash.isSelected()){
			JOptionPane.showMessageDialog(mainFrame, Messages.getString("CheckoutDialog.GetCash") + numGetCash.getText()
			+ "\n" + Messages.getString("CheckoutDialog.ShouldPayAmount") + String.format(ConstantValue.FORMAT_DOUBLE, discountPrice)
			+ "\n" + Messages.getString("CheckoutDialog.Charge") + change);
		}
	}
	
	protected void doPrint(Indent indent, double paidPrice, double getPay, String payway, String change){
		Map<String,String> keys = new HashMap<String, String>();
		keys.put("sequence", indent.getDailySequence()+"");
		keys.put("customerAmount", indent.getCustomerAmount()+"");
		keys.put("tableNo", indent.getDeskName());
		keys.put("printType", "Invoice");
		keys.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
		keys.put("totalPrice", String.format(ConstantValue.FORMAT_DOUBLE, indent.getTotalPrice()));
		keys.put("paidPrice", String.format(ConstantValue.FORMAT_DOUBLE, discountPrice));
		keys.put("gst", String.format(ConstantValue.FORMAT_DOUBLE,(double)(discountPrice/11)));
		keys.put("printTime", ConstantValue.DFYMDHMS.format(new Date()));
		keys.put("payway", payway);
		if (getPay > 0){
			keys.put("change", String.format(ConstantValue.FORMAT_DOUBLE, getPay - discountPrice));
		} else {
			keys.put("change", "0");
		}
		if (ConstantValue.INDENT_PAYWAY_MEMBER.equals(payway)){
			Member m = doLookforMember(member.getMemberCard());
			if (m == null)
				keys.put("memberinfo", "");
			else keys.put("memberinfo", "Member: balance \\$" + m.getBalanceMoney() + ", score " + m.getScore());
		} else {
			keys.put("memberinfo", "");
		}
		List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
		for(IndentDetail d : indent.getItems()){
			Dish dish = mainFrame.getDishById(d.getDishId());
			if (dish == null){
				JOptionPane.showMessageDialog(mainFrame, "Print ticket failed. The reason is that cannot find dish by ID " + d.getDishId() +". Please restart this app and retry");
				return;
			}
			Map<String, String> mg = new HashMap<String, String>();
			mg.put("name", d.getDishFirstLanguageName());
			mg.put("price", String.format(ConstantValue.FORMAT_DOUBLE,d.getDishPrice()));
			mg.put("amount", d.getAmount()+"");
			
			String requirement = "";
			if (d.getAdditionalRequirements() != null)
				requirement += d.getAdditionalRequirements();
			//按重量卖的dish, 把重量加入requirement
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
				requirement += " " + d.getWeight();
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT){
				mg.put("totalPrice", String.format(ConstantValue.FORMAT_DOUBLE,d.getWeight() * d.getDishPrice() * d.getAmount()));
			} else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT){
				mg.put("totalPrice", String.format(ConstantValue.FORMAT_DOUBLE,d.getDishPrice() * d.getAmount()));
			}
			mg.put("requirement", requirement);
			goods.add(mg);
			
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", keys);
		params.put("goods", goods);
		PrintJob job = new PrintJob("/payorder_template.json", params, mainFrame.printerName);
		PrintQueue.add(job);
	}
	
	private Member doLookforMember(String memberCard){
		String url = "member/querymember";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId()+"");
		params.put("memberCard", member.getMemberCard());
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			return null;
		}
		Gson gsonTime = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<Member>> result = gsonTime.fromJson(response, new TypeToken<HttpResult<ArrayList<Member>>>(){}.getType());
		if (!result.success){
			return null;
		}
		ArrayList<Member> ms = result.data;
		if (ms == null || ms.isEmpty()){
			return null;
		} else {
			return ms.get(0);
		} 
	}
	
	private void doSplitIndent(){
		SplitIndentDialog dlg  = new SplitIndentDialog(mainFrame, Messages.getString("SplitIndentDialog.title"), desk, indent);
		dlg.setVisible(true);
	}
	
	private void doCancelOrder(){
		String code = JOptionPane.showInputDialog(this, Messages.getString("CheckoutDialog.InputCodeOfCancelOrder"));
		if (code == null){
			return;
		}
		if (!code.equals(mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_CANCELORDERCODE))){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.ErrorCashdrawerCode"));
			return;
		}
		
		String url = "indent/docancelindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId() + "");
		params.put("id", indent.getId() + "");
		params.put("operatetype", ConstantValue.INDENT_OPERATIONTYPE_CANCEL+"");
		params.put("paidCash", "0");
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		JSONObject jsonObj = new JSONObject(response);
		if (!jsonObj.getBoolean("success")){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("Do checkout failed. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(mainFrame, Messages.getString("CheckoutDialog.FailPayMsg")); //$NON-NLS-1$
		}
		//clean table
		CheckoutDialog.this.setVisible(false);
		mainFrame.loadDesks();
		mainFrame.loadCurrentIndentInfo();
	}
	
	
	
	public IconButton getBtnSplitIndent() {
		return btnSplitIndent;
	}

	public JButton getBtnCancelOrder() {
		return btnCancelOrder;
	}

	public String toSHA1(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException ex) {
			logger.error("Can't get SHA-1 algorithm message digest.");
			throw ex;
		}
		return toHex(md.digest(data));
	}

	private String toHex(byte[] digest) {
		StringBuilder sb = new StringBuilder();
		for (byte b : digest) {
			sb.append(String.format("%1$02X", b));
		}

		return sb.toString();
	}

	class DiscountTemplateRadioButton extends JRadioButton{
		private DiscountTemplate temp;
		public DiscountTemplateRadioButton (boolean selected, DiscountTemplate temp) {
	        super(temp.getName(), selected);
	        this.temp = temp;
	    }
		public DiscountTemplate getDiscountTemplate() {
			return temp;
		}
		public void setDiscountTemplate(DiscountTemplate temp) {
			this.temp = temp;
		}
	}
}
