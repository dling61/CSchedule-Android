package com.e2w.cschedule;

import com.bugsense.trace.BugSenseHandler;
import com.e2w.cschedule.utils.CommConstant;
import com.e2w.cschedule.utils.Utils;

import android.app.Activity;
import android.os.Bundle;

public class BaseUpDownActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, CommConstant.BUGSENSE_KEY);
		Utils.slideUpDown(BaseUpDownActivity.this);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Utils.postLeftToRight(BaseUpDownActivity.this);
	}

}
