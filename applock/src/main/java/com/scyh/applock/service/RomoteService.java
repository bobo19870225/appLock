package com.scyh.applock.service;

import com.scyh.applock.utils.ServiceUtils;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class RomoteService extends Service {
    MyConn conn;
    MyBinder binder;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        conn = new MyConn();
        binder = new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

      //  Toast.makeText(this, " 远程服务started", Toast.LENGTH_SHORT).show();
        this.bindService(new Intent(this, LockService.class), conn, Context.BIND_IMPORTANT);

        return START_STICKY;
    }

    static class MyBinder extends IMyAidlInterface.Stub {
        @Override
        public String getServiceName() throws RemoteException {
            return RomoteService.class.getSimpleName();
        }
    }

    class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        	ServiceUtils.startLockService(RomoteService.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          //  Toast.makeText(RomoteService.this, "本地服务killed", Toast.LENGTH_SHORT).show();

            //开启本地服务
            RomoteService.this.startService(new Intent(RomoteService.this, LockService.class));
            //绑定本地服务
            RomoteService.this.bindService(new Intent(RomoteService.this, LockService.class), conn, Context.BIND_IMPORTANT);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        //开启本地服务
     //   RomoteService.this.startService(new Intent(RomoteService.this, LocalService.class));
        //绑定本地服务
       // RomoteService.this.bindService(new Intent(RomoteService.this, LocalService.class), conn, Context.BIND_IMPORTANT);

    }
}