package com.scyh.applock.utils;

import android.util.Log;


/**
 * 日志记录类
 * 
 * @author Administrator
 *
 */
public class Logger {
	
	private static final boolean DEBUG_LOG=true;
	private static final int DEBUG_LEVEL=7;
	
	private static final int LOG_VERBOSE=1;
	private static final int LOG_DEBUG=2;
	private static final int LOG_INFO=3;
	private static final int LOG_WARN=4;
	private static final int LOG_ERROR=5;
	
	
	private static final String DFT_TAG="COM.JJ.APPLOCK";

	public static void v(String TAG,String msg){
		if(msg==null || msg.equals("")){
			return;
		}
		if(DEBUG_LOG){
			if(DEBUG_LEVEL>LOG_VERBOSE){
				Log.v(TAG, msg);
			}
		}
	}
	public static void d(String TAG,String msg){
		if(msg==null || msg.equals("")){
			return;
		}
		if(DEBUG_LOG){
			if(DEBUG_LEVEL>LOG_DEBUG){
				Log.d(TAG, msg);
			}
		}
	}
	public static void i(String TAG,String msg){
		if(msg==null || msg.equals("")){
			return;
		}
		if(DEBUG_LOG){
			if(DEBUG_LEVEL>LOG_INFO){
				Log.i(TAG, msg);
			}
		}
	}
	public static void w(String TAG,String msg){
		if(msg==null || msg.equals("")){
			return;
		}
		if(DEBUG_LOG){
			if(DEBUG_LEVEL>LOG_WARN){
				Log.w(TAG, msg);
			}
		}
	}
	public static void e(String TAG,String msg){
		if(msg==null || msg.equals("")){
			return;
		}
		if(DEBUG_LOG){
			if(DEBUG_LEVEL>LOG_ERROR){
				Log.e(TAG, msg);
			}
		}
	}
	
	public static void dft(String msg){
		if(msg!=null&&!msg.isEmpty()){
			if(DEBUG_LOG){
				Log.e(DFT_TAG, msg);
			}
		}
	}

}
