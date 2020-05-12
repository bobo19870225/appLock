package com.scyh.applock.ui.view;

import android.os.Handler;
import android.os.Message;

public class FrameAnimationController {

    public static final int ANIMATION_FRAME_DURATION = 16;
    private static final int MSG_ANIMATE = 1000;
    private static final Handler mHandler=new Handler(){
    	public void handleMessage(Message arg2) {
            switch(arg2.what) {
                case 1000: {
                    if(arg2.obj != null) {
                    	Runnable r=(Runnable) arg2.obj;
                    	r.run();
                    }

                    break;
                }
            }
        }
    };


    private FrameAnimationController() {
        super();
        throw new UnsupportedOperationException();
    }

    public static void requestAnimationFrame(Runnable arg4) {
        Message v0 = new Message();
        v0.what = 1000;
        v0.obj = arg4;
        FrameAnimationController.mHandler.sendMessageDelayed(v0, 16);
    }

    public static void requestFrameDelay(Runnable arg2, long arg3) {
        Message v0 = new Message();
        v0.what = 1000;
        v0.obj = arg2;
        FrameAnimationController.mHandler.sendMessageDelayed(v0, arg3);
    }
}

