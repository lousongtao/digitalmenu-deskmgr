package com.shuishou.deskmgr;

import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ConstantValue {
	public static final DateFormat DFYMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final DateFormat DFYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DateFormat DFHMS = new SimpleDateFormat("HH:mm:ss");
	public static final DateFormat DFYMD = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DFWEEK = new SimpleDateFormat("EEE");
	public static final DateFormat DFYMDHMS_2 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static final String DATE_PATTERN_YMD = "yyyy-MM-dd";
	public static final String DATE_PATTERN_YMDHMS = "yyyy-MM-dd HH:mm:ss";
	
	public static final String FORMAT_DOUBLE = "%.2f";
	
	public static final Font FONT_25BOLD = new Font(null, Font.BOLD, 25);
	public static final Font FONT_25PLAIN = new Font(null, Font.PLAIN, 25);	
	public static final Font FONT_20BOLD = new Font(null, Font.BOLD, 20);
	public static final Font FONT_20PLAIN = new Font(null, Font.PLAIN, 20);
	public static final Font FONT_15BOLD = new Font(null, Font.BOLD, 15);
	public static final Font FONT_15PLAIN = new Font(null, Font.PLAIN, 15);
	public static final Font FONT_10BOLD = new Font(null, Font.BOLD, 10);
	public static final Font FONT_10PLAIN = new Font(null, Font.PLAIN, 10);
	
	public static final String LANGUAGE_CHINESE = "cn";
	public static final String LANGUAGE_ENGLISH = "en";
	
	public static final String PERMISSION_QUERY_USER = "QUERY_USER";
	public static final String PERMISSION_CREATE_USER = "CREATE_USER";
	public static final String PERMISSION_EDIT_MENU = "EDIT_MENU";
	public static final String PERMISSION_QUERY_ORDER = "QUERY_ORDER";
	public static final String PERMISSION_UPDATE_ORDER = "UPDATE_ORDER";
	public static final String PERMISSION_CHANGE_CONFIRMCODE = "CHANGE_CONFIRMCODE";
	public static final String PERMISSION_QUERY_DESK = "QUERY_DESK";
	public static final String PERMISSION_EDIT_DESK = "EDIT_DESK";
	public static final String PERMISSION_EDIT_PRINTER = "EDIT_PRINTER";
	
	public final static String SPLITTAG_PERMISSION = ";";
	
	public static final String TYPE_CATEGORY1INFO = "C1";
	public static final String TYPE_CATEGORY2INFO = "C2";
	public static final String TYPE_DISHINFO = "DISH";
	
	public static final String CATEGORY_DISHIMAGE_ORIGINAL = "dishimage_original";
	public static final String CATEGORY_DISHIMAGE_MIDDLE = "dishimage_middle";
	public static final String CATEGORY_DISHIMAGE_SMALL = "dishimage_small";
	public static final String CATEGORY_PRINTTEMPLATE = "printtemplate";
	
	public static final int DISHIMAGE_WIDTH_SMALL = 50;
	public static final int DISHIMAGE_HEIGHT_SMALL = 50;
	public static final int DISHIMAGE_WIDTH_MIDDLE = 280;
	public static final int DISHIMAGE_HEIGHT_MIDDLE = 300;
	
	public static final String CSS_MENUTREENODE_ICON_SIZE = "menutreenode-icon-size";
	
	public static final byte INDENT_STATUS_OPEN = 1;
	public static final byte INDENT_STATUS_CLOSED = 2;
	public static final byte INDENT_STATUS_PAID = 3;
	public static final byte INDENT_STATUS_CANCELED = 4;
	public static final byte INDENT_STATUS_FORCEEND = 5;//强制清台
	public static final byte INDENT_STATUS_REFUND = 6;
	
	public static final byte INDENT_OPERATIONTYPE_ADD = 1;
	public static final byte INDENT_OPERATIONTYPE_DELETE = 2;
	public static final byte INDENT_OPERATIONTYPE_CANCEL = 3;
	public static final byte INDENT_OPERATIONTYPE_PAY = 4;
	public static final byte INDENT_OPERATIONTYPE_REFUND = 5;
	
	//付款方式
	public static final String INDENT_PAYWAY_CASH = "cash";//现金
	public static final String INDENT_PAYWAY_BANKCARD = "bankcard";//刷卡
	public static final String INDENT_PAYWAY_MEMBER = "member";//会员
	
	public static final byte INDENTDETAIL_OPERATIONTYPE_ADD = 1;
	public static final byte INDENTDETAIL_OPERATIONTYPE_DELETE = 2;
	public static final byte INDENTDETAIL_OPERATIONTYPE_CHANGEAMOUNT = 5;
	public static final byte INDENTDETAIL_OPERATIONTYPE_REFUND = 6;
	
	public static final byte MENUCHANGE_TYPE_SOLDOUT = 0;
	
	public static final byte PRINT_TYPE_TOGETHER = 0;
	public static final byte PRINT_TYPE_SEPARATELY = 1;
	
	public static final byte SHIFTWORK_ONWORK = 0;
	public static final byte SHIFTWORK_OFFWORK = 1;
	
	public static final byte DISH_CHOOSEMODE_DEFAULT = 1;
	public static final byte DISH_CHOOSEMODE_SUBITEM = 2;
	public static final byte DISH_CHOOSEMODE_POPINFOCHOOSE = 3;
	public static final byte DISH_CHOOSEMODE_POPINFOQUIT = 4;
	
	public static final byte DISH_PURCHASETYPE_UNIT = 1;
	public static final byte DISH_PURCHASETYPE_WEIGHT = 2;
	
	public static final String CONFIGS_CANCELORDERCODE = "CANCELORDERCODE";
	public static final String CONFIGS_CLEARTABLECODE = "CLEARTABLECODE";
	public static final String CONFIGS_CONFIRMCODE = "CONFIRMCODE";
	public static final String CONFIGS_OPENCASHDRAWERCODE = "OPENCASHDRAWERCODE";
	public static final String CONFIGS_LANGUAGEAMOUNT = "LANGUAGEAMOUNT";
	public static final String CONFIGS_FIRSTLANGUAGENAME= "FIRSTLANGUAGENAME";
	public static final String CONFIGS_FIRSTLANGUAGEABBR = "FIRSTLANGUAGEABBR";
	public static final String CONFIGS_SECONDLANGUAGENAME= "SECONDLANGUAGENAME";
	public static final String CONFIGS_SECONDLANGUAGEABBR = "SECONDLANGUAGEABBR";
	public static final String CONFIGS_PRINT2NDLANGUAGENAME= "PRINT2NDLANGUAGENAME";
	public static final String CONFIGS_BRANCHNAME= "BRANCHNAME";
	public static final String CONFIGS_MEMBERMGR_NEEDPASSWORD = "MEMBERMGR_NEEDPASSWORD";
	public static final String CONFIGS_MEMBERMGR_BYSCORE= "MEMBERMGR_BYSCORE";
	public static final String CONFIGS_MEMBERMGR_BYDEPOSIT = "MEMBERMGR_BYDEPOSIT";
	public static final String CONFIGS_MEMBERMGR_SCOREPERDOLLAR = "MEMBERMGR_SCOREPERDOLLAR";
	
	public static final String FUNCTION_ACCOUNT="ACCOUNT";
	public static final String FUNCTION_CONFIG="CONFIG";
	public static final String FUNCTION_MENU="MENU";
	public static final String FUNCTION_FLAVOR="FLAVOR";
	public static final String FUNCTION_DESK="DESK";
	public static final String FUNCTION_PAYWAY="PAYWAY";
	public static final String FUNCTION_DISCOUNT="DISCOUNT";
	public static final String FUNCTION_PRINTER="PRINTER";
	public static final String FUNCTION_MATERIAL="MATERIAL";
	public static final String FUNCTION_LOGQUERY="LOGQUERY";
	public static final String FUNCTION_ORDERQUERY="ORDERQUERY";
	public static final String FUNCTION_SHIFTWORK="SHIFTWORK";
	public static final String FUNCTION_STATISTICS="STATISTICS";
	public static final String FUNCTION_MEMBER="MEMBER";
	
	public static final int DISCOUNTTYPE_RATE= 1;     //折扣类型-按比例折扣
	public static final int DISCOUNTTYPE_QUANTITY = 2;//折扣类型-直接减数量
	
	public static int DESK_COLUMN_AMOUNT;
	public static int TABLECELL_WIDTH;
	public static int TABLECELL_HEIGHT;
	public static int WINDOW_WIDTH;
	public static int WINDOW_HEIGHT;
	public static int WINDOW_LOCATIONX;
	public static int WINDOW_LOCATIONY;
	public static String language;
	public static String SERVER_URL;
	public static String portCashdrawer;
	public static String printerName;
	public static String printerIP;
	public static String functionlist;
	public static int refreshInterval = 60; //default 60 seconds
	public static int openTableDialog_Category2Layout_Row = 3;
	public static int openTableDialog_Category2Layout_Column = 5;
	public static int openTableDialog_DishLayout_Row = 4;
	public static int openTableDialog_DishLayout_Column = 4;
	public static int dishConfig_Column = 6;
	
}
