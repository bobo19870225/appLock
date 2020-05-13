package com.scyh.applock.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import cn.gz3create.scyh_account.ScyhAccountLib;

public class AppStart extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

//		Intent intet = new Intent(this, LoginActivity.class);
//		startActivity(intent);
		ScyhAccountLib.getInstance().login(this, 334, "ziji");
//		fingerUtils = new LockPatternUtils(this);
//		if (fingerUtils.savedPatternExists()) {
//			Intent intent = new Intent(this, LoginActivity.class);
//			startActivity(intent);
//		} else {
//			Intent intent = new Intent(this, GestureCreateActivity.class);
//			startActivity(intent);
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ScyhAccountLib.getInstance().onLoginCallback(new ScyhAccountLib.ILoginCallback() {
			@Override
			public void success() {
				Intent intet1 = new Intent(AppStart.this, MainActivity.class);
				startActivity(intet1);
				AppStart.this.finish();
			}

			@Override
			public void failed() {

			}
		}, 334, requestCode, resultCode, data);

	}
}
