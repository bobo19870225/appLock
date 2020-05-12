package com.scyh.applock.ui.dialog;

import com.scyh.applock.R;

import android.animation.AnimatorSet;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;


public class DialogPermission extends BaseDialog {

    private TextView mBtnPermission;
    private onClickListener mOnClickListener;

    public DialogPermission(Context context) {
        super(context);
    }

    @Override
    protected float setWidthScale() {
        return 0.9f;
    }

    @Override
    protected AnimatorSet setEnterAnim() {
        return null;
    }

    @Override
    protected AnimatorSet setExitAnim() {
        return null;
    }

    @Override
    protected void init() {
        mBtnPermission = (TextView) findViewById(R.id.btn_permission);
        mBtnPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    dismiss();
                    mOnClickListener.onClick();
                }
            }
        });
    }

    public void setOnClickListener(onClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface onClickListener {
        void onClick();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_permission;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//			this.exitSystem();
			android.os.Process.killProcess(android.os.Process.myPid());    //获取PID 
			System.exit(0); 
		}
    	return super.onKeyDown(keyCode, event);
    }

}
