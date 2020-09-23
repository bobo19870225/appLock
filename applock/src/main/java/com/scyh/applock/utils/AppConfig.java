package com.scyh.applock.utils;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.scyh.applock.R;

public class AppConfig {

    private static final String HOST_SERVER_SC = "http://47.100.205.195:8066/scapi/api";
    private static final String HOST_SERVER_LOCAL = "http://192.168.31.247:8080/scapi/api";


//	public static final String SERVICE_DEFAULT=HOST_SERVER_LOCAL;

    public static final String SERVICE_DEFAULT = HOST_SERVER_SC;

    public static final String SERVICE_OPEN_VIP = SERVICE_DEFAULT + "/applock/openvip";
    public static final String SERVICE_VIP_PRICE = SERVICE_DEFAULT + "/scpay/applock_price";
    public static final String SERVICE_GETVIP = SERVICE_DEFAULT + "/qm/getvip";
    public static final String SERVICE_LOGIN = SERVICE_DEFAULT + "/applock/login";
    public static final String SERVICE_REGISTER = SERVICE_DEFAULT + "/applock/reg";
    public static final String SERVICE_MODIFY = SERVICE_DEFAULT + "/applock/modifypwd";
    //    友盟 Appkey：5f61d308b473963242a06c2c
    public static final String UM_APP_KEY = "5f6320e5b473963242a1cd5e";

    public static final String SP_KEY_CACHE_LOGIN_USERNAME = "cache_username";
    public static final String SP_KEY_CACHE_LOGIN_ = "";


    public static final String INIT_PASSWORD_RC4 = "Sc!@#f33";

    public static void a(final Activity arg5, View arg6, String arg7) {
        View v1 = View.inflate(arg5, R.layout.popup_select, null);
        final PopupWindow v2 = new PopupWindow(v1, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        v2.setAnimationStyle(R.style.PopupAnimation_alpha);
        v2.setBackgroundDrawable(new BitmapDrawable());
        v2.showAtLocation(arg6, 170, 0, 0);
        v1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg2, MotionEvent arg3) {
                v2.dismiss();
                return false;
            }
        });
        v1.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg4) {//arg5, v2
                Intent v0 = new Intent();
//				v0.setClass(arg5, PayActivity.class);
//				this.a.startActivity(v0);
//				AppUtil.jumpActivity(arg5, v0);
//				arg5.overridePendingTransition(R.anim.popup_enter_right, R.anim.popup_enter_left);
                v2.dismiss();
            }
        });
//		View v0 = v1.findViewById(R.id.content);
//		((TextView) v0).setText(Html.fromHtml(arg7));
//		v0 = v1.findViewById(R.id.title);
//		((TextView) v0).setText(Html.fromHtml("<strong>开通会员,解锁所有功能。</strong>"));

//		 AlertDialog.Builder builder = new AlertDialog.Builder(arg5);  
//	        LayoutInflater inflater = arg5.getLayoutInflater();  
//	        final View layout = inflater.inflate(R.layout.popup_select, null);//获取自定义布局
//	        builder.setView(layout);  
//	        layout.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
//				public void onClick(View arg4) {//arg5, v2
//					Intent v0 = new Intent();
//					v0.setClass(arg5, PayActivity.class);
////					this.a.startActivity(v0);
//					AppUtil.jumpActivity(arg5, v0);
//					arg5.overridePendingTransition(R.anim.popup_enter_right, R.anim.popup_enter_left);
//					builder.dismiss();
//				}
//			});
    }

}
