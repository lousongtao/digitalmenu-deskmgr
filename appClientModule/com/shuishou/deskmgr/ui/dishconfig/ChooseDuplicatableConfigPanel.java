package com.shuishou.deskmgr.ui.dishconfig;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.DishConfig;
import com.shuishou.deskmgr.beans.DishConfigGroup;

/**
 * 需要选择0个或者一个以上配置项, 且不允许重复的情况
 * @author Administrator
 *
 */
public class ChooseDuplicatableConfigPanel extends JPanel implements DishConfigGroupIFC{
	private DishConfigGroup group;
	private JButton btnRemove = new JButton(Messages.getString("Remove"));
	private JList<DishConfig> listChoosed = new JList<>();
	private DefaultListModel<DishConfig> listModelChoosed = new DefaultListModel<>();
	private DishConfigDialog parent;
	public ChooseDuplicatableConfigPanel(DishConfigDialog parent, DishConfigGroup group){
		this.group = group;
		this.parent = parent;
		initUI();
	}
	
	private void initUI(){
		if (group.getDishConfigs() == null || group.getDishConfigs().isEmpty())
			return;
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		if (!group.getDishConfigs().isEmpty()){
			Collections.sort(group.getDishConfigs(), new Comparator<DishConfig>(){

				@Override
				public int compare(DishConfig o1, DishConfig o2) {
					return o1.getSequence() - o2.getSequence();
				}});
			for (int i = 0; i < group.getDishConfigs().size(); i++) {
				DishConfig config = group.getDishConfigs().get(i);
				
			}
		}
		
		
		JLabel lbReqAmount = new JLabel(Messages.getString("DishSubitemDialog.ReqAmount") + group.getRequiredQuantity());
		lbReqAmount.setFont(ConstantValue.FONT_25BOLD);
		
		listChoosed.setModel(listModelChoosed);
		listChoosed.setCellRenderer(new DishConfigRenderer());
		listChoosed.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listChoosed.setFixedCellHeight(30);
		listChoosed.setMinimumSize(new Dimension(250, 250));
		setPreferredSize(new Dimension(150, 250));
		setMaximumSize(new Dimension(150, 250));
		
		btnRemove.setPreferredSize(new Dimension(150, 50));
		
		JScrollPane jspChoosed = new JScrollPane(listChoosed, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JPanel pConfigs = new JPanel(new GridLayout(0, 4, 5, 5));
		pConfigs.setBorder(BorderFactory.createTitledBorder("items"));
		pConfigs.setBackground(Color.white);
		for (int i = 0; i < group.getDishConfigs().size(); i++) {
			ConfigButton btn = new ConfigButton(group.getDishConfigs().get(i));
			pConfigs.add(btn);
		}
		
		JPanel pChoosed = new JPanel(new GridBagLayout());
		pChoosed.add(lbReqAmount, 		new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosed.add(jspChoosed, 	new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pChoosed.add(btnRemove,	 		new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		setLayout(new BorderLayout());
		add(pChoosed, BorderLayout.WEST);
		add(pConfigs,BorderLayout.CENTER);
		btnRemove.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (listChoosed.getSelectedIndex() < 0)
					return;
				listModelChoosed.removeElementAt(listChoosed.getSelectedIndex());
				parent.onChooseChange();
			}});
		setBorder(BorderFactory.createTitledBorder(group.getFirstLanguageName()));
	}
	
	@Override
	public boolean checkData() {
		if (group.getRequiredQuantity() == 0){
			return true;
		}
		if (listChoosed.getModel().getSize() != group.getRequiredQuantity()){
			String msg = "The required amount in ["+group.getFirstLanguageName()+"] is "+ group.getRequiredQuantity();
			JOptionPane.showMessageDialog(this, msg);
			return false;
		}
		return true;
	}

	@Override
	public ArrayList<DishConfig> getChoosedData() {
		ArrayList<DishConfig> configs = new ArrayList<>();
		for (int i = 0; i < listChoosed.getModel().getSize(); i++) {
			configs.add(listChoosed.getModel().getElementAt(i));
		}
        return configs;
	}

	@Override
	public DishConfigGroup getDishConfigGroup() {
		return group;
	}

	class ConfigButton extends JButton implements DishConfigIFC{
		private final DishConfig config;
		public ConfigButton(DishConfig c){
			this.config = c;
			String txt = config.getFirstLanguageName();
			if (config.getPrice() != 0){
				txt += "$" + config.getPrice();
			}
			this.setText(txt);
			setFont(ConstantValue.FONT_20BOLD);
			this.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					listModelChoosed.addElement(config);
					parent.onChooseChange();
				}
				
			});
			this.setPreferredSize(new Dimension(200, 50));
		}
		@Override
		public DishConfig getDishConfig() {
			return config;
		}
	}
	
	class DishConfigRenderer extends JPanel implements ListCellRenderer{
		private JLabel lbName = new JLabel();
		public DishConfigRenderer(){
			setLayout(new BorderLayout());
			add(lbName, BorderLayout.CENTER);
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
			DishConfig config = (DishConfig)value;
			String txt = config.getFirstLanguageName();
			if (config.getPrice() != 0){
				txt += "$" + config.getPrice();
			}
			if (txt.length() > 20)
				txt = txt.substring(0, 20) + "...";
			lbName.setText(txt);
			return this;
		}
		
	}

}
