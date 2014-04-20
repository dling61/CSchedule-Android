package com.dling61.calendarschedule;

import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.models.ParticipantTable;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.SharedReference;
import com.dling61.calendarschedule.utils.Utils;
import com.dling61.calendarschedule.views.AddParticipantView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
			view.btn_remove_contact.setVisibility(View.GONE);
			setEdittextEditable(view.et_email, true);
			setEdittextEditable(view.et_mobile, true);
			setEdittextEditable(view.et_name, true);
			view.layout_edit.setVisibility(View.GONE);
			view.layout_done.setVisibility(View.VISIBLE);
		} else if (composeType == DatabaseHelper.EXISTED) {
			view.tv_title.setText(mContext.getResources().getString(
					R.string.edit_participant));
			selectedParticipantID = myIntent.getIntExtra(
					CommConstant.CONTACT_ID, -1);
			thisParticipant = dbHelper.getParticipant(selectedParticipantID);
			view.btn_remove_contact.setVisibility(View.VISIBLE);
			setEdittextEditable(view.et_email, false);
			setEdittextEditable(view.et_mobile, false);
			setEdittextEditable(view.et_name, false);
			view.layout_edit.setVisibility(View.VISIBLE);
			view.layout_done.setVisibility(View.GONE);

			if (thisParticipant != null) {
				view.et_email.setText(thisParticipant.getEmail());
				view.et_mobile.setText(thisParticipant.getMobile());
				view.et_name.setText(thisParticipant.getName());
			}
		}
		// view.et_email.setText(thisParticipant.getEmail());
		// view.et_name.setText(thisParticipant.getName());
		// view.et_mobile.setText(thisParticipant.getMobile());

		onClickListener();
		try {
			registerReceiver(deleteContactComplete, new IntentFilter(
					CommConstant.DELETE_CONTACT_COMPLETE));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Set edittext editable or not
	 * */
	private void setEdittextEditable(EditText edittext, boolean select) {
		edittext.setFocusable(select);
		edittext.setFocusableInTouchMode(select); // user touches widget on
													// phone with touch screen
		edittext.setClickable(select);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void onClickListener() {
		view.layout_done.setOnClickListener(this);
		view.layout_back.setOnClickListener(this);
		view.btn_remove_contact.setOnClickListener(this);
		view.layout_edit.setOnClickListener(this);
		view.et_email.setOnClickListener(this);
		view.et_mobile.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == view.layout_done) {

			addParticipant();

		} else if (v == view.layout_back) {
			((Activity) mContext).finish();
		} else if (v == view.btn_remove_contact) {
			if (selectedParticipantID > 0) {
				removeParticipant();
			}
		} else if (v == view.layout_edit) {
			setEdittextEditable(view.et_email, true);
			setEdittextEditable(view.et_mobile, true);
			setEdittextEditable(view.et_name, true);
			view.layout_done.setVisibility(View.VISIBLE);
			view.layout_edit.setVisibility(View.GONE);
		} else if (v == view.et_email) {
			if (composeType == DatabaseHelper.EXISTED
					&& (view.layout_edit.getVisibility() == View.VISIBLE)) {
				Utils.sendAnEmail(mContext, thisParticipant.getEmail());
			}
		} else if (v == view.et_mobile) {
			if (composeType == DatabaseHelper.EXISTED
					&& (view.layout_edit.getVisibility() == View.VISIBLE)) {
				Utils.makeAPhoneCall(mContext, thisParticipant.getMobile());
			}
		}
	}

	BroadcastReceiver deleteContactComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			finish();
		}
	};

	/**
	 * Remove participant
	 * */
	private void removeParticipant() {
		SharedReference ref=new SharedReference();
		int currentOwnerId=ref.getCurrentOwnerId(mContext);
		Log.d("current ownerid",currentOwnerId+"");
		Log.d("participant id",thisParticipant.getID()+"");
		if (thisParticipant.getID()==currentOwnerId) {
			Toast.makeText(mContext,
					mContext.getResources().getString(R.string.your_contact),
					Toast.LENGTH_LONG).show();
			return;
		} else {
			runOnUiThread(new Runnable() {
				public void run() {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							mContext);
					alertDialog.setTitle(mContext.getResources().getString(
							R.string.caution));
					alertDialog.setMessage(mContext.getResources().getString(
							R.string.delete_contact)
							+ " " + thisParticipant.getName() + "?");
					alertDialog.setPositiveButton(mContext.getResources()
							.getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (thisParticipant != null) {
										WebservicesHelper ws = new WebservicesHelper(
												mContext);
										ws.deleteParticipant(thisParticipant);
									}
								}
							});
					alertDialog.setNegativeButton(mContext.getResources()
							.getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// Toast.makeText(mContext,
									// "You clicked on NO",
									// Toast.LENGTH_SHORT).show();
									dialog.cancel();
								}
							});
					alertDialog.show();
				}
			});
		}
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			unregisterReceiver(deleteContactComplete);
		} catch (Exception ex) {

		}
	}
}
