package com.dling61.calendarschedule;

import com.dling61.calendarschedule.utils.Utils;

import android.app.Activity;
import android.os.Bundle;

public class BaseUpDownActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Utils.slideUpDown(BaseUpDownActivity.this);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Utils.postLeftToRight(BaseUpDownActivity.this);
	}

}
