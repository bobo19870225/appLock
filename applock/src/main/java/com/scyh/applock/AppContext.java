package com.scyh.applock;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import com.scyh.applock.service.LoadAppListService;
import com.scyh.applock.utils.AppConfig;
import com.scyh.applock.utils.SpUtil;

import org.litepal.LitePalApplication;

import java.util.HashMap;
import java.util.Map;

import cn.gz3create.scyh_account.ScyhAccountLib;
import cn.gz3create.scyh_account.utils.LibProduct;

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
        //安卓8.0以上禁止后台启动服务，所以要设为前台服务
        Intent i = new Intent(getApplicationContext(), LoadAppListService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i);
        } else {
            startService(i);
        }
        Map<String, String> appKey = new HashMap<>();
        appKey.put("UM", AppConfig.UM_APP_KEY);
        ScyhAccountLib.initLib(this, LibProduct.AppLock.APPID, appKey);
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
