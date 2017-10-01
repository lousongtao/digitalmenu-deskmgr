package com.shuishou.deskmgr.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.deskmgr.ConstantValue;
import com.shuishou.deskmgr.Messages;
import com.shuishou.deskmgr.beans.Category1;
import com.shuishou.deskmgr.beans.Category2;
import com.shuishou.deskmgr.beans.CurrentDutyInfo;
import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.beans.DeskWithIndent;
import com.shuishou.deskmgr.beans.DiscountTemplate;
import com.shuishou.deskmgr.beans.Dish;
import com.shuishou.deskmgr.beans.HttpResult;
import com.shuishou.deskmgr.beans.Indent;
import com.shuishou.deskmgr.beans.UserData;
import com.shuishou.deskmgr.http.HttpUtil;

public class MainFrame extends JFrame implements ActionListener{
	private final Logger logger = Logger.getLogger(MainFrame.class.getName());
	private int deskColumnAmount = 6;
	private Properties properties = null;
	public static String SERVER_URL;
	private JPanel pDeskArea = null;
	private JLabel lbStatusLogin = new JLabel();
	private JLabel lbStatusDesks = new JLabel();
	private JButton btnOpenDesk = new JButton(Messages.getString("MainFrame.OpenDesk")); //$NON-NLS-1$
	private JButton btnAddDish = new JButton(Messages.getString("MainFrame.AddDish")); //$NON-NLS-1$
	private JButton btnViewIndent = new JButton(Messages.getString("MainFrame.ViewIndent")); //$NON-NLS-1$
	private JButton btnCheckout = new JButton(Messages.getString("MainFrame.Checkout")); //$NON-NLS-1$
	private JButton btnMergeDesk = new JButton(Messages.getString("MainFrame.MergeDesk")); //$NON-NLS-1$
	private JButton btnCleatDesk = new JButton(Messages.getString("MainFrame.ClearDesk")); //$NON-NLS-1$
	private JButton btnPrintTicket = new JButton(Messages.getString("MainFrame.PrintTicket")); //$NON-NLS-1$
	private JButton btnShiftWork = new JButton(Messages.getString("MainFrame.ShiftWork")); //$NON-NLS-1$
	private JButton btnRefresh = new JButton(Messages.getString("MainFrame.Refresh")); //$NON-NLS-1$
	
	private ArrayList<Desk> deskList = new ArrayList<>();
	private ArrayList<DiscountTemplate> discountTemplateList = new ArrayList<>(); 
	private ArrayList<DeskCell> deskcellList = new ArrayList<>();
	private ArrayList<Category1> category1List = new ArrayList<>();
	private UserData loginUser = null;
	private UserData onDutyUser = null;//在值班状态用户名称
	private String confirmCode = null;
	
	private Gson gson = new Gson();
	
	public MainFrame(String serverUrl){
		SERVER_URL = serverUrl;
		initUI();
	}
	
	public void setDeskColumnAmount(int deskColumnAmount) {
		this.deskColumnAmount = deskColumnAmount;
	}

	public UserData getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(UserData loginUser) {
		this.loginUser = loginUser;
	}

	public List<DiscountTemplate> getDiscountTemplateList() {
		return discountTemplateList;
	}

	public void setDiscountTemplateList(ArrayList<DiscountTemplate> discountTemplateList) {
		this.discountTemplateList = discountTemplateList;
	}

	public void startLogin(){
		LoginDialog dlg = new LoginDialog(this);
		dlg.setVisible(true);
	}
	
