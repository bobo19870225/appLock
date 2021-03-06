package com.scyh.applock.utils;

import java.util.List;

import com.scyh.applock.service.LockService;
import com.scyh.applock.ui.activity.SetActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ServiceUtils {
	
	/**
	 * 判断某个服务是否正在运行的方法
	 * 
	 * @param mContext
	 * @param serviceName
	 *            是包名+服务的类名（例如：com.beidian.test.service.BasicInfoService ）
	 * @return
	 */
	public static boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> myList = myAM.getRunningServices(100);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}

	/**
	 * 判断进程是否运行
	 * 
	 * @return
	 */
	public static boolean isProessRunning(Context context, String proessName) {

		boolean isRunning = false;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningAppProcessInfo> lists = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : lists) {
			if (info.processName.equals(proessName)) {
				isRunning = true;
			}
		}

		return isRunning;
	}
	
	public  static void startLockService(Context context){
		boolean logc = ServiceUtils.isServiceWork(context, LockService.class.getName());
		if(!logc){
			SharedPreferences sp=context.getSharedPreferences("applock", Context.MODE_PRIVATE);
			boolean need_open=sp.getBoolean(SetActivity.TAG_SERVICE_APP,	false);
			if(need_open){
				Intent i = new Intent(context, LockService.class);
				context.startService(i);
			}
		}
	}

}
