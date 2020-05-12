package com.scyh.applock.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * 一个旋转的对话框
 * 
 * @author fxq
 *
 */
public class RotationDialog extends DialogFragment {

	public static RotationDialog newInstance(boolean cancelable, String content) {
		RotationDialog dialog = new RotationDialog();
		Bundle data = new Bundle();
		data.putBoolean("cancelable", cancelable);
		data.putString("content", content);
		dialog.setArguments(data);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setStyle(STYLE_NO_TITLE, 0);
		Bundle data = getArguments();
		String content = data.getString("content");
		boolean cancelable = data.getBoolean("cancelable");
		setCancelable(cancelable);
		ProgressDialog dialog = new ProgressDialog(getActivity(),
				ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
		dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Inverse);
		dialog.setMessage(content);
		return dialog;
	}
}
