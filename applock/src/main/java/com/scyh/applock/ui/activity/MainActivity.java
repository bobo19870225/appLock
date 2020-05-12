package com.scyh.applock.ui.activity;

import java.security.PrivilegedActionException;
import java.util.List;

import com.scyh.applock.AppConstants;
import com.scyh.applock.AppManager;
import com.scyh.applock.R;
import com.scyh.applock.adapter.MainAdapter;
import com.scyh.applock.model.CommLockInfo;
import com.scyh.applock.service.LocalService;
import com.scyh.applock.service.LockService;
import com.scyh.applock.service.RomoteService;
import com.scyh.applock.task.LockMainContract;
import com.scyh.applock.task.LockMainPresenter;
import com.scyh.applock.ui.dialog.DialogPermission;
import com.scyh.applock.ui.dialog.RefusedDialog;
import com.scyh.applock.ui.dialog.RotationDialog;
import com.scyh.applock.utils.AppUtil;
import com.scyh.applock.utils.LockPatternUtils;
import com.scyh.applock.utils.LockUtil;
import com.scyh.applock.utils.ServiceUtils;
import com.scyh.applock.utils.SpUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends BaseNavigatActivity implements LockMainContract.View, View.OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	private LockMainPresenter mLockMainPresenter;

	private RecyclerView mRecyclerView;
	private MainAdapter mMainAdapter;
	private SharedPreferences sp;
	protected long exitTime = 0;
	private RotationDialog dialog;
	public static final String TAG_SERVICE_APP="service_app_opern";
	public static  int count = 0;
	private boolean judge = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initNavigatView(findViewById(R.id.navigat_toolbar));
		displayBackMenuOn();
//		displayBackMenuOn(R.drawable.ic_launcher);
		displayRgtRgtMenuOn(R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha);
		//displayRgtLftMenuOn(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		setTitleBar(R.string.title_bar_main);
		sp = getSharedPreferences("applock", Context.MODE_PRIVATE);
		count = 1;

		dialog=RotationDialog.newInstance(false, "请稍后...");
		dialog.show(getFragmentManager(), "");
		
		mLockMainPresenter = new LockMainPresenter(this, this);
		mLockMainPresenter.loadAppInfo(this);

		mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		mMainAdapter = new MainAdapter(this);
		mRecyclerView.setAdapter(mMainAdapter);

		boolean isFirstLock = SpUtil.getInstance().getBoolean(AppConstants.LOCK_IS_FIRST_LOCK, true);
		if (isFirstLock) { // 如果第一次
			myShowDialog();
		}
//		ServiceUtils.startLockService(this);
		if(sp.getBoolean(TAG_SERVICE_APP, false)){
			startService(new Intent(this,LocalService.class));	
		}
//		startService(new Intent(this,LeftService.class));
		startService(new Intent(this,RomoteService.class));
		setServer();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LockPatternUtils util  = new LockPatternUtils(getApplicationContext());
		if(count!=0||!util.hasPassword()){
			count =0;
		}else{
			Intent intent = new Intent(MainActivity.this , GestureUnlockAppActivity.class);
			intent.putExtra("Main" , "only");
			startActivityForResult(intent, 10);
		}
	}
	private int RESULT_ACTION_USAGE_ACCESS_SETTINGS = 1;
	private int RESULT_ACTION_MANAGE_OVERLAY_PERMISSION = 2;

	private void myShowDialog() {
		// 如果没有获得查看使用情况权限和手机存在查看使用情况这个界面
		if (!LockUtil.isStatAccessPermissionSet(this) && LockUtil.isNoOption(this)) {
			DialogPermission dialog = new DialogPermission(this);
			dialog.show();
			dialog.setOnClickListener(new DialogPermission.onClickListener() {
				@Override
				public void onClick() {
					Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
					startActivityForResult(intent, RESULT_ACTION_USAGE_ACCESS_SETTINGS);
				}
			});
		} else {
			// gotoCreatePwdActivity();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_ACTION_USAGE_ACCESS_SETTINGS) {
			if (LockUtil.isStatAccessPermissionSet(this)) {
				LockPatternUtils util  = new LockPatternUtils(getApplicationContext());
				if(!util.hasPassword()){
					Toast.makeText(getApplicationContext(), "授权成功，开始设置密码", 0).show();
					Intent intent = new Intent(MainActivity.this,SetActivity.class);
					intent.putExtra("permission", true);
					startActivityForResult(intent, 10);
				}
				
			} else {
				// ToastUtil.showToast("没有权限");
//				Toast.makeText(getApplicationContext(), "权限失败", 0).show();
//				finish();
				RefusedDialog dia = new RefusedDialog(MainActivity.this);
				dia.show();
				dia.setOnClickListener(new RefusedDialog.onClickListener() {
					@Override
					public void onClick() {
						Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
						startActivityForResult(intent, RESULT_ACTION_USAGE_ACCESS_SETTINGS);
					}
				});
			}
		}
		if(requestCode == 10){
			
		}
	}
	


	protected void exitSystem() {

//		if (SystemClock.uptimeMillis() - exitTime > 2000) {
//			Toast.makeText(this, "再按一次退出系统...", Toast.LENGTH_SHORT).show();
//			exitTime = SystemClock.uptimeMillis();
//		} else {
//			executeExit();
//		}
		moveTaskToBack(false);
	}
	@Override
	protected void onLeftMenuClick() {
		super.onLeftMenuClick();
		exitSystem();
	}
	
	@Override
	protected void onRgtRgtMenuClick(View v) {
		super.onRgtRgtMenuClick(v);
		
//		PPWNearBy p=new PPWNearBy(this, new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				switch (arg0.getId()) {
//				case R.id.ly_ppw_detail:
					Intent intent=new Intent(MainActivity.this,SetActivity.class);
					intent.putExtra("permission", false);
					startActivity(intent);
//					break;
//
//				default:
//					break;
//				}
//				
//			}
//		});
//		p.showAsDropDown(v);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//			this.exitSystem();
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected void executeExit() {

		final RotationDialog dialog = RotationDialog.newInstance(false, "正在退出,请稍后...");
		dialog.show(this.getFragmentManager(), "");
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				dialog.dismissAllowingStateLoss();
				AppManager.getAppManager().AppExit(getApplicationContext());
			}
		}, 2048);
	}
	@Override
	public void onClick(View v) {

	}

	@Override
	public void loadAppInfoSuccess(List<CommLockInfo> list) {
		if(dialog!=null&&dialog.getShowsDialog()){
			dialog.dismiss();
		}
		mMainAdapter.setLockInfos(list);
	}
	
	private void setServer(){
//			SharedPreferences sp = getSharedPreferences("applock", Context.MODE_PRIVATE);
//			if(!sp.getBoolean(SetActivity.TAG_SERVICE_APP, true)){
//				ServiceUtils.startLockService(MainActivity.this);
//				sp.edit().putBoolean(SetActivity.TAG_SERVICE_APP, true);
//			}
	}
	

	
}
