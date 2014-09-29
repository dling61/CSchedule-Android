package com.e2w.cschedule;

import com.bugsense.trace.BugSenseHandler;
import com.e2w.cschedule.fragments.ContactFragment;
import com.e2w.cschedule.utils.CommConstant;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ContactActivity extends FragmentActivity {
	String activity_id="";
	@Override
	protected void onCreate(Bundle arg0) {
		BugSenseHandler.initAndStartSession(this, CommConstant.BUGSENSE_KEY);
		overridePendingTransition(R.anim.animation_enter,
			      R.anim.animation_leave);
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.participant);
		activity_id=getIntent().getStringExtra(CommConstant.ACTIVITY_ID);
		ContactFragment contactFragment = new ContactFragment();
		contactFragment.setInSideTab(true);
		contactFragment.setActivity_id(activity_id);
		
		getSupportFragmentManager().beginTransaction().replace(R.id.container,contactFragment).commit(); 
		
		
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.push_left_in,
			      R.anim.push_left_out);
	}
}
