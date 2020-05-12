package com.scyh.applock.ui.activity;

import com.scyh.applock.common.net.HttpHelper;
import com.scyh.applock.common.net.NetExecutorService;
import com.scyh.applock.common.net.NetExecutorService.IHttpRequestCallback;
import com.scyh.applock.utils.AppConfig;
import com.scyh.applock.utils.AppUtil;
import com.scyh.applock.utils.DESHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PayActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		b();
	}
	private void b() {

		NetExecutorService.getInstance(false).requestPostAsync(this, new IHttpRequestCallback() {

			@Override
			public void onNetCallback(String result) {
				// TODO Auto-generated method stub
				System.out.println("服务器返回。。。" + result);
				String jiemi;
				try {
					jiemi = DESHelper.decryptBasedDes(result, AppConfig.INIT_PASSWORD_RC4);
					System.out.println("解密后：" + jiemi);
//					Intent intent = new Intent(PayActivity.this, ScPayActivityImpl.class);
//					intent.putExtra("json_price", jiemi);
//					// startActivity(intent);
//					startActivityForResult(intent, 38);
					PayActivity.this.finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onException(int type, String e) {
				System.out.println("错误：" + e);

			}
		}, AppConfig.SERVICE_VIP_PRICE, "");
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==38){
			if(resultCode==RESULT_OK){
				boolean b=data.getBooleanExtra("vip_success", false);
				if(b){
					AppUtil.toast("vip开通成功");
					HttpHelper.getvipInfo(PayActivity.this);
					PayActivity.this.finish();
					return ;
				}
			}
		}
	}
}
