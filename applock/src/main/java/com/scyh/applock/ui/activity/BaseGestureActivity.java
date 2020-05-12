
package com.scyh.applock.ui.activity;

import com.scyh.applock.AppManager;
import com.scyh.applock.R;
import com.scyh.applock.ui.dialog.RotationDialog;
import com.scyh.applock.utils.ScreenUtil;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BaseGestureActivity extends BaseActivity {
	private static final String TAG = "BaseActivity";
	
	protected long exitTime = 0;

	public static String apkName;

	protected boolean isStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isStart = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		isStart = false;
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 退出Activity时动画
			finish();
			// overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}
		return super.onKeyDown(keyCode, event);
	}
	protected  void initData(){}

    protected  void initAction(){}
    
    
	protected void unbindDrawables(View view) {
		if (view != null) {
			if (view.getBackground() != null) {
				view.getBackground().setCallback(null);
			}
			if (view instanceof ImageView) {
				ImageView imageView = (ImageView) view;
				imageView.setImageDrawable(null);
			}
			if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
				for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
					unbindDrawables(((ViewGroup) view).getChildAt(i));
				}
				if (!(view instanceof AbsSpinner) && !(view instanceof AbsListView)) {
					((ViewGroup) view).removeAllViews();
				}
			}
		}
	}

	protected View inflateSubView(int subId, int inflateId) {
		View noNetSubTree = findViewById(inflateId);
		if (noNetSubTree == null) {
			ViewStub viewStub = (ViewStub) findViewById(subId);
			noNetSubTree = viewStub.inflate();
		}
		noNetSubTree.setVisibility(View.VISIBLE);
		return noNetSubTree;
	}

	public void initBackButton() {
		ImageView backButton = (ImageView) this.findViewById(R.id.iv_navigat_left);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				exitSystem();
			}
		});
	}
	//

	protected void exitSystem() {
		Toast.makeText(this, "再按一次退出系统...", Toast.LENGTH_SHORT).show();
	}

	protected void setImmerseLayout(View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			int statusBarHeight = ScreenUtil.getStatusBarHeight(this.getBaseContext());
			view.setPadding(0, statusBarHeight, 0, 0);
		}
	}

	protected void setImmerseLayout() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	public void finishActivity() {
		finish();
		// overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
	}

	public void setTitleBar(int id) {
		TextView tvName = (TextView) findViewById(R.id.tv_navigat_title);
		tvName.setText(id);
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
}
