package com.e2w.cschedule;

import com.e2w.cschedule.utils.CommConstant;
import com.e2w.cschedule.utils.Utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.bugsense.trace.BugSenseHandler;

public class BaseActivity extends FragmentActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(BaseActivity.this, CommConstant.BUGSENSE_KEY);
		
//			Utils.checkCurrentVersion(BaseActivity.this);
		
		try {
			registerReceiver(getServerSetting, new IntentFilter(
					CommConstant.SERVER_SETTING_SUCCESSFULLY));
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Utils.postLeftToRight(BaseActivity.this);
	}
	BroadcastReceiver getServerSetting = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			
			
			
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(getServerSetting);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
