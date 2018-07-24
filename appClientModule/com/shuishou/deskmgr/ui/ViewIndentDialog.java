package com.shuishou.deskmgr.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
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
import com.shuishou.deskmgr.ui.components.IconButton;
import com.shuishou.deskmgr.ui.components.NumberInputDialog;
import com.shuishou.deskmgr.ui.components.NumberTextField;

public class ViewIndentDialog extends JDialog implements ActionListener{
	private final Logger logger = Logger.getLogger(ViewIndentDialog.class.getName());
	private static final int ROWAMOUNTPERPAGE = 8;
	private MainFrame mainFrame;
	private Desk desk;
	private Indent indent;
	
	private JButton btnRemove = new JButton(Messages.getString("ViewIndentDialog.RemoveDish"));
	private JButton btnChangeAmount = new JButton(Messages.getString("ViewIndentDialog.ChangeAmount"));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private IconButton btnNextpage = new IconButton(Messages.getString("ViewIndentDialog.NextPage"), "/resource/arrowdown.png"); //$NON-NLS-1$
	private IconButton btnPrepage = new IconButton(Messages.getString("ViewIndentDialog.PrePage"), "/resource/arrowup.png"); //$NON-NLS-1$
	private IconButton btnCheckout = new IconButton(Messages.getString("ViewIndentDialog.Checkout"), "/resource/checkout.png"); //$NON-NLS-1$
	
	private JLabel lbPrice = new JLabel();
	private JTable tabIndentDetail = new JTable();
	private IndentDetailModel tableModel = null;
	
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
		lbDeskNo.setFont(ConstantValue.FONT_25BOLD);
		
		lbPrice.setFont(ConstantValue.FONT_25BOLD);
		lbPrice.setText(Messages.getString("ViewIndentDialog.Price")+" $" + indent.getFormatTotalPrice());
		btnRemove.setPreferredSize(new Dimension(100, 50));
		btnChangeAmount.setPreferredSize(new Dimension(100,50));
		btnClose.setPreferredSize(new Dimension(100, 50));
		btnNextpage.setPreferredSize(new Dimension(100,50));
		btnPrepage.setPreferredSize(new Dimension(100, 50));
		btnCheckout.setPreferredSize(new Dimension(100, 50));
		
		tableModel = new IndentDetailModel(indent.getItems());
		tabIndentDetail.setModel(tableModel);
		tabIndentDetail.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabIndentDetail.setRowHeight(50);
		tabIndentDetail.getColumnModel().getColumn(0).setPreferredWidth(120);
//		tabIndentDetail.getColumnModel().getColumn(1).setPreferredWidth(120);
		tabIndentDetail.getColumnModel().getColumn(1).setPreferredWidth(30);
		tabIndentDetail.getColumnModel().getColumn(2).setPreferredWidth(50);
		tabIndentDetail.getColumnModel().getColumn(3).setPreferredWidth(50);
		tabIndentDetail.getColumnModel().getColumn(4).setPreferredWidth(140);
		tabIndentDetail.getColumnModel().getColumn(5).setPreferredWidth(400);
		JScrollPane jspTable = new JScrollPane(tabIndentDetail, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JPanel pTop = new JPanel(new GridBagLayout());
		pTop.add(lbDeskNo, 		new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pTop.add(lbPrice, 		new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 100, 0, 0), 0, 0));
		
