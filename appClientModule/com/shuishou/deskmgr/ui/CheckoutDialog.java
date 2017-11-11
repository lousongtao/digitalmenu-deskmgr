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
import org.json.JSONObject;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.beans.DiscountTemplate;
import com.shuishou.deskmgr.beans.Indent;
import com.shuishou.deskmgr.beans.PayWay;
import com.shuishou.deskmgr.http.HttpUtil;
import com.shuishou.deskmgr.ui.components.IconButton;
import com.shuishou.deskmgr.ui.components.JBlockedButton;
import com.shuishou.deskmgr.ui.components.NumberTextField;

public class CheckoutDialog extends JDialog{
	private final Logger logger = Logger.getLogger(CheckoutDialog.class.getName());
	protected MainFrame mainFrame;
	protected Desk desk;
	protected Indent indent;
	
	protected JLabel lbDiscountPrice = new JLabel();
	protected JRadioButton rbPayCash = new JRadioButton(Messages.getString("CheckoutDialog.Cash"), true); //$NON-NLS-1$
	protected JRadioButton rbPayBankCard = new JRadioButton(Messages.getString("CheckoutDialog.BandCard"), false); //$NON-NLS-1$
	protected JRadioButton rbPayMember = new JRadioButton(Messages.getString("CheckoutDialog.MemberCard"), false); //$NON-NLS-1$
	protected JRadioButton rbDiscountNon = new JRadioButton(Messages.getString("CheckoutDialog.NoDiscount"), true); //$NON-NLS-1$
	protected JRadioButton rbDiscountTemp = new JRadioButton(Messages.getString("CheckoutDialog.TempDiscount"), false); //$NON-NLS-1$
	protected JRadioButton rbDiscountDirect = new JRadioButton(Messages.getString("CheckoutDialog.DirectDiscount"), false); //$NON-NLS-1$
	protected ArrayList<JRadioButton> listRBOtherPayway = new ArrayList<>();
	protected NumberTextField tfDiscountPrice = null;
	protected JTextField tfMember = new JTextField();
	protected JBlockedButton btnPay = new JBlockedButton(Messages.getString("CheckoutDialog.PayButton"), "/resource/checkout.png"); //$NON-NLS-1$
	protected JButton btnClose = new JButton(Messages.getString("CloseDialog")); //$NON-NLS-1$
	protected IconButton btnSplitIndent = new IconButton(Messages.getString("CheckoutDialog.SplitIndentButton"), "/resource/splitorder.png"); //$NON-NLS-1$
	protected JButton btnCancelOrder = new JButton(Messages.getString("CheckoutDialog.CancelOrderButton")); //$NON-NLS-1$
	protected NumberTextField numGetCash;
	protected JLabel lbCharge;
	protected double discountPrice = 0;
	
	private List<DiscountTemplateRadioButton> discountTempRadioButtonList = new ArrayList<DiscountTemplateRadioButton>();
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
		
		tfDiscountPrice = new NumberTextField(this, true);
		
		numGetCash = new NumberTextField(this, true);
		JLabel lbGetCash = new JLabel(Messages.getString("CheckoutDialog.GetCash"));
		lbCharge = new JLabel();
		Color bgPayColor = new Color(201,255,255);
		Color bgDiscountColor = new Color(235, 255, 244);
		
