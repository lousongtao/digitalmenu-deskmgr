package com.shuishou.deskmgr.ui.dishconfig;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.beans.DishConfig;
import com.shuishou.deskmgr.beans.DishConfigGroup;
import com.shuishou.deskmgr.ui.MainFrame;

/**
 * 需要选择0个或者多个配置项, 不允许选择一个, 不允许重复
 * @author Administrator
 *
 */
public class ChooseNonDuplicatableConfigPanel extends JPanel implements DishConfigGroupIFC{
	private ArrayList<DishConfigCheckBox> components = new ArrayList<>();
	private DishConfigGroup group;
	private DishConfigDialog parent;
	public ChooseNonDuplicatableConfigPanel(DishConfigDialog parent, DishConfigGroup group){
		this.group = group;
		this.parent = parent;
		initUI();
	}
	
	private void initUI(){
		if (group.getDishConfigs() == null || group.getDishConfigs().isEmpty())
			return;
		this.setLayout(new GridBagLayout());
		if (!group.getDishConfigs().isEmpty()){
			Collections.sort(group.getDishConfigs(), new Comparator<DishConfig>(){

				@Override
				public int compare(DishConfig o1, DishConfig o2) {
					return o1.getSequence() - o2.getSequence();
				}});
			for (int i = 0; i < group.getDishConfigs().size(); i++) {
				DishConfig config = group.getDishConfigs().get(i);
				
				DishConfigCheckBox cb = new DishConfigCheckBox(parent, config);
				this.add(cb, new GridBagConstraints(i % ConstantValue.dishConfig_Column, (int)i/ConstantValue.dishConfig_Column, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
				components.add(cb);
			}
		}
		setBorder(BorderFactory.createTitledBorder(group.getFirstLanguageName()));
	}
	
	@Override
	public boolean checkData() {
		if (group.getRequiredQuantity() == 0)
			return true;
		else {
			return getChoosedData().size() == group.getRequiredQuantity();
		}
	}

	@Override
	public ArrayList<DishConfig> getChoosedData() {
		ArrayList<DishConfig> configs = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).isSelected()){
                configs.add(components.get(i).getDishConfig());
            }
        }
        return configs;
	}

	@Override
	public DishConfigGroup getDishConfigGroup() {
		return group;
	}

	class ConfigCheckBox extends JCheckBox implements DishConfigIFC{
		public DishConfig config;
		public ConfigCheckBox(DishConfig config){
			super();
			String txt = config.getFirstLanguageName();
			if (config.getPrice() != 0){
				txt += "$" + config.getPrice();
			}
			this.config = config;
			setText(txt);
			setFont(ConstantValue.FONT_20BOLD);
			addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					parent.onChooseChange();
				}});
		}
		@Override
		public DishConfig getDishConfig() {
			return config;
		}
		
		
	}

}
