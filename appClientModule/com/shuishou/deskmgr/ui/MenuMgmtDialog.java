
package com.shuishou.deskmgr.ui;

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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Category2;
import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.beans.Dish;
import com.shuishou.deskmgr.beans.HttpResult;
import com.shuishou.deskmgr.http.HttpUtil;
import com.shuishou.deskmgr.ui.components.JBlockedButton;
import com.shuishou.deskmgr.ui.components.NumberInputDialog;
import com.shuishou.deskmgr.ui.components.NumberTextField;

public class MenuMgmtDialog extends JDialog {
	private final Logger logger = Logger.getLogger(MenuMgmtDialog.class.getName());
	private MainFrame mainFrame;
	
	private JTextField tfSearchCode = new JTextField();
	private JButton btnCancelSoldout = new JButton(Messages.getString("MenuMgmtDialog.CancelSoldout"));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JBlockedButton btnMakeSoldout = new JBlockedButton(Messages.getString("MenuMgmtDialog.MakeSoldout"), null);
	private JPanel pDishes = new JPanel(new GridBagLayout());
	private JList<Dish> listDish = new JList<>();
	private DefaultListModel<Dish> listModelDish = new DefaultListModel<>();
	
	public MenuMgmtDialog(MainFrame mainFrame,String title, boolean modal){
		super(mainFrame, title, modal);
		this.mainFrame = mainFrame;
		initUI();
		initSoldoutList();
	}
	
