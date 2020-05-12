package com.scyh.applock.ui.activity;

import com.scyh.applock.AppManager;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AppManager.getAppManager().addActivity(this);
	}

}
