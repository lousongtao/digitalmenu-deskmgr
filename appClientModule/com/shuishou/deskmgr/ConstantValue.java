package com.shuishou.deskmgr;

import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ConstantValue {
	public static final DateFormat DFYMDHMS = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static final DateFormat DFHMS = new SimpleDateFormat("HH:mm:ss");
	public static final DateFormat DFYMD = new SimpleDateFormat("yyyy/MM/dd");
	
	public static final Font FONT_30BOLD = new Font(null, Font.BOLD, 30);
	public static final Font FONT_30PLAIN = new Font(null, Font.PLAIN, 30);	
	public static final Font FONT_20BOLD = new Font(null, Font.BOLD, 20);
	public static final Font FONT_20PLAIN = new Font(null, Font.PLAIN, 20);
	
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
	
	public static final byte INDENT_OPERATIONTYPE_ADD = 1;
	public static final byte INDENT_OPERATIONTYPE_DELETE = 2;
	public static final byte INDENT_OPERATIONTYPE_CANCEL = 3;
	public static final byte INDENT_OPERATIONTYPE_PAY = 4;
	
	//付款方式
	public static final byte INDENT_PAYWAY_CASH = 1;//现金
	public static final byte INDENT_PAYWAY_CARD = 2;//刷卡
	public static final byte INDENT_PAYWAY_MEMBER = 3;//会员
	
	public static final byte INDENTDETAIL_OPERATIONTYPE_ADD = 1;
	public static final byte INDENTDETAIL_OPERATIONTYPE_DELETE = 2;
	public static final byte INDENTDETAIL_OPERATIONTYPE_CHANGEAMOUNT = 5;
	
	public static final byte MENUCHANGE_TYPE_SOLDOUT = 0;
	
	public static final byte PRINT_TYPE_TOGETHER = 0;
	public static final byte PRINT_TYPE_SEPARATELY = 1;
	
	public static final byte SHIFTWORK_ONWORK = 0;
	public static final byte SHIFTWORK_OFFWORK = 1;
}
