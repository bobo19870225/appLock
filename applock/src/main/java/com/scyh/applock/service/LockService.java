package com.scyh.applock.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;

import com.scyh.applock.AppConstants;
import com.scyh.applock.R;
import com.scyh.applock.db.CommLockInfoManager;
import com.scyh.applock.task.RecycleTask;
import com.scyh.applock.ui.activity.GestureUnlockAppActivity;
import com.scyh.applock.utils.Logger;
import com.scyh.applock.utils.ServiceUtils;
import com.scyh.applock.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

public class LockService extends IntentService {
	public LockService() {
		super("LockService");
	}

	private static LockService instance;
	public static final int NOTICE_ID = 100;
	public synchronized static LockService getInstance() {
		return instance;
	}

	public boolean threadIsTerminate = true; // 是否开启循环

	public static final String UNLOCK_ACTION = "UNLOCK_ACTION";
//	public static final String GOTO_PWD_ACTIVITY = "GOTO_PWD_ACTIVITY";
	public static final String LOCK_SERVICE_LASTTIME = "LOCK_SERVICE_LASTTIME";
	public static final String LOCK_SERVICE_LASTAPP = "LOCK_SERVICE_LASTAPP";

	private long lastUnlockTimeSeconds = 0; // 最后解锁的时间
	private String lastUnlockPackageName = ""; // 最后解锁的程序包名

	private boolean lockState;
	private MyBinder binder;

	private ServiceReceiver mServiceReceiver;
	private CommLockInfoManager mLockInfoManager;
	private static ActivityManager activityManager;
	private static UsageStatsManager sUsageStatsManager; 
	public static boolean isActionLock = false;
	private Thread recycleThread;
	public String savePkgName;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 4:

//				Logger.e("s", "hander rece----------------------msg");
				String pkg = msg.obj.toString();
				passwordLock(pkg);
				break;

			default:
				break;
			}
		}

	};

	static class MyBinder extends IMyAidlInterface.Stub {
		@Override
		public String getServiceName() throws RemoteException {
			return LocalService.class.getSimpleName();
		}
	}


	@Override
	public void onCreate() {
		super.onCreate();
		MyConn conn = new MyConn();
		instance = this;
		lockState = SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE);
		lockState = true;
		mLockInfoManager = new CommLockInfoManager(this);
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		sUsageStatsManager=(UsageStatsManager) getSystemService("usagestats");

		// 注册广播
		mServiceReceiver = new ServiceReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(UNLOCK_ACTION);
//		filter.addAction(GOTO_PWD_ACTIVITY);
		filter.addAction("fxq_recycle_task");
		registerReceiver(mServiceReceiver, filter);
		
//		registSreenStatusReceiver();
		

		// 开启一个检查锁屏的线程
		threadIsTerminate = true;
		Logger.e("fxq", "---service------oncreate");

		// new Thread(new RecycleTask()).start();
		recycleThread = new Thread(new RecycleTask(this, activityManager, mLockInfoManager, handler));
		// recycleThread.start();

//		new Thread(new RecycleLauncher()).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		Logger.e("fxq", "---service------onHandleIntent");
		// checkData();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Logger.e("fxq", "---service------onStart");
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {//如果API大于18，需要弹出一个可见通知
			Notification.Builder builder = new Notification.Builder(this);
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setContentTitle("应用锁");
			builder.setContentText("应用锁持续为您守护...");
			startForeground(NOTICE_ID,builder.build());
		}else{
			startForeground(NOTICE_ID,new Notification());
		}
//		Logger.e("fxq", "---service------onStartCommand");
//		flags=Service.START_STICKY;
//		PendingIntent notificationIntent = PendingIntent.getActivity(this, 0, intent, 0);
//		Notification noti = new Notification.Builder(this)
//				.setContentTitle("应用锁")
//				.setContentText("应用锁持续为您守护...")
//				.setSmallIcon(R.drawable.ic_launcher)
//				.setContentIntent(notificationIntent)
//				.build();
//		startForeground(123456, noti);
		Thread.State state = recycleThread.getState();
//		Log.e("recycleThread", state.toString());
		if (state == Thread.State.NEW) {
			recycleThread.start();
		}
        return START_STICKY;
	}


	public static String current_launcher="com.android.gallery3d";
