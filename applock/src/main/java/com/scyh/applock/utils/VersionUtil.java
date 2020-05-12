package com.scyh.applock.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionUtil {
	public static int getLocalVersion(Context ctx) {
		int localVersion = 0;
		try {
			PackageInfo packageInfo = ctx.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(ctx.getPackageName(), 0);
			localVersion = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return localVersion;
	}

	/**
	 * 获取本地软件版本号名称
	 */
	public static String getLocalVersionName(Context ctx) {
		String localVersion = "";
		try {
			PackageInfo packageInfo = ctx.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(ctx.getPackageName(), 0);
			localVersion = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return localVersion;
	}
}
