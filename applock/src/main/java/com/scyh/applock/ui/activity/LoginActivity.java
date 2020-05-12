package com.scyh.applock.ui.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scyh.applock.AppContext;
import com.scyh.applock.R;
import com.scyh.applock.common.net.CommonNetCallable;
import com.scyh.applock.common.net.HttpHelper;
import com.scyh.applock.common.net.NetExecutorService.IHttpRequestCallback;
import com.scyh.applock.utils.AppConfig;
import com.scyh.applock.utils.AppUtil;
import com.scyh.applock.utils.DESHelper;
import com.scyh.applock.utils.Logger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * 用户登录页面
 * @author sc
 *
 */
public class LoginActivity extends BaseNavigatActivity{
	
	private static final String TAG=LoginActivity.class.getSimpleName();
	private EditText et_phone;
	private EditText et_pwd;
	
	private SharedPreferences sp;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		super.initNavigatView(findViewById(R.id.navigat_toolbar));
		displayBackMenuOn();
		setTitleBar(R.string.page_login);
		
		sp=getSharedPreferences("applock", Context.MODE_PRIVATE);
		et_phone=(EditText) findViewById(R.id.et_login_phone);
		et_pwd=(EditText) findViewById(R.id.et_login_pwd);
		findViewById(R.id.tv_login_reg).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				jump();
			}
		});
		
		findViewById(R.id.btn_login_submit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				tomain();
				login_ready();
			}
		});
		
		findViewById(R.id.tv_login_forget).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				forgetPwd();
			}
		});
		
		initPhoneFromSp();
	}
	
	private void initPhoneFromSp(){
		String phone=sp.getString(AppConfig.SP_KEY_CACHE_LOGIN_USERNAME, "");
		if(phone!=null&&!phone.equals("")){
			et_phone.setText(phone);
		}
	}

	
	private void login_ready(){
		if(TextUtils.isEmpty(et_phone.getText())){
			AppUtil.toast("用户名不能为空");
			return ;
		}
		
		if(TextUtils.isEmpty(et_pwd.getText())){
			AppUtil.toast("密码不能为空");
			return ;
		}
		
		String sj=et_phone.getText().toString().trim();
		String mm=et_pwd.getText().toString().trim();
		login(sj, mm);
	}
	/**
	 * 
	 * 用户登录
	 * @param phone 手机号码
	 * @param pwd 登录密码
	 * 
	 */
	private void login(final String phone,String pwd){
		
		if(!CommonNetCallable.isNetUsable(this)){
			AppUtil.toast("请先连接网络");
			return ;
		}
		HttpHelper.login(this, phone, pwd, new IHttpRequestCallback() {
			
			@Override
			public void onNetCallback(String result) {
				if(result!=null&&!result.equals("")){
					String txt_res=DESHelper.decryptBasedDes(result, AppConfig.INIT_PASSWORD_RC4);
					Logger.e(TAG, txt_res);
					JSONObject j=JSON.parseObject(txt_res);
					if(j.getBooleanValue("success")){
						//成功
						
						sp.edit().putString(AppConfig.SP_KEY_CACHE_LOGIN_USERNAME, phone).commit();
						AppContext.getInstance().saveCurrentUser(phone);
						Intent intent=new Intent(LoginActivity.this,MainActivity.class);
						startActivity(intent);
						HttpHelper.getvipInfo(LoginActivity.this);
						LoginActivity.this.finish();
						
					}else{
						int cd=j.getIntValue("code");
						if(cd==-1){
							AppUtil.toast(j.getString("msg"));
							return ;
						}else{
							AppUtil.toast("登录失败,请稍候再试...");
							return ;
						}
					}
				}
			}
			
			
			@Override
			public void onException(int type, String e) {
				AppUtil.toast("登录失败,请稍候再试");
				return ;
				
			}
		});
		
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	private void jump(){
		Intent intent=new Intent(this,RegisterPhoneActivity.class);
		intent.putExtra("action_type", "reg");
		startActivity(intent);
	}
	
	private void forgetPwd(){
		Intent intent=new Intent(this,RegisterPhoneActivity.class);
		intent.putExtra("action_type", "forget_prd");
		startActivity(intent);
	}
	
	private void tomain(){
//		MZPhoneGetter.getinstance(LoginActivity.this).save();
		Intent intent=new Intent(this,MainActivity.class);
		startActivity(intent);
	}
}
