package com.scyh.applock.ui.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scyh.applock.AppContext;
import com.scyh.applock.R;
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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//import cn.smssdk.EventHandler;
//import cn.smssdk.SMSSDK;

public class RegisterConfirmActivity extends BaseNavigatActivity implements OnClickListener {

	private static final String TAG = RegisterConfirmActivity.class.getSimpleName();

	private static final int MSG_CODE_VALID_OK = 1;
	private static final int MSG_CODE_ERROR = 2;
	private static final int MSG_CODE_SEND = 3;

	private EditText mEditText_Code;
	private EditText mEditText_Pwd;
	private Button mButton_ok;
	private TextView mTv_code_again;

	private TimeCount mTimeCount;
	private SharedPreferences sp;

	private String action_type;
	private String phone, code;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CODE_VALID_OK:
				// 开始注册
//				AppUtil.toast("验证成功");
				mTimeCount.cancel();
				mTv_code_again.setVisibility(View.GONE);
					reg();
				break;
			case MSG_CODE_SEND:
				AppUtil.toast("验证码已经发送");
				break;
			case MSG_CODE_ERROR:
				String s = msg.obj == null ? "" : msg.obj.toString();
				if (AppUtil.isEmpty(s)) {
					AppUtil.toast("验证错误,请稍后再试...");
					return;
				} else {
					try {
						JSONObject j = JSON.parseObject(s);
						String m = j.getString("detail");
						if (!AppUtil.isEmpty(m)) {
							AppUtil.toast(m);
						} else {
							AppUtil.toast(s);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				mTimeCount.cancel();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_reg_confirm);
		initNavigatView(findViewById(R.id.navigat_toolbar));
		displayBackMenuOn();
		action_type=getIntent().getStringExtra("action_type");
		if(action_type.equals("reg")){
			setTitleBar(R.string.page_login_bycode);
		}else{
			setTitleBar(R.string.page_login_findpwd);
		}
		
		sp = getSharedPreferences("applock", Context.MODE_PRIVATE);
		mEditText_Code = (EditText) findViewById(R.id.et_reg_code);
		mEditText_Pwd = (EditText) findViewById(R.id.et_reg_pwd);
		mTv_code_again = (TextView) findViewById(R.id.tv_reg_code_again);
		mButton_ok = (Button) findViewById(R.id.btn_reg_ok);

		mButton_ok.setOnClickListener(this);
		mTv_code_again.setOnClickListener(this);
		mTimeCount = new TimeCount(60 * 1000, 1000);

		phone = getIntent().getStringExtra("tel");

//		SMSSDK.registerEventHandler(new EventHandler() {
//
//			@Override
//			public void afterEvent(int event, int result, Object data) {
//				if (data instanceof Throwable) {
//					Throwable throwable = (Throwable) data;
//					// String msg = throwable.getMessage();
//					// System.out.println("错误消息："+msg);
//					Message msg = handler.obtainMessage(MSG_CODE_ERROR);
//					msg.obj = throwable.getMessage();
//					handler.sendMessage(msg);
//				} else {
//					if (event == 3) {
//						handler.sendEmptyMessage(MSG_CODE_VALID_OK);
//					} else if (event == 2) {
//						handler.sendEmptyMessage(MSG_CODE_SEND);
//
//					} else {
//						Message msg = handler.obtainMessage(MSG_CODE_ERROR);
//						msg.obj = data.toString();
//						handler.sendMessage(msg);
//						Logger.dft("err:" + data.toString());
//					}
//				}
//			}
//		});
		mTimeCount.start();
	}

	@Override
	protected void onLeftMenuClick() {
		super.onLeftMenuClick();
		finish();
	}

	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		public void onFinish() {
			mTv_code_again.setText("再次获取");
			mTv_code_again.setEnabled(true);
			mTv_code_again.setTextColor(getResources().getColor(R.color.app_main));
		}

		public void onTick(long arg6) {
			mTv_code_again.setText("剩余时间(" + arg6 / 1000 + ")");
			mTv_code_again.setEnabled(false);
			mTv_code_again.setTextColor(getResources().getColor(R.color.gray));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reg_ok:
			trytoValidCode();
//			reg_execute("13368609001", "123456");
			break;
		case R.id.tv_reg_code_again:
//			SMSSDK.getVerificationCode("86", phone);
			mTimeCount.cancel();
			mTimeCount.start();
			break;

		default:
			break;
		}
	}

	private void trytoValidCode() {
		if (TextUtils.isEmpty(mEditText_Code.getText())) {
			AppUtil.toast("验证码不能为空");
			return;
		}

		code = mEditText_Code.getText().toString().trim();
//		SMSSDK.submitVerificationCode("86", phone, code);

	}

	private void reg_execute(final String _p, String pwd) {

		HttpHelper.reg(this, _p, pwd, new IHttpRequestCallback() {

			@Override
			public void onNetCallback(String result) {
				if (result != null && !result.equals("")) {
					String txt_res = DESHelper.decryptBasedDes(result, AppConfig.INIT_PASSWORD_RC4);
					Logger.e(TAG, txt_res);
					JSONObject j = JSON.parseObject(txt_res);
					if (j.getBooleanValue("success")) {
						if (j.getIntValue("code") == -1) {
							AppUtil.toast(j.getString("msg"));
							return;
						}
						if (j.get("data") != null) {
							int cj = Integer.valueOf(j.get("data").toString());
							if (cj > 0) {
								AppUtil.toast("注册成功");
								sp.edit().putString(AppConfig.SP_KEY_CACHE_LOGIN_USERNAME, _p).commit();
								AppContext.getInstance().saveCurrentUser(_p);
								Intent intent = new Intent(RegisterConfirmActivity.this, MainActivity.class);
								startActivity(intent);
								RegisterConfirmActivity.this.finish();
								return;
							}
						}

						AppUtil.toast("注册失败");
					} else {
						AppUtil.toast("注册失败");
						return;
					}
				}
			}

			@Override
			public void onException(int type, String e) {
				AppUtil.toast("注册失败：" + e);
				return;
			}
		});
	}
	

	private void reg() {
		if (phone == null || phone.isEmpty()) {
			AppUtil.toast("手机号码不能为空");
			return;
		}

		if (TextUtils.isEmpty(mEditText_Pwd.getText())) {
			AppUtil.toast("密码不能为空");
			return;
		}

		String pwd = mEditText_Pwd.getText().toString().trim();
		
		if(action_type.equals("reg")){
			reg_execute(phone, pwd);
		}else{
			modifyPwd(phone,pwd);
		}
	}
	
	private void modifyPwd(String phone,String pwd){
		HttpHelper.modify(this, phone, pwd,new IHttpRequestCallback() {
			
			@Override
			public void onNetCallback(String result) {
				if(result!=null&&!result.equals("")){
					String txt_res=DESHelper.decryptBasedDes(result, AppConfig.INIT_PASSWORD_RC4);
					Logger.e(TAG, txt_res);
					JSONObject j=JSON.parseObject(txt_res);
					if(j.getBooleanValue("success")){
						AppUtil.toast("密码修改成功");
						RegisterConfirmActivity.this.finish();
						return ;
					}
				}else{
					AppUtil.toast("密码重置失败");
					return ;
				}
			}
			
			@Override
			public void onException(int type, String e) {
				// TODO Auto-generated method stub
				AppUtil.toast(e);
			}
		});
	}
}
