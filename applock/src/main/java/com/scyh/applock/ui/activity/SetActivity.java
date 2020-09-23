package com.scyh.applock.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.scyh.applock.AppContext;
import com.scyh.applock.AppManager;
import com.scyh.applock.R;
import com.scyh.applock.db.CommLockInfoManager;
import com.scyh.applock.service.LockService;
import com.scyh.applock.utils.AppUtil;
import com.scyh.applock.utils.LockPatternUtils;
import com.scyh.applock.utils.ServiceUtils;
import com.scyh.applock.utils.VipUtils;

import cn.gz3create.scyh_account.ScyhAccountLib;
import cn.gz3create.scyh_account.utils.LibProduct;

public class SetActivity extends BaseNavigatActivity {

    private static final String TAG = SetActivity.class.getSimpleName();

    private ImageView iv_vip;
    private TextView tv;
    private SharedPreferences sp;
    private static int count = 0;

    private TextView tv_service_open;
    private TextView tv_user;
    public static final String TAG_SERVICE_APP = "service_app_opern";
    private static CheckBox cb;
    private LockPatternUtils lockUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set);

        initNavigatView(findViewById(R.id.navigat_toolbar));
        displayBackMenuOn();
        // displayRgtRgtMenuOn(R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha);
        // displayRgtLftMenuOn(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setTitleBar(R.string.title_bar_main);
        sp = getSharedPreferences("applock", Context.MODE_PRIVATE);
        findViewById(R.id.rl_fgmore_name).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intnet = new Intent(SetActivity.this, GestureCreateActivity.class);
                startActivity(intnet);
            }
        });

        findViewById(R.id.rl_fgmore_about).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(SetActivity.this, AboutActivity.class);

                startActivity(intent);
            }
        });

        iv_vip = (ImageView) findViewById(R.id.iv_set_vip);
        tv = (TextView) findViewById(R.id.tv_vip_endtime);
        tv_user = (TextView) findViewById(R.id.tv_set_user);
        tv_user.setText(AppContext.getInstance().getCurrentUser());
        lockUtils = new LockPatternUtils(this);
        tv_service_open = (TextView) findViewById(R.id.tv_service_open);
        findViewById(R.id.rl_user).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ScyhAccountLib.getInstance().userCenter(SetActivity.this, 111);
            }
        });
        if (VipUtils.validVip()) {
            iv_vip.setBackground(getResources().getDrawable(R.drawable.golden_crown));
            String v_end = sp.getString("vip_endtime", "");
            if (!v_end.equals("")) {
                tv.setVisibility(View.VISIBLE);
                tv.setText("Vip到期时间:" + v_end);
            }
        } else {
            iv_vip.setBackground(getResources().getDrawable(R.drawable.gray_crown));
            tv.setVisibility(View.GONE);
        }
        cb = (CheckBox) findViewById(R.id.switch_compat_service);

//		boolean service_open=sp.getBoolean(TAG_SERVICE_APP, false);
//		if(service_open){
        final CommLockInfoManager manager = new CommLockInfoManager(getApplicationContext());
//			if(manager.getLockedCount()==0){
//				cb.setChecked(false);
//			}else{
//				sp.edit().putBoolean(TAG_SERVICE_APP, true).commit();
//				cb.setChecked(true);
//				tv_service_open.setText("应用锁服务已开启");
//			}
        if (sp.getBoolean(TAG_SERVICE_APP, true)) {
            cb.setChecked(true);
            tv_service_open.setText("应用锁服务已开启");
        } else {
            cb.setChecked(false);
            tv_service_open.setText("服务未开启,请开启服务");
        }
//		}else{
//			cb.setChecked(false);
//			tv_service_open.setText("服务未开启,请开启服务");
//		}

        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean checked) {

                startServer(checked);
            }
        });
        if (getIntent().getBooleanExtra("permission", true)) {
            startServer(true);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//		LockPatternUtils util = new LockPatternUtils(getApplicationContext());
//		if(count!=0||!util.hasPassword()){
//			count =0;
//		}else{
//			Intent intent = new Intent(SetActivity.this , GestureUnlockAppActivity.class);
//			intent.putExtra("Main" , "only");
//			startActivityForResult(intent, 10);
//		}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MainActivity.count = 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onLeftMenuClick() {
        super.onLeftMenuClick();
        MainActivity.count = 1;
        finish();
    }

    public void startServer(boolean b) {
        if (b) {
            sp.edit().putBoolean(TAG_SERVICE_APP, b).commit();
            tv_service_open.setText("应用锁服务已开启");
            if (!lockUtils.savedPatternExists()) {

                Intent intent = new Intent(SetActivity.this, GestureCreateActivity.class);
                startActivityForResult(intent, 5);
            }
            ServiceUtils.startLockService(SetActivity.this);
        }
//		else if(manager.getLockedCount()==0){
//			AppUtil.toast("请至少为一个应用加锁");
//			cb.setChecked(false);
//		}
        else {
            Intent intent = new Intent(SetActivity.this, LockService.class);
            stopService(intent);
            sp.edit().putBoolean(TAG_SERVICE_APP, false).apply();
            tv_service_open.setText("服务未开启,请开启服务");
            cb.setChecked(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ScyhAccountLib.getInstance().onUserCenterCallback(new ScyhAccountLib.IUserCenterCallback() {
            @Override
            public void onLogout() {//退出
                AppManager.getAppManager().AppExit(SetActivity.this);
            }

            @Override
            public void onUnregister() {

            }
        }, 111, requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 5) {
//			startActivity(new Intent(SetActivity.this , MainActivity.class));
            MainActivity.count = 1;
            finish();
            AppUtil.toast("给应用加锁吧");
        }


    }

}
