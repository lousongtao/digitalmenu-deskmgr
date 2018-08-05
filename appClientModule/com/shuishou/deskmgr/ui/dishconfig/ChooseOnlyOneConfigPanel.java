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
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.beans.DishConfig;
import com.shuishou.deskmgr.beans.DishConfigGroup;
import com.shuishou.deskmgr.ui.MainFrame;

/**
 * 要求选择数量为1个, 不可以是0个或多个 : 此时使用RadioButton做为控件, 默认选中第一个选项
 * @author Administrator
 *
 */
public class ChooseOnlyOneConfigPanel extends JPanel implements DishConfigGroupIFC{
	private ArrayList<DishConfigRadioButton> components = new ArrayList<>();
	private DishConfigGroup group;
	private DishConfigDialog parent;
	public ChooseOnlyOneConfigPanel(DishConfigDialog parent, DishConfigGroup group){
		this.group = group;
		this.parent = parent;
		initUI();
	}
	
	private void initUI(){
		if (group.getDishConfigs() == null || group.getDishConfigs().isEmpty())
			return;
		this.setLayout(new GridBagLayout());
		if (!group.getDishConfigs().isEmpty()){
			ButtonGroup bg = new ButtonGroup();
			Collections.sort(group.getDishConfigs(), new Comparator<DishConfig>(){

				@Override
				public int compare(DishConfig o1, DishConfig o2) {
					return o1.getSequence() - o2.getSequence();
				}});
			for (int i = 0; i < group.getDishConfigs().size(); i++) {
				DishConfig config = group.getDishConfigs().get(i);
				
				DishConfigRadioButton rb = new DishConfigRadioButton(parent, config);
				this.add(rb, new GridBagConstraints(i % ConstantValue.dishConfig_Column, (int)i/ConstantValue.dishConfig_Column, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
				bg.add(rb.getRadioButton());
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
                configs.add(components.get(i).getDishConfig());
            }
        }
        return configs;
	}

	@Override
	public DishConfigGroup getDishConfigGroup() {
		return group;
	}


}
