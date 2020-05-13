package com.scyh.applock;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;

import com.mob.MobSDK;
import com.scyh.applock.service.LoadAppListService;
import com.scyh.applock.utils.SpUtil;

import org.litepal.LitePalApplication;

import cn.gz3create.scyh_account.ScyhAccountLib;

public class AppContext extends LitePalApplication {

    private static AppContext instance;

    public static synchronized AppContext getInstance() {
        return AppContext.instance;
    }

    private boolean isvip;

    public boolean getIsVip() {
        return isvip;
    }

    public void putvip(boolean vip) {
        this.isvip = vip;
    }


    private String user;

    public String getCurrentUser() {
        if (user == null) {
            return "";
        } else {
            return user;
        }
    }

    public void saveCurrentUser(String user) {
        this.user = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.instance = this;
        startService(new Intent(this, LoadAppListService.class));
        ScyhAccountLib.initLib(this);

        SpUtil.getInstance().init(this);
        SpUtil.getInstance().putBoolean("need_recycle", true);
    }

    public int getMyAppIconWidth() {
        int v6;
        PackageManager v5 = getPackageManager();
        String v4 = getPackageName();
        try {
            BitmapDrawable bitmap = (BitmapDrawable) v5.getApplicationIcon(v4);
            v6 = bitmap.getBitmap().getWidth();
            return v6;
        } catch (Exception v3) {
            v3.printStackTrace();
        }

        return 50;
    }


}
