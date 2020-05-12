package com.scyh.applock.ui.dialog;

import com.scyh.applock.R;
import com.scyh.applock.ui.dialog.DialogPermission.onClickListener;

import android.animation.AnimatorSet;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class RefusedDialog extends BaseDialog{
	 public RefusedDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private TextView mBtnPermission;
	private TextView mExit;
	    private onClickListener mOnClickListener;

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
	        mBtnPermission = (TextView) findViewById(R.id.new_get);
	        mExit =  (TextView) findViewById(R.id.exit);
	        mBtnPermission.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	                if (mOnClickListener != null) {
	                    dismiss();
	                    mOnClickListener.onClick();
	                }
	            }
	        });
	        mExit.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					android.os.Process.killProcess(android.os.Process.myPid());    //获取PID 
					System.exit(0); 
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
	        return R.layout.dialog_refused;
	    }
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	// TODO Auto-generated method stub
	    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//				this.exitSystem();
				android.os.Process.killProcess(android.os.Process.myPid());    //获取PID 
				System.exit(0); 
			}
	    	return super.onKeyDown(keyCode, event);
	    	
	    }
}
