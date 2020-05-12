package com.scyh.applock.utils;

import com.scyh.applock.AppContext;

public class VipUtils {
	
	public static boolean validVip(){
		return AppContext.getInstance().getIsVip();
	}

}
