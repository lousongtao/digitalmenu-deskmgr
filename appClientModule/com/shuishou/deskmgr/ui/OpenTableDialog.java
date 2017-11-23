
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
import com.shuishou.deskmgr.beans.DishChooseSubitem;
import com.shuishou.deskmgr.beans.Flavor;
import com.shuishou.deskmgr.beans.HttpResult;
import com.shuishou.deskmgr.http.HttpUtil;
import com.shuishou.deskmgr.ui.components.JBlockedButton;
import com.shuishou.deskmgr.ui.components.NumberInputDialog;
import com.shuishou.deskmgr.ui.components.NumberTextField;

public class OpenTableDialog extends JDialog {
	private final Logger logger = Logger.getLogger(OpenTableDialog.class.getName());
	private MainFrame mainFrame;
	private Desk desk;
	
	private JTextField tfSearchCode = new JTextField();
	private NumberTextField tfCustomerAmount = new NumberTextField(this, false);
	private JButton btnRemove = new JButton(Messages.getString("OpenTableDialog.RemoveDish"));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JButton btnFlavor = new JButton(Messages.getString("OpenTableDialog.SetFlavor"));
	private JBlockedButton btnConfirm = new JBlockedButton(Messages.getString("OpenTableDialog.ConfirmOrder"), null);
	private JPanel pDishes = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JList<ChoosedDish> listChoosedDish = new JList<>();
	private ListModel<ChoosedDish> listModelChoosedDish = new ListModel();
	private JTextField tfWholeOrderComment = new JTextField();
	
	public static final byte MAKENEWORDER = 1;
	public static final byte ADDDISH = 2;
	private byte status = 0;
	
	public OpenTableDialog(MainFrame mainFrame,String title, boolean modal, Desk desk, byte status){
		super(mainFrame, title, modal);
		this.mainFrame = mainFrame;
		this.desk = desk;
		this.status = status;
		initUI();
	}
	
