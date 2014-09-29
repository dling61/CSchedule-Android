package com.e2w.cschedule;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.bugsense.trace.BugSenseHandler;
import com.e2w.cschedule.fragments.ParticipantFragment;
import com.e2w.cschedule.models.Confirm;
import com.e2w.cschedule.utils.CommConstant;
import com.e2w.cschedule.utils.Utils;

/**
 * This page looks like “Contact” page. But it’s different. It shows all
 * participants with role name attached and has a select button for each
 * participant. The role “participant” is Not displayed. A user can assign some
 * participants for a schedule. We call it “On Duty”
 * */
public class ParticipantActivity extends FragmentActivity {
	String activity_id = "";

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		BugSenseHandler.initAndStartSession(this, CommConstant.BUGSENSE_KEY);
		Utils.pushRightToLeft(ParticipantActivity.this);
		setContentView(R.layout.participant);
		activity_id = getIntent().getStringExtra(CommConstant.ACTIVITY_ID);
		ParticipantFragment participantFragment = new ParticipantFragment();
		participantFragment.setActivity_id(activity_id);
		ArrayList<String> selectedParticipant = getIntent()
				.getStringArrayListExtra("pins");
		
		ArrayList<Confirm> listPaticipants=new ArrayList<Confirm>();
		if(selectedParticipant!=null)
		{
			int size=selectedParticipant.size();
			for(int i=0;i<size;i++)
			{
				String str=selectedParticipant.get(i);
				String[]strs=str.split(";");
				if(strs!=null&&strs.length>=2)
				{
					listPaticipants.add(new Confirm(Integer.parseInt(strs[0]), Integer.parseInt(strs[1])));
				}
			}
			participantFragment.setSelectedParticipant(listPaticipants);
		}
		
		participantFragment.setType(getIntent().getIntExtra(CommConstant.TYPE,
				CommConstant.TYPE_CONTACT));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, participantFragment).commit();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Utils.postLeftToRight(ParticipantActivity.this);
	}
}
