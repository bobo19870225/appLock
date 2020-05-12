package com.scyh.applock.receiver;

import com.scyh.applock.service.LockService;
import com.scyh.applock.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver{

	
	private static final String TAG=BootBroadcastReceiver.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Logger.e(TAG, "BOOT COMPLETE---START---SERVICE");
		Intent service=new Intent(context,LockService.class);
		context.startService(service);
	}

}
