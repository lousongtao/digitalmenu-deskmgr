package com.shuishou.deskmgr.ui;

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
import com.shuishou.deskmgr.http.HttpUtil;

public class CheckoutDialog extends JDialog{
	private final Logger logger = Logger.getLogger(CheckoutDialog.class.getName());
	private MainFrame mainFrame;
	private Desk desk;
	private Indent indent;
	
	private JLabel lbDiscountPrice = new JLabel();
	private JRadioButton rbPayCash = new JRadioButton(Messages.getString("CheckoutDialog.Cash"), true); //$NON-NLS-1$
	private JRadioButton rbPayBankCard = new JRadioButton(Messages.getString("CheckoutDialog.BandCard"), false); //$NON-NLS-1$
	private JRadioButton rbPayMember = new JRadioButton(Messages.getString("CheckoutDialog.MemberCard"), false); //$NON-NLS-1$
	private JRadioButton rbDiscountNon = new JRadioButton(Messages.getString("CheckoutDialog.NoDiscount"), true); //$NON-NLS-1$
	private JRadioButton rbDiscountTemp = new JRadioButton(Messages.getString("CheckoutDialog.TempDiscount"), false); //$NON-NLS-1$
	private JRadioButton rbDiscountDirect = new JRadioButton(Messages.getString("CheckoutDialog.DirectDiscount"), false); //$NON-NLS-1$

	private JFormattedTextField tfDiscountPrice = null;
	private JTextField tfMember = new JTextField();
	private JButton btnPay = new JButton(Messages.getString("CheckoutDialog.PayButton")); //$NON-NLS-1$
	private JButton btnClose = new JButton(Messages.getString("CheckoutDialog.CloseButton")); //$NON-NLS-1$
	
	private double discountPrice = 0;
	
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
		
//		NumberFormat longFormat = NumberFormat.getNumberInstance();
//		NumberFormatter numberFormatter = new NumberFormatter(longFormat);
//		numberFormatter.setValueClass(Long.class); //optional, ensures you will always get a long value
//		numberFormatter.setAllowsInvalid(false); //this is the key!!
//		numberFormatter.setMinimum(0l); //Optional
//		tfDiscountPrice = new JFormattedTextField(numberFormatter);
		tfDiscountPrice = new JFormattedTextField();
		
		JPanel pPayway = new JPanel(new GridBagLayout());
		pPayway.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.PayWay"))); //$NON-NLS-1$
		ButtonGroup bgPayway = new ButtonGroup();
		bgPayway.add(rbPayCash);
		bgPayway.add(rbPayBankCard);
		bgPayway.add(rbPayMember);
		pPayway.add(rbPayCash, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pPayway.add(rbPayBankCard, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pPayway.add(rbPayMember,new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		pPayway.add(tfMember, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		
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
		
		JPanel pDiscount = new JPanel(new GridBagLayout());
		pDiscount.setBorder(BorderFactory.createTitledBorder(Messages.getString("CheckoutDialog.BorderDiscount"))); //$NON-NLS-1$
		ButtonGroup bgDiscount = new ButtonGroup();
		bgDiscount.add(rbDiscountNon);
		bgDiscount.add(rbDiscountTemp);
		bgDiscount.add(rbDiscountDirect);
		pDiscount.add(rbDiscountNon, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pDiscount.add(rbDiscountTemp, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		pDiscount.add(pDiscountTemplate, new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		pDiscount.add(rbDiscountDirect, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		pDiscount.add(tfDiscountPrice, new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		
		JPanel pButton = new JPanel(new FlowLayout(FlowLayout.LEFT, 60, 5));
		btnPay.setPreferredSize(new Dimension(150, 40));
		btnClose.setPreferredSize(new Dimension(150, 40));
		pButton.add(btnPay);
		pButton.add(btnClose);
		
		lbDeskNo.setFont(ConstantValue.FONT_30BOLD);
		lbPrice.setFont(ConstantValue.FONT_30BOLD);
		lbDiscountPrice.setFont(ConstantValue.FONT_30BOLD);
		lbDeskNo.setText(Messages.getString("CheckoutDialog.TableNo") + desk.getName()); //$NON-NLS-1$
		lbPrice.setText(Messages.getString("CheckoutDialog.Price") + indent.getTotalPrice()); //$NON-NLS-1$
		lbDiscountPrice.setText(Messages.getString("CheckoutDialog.DiscountPrice") + discountPrice); //$NON-NLS-1$
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				CheckoutDialog.this.setVisible(false);
			}});
		
		btnPay.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doPay();
			}});
		
		tfDiscountPrice.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE) || (c == '.'))) {
					getToolkit().beep();
					e.consume();
				} 
				if (c == '.'){
					if (tfDiscountPrice.getText() != null && tfDiscountPrice.getText().indexOf(".") >= 0){
						getToolkit().beep();
						e.consume();
					}
				}
				
			}
		});
		
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
		
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(lbDeskNo, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(lbPrice, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pPayway, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pDiscount, new GridBagConstraints(0, 3, 1, 1, 1, 3, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(lbDiscountPrice, new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pButton, new GridBagConstraints(0, 5, 1, 1, 1, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		this.setSize(new Dimension(600, 600));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
		
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
	}
	
	private void doPay(){
		if (rbPayMember.isSelected()){
			if (tfMember.getText() == null || tfMember.getText().length() == 0){
				JOptionPane.showMessageDialog(mainFrame, Messages.getString("CheckoutDialog.InputNumber")); //$NON-NLS-1$
				return;
			}
		}
		String url = "indent/operateindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getLoginUser().getId() + "");
		params.put("id", indent.getId() + "");
		params.put("operatetype", ConstantValue.INDENT_OPERATIONTYPE_PAY+"");
		params.put("paidPrice", discountPrice + "");
		if (rbPayCash.isSelected())
			params.put("payWay", ConstantValue.INDENT_PAYWAY_CASH + "");
		else if (rbPayBankCard.isSelected())
			params.put("payWay", ConstantValue.INDENT_PAYWAY_CARD + "");
		else if (rbPayMember.isSelected()){
			params.put("payWay", ConstantValue.INDENT_PAYWAY_MEMBER + "");
			params.put("memberCard", tfMember.getText());
		}
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
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
