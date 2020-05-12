
package com.scyh.applock.ui.activity;

import com.scyh.applock.R;
import com.scyh.applock.utils.ScreenUtil;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseNavigatActivity extends BaseActivity {
	private static final String TAG = "BaseActivity";

	private View navigat_view;
	private ImageView iv_left, iv_rgt_lft, iv_rgt_rgt;
	private LinearLayout ly_left, ly_rgt_lft, ly_rgt_rgt;
	private TextView tv;
	public static String app_name;

	public enum BACKMENU_OFFMODEL {
		P_KEEP_POS, P_GONE
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("InlinedApi")
	protected void initNavigatView(View view) {
		navigat_view = view;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			int statusBarHeight = ScreenUtil.getStatusBarHeight(this.getBaseContext());
			view.setPadding(0, statusBarHeight, 0, 0);
		}

		ly_left = (LinearLayout) navigat_view.findViewById(R.id.ly_navigat_left);
		iv_left = (ImageView) navigat_view.findViewById(R.id.iv_navigat_left);
		ly_rgt_lft = (LinearLayout) navigat_view.findViewById(R.id.ly_navigat_rgt_lft);
		ly_rgt_rgt = (LinearLayout) navigat_view.findViewById(R.id.ly_navigat_rgt_rgt);
		iv_rgt_lft = (ImageView) navigat_view.findViewById(R.id.iv_navigat_rgt_lft);
		iv_rgt_rgt = (ImageView) navigat_view.findViewById(R.id.iv_navigat_rgt_rgt);
		tv = (TextView) navigat_view.findViewById(R.id.tv_navigat_title);

		displayBackMenuOff(BACKMENU_OFFMODEL.P_GONE);
		displayRgtLftMenuOff();
		displayRgtRgtMenuOff();
	}

	protected void displayBackMenuOff(BACKMENU_OFFMODEL m) {
		if (m == BACKMENU_OFFMODEL.P_GONE) {
			ly_left.setVisibility(View.GONE);
		} else if (m == BACKMENU_OFFMODEL.P_KEEP_POS) {
			ly_left.setVisibility(View.VISIBLE);
		}
		iv_left.setVisibility(View.GONE);
	}

	/**
	 * 显示导航左侧返回菜单 <br>
	 * 默认显示图片
	 */
	protected void displayBackMenuOn() {
		ly_left.setVisibility(View.VISIBLE);
		iv_left.setVisibility(View.VISIBLE);
		iv_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onLeftMenuClick();
			}
		});
	}

	/**
	 * 显示导航左侧返回菜单 <br>
	 * 指定需要设置的背景图片
	 */
	protected void displayBackMenuOn(int bitmap_id) {
		displayBackMenuOn();
		iv_left.setBackground(getResources().getDrawable(bitmap_id));
	}

	protected void onLeftMenuClick() {
	}

	/**
	 * 隐藏右侧靠左菜单
	 */
	private void displayRgtLftMenuOff() {
		ly_rgt_lft.setVisibility(View.GONE);
	}

	/**
	 * 隐藏右侧靠右菜单
	 */
	private void displayRgtRgtMenuOff() {
		ly_rgt_rgt.setVisibility(View.GONE);
	}

	/**
	 * 显示右侧靠左菜单
	 * 
	 * @param bitmap_id 分辨率48*48的图
	 */
	protected void displayRgtLftMenuOn(int bitmap_id) {
		ly_rgt_lft.setVisibility(View.VISIBLE);
		iv_rgt_lft.setVisibility(View.VISIBLE);
		if(bitmap_id!=0){
			iv_rgt_lft.setBackground(getResources().getDrawable(bitmap_id));
		}
		iv_rgt_lft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onRgtLftMenuClick();
			}
		});
	}

	protected void onRgtLftMenuClick() {

	}

	/**
	 * 显示右侧靠右菜单
	 * 
	  @param bitmap_id 分辨率48*48的图
	 */
	protected void displayRgtRgtMenuOn(int bitmap_id) {
		ly_rgt_rgt.setVisibility(View.VISIBLE);
		iv_rgt_rgt.setVisibility(View.VISIBLE);
		if(bitmap_id!=0){
			iv_rgt_rgt.setBackground(getResources().getDrawable(bitmap_id));
		}
		iv_rgt_rgt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onRgtRgtMenuClick(v);
			}
		});
	}
	

	protected void onRgtRgtMenuClick(View v) {

	}

	public void setTitleBar(int id) {
		TextView tvName = (TextView) findViewById(R.id.tv_navigat_title);
		tvName.setText(id);
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
		super.onDestroy();
	}

	// protected void unbindDrawables(View view) {
	// if (view != null) {
	// if (view.getBackground() != null) {
	// view.getBackground().setCallback(null);
	// }
	// if (view instanceof ImageView) {
	// ImageView imageView = (ImageView) view;
	// imageView.setImageDrawable(null);
	// }
	// if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
	// for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	// unbindDrawables(((ViewGroup) view).getChildAt(i));
	// }
	// if (!(view instanceof AbsSpinner) && !(view instanceof AbsListView)) {
	// ((ViewGroup) view).removeAllViews();
	// }
	// }
	// }
	// }

	// protected View inflateSubView(int subId, int inflateId) {
	// View noNetSubTree = findViewById(inflateId);
	// if (noNetSubTree == null) {
	// ViewStub viewStub = (ViewStub) findViewById(subId);
	// noNetSubTree = viewStub.inflate();
	// }
	// noNetSubTree.setVisibility(View.VISIBLE);
	// return noNetSubTree;
	// }
	//
	// public void initBackButton() {
	// ImageView backButton = (ImageView)
	// this.findViewById(R.id.durian_back_image);
	// backButton.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View v) {
	//// exitSystem();
	// }
	// });
	// }

	protected void setImmerseLayout(View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			int statusBarHeight = ScreenUtil.getStatusBarHeight(this.getBaseContext());
			view.setPadding(0, statusBarHeight, 0, 0);
		}
	}

	// protected void setImmerseLayout() {
	// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	// Window window = getWindow();
	// window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
	// WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	// }
	// }

}
