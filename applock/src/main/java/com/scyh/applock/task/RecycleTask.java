package com.scyh.applock.task;

import java.util.ArrayList;
import java.util.List;

import com.scyh.applock.AppConstants;
import com.scyh.applock.db.CommLockInfoManager;
import com.scyh.applock.service.LockService;
import com.scyh.applock.ui.activity.GestureUnlockAppActivity;
import com.scyh.applock.utils.Logger;
import com.scyh.applock.utils.SpUtil;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class RecycleTask implements Runnable {

	private Context context;
	private ActivityManager activityManager;
	private CommLockInfoManager mLockInfoManager;
	private Handler handler;

	public RecycleTask(Context context, ActivityManager activityManager, CommLockInfoManager mLockInfoManager,Handler handler) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.activityManager = activityManager;
		this.mLockInfoManager = mLockInfoManager;
		this.handler=handler;
	}

	@Override
	public void run() {

		while (true) {
			try {
				boolean bb = SpUtil.getInstance().getBoolean("need_recycle");
				bb = true;
				String packageName="" ;
				if (bb) {
				   packageName = LockService.getLauncherTopApp();
//				   Logger.dft("--current"+packageName);
//					packageName=LockService.current_launcher;
					if (packageName != null && !TextUtils.isEmpty(packageName)) {
						if (!inWhiteList(packageName)) {
							String savePkgName = SpUtil.getInstance().getString(AppConstants.LOCK_LAST_LOAD_PKG_NAME);
							if (!TextUtils.isEmpty(savePkgName)) {
								if (!TextUtils.isEmpty(packageName)) {
									if (!savePkgName.equals(packageName)) {
										if (getHomes().contains(packageName) || packageName.contains("launcher")) {
											boolean isSetUnLock = mLockInfoManager.isSetUnLock(savePkgName);
											if (!isSetUnLock) {
												mLockInfoManager.lockCommApplication(savePkgName);
												
												
											}
										}
									}
								}
							}
						}
					}
					if (mLockInfoManager.isLockedPackageName(packageName)) {
//						passwordLock(packageName);
						Message msg=handler.obtainMessage(4);
						msg.obj=packageName;
						handler.sendMessage(msg);
						continue;
						//break;
					}	
					Thread.sleep(400);
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void passwordLock(String packageName) {
		// LockApplication.getInstance().clearAllActivity();
		Intent intent = new Intent(context, GestureUnlockAppActivity.class);

		intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, packageName);
		intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_FINISH);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP & Intent.FLAG_ACTIVITY_NO_HISTORY
				& Intent.FLAG_ACTIVITY_CLEAR_TASK);
		context.startActivity(intent);
	}

	private List<String> getHomes() {
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}

//	public static String getLauncherTopApp() {
//
//		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//			List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
//			if (null != appTasks && !appTasks.isEmpty()) {
//				return appTasks.get(0).topActivity.getPackageName();
//			}
//		} else {
//			// //5.0以后需要用这方法
//			UsageStatsManager sUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
//			long endTime = System.currentTimeMillis();
//			long beginTime = endTime - 2000;
//			String result = "";
//			UsageEvents.Event event = new UsageEvents.Event();
//			UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
//			while (usageEvents.hasNextEvent()) {
//				usageEvents.getNextEvent(event);
//				if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
//					result = event.getPackageName();
//				}
//			}
//			if (!android.text.TextUtils.isEmpty(result)) {
//				return result;
//			}
//		}
//		return "";
//	}

	private boolean inWhiteList(String packageName) {
		return packageName.equals(AppConstants.APP_PACKAGE_NAME) || packageName.equals("com.android.settings");
	}

}