	public void initUI(){
		pDeskArea = new JPanel(new GridBagLayout());
		JScrollPane jspDeskArea = new JScrollPane(pDeskArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JPanel pFunction = new JPanel(new GridBagLayout());
		int row = 0;
		pFunction.add(btnOpenDesk, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10,0,0,0),0,0));
		pFunction.add(btnAddDish, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10,0,0,0),0,0));
		pFunction.add(btnViewIndent, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10,0,0,0),0,0));
		pFunction.add(btnCheckout, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10,0,0,0),0,0));
		pFunction.add(btnMergeDesk, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10,0,0,0),0,0));
		pFunction.add(btnCleatDesk, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10,0,0,0),0,0));
		pFunction.add(btnPrintTicket, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10,0,0,0),0,0));
		pFunction.add(btnRefresh, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10,0,0,0),0,0));
		pFunction.add(btnShiftWork, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10,0,0,0),0,0));
		
		pFunction.setPreferredSize(new Dimension(180, 0));
		
		btnOpenDesk.addActionListener(this);
		btnAddDish.addActionListener(this);
		btnViewIndent.addActionListener(this);
		btnCheckout.addActionListener(this);
		btnMergeDesk.addActionListener(this);
		btnCleatDesk.addActionListener(this);
		btnPrintTicket.addActionListener(this);
		btnRefresh.addActionListener(this);
		btnShiftWork.addActionListener(this);
		
		
		JPanel pStatus = new JPanel(new GridLayout(1, 2, 2, 0));
		pStatus.add(lbStatusLogin);
		lbStatusLogin.setBorder(BorderFactory.createLineBorder(Color.gray));
		pStatus.add(lbStatusDesks);
		lbStatusDesks.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		this.getContentPane().setLayout(new BorderLayout(5, 5));
		this.getContentPane().add(jspDeskArea, BorderLayout.CENTER);
		this.getContentPane().add(pFunction, BorderLayout.EAST);
		this.getContentPane().add(pStatus, BorderLayout.SOUTH);
		
		loadDesks();
		loadShiftDutyInfo();
		loadDiscountTemplates();
		loadMenu();
		loadConfirmCode();
		initRefreshTimer();
		buildDeskCells();
	}
	
	private void initRefreshTimer(){
		Timer timer = new Timer();
		timer.schedule(new java.util.TimerTask(){

			@Override
			public void run() {
				loadCurrentIndentInfo();
			}}, 0, 60*1000);
	}
	
	/**
	 * this class just hold Category1 objects. if need dish object, please loop into the category1 objects
	 */
	private void loadMenu(){
		String url = "menu/querymenu";
		String response = HttpUtil.getJSONObjectByGet(SERVER_URL + url);
		if (response == null){
			logger.error("get null from server for loading menu. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for loading menu. URL = " + url);
			return;
		}
		HttpResult<ArrayList<Category1>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Category1>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading menu. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while loading menu. URL = " + url);
			return;
		}
		category1List = result.data;
	}
	
	private void loadConfirmCode(){
		String url = "common/getconfirmcode";
		String response = HttpUtil.getJSONObjectByGet(SERVER_URL + url);
		if (response == null){
			logger.error("get null from server for getting confirm code. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for getting confirm code. URL = " + url);
			return;
		}
		HttpResult<String> result = gson.fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
		if (!result.success){
			logger.error("return false while getting confirm code. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while getting confirm code. URL = " + url);
			return;
		}
		confirmCode = result.data;
	}
	
	private void loadShiftDutyInfo(){
		String url = "management/getcurrentduty";
		String response = HttpUtil.getJSONObjectByGet(SERVER_URL + url);
		if (response == null){
			logger.error("get null from server for duty employee info. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for duty employee info. URL = " + url);
			return;
		}
		HttpResult<CurrentDutyInfo> result = gson.fromJson(response, new TypeToken<HttpResult<CurrentDutyInfo>>(){}.getType());
		if (!result.success){
			logger.error("return false while get duty employee info. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while get duty employee info. URL = " + url);
			return;
		}
		if (result.data != null){
			Date startTime = null;
			try {
				startTime = ConstantValue.DFYMDHMS.parse(result.data.startTime);
			} catch (Exception e) {
				logger.error("get wrong format of on duty date, URL = " + url + ", returnjson = " + response);
			}
			onDutyUser = new UserData(result.data.currentDutyId, result.data.currentDutyName, startTime);
			lbStatusLogin.setText(Messages.getString("MainFrame.currentDutyWorker") + onDutyUser.getName() //$NON-NLS-1$
					+ Messages.getString("MainFrame.DutyStartTime") + result.data.startTime); //$NON-NLS-1$
		}
		
	}
	
	private void loadDiscountTemplates(){
		String url = "common/getdiscounttemplates";
		String response = HttpUtil.getJSONObjectByGet(SERVER_URL + url);
		if (response == null){
			logger.error("get null from server for discount templates. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for discount templates. URL = " + url);
			return;
		}
		HttpResult<ArrayList<DiscountTemplate>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<DiscountTemplate>>>(){}.getType());
		if (!result.success){
			logger.error("return false while get discount templates. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while get discount templates. URL = " + url);
			return;
		}
		discountTemplateList.clear();
		discountTemplateList.addAll(result.data);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void buildDeskCells(){
		pDeskArea.removeAll();
		deskcellList.clear();
		
		for (int i = 0; i < deskList.size(); i++) {
			DeskCell dc = new DeskCell(deskList.get(i));
			pDeskArea.add(dc, new GridBagConstraints(i % deskColumnAmount, (int)(i / deskColumnAmount), 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0),0,0));
			deskcellList.add(dc);
		}
		pDeskArea.updateUI();
	}
	
	/**
	 * load desk info, attach the merge table info. after loading, need reset the desk object into deskcell.
	 */
	public void loadDesks(){
		String url = "common/getdesks";
		String response = HttpUtil.getJSONObjectByGet(SERVER_URL + url);
		if (response == null){
			logger.error("get null from server for desks list. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for desks list. URL = " + url);
			return;
		}
		HttpResult<ArrayList<Desk>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Desk>>>(){}.getType());
		if (!result.success){
			logger.error("return false while get desks. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while get desks. URL = " + url);
			return;
		}
		deskList.clear();
		deskList.addAll(result.data);
		Collections.sort(deskList, new Comparator(){

			@Override
			public int compare(Object o1, Object o2) {
				return ((Desk)o1).getId() - ((Desk)o2).getId();
			}});
		//rebind desk objects to deskcell objects
		for(DeskCell dc : deskcellList){
			for(Desk desk: deskList){
				if (desk.getId() == dc.getDesk().getId()){
					dc.setDesk(desk);
					break;
				}
			}
		}
	}
	
	/**
	 * load the unpaid indent info, attaching the intenddetail info.
	 */
	public void loadCurrentIndentInfo(){
		if (loginUser == null)
			return;
		String url = "indent/queryindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("status", "Unpaid");
		String response = HttpUtil.getJSONObjectByPost(SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for query indent error. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for query indent info. URL = " + url);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();
		HttpResult<ArrayList<Indent>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Indent>>>(){}.getType());
		if (!result.success){
			logger.error("return false while get desks with indents. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while get desks with indents. URL = " + url);
			return;
		}
		for (int i = 0; i < deskcellList.size(); i++) {
			DeskCell dc = deskcellList.get(i);
			dc.setIndent(null);
			for (int j = 0; j < result.data.size(); j++) {
				Indent indent = result.data.get(j);
				if (indent.getDeskName().equals(dc.getDesk().getName())){
					dc.setIndent(indent);
					break;
				}
			}
		}
		
		refreshDeskStatus();
	}
	
	private void refreshDeskStatus() {
		// refresh status
		int openDesk = 0;
		int customers = 0;
		for (DeskCell dc : deskcellList) {
			if (dc.getIndent() != null) {
				openDesk++;
				customers += dc.getIndent().getCustomerAmount();
			}
			dc.setMergeTo(dc.getDesk().getMergeTo());
		}
		lbStatusDesks.setText(Messages.getString("MainFrame.OpenedTables") + openDesk + Messages.getString("MainFrame.CurrentCustomers") + customers); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void doOnDuty(int userId){
		String url = "management/startshiftwork";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userId+"");
		String response = HttpUtil.getJSONObjectByPost(SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for starting shiftwork. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for starting shiftwork. URL = " + url + ", param = "+ params);
			return;
		}
		HttpResult<CurrentDutyInfo> result = gson.fromJson(response, new TypeToken<HttpResult<CurrentDutyInfo>>(){}.getType());
		if (!result.success){
			logger.error("return false while starting shiftwork. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while starting shiftwork. URL = " + url);
			return;
		}
		Date startTime = null;
		try {
			startTime = ConstantValue.DFYMDHMS.parse(result.data.startTime);
		} catch (ParseException e) {
			logger.error("get wrong format of on duty date, URL = " + url + ", param = " + params +", returnjson = "+response);
		}
		onDutyUser = new UserData(result.data.currentDutyId, result.data.currentDutyName, startTime);
		lbStatusLogin.setText(Messages.getString("MainFrame.currentDutyWorker") + result.data.currentDutyName  //$NON-NLS-1$
			+ Messages.getString("MainFrame.DutyStartTime") + result.data.startTime); //$NON-NLS-1$
	}
	
	public void doOffDuty(UserData user, boolean print){
		String url = "management/endshiftwork";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", user.getId()+"");
		params.put("printShiftTicket", Boolean.valueOf(print).toString());
		params.put("startTime", ConstantValue.DFYMDHMS.format(user.getStartTime()));
		String response = HttpUtil.getJSONObjectByPost(SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server while off duty. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while off duty. URL = " + url + ", param = "+ params);
			return;
		}
		HttpResult<CurrentDutyInfo> result = gson.fromJson(response, new TypeToken<HttpResult<CurrentDutyInfo>>(){}.getType());
		if (!result.success){
			logger.error("return false while end shiftwork. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while end shiftwork. URL = " + url);
			return;
		}
		
		onDutyUser = null;
		lbStatusLogin.setText("");

		startLogin();
	}
	
	public UserData getOnDutyUser() {
		return onDutyUser;
	}

	public List<DeskCell> getSelectedDesks(){
		ArrayList<DeskCell> dcs = new ArrayList<DeskCell>();
		for(DeskCell dc : deskcellList){
			if (dc.isSelected())
				dcs.add(dc);
		}
		return dcs;
	}
	
	public void cleanTable(Desk desk){
		for(DeskCell dc : deskcellList){
			if (dc.getDesk().getId() == desk.getId()){
				dc.setLastIndentId(dc.getIndent().getId());
				dc.setIndentInfo(null);
				break;
			}
		}
		refreshDeskStatus();
	}
	
	private void doClearDesk(){
		if (onDutyUser == null){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.NobodyOndutyMsg")); //$NON-NLS-1$
			return;
		}
		List<DeskCell> selectDC = getSelectedDesks();
		if (selectDC.size() > 1){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.SelectOverONETable"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		int deskid = selectDC.get(0).getDesk().getId();
		
		String url = "indent/cleardesk";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", onDutyUser.getId()+"");
		params.put("deskId", deskid+"");
		String response = HttpUtil.getJSONObjectByPost(SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server while clear desks failed. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while clear desks failed. URL = " + url + ", param = "+ params);
			return;
		}
		HttpResult<String> result = gson.fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
		if (!result.success){
			logger.error("return false while clean table. URL = " + url);
			JOptionPane.showMessageDialog(this, "return false while clean table. URL = " + url);
			return;
		}
		loadCurrentIndentInfo();
		loadDesks();
		refreshDeskStatus();
	}
	
	private void doMergeTables(){
		if (onDutyUser == null){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.NobodyOndutyMsg")); //$NON-NLS-1$
			return;
		}
		List<DeskCell> selectDC = getSelectedDesks();
		if (selectDC.isEmpty() || selectDC.size() < 2)
			return;
		int mainDeskid = selectDC.get(0).getDesk().getId();
		
		String msg = Messages.getString("MainFrame.MergeTableMsg1"); //$NON-NLS-1$
		for(int i = 0; i< selectDC.size(); i++){
			if (i != 0){
				msg += ", ";
			}
			msg += selectDC.get(i).getDesk().getName();
		}
		msg += Messages.getString("MainFrame.MergeTableMsg2"); //$NON-NLS-1$
		if (JOptionPane.showConfirmDialog(this, msg, Messages.getString("MainFrame.MergeTableTitle"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION){ //$NON-NLS-1$
			return;
		}
		String subDeskIds = "";
		for(int i = 1; i< selectDC.size(); i++){
			if (i != 1){
				subDeskIds += "/";
			}
			subDeskIds +=selectDC.get(i).getDesk().getId();
		}
		
		String url = "common/mergedesks";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", onDutyUser.getId()+"");
		params.put("mainDeskId", mainDeskid+"");
		params.put("subDeskId", subDeskIds);
		String response = HttpUtil.getJSONObjectByPost(SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server while do merge desks failed. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while do merge desks failed. URL = " + url + ", param = "+ params);
			return;
		}
		HttpResult<ArrayList<DeskWithIndent>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<DeskWithIndent>>>(){}.getType());
		for (int i = 0; i < deskcellList.size(); i++) {
			DeskCell dc = deskcellList.get(i);
			for (int j = 0; j < result.data.size(); j++) {
				DeskWithIndent di = result.data.get(j);
				if (di.id == dc.getDesk().getId()){
					if (di.indentId == 0){
						dc.setIndentInfo(null);
					} else {
						Indent indent = dc.getIndent();
						if (indent == null){
							indent = new Indent();
						}
						indent.setId(di.indentId);
						indent.setCustomerAmount(di.customerAmount);
						try {
							indent.setStartTime(ConstantValue.DFYMDHMS.parse(di.startTime));
						} catch (JSONException | ParseException e) {
							logger.error("Date format error for indent ID = " + di.id);
						}
						indent.setTotalPrice(di.price);
						dc.setIndentInfo(indent);
					}
					dc.setMergeTo(di.mergeTo);
					break;
				}
			}
		}
		loadDesks();
		refreshDeskStatus();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCheckout){
			List<DeskCell> selectDC = getSelectedDesks();
			if (selectDC.isEmpty())
				return;
			if (selectDC.size() > 1){
				JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.SelectOverONETable"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			if (selectDC.get(0).getIndent() == null)
				return;
			CheckoutDialog dlg = new CheckoutDialog(this, Messages.getString("MainFrame.CheckoutTitle"), true, selectDC.get(0).getDesk(), selectDC.get(0).getIndent()); //$NON-NLS-1$
			dlg.setVisible(true);
		} else if (e.getSource() == btnOpenDesk){
			List<DeskCell> selectDC = getSelectedDesks();
			if (selectDC.isEmpty())
				return;
			if (selectDC.size() > 1){
				JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.SelectOverONETable"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			if (selectDC.get(0).getIndent() != null){
				JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.TableIsUsed"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			OpenTableDialog dlg = new OpenTableDialog(this, Messages.getString("MainFrame.OpenDesk"), true, selectDC.get(0).getDesk(), OpenTableDialog.MAKENEWORDER); //$NON-NLS-1$
			dlg.setVisible(true);
		} else if (e.getSource() == btnAddDish){
			List<DeskCell> selectDC = getSelectedDesks();
			if (selectDC.isEmpty())
				return;
			if (selectDC.size() > 1){
				JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.SelectOverONETable"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			if (selectDC.get(0).getIndent() == null){
				JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.NoIndentOnTable"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			OpenTableDialog dlg = new OpenTableDialog(this, Messages.getString("MainFrame.AddDish"), true, selectDC.get(0).getDesk(), OpenTableDialog.ADDDISH); //$NON-NLS-1$
			dlg.setVisible(true);
		} else if (e.getSource() == btnViewIndent){
			List<DeskCell> selectDC = getSelectedDesks();
			if (selectDC.isEmpty())
				return;
			if (selectDC.size() > 1){
				JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.SelectOverONETable"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			if (selectDC.get(0).getIndent() == null){
				JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.NoIndentOnTable"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			ViewIndentDialog dlg = new ViewIndentDialog(this, Messages.getString("MainFrame.ViewIndent"), true, selectDC.get(0).getDesk(), selectDC.get(0).getIndent()); //$NON-NLS-1$
			dlg.setVisible(true);
		} else if (e.getSource() == btnPrintTicket){
			
		} else if (e.getSource() == btnMergeDesk){
			doMergeTables();
		} else if (e.getSource() == btnCleatDesk){
			doClearDesk();
		} else if (e.getSource() == btnRefresh){
			loadCurrentIndentInfo();
		} else if (e.getSource() == btnShiftWork){
			if(onDutyUser == null && loginUser != null){
				String msg = Messages.getString("LoginDialog.NoDutyMsg") +loginUser.getName(); //$NON-NLS-1$
				if (JOptionPane.showConfirmDialog(this, msg, Messages.getString("LoginDialog.OnDutyTitle"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){ //$NON-NLS-1$
					doOnDuty(loginUser.getId());
				}
			} else {
				String msg = Messages.getString("MainFrame.OffDutyMsg") + onDutyUser.getName(); //$NON-NLS-1$
				Object[] options = {"ShiftWork", "ShiftWork & Print", "Close"};
				int n = JOptionPane.showOptionDialog(this, msg, Messages.getString("MainFrame.ShiftWorkTitle"), 
						JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (n == 0){ 
					doOffDuty(onDutyUser, false);
				} else if (n == 1){
					doOffDuty(onDutyUser, true);
				}
			}
		}
	}
	
	public ArrayList<Dish> getAllDishes(){
		ArrayList<Dish> dishes = new ArrayList<>();
		for(Category1 c1 : this.category1List){
			for(Category2 c2 : c1.getCategory2s()){
				dishes.addAll(c2.getDishes());
			}
		}
		return dishes;
	}
	
	public String getConfirmCode(){
		return confirmCode;
	}
	
	public static void main(String[] args){
		//load properties
		Properties prop = new Properties();
		InputStream input = null;
		try {
//			input = new FileInputStream("config.properties");
			input = MainFrame.class.getClassLoader().getResourceAsStream("config.properties");
			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Messages.initResourceBundle(prop.getProperty("language"));
		try {
//			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); //java 格式
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//windows 格式
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");//Windows 格式
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		Enumeration enums = UIManager.getDefaults().keys();
		while(enums.hasMoreElements()){
			Object key = enums.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof Font){
				UIManager.put(key, ConstantValue.FONT_20PLAIN);
			}
		}
		MainFrame f = new MainFrame(prop.getProperty("SERVER_URL"));
		
		f.setSize(Integer.parseInt(prop.getProperty("mainframe.width")), Integer.parseInt(prop.getProperty("mainframe.height")));
		f.setDeskColumnAmount(Integer.parseInt(prop.getProperty("onelinetables")));
		f.setLocation(0, 0);
		f.setTitle(Messages.getString("MainFrame.FrameTitle")); //$NON-NLS-1$
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.startLogin();
	}
}
