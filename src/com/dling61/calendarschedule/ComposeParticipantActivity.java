package com.dling61.calendarschedule;

import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.models.ParticipantTable;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.SharedReference;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeParticipantActivity extends Activity {
	private Participant thisParticipant;
	private int composeType;
	private DatabaseHelper dbHelper;
	private EditText email_et;
	private EditText name_et;
	private EditText mobile_et;
	private TextView title_tv;
	Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.composeparticipant);
		mContext=this;
		this.findViews();
		dbHelper = DatabaseHelper.getSharedDatabaseHelper(this);
		Intent myIntent = this.getIntent();
		composeType = myIntent.getIntExtra("type", -1);
		
		SharedReference ref=new SharedReference();
		if (composeType == DatabaseHelper.NEW)
		{
			title_tv.setText("Add Participant");
			
			int newParticipantID = dbHelper.getNextParticipantID();
			int ownerid =ref.getCurrentOwnerId(mContext);
			thisParticipant = new Participant(newParticipantID,null,null,null,ownerid);
		}
		else if (composeType == DatabaseHelper.EXISTED)
		{
			title_tv.setText("Edit Participant");
			int selectedParticipantID = myIntent.getIntExtra("participantid", -1);
			thisParticipant = dbHelper.getParticipant(selectedParticipantID);
		}
		email_et.setText(thisParticipant.getEmail());
		name_et.setText(thisParticipant.getName());
		mobile_et.setText(thisParticipant.getMobile());
	}
	
	public void findViews()
	{
		email_et = (EditText) this.findViewById(R.id.compose_participant_email_et);
		name_et = (EditText) this.findViewById(R.id.compose_participant_name_et);
		mobile_et = (EditText) this.findViewById(R.id.compose_participant_mobile_et);
		title_tv = (TextView) this.findViewById(R.id.compose_participant_toptitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void composeParticipantDone(View v)
	{
		Toast t = Toast.makeText(this, "done composing participant button pressed", Toast.LENGTH_LONG);
		t.show();
		if (DatabaseHelper.isEmailValid(email_et.getText().toString()) == false)
		{
			Toast.makeText(this, "The emial address is invalid",Toast.LENGTH_LONG).show();
			return ;
		}
		thisParticipant.setEmail(email_et.getText().toString());
		thisParticipant.setName(name_et.getText().toString());
		thisParticipant.setMobile(mobile_et.getText().toString());
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
			
			Intent resultIntent = new Intent();
			resultIntent.putExtra("id", thisParticipant.getID());
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
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
		
			Intent resultIntent = new Intent();
			resultIntent.putExtra("id", thisParticipant.getID());
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}
		
	}
}
