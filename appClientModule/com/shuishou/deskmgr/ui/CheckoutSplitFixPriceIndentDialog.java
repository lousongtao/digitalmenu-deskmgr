package com.shuishou.deskmgr.ui;

import com.shuishou.deskmgr.beans.Desk;
import com.shuishou.deskmgr.beans.Indent;

/**
 * 固定金额分帐, 付款界面
 */
public class CheckoutSplitFixPriceIndentDialog extends CheckoutSplitIndentDialog{
	public CheckoutSplitFixPriceIndentDialog(MainFrame mainFrame, String title, boolean modal, Desk desk, Indent indent, int originIndentId){
		super(mainFrame, title, modal, desk, indent, originIndentId);
	}

    public String getServerURL(){
        return "indent/splitfixpriceindentandpay";
    }

}
