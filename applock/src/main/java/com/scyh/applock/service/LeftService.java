package com.scyh.applock.service;

import com.scyh.applock.utils.Logger;
import com.scyh.applock.utils.ServiceUtils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

public class LeftService extends Service {

	private static final String TAG=LeftService.class.getSimpleName();
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				startServiceRight();
			}

		};
	};

	private RemoteCytual cytual = new RemoteCytual.Stub() {
		@Override
		public void stopService() throws RemoteException {
			Intent i = new Intent(getBaseContext(), RightService.class);
			getBaseContext().stopService(i);
		}

		@Override
		public void startService() throws RemoteException {
			Intent i = new Intent(getBaseContext(), RightService.class);
			getBaseContext().startService(i);
		}
	};

	@Override
	public void onTrimMemory(int level) {

		startServiceRight();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		Logger.e(TAG, "left-service-oncreate");
		ServiceUtils.startLockService(this);
		startServiceRight();
		new Thread() {
			public void run() {
				while (true) {
					boolean isRun = ServiceUtils.isServiceWork(LeftService.this,
							"com.jj.applock.service.RightService");
					if (!isRun) {
						Message msg = Message.obtain();
						msg.what = 1;
						handler.sendMessage(msg);
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return (IBinder) cytual;
	}

	private void startServiceRight() {

		boolean isRun = ServiceUtils.isServiceWork(LeftService.this, "com.jj.applock.service.RightService");
		ServiceUtils.startLockService(this);
		if (isRun == false) {
			try {
				cytual.startService();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.e(TAG, "left-service-onStartCommand");
		return START_STICKY;
	}
	

}
