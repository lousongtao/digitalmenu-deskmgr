package com.shuishou.deskmgr.ui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.beans.Dish;
import com.shuishou.deskmgr.beans.HttpResult;
import com.shuishou.deskmgr.beans.Indent;
import com.shuishou.deskmgr.beans.IndentDetail;
import com.shuishou.deskmgr.http.HttpUtil;
import com.shuishou.deskmgr.ui.components.NumberInputDialog;

public class ViewIndentDialog extends JDialog {
	private final Logger logger = Logger.getLogger(CheckoutDialog.class.getName());
	private MainFrame mainFrame;
	private Desk desk;
	private Indent indent;
	
	private JButton btnRemove = new JButton(Messages.getString("ViewIndentDialog.RemoveDish"));
	private JButton btnChangeAmount = new JButton(Messages.getString("ViewIndentDialog.ChangeAmount"));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JLabel lbPrice = new JLabel();
	private JTable tabIndentDetail = new JTable();
	private IndentDetailModel tableModel = null;
	private Vector tableHeader = new Vector();
	
	public ViewIndentDialog(MainFrame mainFrame,String title, boolean modal, Desk desk, Indent indent){
		super(mainFrame, title, modal);
		this.mainFrame = mainFrame;
		this.desk = desk;
		this.indent = indent;
		initUI();
//		initData();
	}
	
