package com.scyh.applock.receiver;

import com.scyh.applock.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LauncherServiceBroadcastReceiver extends BroadcastReceiver{
	
	
	 String SCREEN_ON = "android.intent.action.SCREEN_ON";  
	    String SCREEN_OFF = "android.intent.action.SCREEN_OFF";  
	private static final String TAG=LauncherServiceBroadcastReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		
		
//		Logger.e(TAG, "--->>>current----action-->>>"+intent.getAction());
		
		if(SCREEN_OFF.equals(intent.getAction())){
			Logger.e(TAG, "屏幕熄灭了。");
		}else if(SCREEN_ON.equals(intent.getAction())){
			Logger.e(TAG, "屏幕打开了");
		}
		
		//Logger.e(TAG, "BOOT COMPLETE---START---SERVICE");
		//Intent service=new Intent(context,LockService.class);
		//context.startService(service);
		
	}

}
