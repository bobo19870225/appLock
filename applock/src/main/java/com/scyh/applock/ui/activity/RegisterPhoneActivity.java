package com.scyh.applock.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.scyh.applock.R;
import com.scyh.applock.utils.AppUtil;
//import cn.smssdk.SMSSDK;

public class RegisterPhoneActivity extends BaseNavigatActivity{
	
	private static final String TAG=RegisterPhoneActivity.class.getSimpleName();
	private Button mButton_Submit;;
	private EditText mEditText_Phone;
	
	private String action_type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_reg_phone);
		initNavigatView(findViewById(R.id.navigat_toolbar));
		displayBackMenuOn();
		setTitleBar(R.string.page_get_code);
		mButton_Submit=(Button) findViewById(R.id.btn_reg_getcode);
		mEditText_Phone=(EditText) findViewById(R.id.et_reg_phone);
		action_type=getIntent().getStringExtra("action_type");
		mButton_Submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getVerifyCode();
//				jump();
			}
		});
	}
	/**
	 * 
	 */
	private void getVerifyCode() {
		if(TextUtils.isEmpty(mEditText_Phone.getText())){
			AppUtil.toast("手机号码不能为空");
			return ;
		}
		
		String phone=mEditText_Phone.getText().toString().trim();
		if(!AppUtil.isMobile(phone)){
			AppUtil.toast("手机号码不正确");
			return ;
		}

//		SMSSDK.setAskPermisionOnReadContact(true);
//		SMSSDK.getVerificationCode("86",phone);
		
		Intent intent=new Intent(this,RegisterConfirmActivity.class);
		intent.putExtra("tel", phone);
		intent.putExtra("action_type", action_type);
		startActivity(intent);
		finish();
	}

	private void jump(){
		Intent intent=new Intent(this,RegisterConfirmActivity.class);
		startActivity(intent);
	}

	
	@Override
	protected void onLeftMenuClick() {
		super.onLeftMenuClick();
		finish();
	}
}
