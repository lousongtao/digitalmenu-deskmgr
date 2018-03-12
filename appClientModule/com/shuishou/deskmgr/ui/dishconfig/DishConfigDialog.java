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
	private JPanel pSubitems = new JPanel(new GridLayout(0, 3, 5, 5));
	private ArrayList<DishConfigGroupIFC> groupPanelList = new ArrayList<>();
	public ArrayList<DishConfig> choosed = new ArrayList<>();
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
		for (int i = 0; i < groups.size(); i++) {
			DishConfigGroup group = groups.get(i);
			if (group.getRequiredQuantity() == 1){
				ChooseOneConfigPanel p = new ChooseOneConfigPanel(this,group);
				pGroup.add(p, new GridBagConstraints(0, i, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				groupPanelList.add(p);
			} else if (group.getRequiredQuantity() > 1){
				ChooseMoreConfigPanel p = new ChooseMoreConfigPanel(this, group);
				pGroup.add(p, new GridBagConstraints(0, i, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				groupPanelList.add(p);
			} else {
				ChooseAnyConfigPanel p = new ChooseAnyConfigPanel(this, group);
				pGroup.add(p, new GridBagConstraints(0, i, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				groupPanelList.add(p);
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
				setVisible(false);
			}});
	}
	
	private void doConfirm(){
		choosed.clear();
        for (int i = 0; i < groupPanelList.size(); i++) {
            DishConfigGroupIFC cview = groupPanelList.get(i);
            if (!cview.checkData())
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
        lbInfo.setText("Price : " + pricepm +"$"+Math.abs(price) + ", " + msg);
    }
	
	
}
