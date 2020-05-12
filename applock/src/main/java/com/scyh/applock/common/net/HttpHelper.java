package com.scyh.applock.common.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scyh.applock.AppContext;
import com.scyh.applock.common.net.NetExecutorService.IHttpRequestCallback;
import com.scyh.applock.utils.AppConfig;
import com.scyh.applock.utils.DESHelper;

import android.content.Context;
import android.content.SharedPreferences;

public class HttpHelper {
	
	
	private static final String SERVICE_LOGIN=AppConfig.SERVICE_LOGIN+"";
	
	public static void login(Context context,String phone,String pwd,IHttpRequestCallback cbk){
		JSONObject j=new JSONObject();
		j.put("phone", phone);
		j.put("pwd", pwd);
		String data=DESHelper.encryptBasedDes(j.toJSONString(), AppConfig.INIT_PASSWORD_RC4);
		NetExecutorService.getInstance().requestPostAsync(context, cbk, SERVICE_LOGIN, data);
	}
	
	public static void reg(Context context,String phone,String pwd,IHttpRequestCallback cbk){
		JSONObject j=new JSONObject();
		j.put("phone", phone);
		j.put("pwd", pwd);
		String data=DESHelper.encryptBasedDes(j.toJSONString(), AppConfig.INIT_PASSWORD_RC4);
		NetExecutorService.getInstance().requestPostAsync(context, cbk, AppConfig.SERVICE_REGISTER, data);
	}
	
	public static void modify(Context context,String phone,String pwd,IHttpRequestCallback cbk){
		JSONObject j=new JSONObject();
		j.put("phone", phone);
		j.put("pwd", pwd);
		String data=DESHelper.encryptBasedDes(j.toJSONString(), AppConfig.INIT_PASSWORD_RC4);
		NetExecutorService.getInstance().requestPostAsync(context, cbk, AppConfig.SERVICE_MODIFY, data);
	}

	
	public static void getVip(Context context,String user,IHttpRequestCallback cbk){
		JSONObject j=new JSONObject();
		j.put("imei", user);
		String data=DESHelper.encryptBasedDes(j.toJSONString(), AppConfig.INIT_PASSWORD_RC4);
		NetExecutorService.getInstance().requestPostAsync(context, cbk, AppConfig.SERVICE_GETVIP, data);
	}
	
	public static void getvipInfo(final Context context){
		JSONObject j=new JSONObject();
		j.put("imei", AppContext.getInstance().getCurrentUser());
		String data=DESHelper.encryptBasedDes(j.toJSONString(), AppConfig.INIT_PASSWORD_RC4);
		NetExecutorService.getInstance(false).requestPostAsync(context, new IHttpRequestCallback() {
			
			@Override
			public void onNetCallback(String result) {
				// TODO Auto-generated method stub
				if(result!=null&&!result.equals("")){
					String txt_res=DESHelper.decryptBasedDes(result, AppConfig.INIT_PASSWORD_RC4);
//					System.out.println("--vip用户："+txt_res);
					JSONObject json=JSON.parseObject(txt_res);
					if(json!=null&&json.getBooleanValue("success")){
						JSONObject jdata=json.getJSONObject("data");
//						System.out.println("--vip用户-------------------1");
						SharedPreferences sp=context.getSharedPreferences("applock", Context.MODE_PRIVATE);
						if(jdata!=null){
							boolean isvip=jdata.getBooleanValue("isvip");
							
							if(isvip){
//								System.out.println("--vip用户-------------------2");
								String t=jdata.getString("vipend");
								
								sp.edit().putBoolean("vip_valid", true).putString("vip_endtime", t).commit();
								//SpfUtil.getInstance().putBoolean(SpfUtil.KEY_VIP_VALID, true);
								//SpfUtil.getInstance().putString(SpfUtil.KEY_VIP_ENDTIME, t);
								AppContext.getInstance().putvip(true);
							}else{
//								System.out.println("--vip用户-------------------3");
//								SpfUtil.getInstance().putBoolean(SpfUtil.KEY_VIP_VALID, false);
								sp.edit().putBoolean("vip_valid", false).commit();
							}
						}else{
//							SpfUtil.getInstance().putBoolean(SpfUtil.KEY_VIP_VALID, false);
							sp.edit().putBoolean("vip_valid", false).commit();
						}
					}
				}
			}
			
			@Override
			public void onException(int type, String e) {
				// TODO Auto-generated method stub
				
				
			}
		}, AppConfig.SERVICE_GETVIP, data);
	}
}