		JPanel pPayway = new JPanel(new GridBagLayout());
//		pPayway.setBackground(bgPayColor);
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
		pPayway.add(rbPayMember,	new GridBagConstraints(1, 1, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 50, 0, 0), 0, 0));
		pPayway.add(tfMember, 		new GridBagConstraints(2, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 20, 0, 0), 0, 0));
		if (!mainFrame.getPaywayList().isEmpty()){
			JPanel pOtherPayway = new JPanel();
//			pOtherPayway.setBackground(bgPayColor);
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
//		pDiscountTemplate.setBackground(bgDiscountColor);
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
		
		Dimension dDiscountPrice = tfDiscountPrice.getPreferredSize();
		dDiscountPrice.width = 150;
		tfDiscountPrice.setPreferredSize(dDiscountPrice);
		
		JPanel pDiscount = new JPanel(new GridBagLayout());
//		pDiscount.setBackground(bgDiscountColor);
		pDiscount.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.BorderDiscount"))); //$NON-NLS-1$
		ButtonGroup bgDiscount = new ButtonGroup();
		bgDiscount.add(rbDiscountNon);
		bgDiscount.add(rbDiscountTemp);
		bgDiscount.add(rbDiscountDirect);
		pDiscount.add(rbDiscountNon, 	new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pDiscount.add(rbDiscountDirect, new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 50, 0, 0), 0, 0));
		pDiscount.add(tfDiscountPrice, 	new GridBagConstraints(2, 0, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
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
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				CheckoutDialog.this.setVisible(false);
			}});
		
		btnSplitIndent.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				CheckoutDialog.this.setVisible(false);
				doSplitIndent();
			}});
		
		btnPay.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doPay();
			}});
		
		btnCancelOrder.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doCancelOrder();
			}});
		
		numGetCash.getDocument().addDocumentListener(new DocumentListener(){

			
			@Override
			public void insertUpdate(DocumentEvent e) {
				rbPayCash.setSelected(true);
				showChargeText();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				rbPayCash.setSelected(true);
				showChargeText();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				rbPayCash.setSelected(true);
				showChargeText();
			}});
		tfDiscountPrice.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void insertUpdate(DocumentEvent e) {
				rbDiscountDirect.setSelected(true);
				calculatePaidPrice();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				rbDiscountDirect.setSelected(true);
				calculatePaidPrice();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				rbDiscountDirect.setSelected(true);
				calculatePaidPrice();
			}});
		
		tfMember.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void insertUpdate(DocumentEvent e) {
				rbPayMember.setSelected(true);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				rbPayMember.setSelected(true);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				rbPayMember.setSelected(true);
			}});
		
		rbDiscountNon.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					calculatePaidPrice();
			    } 
			}
		});
		
		rbDiscountTemp.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					calculatePaidPrice();
			    } 
			}
		});
		
		rbDiscountDirect.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					calculatePaidPrice();
			    } 
			}
		});
		
		
		
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
	
	private DiscountTemplateRadioButton getSelectedDiscountTemplateRadioButton(){
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
			discountPrice = indent.getTotalPrice() * rbTemplate.getDiscountTemplate().getRate();
		} else if (rbDiscountDirect.isSelected()) {
			double dp = 0;
			try{
				dp = Double.parseDouble(tfDiscountPrice.getText());
			}catch(Exception e){}
			discountPrice = indent.getTotalPrice() - dp;
		}
		
		lbDiscountPrice.setText(Messages.getString("CheckoutDialog.DiscountPrice") + new DecimalFormat("0.00").format(discountPrice)); //$NON-NLS-1$
		showChargeText();
	}
	
	public void doPay(){
		if (rbPayMember.isSelected()){
			if (tfMember.getText() == null || tfMember.getText().length() == 0){
				JOptionPane.showMessageDialog(mainFrame, Messages.getString("CheckoutDialog.InputNumber")); //$NON-NLS-1$
				return;
			}
		}
		String url = "indent/operateindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId() + "");
		params.put("id", indent.getId() + "");
		params.put("operatetype", ConstantValue.INDENT_OPERATIONTYPE_PAY+"");
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
		JSONObject jsonObj = new JSONObject(response);
		if (!jsonObj.getBoolean("success")){
			logger.error("Do checkout failed. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(mainFrame, Messages.getString("CheckoutDialog.FailPayMsg")); //$NON-NLS-1$
		}
		//clean table
		CheckoutDialog.this.setVisible(false);
		mainFrame.loadDesks();
		mainFrame.loadCurrentIndentInfo();
		if (rbPayCash.isSelected()){
			mainFrame.doOpenCashdrawer(false);
		}
	}
	
	private void doSplitIndent(){
		SplitIndentDialog dlg  = new SplitIndentDialog(mainFrame, Messages.getString("SplitIndentDialog.title"), desk, indent);
		dlg.setVisible(true);
	}
	
	private void doCancelOrder(){
		if (JOptionPane.showConfirmDialog(this, Messages.getString("CheckoutDialog.ConfirmCancelOrder"), "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			return;
		String url = "indent/operateindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId() + "");
		params.put("id", indent.getId() + "");
		params.put("operatetype", ConstantValue.INDENT_OPERATIONTYPE_CANCEL+"");
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		JSONObject jsonObj = new JSONObject(response);
		if (!jsonObj.getBoolean("success")){
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
