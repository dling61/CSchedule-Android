package com.e2wstudy.cschedule.net;

import com.e2wstudy.cschedule.db.DatabaseHelper;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.SharedReference;
import com.e2wstudy.cschedule.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

public class OnUpgradeBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		String appVersion=Utils.getVersionName(context);
		if (VERSION.SDK_INT <= VERSION_CODES.HONEYCOMB
				&& !context.getPackageName().equals(
						intent.getData().getSchemeSpecificPart())) {
			android.util.Log.d("AppLog", "other apps were upgraded");
			try {
				
				new SharedReference().setVersion(context, appVersion);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}
		android.util.Log.d("AppLog", "current app was upgraded");
		try {
			new SharedReference().setVersion(context,appVersion);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		CommConstant.UPDATE=false;
		CommConstant.MUST_UPDATE=false;
	}
}