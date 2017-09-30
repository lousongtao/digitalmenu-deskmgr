package com.shuishou.deskmgr;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME_CN = "com.shuishou.deskmgr.messages_cn"; //$NON-NLS-1$
	private static final String BUNDLE_NAME_EN = "com.shuishou.deskmgr.messages_en"; //$NON-NLS-1$
	private static ResourceBundle RESOURCE_BUNDLE= null;

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static void initResourceBundle(String lang){
		if ("en".equals(lang)){
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME_EN);
		} else if ("cn".equals(lang)){
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME_CN);
		}
		
	}
}
