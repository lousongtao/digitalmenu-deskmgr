
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
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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
import com.shuishou.deskmgr.beans.DishConfig;
import com.shuishou.deskmgr.beans.Flavor;
import com.shuishou.deskmgr.beans.HttpResult;
import com.shuishou.deskmgr.beans.Indent;
import com.shuishou.deskmgr.http.HttpUtil;
import com.shuishou.deskmgr.ui.components.JBlockedButton;
import com.shuishou.deskmgr.ui.components.NumberInputDialog;
import com.shuishou.deskmgr.ui.components.NumberTextField;
import com.shuishou.deskmgr.ui.dishconfig.DishConfigDialog;

public class OpenTableDialog extends JDialog implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(OpenTableDialog.class.getName());
	private static final Color COLOR_CHOOSE = Color.green;
	private static final Color COLOR_UNCHOOSE = new Color(240, 240, 240);
	private MainFrame mainFrame;
	private Desk desk;
	
	private ArrayList<Category2Label> listCategory2Label = new ArrayList<>();
	
	private JTextField tfSearchCode = new JTextField();
	private NumberTextField tfCustomerAmount = new NumberTextField(this, false);
	private JButton btnRemove = new JButton(Messages.getString("OpenTableDialog.RemoveDish"));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JButton btnFlavor = new JButton(Messages.getString("OpenTableDialog.SetFlavor"));
	private JButton btnTakeaway = new JButton(Messages.getString("OpenTableDialog.Takeaway"));
	private JBlockedButton btnConfirm = new JBlockedButton(Messages.getString("OpenTableDialog.ConfirmOrder"), null);
	private JBlockedButton btnConfirmAndPay = new JBlockedButton(Messages.getString("OpenTableDialog.ConfirmAndPayOrder"), null);
	private JPanel pDishes = new JPanel(new GridLayout(ConstantValue.openTableDialog_DishLayout_Row, ConstantValue.openTableDialog_DishLayout_Column));
	private JList<ChoosedDish> listChoosedDish = new JList<>();
	private ListModel<ChoosedDish> listModelChoosedDish = new ListModel<>();
	private JTextField tfWholeOrderComment = new JTextField();
	private JLabel lbPrice = new JLabel();
	private JPanel pSearch;
	private JPanel pCategory2;
	private Image imgNextPage;
	private Image imgPrePage;
	
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
		try {
			imgNextPage = ImageIO.read(getClass().getResource("/resource/arrowdown.png"));
			imgPrePage = ImageIO.read(getClass().getResource("/resource/arrowup.png"));
		} catch (IOException e1) {
			logger.error("", e1);
		}
		lbPrice.setFont(ConstantValue.FONT_25BOLD);
		JLabel lbDeskNo = new JLabel(Messages.getString("OpenTableDialog.TableNo") + desk.getName());
		lbDeskNo.setFont(ConstantValue.FONT_25BOLD);
		JLabel lbCustomerAmount = new JLabel();
		lbCustomerAmount.setFont(ConstantValue.FONT_25BOLD);
		lbCustomerAmount.setText(Messages.getString("OpenTableDialog.CustomerAmount"));
		tfCustomerAmount.setText("2");
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
		btnTakeaway.setPreferredSize(new Dimension(150, 50));
		
		JScrollPane jspChooseDish = new JScrollPane(listChoosedDish, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		pDishes.setBorder(BorderFactory.createTitledBorder("Dishes"));
		pDishes.setBackground(Color.white);
		JScrollPane jspDish = new JScrollPane(pDishes, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JPanel pDishDishplay = new JPanel(new GridBagLayout());
		
		//搜索框会被拉宽, 需要用两个label把他向内挤压
		pSearch = new JPanel(new GridBagLayout());
		pSearch.setBorder(BorderFactory.createLineBorder(Color.gray));
//		pSearch.add(new JLabel(), new GridBagConstraints(0, 0, 2, 1, 1, 0.2, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pSearch.add(lbSearchCode, new GridBagConstraints(0, 0, 1, 1, 1, 0.2, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		pSearch.add(tfSearchCode, new GridBagConstraints(0, 1, 1, 1, 1, 0.2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
		pSearch.add(new JLabel(), new GridBagConstraints(0, 2, 2, 1, 1, 0.2, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
		
		pCategory2 = new JPanel(new GridLayout(ConstantValue.openTableDialog_Category2Layout_Row, ConstantValue.openTableDialog_Category2Layout_Column));
		pCategory2.setBorder(BorderFactory.createTitledBorder("Category2"));
		
		generateCategory2Panel(0);
//		JScrollPane jspCategory2 = new JScrollPane(pCategory2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		pDishDishplay.add(pCategory2, 	new GridBagConstraints(0, 0, 1, 1, 1, 0.2, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pDishDishplay.add(jspDish, 		new GridBagConstraints(0, 1, 1, 1, 1, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		JPanel pChoosedDish = new JPanel(new GridBagLayout());
		pChoosedDish.add(lbDeskNo, 			new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(lbCustomerAmount, 	new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(tfCustomerAmount, 	new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(lbPrice, 			new GridBagConstraints(0, 2, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(jspChooseDish, 	new GridBagConstraints(0, 3, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedDish.add(tfWholeOrderComment,new GridBagConstraints(0, 4, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		btnClose.setPreferredSize(new Dimension(100, 50));
		btnRemove.setPreferredSize(new Dimension(100, 50));
		btnFlavor.setPreferredSize(new Dimension(100, 50));
		btnTakeaway.setPreferredSize(new Dimension(100, 50));
		btnConfirm.setPreferredSize(new Dimension(100, 50));
		btnConfirmAndPay.setPreferredSize(new Dimension(100, 50));
		JPanel pButton = new JPanel(new GridLayout(1, 0, 20, 0));
		pButton.add(btnClose);
		pButton.add(btnRemove);
		pButton.add(btnFlavor);
		pButton.add(btnTakeaway);
		pButton.add(btnConfirm);
		pButton.add(btnConfirmAndPay);
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(pChoosedDish, BorderLayout.WEST);
		c.add(pDishDishplay,BorderLayout.CENTER);
		c.add(pButton, BorderLayout.SOUTH);
		btnClose.addActionListener(this);
		btnConfirm.addActionListener(this);
		btnRemove.addActionListener(this);
		btnConfirmAndPay.addActionListener(this);
		tfSearchCode.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {
				doSearchDish();
			}
		});
		btnFlavor.addActionListener(this);
		btnTakeaway.addActionListener(this);
		this.setSize(new Dimension(mainFrame.getWidth(), mainFrame.getHeight()));
//		this.setSize(new Dimension(1280, 768));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnClose){
			OpenTableDialog.this.setVisible(false);
		} else if (e.getSource() == btnTakeaway){
			doSetTakeaway();
		} else if (e.getSource() == btnConfirm){
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
		} else if (e.getSource() == btnRemove){
			doRemoveDish();
		} else if (e.getSource() == tfSearchCode){
			doSearchDish();
		} else if (e.getSource() == btnFlavor){
			doSetFlavor();
		} else if (e.getSource() == btnConfirmAndPay){
			doConfirmAndPay();
		}
	}
	
	private void doConfirmAndPay(){
		boolean successOrder = false;
		if (status == MAKENEWORDER){
			successOrder = doMakeNewOrder();
		} else if (status == ADDDISH){
			successOrder = doAddDish();
		}
		if (successOrder){
			OpenTableDialog.this.setVisible(false);
			Indent indent = mainFrame.loadIndentByDesk(desk.getName());
			if (indent == null)
				return;
			CheckoutDialog dlg = new CheckoutDialog(mainFrame, Messages.getString("MainFrame.CheckoutTitle"), true, desk, indent); //$NON-NLS-1$
			dlg.setVisible(true);
		}
		
	}
	
	/**
	 * 根据GridLayout生成Category2的面板
	 * 如果category2数量多, 需要分页显示
	 * 如果category2数量太少, 不足一行, 需要补充几个空控件, 否则界面会被拉长, 很难看
	 * @param startPosition 当category2数量过多时, 需要分页显示, 这个参数指示当前页从第几个category2开始
	 */
	private void generateCategory2Panel(int startPosition ){
		ArrayList<Category2> c2s = mainFrame.getAllCategory2s();
		pCategory2.removeAll();
		listCategory2Label.clear();
		pCategory2.add(pSearch, 0); //搜索框始终位于第一位
		
		int lastPosition = startPosition + ConstantValue.openTableDialog_Category2Layout_Row * ConstantValue.openTableDialog_Category2Layout_Column - 1; //最后一个可以显示的位置
		
		if (startPosition > 0){
			lastPosition--;//减去一个位置, 显示上一页
			pCategory2.add(new Category2PrePageLabel(0));
		}
		if (c2s.size() > lastPosition)
			lastPosition--;//减去一个位置, 显示下一页
		for (int i = 0; i < c2s.size(); i++) {
			if (i < startPosition)
				continue;
			if (i < lastPosition || i == c2s.size() - 1){
				Category2Label lb = new Category2Label(c2s.get(i));
				pCategory2.add(lb);
				listCategory2Label.add(lb);
			} else {
				pCategory2.add(new Category2NextPageLabel(i));
				break;
			}
		}
		//如果category2数量太少, 需要补充几个空控件, 否则组件会被拉长, 很难看. 注意: 搜索框已经占据了一个位置
//		if (listCategory2Label.size() < ConstantValue.openTableDialog_Category2Layout_Row * ConstantValue.openTableDialog_Category2Layout_Column - 1){
//			for (int i = 0; i < ConstantValue.openTableDialog_Category2Layout_Row * ConstantValue.openTableDialog_Category2Layout_Column - 1 - listCategory2Label.size(); i++) {
//				pCategory2.add(new JLabel());
//			}
//		}
		pCategory2.updateUI();
	}
	
	private void doSearchDish(){
		pDishes.removeAll();
		if (tfSearchCode.getText() == null || this.tfSearchCode.getText().length() < 2)
			return;
		ArrayList<Dish> allDishes = mainFrame.getAllDishes();
		for(Dish dish : allDishes){
			if (dish.getFirstLanguageName().toLowerCase().indexOf(tfSearchCode.getText().toLowerCase()) >= 0){
				pDishes.add(new DishLabel(dish));
			} else if (dish.getSecondLanguageName() != null && dish.getSecondLanguageName().toLowerCase().indexOf(tfSearchCode.getText().toLowerCase()) >= 0){
				pDishes.add(new DishLabel(dish));
			} else if (dish.getAbbreviation() != null && dish.getAbbreviation().toLowerCase().indexOf(tfSearchCode.getText().toLowerCase()) >= 0){
				pDishes.add(new DishLabel(dish));
			}
		}
		pDishes.updateUI();
	}
	
	
	private void doRemoveDish(){
		if (listChoosedDish.getSelectedIndex() < 0)
			return;
		listModelChoosedDish.removeElementAt(listChoosedDish.getSelectedIndex());
		refreshPriceLabel();
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
	
	private void doSetTakeaway(){
		if (listChoosedDish.getSelectedIndex() < 0)
			return;
		ChoosedDish cd = listChoosedDish.getSelectedValue();
		Flavor f = new Flavor();
		f.setFirstLanguageName(Messages.getString("OpenTableDialog.Takeaway"));
		f.setSecondLanguageName(Messages.getString("OpenTableDialog.Takeaway"));
		cd.flavors.add(f);
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
			jo.put("dishPrice", cd.getPrice());
			jo.put("operator", mainFrame.getOnDutyUser().getName());
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
		
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server while making order. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while making order. URL = " + url + ", param = "+ params);
			return false;
		}
		HttpResult<Integer> result = new Gson().fromJson(response, new TypeToken<HttpResult<Integer>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while making order. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
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
			jo.put("dishPrice", cd.getPrice());
			jo.put("operator", mainFrame.getOnDutyUser().getName());
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
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server while add dish to order. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while add dish to order. URL = " + url + ", param = "+ params);
			return false;
		}
		HttpResult<Integer> result = new Gson().fromJson(response, new TypeToken<HttpResult<Integer>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while add dish to order. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return false;
		}
		return true;
	}
	
	private String generateRequires(ChoosedDish cd){
		String requires = "";
		if (cd.configs != null && !cd.configs.isEmpty()){
			for(DishConfig config : cd.configs){
				requires += config.getFirstLanguageName();
//				if (config.getPrice() !=0 )
//					requires += "$" + config.getPrice();
				requires += "\n";
			}
		}
		if (cd.flavors != null && !cd.flavors.isEmpty()){
			for(Flavor f : cd.flavors){
				requires += f.getFirstLanguageName() + "\n";
			}
		}
		return requires;
	}
	
	private void generateDishPanel(Category2 c2, int startPosition){
		pDishes.removeAll();
		int lastPosition = startPosition + ConstantValue.openTableDialog_DishLayout_Row * ConstantValue.openTableDialog_DishLayout_Column;
		if (startPosition > 0){
			lastPosition--;//减去一个位置, 显示上一页
			pDishes.add(new DishPrePageLabel(c2, 0));
		}
		if (c2.getDishes().size() > lastPosition){
			lastPosition--;//减去一个位置, 显示下一页
		}
		
		for(int i = 0; i < c2.getDishes().size(); i++){
			if (i < startPosition)
				continue;
			if (i < lastPosition || i == c2.getDishes().size() - 1){
				Dish dish = c2.getDishes().get(i);
				DishLabel lb = new DishLabel(dish);
				pDishes.add(lb);
			} else {
				pDishes.add(new DishNextPageLabel(c2, i));
				break;
			}
		}
		pDishes.updateUI();
		for(Category2Label lb : listCategory2Label){
			if (lb.c2.getId() == c2.getId())
				lb.setBackground(COLOR_CHOOSE);
			else 
				lb.setBackground(COLOR_UNCHOOSE);
		}
	}
	
	/**
	 * 点菜时优先判断chooseMode, 
	 * 1. 需要弹出提示消息. 
	 * 		这类不用特殊处理, 因为这个提示消息是给安卓端看的
	 * 
	 * 2. 存在配置项
	 * 		弹出一个对话框, 要求用户选择某个或者多个配置, 将选择结果作为requirement记录到indentdetail里面, 然后将dish加入choose列表
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
	private void doDishLabelPress(Dish dish){
		if (dish.getChooseMode() == ConstantValue.DISH_CHOOSEMODE_POPINFOCHOOSE
				|| dish.getChooseMode() == ConstantValue.DISH_CHOOSEMODE_POPINFOQUIT){
			//do this type as normal
		}
		//build a new ChoosedDish
		ChoosedDish cd = new ChoosedDish();
		cd.dish = dish;
		cd.amount = 1;
		
		if (dish.getConfigGroups() != null && !dish.getConfigGroups().isEmpty()){
			DishConfigDialog dlg = new DishConfigDialog(this, "Choose Config", dish);
			dlg.setVisible(true);
			if (dlg.isCancel())
				return;
			if (dlg.choosed != null)
				cd.configs.addAll(dlg.choosed);
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
				listModelChoosedDish.insertElementAt(cd,0);
			}
		} else {
			listModelChoosedDish.insertElementAt(cd,0);
		}
		refreshPriceLabel();
	}
	
	private void refreshPriceLabel(){
		if (listModelChoosedDish.getSize() > 0){
			double price = 0;
			for (int i = 0; i < listModelChoosedDish.size(); i++) {
				ChoosedDish cd = listModelChoosedDish.get(i);
				if (cd.dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT)
					price += cd.getPrice() * cd.amount;
				else 
					price += cd.getPrice() * cd.amount * cd.weight;
			}
			lbPrice.setText("Items: " + listModelChoosedDish.size() + "  Price: $" + String.format(ConstantValue.FORMAT_DOUBLE, price));
		} else {
			lbPrice.setText("");
		}
	}
	
	class ChoosedDishRenderer extends JPanel implements ListCellRenderer{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JLabel lbDish = new JLabel();
		private JLabel lbAmountPrice = new JLabel();
		private JLabel lbRequire = new JLabel();
		public ChoosedDishRenderer(){
			setLayout(new GridBagLayout());
			add(lbDish, 	new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			add(lbAmountPrice, 	new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			add(lbRequire, 	new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			lbRequire.setFont(ConstantValue.FONT_10PLAIN);
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
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT)
				lbAmountPrice.setText(cd.amount+" / $"+ String.format(ConstantValue.FORMAT_DOUBLE, cd.getPrice()));
			else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
				lbAmountPrice.setText(cd.amount+" / "+cd.weight);
			lbRequire.setText(generateRequires(cd));
			return this;
		}
		
	}
	
	class ChoosedDish {
		public int amount;
		public Dish dish;
		public ArrayList<Flavor> flavors = new ArrayList<>();
		public ArrayList<DishConfig> configs = new ArrayList<>();
		public double weight;
		public double getPrice(){
			return dish.getPrice() + getAdjustPrice();
		}
		public double getAdjustPrice(){
	        if (configs == null || configs.isEmpty())
	            return 0;
	        double ap = 0;
	        for (int i = 0; i < configs.size(); i++) {
	            ap += configs.get(i).getPrice();
	        }
	        return ap;
	    }
	}
	
	class Category2Label extends JLabel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Category2 c2;
		public Category2Label(Category2 category2){
			this.c2 = category2;
			setText(c2.getFirstLanguageName());
			setOpaque(true);
			setHorizontalAlignment(JLabel.CENTER);
			addMouseListener(new MouseAdapter(){

				@Override
				public void mousePressed(MouseEvent e) {
					
					generateDishPanel(c2, 0);
				}
				
			});
			setBorder(BorderFactory.createLineBorder(Color.gray));
			Dimension d = this.getPreferredSize();
			double width = d.getWidth();
			if (width < 100)
				width = 100;
			d.setSize(width, 50);
			this.setPreferredSize(d);
		}
	}
	
	class DishLabel extends JLabel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Dish dish;
		
		public DishLabel(Dish d){
			dish = d;
			setText(d.getFirstLanguageName());
			setHorizontalAlignment(JLabel.CENTER);
			setBorder(BorderFactory.createLineBorder(Color.gray));
			addMouseListener(new MouseAdapter(){

				@Override
				public void mousePressed(MouseEvent e) {
					
					doDishLabelPress(dish);
				}
				
			});
			setPreferredSize(buttonsize);
		}
	}
	
	class Category2NextPageLabel extends JLabel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public Category2NextPageLabel(final int nextPosition){
			if (imgNextPage != null)
				setIcon(new ImageIcon(imgNextPage));
			setHorizontalAlignment(JLabel.CENTER);
			addMouseListener(new MouseAdapter(){

				@Override
				public void mousePressed(MouseEvent e) {
					
					generateCategory2Panel(nextPosition);
				}
				
			});
			setBorder(BorderFactory.createLineBorder(Color.gray));
			Dimension d = this.getPreferredSize();
			double width = d.getWidth();
			if (width < 100)
				width = 100;
			d.setSize(width, 50);
			this.setPreferredSize(d);
		}
	}
	
	class Category2PrePageLabel extends JLabel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Category2PrePageLabel(final int startPosition){
			if (imgPrePage != null)
				setIcon(new ImageIcon(imgPrePage));
			setHorizontalAlignment(JLabel.CENTER);
			addMouseListener(new MouseAdapter(){

				@Override
				public void mousePressed(MouseEvent e) {
					
					generateCategory2Panel(startPosition);
				}
				
			});
			setBorder(BorderFactory.createLineBorder(Color.gray));
			Dimension d = this.getPreferredSize();
			double width = d.getWidth();
			if (width < 100)
				width = 100;
			d.setSize(width, 50);
			this.setPreferredSize(d);
		}
	}
	
	class DishNextPageLabel extends JLabel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public DishNextPageLabel(final Category2 c2, final int nextPosition){
			if (imgNextPage != null)
				setIcon(new ImageIcon(imgNextPage));
			setHorizontalAlignment(JLabel.CENTER);
			addMouseListener(new MouseAdapter(){

				@Override
				public void mousePressed(MouseEvent e) {
					
					generateDishPanel(c2, nextPosition);
				}
				
			});
			setBorder(BorderFactory.createLineBorder(Color.gray));
			Dimension d = this.getPreferredSize();
			double width = d.getWidth();
			if (width < 100)
				width = 100;
			d.setSize(width, 50);
			this.setPreferredSize(d);
		}
	}
	
	class DishPrePageLabel extends JLabel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DishPrePageLabel(final Category2 c2, final int startPosition){
			if (imgPrePage != null)
				setIcon(new ImageIcon(imgPrePage));
			setHorizontalAlignment(JLabel.CENTER);
			addMouseListener(new MouseAdapter(){

				@Override
				public void mousePressed(MouseEvent e) {
					
					generateDishPanel(c2, startPosition);
				}
				
			});
			setBorder(BorderFactory.createLineBorder(Color.gray));
			Dimension d = this.getPreferredSize();
			double width = d.getWidth();
			if (width < 100)
				width = 100;
			d.setSize(width, 50);
			this.setPreferredSize(d);
		}
	}
	
	class ListModel<ChoosedDish> extends DefaultListModel<ChoosedDish>{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void refreshData(ChoosedDish cd, int start, int end){
			super.fireContentsChanged(cd, start, end);
		}
	}
	private final static Dimension buttonsize = new Dimension(180, 50);
}
