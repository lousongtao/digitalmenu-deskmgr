package com.shuishou.deskmgr.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
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
import com.shuishou.deskmgr.beans.Flavor;
import com.shuishou.deskmgr.beans.HttpResult;
import com.shuishou.deskmgr.beans.Indent;
import com.shuishou.deskmgr.beans.Member;
import com.shuishou.deskmgr.beans.PayWay;
import com.shuishou.deskmgr.beans.UserData;
import com.shuishou.deskmgr.http.HttpUtil;
import com.shuishou.deskmgr.printertool.PrintThread;
import com.shuishou.deskmgr.ui.components.IconButton;
import com.shuishou.deskmgr.ui.components.JBlockedButton;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class MainFrame extends JFrame implements ActionListener{
	public final static Logger logger = Logger.getLogger(MainFrame.class.getName());
	
	private OutputStream outputStreamCashdrawer;
	
	private JPanel pDeskArea = null;
	private JLabel lbStatusLogin = new JLabel();
	private JLabel lbStatusDesks = new JLabel();
	private JLabel lbCurrentTime = new JLabel();
	private IconButton btnOpenDesk = new IconButton(Messages.getString("MainFrame.OpenDesk"), "/resource/opentable.png"); //$NON-NLS-1$
//	private IconButton btnAddDish = new IconButton(Messages.getString("MainFrame.AddDish"), "/resource/adddish.png"); //$NON-NLS-1$
	private IconButton btnViewIndent = new IconButton(Messages.getString("MainFrame.ViewIndent"), "/resource/viewindent.png"); //$NON-NLS-1$
	private IconButton btnCheckout = new IconButton(Messages.getString("MainFrame.Checkout"), "/resource/checkout.png"); //$NON-NLS-1$
	private IconButton btnChangeDesk = new IconButton(Messages.getString("MainFrame.ChangeDesk"), "/resource/changedesk.png"); //$NON-NLS-1$
	private JBlockedButton btnOpenCashdrawer = new JBlockedButton(Messages.getString("MainFrame.OpenCashdrawer"), "/resource/cashdrawer.png"); //$NON-NLS-1$
	private IconButton btnMergeDesk = new IconButton(Messages.getString("MainFrame.MergeDesk"), "/resource/mergedesk.png"); //$NON-NLS-1$
	private IconButton btnClearDesk = new IconButton(Messages.getString("MainFrame.ClearDesk"), "/resource/cleardesk.png"); //$NON-NLS-1$
	private IconButton btnPrintTicket = new IconButton(Messages.getString("MainFrame.PrintTicket"), "/resource/printer.png"); //$NON-NLS-1$
	private IconButton btnShiftWork = new IconButton(Messages.getString("MainFrame.ShiftWork"), "/resource/swiftwork.png"); //$NON-NLS-1$
	private JBlockedButton btnRefresh = new JBlockedButton(Messages.getString("MainFrame.Refresh"), "/resource/refresh.png"); //$NON-NLS-1$
	private IconButton btnMaintainMenu = new IconButton(Messages.getString("MainFrame.MaintainMenu"), "/resource/viewindent.png"); //$NON-NLS-1$
	
	private ArrayList<Desk> deskList = new ArrayList<>();
	private ArrayList<DiscountTemplate> discountTemplateList = new ArrayList<>(); 
	private ArrayList<PayWay> paywayList = new ArrayList<>(); 
	private ArrayList<DeskCell> deskcellList = new ArrayList<>();
	private ArrayList<Category1> category1List = new ArrayList<>();
	private ArrayList<Flavor> flavorList = new ArrayList<>();
	private ArrayList<Member> memberList = new ArrayList<>();
//	private UserData loginUser = null;
	private UserData onDutyUser = null;//在值班状态用户名称
//	private String confirmCode = null;
//	private String openCashdrawerCode = null;
	private HashMap<String, String> configsMap;
	private Gson gson = new Gson();
	private Gson gsonTime = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
	private int gapButtons = 5; // the gap between buttons for top
	
	
	public MainFrame(){
		initUI();
		initData();
//		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLocation(ConstantValue.WINDOW_LOCATIONX, ConstantValue.WINDOW_LOCATIONY);
		setTitle(Messages.getString("MainFrame.FrameTitle")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

	public List<DiscountTemplate> getDiscountTemplateList() {
		return discountTemplateList;
	}

	public void setDiscountTemplateList(ArrayList<DiscountTemplate> discountTemplateList) {
		this.discountTemplateList = discountTemplateList;
	}

	public ArrayList<PayWay> getPaywayList() {
		return paywayList;
	}

	public void setPaywayList(ArrayList<PayWay> paywayList) {
		this.paywayList = paywayList;
	}

	
	public ArrayList<Flavor> getFlavorList() {
		return flavorList;
	}

	public void setFlavorList(ArrayList<Flavor> flavorList) {
		this.flavorList = flavorList;
	}
	
	public ArrayList<Member> getMemberList() {
		return memberList;
	}

	public void startLogin(String userName, String password){
		LoginDialog dlg = new LoginDialog(this);
		dlg.setValue(userName, password);
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
		pFunction.add(btnViewIndent, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnCheckout, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnChangeDesk, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnMergeDesk, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnClearDesk, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnPrintTicket, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnRefresh, 	new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnShiftWork, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnOpenCashdrawer, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnMaintainMenu, new GridBagConstraints(0, row++, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.setPreferredSize(new Dimension(180, 0));
		
		btnOpenDesk.addActionListener(this);
		btnMaintainMenu.addActionListener(this);
		btnViewIndent.addActionListener(this);
		btnCheckout.addActionListener(this);
		btnOpenCashdrawer.addActionListener(this);
		btnChangeDesk.addActionListener(this);
		btnMergeDesk.addActionListener(this);
		btnClearDesk.addActionListener(this);
		btnPrintTicket.addActionListener(this);
		btnRefresh.addActionListener(this);
		btnShiftWork.addActionListener(this);
		
		lbStatusLogin.setBorder(BorderFactory.createLineBorder(Color.gray));
		lbStatusDesks.setBorder(BorderFactory.createLineBorder(Color.gray));
		lbCurrentTime.setBorder(BorderFactory.createLineBorder(Color.gray));
		JPanel pStatus = new JPanel(new GridBagLayout());
		pStatus.add(lbStatusLogin, new GridBagConstraints(0, 0, 1, 1,3,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		pStatus.add(lbStatusDesks, new GridBagConstraints(1, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,3,0,0),0,0));
		pStatus.add(lbCurrentTime, new GridBagConstraints(2, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,3,0,0),0,0));
		
		this.getContentPane().setLayout(new BorderLayout(5, 5));
		this.getContentPane().add(jspDeskArea, BorderLayout.CENTER);
		this.getContentPane().add(pFunction, BorderLayout.EAST);
		this.getContentPane().add(pStatus, BorderLayout.SOUTH);
		
		//start printer thread
        new PrintThread().startThread();
	}
	
	private void initData(){
		loadDesks();
		loadShiftDutyInfo();
		loadDiscountTemplates();
		loadMenu();
		loadConfigsMap();
		loadPayWay();
		loadFlavor();
		loadMember();
		initRefreshTimer();
		buildDeskCells();
	}
	
	private void initRefreshTimer(){
		Timer timer = new Timer();
		timer.schedule(new java.util.TimerTask(){

			@Override
			public void run() {
				loadCurrentIndentInfo();
				//refresh time
				lbCurrentTime.setText(ConstantValue.DFYMDHM.format(new Date()));
			}}, 0, ConstantValue.refreshInterval * 1000);
	}
	
	/**
	 * this class just hold Category1 objects. if need dish object, loop into the category1 objects
	 */
	public void loadMenu(){
		String url = "menu/querymenu";
		String response = HttpUtil.getJSONObjectByGet(ConstantValue.SERVER_URL + url);
		if (response == null){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server for loading menu. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for loading menu. URL = " + url);
			return;
		}
		HttpResult<ArrayList<Category1>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Category1>>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while loading menu. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		category1List = result.data;
		Collections.sort(category1List, category1Comparator);
	}
	
	private void loadConfigsMap(){
		String url = "common/queryconfigmap";
		String response = HttpUtil.getJSONObjectByGet(ConstantValue.SERVER_URL + url);
		if (response == null){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server for loading configs. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for loading configs. URL = " + url);
			return;
		}
		HttpResult<HashMap<String, String>> result = new Gson().fromJson(response, new TypeToken<HttpResult<HashMap<String, String>>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while loading configs. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		configsMap = result.data;	
	}
	
	private void loadShiftDutyInfo(){
		String url = "management/getcurrentduty";
		String response = HttpUtil.getJSONObjectByGet(ConstantValue.SERVER_URL + url);
		if (response == null){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server for duty employee info. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for duty employee info. URL = " + url);
			return;
		}
		HttpResult<CurrentDutyInfo> result = gson.fromJson(response, new TypeToken<HttpResult<CurrentDutyInfo>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while get duty employee info. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		if (result.data != null && result.data.currentDutyId > 0){
			Date startTime = null;
			try {
				startTime = ConstantValue.DFYMDHMS.parse(result.data.startTime);
			} catch (Exception e) {
				logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
				logger.error("get wrong format of on duty date, URL = " + url + ", returnjson = " + response);
			}
			onDutyUser = new UserData(result.data.currentDutyId, result.data.currentDutyName, startTime);
			lbStatusLogin.setText(Messages.getString("MainFrame.currentDutyWorker") + onDutyUser.getName() //$NON-NLS-1$
					+ Messages.getString("MainFrame.DutyStartTime") + result.data.startTime); //$NON-NLS-1$
		}
		
	}
	
	private void loadDiscountTemplates(){
		String url = "common/getdiscounttemplates";
		String response = HttpUtil.getJSONObjectByGet(ConstantValue.SERVER_URL + url);
		if (response == null){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server for discount templates. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for discount templates. URL = " + url);
			return;
		}
		HttpResult<ArrayList<DiscountTemplate>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<DiscountTemplate>>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while get discount templates. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		discountTemplateList.clear();
		discountTemplateList.addAll(result.data);
	}
	
	private void loadPayWay(){
		String url = "common/getpayways";
		String response = HttpUtil.getJSONObjectByGet(ConstantValue.SERVER_URL + url);
		if (response == null){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server for pay way. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for pay way. URL = " + url);
			return;
		}
		HttpResult<ArrayList<PayWay>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<PayWay>>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while get pay way. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		paywayList.clear();
		paywayList.addAll(result.data);
	}
	
	private void loadFlavor(){
		String url = "menu/queryflavor";
		String response = HttpUtil.getJSONObjectByGet(ConstantValue.SERVER_URL + url);
		if (response == null){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server for flavor. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for flavor. URL = " + url);
			return;
		}
		HttpResult<ArrayList<Flavor>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Flavor>>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while get flavor. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		flavorList.clear();
		flavorList.addAll(result.data);
	}
	
	private void loadMember(){
		String url = "member/queryallmember";
		String response = HttpUtil.getJSONObjectByGet(ConstantValue.SERVER_URL + url);
		if (response == null){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server for member. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for member. URL = " + url);
			return;
		}
		HttpResult<ArrayList<Member>> result = gsonTime.fromJson(response, new TypeToken<HttpResult<ArrayList<Member>>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while get member. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		memberList.clear();
		if (result.data != null)
			memberList.addAll(result.data);
	}
	
	private void buildDeskCells(){
		pDeskArea.removeAll();
		deskcellList.clear();
		if (!deskList.isEmpty()){
			Collections.sort(deskList, new Comparator<Desk>(){

				@Override
				public int compare(Desk o1, Desk o2) {
					return o1.getSequence() - o2.getSequence();
				}});
			for (int i = 0; i < deskList.size(); i++) {
				DeskCell dc = new DeskCell(this, deskList.get(i));
				pDeskArea.add(dc, new GridBagConstraints(i % ConstantValue.DESK_COLUMN_AMOUNT, (int)(i / ConstantValue.DESK_COLUMN_AMOUNT), 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0),0,0));
				deskcellList.add(dc);
			}
		}
		
		pDeskArea.updateUI();
	}
	
	/**
	 * load desk info, attach the merge table info. after loading, need reset the desk object into deskcell.
	 */
	public void loadDesks(){
		String url = "common/getdesks";
		String response = HttpUtil.getJSONObjectByGet(ConstantValue.SERVER_URL + url);
		if (response == null){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server for desks list. URL = " + url);
			JOptionPane.showMessageDialog(this, "get null from server for desks list. URL = " + url);
			return;
		}
		HttpResult<ArrayList<Desk>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Desk>>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while get desks. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		deskList.clear();
		deskList.addAll(result.data);
		
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
	 * load the unpaid indent info, attached the intenddetail info.
	 */
	public void loadCurrentIndentInfo(){
		if (onDutyUser == null)
			return;
		String url = "indent/queryindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("status", "Unpaid");
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server for query indent error. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for query indent info. URL = " + url);
			return;
		}
		
		HttpResult<ArrayList<Indent>> result = gsonTime.fromJson(response, new TypeToken<HttpResult<ArrayList<Indent>>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while get indents. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
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
	
	/**
	 * load the unpaid indent info, attached the intenddetail info.
	 */
	public Indent loadIndentByDesk(String deskName){
		if (onDutyUser == null)
			return null;
		String url = "indent/queryindent";
		Map<String, String> params = new HashMap<String, String>();
		params.put("status", "Unpaid");
		params.put("deskname", deskName);
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server for query indent by desk error. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for query indent info by desk . URL = " + url);
			return null;
		}
		
		HttpResult<ArrayList<Indent>> result = gsonTime.fromJson(response, new TypeToken<HttpResult<ArrayList<Indent>>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while get indents by desk . URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return null;
		}
		if (result.data == null || result.data.isEmpty())
			return null;
		return result.data.get(0);
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
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server for starting shiftwork. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server for starting shiftwork. URL = " + url + ", param = "+ params);
			return;
		}
		HttpResult<CurrentDutyInfo> result = gson.fromJson(response, new TypeToken<HttpResult<CurrentDutyInfo>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while starting shiftwork. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		Date startTime = null;
		try {
			startTime = ConstantValue.DFYMDHMS.parse(result.data.startTime);
		} catch (ParseException e) {
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
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
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server while off duty. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while off duty. URL = " + url + ", param = "+ params);
			return;
		}
		HttpResult<CurrentDutyInfo> result = gson.fromJson(response, new TypeToken<HttpResult<CurrentDutyInfo>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while end shiftwork. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
			return;
		}
		
		onDutyUser = null;
		lbStatusLogin.setText("");

		startLogin(null, null);
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
		String code = JOptionPane.showInputDialog(this, Messages.getString("MainFrame.InputCodeOfClearDesk"));
		if (code == null)
			return;
		if (!code.equals(configsMap.get(ConstantValue.CONFIGS_CLEARTABLECODE))){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.ErrorCashdrawerCode"));
			return;
		}
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
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server while clear desks failed. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while clear desks failed. URL = " + url + ", param = "+ params);
			return;
		}
		HttpResult<String> result = gson.fromJson(response, new TypeToken<HttpResult<String>>(){}.getType());
		if (!result.success){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("return false while clean table. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(this, result.result);
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
		String response = HttpUtil.getJSONObjectByPost(ConstantValue.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("get null from server while do merge desks failed. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(this, "get null from server while do merge desks failed. URL = " + url + ", param = "+ params);
			return;
		}
		
		HttpResult<ArrayList<DeskWithIndent>> result = gsonTime.fromJson(response, new TypeToken<HttpResult<ArrayList<DeskWithIndent>>>(){}.getType());
		for (int i = 0; i < deskcellList.size(); i++) {
			DeskCell dc = deskcellList.get(i);
			for (int j = 0; j < result.data.size(); j++) {
				DeskWithIndent di = result.data.get(j);
				if (di.id == dc.getDesk().getId()){
					if (di.indent == null){
						dc.setIndentInfo(null);
					} else {
						dc.setIndentInfo(di.indent);
					}
					dc.getDesk().setMergeTo(di.mergeTo);
					dc.setMergeTo(di.mergeTo);
					break;
				}
			}
		}
//		loadDesks();
		refreshDeskStatus();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCheckout){
			doCheckout();
		} else if (e.getSource() == btnOpenDesk){
			doOpenDesk();
		} else if (e.getSource() == btnMaintainMenu){
			doMaintainMenu();
		} else if (e.getSource() == btnViewIndent){
			doViewIndent();
		} else if (e.getSource() == btnPrintTicket){
			doPrintTicket();
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
			doOpenCashdrawer(true);
		}
	}
	
	private void doPrintTicket(){
		DeskCell selectDC = getSelectedDesk();
		if (selectDC == null)
			return;
		
		ViewHistoryIndentByDeskDialog dlg = new ViewHistoryIndentByDeskDialog(this, "", true, selectDC.getDesk());
		dlg.setVisible(true);
	}
	
	public void doOpenCashdrawer(boolean needpassword){
		if (needpassword){
			String code = JOptionPane.showInputDialog(this, Messages.getString("MainFrame.InputCodeOfOpenCashdrawer"));
			if (code == null){
				return;
			}
			if (!code.equals(configsMap.get(ConstantValue.CONFIGS_OPENCASHDRAWERCODE))){
				JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.ErrorCashdrawerCode"));
				return;
			}
		}
		
		if (ConstantValue.printerIP != null) {
			doOpenCashdrawerByPrinter();
		} else if (ConstantValue.portCashdrawer != null) {
			doOpenCashdrawerByCard();
		}
	}
	
	/**
	 * 基于网线打印机接口打开钱箱
	 */
	private void doOpenCashdrawerByPrinter(){
		Socket socket = null;
		OutputStream socketOut = null;
		OutputStreamWriter writer = null;
		try {
			socket = new Socket(ConstantValue.printerIP, 9100);//打印机默认端口是9100, 如果某些型号打印机不是这个, 单独配置
			socket.setSoTimeout(1000);
			socketOut = socket.getOutputStream();
			writer = new OutputStreamWriter(socketOut, "GBK");
			char[] c = {27, 'p', 0, 60, 240};
			writer.write(c);
			writer.flush();
		} catch (IOException e) {
			logger.error("", e);
		} finally{
			try {
				if (socket != null)
					socket.close();
				if (socketOut != null)
					socketOut.close();
				if (writer != null)
					writer.close();
			} catch (IOException e) {}
		}
	}
	
	/**
	 * 基于钱箱卡打开钱箱
	 */
	private void doOpenCashdrawerByCard() {
		if (outputStreamCashdrawer == null) {
			Enumeration portList = CommPortIdentifier.getPortIdentifiers();
			while (portList.hasMoreElements()) {
				CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					if (portId.getName().equals(ConstantValue.portCashdrawer)) {
						try {
							SerialPort serialPort = (SerialPort) portId.open("SimpleWriteApp", 2000);
							outputStreamCashdrawer = serialPort.getOutputStream();
							serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
									SerialPort.PARITY_NONE);
						} catch (PortInUseException | IOException | UnsupportedCommOperationException e) {
							logger.error(e);
						}
						break;
					}
				}
			}
		}
		try {
			if (outputStreamCashdrawer == null) {
				JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.WrongCashdrawerPort"));
				return;
			}
			outputStreamCashdrawer.write("A".getBytes());// any string is ok
		} catch (IOException e) {
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("", e);
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
		if (dlg.getMergeResponse() != null){
			HttpResult<ArrayList<DeskWithIndent>> result = gsonTime.fromJson(dlg.getMergeResponse(), new TypeToken<HttpResult<ArrayList<DeskWithIndent>>>(){}.getType());
			for (int i = 0; i < deskcellList.size(); i++) {
				DeskCell dc = deskcellList.get(i);
				for (int j = 0; j < result.data.size(); j++) {
					DeskWithIndent di = result.data.get(j);
					if (di.id == dc.getDesk().getId()){
						if (di.indent == null){
							dc.setIndentInfo(null);
						} else {
							dc.setIndentInfo(di.indent);
						}
						dc.setMergeTo(di.mergeTo);
						break;
					}
				}
			}
		}
	}
	
	private void doCheckout(){
		DeskCell selectDC = getSelectedDesk();
		if (selectDC == null)
			return;
		Indent indent = loadIndentByDesk(selectDC.getDesk().getName());
		if (indent == null)
			return;
		CheckoutDialog dlg = new CheckoutDialog(this, Messages.getString("MainFrame.CheckoutTitle"), true, selectDC.getDesk(), indent); //$NON-NLS-1$
		dlg.setVisible(true);
	}
	
	private void doOpenDesk(){
		DeskCell selectDC = getSelectedDesk();
		if (selectDC == null)
			return;
		if (selectDC.getDesk().getMergeTo() != null){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.TableIsUsed"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		if (selectDC.getIndent() == null ){
			OpenTableDialog dlg = new OpenTableDialog(this, Messages.getString("MainFrame.OpenTable"), true, selectDC.getDesk(), OpenTableDialog.MAKENEWORDER); //$NON-NLS-1$
			dlg.setVisible(true);
		} else {
			OpenTableDialog dlg = new OpenTableDialog(this, Messages.getString("MainFrame.AddDish"), true, selectDC.getDesk(), OpenTableDialog.ADDDISH); //$NON-NLS-1$
			dlg.setVisible(true);
		}
	}
	
	private void doViewIndent(){
		DeskCell selectDC = getSelectedDesk();
		if (selectDC == null)
			return;
		if (selectDC.getIndent() == null){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.NoIndentOnTable"), Messages.getString("MainFrame.Error"), JOptionPane.YES_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		Indent indent = loadIndentByDesk(selectDC.getDesk().getName());
		ViewIndentDialog dlg = new ViewIndentDialog(this, Messages.getString("MainFrame.ViewIndent"), true, selectDC.getDesk(), indent); //$NON-NLS-1$
		dlg.setVisible(true);
	}
	
	private void doMaintainMenu(){
		MenuMgmtDialog dlg = new MenuMgmtDialog(this, Messages.getString("MainFrame.MaintainMenu"), true);
		dlg.setVisible(true);
	}

	/**
	 * if there is no duty user currently, do nother
	 * if there is a duty user, as whether print the shift ticket.
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
		for (int i = 0; i < category1List.size(); i++) {
			Category1 c1 = category1List.get(i);
			if (c1.getCategory2s() !=null){
				Collections.sort(c1.getCategory2s(), category2Comparator);
				for (int j = 0; j < c1.getCategory2s().size(); j++) {
					Category2 c2 = c1.getCategory2s().get(j);
					if (c2.getDishes() != null){
						Collections.sort(c2.getDishes(), dishComparator);
						dishes.addAll(c2.getDishes());
					}
				}
			}
		}
		return dishes;
	}
	
	public Dish getDishById(int id){
		for (int i = 0; i < category1List.size(); i++) {
			Category1 c1 = category1List.get(i);
			if (c1.getCategory2s() !=null){
				Collections.sort(c1.getCategory2s(), category2Comparator);
				for (int j = 0; j < c1.getCategory2s().size(); j++) {
					Category2 c2 = c1.getCategory2s().get(j);
					if (c2.getDishes() != null){
						for(Dish dish : c2.getDishes()){
							if (dish.getId() == id)
								return dish;
						}
					}
				}
			}
		}
		return null;
	}
	
	public ArrayList<Category2> getAllCategory2s(){
		ArrayList<Category2> c2s = new ArrayList<>();
		for (int i = 0; i < category1List.size(); i++) {
			Category1 c1 = category1List.get(i);
			if (c1.getCategory2s() !=null){
				Collections.sort(c1.getCategory2s(), category2Comparator);
				c2s.addAll(c1.getCategory2s());
			}
		}
		return c2s;
	}
	
	
	public HashMap<String, String> getConfigsMap() {
		return configsMap;
	}

	public void setConfigsMap(HashMap<String, String> configsMap) {
		this.configsMap = configsMap;
	}

	private Comparator<Category1> category1Comparator = new Comparator<Category1>(){

		@Override
		public int compare(Category1 o1, Category1 o2) {
			return o1.getSequence() - o2.getSequence();
		}};
		
	private Comparator<Category2> category2Comparator = new Comparator<Category2>() {

		@Override
		public int compare(Category2 o1, Category2 o2) {
			return o1.getSequence() - o2.getSequence();
		}
	};

	private Comparator<Dish> dishComparator = new Comparator<Dish>(){

		@Override
		public int compare(Dish o1, Dish o2) {
			return o1.getSequence() - o2.getSequence();
		}};

	public static void main(String[] args){
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				MainFrame.logger.error(ConstantValue.DFYMDHMS.format(new Date()));
				MainFrame.logger.error("", e);
				e.printStackTrace();
			}
		});
		StartingWaitDialog waitDlg = new StartingWaitDialog();
		waitDlg.setVisible(true);
		
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
		
		int fontsize = Integer.parseInt(prop.getProperty("fontsize"));
		Font font = new Font(null, Font.PLAIN, fontsize);
		Enumeration enums = UIManager.getDefaults().keys();
		while(enums.hasMoreElements()){
			Object key = enums.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof Font){
				UIManager.put(key, font);
			}
		}
		ConstantValue.SERVER_URL = prop.getProperty("SERVER_URL");
		ConstantValue.DESK_COLUMN_AMOUNT = Integer.parseInt(prop.getProperty("onelinetables"));
		ConstantValue.TABLECELL_WIDTH = Integer.parseInt(prop.getProperty("tablecell.width"));
		ConstantValue.TABLECELL_HEIGHT = Integer.parseInt(prop.getProperty("tablecell.height"));
		ConstantValue.WINDOW_WIDTH = Integer.parseInt(prop.getProperty("mainframe.width"));
		ConstantValue.WINDOW_HEIGHT = Integer.parseInt(prop.getProperty("mainframe.height"));
		ConstantValue.WINDOW_LOCATIONX = Integer.parseInt(prop.getProperty("mainframe.locationx"));
		ConstantValue.WINDOW_LOCATIONY = Integer.parseInt(prop.getProperty("mainframe.locationy"));
		ConstantValue.language = prop.getProperty("language");
		ConstantValue.portCashdrawer=prop.getProperty("portCashdrawer");
		ConstantValue.printerName = prop.getProperty("printerName");
		ConstantValue.printerIP = prop.getProperty("printerIP");
		ConstantValue.functionlist = prop.getProperty("mainframe.functionlist");
		try{
			ConstantValue.refreshInterval = Integer.parseInt(prop.getProperty("refreshInterval"));
			ConstantValue.openTableDialog_Category2Layout_Row = Integer.parseInt(prop.getProperty("OpenTableDialog.Category2Layout.Row"));
			ConstantValue.openTableDialog_Category2Layout_Column = Integer.parseInt(prop.getProperty("OpenTableDialog.Category2Layout.Column"));
			ConstantValue.openTableDialog_DishLayout_Row = Integer.parseInt(prop.getProperty("OpenTableDialog.DishLayout.Row"));
			ConstantValue.openTableDialog_DishLayout_Column = Integer.parseInt(prop.getProperty("OpenTableDialog.DishLayout.Column"));
			ConstantValue.dishConfig_Column = Integer.parseInt(prop.getProperty("DishConfigDialog.Column"));
		} catch (NumberFormatException e){
			e.printStackTrace();
		}
		final MainFrame f = new MainFrame();
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		f.setUndecorated(true);
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(f, "Do you want to quit this system?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					System.exit(0);
				}
			}
		});
		waitDlg.setVisible(false);
		f.setVisible(true);
		f.startLogin(prop.getProperty("defaultuser.name"), prop.getProperty("defaultuser.password"));
	}
}
