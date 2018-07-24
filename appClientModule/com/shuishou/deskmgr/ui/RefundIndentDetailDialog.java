package com.shuishou.deskmgr.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;
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
import com.shuishou.deskmgr.ui.components.WaitDialog;

public class RefundIndentDetailDialog extends JDialog implements ActionListener{
	private final Logger logger = Logger.getLogger(RefundIndentDetailDialog.class.getName());
	private MainFrame mainFrame;
	private Desk desk;
	private Indent indent;
	
	private JButton btnChooseAll = new JButton(Messages.getString("RefundIndentDetailDialog.ChooseAll"));
	private JButton btnRefund = new JButton(Messages.getString("RefundIndentDetailDialog.Refund"));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	
	private JLabel lbInfo = new JLabel();
	private JTable tabIndentDetail = new JTable();
	private IndentDetailModel tableModel = null;
	
	public RefundIndentDetailDialog(MainFrame mainFrame, Desk desk, Indent indent){
		super(mainFrame, "Refund", true);
		this.mainFrame = mainFrame;
		this.desk = desk;
		this.indent = indent;
		initUI();
//		initData();
	}
	
	private void initUI(){
		JLabel lbDeskNo = new JLabel(Messages.getString("ViewIndentDialog.TableNo") + desk.getName());
		lbDeskNo.setFont(ConstantValue.FONT_25BOLD);
		
		lbInfo.setFont(ConstantValue.FONT_25BOLD);
		lbInfo.setText("Table " + desk.getName() + "    Sequence " + indent.getDailySequence() + "    Total Price $" + indent.getFormatTotalPrice());
		btnRefund.setPreferredSize(new Dimension(100, 50));
		btnClose.setPreferredSize(new Dimension(100, 50));
		btnChooseAll.setPreferredSize(new Dimension(100, 50));
		
		tableModel = new IndentDetailModel(indent.getItems());
		tabIndentDetail.setModel(tableModel);
		tabIndentDetail.setDefaultRenderer(Object.class, new IndentDetailTableCellRenderer());
		tabIndentDetail.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabIndentDetail.setRowHeight(50);
		tabIndentDetail.getColumnModel().getColumn(0).setPreferredWidth(20);
		tabIndentDetail.getColumnModel().getColumn(1).setPreferredWidth(120);
		tabIndentDetail.getColumnModel().getColumn(2).setPreferredWidth(30);
		tabIndentDetail.getColumnModel().getColumn(3).setPreferredWidth(50);
		tabIndentDetail.getColumnModel().getColumn(4).setPreferredWidth(50);
		tabIndentDetail.getColumnModel().getColumn(5).setPreferredWidth(140);
		tabIndentDetail.getColumnModel().getColumn(6).setPreferredWidth(400);
		JScrollPane jspTable = new JScrollPane(tabIndentDetail, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JPanel pFunction = new JPanel(new GridBagLayout());
		pFunction.add(btnChooseAll,	new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pFunction.add(btnRefund,	new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 50, 0, 0), 0, 0));
		pFunction.add(btnClose,		new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 50, 0, 0), 0, 0));

		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(lbInfo, 		new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(jspTable, 	new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pFunction, 	new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		tableModel.addTableModelListener(new TableModelListener(){

			@Override
			public void tableChanged(TableModelEvent e) {
				refreshLabelText();
			}});
		tabIndentDetail.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				JTable source = (JTable)e.getSource();
                int row = source.rowAtPoint( e.getPoint() );
                RowDetail rd = tableModel.getObjectAt(row);
                rd.setChoosed(!rd.isChoosed());
                tableModel.fireTableDataChanged();
                refreshLabelText();
			}
		});
		btnClose.addActionListener(this);	
		btnChooseAll.addActionListener(this);
		btnRefund.addActionListener(this);
		
		
		this.setSize(new Dimension(ConstantValue.WINDOW_WIDTH, 600));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
		
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnClose){
			RefundIndentDetailDialog.this.setVisible(false);
		} else if (e.getSource() == btnRefund){
			doRefund();
		} else if (e.getSource() == btnChooseAll){
			doChooseAll();
		}
	}
	
	private void doChooseAll(){
		if (tableModel.getData() != null){
			for (int i = 0; i < tableModel.getData().size(); i++) {
				tableModel.getData().get(i).setChoosed(true);
			}
			tableModel.fireTableDataChanged();
		}
	}
	
	private void refreshLabelText(){
		double choosedPrice = 0;
		for (int i = 0; i < tableModel.getData().size(); i++) {
			if (tableModel.getData().get(i).isChoosed()){
				IndentDetail d = tableModel.getData().get(i).getIndentDetail();
				Dish dish = mainFrame.getDishById(d.getDishId());
				if (dish == null){
					JOptionPane.showMessageDialog(this, "Error: cannot find dish by id" + d.getDishId() + ", please restart this application.");
					return;
				}
				if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT)
					choosedPrice += dish.getPrice() * d.getAmount();
				else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
					choosedPrice += dish.getPrice() * d.getWeight();
			}
		}
		if (choosedPrice > 0)
			lbInfo.setText("Table " + desk.getName() + "    Sequence " + indent.getDailySequence() + "    Total Price $" + indent.getFormatTotalPrice() + "    Choosed $" + String.format(ConstantValue.FORMAT_DOUBLE, choosedPrice));
	}
	
	private void refreshData(){
		tableModel.setData(indent.getItems());
		tableModel.fireTableDataChanged();
		lbInfo.setText("Table " + desk.getName() + "    Sequence " + indent.getDailySequence() + "    Total Price $" + indent.getFormatTotalPrice());
	}
	
	/**
	 * 如果是全部选中, 就调用退单接口;
	 * 如果是部分选中, 就调用删除菜的接口
	 */
	private void doRefund(){
		if (tableModel.getData() == null || tableModel.getData().isEmpty())
			return;
		
		ArrayList<Integer> indentDetailIdList = new ArrayList<>();
		boolean existUnchecked = false;
		for (int i = 0; i < tableModel.getData().size(); i++) {
			if (tableModel.getData().get(i).isChoosed()){
				indentDetailIdList.add(tableModel.getData().get(i).getIndentDetail().getId());
			} else {
				existUnchecked = true;
			}
		}
		if (indentDetailIdList.isEmpty())
			return;
		if (JOptionPane.showConfirmDialog(this, Messages.getString("RefundIndentDetailDialog.RefundChoosed"), "", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
			return;
		if (!existUnchecked){
			doRefundIndent();
			setVisible(false);
		} else {
			doRemoveDish(indentDetailIdList);
		}
		
	}
	
	private void doRefundIndent(){
		String url = "indent/dorefundindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId() + "");
		params.put("id", indent.getId() + "");
		
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		JSONObject jsonObj = new JSONObject(response);
		if (!jsonObj.getBoolean("success")){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("Do refund failed. URL = " + url + ", param = "+ params);
			if (jsonObj.has("result"))
				JOptionPane.showMessageDialog(mainFrame, jsonObj.getString("result")); //$NON-NLS-1$
		} else {
			JOptionPane.showMessageDialog(mainFrame, "Order refund successfully"); //$NON-NLS-1$
		}
	}
	
	private void doRemoveDish(final ArrayList<Integer> indentDetailIdList){
		final String url = "indent/operateindentdetail";
		final Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss").create();
		final Map<String, String> params = new HashMap<String, String>();
		params.put("userId", mainFrame.getOnDutyUser().getId()+"");
		params.put("operatetype", ConstantValue.INDENTDETAIL_OPERATIONTYPE_REFUND+"");
		WaitDialog wd = new WaitDialog(this, "refund the choosed dish..."){
			public Object work(){
				for (int i = 0; i < indentDetailIdList.size(); i++) {
					params.put("indentDetailId", indentDetailIdList.get(i) + "");
					String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
					if (response == null || response.length() == 0){
						logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
						logger.error("get null from server while delete indent detail. URL = " + url + ", param = "+ params);
						JOptionPane.showMessageDialog(this, "get null from server while delete indent detail. URL = " + url + ", param = "+ params);
						return null;
					}
					
					HttpResult<Indent> result = gson.fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
					if (!result.success){
						logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
						logger.error("return false while delete indent detail. URL = " + url + ", response = "+response);
						JOptionPane.showMessageDialog(this, result.result);
						return null;
					}
					indent = result.data;
				}
				refreshData();
				return null;
			}
		};
	}
	
	class IndentDetailTableCellRenderer extends DefaultTableCellRenderer{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	    {
	        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        if (isSelected){
	        	super.setForeground(table.getSelectionForeground());
	        	super.setBackground(table.getSelectionBackground());
	        } else {
		        c.setForeground(Color.black);
		        RowDetail rd = tableModel.getObjectAt(row);
		        if (rd.isChoosed())
		        	c.setBackground(Color.lightGray);
		        else 
		        	c.setBackground(Color.white);
	        }
	        return c;
	    }
	}
	
	class RowDetail{
		private IndentDetail detail;
		private boolean choosed = false;
		public RowDetail(IndentDetail detail){
			this.detail = detail;
		}
		public IndentDetail getIndentDetail() {
			return detail;
		}
		public void setIndentDetail(IndentDetail detail) {
			this.detail = detail;
		}
		public boolean isChoosed() {
			return choosed;
		}
		public void setChoosed(boolean choosed) {
			this.choosed = choosed;
		}
		
		
	}
	
	class IndentDetailModel extends AbstractTableModel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private List<RowDetail> items;
		private String[] header = new String[]{
				"",
				Messages.getString("ViewIndentDialog.Header.FirstLanguageName"),
				Messages.getString("ViewIndentDialog.Header.Amount"),
				Messages.getString("ViewIndentDialog.Header.Price"),
				Messages.getString("ViewIndentDialog.Header.Weight"),
				Messages.getString("ViewIndentDialog.Header.Time"),
				Messages.getString("ViewIndentDialog.Header.Requirements")
		};
//		public IndentDetailModel(List<RowDetail> items){
//			this.items = items;
//		}
		
		public IndentDetailModel(List<IndentDetail> items){
			this.items = new ArrayList<RowDetail>(items.size());
			for (int i = 0; i < items.size(); i++) {
				RowDetail rd = new RowDetail(items.get(i));
				this.items.add(rd);
			}
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
			RowDetail rd = getObjectAt(rowIndex);
			IndentDetail d = rd.getIndentDetail();
			switch(columnIndex){
			case 0:
				return rd.isChoosed();
			case 1:
				return d.getDishFirstLanguageName();
			case 2:
				return d.getAmount();
			case 3:
				return d.getDishPrice();
			case 4:
				if (d.getWeight() > 0)
					return d.getWeight()+"";
				else return "";
			case 5:
				if (d.getTime() != null)
					return ConstantValue.DFYMDHMS.format(d.getTime());
				else return "";
			case 6:
				return d.getAdditionalRequirements();
			}
			return "";
		}
		
		@Override
		public String getColumnName(int column) {
			return header[column];
	    }
		
		public void setData(List<IndentDetail> items){
			this.items = new ArrayList<RowDetail>(items.size());
			for (int i = 0; i < items.size(); i++) {
				RowDetail rd = new RowDetail(items.get(i));
				this.items.add(rd);
			}
		}
		public RowDetail getObjectAt(int index){
			return items.get(index);
		}
		
		public List<RowDetail> getData(){
			return items;
		}
		
		@Override
	     public Class getColumnClass(int column) {
	         switch (column) {
	             case 0:
	                 return Boolean.class;
	             case 1:
	                 return String.class;
	             case 2:
	                 return Double.class;
	             case 3:
	                 return Double.class;
	             case 4:
	            	 return double.class;
	             case 5:
	            	 return String.class;
	             default:
	                 return String.class;
	         }
	     }
		
		@Override
		public boolean isCellEditable(int row, int column) {
	        if (column == 0)
	        	return true;
	        else return false;
	    }
	}
	
	
}