	private void initUI(){
		JLabel lbDeskNo = new JLabel(Messages.getString("OpenTableDialog.TableNo") + desk.getName());
		lbDeskNo.setFont(ConstantValue.FONT_25BOLD);
		JLabel lbCustomerAmount = new JLabel();
		lbCustomerAmount.setFont(ConstantValue.FONT_25BOLD);
		lbCustomerAmount.setText(Messages.getString("OpenTableDialog.CustomerAmount"));
		if (status == ADDDISH){
			lbCustomerAmount.setVisible(false);
			tfCustomerAmount.setVisible(false);
			tfWholeOrderComment.setVisible(false);
		}
		
		JLabel lbSearchCode = new JLabel(Messages.getString("OpenTableDialog.SearchCode"));
		listChoosedDish.setModel(listModelChoosedDish);
		listChoosedDish.setCellRenderer(new ChoosedDishRenderer());
		listChoosedDish.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listChoosedDish.setFixedCellHeight(50);
		tfWholeOrderComment.setBorder(BorderFactory.createTitledBorder(Messages.getString("OpenTableDialog.WholeOrderComment")));
		btnRemove.setPreferredSize(new Dimension(150, 50));
		btnClose.setPreferredSize(new Dimension(150, 50));
		btnConfirm.setPreferredSize(new Dimension(100, 50));
		btnFlavor.setPreferredSize(new Dimension(150, 50));
		
		JScrollPane jspChooseDish = new JScrollPane(listChoosedDish, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		pDishes.setBorder(BorderFactory.createTitledBorder("Dishes"));
		pDishes.setBackground(Color.white);
		JPanel pDishDishplay = new JPanel(new GridBagLayout());
		
		JPanel pSearch = new JPanel(new BorderLayout());
		pSearch.setPreferredSize(new Dimension(200, 50));
		pSearch.add(lbSearchCode, BorderLayout.WEST);
		pSearch.add(tfSearchCode, BorderLayout.CENTER);
		
		JPanel pCategory2 = new JPanel(new GridLayout(0, 5, 5, 5));
		pCategory2.add(pSearch, 0);
		generateCategory2Panel(pCategory2);
		JScrollPane jspCategory2 = new JScrollPane(pCategory2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		pDishDishplay.add(jspCategory2, 	new GridBagConstraints(0, 0, 1, 1, 1, 0.2, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pDishDishplay.add(pDishes, 			new GridBagConstraints(0, 1, 1, 1, 1, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		JPanel pChoosedDish = new JPanel(new GridBagLayout());
		pChoosedDish.add(lbDeskNo, 			new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(lbCustomerAmount, 	new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(tfCustomerAmount, 	new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(jspChooseDish, 	new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(tfWholeOrderComment,new GridBagConstraints(0, 3, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(btnClose,	 		new GridBagConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(btnRemove,	 		new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(btnFlavor,			new GridBagConstraints(0, 5, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		pChoosedDish.add(btnConfirm,		new GridBagConstraints(0, 6, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(pChoosedDish, BorderLayout.WEST);
		c.add(pDishDishplay,BorderLayout.CENTER);
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				OpenTableDialog.this.setVisible(false);
			}});
		
		btnConfirm.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (status == MAKENEWORDER){
					if (doMakeNewOrder()){
						mainFrame.loadCurrentIndentInfo();
						OpenTableDialog.this.setVisible(false);
					}
				} else if (status == ADDDISH){
					if (doAddDish()){
						mainFrame.loadCurrentIndentInfo();
						OpenTableDialog.this.setVisible(false);
					}
				}
			}});
		btnRemove.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doRemoveDish();
			}});
		tfSearchCode.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {
				doSearchDish();
			}
		});
		btnFlavor.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				doSetFlavor();
			}});
		this.setSize(new Dimension(MainFrame.WINDOW_WIDTH, MainFrame.WINDOW_HEIGHT));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	private void generateCategory2Panel(JPanel p ){
//		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ArrayList<Category2> c2s = mainFrame.getAllCategory2s();
		for (int i = 0; i < c2s.size(); i++) {
			Category2Button btn = new Category2Button(c2s.get(i));
			p.add(btn);
		}
//		return p;
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
	
	
	private void doRemoveDish(){
		if (listChoosedDish.getSelectedIndex() < 0)
			return;
		listModelChoosedDish.removeElementAt(listChoosedDish.getSelectedIndex());
	}
	
	private void doSetFlavor(){
		if (listChoosedDish.getSelectedIndex() < 0)
			return;
		ChoosedDish cd = listChoosedDish.getSelectedValue();
		if (!cd.dish.isAllowFlavor()){
			JOptionPane.showMessageDialog(this, Messages.getString("OpenTableDialog.DishNoFlavor"));
			return;
		}
		SetFlavorDialog dlg = new SetFlavorDialog(this, "Flavor", mainFrame.getFlavorList(), cd.flavors);
		dlg.setVisible(true);
		cd.flavors = dlg.getChoosedFlavors();
		listModelChoosedDish.refreshData(cd, listChoosedDish.getSelectedIndex(), listChoosedDish.getSelectedIndex());
	}
	
	private boolean doMakeNewOrder(){
		if (listModelChoosedDish.getSize() == 0)
			return false;
		if (tfCustomerAmount.getText() == null || tfCustomerAmount.getText().length() == 0){
			JOptionPane.showMessageDialog(this, "Please input customer amount.");
			return false;
		}
		JSONArray ja = new JSONArray();
		for (int i = 0; i< listModelChoosedDish.getSize(); i++) {
			JSONObject jo = new JSONObject();
			ChoosedDish cd = listModelChoosedDish.getElementAt(i);
			jo.put("id", cd.dish.getId());
			jo.put("amount", cd.amount);
			String requires = generateRequires(cd);
			if (requires.length() > 0)
				jo.put("additionalRequirements", requires);
			if (listModelChoosedDish.getElementAt(i).weight > 0){
				jo.put("weight", cd.weight+"");
			}
			ja.put(jo);
		}
		String url = "indent/makeindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("confirmCode", mainFrame.getConfigsMap().get(ConstantValue.CONFIGS_CONFIRMCODE));
		params.put("indents", ja.toString());
		params.put("deskid", desk.getId()+"");
		params.put("customerAmount", tfCustomerAmount.getText());
		params.put("comments", tfWholeOrderComment.getText());
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error("get null from server while making order. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while making order. URL = " + url + ", param = "+ params);
			return false;
		}
		HttpResult<Integer> result = new Gson().fromJson(response, new TypeToken<HttpResult<Integer>>(){}.getType());
		if (!result.success){
			logger.error("return false while making order. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, "return false while making order. URL = " + url + ", response = "+response);
			return false;
		}
		return true;
	}
	
	//add dish to an exist order, used for order exsting on a table
	private boolean doAddDish(){
		if (listModelChoosedDish.getSize() == 0)
			return false;
		
		JSONArray ja = new JSONArray();
		for (int i = 0; i< listModelChoosedDish.getSize(); i++) {
			JSONObject jo = new JSONObject();
			ChoosedDish cd = listModelChoosedDish.getElementAt(i);
			
			jo.put("id", cd.dish.getId());
			jo.put("amount", cd.amount);
			String requires = generateRequires(cd);
			if (requires.length() > 0)
				jo.put("additionalRequirements", requires);
			if (listModelChoosedDish.getElementAt(i).weight > 0){
				jo.put("weight", listModelChoosedDish.getElementAt(i).weight+"");
			}
			ja.put(jo);
		}
		String url = "indent/adddishtoindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("indents", ja.toString());
		params.put("deskid", desk.getId()+"");
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error("get null from server while add dish to order. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while add dish to order. URL = " + url + ", param = "+ params);
			return false;
		}
		HttpResult<Integer> result = new Gson().fromJson(response, new TypeToken<HttpResult<Integer>>(){}.getType());
		if (!result.success){
			logger.error("return false while add dish to order. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, "return false while add dish to order. URL = " + url + ", response = "+response);
			return false;
		}
		return true;
	}
	
	private String generateRequires(ChoosedDish cd){
		String requires = "";
		if (cd.subitems != null && !cd.subitems.isEmpty()){
			for(DishChooseSubitem si : cd.subitems){
				requires += si.getFirstLanguageName()+ " ";
			}
		}
		if (cd.flavors != null && !cd.flavors.isEmpty()){
			for(Flavor f : cd.flavors){
				requires += f.getFirstLanguageName() + " ";
			}
		}
		return requires;
	}
	
	private void doCategory2ButtonClick(Category2 c2){
		pDishes.removeAll();
		for(Dish dish : c2.getDishes()){
			DishButton btn = new DishButton(dish);
			pDishes.add(btn);
		}
		pDishes.updateUI();
	}
	
	/**
	 * 点菜时优先判断chooseMode, 
	 * 1. 需要弹出提示消息. 
	 * 		这类不用特殊处理, 因为这个提示消息是给安卓端看的
	 * 
	 * 2. 需要选择subitem
	 * 		弹出一个对话框, 强制选择subitem, 并将选中的subitem作为requirement记录到indentdetail里面, 然后将dish加入choose列表
	 * 
	 * 3. 普通类型
	 * 		直接将dish加入choose列表即可
	 * 
	 * 把dish加入choose列表时, 判断是否要合并同类项
	 * 1. 不需要合并
	 * 		直接增加一个新的ChoosedDish即可
	 * 		这里要判断购买类型
	 * 
	 * 2. 需要合并
	 * 		查找列表中的同类项, 
	 * 		如果找到, 根据购买类型, 如果是按份购买, 直接份数加一, 如果按重量购买, 需要弹出框, 输入重量, 并将该重量累加到之前购买的里面
	 * 		如果未找到, 新加一条记录, 同时根据购买类型处理.
	 * @param dish
	 */
	private void doDishButtonClick(Dish dish){
		if (dish.getChooseMode() == ConstantValue.DISH_CHOOSEMODE_POPINFOCHOOSE
				|| dish.getChooseMode() == ConstantValue.DISH_CHOOSEMODE_POPINFOQUIT){
			//do this type as normal
		}
		//build a new ChoosedDish
		ChoosedDish cd = new ChoosedDish();
		cd.dish = dish;
		cd.amount = 1;
		
		if (dish.getChooseMode() == ConstantValue.DISH_CHOOSEMODE_SUBITEM){
			DishSubitemDialog dlg = new DishSubitemDialog(this, Messages.getString("OpenTableDialog.ChooseSubitem"), dish);
			dlg.setVisible(true);
			if (dlg.choosed == null || dlg.choosed.isEmpty()){
				return;
			}
			cd.subitems.addAll(dlg.choosed);
		}
		
		if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT){
			NumberInputDialog numdlg = new NumberInputDialog(this, "Input", Messages.getString("OpenTableDialog.InputWeight"), false);
			numdlg.setVisible(true);
			if (!numdlg.isConfirm)
				return;
			cd.weight = numdlg.inputDouble;
		}
		//判断是否要合同同类型, 如果需要合并, 再查找是否列表中已经存在, 存在的话, 再判断购买类型
		if (dish.isAutoMergeWhileChoose()) {
			boolean foundexist = false;
			for (int i = 0; i < this.listModelChoosedDish.size(); i++) {
				if (listModelChoosedDish.getElementAt(i).dish.getId() == dish.getId()) {
					if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT){
						listModelChoosedDish.getElementAt(i).amount = listModelChoosedDish.getElementAt(i).amount + 1;
					} else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT){
						listModelChoosedDish.getElementAt(i).weight += cd.weight;
					}
					listChoosedDish.updateUI();
					foundexist = true;
					break;
				}
			}
			if (!foundexist) {
				listModelChoosedDish.addElement(cd);
			}
		} else {
			listModelChoosedDish.addElement(cd);
		}
	}
	
	
	class ChoosedDishRenderer extends JPanel implements ListCellRenderer{
		private JLabel lbDish = new JLabel();
		private JLabel lbAmount = new JLabel();
		private JLabel lbRequire = new JLabel();
		public ChoosedDishRenderer(){
			setLayout(new GridBagLayout());
			add(lbDish, 	new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			add(lbAmount, 	new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			add(lbRequire, 	new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			lbRequire.setFont(ConstantValue.FONT_20PLAIN);
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
			ChoosedDish cd = (ChoosedDish)value;
			Dish dish = cd.dish;
			String txt = dish.getFirstLanguageName();
			if (txt.length() > 17)
				txt = txt.substring(0, 17) + "...";
			lbDish.setText(txt);
			lbAmount.setText(cd.amount+"");
			lbRequire.setText(generateRequires(cd));
			return this;
		}
		
	}
	
	class ChoosedDish {
		public int amount;
		public Dish dish;
		public ArrayList<Flavor> flavors = new ArrayList<>();
		public ArrayList<DishChooseSubitem> subitems = new ArrayList<>();
		public double weight;
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
			this.setPreferredSize(buttonsize);
		}
	}
	
	class DishButton extends JButton{
		private final Dish dish;
		
		public DishButton(Dish d){
			dish = d;
			this.setText(d.getFirstLanguageName());
			this.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					
					doDishButtonClick(dish);
				}
				
			});
			this.setPreferredSize(buttonsize);
		}
	}
	
	class ListModel<ChoosedDish> extends DefaultListModel<ChoosedDish>{
		public void refreshData(ChoosedDish cd, int start, int end){
			super.fireContentsChanged(cd, start, end);
		}
	}
	private final static Dimension buttonsize = new Dimension(180, 50);
}
