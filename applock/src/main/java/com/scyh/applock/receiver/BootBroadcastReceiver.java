package com.scyh.applock.receiver;

import com.scyh.applock.service.LoadAppListService;
import com.scyh.applock.service.LockService;
import com.scyh.applock.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootBroadcastReceiver extends BroadcastReceiver{

	
	private static final String TAG=BootBroadcastReceiver.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.e(TAG, "BOOT COMPLETE---START---SERVICE");
//		Intent service=new Intent(context,LockService.class);
//		context.startService(service);
		//安卓8.0以上禁止后台启动服务，所以要设为前台服务
		Intent i = new Intent(context.getApplicationContext(), LockService.class);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			context.startForegroundService(i);
		} else {
			context.startService(i);
		}
	}

}
