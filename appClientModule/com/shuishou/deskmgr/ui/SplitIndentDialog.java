package com.shuishou.deskmgr.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import com.shuishou.deskmgr.ui.components.NumberInputDialog;
import org.apache.log4j.Logger;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.beans.Dish;
import com.shuishou.deskmgr.beans.Indent;
import com.shuishou.deskmgr.beans.IndentDetail;
import com.shuishou.deskmgr.ui.components.IconButton;

/**
 * 把一个账单分割成多个子账单支付
 * 原则: 将Indent.item罗列在表格中, 由用户点选要支付的项; 如果该项数量为多个, 每次只减少一个, 直到减少到0为止;
 *
 * 支付: 付款时, 将移入到列表里面items, 重新生成一个订单(虚拟订单, 未持久化到服务端), 弹出付款界面, 收集付款信息,
 * 然后将此虚拟订单连同付款信息一同发给服务端做持久化.
 *
 * 固定金额分帐: 用于将部分账单单独付费, 其他部分使用另一种支付方式付费; 使用此功能类似于分帐功能, 但是要用户输入计划分帐
 * 的金额(此金额只能小于账单整体金额), 然后要用户必须选取至少一个菜作为IndentDetail信息(为避免空指针异常), 然后再生成一个
 * 虚拟订单, 弹出付款的界面.
 * 
 * 预防用户点击关闭操作, 所以该界面的上下移动操作, 不可以直接改变indent数据, 而是应该使用copy的数据. 当做完一个操作, 
 * 完成了数据库的持久化以后, 从服务端同步indent的最新结果
 * @author Administrator
 *
 */
public class SplitIndentDialog extends JDialog {
	private final Logger logger = Logger.getLogger(SplitIndentDialog.class.getName());
	private MainFrame mainFrame;
	private Desk desk;
	private Indent indent;
	
	private IconButton btnMoveDown = new IconButton(Messages.getString("SplitIndentDialog.MoveDown"), "/resource/arrowdown.png");
	private IconButton btnMoveUp = new IconButton(Messages.getString("SplitIndentDialog.MoveUp"), "/resource/arrowup.png");
	private IconButton btnPay = new IconButton(Messages.getString("SplitIndentDialog.Pay"), "/resource/checkout.png");
	private IconButton btnPayRest = new IconButton(Messages.getString("SplitIndentDialog.PayRest"), "/resource/checkout.png");
    private IconButton btnPayFixPrice = new IconButton(Messages.getString("SplitIndentDialog.PayFixPrice"), "/resource/checkout.png");
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JLabel lbPrice = new JLabel();
	private JTable tableIndentUp = new JTable();
	private IndentDetailModel tableModelIndentUp = null;
	private JTable tableIndentDown = new JTable();
	private IndentDetailModel tableModelIndentDown = null;
//	private ArrayList<IndentDetail> splitDetailList = new ArrayList<>();
	
	public SplitIndentDialog(MainFrame mainFrame, String title, Desk desk, Indent indent){
		super(mainFrame, title, true);
		this.mainFrame = mainFrame;
		this.desk = desk;
		this.indent = indent;
		initUI();
	}
	
