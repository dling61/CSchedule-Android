package com.dling61.calendarschedule;

import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.models.ParticipantTable;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.SharedReference;
import com.dling61.calendarschedule.views.AddParticipantView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class AddNewContactActivity extends Activity implements OnClickListener {
	private Participant thisParticipant;
	private int composeType;
	private DatabaseHelper dbHelper;
	AddParticipantView view;
	Context mContext;
	int selectedParticipantID = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		view = new AddParticipantView(mContext);
		this.setContentView(view.layout);

		Intent myIntent = this.getIntent();
		composeType = myIntent.getIntExtra("type", -1);
		dbHelper = DatabaseHelper.getSharedDatabaseHelper(this);
		SharedReference ref = new SharedReference();
		if (composeType == DatabaseHelper.NEW) {
			view.tv_title.setText(mContext.getResources().getString(
					R.string.add_participant));

			int newParticipantID = dbHelper.getNextParticipantID();
			int ownerid = ref.getCurrentOwnerId(mContext);
			thisParticipant = new Participant(newParticipantID, null, null,
					null, ownerid);
			view.btn_remove_activity.setVisibility(View.GONE);
		} else if (composeType == DatabaseHelper.EXISTED) {
			view.tv_title.setText(mContext.getResources().getString(
					R.string.edit_participant));
			selectedParticipantID = myIntent.getIntExtra(
					CommConstant.CONTACT_ID, -1);
			thisParticipant = dbHelper.getParticipant(selectedParticipantID);
			view.btn_remove_activity.setVisibility(View.VISIBLE);
			if (thisParticipant != null) {
				view.et_email.setText(thisParticipant.getEmail());
				view.et_mobile.setText(thisParticipant.getMobile());
				view.et_name.setText(thisParticipant.getName());
			}
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

	private void onClickListener() {
		view.layout_next.setOnClickListener(this);
		view.layout_back.setOnClickListener(this);
		view.btn_remove_activity.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == view.layout_next) {

			addParticipant();

		} else if (v == view.layout_back) {
			((Activity) mContext).finish();
		} else if (v == view.btn_remove_activity) {
			if (selectedParticipantID > 0) {
				removeParticipant();
			}
		}
	}

	/**
	 * Remove participant
	 * */
	private void removeParticipant() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle(mContext.getResources()
				.getString(R.string.caution));
		alertDialog.setMessage(mContext.getResources().getString(
				R.string.delete_contact));
		alertDialog.setPositiveButton(
				mContext.getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (thisParticipant != null) {
							WebservicesHelper ws = new WebservicesHelper(
									mContext);
							ws.deleteParticipant(thisParticipant);
						}
					}
				});
		alertDialog.setNegativeButton(
				mContext.getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Toast.makeText(mContext, "You clicked on NO",
						// Toast.LENGTH_SHORT).show();
						dialog.cancel();
					}
				});
		alertDialog.show();
	}

	/**
	 * Add participant
	 * */
	public void addParticipant() {

		if (DatabaseHelper.isEmailValid(view.et_email.getText().toString()) == false) {
			Toast.makeText(this, "The email address is invalid",
					Toast.LENGTH_LONG).show();
			return;
		}
		thisParticipant.setEmail(view.et_email.getText().toString());
		thisParticipant.setName(view.et_name.getText().toString());
		thisParticipant.setMobile(view.et_mobile.getText().toString());
		if (composeType == DatabaseHelper.NEW) {
			ContentValues newParticipant = new ContentValues();
			newParticipant.put(ParticipantTable.last_Modified, "noupload");
			newParticipant.put(ParticipantTable.participant_Name,
					thisParticipant.getName());
			newParticipant.put(ParticipantTable.own_ID,
					thisParticipant.getOwnerID());
			newParticipant.put(ParticipantTable.participant_ID,
					thisParticipant.getID());
			newParticipant.put(ParticipantTable.participant_Mobile,
					thisParticipant.getMobile());
			newParticipant.put(ParticipantTable.participant_Email,
					thisParticipant.getEmail());
			newParticipant.put(ParticipantTable.is_Registered, 1);
			newParticipant.put(ParticipantTable.is_Deleted, 0);
			newParticipant.put(ParticipantTable.is_Sychronized, 0);
			dbHelper.insertParticipant(newParticipant);
			WebservicesHelper ws = new WebservicesHelper(mContext);
			ws.addParticipant(thisParticipant);
		} else if (composeType == DatabaseHelper.EXISTED) {
			ContentValues cv = new ContentValues();
			cv.put(ParticipantTable.participant_Name, thisParticipant.getName());
			cv.put(ParticipantTable.participant_Mobile,
					thisParticipant.getMobile());
			cv.put(ParticipantTable.participant_Email,
					thisParticipant.getEmail());
			cv.put(ParticipantTable.is_Registered, 1);
			cv.put(ParticipantTable.is_Deleted, 0);
			cv.put(ParticipantTable.is_Sychronized, 0);
			dbHelper.updateParticipant(thisParticipant.getID(), cv);

			WebservicesHelper ws = new WebservicesHelper(mContext);
			ws.updateParticipant(thisParticipant);
		}

	}
}
