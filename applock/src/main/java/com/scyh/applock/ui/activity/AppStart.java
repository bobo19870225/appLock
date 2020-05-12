package com.scyh.applock.ui.activity;

import com.scyh.applock.utils.LockPatternUtils;

import android.content.Intent;
import android.os.Bundle;

public class AppStart extends BaseActivity {

	private LockPatternUtils fingerUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
//		fingerUtils = new LockPatternUtils(this);
//		if (fingerUtils.savedPatternExists()) {
//			Intent intent = new Intent(this, LoginActivity.class);
//			startActivity(intent);
//		} else {
//			Intent intent = new Intent(this, GestureCreateActivity.class);
//			startActivity(intent);
//		}
	}

}