	private void initUI(){
		JLabel lbDeskNo = new JLabel(Messages.getString("ViewIndentDialog.TableNo") + desk.getName());
		lbDeskNo.setFont(ConstantValue.FONT_15BOLD);
		
		lbPrice.setFont(ConstantValue.FONT_15BOLD);
		lbPrice.setText(Messages.getString("ViewIndentDialog.Price")+" $" + indent.getFormatTotalPrice());
		btnMoveDown.setPreferredSize(new Dimension(100, 50));
		btnMoveUp.setPreferredSize(new Dimension(100,50));
		btnClose.setPreferredSize(new Dimension(100, 50));
		btnPay.setPreferredSize(new Dimension(100, 50));
		btnPayRest.setPreferredSize(new Dimension(100,50));
		
		//copy indent, cannot use indent directly, otherwise click "close" will lose data
		Indent copiedIndent = indent.copy();
		tableModelIndentUp = new IndentDetailModel(copiedIndent.getItems());
		tableIndentUp.setModel(tableModelIndentUp);
		tableIndentUp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableIndentUp.setRowHeight(30);
		tableIndentUp.getColumnModel().getColumn(0).setPreferredWidth(120);
		tableIndentUp.getColumnModel().getColumn(1).setPreferredWidth(120);
		tableIndentUp.getColumnModel().getColumn(2).setPreferredWidth(30);
		tableIndentUp.getColumnModel().getColumn(3).setPreferredWidth(50);
		tableIndentUp.getColumnModel().getColumn(4).setPreferredWidth(50);
		tableIndentUp.getColumnModel().getColumn(5).setPreferredWidth(400);
		JScrollPane jspTableUp = new JScrollPane(tableIndentUp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		tableModelIndentDown = new IndentDetailModel(new ArrayList<IndentDetail>());
		tableIndentDown.setModel(tableModelIndentDown);
		tableIndentDown.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableIndentDown.setRowHeight(30);
		tableIndentDown.getColumnModel().getColumn(0).setPreferredWidth(120);
		tableIndentDown.getColumnModel().getColumn(1).setPreferredWidth(120);
		tableIndentDown.getColumnModel().getColumn(2).setPreferredWidth(30);
		tableIndentDown.getColumnModel().getColumn(3).setPreferredWidth(50);
		tableIndentDown.getColumnModel().getColumn(4).setPreferredWidth(50);
		tableIndentDown.getColumnModel().getColumn(5).setPreferredWidth(400);
		JScrollPane jspTableDown = new JScrollPane(tableIndentDown, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JPanel pInfo = new JPanel(new GridBagLayout());
		pInfo.add(lbDeskNo, 		new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pInfo.add(lbPrice, 		new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 100, 0, 0), 0, 0));
		
		JPanel pFunction = new JPanel(new GridBagLayout());
		pFunction.add(btnMoveDown, 		new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pFunction.add(btnMoveUp,		new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 30, 0, 0), 0, 0));
		pFunction.add(btnPay,			new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 30, 0, 0), 0, 0));
		pFunction.add(btnPayRest,		new GridBagConstraints(3, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 30, 0, 0), 0, 0));
        pFunction.add(btnPayFixPrice,	new GridBagConstraints(4, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 30, 0, 0), 0, 0));
		pFunction.add(btnClose,			new GridBagConstraints(5, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 30, 0, 0), 0, 0));
		Dimension dPButton = pFunction.getPreferredSize();
		dPButton.height = 60;
		pFunction.setPreferredSize(dPButton);
		
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		c.add(pInfo, 		new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		c.add(jspTableUp, 	new GridBagConstraints(0, 1, 1, 1, 1, 3, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		c.add(pFunction, 	new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
		c.add(jspTableDown,	new GridBagConstraints(0, 3, 1, 1, 1, 2, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				SplitIndentDialog.this.setVisible(false);
			}});
		
		btnMoveUp.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doMoveUp();
			}});
		btnMoveDown.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doMoveDown();
			}});
		btnPay.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doPay();
			}});
		btnPayRest.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doPayRest();
			}});
		btnPayFixPrice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doPayFixPrice();
            }
        });
		this.setSize(new Dimension(ConstantValue.WINDOW_WIDTH, ConstantValue.WINDOW_HEIGHT));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
		
	}
	
	private void doMoveUp(){
		int row = tableIndentDown.getSelectedRow();
		if (row < 0)
			return;
		IndentDetail downDetail = tableModelIndentDown.getObjectAt(row);
		IndentDetail upDetail = downDetail.copy();
		upDetail.setAmount(0);
		tableModelIndentUp.items.add(upDetail);
		
		upDetail.setAmount(upDetail.getAmount() + 1);
		downDetail.setAmount(downDetail.getAmount() - 1);
		if (downDetail.getAmount() <= 0){
			tableModelIndentDown.items.remove(downDetail);
		}
		tableModelIndentUp.fireTableDataChanged();
		tableModelIndentDown.fireTableDataChanged();
		if (downDetail.getAmount() > 0){
			tableIndentDown.setRowSelectionInterval(row, row);
		}
	}
	
	/**
	 * move the choosed item into down table, no matter what type dish, just new one record in the down table
	 *  
	 */
	private void doMoveDown(){
		int row = tableIndentUp.getSelectedRow();
		if (row < 0)
			return;
		IndentDetail upDetail = tableModelIndentUp.getObjectAt(row);
		//check if the detail exist in Down list
		IndentDetail downDetail = upDetail.copy();

		// clear non-useful properties
		downDetail.setIndent(null);
		downDetail.setAmount(0);
		tableModelIndentDown.items.add(downDetail);

		downDetail.setAmount(downDetail.getAmount()+1);
		upDetail.setAmount(upDetail.getAmount() - 1);
		if (upDetail.getAmount() <= 0){
			tableModelIndentUp.items.remove(upDetail);
		}
		tableModelIndentUp.fireTableDataChanged();
		tableModelIndentDown.fireTableDataChanged();
		if (upDetail.getAmount() > 0){
			tableIndentUp.setRowSelectionInterval(row, row);
		}
	}

    /**
     * 固定金额付款, 用于一个账单, 使用多种支付方式, 分割出来一个固定金额的订单.
     * 1. 检查列表中是否选择了一定数目的菜, 如果没有选择, 给出提示:至少选择一道菜; 如果列表中选择的菜包含了订单全部的菜, 即上方列表已经为空, 给予错误提示: 不能把所有菜都选择进新订单.
     * 2. 弹出数字输入框, 由用户输入一个固定金额, 要求该金额大于零并小于原订单金额. 如果输入值超出范围, 给予错误提示.
     * 3. 根据用户输入的金额, 选择的菜, 生成一个临时订单. 订单的paid_Price设定为输入金额
     */
	private void doPayFixPrice(){
        if (tableModelIndentDown.items == null || tableModelIndentDown.items.isEmpty()){
            JOptionPane.showMessageDialog(mainFrame, Messages.getString("SplitIndentDialog.MustChooseOneDish"));
            return;
        }
        if (tableModelIndentUp.items == null || tableModelIndentUp.items.isEmpty()){
            JOptionPane.showMessageDialog(mainFrame, Messages.getString("SplitIndentDialog.ChoseAllDishes"));
            return;
        }
        NumberInputDialog numdlg = new NumberInputDialog(this, "Input", "Input a price", true);
        numdlg.setVisible(true);
        if (!numdlg.isConfirm)
            return;
        if (numdlg.inputDouble <= 0 || numdlg.inputDouble >= indent.getTotalPrice()){
            JOptionPane.showMessageDialog(mainFrame, Messages.getString("SplitIndentDialog.PriceNotAvailable") + indent.getFormatTotalPrice());
            return;
        }
        //build a indent object
        Indent tempIndent = new Indent();
        tempIndent.setItems(tableModelIndentDown.items);
        tempIndent.setTotalPrice(numdlg.inputDouble);
        CheckoutSplitFixPriceIndentDialog dlg = new CheckoutSplitFixPriceIndentDialog(mainFrame, Messages.getString("MainFrame.CheckoutTitle"), true, desk, tempIndent, this.indent.getId());
        dlg.setVisible(true);
        if (dlg.isCancel)
            return;
        //clear down table
        tableModelIndentDown.items.clear();
        tableModelIndentDown.fireTableDataChanged();
        //get the return data(the left without paid for the indent)
        indent = dlg.getIndentAfterSplit();
        if (indent == null){
            tableModelIndentUp.items.clear();
            tableModelIndentUp.fireTableDataChanged();
            lbPrice.setText("0");
        } else {
            tableModelIndentUp.items = indent.copy().getItems();
            tableModelIndentUp.fireTableDataChanged();
            lbPrice.setText("$"+this.indent.getTotalPrice());
        }
        //refresh mainframe deskcell
        for(DeskCell dc : mainFrame.getDeskcellList()){
            if (dc.getDesk().getName().equals(desk.getName())){
                dc.setIndent(this.indent);
                if (indent != null && indent.getStatus() == ConstantValue.INDENT_STATUS_PAID){
                    dc.setIndent(null);
                }
            }
            //if the indent status changed to paid, then clear the merge data
            if (indent != null && indent.getStatus() == ConstantValue.INDENT_STATUS_PAID){
                if (desk.getName().equals(dc.getDesk().getMergeTo())){
                    dc.getDesk().setMergeTo(null);
                    dc.setMergeTo(null);
                }
            }
        }
    }
	/**
	 * 把订单的剩余项全部支付
	 */
	private void doPayRest(){
		this.setVisible(false);
		CheckoutDialog dlg = new CheckoutDialog(mainFrame, Messages.getString("MainFrame.CheckoutTitle"), true, desk, indent); //$NON-NLS-1$
		dlg.setVisible(true);
	}
	/**
	 * 将列表中的IndentDetail发送给服务端, 服务端创建一个新的Indent, 包含这些IndentDetail. 原来Indent删除掉这几个IndentDetail.
	 * 操作成功后, 将原来Indent的数据返回, 更新客户端的Indent对象. 
	 * 客户端进行检查, 如果发现操作员把原订单下所有的IndentDetail都选择进来, 意味着操作员想付款订单的整个剩余部分, 此时直接调用订单的付款接口即可
	 */
	private void doPay(){
		if (tableModelIndentDown.items == null || tableModelIndentDown.items.isEmpty()){
			return;
		}
		//build a indent object
		Indent tempIndent = new Indent();
		tempIndent.setItems(tableModelIndentDown.items);
		double price = 0;
		for(IndentDetail d : tempIndent.getItems()){
			Dish dish = getDishById(d.getDishId());
			if (dish == null){
				JOptionPane.showMessageDialog(mainFrame, Messages.getString("SplitIndentDialog.NoFindDishById") + d.getDishId());
				return;
			}
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT)
				price += d.getAmount() * d.getDishPrice();
			else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
				price += d.getAmount() * d.getWeight() * d.getDishPrice();
			
		}
		tempIndent.setTotalPrice(price);
