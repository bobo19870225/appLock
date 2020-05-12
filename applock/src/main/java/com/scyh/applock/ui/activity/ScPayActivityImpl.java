//package com.scyh.applock.ui.activity;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.scyh.applock.AppContext;
//import com.scyh.applock.utils.AppConfig;
//import com.scyh.applock.utils.DESHelper;
//import com.scyh.applock.utils.Logger;
//import com.scyh.lib.net.NetExecutorService;
//import com.scyh.lib.net.NetExecutorService.IHttpRequestCallback;
//import com.scyh.lib.pay.SCPayActivity;
//import com.scyh.lib.utils.PhoneUtils;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//
//public class ScPayActivityImpl extends SCPayActivity {
//
//	private static final String TAG = ScPayActivityImpl.class.getSimpleName();
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		displayBackMenuOn();
//
//		rpay_wx.setVisibility(View.GONE);
//	}
//
//	@Override
//	protected void onLeftMenuClick() {
//		super.onLeftMenuClick();
//		this.finish();
//	}
//
//	@Override
//	public void openVip(Context context, IHttpRequestCallback cbk, String trade_no, int vtype) {
//		// TODO Auto-generated method stub
//
//		JSONObject j = new JSONObject();
//		j.put("phone", AppContext.getInstance().getCurrentUser());
//		j.put("vt", vtype);
//		j.put("trade_no", trade_no);
//		j.put("model", PhoneUtils.getPhoneModel());
//		// System.out.println("开通vip发送参数："+j.toJSONString());
//		Logger.e(TAG, j.toJSONString());
//		String json = DESHelper.encryptBasedDes(j.toJSONString(), AppConfig.INIT_PASSWORD_RC4);
//		NetExecutorService.getInstance().requestPostAsync(context, new IHttpRequestCallback() {
//
//			@Override
//			public void onNetCallback(String result) {
//				// TODO Auto-generated method stub
//				if (result != null && !result.equals("")) {
//					String txt_res = DESHelper.decryptBasedDes(result, AppConfig.INIT_PASSWORD_RC4);
//					System.out.println("开通会员接口返回：" + txt_res);
//					JSONObject j_res = JSON.parseObject(txt_res);
//
//					if (j_res.getBooleanValue("success")) {
//						Intent data = ScPayActivityImpl.this.getIntent();
//						data.putExtra("vip_success", true);
//						ScPayActivityImpl.this.setResult(RESULT_OK, data);
//						ScPayActivityImpl.this.finish();
//						return;
//					}
//					//
//				}
//				Intent data = ScPayActivityImpl.this.getIntent();
//				data.putExtra("vip_success", false);
//				ScPayActivityImpl.this.setResult(RESULT_OK, data);
//				ScPayActivityImpl.this.finish();
//			}
//
//			@Override
//			public void onException(int type, String e) {
//				// TODO Auto-generated method stub
//
//			}
//		}, AppConfig.SERVICE_OPEN_VIP, json);
//		// super.openVip(context, cbk, trade_no, vtype);
//	}
//
//}
