package com.scyh.applock.ui.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Window;

import com.scyh.applock.AppConstants;
import com.scyh.applock.R;
import com.scyh.applock.model.LockStage;
import com.scyh.applock.service.LockService;
import com.scyh.applock.ui.view.LockPatternView;
import com.scyh.applock.ui.view.LockPatternViewPattern;
import com.scyh.applock.utils.LockPatternUtils;
import com.scyh.applock.utils.LockUtil;
import com.scyh.applock.utils.ServiceUtils;

import java.util.List;

/**
 * Created by xian on 2017/2/17.
 */

public class GestureUnlockAppActivity extends BaseNavigatActivity {

	private LockPatternView mLockPatternView;

	private LockStage mUiStage = LockStage.Introduction;
	protected static List<LockPatternView.Cell> mChosenPattern = null; // 密码
	private static final String KEY_PATTERN_CHOICE = "chosenPattern";
	private static final String KEY_UI_STAGE = "uiStage";
	private LockPatternUtils mLockPatternUtils;
	private LockPatternViewPattern mPatternViewPattern;
	private String topkg = "";
	private ActivityManager activityManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gesture_lock);
		initNavigatView(findViewById(R.id.navigat_toolbar));
		displayBackMenuOn();
		setTitleBar(R.string.password_gestrue_tips);
		mLockPatternView = (LockPatternView) findViewById(R.id.lock_pattern_view);
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		initLockPatternView();
		if (savedInstanceState == null) {
		} else {
			final String patternString = savedInstanceState.getString(KEY_PATTERN_CHOICE);
			if (patternString != null) {
				mChosenPattern = LockPatternUtils.stringToPattern(patternString);
			}
		}

		topkg = getIntent().getStringExtra(AppConstants.LOCK_PACKAGE_NAME);
	}

	/**
	 * 初始化锁屏控件
	 */
	private void initLockPatternView() {
		mLockPatternUtils = new LockPatternUtils(this);
		mPatternViewPattern = new LockPatternViewPattern(mLockPatternView);
		mPatternViewPattern.setPatternListener(new LockPatternViewPattern.onPatternListener() {
			@Override
			public void onPatternDetected(List<LockPatternView.Cell> pattern) {
				// mGestureCreatePresenter.onPatternDetected(pattern,
				// mChosenPattern, mUiStage);
				
				if (mLockPatternUtils.checkPattern(pattern)) {
//					
					mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
//					Intent intent = new Intent();
//					// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					// startActivity(intent);
//					LockUtil.goHome(GestureUnlockAppActivity.this);
					Intent intent = new Intent(LockService.UNLOCK_ACTION);
                    intent.putExtra(LockService.LOCK_SERVICE_LASTTIME, System.currentTimeMillis());
                    intent.putExtra(LockService.LOCK_SERVICE_LASTAPP, topkg);
                    sendBroadcast(intent);
					finish();
					if (("only").equals(getIntent().getStringExtra("Main"))) {
						MainActivity.count = 1;
						finish();
					}
				} else {
					mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
					setTitleBar(R.string.password_error_count);
					new Handler().postDelayed(mClearPatternRunnable, 500);
				}
			}
		});
		mLockPatternView.setOnPatternListener(mPatternViewPattern);
		mLockPatternView.setTactileFeedbackEnabled(true);
	}

	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
			setTitleBar(R.string.password_gestrue_tips);
			activityManager.killBackgroundProcesses(topkg);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			try {

				// Intent intent=new Intent("fxq_recycle_task");
				// sendBroadcast(intent);
				LockUtil.goHome(GestureUnlockAppActivity.this);
//				
				ServiceUtils.startLockService(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onLeftMenuClick() {
		super.onLeftMenuClick();

		onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
	}

}