//		Desk desk = new Desk();
//		desk.setName(indent.getDeskName());
		CheckoutSplitIndentDialog dlg = new CheckoutSplitIndentDialog(mainFrame, Messages.getString("MainFrame.CheckoutTitle"), true, desk, tempIndent, this.indent.getId());
		dlg.setVisible(true);
		if (dlg.isCancel)
			return;
		//clear down table
		tableModelIndentDown.items.clear();
		tableModelIndentDown.fireTableDataChanged();
		//get the return data(the left without paid for the indent)
		indent = dlg.getIndentAfterSplit();
		if (indent == null){
			tableModelIndentUp.items.clear();
			tableModelIndentUp.fireTableDataChanged();
			lbPrice.setText("0");
		} else {
			tableModelIndentUp.items = indent.copy().getItems();
			tableModelIndentUp.fireTableDataChanged();
			lbPrice.setText("$"+this.indent.getTotalPrice());
		}
		//refresh mainframe deskcell
		for(DeskCell dc : mainFrame.getDeskcellList()){
			if (dc.getDesk().getName().equals(desk.getName())){
				dc.setIndent(this.indent);
				if (indent != null && indent.getStatus() == ConstantValue.INDENT_STATUS_PAID){
					dc.setIndent(null);
				}
			}
			//if the indent status changed to paid, then clear the merge data
			if (indent != null && indent.getStatus() == ConstantValue.INDENT_STATUS_PAID){
				if (desk.getName().equals(dc.getDesk().getMergeTo())){
					dc.getDesk().setMergeTo(null);
					dc.setMergeTo(null);
				}
			}
		}
	}
	
	private Dish getDishById(int dishId){
		for (Dish d : mainFrame.getAllDishes()){
			if (d.getId() == dishId)
				return d;
		}
		return null;
	}
	
	class IndentDetailModel extends AbstractTableModel{
		private List<IndentDetail> items;
		private String[] header = new String[]{
				Messages.getString("ViewIndentDialog.Header.FirstLanguageName"),
				Messages.getString("ViewIndentDialog.Header.SecondLanguageName"),
				Messages.getString("ViewIndentDialog.Header.Amount"),
				Messages.getString("ViewIndentDialog.Header.Price"),
				Messages.getString("ViewIndentDialog.Header.Weight"),
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
			case 1:
				return d.getDishSecondLanguageName();
			case 2:
				return d.getAmount();
			case 3:
				return d.getDishPrice();
			case 4:
				if (d.getWeight() > 0)
					return d.getWeight()+"";
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
}