	private void initUI(){
		JLabel lbSearchCode = new JLabel(Messages.getString("OpenTableDialog.SearchCode"));
		listDish.setModel(listModelDish);
		listDish.setCellRenderer(new DishRenderer());
		listDish.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listDish.setFixedCellHeight(50);
		btnCancelSoldout.setPreferredSize(new Dimension(150, 50));
		btnClose.setPreferredSize(new Dimension(150, 50));
		btnMakeSoldout.setPreferredSize(new Dimension(100, 50));
		
		JScrollPane jspChooseDish = new JScrollPane(listDish, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		pDishes.setBorder(BorderFactory.createTitledBorder("Dishes"));
		pDishes.setBackground(Color.white);
		JScrollPane jspDish = new JScrollPane(pDishes, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JPanel pDishDishplay = new JPanel(new GridBagLayout());
		
		JPanel pSearch = new JPanel(new BorderLayout());
		pSearch.setPreferredSize(new Dimension(200, 50));
		pSearch.add(lbSearchCode, BorderLayout.WEST);
		pSearch.add(tfSearchCode, BorderLayout.CENTER);
		
		JPanel pCategory2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pCategory2.add(pSearch, 0);
		generateCategory2Panel(pCategory2);
//		JScrollPane jspCategory2 = new JScrollPane(pCategory2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		pDishDishplay.add(pCategory2, 		new GridBagConstraints(0, 0, 1, 1, 1, 0.2, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pDishDishplay.add(jspDish, 			new GridBagConstraints(0, 1, 1, 1, 1, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		JPanel pChoosedDish = new JPanel(new GridBagLayout());
		pChoosedDish.add(jspChooseDish, 	new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(btnClose,	 		new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(btnCancelSoldout,	new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(btnMakeSoldout,	new GridBagConstraints(0, 4, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 0, 0), 0, 0));
		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(pChoosedDish, BorderLayout.WEST);
		c.add(pDishDishplay,BorderLayout.CENTER);
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				MenuMgmtDialog.this.setVisible(false);
			}});
		
		btnMakeSoldout.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < listModelDish.size(); i++) {
					Dish dish = listModelDish.getElementAt(i);
					if (!dish.isSoldOut()){
						if (doSoldout(dish, true)){
							listDish.updateUI();
						}
					}
				}
			}});
		btnCancelSoldout.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Dish dish = listDish.getSelectedValue();
				if (dish == null)
					return;
				if (!dish.isSoldOut()){
					listModelDish.removeElement(dish);
				} else if (doSoldout(dish, false)){
					listModelDish.removeElement(dish);
				}
			}});
		tfSearchCode.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {
				doSearchDish();
			}
		});
		this.setSize(new Dimension(ConstantValue.WINDOW_WIDTH, ConstantValue.WINDOW_HEIGHT));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	private void initSoldoutList(){
		//reload the menu data
		mainFrame.loadMenu();
		ArrayList<Dish> dishes = mainFrame.getAllDishes();
		for (int j = 0; j < dishes.size(); j++) {
			if (dishes.get(j).isSoldOut()) {
				listModelDish.addElement(dishes.get(j));
			}
		}
	}
	
	private void generateCategory2Panel(JPanel p ){
		ArrayList<Category2> c2s = mainFrame.getAllCategory2s();
		for (int i = 0; i < c2s.size(); i++) {
			Category2Button btn = new Category2Button(c2s.get(i));
			p.add(btn);
		}
	}
	
	private void doSearchDish(){
		pDishes.removeAll();
		if (tfSearchCode.getText() == null || this.tfSearchCode.getText().length() < 2)
			return;
		ArrayList<Dish> allDishes = mainFrame.getAllDishes();
		for(Dish dish : allDishes){
			if (dish.getFirstLanguageName().toLowerCase().indexOf(tfSearchCode.getText().toLowerCase()) >= 0){
				pDishes.add(new DishButton(dish));
			} else if (dish.getSecondLanguageName() != null && dish.getSecondLanguageName().toLowerCase().indexOf(tfSearchCode.getText().toLowerCase()) >= 0){
				pDishes.add(new DishButton(dish));
			} else if (dish.getAbbreviation() != null && dish.getAbbreviation().toLowerCase().indexOf(tfSearchCode.getText().toLowerCase()) >= 0){
				pDishes.add(new DishButton(dish));
			}
		}
		pDishes.updateUI();
	}
	
	private boolean doSoldout(Dish dish, boolean isSoldout){
		String url = "menu/changedishsoldout";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", String.valueOf(mainFrame.getOnDutyUser().getId()));
		params.put("id", String.valueOf(dish.getId()));
		params.put("isSoldOut", String.valueOf(isSoldout));
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server while set/cancel sold out. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while set/cancel sold out. URL = " + url + ", param = "+ params);
			return false;
		}
		HttpResult<Dish> result = new Gson().fromJson(response, new TypeToken<HttpResult<Dish>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while set/cancel sold out. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return false;
		}
		dish.setSoldOut(isSoldout);
		return true;
	}
	
	private void doCategory2ButtonClick(Category2 c2){
		int amountPerRow = 4;
		pDishes.removeAll();
		for(int i = 0; i < c2.getDishes().size(); i++){
			Dish dish = c2.getDishes().get(i);
			DishButton btn = new DishButton(dish);
			pDishes.add(btn, new GridBagConstraints(i % amountPerRow, (int) i / amountPerRow, 1, 1, 1, 0.2, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		pDishes.updateUI();
	}
	
	/**
	 * 如果dish已经存在于列表中, 不做处理
	 * @param dish
	 */
	private void doDishButtonClick(Dish dish){
		boolean foundexist = false;
		for (int i = 0; i < this.listModelDish.size(); i++) {
			if (listModelDish.getElementAt(i).getId() == dish.getId()) {
				foundexist = true;
				break;
			}
		}
		if (!foundexist) {
			listModelDish.insertElementAt(dish, 0);
		}
	}
	

	
	class DishRenderer extends JPanel implements ListCellRenderer{
		private JLabel lbDish = new JLabel();
		public DishRenderer(){
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
			Dish dish = (Dish)value;
			String txt = dish.getFirstLanguageName();
			if (txt.length() > 15)
				txt = txt.substring(0, 15) + "...";
			if (dish.isSoldOut())
				lbDish.setText("<html>"+txt+"<font color='red'><b>SOLDOUT</b></font></html>");
			else lbDish.setText(txt);
			return this;
		}
	}
	
	class Category2Button extends JButton{
		private final Category2 c2;
		public Category2Button(Category2 category2){
			this.c2 = category2;
			this.setText(c2.getFirstLanguageName());
			this.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					
					doCategory2ButtonClick(c2);
				}
			});
			Dimension d = this.getPreferredSize();
			double width = d.getWidth();
			if (width < 100)
				width = 100;
			d.setSize(width, 50);
			this.setPreferredSize(d);
//			this.setPreferredSize(buttonsize);
		}
	}
	
	class DishButton extends JButton{
		private final Dish dish;
		public DishButton(Dish d){
			dish = d;
			this.setText(dish.getFirstLanguageName());
			this.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					
					doDishButtonClick(dish);
				}
			});
			this.setPreferredSize(buttonsize);
		}
	}
	
	private final static Dimension buttonsize = new Dimension(180, 50);
}
