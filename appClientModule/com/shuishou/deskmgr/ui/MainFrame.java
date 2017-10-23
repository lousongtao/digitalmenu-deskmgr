package com.shuishou.deskmgr.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
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
import com.shuishou.deskmgr.ui.components.IconButton;
import com.shuishou.deskmgr.ui.components.JBlockedButton;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class MainFrame extends JFrame implements ActionListener{
	public final static Logger logger = Logger.getLogger(MainFrame.class.getName());
	public static int DESK_COLUMN_AMOUNT;
	public static int TABLECELL_WIDTH;
	public static int TABLECELL_HEIGHT;
	public static int WINDOW_WIDTH;
	public static int WINDOW_HEIGHT;
	public static int WINDOW_LOCATIONX;
	public static int WINDOW_LOCATIONY;
	public static String language;
	public static String SERVER_URL;
	private OutputStream outputStreamCashdrawer;
	public static String portCashdrawer;
	
	private JPanel pDeskArea = null;
	private JLabel lbStatusLogin = new JLabel();
	private JLabel lbStatusDesks = new JLabel();
	private IconButton btnOpenDesk = new IconButton(Messages.getString("MainFrame.OpenDesk"), "/resource/opentable.png"); //$NON-NLS-1$
	private IconButton btnAddDish = new IconButton(Messages.getString("MainFrame.AddDish"), "/resource/adddish.png"); //$NON-NLS-1$
	private IconButton btnViewIndent = new IconButton(Messages.getString("MainFrame.ViewIndent"), "/resource/viewindent.png"); //$NON-NLS-1$
	private IconButton btnCheckout = new IconButton(Messages.getString("MainFrame.Checkout"), "/resource/pay.png"); //$NON-NLS-1$
	private IconButton btnChangeDesk = new IconButton(Messages.getString("MainFrame.ChangeDesk"), "/resource/changedesk.png"); //$NON-NLS-1$
	private JBlockedButton btnOpenCashdrawer = new JBlockedButton(Messages.getString("MainFrame.OpenCashdrawer"), "/resource/cashdrawer.png"); //$NON-NLS-1$
	private IconButton btnMergeDesk = new IconButton(Messages.getString("MainFrame.MergeDesk"), "/resource/mergedesk.png"); //$NON-NLS-1$
	private IconButton btnClearDesk = new IconButton(Messages.getString("MainFrame.ClearDesk"), "/resource/cleardesk.png"); //$NON-NLS-1$
	private IconButton btnPrintTicket = new IconButton(Messages.getString("MainFrame.PrintTicket"), "/resource/printer.png"); //$NON-NLS-1$
	private IconButton btnShiftWork = new IconButton(Messages.getString("MainFrame.ShiftWork"), "/resource/swiftwork.png"); //$NON-NLS-1$
	private JBlockedButton btnRefresh = new JBlockedButton(Messages.getString("MainFrame.Refresh"), "/resource/refresh.png"); //$NON-NLS-1$
	
	private ArrayList<Desk> deskList = new ArrayList<>();
	private ArrayList<DiscountTemplate> discountTemplateList = new ArrayList<>(); 
	private ArrayList<DeskCell> deskcellList = new ArrayList<>();
	private ArrayList<Category1> category1List = new ArrayList<>();
//	private UserData loginUser = null;
	private UserData onDutyUser = null;//在值班状态用户名称
	private String confirmCode = null;
	
	private Gson gson = new Gson();
	private int gapButtons = 5; // the gap between buttons for top
	
	
	public MainFrame(){
		initUI();
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLocation(WINDOW_LOCATIONX, WINDOW_LOCATIONY);
		setTitle(Messages.getString("MainFrame.FrameTitle")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		Color colorBackground = new Color(235, 255, 244);
		pDeskArea = new JPanel(new GridBagLayout());
		JScrollPane jspDeskArea = new JScrollPane(pDeskArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pDeskArea.setBackground(colorBackground);
		JPanel pFunction = new JPanel(new GridBagLayout());
		pFunction.setBackground(colorBackground);
		
		
		int row = 0;
		Insets insets = new Insets(gapButtons,0,0,0);
		pFunction.add(btnOpenDesk, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnAddDish, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnViewIndent, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnCheckout, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnChangeDesk, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnMergeDesk, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnClearDesk, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnPrintTicket, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnRefresh, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnShiftWork, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnOpenCashdrawer, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.setPreferredSize(new Dimension(180, 0));
		
		btnOpenDesk.addActionListener(this);
		btnAddDish.addActionListener(this);
		btnViewIndent.addActionListener(this);
		btnCheckout.addActionListener(this);
		btnOpenCashdrawer.addActionListener(this);
		btnChangeDesk.addActionListener(this);
		btnMergeDesk.addActionListener(this);
		btnClearDesk.addActionListener(this);
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
//		loadCurrentIndentInfo();
		
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
		if (result.data != null && result.data.currentDutyId > 0){
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
			DeskCell dc = new DeskCell(this, deskList.get(i));
			pDeskArea.add(dc, new GridBagConstraints(i % DESK_COLUMN_AMOUNT, (int)(i / DESK_COLUMN_AMOUNT), 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0),0,0));
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
		if (onDutyUser == null)
			return;
		String url = "indent/queryindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("status", "Unpaid");
		String response = HttpUtil.getJSONObjectByPost(SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
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
			if (result.data != null){
				for (int j = 0; j < result.data.size(); j++) {
					Indent indent = result.data.get(j);
					if (indent.getDeskName().equals(dc.getDesk().getName())){
						dc.setIndent(indent);
						break;
					}
				}
			}
			
		}
		refreshDeskStatus();
	}
	
	public void refreshDeskStatus() {
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
	
	public void doOnDuty(int userId, boolean printLastDutyTicket){
		String url = "management/startshiftwork";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", String.valueOf(userId));
		params.put("printLastDutyTicket", String.valueOf(printLastDutyTicket));
		String response = HttpUtil.getJSONObjectByPost(SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
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
		params.put("userId", String.valueOf(user.getId()));
		params.put("printShiftTicket", Boolean.valueOf(print).toString());
		params.put("startTime", ConstantValue.DFYMDHMS.format(user.getStartTime()));
		String response = HttpUtil.getJSONObjectByPost(SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
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

	public DeskCell getSelectedDesk(){
//		ArrayList<DeskCell> dcs = new ArrayList<DeskCell>();
		for(DeskCell dc : deskcellList){
			if (dc.isSelected())
				return dc;
		}
		return null;
	}
	
	
	
	public ArrayList<DeskCell> getDeskcellList() {
		return deskcellList;
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
//		List<DeskCell> selectDC = getSelectedDesks();
//		if (selectDC.size() > 1){
//			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.SelectOverONETable"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
//			return;
//		}
		DeskCell selectDC = getSelectedDesk();
		if (selectDC == null)
			return;
		if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, Messages.getString("MainFrame.ConfirmForClearDesk"), Messages.getString("MainFrame.confirm"), JOptionPane.YES_NO_OPTION))
			return;
		int deskid = selectDC.getDesk().getId();
		
		String url = "indent/cleardesk";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", String.valueOf(onDutyUser.getId()));
		params.put("deskId", String.valueOf(deskid));
		String response = HttpUtil.getJSONObjectByPost(SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
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
		
		MergeDeskDialog dlg = new MergeDeskDialog(this, Messages.getString("MainFrame.MergeTableTitle"), deskList);
		dlg.setVisible(true);
		
		if (!dlg.isConfirm){
			return;
		}
		
		List<MergeDeskDialog.DeskCell> selectDC = dlg.getSelectDesks();
		
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
		params.put("userId", String.valueOf(onDutyUser.getId()));
		params.put("mainDeskId", String.valueOf(mainDeskid));
		params.put("subDeskId", subDeskIds);
		String response = HttpUtil.getJSONObjectByPost(SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
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
			doCheckout();
		} else if (e.getSource() == btnOpenDesk){
			doOpenDesk();
		} else if (e.getSource() == btnAddDish){
			doAddDish();
		} else if (e.getSource() == btnViewIndent){
			doViewIndent();
		} else if (e.getSource() == btnPrintTicket){
			
		} else if (e.getSource() == btnMergeDesk){
			doMergeTables();
		} else if (e.getSource() == btnClearDesk){
			doClearDesk();
		} else if (e.getSource() == btnRefresh){
			loadCurrentIndentInfo();
		} else if (e.getSource() == btnShiftWork){
			doSwiftWork();
		} else if (e.getSource() == btnChangeDesk){
			doChangeDesk();
		} else if (e.getSource() == btnOpenCashdrawer){
			doOpenCashdrawer();
		}
	}
	
	public void doOpenCashdrawer(){
		if (outputStreamCashdrawer == null){
			Enumeration portList = CommPortIdentifier.getPortIdentifiers();
			while (portList.hasMoreElements()) {
				CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					if (portId.getName().equals(portCashdrawer)) {
						try {
							SerialPort serialPort = (SerialPort) portId.open("SimpleWriteApp", 2000);
							outputStreamCashdrawer = serialPort.getOutputStream();
							serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
						} catch (PortInUseException | IOException | UnsupportedCommOperationException e) {
							logger.error(e);
						}
						break;
					}
				}
			}
		}
		try {
			outputStreamCashdrawer.write("A".getBytes());// any string is ok
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	private void doChangeDesk(){
		DeskCell selectDC = getSelectedDesk();
		if (selectDC == null)
			return;
		if (selectDC.getIndent() == null){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.NeedSelectOneOccupied"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		ArrayList<Desk> availableDesks = new ArrayList<>();
		for(int i = 0; i < deskcellList.size(); i++){
			if (deskcellList.get(i).getIndent() == null && deskcellList.get(i).getDesk().getMergeTo() == null)
				availableDesks.add(deskcellList.get(i).getDesk());
		}
		ChangeDeskDialog dlg = new ChangeDeskDialog(this, Messages.getString("MainFrame.ChangeDesk"), selectDC.getDesk(), availableDesks);
		dlg.setVisible(true);
	}
	
	private void doCheckout(){
		DeskCell selectDC = getSelectedDesk();
		if (selectDC == null)
			return;

		if (selectDC.getIndent() == null)
			return;
		CheckoutDialog dlg = new CheckoutDialog(this, Messages.getString("MainFrame.CheckoutTitle"), true, selectDC.getDesk(), selectDC.getIndent()); //$NON-NLS-1$
		dlg.setVisible(true);
	}
	
	private void doOpenDesk(){
		DeskCell selectDC = getSelectedDesk();
		if (selectDC == null)
			return;
		if (selectDC.getIndent() != null){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.TableIsUsed"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		if (selectDC.getDesk().getMergeTo() != null){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.TableIsUsed"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		OpenTableDialog dlg = new OpenTableDialog(this, Messages.getString("MainFrame.OpenDesk"), true, selectDC.getDesk(), OpenTableDialog.MAKENEWORDER); //$NON-NLS-1$
		dlg.setVisible(true);
	}
	
	private void doAddDish(){
		DeskCell selectDC = getSelectedDesk();
		if (selectDC == null)
			return;
		if (selectDC.getIndent() == null){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.NoIndentOnTable"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		OpenTableDialog dlg = new OpenTableDialog(this, Messages.getString("MainFrame.AddDish"), true, selectDC.getDesk(), OpenTableDialog.ADDDISH); //$NON-NLS-1$
		dlg.setVisible(true);
	}
	
	private void doViewIndent(){
		DeskCell selectDC = getSelectedDesk();
		if (selectDC == null)
			return;
		if (selectDC.getIndent() == null){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.NoIndentOnTable"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		ViewIndentDialog dlg = new ViewIndentDialog(this, Messages.getString("MainFrame.ViewIndent"), true, selectDC.getDesk(), selectDC.getIndent()); //$NON-NLS-1$
		dlg.setVisible(true);
	}

	/**
	 * if there is no duty user currently, do nother
	 * if there is a duty user, as whether print the swift ticket.
	 */
	public void doSwiftWork() {
		if (onDutyUser == null) {
//			String msg = Messages.getString("LoginDialog.NoDutyMsg") + loginUser.getName(); //$NON-NLS-1$
//			if (JOptionPane.showConfirmDialog(this, msg, Messages.getString("LoginDialog.OnDutyTitle"), //$NON-NLS-1$
//					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//				doOnDuty(loginUser.getId());
//			}
		} else {
			String msg = Messages.getString("MainFrame.OffDutyMsg") + onDutyUser.getName(); //$NON-NLS-1$
			Object[] options = { Messages.getString("MainFrame.ShiftWork"),
					Messages.getString("MainFrame.ShiftWorkPrint"), Messages.getString("MainFrame.Cancel") };
			int n = JOptionPane.showOptionDialog(this, msg, Messages.getString("MainFrame.ShiftWorkTitle"),
					JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (n == 0) {
				doOffDuty(onDutyUser, false);
			} else if (n == 1) {
				doOffDuty(onDutyUser, true);
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
	
	public ArrayList<Category2> getAllCategory2s(){
		ArrayList<Category2> c2s = new ArrayList<>();
		for(Category1 c1 : this.category1List){
			c2s.addAll(c1.getCategory2s());
		}
		return c2s;
	}
	
	public String getConfirmCode(){
		return confirmCode;
	}
	
	public static void main(String[] args){
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				MainFrame.logger.error("", e);
			}
		});
		//load properties
		Properties prop = new Properties();
		InputStream input = null;
		try {
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
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//windows 格式
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
		MainFrame.SERVER_URL = prop.getProperty("SERVER_URL");
		MainFrame.DESK_COLUMN_AMOUNT = Integer.parseInt(prop.getProperty("onelinetables"));
		MainFrame.TABLECELL_WIDTH = Integer.parseInt(prop.getProperty("tablecell.width"));
		MainFrame.TABLECELL_HEIGHT = Integer.parseInt(prop.getProperty("tablecell.height"));
		MainFrame.WINDOW_WIDTH = Integer.parseInt(prop.getProperty("mainframe.width"));
		MainFrame.WINDOW_HEIGHT = Integer.parseInt(prop.getProperty("mainframe.height"));
		MainFrame.WINDOW_LOCATIONX = Integer.parseInt(prop.getProperty("mainframe.locationx"));
		MainFrame.WINDOW_LOCATIONY = Integer.parseInt(prop.getProperty("mainframe.locationy"));
		MainFrame.language = prop.getProperty("language");
		MainFrame.portCashdrawer=prop.getProperty("portCashdrawer");
		MainFrame f = new MainFrame();
		f.setVisible(true);
		f.startLogin();
	}
}
