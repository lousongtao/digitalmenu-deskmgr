package com.shuishou.deskmgr.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.beans.Dish;
import com.shuishou.deskmgr.beans.HttpResult;
import com.shuishou.deskmgr.http.HttpUtil;

public class OpenTableDialog extends JDialog {
	private final Logger logger = Logger.getLogger(CheckoutDialog.class.getName());
	private MainFrame mainFrame;
	private Desk desk;
	
	private JTextField tfSearchCode = new JTextField();
	private JTextField tfCustomerAmount = new JTextField();
	private JButton btnAdd = new JButton(Messages.getString("OpenTableDialog.AddDish"));
	private JButton btnRemove = new JButton(Messages.getString("OpenTableDialog.RemoveDish"));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JButton btnConfirm = new JButton(Messages.getString("OpenTableDialog.ConfirmOrder"));
	private JList<Dish> listSearchResult = new JList<>();
	private DefaultListModel<Dish> listModelSearchResult = new DefaultListModel<>();
	private JList<ChoosedDish> listChoosedDish = new JList<>();
	private DefaultListModel<ChoosedDish> listModelChoosedDish = new DefaultListModel<>();
	
	public static final byte MAKENEWORDER = 1;
	public static final byte ADDDISH = 2;
	private byte status = 0;
	
	public OpenTableDialog(MainFrame mainFrame,String title, boolean modal, Desk desk, byte status){
		super(mainFrame, title, modal);
		this.mainFrame = mainFrame;
		this.desk = desk;
		this.status = status;
		initUI();
	}
	
	private void initUI(){
		JLabel lbDeskNo = new JLabel(Messages.getString("OpenTableDialog.TableNo") + desk.getName());
		lbDeskNo.setFont(ConstantValue.FONT_30BOLD);
		JLabel lbCustomerAmount = new JLabel();
		lbCustomerAmount.setFont(ConstantValue.FONT_30BOLD);
		lbCustomerAmount.setText(Messages.getString("OpenTableDialog.CustomerAmount"));
		if (status == ADDDISH){
			lbCustomerAmount.setVisible(false);
			tfCustomerAmount.setVisible(false);
		}
		JLabel lbSearchCode = new JLabel(Messages.getString("OpenTableDialog.SearchCode"));
		listSearchResult.setModel(listModelSearchResult);
		listSearchResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSearchResult.setCellRenderer(new SearchResultRenderer());
		listSearchResult.setFixedCellHeight(50);
		listChoosedDish.setModel(listModelChoosedDish);
		listChoosedDish.setCellRenderer(new ChoosedDishRenderer());
		listChoosedDish.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listChoosedDish.setFixedCellHeight(50);
		btnAdd.setPreferredSize(new Dimension(100, 50));
		btnRemove.setPreferredSize(new Dimension(100, 50));
		btnClose.setPreferredSize(new Dimension(100, 50));
		btnConfirm.setPreferredSize(new Dimension(100, 50));
		
		JScrollPane jspSearchResult = new JScrollPane(listSearchResult, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane jspChooseDish = new JScrollPane(listChoosedDish, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JPanel pLeft = new JPanel(new GridBagLayout());
		pLeft.add(lbDeskNo, 		new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pLeft.add(lbCustomerAmount, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pLeft.add(tfCustomerAmount, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pLeft.add(lbSearchCode, 	new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pLeft.add(tfSearchCode, 	new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pLeft.add(jspSearchResult, 	new GridBagConstraints(0, 3, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		JPanel pCenter = new JPanel(new GridBagLayout());
		pCenter.add(btnAdd, 		new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pCenter.add(btnRemove, 		new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		JPanel pRight = new JPanel(new GridBagLayout());
		pRight.add(jspChooseDish, 	new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pRight.add(btnClose,	 	new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pRight.add(btnConfirm,		new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(pLeft, 	new GridBagConstraints(0, 0, 1, 1, 5, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pCenter, 	new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pRight, 	new GridBagConstraints(2, 0, 1, 1, 5, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				OpenTableDialog.this.setVisible(false);
			}});
		
		btnConfirm.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (status == MAKENEWORDER){
					if (doMakeNewOrder()){
						mainFrame.loadCurrentIndentInfo();
						OpenTableDialog.this.setVisible(false);
					}
				} else if (status == ADDDISH){
					if (doAddDish()){
						mainFrame.loadCurrentIndentInfo();
						OpenTableDialog.this.setVisible(false);
					}
				}
			}});
		btnAdd.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doChooseDish();
			}});
		btnRemove.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doRemoveDish();
			}});
		tfSearchCode.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {
				doSearchDish();
			}
		});
		tfCustomerAmount.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!((c >= '0') && (c <= '9'))) {
					getToolkit().beep();
					e.consume();
				} 
			}
		});
		this.setSize(new Dimension(800, 600));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	private void doSearchDish(){
		listModelSearchResult.clear();
		if (tfSearchCode.getText() == null || this.tfSearchCode.getText().length() < 2)
			return;
		ArrayList<Dish> allDishes = mainFrame.getAllDishes();
		for(Dish dish : allDishes){
			if (dish.getEnglishName().toLowerCase().indexOf(tfSearchCode.getText().toLowerCase()) >= 0){
				this.listModelSearchResult.addElement(dish);
			} else if (dish.getAbbreviation() != null && dish.getAbbreviation().toLowerCase().indexOf(tfSearchCode.getText().toLowerCase()) >= 0){
				this.listModelSearchResult.addElement(dish);
			}
		}
		if (listModelSearchResult.size() > 0)
			listSearchResult.setSelectedIndex(0);
	}
	
