package com.dling61.calendarschedule;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.dling61.calendarschedule.fragments.ParticipantFragment;
import com.dling61.calendarschedule.utils.CommConstant;

/**
 * This page looks like “Contact” page. But it’s different. It shows all participants with role name attached and has a select button for each participant. The role “participant” is Not displayed.
 * A user can assign some participants for a schedule. We call it “On Duty”
 * */
public class ParticipantActivity extends FragmentActivity {
	String activity_id="";
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.participant);
		activity_id=getIntent().getStringExtra(CommConstant.ACTIVITY_ID);
		ParticipantFragment participantFragment = new ParticipantFragment();
		participantFragment.setActivity_id(activity_id);	
		participantFragment.setType(getIntent().getIntExtra(CommConstant.TYPE, CommConstant.TYPE_CONTACT));
		getSupportFragmentManager().beginTransaction().replace(R.id.container,participantFragment).commit(); 
		
		
	}
}
