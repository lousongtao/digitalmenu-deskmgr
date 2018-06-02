package com.shuishou.deskmgr.ui.dishconfig;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Dish;
import com.shuishou.deskmgr.beans.DishConfig;
import com.shuishou.deskmgr.beans.DishConfigGroup;
import com.shuishou.deskmgr.ui.components.JBlockedButton;

public class DishConfigDialog extends JDialog {
	private final Logger logger = Logger.getLogger(DishConfigDialog.class.getName());
	private Dish dish;
	private Dialog parent;
	private JLabel lbInfo = new JLabel();
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JBlockedButton btnConfirm = new JBlockedButton(Messages.getString("ConfirmDialog"), null);
	private ArrayList<DishConfigGroupIFC> groupPanelList = new ArrayList<>();
	public ArrayList<DishConfig> choosed = new ArrayList<>();
	public boolean isCancel = false;//记录客户是否点击了取消按钮
	public DishConfigDialog(Dialog parent, String title, Dish dish){
		super(parent, title, true);
		this.parent = parent;
		this.dish = dish;
		initUI();
	}
	
	private void initUI(){
		lbInfo.setFont(ConstantValue.FONT_25BOLD);
		btnClose.setPreferredSize(new Dimension(150, 50));
		btnConfirm.setPreferredSize(new Dimension(150, 50));
		ArrayList<DishConfigGroup> groups = dish.getConfigGroups();
		Collections.sort(groups, new Comparator<DishConfigGroup>(){

			@Override
			public int compare(DishConfigGroup o1, DishConfigGroup o2) {
				return o1.getSequence() - o2.getSequence();
			}});
		JPanel pGroup = new JPanel(new GridBagLayout());
		/**
		 * 弹出一个对话框, 要求用户选择某个或者多个配置, 将选择结果作为requirement记录到indentdetail里面, 然后将dish加入choose列表
		 *      配置项根据情况不同, 有不同的选择方式. 针对一个dish, 配置项可能有多个不同的类属.
		 *      2.1 要求选择数量为1个, 不可以是0个或多个 : 
		 *      	此时使用RadioButton做为控件, 默认选中第一个选项
		 *      2.2 要求选择数量为多个(大于等于2), 且不可以重复
		 *      	此时使用CheckBox做为控件, 结束时要检查是否数量相同
		 *      2.3要求选择数量为任意个, 0-n个, 且不可以重复
		 *      	此时使用CheckBox做为控件, 
		 *      2.4要求选择数量为n个(n>1), 且允许重复
		 *      	此时使用一个list控件, 在结束时, 检查选择数量是否正确
		 *      2.5要求选择数量为0个, 即可选择数量可以为0-n任意个
		 *      	此时使用一个list, 结束时不用检查数量
		 */
		for (int i = 0; i < groups.size(); i++) {
			DishConfigGroup group = groups.get(i);
			if (group.getRequiredQuantity() == 1){
				ChooseOnlyOneConfigPanel p = new ChooseOnlyOneConfigPanel(this,group);
				pGroup.add(p, new GridBagConstraints(0, i, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				groupPanelList.add(p);
			} else {
				if (group.isAllowDuplicate()){
					ChooseDuplicatableConfigPanel p = new ChooseDuplicatableConfigPanel(this, group);
					pGroup.add(p, new GridBagConstraints(0, i, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					groupPanelList.add(p);
				} else {
					ChooseNonDuplicatableConfigPanel p = new ChooseNonDuplicatableConfigPanel(this, group);
					pGroup.add(p, new GridBagConstraints(0, i, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					groupPanelList.add(p);
				}
			}
		}
		JScrollPane jsp = new JScrollPane(pGroup, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JPanel pButton = new JPanel(new FlowLayout());
		pButton.add(btnConfirm);
		pButton.add(btnClose);
		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(lbInfo, BorderLayout.NORTH);
		c.add(jsp, BorderLayout.CENTER);
		c.add(pButton, BorderLayout.SOUTH);
		this.setSize(parent.getWidth(), parent.getHeight());
		this.setLocation((int)(parent.getWidth() / 2 - this.getWidth() /2 + parent.getLocation().getX()), 
				(int)(parent.getHeight() / 2 - this.getHeight() / 2 + parent.getLocation().getY()));
		onChooseChange();
		btnConfirm.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doConfirm();
			}});
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				choosed.clear();
				isCancel = true;
				setVisible(false);
			}});
	}
	
	public boolean isCancel(){
		return isCancel;
	}
	
	private void doConfirm(){
		choosed.clear();
        for (int i = 0; i < groupPanelList.size(); i++) {
            DishConfigGroupIFC cview = groupPanelList.get(i);
            if (!cview.checkData())//check data
                return;
            choosed.addAll(cview.getChoosedData());
        }
		setVisible(false);
	}
	
	public void onChooseChange(){
        double price = 0;
        String msg = "";
        for (int i = 0; i < groupPanelList.size(); i++) {
            DishConfigGroupIFC cview = groupPanelList.get(i);

            ArrayList<DishConfig> choosedConfigs = cview.getChoosedData();
            if (!choosedConfigs.isEmpty()){
                DishConfigGroup group = cview.getDishConfigGroup();
                String groupName = group.getFirstLanguageName();
                if (msg.length() == 0)
                    msg += groupName + "=";
                else
                    msg += "; " + groupName + "=";

                for (int j = 0; j < choosedConfigs.size(); j++){
                    DishConfig config = choosedConfigs.get(j);

                    price += config.getPrice();
                    String configName = config.getFirstLanguageName();
                    if (j > 0)
                        msg += "/";
                    msg += configName;
                }
            }
        }
        String pricepm = "+";
        if (price < 0)
            pricepm = "-";
        lbInfo.setText("Price : " + pricepm +"$"+String.format(ConstantValue.FORMAT_DOUBLE, Math.abs(price)) + ", " + msg);
    }
	
	
}
