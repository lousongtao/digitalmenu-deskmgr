package com.shuishou.deskmgr.ui.dishconfig;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.beans.DishConfig;
import com.shuishou.deskmgr.beans.DishConfigGroup;

/**
 * 需要选择0个或者一个以上配置项, 且不允许重复的情况
 * @author Administrator
 *
 */
public class ChooseAnyConfigPanel extends JPanel implements DishConfigGroupIFC{
	private ArrayList<ConfigCheckBox> components = new ArrayList<>();
	private DishConfigGroup group;
	private DishConfigDialog parent;
	public ChooseAnyConfigPanel(DishConfigDialog parent, DishConfigGroup group){
		this.group = group;
		this.parent = parent;
		initUI();
	}
	
	private void initUI(){
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		if (!group.getDishConfigs().isEmpty()){
			for (int i = 0; i < group.getDishConfigs().size(); i++) {
				DishConfig config = group.getDishConfigs().get(i);
				
				ConfigCheckBox cb = new ConfigCheckBox(config);
				this.add(cb);
				components.add(cb);
			}
		}
		setBorder(BorderFactory.createTitledBorder(group.getFirstLanguageName()));
	}
	
	@Override
	public boolean checkData() {
		return true;
	}

	@Override
	public ArrayList<DishConfig> getChoosedData() {
		ArrayList<DishConfig> configs = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).isSelected()){
                configs.add(components.get(i).config);
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