	private void initUI(){
		JLabel lbDeskNo = new JLabel(Messages.getString("ViewIndentDialog.TableNo") + desk.getName());
		lbDeskNo.setFont(ConstantValue.FONT_30BOLD);
		
		lbPrice.setFont(ConstantValue.FONT_30BOLD);
		lbPrice.setText(Messages.getString("ViewIndentDialog.Price")+" $" + indent.getFormatTotalPrice());
		btnRemove.setPreferredSize(new Dimension(100, 50));
		btnChangeAmount.setPreferredSize(new Dimension(100,50));
		btnClose.setPreferredSize(new Dimension(100, 50));
		
		Vector vData = new Vector();
//		for (int i = 0; i < indent.getItems().size(); i++) {
//			Vector vRow = new Vector();
//			vRow.add(indent.getItems().get(i).getDishChineseName());
//			vRow.add(indent.getItems().get(i).getDishEnglishName());
//			vRow.add(indent.getItems().get(i).getAmount());
//			vRow.add(indent.getItems().get(i).getDishPrice());
//			vRow.add(indent.getItems().get(i).getAdditionalRequirements());
//			vRow.add(indent.getItems().get(i));
//			vData.add(vRow);
//		}
//		tableModel = new DefaultTableModel(vData, tableHeader){
//			public boolean isCellEditable(int row, int column){
//				return false;
//			}
//		};
		tableModel = new IndentDetailModel(indent.getItems());
		tabIndentDetail.setModel(tableModel);
		tabIndentDetail.setRowHeight(50);
		tabIndentDetail.getColumnModel().getColumn(0).setPreferredWidth(150);
		tabIndentDetail.getColumnModel().getColumn(1).setPreferredWidth(150);
		tabIndentDetail.getColumnModel().getColumn(2).setPreferredWidth(50);
		tabIndentDetail.getColumnModel().getColumn(3).setPreferredWidth(80);
		tabIndentDetail.getColumnModel().getColumn(4).setPreferredWidth(300);
		JScrollPane jspTable = new JScrollPane(tabIndentDetail, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JPanel pTop = new JPanel(new GridBagLayout());
		pTop.add(lbDeskNo, 		new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pTop.add(lbPrice, 		new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 100, 0, 0), 0, 0));
		
		JPanel pFunction = new JPanel(new GridBagLayout());
		pFunction.add(btnRemove, 		new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pFunction.add(btnChangeAmount,	 new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 50, 0, 0), 0, 0));
		pFunction.add(btnClose,			new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 50, 0, 0), 0, 0));

		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(pTop, 		new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(jspTable, 	new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pFunction, 	new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				ViewIndentDialog.this.setVisible(false);
			}});
		
		btnRemove.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doRemoveDish();
			}});
		btnChangeAmount.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doChangeAmount();
			}});
		this.setSize(new Dimension(1000, 600));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
		
	}
	
	private void refreshData(){
		tableModel.setData(indent.getItems());
		tableModel.fireTableDataChanged();
		lbPrice.setText(Messages.getString("ViewIndentDialog.Price")+" $" + indent.getFormatTotalPrice());
	}
	
	private void doRemoveDish(){
		int row = tabIndentDetail.getSelectedRow();
		if (row < 0)
			return;
		if (JOptionPane.showConfirmDialog(this, Messages.getString("ViewIndentDialog.ConfirmDeleteDish"), "", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
			return;
		String url = "indent/operateindentdetail";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId()+"");
		params.put("operatetype", ConstantValue.INDENTDETAIL_OPERATIONTYPE_DELETE+"");
		params.put("indentDetailId", tableModel.getObjectAt(row).getId()+"");
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error("get null from server while delete indent detail. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while delete indent detail. URL = " + url + ", param = "+ params);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss").create();
		HttpResult<Indent> result = gson.fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
		if (!result.success){
			logger.error("return false while delete indent detail. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while delete indent detail. URL = " + url);
			return;
		}
		this.indent = result.data;
		mainFrame.loadCurrentIndentInfo();
		refreshData();
	}
	
	private void doChangeAmount(){
		int row = tabIndentDetail.getSelectedRow();
		if (row < 0)
			return;
		NumberInputDialog dlg = new NumberInputDialog(this, "input", Messages.getString("ViewIndentDialog.ChangeAmountMessage"), false);
		dlg.setVisible(true);
		if (!dlg.isConfirm)
			return;
		int newAmount = dlg.inputInteger;
		String url = "indent/operateindentdetail";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId()+"");
		params.put("operatetype", ConstantValue.INDENTDETAIL_OPERATIONTYPE_CHANGEAMOUNT+"");
		params.put("indentDetailId", tableModel.getObjectAt(row).getId()+"");
		params.put("amount", newAmount+"");
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error("get null from server while delete indent detail. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while delete indent detail. URL = " + url + ", param = "+ params);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss").create();
		HttpResult<Indent> result = gson.fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
		if (!result.success){
			logger.error("return false while delete indent detail. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while delete indent detail. URL = " + url);
			return;
		}
		this.indent = result.data;
		mainFrame.loadCurrentIndentInfo();
		refreshData();
	}
	
	class IndentDetailModel extends AbstractTableModel{
		private List<IndentDetail> items;
		public IndentDetailModel(List<IndentDetail> items){
			this.items = items;
		}
		@Override
		public int getRowCount() {
			return items.size();
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			IndentDetail d = getObjectAt(rowIndex);
			switch(columnIndex){
			case 0:
				return d.getDishChineseName();
			case 1:
				return d.getDishEnglishName();
			case 2:
				return d.getAmount();
			case 3:
				return d.getDishPrice();
			case 4:
				String req = "";
				if (d.getAdditionalRequirements() != null)
					req += d.getAdditionalRequirements();
				if (d.getWeight() > 0)
					req += d.getWeight();
				return req;
			}
			return "";
		}
		
		@Override
		public String getColumnName(int column) {
			switch(column){
			case 0:
				return Messages.getString("ViewIndentDialog.Header.ChineseName");
			case 1:
				return Messages.getString("ViewIndentDialog.Header.EnglishName");
			case 2:
				return Messages.getString("ViewIndentDialog.Header.Amount");
			case 3:
				return Messages.getString("ViewIndentDialog.Header.Price");
			case 4:
				return Messages.getString("ViewIndentDialog.Header.Requirements");
			}
			return "";
	    }
		
		public void setData(List<IndentDetail> items){
			this.items = items;
		}
		public IndentDetail getObjectAt(int index){
			return items.get(index);
		}
	}
}
