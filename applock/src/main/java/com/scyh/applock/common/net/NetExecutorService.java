package com.scyh.applock.common.net;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class NetExecutorService {

	private static final String TAG = NetExecutorService.class.getSimpleName();

	private static NetExecutorService instance;
	private int timeout;
	private boolean showDialog;

	/**
	 * http请求执行成功
	 */
	private static final int HTTP_EXECUTE_EXCEPTION_SUCCESS = 1;
	/**
	 * http请求执行超时
	 */
	private static final int HTTP_EXECUTE_EXCEPTION_TIMOUT = 2;
	/**
	 * http请求执行网络异常
	 */
	private static final int HTTP_EXECUTE_EXCEPTION_NET = 3;
	/**
	 * http请求执行操作错误（涵盖其它所有类型错误）
	 */
	private static final int HTTP_EXECUTE_EXCEPTION_CALL = 0;
	/**
	 * http请求默认超时5秒
	 */

	private static final int HTTP_EXECUTE_TIMOUT_DEFAULT = 60;

	public interface IHttpRequestCallback {

		void onNetCallback(String result);

		void onException(int type, String e);

	}

	public static NetExecutorService getInstance() {
		if (NetExecutorService.instance == null) {
			synchronized (NetExecutorService.class) {
				if (NetExecutorService.instance == null) {
					NetExecutorService.instance = new NetExecutorService();
				}
			}
		}
		NetExecutorService.instance.showDialog = true;
		NetExecutorService.instance.timeout = HTTP_EXECUTE_TIMOUT_DEFAULT;
		return NetExecutorService.instance;
	}

	public static NetExecutorService getInstance(int timeout) {
		if (NetExecutorService.instance == null) {
			synchronized (NetExecutorService.class) {
				NetExecutorService.instance = new NetExecutorService();
			}
		}
		NetExecutorService.instance.showDialog = true;
		NetExecutorService.instance.timeout = timeout;
		return NetExecutorService.instance;
	}

	public static NetExecutorService getInstance(boolean showDialog) {
		if (NetExecutorService.instance == null) {
			synchronized (NetExecutorService.class) {
				NetExecutorService.instance = new NetExecutorService();
			}
		}
		NetExecutorService.instance.showDialog = showDialog;
		NetExecutorService.instance.timeout = HTTP_EXECUTE_TIMOUT_DEFAULT;
		return NetExecutorService.instance;
	}

	public static NetExecutorService getInstance(int timeout, boolean showDialog) {
		if (NetExecutorService.instance == null) {
			synchronized (NetExecutorService.class) {
				NetExecutorService.instance = new NetExecutorService();
			}
		}
		NetExecutorService.instance.showDialog = showDialog;
		NetExecutorService.instance.timeout = timeout;
		return NetExecutorService.instance;
	}

	private NetExecutorService() {
	}

	/**
	 * HTTP GET异步请求方法，默认超时3秒，自带提示进度条对话框
	 * 
	 * @param context
	 * @param cbk
	 * @param host
	 */
	public void requestGetAsync(Context context, IHttpRequestCallback cbk, String host) {
		requestAsync(CommonNetCallable.HTTP_METHOD_GET, context, cbk, host, "", timeout, showDialog);
	}

	/**
	 * HTTP POST异步请求方法，默认超时3秒，自带提示进度条对话框
	 * 
	 * @param context
	 * @param cbk
	 * @param host
	 */
	public void requestPostAsync(Context context, IHttpRequestCallback cbk, String host, String data) {
		requestAsync(CommonNetCallable.HTTP_METHOD_POST, context, cbk, host, data, timeout, showDialog);
	}

	public void requestHttpStream(Context context, IHttpRequestCallback cbk, String host, String data) {
		requestAsync(CommonNetCallable.HTTP_METHOD_STREAM, context, cbk, host, data, timeout, showDialog);
	}

	/**
	 * HTTP PUT异步请求方法，默认超时3秒，自带提示进度条对话框
	 * 
	 * @param context
	 * @param cbk
	 * @param host
	 */
	public void requestPutAsync(Context context, IHttpRequestCallback cbk, String host, String data) {
		requestAsync(CommonNetCallable.HTTP_METHOD_PUT, context, cbk, host, data, timeout, showDialog);
	}

	/**
	 * HTTP DELETE异步请求方法，默认超时3秒，自带提示进度条对话框
	 * 
	 * @param context
	 * @param cbk
	 * @param host
	 */
	public void requestDeleteAsync(Context context, IHttpRequestCallback cbk, String host, String data) {
		requestAsync(CommonNetCallable.HTTP_METHOD_DELETE, context, cbk, host, data, timeout, showDialog);
	}

	private static void requestAsync(final int type, final Context context, final IHttpRequestCallback cbk,
			final String host, final String data, final int timeout, final boolean show) {
		final ProgressDialog dialog = new ProgressDialog(context, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
		dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Inverse);
		dialog.setMessage("操作中,请稍后...");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		if (show) {
			dialog.show();
		}
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				switch (msg.what) {
				case HTTP_EXECUTE_EXCEPTION_SUCCESS:
					cbk.onNetCallback(msg.obj == null ? null : msg.obj.toString());
					break;
				case HTTP_EXECUTE_EXCEPTION_TIMOUT:
					cbk.onException(HTTP_EXECUTE_EXCEPTION_TIMOUT, "操作超时");
					break;
				case HTTP_EXECUTE_EXCEPTION_NET:
					cbk.onException(HTTP_EXECUTE_EXCEPTION_NET, "网络错误");
					break;
				default:
					cbk.onException(HTTP_EXECUTE_EXCEPTION_CALL, "操作异常");
					break;
				}
			}
		};
		new Thread(new Runnable() {

			@Override
			public void run() {
				String result;
				Message msg;
				try {
					result = executeSync(context, type, host, data, timeout);
					msg = handler.obtainMessage(HTTP_EXECUTE_EXCEPTION_SUCCESS);
					msg.obj = result;
					handler.sendMessage(msg);
				} catch (TimeoutException e) {
					handler.sendEmptyMessage(HTTP_EXECUTE_EXCEPTION_TIMOUT);
				} catch (NetException e) {
					handler.sendEmptyMessage(HTTP_EXECUTE_EXCEPTION_NET);
				} catch (CallException e) {
					handler.sendEmptyMessage(HTTP_EXECUTE_EXCEPTION_CALL);
				}
			}
		}).start();
	}

	private static String executeSync(Context context, int type, String host, String data, int timeout)
			throws TimeoutException, NetException, CallException {
		CommonNetCallable call = new CommonNetCallable(type, host, data);
		ExecutorService exec = Executors.newFixedThreadPool(1);
		try {
			if (CommonNetCallable.isNetUsable(context)) {
				//Log.v(TAG, "请求服务器：host->" + host + "\n data:" + data);
				Future<String> future = exec.submit(call);
				String result = future.get(timeout, TimeUnit.SECONDS);
				//Log.v(TAG, "服务器返回：" + result);
				return result;
			}
		} catch (InterruptedException e) {
			throw new CallException();
		} catch (ExecutionException e) {
			throw new CallException();
		} finally {
			exec.shutdown();
		}
		throw new NetException();
	}
}