	private void doChooseDish(){
		Dish dish = listSearchResult.getSelectedValue();
		if (dish == null)
			return;
		for(int i = 0; i< this.listModelChoosedDish.size(); i++){
			if (listModelChoosedDish.getElementAt(i).dish.getId() == dish.getId()){
				listModelChoosedDish.getElementAt(i).amount = listModelChoosedDish.getElementAt(i).amount + 1;
				listChoosedDish.updateUI();
				return;
			}
		}
		ChoosedDish cd = new ChoosedDish();
		cd.dish = dish;
		cd.amount = 1;
		listModelChoosedDish.addElement(cd);
	}
	
	private void doRemoveDish(){
		if (listChoosedDish.getSelectedIndex() < 0)
			return;
		listModelChoosedDish.removeElementAt(listChoosedDish.getSelectedIndex());
	}
	
	private boolean doMakeNewOrder(){
		if (listModelChoosedDish.getSize() == 0)
			return false;
		if (tfCustomerAmount.getText() == null || tfCustomerAmount.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input customer amount.");
			return false;
		}
		JSONArray ja = new JSONArray();
		for (int i = 0; i< listModelChoosedDish.getSize(); i++) {
			JSONObject jo = new JSONObject();
			jo.put("id", listModelChoosedDish.getElementAt(i).dish.getId());
			jo.put("amount", listModelChoosedDish.getElementAt(i).amount);
			ja.put(jo);
		}
		String url = "indent/makeindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("confirmCode", mainFrame.getConfirmCode());
		params.put("indents", ja.toString());
		params.put("deskid", desk.getId()+"");
		params.put("customerAmount", tfCustomerAmount.getText());
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server while making order. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while making order. URL = " + url + ", param = "+ params);
			return false;
		}
		HttpResult<Integer> result = new Gson().fromJson(response, new TypeToken<HttpResult<Integer>>(){}.getType());
		if (!result.success){
			logger.error("return false while making order. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while making order. URL = " + url);
			return false;
		}
		return true;
	}
	
	private boolean doAddDish(){
		if (listModelChoosedDish.getSize() == 0)
			return false;
		
		JSONArray ja = new JSONArray();
		for (int i = 0; i< listModelChoosedDish.getSize(); i++) {
			JSONObject jo = new JSONObject();
			jo.put("id", listModelChoosedDish.getElementAt(i).dish.getId());
			jo.put("amount", listModelChoosedDish.getElementAt(i).amount);
			ja.put(jo);
		}
		String url = "indent/adddishtoindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("indents", ja.toString());
		params.put("deskid", desk.getId()+"");
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server while add dish to order. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while add dish to order. URL = " + url + ", param = "+ params);
			return false;
		}
		HttpResult<Integer> result = new Gson().fromJson(response, new TypeToken<HttpResult<Integer>>(){}.getType());
		if (!result.success){
			logger.error("return false while add dish to order. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while add dish to order. URL = " + url);
			return false;
		}
		return true;
	}
	
	class SearchResultRenderer extends JPanel implements ListCellRenderer{
		private JLabel lb= new JLabel();
		public SearchResultRenderer(){
			setLayout(new BorderLayout());
			add(lb, BorderLayout.CENTER);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }
			Dish dish = (Dish)value;
			String txt = dish.getChineseName() + "/" + dish.getEnglishName();
			lb.setText(txt);
			return this;
		}
		
	}
	
	
	class ChoosedDishRenderer extends JPanel implements ListCellRenderer{
		private JLabel lbDish = new JLabel();
		private JLabel lbAmount = new JLabel();
		public ChoosedDishRenderer(){
			setLayout(new BorderLayout());
			add(lbDish, BorderLayout.CENTER);
			add(lbAmount, BorderLayout.EAST);
		}
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }
			ChoosedDish cd = (ChoosedDish)value;
			Dish dish = cd.dish;
			String txt = dish.getChineseName() + "/" + dish.getEnglishName();
			if (txt.length() > 20)
				txt = txt.substring(0, 20) + "...";
			lbDish.setText(txt);
			lbAmount.setText(cd.amount+"");
			return this;
		}
		
	}
	
	class ChoosedDish {
		public int amount;
		public Dish dish;
	}
}
