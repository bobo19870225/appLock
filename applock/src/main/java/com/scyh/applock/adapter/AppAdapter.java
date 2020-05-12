package com.scyh.applock.adapter;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.scyh.applock.AppContext;
import com.scyh.applock.R;
import com.scyh.applock.db.CommLockInfoManager;
import com.scyh.applock.model.CommLockInfo;
import com.scyh.applock.utils.GraphUtils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class AppAdapter extends BaseAdapter {
    class ViewHolder {
        CheckBox checkBox_id_checked;
        TextView textView_app_size;
        TextView textView_app_version_name;
        TextView textView_wave_app_about;
        TextView textView_wave_app_icon;
        TextView textView_wave_app_name;
        TextView textView_wave_app_to_selected;

    }

    private static final String TAG = "AppToSelectAdapter";
    private int mAppIconWidth;
//    private LayoutInflater mInflater;
//    private List mResultInfoList;
    private List<CommLockInfo> mResultInfoList = new ArrayList<CommLockInfo>();
    private int selectedItem;
    private CommLockInfoManager mLockInfoManager;
    private Context context;
    private PackageManager packageManager;
    private CommLockInfo lockinfo;

    public AppAdapter(Context context) {
        super();
        this.selectedItem = -1;
        this.mAppIconWidth = 55;
        this.mAppIconWidth = AppContext.getInstance().getMyAppIconWidth();
        this.context=context;
        mLockInfoManager=new CommLockInfoManager(context);
        packageManager=context.getPackageManager();
    }

    public String formetFileSize(long arg8) {
        String v1;
        DecimalFormat v0 = new DecimalFormat("#.00");
        if(arg8 < 1024) {
            v1 = String.valueOf(v0.format(((double)arg8))) + "B";
        }
        else if(arg8 < 1048576) {
            v1 = String.valueOf(v0.format((((double)arg8)) / 1024)) + "K";
        }
        else if(arg8 < 1073741824) {
            v1 = String.valueOf(v0.format((((double)arg8)) / 1048576)) + "M";
        }
        else {
            v1 = String.valueOf(v0.format((((double)arg8)) / 1073741824)) + "G";
        }

        return v1;
    }

    private String getAppSize(String arg5) {
        long v1 = 1200;
        File v0 = new File(arg5);
        if((v0.exists()) && (v0.isFile())) {
            v1 = v0.length();
        }

        return this.formetFileSize(v1);
    }

    public int getCount() {
        int v0 = this.mResultInfoList == null ? 0 : this.mResultInfoList.size();
        return v0;
    }

    

    public Object getItem(int arg2) {
    	if(mResultInfoList!=null&&mResultInfoList.size()>0){
    		return mResultInfoList.get(arg2);
    	}
    	return null;
    }

    public long getItemId(int arg3) {
        return ((long)arg3);
    }

//    public ResolveInfo getSelectedItem() {
//        return this.getItem2(this.selectedItem);
//    }

    public View getView(int arg12, View arg13, ViewGroup arg14) {
        ViewHolder v3=null;
        if(arg13 == null) {
//            arg13 = View.inflate(context, R.layout.activity_, null);
        	arg13=View.inflate(context, R.layout.activity_app_item, null);
            v3 = new ViewHolder();
            v3.textView_wave_app_icon = (TextView) arg13.findViewById(R.id.textView_wave_app_icon);
            v3.textView_wave_app_name = (TextView) arg13.findViewById(R.id.textView_wave_app_name);
            v3.textView_wave_app_about = (TextView) arg13.findViewById(R.id.textView_wave_app_about);
            v3.textView_app_version_name = (TextView) arg13.findViewById(R.id.textView_app_version_name);
            v3.textView_app_size = (TextView) arg13.findViewById(R.id.textView_app_size);
            v3.checkBox_id_checked = (CheckBox) arg13.findViewById(R.id.checkBox_id_checked);
            arg13.setTag(v3);
        }
        else {
        	v3=(ViewHolder) arg13.getTag();
        }

//        ResolveInfo v4 = this.getItem2(arg12);
        lockinfo=mResultInfoList.get(arg12);
        v3.checkBox_id_checked.setTag(lockinfo);
      //  final String pkgname = lockinfo.getPackageName();
        String appname = packageManager.getApplicationLabel(lockinfo.getAppInfo()).toString();
        v3.checkBox_id_checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg8, boolean arg9) {
               
                if(arg9) {
                    //LockJsonSaveUtil.addLockApp(v1);
                	lockinfo.setLocked(true);
                    mLockInfoManager.lockCommApplication(lockinfo.getPackageName());
                }
                else {
                	lockinfo.setLocked(false);
                  //  LockJsonSaveUtil.deleteLockApp(v1);
                    mLockInfoManager.unlockCommApplication(lockinfo.getPackageName());
                }
            }
        });
//        if(LockJsonSaveUtil.isContainsTheApp(v4.activityInfo.packageName)) {
//            v3.checkBox_id_checked.setChecked(true);
//            Log.d("AppToSelectAdapter", "选择了=  appPackgeName=" + v4.activityInfo.packageName);
//        }
//        else {
//            v3.checkBox_id_checked.setChecked(false);
//        }

        if(lockinfo != null) {
        	BitmapDrawable bitmap=(BitmapDrawable) packageManager.getApplicationIcon(lockinfo.getAppInfo());
            v3.textView_wave_app_icon.setBackground(bitmap);
            v3.textView_wave_app_name.setText(appname);
            try {
                PackageInfo v6 = packageManager.getPackageInfo(lockinfo.getPackageName(), 4096);
                v3.textView_wave_app_about.setText(this.setDateFormat(v6.lastUpdateTime));
                v3.textView_app_version_name.setText("版本：" + v6.versionName);
                v3.textView_app_size.setText(this.getAppSize(v6.applicationInfo.publicSourceDir));
            }
            catch(Exception v2) {
                v2.printStackTrace();
            }

            
            
            if(bitmap.getBitmap().getWidth() > this.mAppIconWidth) {
                v3.textView_wave_app_icon.setBackground(GraphUtils.zoomDrawable(bitmap, this.mAppIconWidth, this.mAppIconWidth));
            }

            v3.textView_wave_app_icon.setWidth(this.mAppIconWidth);
            v3.textView_wave_app_icon.setHeight(this.mAppIconWidth);
        }

        return arg13;
    }

    public void refreshAllApps(List arg1) {
        this.mResultInfoList = arg1;
        this.notifyDataSetChanged();
    }

    public void selectedItem(int arg1) {
        this.selectedItem = arg1;
        this.notifyDataSetChanged();
    }

    private CharSequence setDateFormat(long arg5) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(arg5));
    }
}

