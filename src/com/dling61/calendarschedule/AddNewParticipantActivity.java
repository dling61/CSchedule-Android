package com.dling61.calendarschedule;

import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.models.ParticipantTable;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.SharedReference;
import com.dling61.calendarschedule.views.AddParticipantView;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class AddNewParticipantActivity extends Activity implements OnClickListener{
	private Participant thisParticipant;
	private int composeType;
	private DatabaseHelper dbHelper;
	AddParticipantView view;
	Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext=this;
		view=new AddParticipantView(mContext);
		this.setContentView(view.layout);
	
		
		Intent myIntent = this.getIntent();
		composeType = myIntent.getIntExtra("type", -1);
		dbHelper = DatabaseHelper.getSharedDatabaseHelper(this);
		SharedReference ref=new SharedReference();
		if (composeType == DatabaseHelper.NEW)
		{
			view.tv_title.setText(mContext.getResources().getString(R.string.add_participant));
			
			int newParticipantID = dbHelper.getNextParticipantID();
			int ownerid =ref.getCurrentOwnerId(mContext);
			thisParticipant = new Participant(newParticipantID,null,null,null,ownerid);
		}
		else if (composeType == DatabaseHelper.EXISTED)
		{
			view.tv_title.setText(mContext.getResources().getString(R.string.edit_participant));
			int selectedParticipantID = myIntent.getIntExtra("participantid", -1);
			thisParticipant = dbHelper.getParticipant(selectedParticipantID);
		}
		view.et_email.setText(thisParticipant.getEmail());
		view.et_name.setText(thisParticipant.getName());
		view.et_mobile.setText(thisParticipant.getMobile());
	
		
		onClickListener();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void onClickListener()
	{
		view.btn_next.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==view.btn_next)
		{
			composeParticipantDone();
		}
	}
	
	public void composeParticipantDone()
	{
	
		if (DatabaseHelper.isEmailValid(view.et_email.getText().toString()) == false)
		{
			Toast.makeText(this, "The email address is invalid",Toast.LENGTH_LONG).show();
			return ;
		}
		thisParticipant.setEmail(view.et_email.getText().toString());
		thisParticipant.setName(view.et_name.getText().toString());
		thisParticipant.setMobile(view.et_mobile.getText().toString());
		if (composeType == DatabaseHelper.NEW)
		{
			ContentValues newParticipant  = new ContentValues();
			newParticipant.put(ParticipantTable.last_Modified, "noupload");
			newParticipant.put(ParticipantTable.participant_Name, thisParticipant.getName());
			newParticipant.put(ParticipantTable.own_ID, thisParticipant.getOwnerID());
			newParticipant.put(ParticipantTable.participant_ID, thisParticipant.getID());
			newParticipant.put(ParticipantTable.participant_Mobile, thisParticipant.getMobile());
			newParticipant.put(ParticipantTable.participant_Email, thisParticipant.getEmail());
			newParticipant.put(ParticipantTable.is_Registered, 1);
			newParticipant.put(ParticipantTable.is_Deleted, 0);
			newParticipant.put(ParticipantTable.is_Sychronized, 0);
			dbHelper.insertParticipant(newParticipant);
			WebservicesHelper ws=new WebservicesHelper(mContext);
			ws.addParticipant(thisParticipant);
			
//			Intent resultIntent = new Intent();
//			resultIntent.putExtra("id", thisParticipant.getID());
//			setResult(Activity.RESULT_OK, resultIntent);
//			finish();
		}
		else
		{
			ContentValues cv  = new ContentValues();
			cv.put(ParticipantTable.participant_Name, thisParticipant.getName());
			cv.put(ParticipantTable.participant_Mobile, thisParticipant.getMobile());
			cv.put(ParticipantTable.participant_Email, thisParticipant.getEmail());
			cv.put(ParticipantTable.is_Registered, 1);
			cv.put(ParticipantTable.is_Deleted, 0);
			cv.put(ParticipantTable.is_Sychronized, 0);
			dbHelper.updateParticipant(thisParticipant.getID(), cv);
			
			WebservicesHelper ws=new WebservicesHelper(mContext);
			ws.updateParticipant(thisParticipant);
		
//			Intent resultIntent = new Intent();
//			resultIntent.putExtra("id", thisParticipant.getID());
//			setResult(Activity.RESULT_OK, resultIntent);
//			finish();
		}
		
	}
}