		JPanel pFunction = new JPanel(new GridBagLayout());
		pFunction.add(btnRemove,	new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pFunction.add(btnChangeAmount,new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 50, 0, 0), 0, 0));
		pFunction.add(btnPrepage,	 new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 50, 0, 0), 0, 0));
		pFunction.add(btnNextpage,	 new GridBagConstraints(3, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 50, 0, 0), 0, 0));
		pFunction.add(btnCheckout,	 new GridBagConstraints(4, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 50, 0, 0), 0, 0));
		pFunction.add(btnClose,		new GridBagConstraints(5, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 50, 0, 0), 0, 0));

		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(pTop, 		new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(jspTable, 	new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pFunction, 	new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		btnClose.addActionListener(this);			
		btnRemove.addActionListener(this);
		btnChangeAmount.addActionListener(this);
		btnPrepage.addActionListener(this);
		btnNextpage.addActionListener(this);
		btnCheckout.addActionListener(this);
		this.setSize(new Dimension(ConstantValue.WINDOW_WIDTH, 600));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
		
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnClose){
			ViewIndentDialog.this.setVisible(false);
		} else if (e.getSource() == btnRemove){
			doRemoveDish();
		} else if (e.getSource() == btnChangeAmount){
			doChangeAmount();
		} else if (e.getSource() == btnPrepage){
			moveToPrepage();
		} else if (e.getSource() == btnNextpage){
			moveToNextpage();
		} else if (e.getSource() == btnCheckout){
			CheckoutDialog dlg = new CheckoutDialog(mainFrame, Messages.getString("MainFrame.CheckoutTitle"), true, desk, indent); //$NON-NLS-1$
			this.setVisible(false);
			dlg.setVisible(true);
		}
	}
	
	private void moveToPrepage(){
		int selectRow = tabIndentDetail.getSelectedRow();
		if (selectRow < 0 || selectRow - ROWAMOUNTPERPAGE < 0){
			//unselected row, move to 1st row
			tabIndentDetail.getSelectionModel().setSelectionInterval(0, 0);
			tabIndentDetail.scrollRectToVisible(new Rectangle(tabIndentDetail.getCellRect(0, 0,true)));
		} else if (selectRow - ROWAMOUNTPERPAGE >= 0){
			tabIndentDetail.getSelectionModel().setSelectionInterval(selectRow - ROWAMOUNTPERPAGE, selectRow - ROWAMOUNTPERPAGE);
			tabIndentDetail.scrollRectToVisible(new Rectangle(tabIndentDetail.getCellRect(selectRow - ROWAMOUNTPERPAGE, 0,true)));
		} 
	}
	
	private void moveToNextpage(){
		int lastrow = tabIndentDetail.getRowCount();
		if (lastrow <= ROWAMOUNTPERPAGE){
			return;
		}
		int row = tabIndentDetail.getSelectedRow();
		if (row + ROWAMOUNTPERPAGE < lastrow){
			tabIndentDetail.getSelectionModel().setSelectionInterval(row + ROWAMOUNTPERPAGE, row + ROWAMOUNTPERPAGE);
			tabIndentDetail.scrollRectToVisible(new Rectangle(tabIndentDetail.getCellRect(row + ROWAMOUNTPERPAGE, 0,true)));
		} else {
			tabIndentDetail.getSelectionModel().setSelectionInterval(lastrow -1 , lastrow - 1);
			tabIndentDetail.scrollRectToVisible(new Rectangle(tabIndentDetail.getCellRect(lastrow-1, 0,true)));
		}
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
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server while delete indent detail. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while delete indent detail. URL = " + url + ", param = "+ params);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss").create();
		HttpResult<Indent> result = gson.fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while delete indent detail. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
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
		int oldAmount = tableModel.getObjectAt(row).getAmount();
		NewAmountInputDialog dlg = new NewAmountInputDialog(this, "input", oldAmount);
		dlg.setVisible(true);
		if (!dlg.isConfirm)
			return;
		int newAmount = dlg.inputInteger;
		String url = "indent/operateindentdetail";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId()+"");
		if (newAmount > 0){
			params.put("operatetype", ConstantValue.INDENTDETAIL_OPERATIONTYPE_CHANGEAMOUNT+"");
			params.put("amount", newAmount+"");
		}else if (newAmount <= 0){
			params.put("operatetype", ConstantValue.INDENTDETAIL_OPERATIONTYPE_DELETE+"");
		}
		params.put("indentDetailId", tableModel.getObjectAt(row).getId()+"");
		
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server while change amount. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while change amount. URL = " + url + ", param = "+ params);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss").create();
		HttpResult<Indent> result = gson.fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while delete indent detail. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		this.indent = result.data;
		mainFrame.loadCurrentIndentInfo();
		refreshData();
	}
	
	class IndentDetailModel extends AbstractTableModel{
		private List<IndentDetail> items;
		private String[] header = new String[]{
				Messages.getString("ViewIndentDialog.Header.FirstLanguageName"),
//				Messages.getString("ViewIndentDialog.Header.SecondLanguageName"),
				Messages.getString("ViewIndentDialog.Header.Amount"),
				Messages.getString("ViewIndentDialog.Header.Price"),
				Messages.getString("ViewIndentDialog.Header.Weight"),
				Messages.getString("ViewIndentDialog.Header.Time"),
				Messages.getString("ViewIndentDialog.Header.Requirements")
		};
		public IndentDetailModel(List<IndentDetail> items){
			this.items = items;
		}
		@Override
		public int getRowCount() {
			return items.size();
		}

		@Override
		public int getColumnCount() {
			return header.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			IndentDetail d = getObjectAt(rowIndex);
			switch(columnIndex){
			case 0:
				return d.getDishFirstLanguageName();
//			case 1:
//				return d.getDishSecondLanguageName();
			case 1:
				return d.getAmount();
			case 2:
				return d.getDishPrice();
			case 3:
				if (d.getWeight() > 0)
					return d.getWeight()+"";
				else return "";
			case 4:
				if (d.getTime() != null)
					return ConstantValue.DFYMDHMS.format(d.getTime());
				else return "";
			case 5:
				return d.getAdditionalRequirements();
			}
			return "";
		}
		
		@Override
		public String getColumnName(int column) {
			return header[column];
	    }
		
		public void setData(List<IndentDetail> items){
			this.items = items;
		}
		public IndentDetail getObjectAt(int index){
			return items.get(index);
		}
	}
	
	class NewAmountInputDialog extends JDialog{
		public int inputInteger;
		public boolean isConfirm = false;
		private NumberTextField txt;
		public NewAmountInputDialog(Dialog parent, String title, int oldAmount){
			super(parent, title,true);
			JButton btnConfirm = new JButton(Messages.getString("ConfirmDialog"));
			JButton btnClose = new JButton(Messages.getString("CloseDialog"));
			IconButton btnPlus = new IconButton("","/resource/plus.png");
			IconButton btnMinus = new IconButton("", "/resource/minus.png");
			JLabel lbOldAmount = new JLabel(Messages.getString("ViewIndentDialog.OldAmount"));
			JLabel lbNewAmount = new JLabel(Messages.getString("ViewIndentDialog.NewAmount"));
			JTextField tfOldAmount = new JTextField();
			btnConfirm.setPreferredSize(new Dimension(150, 50));
			btnClose.setPreferredSize(new Dimension(150, 50));
			btnPlus.setPreferredSize(new Dimension(80, 80));
			btnMinus.setPreferredSize(new Dimension(80,80));
			
			txt = new NumberTextField(this, false);
			tfOldAmount.setText(oldAmount + "");
			txt.setText(oldAmount + "");
			tfOldAmount.setEditable(false);
			Container c = this.getContentPane();
			c.setLayout(new GridBagLayout());
			c.add(lbOldAmount, 			new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
			c.add(tfOldAmount, 			new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
			c.add(btnPlus, 				new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 30, 0, 0), 0, 0));
			c.add(lbNewAmount, 			new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
			c.add(txt,		   			new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
			c.add(btnMinus, 			new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 30, 0, 0), 0, 0));
			c.add(btnConfirm, 			new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
			c.add(btnClose, 			new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
			
			btnClose.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			
			btnConfirm.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					if (txt.getText() == null || txt.getText().length() == 0)
						return;
					isConfirm = true;
					inputInteger = Integer.parseInt(txt.getText());
					setVisible(false);
				}
			});
			btnPlus.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					int i = Integer.parseInt(txt.getText());
					txt.setText(String.valueOf(i + 1));
				}});
			btnMinus.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					int i = Integer.parseInt(txt.getText());
					i--;
					if (i < 0)
						i = 0;
					txt.setText(String.valueOf(i));
				}});
			this.setSize(new Dimension(500, 320));
			this.setLocation((int)(parent.getWidth() / 2 - this.getWidth() /2 + parent.getLocation().getX()), 
					(int)(parent.getHeight() / 2 - this.getHeight() / 2 + parent.getLocation().getY()));
		}
	}
}
