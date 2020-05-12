package com.scyh.applock.ui.activity;

import java.util.List;

import com.scyh.applock.R;
import com.scyh.applock.model.LockStage;
import com.scyh.applock.task.GestureCreateContract;
import com.scyh.applock.task.GestureCreatePresenter;
import com.scyh.applock.ui.view.LockPatternView;
import com.scyh.applock.ui.view.LockPatternViewPattern;
import com.scyh.applock.utils.LockPatternUtils;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class GestureCreateActivity extends BaseNavigatActivity
		implements View.OnClickListener, GestureCreateContract.View {

	private LockPatternView mLockPatternView;

	private LockStage mUiStage = LockStage.Introduction;
	protected List<LockPatternView.Cell> mChosenPattern = null; // 密码
	private static final String KEY_PATTERN_CHOICE = "chosenPattern";
	private static final String KEY_UI_STAGE = "uiStage";
	private LockPatternUtils mLockPatternUtils;
	private LockPatternViewPattern mPatternViewPattern;
	private GestureCreatePresenter mGestureCreatePresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gesture_lock);
//		setImmerseLayout(findViewById(R.id.navigat_toolbar));
//		setTitleBar(R.string.title_bar_createpwd);
		super.initNavigatView(findViewById(R.id.navigat_toolbar));
		setTitleBar(R.string.title_bar_createpwd);
		mLockPatternView = (LockPatternView) findViewById(R.id.lock_pattern_view);
		// initBackButton();
		displayBackMenuOn();
		mGestureCreatePresenter = new GestureCreatePresenter(this, this);
		initLockPatternView();
		if (savedInstanceState == null) {
			mGestureCreatePresenter.updateStage(LockStage.Introduction);
		} else {
			final String patternString = savedInstanceState.getString(KEY_PATTERN_CHOICE);
			if (patternString != null) {
				mChosenPattern = LockPatternUtils.stringToPattern(patternString);
			}
			mGestureCreatePresenter.updateStage(LockStage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
		}
	
	}

	// @Override
	// protected void initViews(Bundle savedInstanceState) {
	// mLockTip = (TextView) findViewById(R.id.lock_tip);
	// mLockPatternView = (LockPatternView)
	// findViewById(R.id.lock_pattern_view);
	// mTopLayout = (RelativeLayout) findViewById(R.id.top_layout);
	// mTopLayout.setPadding(0, SystemBarHelper.getStatusBarHeight(this),0,0);
	//
	// mGestureCreatePresenter = new GestureCreatePresenter(this, this);

	// }

	/**
	 * 初始化锁屏控件
	 */
	private void initLockPatternView() {
		mLockPatternUtils = new LockPatternUtils(this);
		mPatternViewPattern = new LockPatternViewPattern(mLockPatternView);
		mPatternViewPattern.setPatternListener(new LockPatternViewPattern.onPatternListener() {
			@Override
			public void onPatternDetected(List<LockPatternView.Cell> pattern) {
				mGestureCreatePresenter.onPatternDetected(pattern, mChosenPattern, mUiStage);
			}
		});
		mLockPatternView.setOnPatternListener(mPatternViewPattern);
		mLockPatternView.setTactileFeedbackEnabled(true);
	}

	@Override
	public void onClick(View view) {

	}

	@Override
	public void updateUiStage(LockStage stage) {
		mUiStage = stage;
	}

	@Override
	public void updateChosenPattern(List<LockPatternView.Cell> mChosenPattern) {
		this.mChosenPattern = mChosenPattern;
	}

	@Override
	public void updateLockTip(String text, boolean isToast) {
		if (isToast) {
			// ToastUtil.showToast(text);
		} else {
			// mLockTip.setText(text);
			Toast.makeText(getApplicationContext(), text, 0).show();
		}
	}

	@Override
	public void setHeaderMessage(int headerMessage) {
		// mLockTip.setText(headerMessage);
		// Toast.makeText(getApplicationContext(),
		// getResources().getString(headerMessage), 0).show();
		setTitleBar(headerMessage);
	}

	@Override
	public void lockPatternViewConfiguration(boolean patternEnabled, LockPatternView.DisplayMode displayMode) {
		if (patternEnabled) {
			mLockPatternView.enableInput();
		} else {
			mLockPatternView.disableInput();
		}
		mLockPatternView.setDisplayMode(displayMode);
	}

	@Override
	public void Introduction() {
		clearPattern();
	}

	@Override
	public void HelpScreen() {

	}

	@Override
	public void ChoiceTooShort() {
		mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong); // 路径太短
		mLockPatternView.removeCallbacks(mClearPatternRunnable);
		mLockPatternView.postDelayed(mClearPatternRunnable, 1000);
	}

	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};

	@Override
	public void moveToStatusTwo() {

	}

	@Override
	public void clearPattern() {
		mLockPatternView.clearPattern();
	}

	@Override
	public void ConfirmWrong() {
		mChosenPattern = null;
		clearPattern();
	}

	@Override
	protected void onLeftMenuClick() {

		finish();

	}

	@Override
	public void ChoiceConfirmed() {
		mLockPatternUtils.saveLockPattern(mChosenPattern); // 保存密码
		clearPattern();
		setResult(RESULT_OK);
		finish();
	}
}