//	class RecycleLauncher implements Runnable {
//
//		@Override
//		public void run() {
//
//			
//			while(true){
//				current_launcher=getLauncherTopApp();
//				Logger.e("xunhuan----------------", "--->>>>>>>>>>>>xunhuan----------------------"+current_launcher);
//				try {
//					Thread.sleep(300);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//
		public static  String getLauncherTopApp() {

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
				if (null != appTasks && !appTasks.isEmpty()) {
					return appTasks.get(0).topActivity.getPackageName();
				}
			} else {
				// //5.0以后需要用这方法
//				long endTime = System.currentTimeMillis();
//				long beginTime = endTime - 10500;
//				String result = "";
//				UsageEvents.Event event = new UsageEvents.Event();
//				UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
//				while (usageEvents.hasNextEvent()) {
//					usageEvents.getNextEvent(event);
//					if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
//						result = event.getPackageName();
//					}
//				}
//				if (!android.text.TextUtils.isEmpty(result)) {
//					return result;
//				}
				
//				UsageStatsManager usageStatsManager = (UsageStatsManager) LockService.this
//                        .getSystemService(Context.USAGE_STATS_SERVICE);
			long ts = System.currentTimeMillis();
			List<UsageStats> queryUsageStats = sUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0,
					ts);
			if (queryUsageStats == null || queryUsageStats.isEmpty()) {
				return null;
			}

			UsageStats recentStats = null;
			for (UsageStats usageStats : queryUsageStats) {
				if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
					// if (DEBUG) Log.d(TAG, usageStats.getPackageName() + ", "
					// + usageStats.getLastTimeStamp());
					recentStats = usageStats;
				}
			}

			// if (DEBUG) Log.d(TAG, "getForegroundApp: " +
			// recentStats.getPackageName());
			return recentStats.getPackageName();
		}
		return "";
	}
//	}

	private void checkData() {

		// new Thread() {
		// public void run() {
		while (threadIsTerminate) {
			if (lockState) {
				// 获取栈顶app的包名
				String packageName = getLauncherTopApp(LockService.this, activityManager);
				if (packageName != null && !TextUtils.isEmpty(packageName)) {
					if (!inWhiteList(packageName)) {

						boolean isLockOffScreenTime = SpUtil.getInstance()
								.getBoolean(AppConstants.LOCK_AUTO_SCREEN_TIME, false); // 是否开启暂时离开
						boolean isLockOffScreen = SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_SCREEN, false); // 是否在手机屏幕关闭后再次锁定
						savePkgName = SpUtil.getInstance().getString(AppConstants.LOCK_LAST_LOAD_PKG_NAME, "");
						// Log.i("Server", "packageName = " + packageName + "
						// savePkgName = " + savePkgName);
						// 情况一 解锁后一段时间才再锁
						// if (isLockOffScreenTime && !isLockOffScreen) {
						// long time =
						// SpUtil.getInstance().getLong(AppConstants.LOCK_CURR_MILLISENCONS,
						// 0); // 获取记录的时间
						// long leaverTime =
						// SpUtil.getInstance().getLong(AppConstants.LOCK_APART_MILLISENCONS,
						// 0); // 获取离开时间
						// if (!TextUtils.isEmpty(savePkgName)) {
						// if (!TextUtils.isEmpty(packageName)) {
						// if (!savePkgName.equals(packageName)) { //
						// if (getHomes().contains(packageName) ||
						// packageName.contains("launcher")) {
						// boolean isSetUnLock =
						// mLockInfoManager.isSetUnLock(savePkgName);
						// if (!isSetUnLock) {
						// if (System.currentTimeMillis() - time > leaverTime) {
						// mLockInfoManager.lockCommApplication(savePkgName);
						// }
						// }
						// }
						// }
						// }
						// }
						// }
						//
						// // 情况二 解锁后没关屏：退出应用后一段时间后再锁
						// if (isLockOffScreenTime && isLockOffScreen) {
						// long time =
						// SpUtil.getInstance().getLong(AppConstants.LOCK_CURR_MILLISENCONS,
						// 0); // 获取记录的时间
						// long leaverTime =
						// SpUtil.getInstance().getLong(AppConstants.LOCK_APART_MILLISENCONS,
						// 0); // 获取离开时间
						// if (!TextUtils.isEmpty(savePkgName)) {
						// if (!TextUtils.isEmpty(packageName)) {
						// if (!savePkgName.equals(packageName)) {
						// if (getHomes().contains(packageName) ||
						// packageName.contains("launcher")) {
						// boolean isSetUnLock =
						// mLockInfoManager.isSetUnLock(savePkgName);
						// if (!isSetUnLock) {
						// if (System.currentTimeMillis() - time > leaverTime) {
						// mLockInfoManager.lockCommApplication(savePkgName);
						// }
						// }
						// }
						// }
						// }
						// }
						// }
						//
						// // 情况三 用户关屏后立马锁定，退出后也锁定
						// if (!isLockOffScreenTime && isLockOffScreen) {
						// if (!TextUtils.isEmpty(savePkgName)) {
						// if (!TextUtils.isEmpty(packageName)) {
						// if (!savePkgName.equals(packageName)) {
						// isActionLock = false;
						// if (getHomes().contains(packageName) ||
						// packageName.contains("launcher")) {
						// boolean isSetUnLock =
						// mLockInfoManager.isSetUnLock(savePkgName);
						// if (!isSetUnLock) {
						// mLockInfoManager.lockCommApplication(savePkgName);
						// }
						// }
						// } else {
						// isActionLock = true;
						// }
						// }
						// }
						// }

						// 情况四 每次都锁
						// if (!isLockOffScreenTime && !isLockOffScreen) {
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
						// }

						// 查找各种的锁，若存在则判断逻辑
						if (mLockInfoManager.isLockedPackageName(packageName)) {
							Logger.e("fxq", "need lock");
							// passwordLock(packageName);
							continue;
						}
					}
				}
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// };
		// }.start();
	}

	private synchronized void passwordLock(String packageName) {
		// LockApplication.getInstance().clearAllActivity();
		Intent intent = new Intent(this, GestureUnlockAppActivity.class);

		intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, packageName);
		intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_FINISH);
		intent.setFlags(  Intent.FLAG_ACTIVITY_NEW_TASK );
		startActivity(intent);
	}

	/**
	 * 白名单
	 */
	private boolean inWhiteList(String packageName) {
		return packageName.equals(AppConstants.APP_PACKAGE_NAME) || packageName.equals("com.android.settings");
	}

	/**
	 * 服务广播
	 */
	public class ServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			boolean isLockOffScreen = SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_SCREEN, false); // 是否在手机屏幕关闭后再次锁定
			boolean isLockOffScreenTime = SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_SCREEN_TIME, false); // 是否在手机屏幕关闭后时间段后再次锁定
			if (action.equals(UNLOCK_ACTION)) {
				lastUnlockPackageName = intent.getStringExtra(LOCK_SERVICE_LASTAPP); // 最后解锁的程序包名
				mLockInfoManager.unlockCommApplication(lastUnlockPackageName);
//				lastUnlockTimeSeconds = intent.getLongExtra(LOCK_SERVICE_LASTTIME, lastUnlockTimeSeconds); // 最后解锁时间
			} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
