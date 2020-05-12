package com.scyh.applock.ui.view;

import com.scyh.applock.R;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * 当前钥匙页面，当手机app连接上蓝牙钥匙之后，<br>
 * 右上角的弹出框菜单
 * 
 * @author fxq33
 *
 */
public class PPWNearBy extends PopupWindow implements OnClickListener  {
	private static final String TAG=PPWNearBy.class.getSimpleName();
	private View v;
	private OnClickListener callback;
	private LinearLayout ly_detail,ly_map,ly_sayhi;
	private Button btn;

	private Activity context;
	public PPWNearBy( Activity context,OnClickListener callback) {
		this.callback=callback;
		this.context=context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inflater.inflate(R.layout.v_ppw_near_by, null);
		//int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		// 设置SelectPicPopupWindow的View
		this.setContentView(v);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(w / 2 + 60);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
		// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimationPreview);
		
		ly_detail=(LinearLayout) v.findViewById(R.id.ly_ppw_detail);
		ly_map=(LinearLayout) v.findViewById(R.id.ly_ppw_map);
		btn=(Button) v.findViewById(R.id.btn_pay_vip);
		ly_detail.setOnClickListener(this);
		ly_map.setOnClickListener(this);
		btn.setOnClickListener(this);
	}

	
	/**
	 * 显示popupWindow
	 * 
	 * @param parent
	 */
	public void showPopup() {
		if (!this.isShowing()) {
			this.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
		} else {
			this.dismiss();
		}
	}
	
	public void showPopupWindow(View v,int x,int y){
		if (!this.isShowing()) {
			this.showAtLocation(v, Gravity.START, x/2, -30);
		} else {
			this.dismiss();
		}
		showDistanse();
	}

	private void showDistanse(){
	}
	@Override
	public void onClick(View v) {
		if(v!=null){
			callback.onClick(v);
		}
		try{
			PPWNearBy.this.dismiss();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}