package com.shuishou.deskmgr.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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
import com.shuishou.deskmgr.beans.DishChooseSubitem;
import com.shuishou.deskmgr.ui.components.JBlockedButton;

public class DishSubitemDialog extends JDialog {
	private final Logger logger = Logger.getLogger(DishSubitemDialog.class.getName());
	private Dish dish;
	private Dialog parent;
	private JButton btnRemove = new JButton(Messages.getString("DishSubitemDialog.Remove"));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JBlockedButton btnConfirm = new JBlockedButton(Messages.getString("DishSubitemDialog.Confirm"), null);
	private JPanel pSubitems = new JPanel(new GridLayout(0, 3, 5, 5));
	private JList<DishChooseSubitem> listChoosed = new JList<>();
	private DefaultListModel<DishChooseSubitem> listModelChoosed = new DefaultListModel<>();
	
	public ArrayList<DishChooseSubitem> choosed = new ArrayList<>();
	public DishSubitemDialog(Dialog parent, String title, Dish dish){
		super(parent, title, true);
		this.parent = parent;
		this.dish = dish;
		initUI();
	}
	
	private void initUI(){
		JLabel lbReqAmount = new JLabel(Messages.getString("DishSubitemDialog.ReqAmount") + dish.getSubitemAmount());
		lbReqAmount.setFont(ConstantValue.FONT_25BOLD);
		
		listChoosed.setModel(listModelChoosed);
		listChoosed.setCellRenderer(new DishSubitemRenderer());
		listChoosed.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listChoosed.setFixedCellHeight(50);
		btnRemove.setPreferredSize(new Dimension(150, 50));
		btnClose.setPreferredSize(new Dimension(150, 50));
		btnConfirm.setPreferredSize(new Dimension(100, 50));
		
		JScrollPane jspChoosed = new JScrollPane(listChoosed, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		pSubitems.setBorder(BorderFactory.createTitledBorder("items"));
		pSubitems.setBackground(Color.white);
		for (int i = 0; i < dish.getChooseSubItems().size(); i++) {
			SubitemButton btn = new SubitemButton(dish.getChooseSubItems().get(i));
			pSubitems.add(btn);
		}
		
		JPanel pChoosed = new JPanel(new GridBagLayout());
		pChoosed.add(lbReqAmount, 		new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosed.add(jspChoosed, 	new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pChoosed.add(btnClose,	 		new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosed.add(btnRemove,	 		new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosed.add(btnConfirm,		new GridBagConstraints(0, 4, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 0, 0), 0, 0));
		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(pChoosed, BorderLayout.WEST);
		c.add(pSubitems,BorderLayout.CENTER);
		
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				DishSubitemDialog.this.setVisible(false);
			}});
		
		btnConfirm.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doConfirm();
			}});
		btnRemove.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (listChoosed.getSelectedIndex() < 0)
					return;
				listModelChoosed.removeElementAt(listChoosed.getSelectedIndex());
			}});
		this.setSize(900, 400);
		this.setLocation((int)(parent.getWidth() / 2 - this.getWidth() /2 + parent.getLocation().getX()), 
				(int)(parent.getHeight() / 2 - this.getHeight() / 2 + parent.getLocation().getY()));
	}
	
	private void doConfirm(){
		if (listChoosed.getModel().getSize() != dish.getSubitemAmount()){
			String msg = "the required amount is "+ dish.getSubitemAmount();
			JOptionPane.showMessageDialog(this, msg);
			return;
		}
		for (int i = 0; i < listChoosed.getModel().getSize(); i++) {
			choosed.add(listChoosed.getModel().getElementAt(i));
		}
		DishSubitemDialog.this.setVisible(false);
	}
	
	class SubitemButton extends JButton{
		private final DishChooseSubitem item;
		public SubitemButton(DishChooseSubitem _item){
			this.item = _item;
			if (ConstantValue.LANGUAGE_CHINESE.equals(MainFrame.language)){
				this.setText(item.getChineseName());
			} else {
				this.setText(item.getEnglishName());
			}
			this.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					listModelChoosed.addElement(item);
				}
				
			});
			this.setPreferredSize(new Dimension(200, 50));
		}
	}
	
	class DishSubitemRenderer extends JPanel implements ListCellRenderer{
		private JLabel lbDish = new JLabel();
		public DishSubitemRenderer(){
			setLayout(new BorderLayout());
			add(lbDish, BorderLayout.CENTER);
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
			DishChooseSubitem item = (DishChooseSubitem)value;
			String txt = item.getChineseName();
			if (ConstantValue.LANGUAGE_ENGLISH.equals(MainFrame.language)){
				item.getEnglishName();
			}
			if (txt.length() > 20)
				txt = txt.substring(0, 20) + "...";
			lbDish.setText(txt);
			return this;
		}
		
	}
}
