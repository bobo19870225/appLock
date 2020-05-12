package com.scyh.applock.ui.activity;

import com.scyh.applock.R;
import com.scyh.applock.utils.VersionUtil;

import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends BaseNavigatActivity{
	private TextView mVersion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_about);
		super.initNavigatView(findViewById(R.id.navigat_toolbar));
		mVersion = (TextView)findViewById(R.id.tv_version);
		displayBackMenuOn();
		setTitleBar(R.string.page_about);
		mVersion.setText("V:"+VersionUtil.getLocalVersionName(getApplicationContext()));
	}

	
	@Override
	protected void onLeftMenuClick() {
		super.onLeftMenuClick();
		finish();
	}
}
