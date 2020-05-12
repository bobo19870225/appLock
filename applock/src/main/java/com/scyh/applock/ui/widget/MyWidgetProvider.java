package com.scyh.applock.ui.widget;

import com.scyh.applock.R;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider{
	
	private static final String TAG=MyWidgetProvider.class.getSimpleName();
	
	private String action="mmaction";
	RemoteViews rv;
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}
	
	
	
	
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		Intent intent = new Intent();
		intent.setAction(action);
		context.sendBroadcast(intent);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
//		Intent intent2=new Intent(context,LoginActivity.class);
//		ComponentName cn=new ComponentName("com.jj.applock","com.jj.applock.ui.activity.LoginActivity");
//		intent2.setComponent(cn);
//		context.startActivity(intent2);
//		PendingIntent pi=PendingIntent.getActivity(context, 0, intent2, 0);
//		
//		rv.setOnClickPendingIntent(R.id.iv_wdt, pi);
//		AppWidgetManager manager=AppWidgetManager.getInstance(context);
//		manager.updateAppWidget(R.id.iv_wdt, rv);
		//appWidgetManager.updateAppWidget(appWidgetIds, rv);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(action)){
			rv = new RemoteViews(context.getPackageName(), R.layout.widget_applauncher);
//			rv.setImageViewBitmap(R.id.iv_wdt, (BitmapDrawable)context.getResources().getDrawable(R.drawable.ic_launcher));
			rv.setImageViewResource(R.id.iv_wdt, R.drawable.ic_launcher);
			
		}
//		super.onReceive(context, intent);
	}

}
