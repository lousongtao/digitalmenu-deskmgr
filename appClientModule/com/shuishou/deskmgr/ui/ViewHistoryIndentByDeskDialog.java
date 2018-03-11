package com.shuishou.deskmgr.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Dish;
import com.shuishou.deskmgr.beans.Indent;
import com.shuishou.deskmgr.beans.IndentDetail;
import com.shuishou.deskmgr.printertool.PrintJob;
import com.shuishou.deskmgr.printertool.PrintQueue;

public class ViewHistoryIndentByDeskDialog extends JDialog {
	private final Logger logger = Logger.getLogger(ViewHistoryIndentByDeskDialog.class.getName());
	private MainFrame mainFrame;
	private ArrayList<Indent> indents;
	
	private JButton btnPrint = new JButton(Messages.getString("ViewHistoryIndentByDeskDialog.Print"));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JTable table = new JTable();
	private IndentModel tableModel = null;
	
	public ViewHistoryIndentByDeskDialog(MainFrame mainFrame,String title, boolean modal, ArrayList<Indent> indents){
		super(mainFrame, title, modal);
		this.mainFrame = mainFrame;
		this.indents = indents;
		initUI();
//		initData();
	}
	
	private void initUI(){
		btnPrint.setPreferredSize(new Dimension(100, 50));
		btnClose.setPreferredSize(new Dimension(100, 50));
		
		tableModel = new IndentModel();
		table.setModel(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(50);
		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.getColumnModel().getColumn(3).setPreferredWidth(150);
		table.getColumnModel().getColumn(4).setPreferredWidth(80);
		table.getColumnModel().getColumn(5).setPreferredWidth(80);
		table.getColumnModel().getColumn(6).setPreferredWidth(120);
		JScrollPane jspTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JPanel pFunction = new JPanel(new GridBagLayout());
		pFunction.add(btnPrint, 		new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pFunction.add(btnClose,			new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 50, 0, 0), 0, 0));

		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(jspTable, 	new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pFunction, 	new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				ViewHistoryIndentByDeskDialog.this.setVisible(false);
			}});
		
		btnPrint.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doPrint();
			}});
		this.setSize(new Dimension(800, 600));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
		
	}
	
//	private void doPrint(){
//		int row = table.getSelectedRow();
//		if (row < 0)
//			return;
//		if (JOptionPane.showConfirmDialog(this, Messages.getString("ViewHistoryIndentByDeskDialog.ConfirmPrint"), "", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
//			return;
//		String url = "indent/printindent";
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("userId", mainFrame.getOnDutyUser().getId()+"");
//		params.put("indentId", tableModel.getObjectAt(row).getId()+"");
//		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
//		if (response == null || response.length() == 0){
//			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
//			logger.error("get null from server while print indent. URL = " + url + ", param = "+ params);
//			JOptionPane.showMessageDialog(this, "get null from server while print indent. URL = " + url + ", param = "+ params);
//			return;
//		}
//		HttpResult<Indent> result = new Gson().fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
//		if (!result.success){
//			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
//			logger.error("return false while print indent. URL = " + url + ", response = "+response);
//			JOptionPane.showMessageDialog(this, "return false while print indent. URL = " + url + ", response = "+response);
//			return;
//		}
//	}
	
	private void doPrint(){
		int row = table.getSelectedRow();
		if (row < 0)
			return;
		if (JOptionPane.showConfirmDialog(this, Messages.getString("ViewHistoryIndentByDeskDialog.ConfirmPrint"), "", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
			return;
		Indent indent = tableModel.getObjectAt(row);
		Map<String,String> keys = new HashMap<String, String>();
		keys.put("sequence", indent.getDailySequence()+"");
		keys.put("customerAmount", indent.getCustomerAmount()+"");
		keys.put("tableNo", indent.getDeskName());
		keys.put("printType", "Invoice");
		keys.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
		keys.put("totalPrice", String.format("%.2f", indent.getTotalPrice()));
		keys.put("paidPrice", String.format("%.2f", indent.getPaidPrice()));
		keys.put("gst", String.format("%.2f",(double)(indent.getPaidPrice()/11)));
		keys.put("printTime", ConstantValue.DFYMDHMS.format(new Date()));
		keys.put("payway", indent.getPayWay());
			keys.put("charge", "");
		List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
		for(IndentDetail d : indent.getItems()){
			Dish dish = mainFrame.getDishById(d.getDishId());
			Map<String, String> mg = new HashMap<String, String>();
			mg.put("name", d.getDishFirstLanguageName());
			mg.put("price", String.format("%.2f",d.getDishPrice()));
			mg.put("amount", d.getAmount()+"");
			
			String requirement = "";
			if (d.getAdditionalRequirements() != null)
				requirement += d.getAdditionalRequirements();
			//按重量卖的dish, 把重量加入requirement
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
				requirement += " " + d.getWeight();
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT){
				mg.put("totalPrice", String.format("%.2f",d.getWeight() * d.getDishPrice() * d.getAmount()));
			} else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT){
				mg.put("totalPrice", String.format("%.2f",d.getDishPrice() * d.getAmount()));
			}
			mg.put("requirement", requirement);
			goods.add(mg);
			
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", keys);
		params.put("goods", goods);
		PrintJob job = new PrintJob("/newIndent_template.json", params, mainFrame.printerName);
		PrintQueue.add(job);
	}
	
	class IndentModel extends AbstractTableModel{
		private String[] header = new String[]{
				Messages.getString("ViewHistoryIndentByDeskDialog.Header.Sequence"),
				Messages.getString("ViewHistoryIndentByDeskDialog.Header.Status"),
				Messages.getString("ViewHistoryIndentByDeskDialog.Header.StartTime"),
				Messages.getString("ViewHistoryIndentByDeskDialog.Header.EndTime"),
				Messages.getString("ViewHistoryIndentByDeskDialog.Header.Price"),
				Messages.getString("ViewHistoryIndentByDeskDialog.Header.PaidPrice"),
				Messages.getString("ViewHistoryIndentByDeskDialog.Header.PayWay")
		};
		public IndentModel(){
		}
		@Override
		public int getRowCount() {
			return indents.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Indent i = getObjectAt(rowIndex);
			switch(columnIndex){
			case 0:
				return i.getDailySequence();
			case 1:
				if (i.getStatus() == ConstantValue.INDENT_STATUS_OPEN){
					return "unpaid";
				} else if (i.getStatus() == ConstantValue.INDENT_STATUS_PAID){
					return "paid";
				} else if (i.getStatus() == ConstantValue.INDENT_STATUS_CANCELED){
					return "canceled";
				} else if (i.getStatus() == ConstantValue.INDENT_STATUS_FORCEEND){
					return "Force Clean";
				} else 
					return i.getStatus();
			case 2:
				return ConstantValue.DFYMDHMS.format(i.getStartTime());
			case 3:
				if (i.getEndTime() == null)
					return "";
				return ConstantValue.DFYMDHMS.format(i.getEndTime());
			case 4:
				return i.getTotalPrice();
			case 5:
				return i.getPaidPrice();
			case 6:
				return i.getPayWay();
			}
			return "";
		}
		
		@Override
		public String getColumnName(int column) {
			return header[column];
	    }
		
		public Indent getObjectAt(int index){
			return indents.get(index);
		}
	}
}
