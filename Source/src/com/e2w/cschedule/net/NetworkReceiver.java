package com.e2w.cschedule.net;

import com.e2w.cschedule.R;
import com.e2w.cschedule.interfaces.GetServerSettingInterface;
import com.e2w.cschedule.interfaces.LoadingInterface;
import com.e2w.cschedule.utils.CommConstant;
import com.e2w.cschedule.views.ToastDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		NetworkInfo mobNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
	  

		if (activeNetInfo != null) {
//			Toast.makeText(context,
//					"Active Network Type : " + activeNetInfo.getTypeName(),
//					Toast.LENGTH_SHORT).show();
		}
		if (mobNetInfo != null) {
//			Toast.makeText(context,
//					"Mobile Network Type : " + mobNetInfo.getTypeName(),
//					Toast.LENGTH_SHORT).show();
		}
		  if (activeNetwork != null ) {
		    
		    }
		
		if(activeNetInfo==null&&mobNetInfo==null&&activeNetwork==null)
		{
//			Toast.makeText(context,
//					"No network connection. Please try again.",
//					Toast.LENGTH_SHORT).show();
			final ToastDialog dialog = new ToastDialog(context,
					context.getResources().getString(R.string.no_network));
			dialog.show();

			dialog.btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}
		else
		{
			if (!CommConstant.DOWNLOAD_SETTING) {
				WebservicesHelper.getInstance().getServerSetting(context,new GetServerSettingInterface( ) {
					
					@Override
					public void onError(String error) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onComplete() {
						// TODO Auto-generated method stub
						
					}
				},new LoadingInterface() {
					
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						
					}
				});
			}
		}
		
	}
}