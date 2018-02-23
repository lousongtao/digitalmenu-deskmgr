package com.shuishou.deskmgr.ui.dishconfig;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.beans.DishConfig;
import com.shuishou.deskmgr.beans.DishConfigGroup;

/**
 * 必须选择一个配置项, 使用RadioButton
 * @author Administrator
 *
 */
public class ChooseOneConfigPanel extends JPanel implements DishConfigGroupIFC{
	private ArrayList<ConfigRadioButton> components = new ArrayList<>();
	private DishConfigGroup group;
	private DishConfigDialog parent;
	public ChooseOneConfigPanel(DishConfigDialog parent, DishConfigGroup group){
		this.group = group;
		this.parent = parent;
		initUI();
	}
	
	private void initUI(){
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		if (!group.getDishConfigs().isEmpty()){
			ButtonGroup bg = new ButtonGroup();
			for (int i = 0; i < group.getDishConfigs().size(); i++) {
				DishConfig config = group.getDishConfigs().get(i);
				
				ConfigRadioButton rb = new ConfigRadioButton(config);
				this.add(rb);
				bg.add(rb);
				components.add(rb);
				if (i == 0){
					rb.setSelected(true);
				}
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

	class ConfigRadioButton extends JRadioButton implements DishConfigIFC{
		public DishConfig config;
		public ConfigRadioButton(DishConfig config){
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
