package com.scyh.applock.adapter;

import java.util.ArrayList;
import java.util.List;

import com.scyh.applock.R;
import com.scyh.applock.db.CommLockInfoManager;
import com.scyh.applock.model.CommLockInfo;
import com.scyh.applock.ui.activity.GestureCreateActivity;
import com.scyh.applock.ui.activity.PayActivity;
import com.scyh.applock.ui.activity.SetActivity;
import com.scyh.applock.ui.view.PPWNearBy;
import com.scyh.applock.utils.AppUtil;
import com.scyh.applock.utils.LockPatternUtils;
import com.scyh.applock.utils.ServiceUtils;
import com.scyh.applock.utils.VipUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xian on 2017/3/1.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private List<CommLockInfo> mLockInfos = new ArrayList<CommLockInfo>();
    private Context mContext;
    private PackageManager packageManager;
    private CommLockInfoManager mLockInfoManager;

    public MainAdapter(Context mContext) {
        this.mContext = mContext;
        packageManager = mContext.getPackageManager();
        mLockInfoManager = new CommLockInfoManager(mContext);
    }

    public void setLockInfos(List<CommLockInfo> lockInfos) {
        mLockInfos.clear();
        CommLockInfo info;
        for(int i=0;i<lockInfos.size();i++){
        	info=lockInfos.get(i);
        	mLockInfos.add(info);
        	 notifyItemInserted(i);
        }
//        for(CommLockInfo info:lockInfos){
//        	mLockInfos.add(info);
//        	 notifyDataSetChanged();
//        	 notifyItemInserted(position);
//        }
//        mLockInfos.addAll(lockInfos);
       
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_list, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        final CommLockInfo lockInfo = mLockInfos.get(position);
        initData(holder.mAppName, holder.mSwitchCompat, holder.mAppIcon, lockInfo);
        holder.mSwitchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeItemLockStatus(holder.mSwitchCompat, lockInfo, position);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData(TextView tvAppName, CheckBox switchCompat, ImageView mAppIcon, CommLockInfo lockInfo) {
        tvAppName.setText(packageManager.getApplicationLabel(lockInfo.getAppInfo()));
        switchCompat.setChecked(lockInfo.isLocked());
        ApplicationInfo appInfo = lockInfo.getAppInfo();
        mAppIcon.setImageDrawable(packageManager.getApplicationIcon(appInfo));
    }

    public void changeItemLockStatus(CheckBox checkBox, CommLockInfo info, int position) {
    	if(checkBox.isChecked()){
    		if(!VipUtils.validVip()){
        		if(mLockInfoManager.getLockedCount()>=1){
        			checkBox.setChecked(!checkBox.isChecked());
//        			AppConfig.a((Activity)mContext, checkBox, "02");
        			PPWNearBy ppv=new PPWNearBy((Activity)mContext,new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							Intent intent=new Intent(mContext,PayActivity.class);
							mContext.startActivity(intent);
						}
					});
        			ppv.showAsDropDown(checkBox);
        			return ;
        		}
        	}
    	}
        if (checkBox.isChecked()) {
        	LockPatternUtils util = new LockPatternUtils(mContext);
        	if(!util.hasPassword()){
        		AppUtil.toast("请先设置解锁密码");
        		
        	}else{
        		info.setLocked(true);
                mLockInfoManager.lockCommApplication(info.getPackageName());
        	}
            
        } else {
            info.setLocked(false);
            mLockInfoManager.unlockCommApplication(info.getPackageName());
        }
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mLockInfos.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        private ImageView mAppIcon;
        private TextView mAppName;
        private CheckBox mSwitchCompat;

        public MainViewHolder(View itemView) {
            super(itemView);
            mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            mAppName = (TextView) itemView.findViewById(R.id.app_name);
            mSwitchCompat = (CheckBox) itemView.findViewById(R.id.switch_compat);
        }
    }
}
