package com.dling61.calendarschedule.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		NetworkInfo mobNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (activeNetInfo != null) {
			Toast.makeText(context,
					"Active Network Type : " + activeNetInfo.getTypeName(),
					Toast.LENGTH_SHORT).show();
		}
		if (mobNetInfo != null) {
			Toast.makeText(context,
					"Mobile Network Type : " + mobNetInfo.getTypeName(),
					Toast.LENGTH_SHORT).show();
		}
		if(activeNetInfo==null&&mobNetInfo==null)
		{
			Toast.makeText(context,
					"No network connection. Please try again.",
					Toast.LENGTH_SHORT).show();
		}
	}
}