//				SpUtil.getInstance().putLong(AppConstants.LOCK_CURR_MILLISENCONS, System.currentTimeMillis()); // 记录屏幕关闭时间
				// 情况三
//				if (!isLockOffScreenTime && isLockOffScreen) {
//					String savePkgName = SpUtil.getInstance().getString(AppConstants.LOCK_LAST_LOAD_PKG_NAME, "");
//					if (!TextUtils.isEmpty(savePkgName)) {
//						if (isActionLock) {
							mLockInfoManager.lockCommApplication(lastUnlockPackageName);
//						}
//					}
//				}
			} else if (action.equals("fxq_recycle_task")) {
				Logger.e("---", "receivy cast---want to start thread...");
				recycleThread.start();
			}
		}
	}

	/**
	 * 获取栈顶应用包名
	 */
	public String getLauncherTopApp(Context context, ActivityManager activityManager) {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
			if (null != appTasks && !appTasks.isEmpty()) {
				return appTasks.get(0).topActivity.getPackageName();
			}
		} else {
			// //5.0以后需要用这方法
			UsageStatsManager sUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
			long endTime = System.currentTimeMillis();
			long beginTime = endTime - 10000;
			String result = "";
			UsageEvents.Event event = new UsageEvents.Event();
			UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
			while (usageEvents.hasNextEvent()) {
				usageEvents.getNextEvent(event);
				if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
					result = event.getPackageName();
				}
			}
			if (!android.text.TextUtils.isEmpty(result)) {
				return result;
			}

		}
		return "";
	}

	/**
	 * 获得属于桌面的应用的应用包名称
	 */
	private List<String> getHomes() {
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = this.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}

	// /**
	// * 转到解锁界面
	// */
	// private void passwordLock(String packageName) {
	// // LockApplication.getInstance().clearAllActivity();
	// Intent intent = new Intent(this, GestureUnlockAppActivity.class);
	//
	// intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, packageName);
	// intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_FINISH);
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// startActivity(intent);
	// }

	@Override
	public void onDestroy() {
		super.onDestroy();
		threadIsTerminate = false;
		unregisterReceiver(mServiceReceiver);
//		unregisterReceiver(mScreenStatusReceiver);
	}

	class MyConn implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			ServiceUtils.startLockService(LockService.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
//			  Toast.makeText(LockService.this, "远程服务killed", Toast.LENGTH_SHORT).show();
			//开启远程服务
//			LockService.this.startService(new Intent(LockService.this, RomoteService.class));
			//绑定远程服务
//			LockService.this.bindService(new Intent(LockService.this, RomoteService.class), conn, Context.BIND_IMPORTANT);
		}
	}
}
