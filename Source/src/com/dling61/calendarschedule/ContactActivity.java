package com.dling61.calendarschedule;

import com.dling61.calendarschedule.fragments.ContactFragment;
import com.dling61.calendarschedule.utils.CommConstant;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ContactActivity extends FragmentActivity {
	String activity_id="";
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.participant);
		activity_id=getIntent().getStringExtra(CommConstant.ACTIVITY_ID);
		ContactFragment contactFragment = new ContactFragment();
		contactFragment.setInSideTab(true);
		contactFragment.setActivity_id(activity_id);
		
		getSupportFragmentManager().beginTransaction().replace(R.id.container,contactFragment).commit(); 
		
		
	}
}
