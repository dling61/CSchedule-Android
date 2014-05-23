package com.dling61.calendarschedule;

import org.json.JSONException;
import org.json.JSONObject;

import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.models.ParticipantTable;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.SharedReference;
import com.dling61.calendarschedule.utils.Utils;
import com.dling61.calendarschedule.views.AddParticipantView;
import com.dling61.calendarschedule.views.ConfirmDialog;
import com.dling61.calendarschedule.views.LoadingPopupViewHolder;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author khoahuyen
 * @category Add new/modify/delete contact
 * */
public class AddNewContactActivity extends BaseActivity implements OnClickListener {
	private Participant thisParticipant;
	private int composeType;
	AddParticipantView view;
	Context mContext;
	int selectedParticipantID = -1;
	ProgressDialog progress = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		view = new AddParticipantView(mContext);
		this.setContentView(view.layout);

		Intent myIntent = this.getIntent();
		composeType = myIntent.getIntExtra(CommConstant.TYPE, -1);
		if (composeType == DatabaseHelper.EXISTED) {
			selectedParticipantID = myIntent.getIntExtra(
					CommConstant.CONTACT_ID, -1);
		}
		initData();
		onClickListener();
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

	/**
	 * On click listener all views
	 * */
	private void onClickListener() {
		view.titleBar.layout_save.setOnClickListener(this);
		view.titleBar.layout_back.setOnClickListener(this);
		view.btn_remove_contact.setOnClickListener(this);
		view.titleBar.layout_edit.setOnClickListener(this);
		view.et_email.setOnClickListener(this);
		view.et_mobile.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == view.titleBar.layout_save) {
			Utils.hideKeyboard(AddNewContactActivity.this, view.et_email);
			Utils.hideKeyboard(AddNewContactActivity.this, view.et_mobile);
			Utils.hideKeyboard(AddNewContactActivity.this, view.et_name);
			addContact();
		} else if (v == view.titleBar.layout_back) {
			Utils.hideKeyboard(AddNewContactActivity.this, view.et_email);
			Utils.hideKeyboard(AddNewContactActivity.this, view.et_mobile);
			Utils.hideKeyboard(AddNewContactActivity.this, view.et_name);
			((Activity) mContext).finish();
		} else if (v == view.btn_remove_contact) {
			if (selectedParticipantID > 0) {
				removeParticipant();
			}
		} else if (v == view.titleBar.layout_edit) {
			setEdittextEditable(view.et_email, true);
			setEdittextEditable(view.et_mobile, true);
			setEdittextEditable(view.et_name, true);
			view.titleBar.layout_save.setVisibility(View.VISIBLE);
			view.titleBar.layout_next.setVisibility(View.GONE);
			view.btn_remove_contact.setVisibility(View.GONE);
			view.titleBar.layout_edit.setVisibility(View.GONE);
		} else if (v == view.et_email) {
			if (composeType == DatabaseHelper.EXISTED
					&& (view.titleBar.layout_next.getVisibility() == View.VISIBLE)
					&& (!thisParticipant.getEmail().equals(""))) {
				Utils.sendAnEmail(mContext, thisParticipant.getEmail());
			}
		} else if (v == view.et_mobile) {
			if (composeType == DatabaseHelper.EXISTED
					&& (view.titleBar.layout_next.getVisibility() == View.VISIBLE)
					&& (!thisParticipant.getMobile().equals(""))) {
				Utils.makeAPhoneCall(mContext, thisParticipant.getMobile());
			}
		}
	}

	/**
	 * receiver when delete contact
	 * */
	BroadcastReceiver deleteContactComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			finish();
		}
	};

	/**
	 * Remove contact Cannot remove themselves
	 * */
	private void removeParticipant() {
		SharedReference ref = new SharedReference();
		int currentOwnerId = ref.getCurrentOwnerId(mContext);
		Log.d("current ownerid", currentOwnerId + "");
		Log.d("participant delete", thisParticipant.getID() + "");
		if (thisParticipant.getID() == currentOwnerId) {
			Toast.makeText(mContext,
					mContext.getResources().getString(R.string.your_contact),
					Toast.LENGTH_LONG).show();
			return;
		} else {
			runOnUiThread(new Runnable() {
				public void run() {
					showAlertDeleteContact();
				}
			});
		}
	}

	JsonHttpResponseHandler deleteContactHandler = new JsonHttpResponseHandler() {
		public void onSuccess(JSONObject response) {
			Log.d("delete str", response.toString());
			try {
				if (response.getString("lastmodified") != null) {

					ContentValues cv = new ContentValues();
					cv.put(ParticipantTable.is_Deleted, 1);
					cv.put(ParticipantTable.is_Sychronized, 1);
					// dbHelper.updateParticipant(id, cv);
					if (thisParticipant.getID() > 0) {
						DatabaseHelper dbHelper = DatabaseHelper
								.getSharedDatabaseHelper(mContext);
						dbHelper.deleteParticipant(thisParticipant.getID());
					}
					((Activity) mContext).finish();
					Intent intent = new Intent(
							CommConstant.DELETE_CONTACT_COMPLETE);
					mContext.sendBroadcast(intent);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onFailure(Throwable e, String response) {
			// Response failed :
			Log.i("failure response", response);
			Log.i("fail", e.toString());
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.delete_contact_error)
							+ "\n" + response.toString(), Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			showLoading(mContext);
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			super.onFinish();
			// progress.dismiss();
			dimissDialog();
		}
	};
	LoadingPopupViewHolder loadingPopup;

	// show loading
	public void showLoading(Context mContext) {
		if (loadingPopup == null) {
			loadingPopup = new LoadingPopupViewHolder(mContext,
					CategoryTabActivity.DIALOG_LOADING_THEME);
		}
		loadingPopup.setCancelable(false);
		loadingPopup.show();
	}

	public void dimissDialog() {
		if (loadingPopup != null && loadingPopup.isShowing()) {
			loadingPopup.dismiss();
		}
	}

	/**
	 * Show alert delete contact Call ws delete contact if click yes
	 * */
	private void showAlertDeleteContact() {
		final ConfirmDialog dialog = new ConfirmDialog(mContext, mContext
				.getResources().getString(R.string.delete_contact)
				+ " "
				+ thisParticipant.getName() + "?");
		dialog.show();
		dialog.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (selectedParticipantID > 0) {
					WebservicesHelper ws = new WebservicesHelper(mContext);
					ws.deleteParticipant(thisParticipant);
				}
			}
		});
	}

	/**
	 * Add contact if create new and update if edit mode
	 * */
	public void addContact() {

		String email = view.et_email.getText().toString();
		String name = view.et_name.getText().toString();
		String mobile = view.et_mobile.getText().toString();

		boolean isNameOK = !name.equals("");
		boolean isEmailOK = Utils.isEmailValid(email);
		String createLog = (isNameOK == false ? "\n"
				+ getResources().getString(R.string.username_empty) : "")
				+ (isEmailOK == false ? "\n"
						+ getResources().getString(R.string.email_invalid) : "");
		if (!createLog.equals("")) {
			Toast.makeText(this, createLog, Toast.LENGTH_LONG).show();
			return;
		}

		thisParticipant.setEmail(email);
		thisParticipant.setName(name);
		thisParticipant.setMobile(mobile);

		ContentValues cv = new ContentValues();
		cv.put(ParticipantTable.last_Modified, "noupload");
		cv.put(ParticipantTable.participant_Name, thisParticipant.getName());
		cv.put(ParticipantTable.participant_Mobile, thisParticipant.getMobile());
		cv.put(ParticipantTable.participant_Email, thisParticipant.getEmail());
		cv.put(ParticipantTable.is_Registered, 1);
		cv.put(ParticipantTable.is_Deleted, 0);
		cv.put(ParticipantTable.is_Sychronized, 0);
		WebservicesHelper ws = new WebservicesHelper(mContext);
		DatabaseHelper dbHelper = DatabaseHelper
				.getSharedDatabaseHelper(mContext);

		/**
		 * Update information into database and call webservice post information
		 * to server
		 */
		if (composeType == DatabaseHelper.NEW) {
			cv.put(ParticipantTable.own_ID, thisParticipant.getOwnerID());
			cv.put(ParticipantTable.participant_ID, thisParticipant.getID());
			Log.d("add member id", thisParticipant.getID() + "");
			cv.put(ParticipantTable.user_login,
					new SharedReference().getCurrentOwnerId(mContext));
			dbHelper.insertParticipant(cv);
			ws.addParticipant(thisParticipant);
		} else if (composeType == DatabaseHelper.EXISTED) {
			dbHelper.updateParticipant(thisParticipant.getID(), cv);
			ws.updateParticipant(thisParticipant);
		}

	}

	/**
	 * Init data If new hide btn_remove_contact else visible it If new edit
	 * texts editable else not editable Set title suitable for each case Fill
	 * data on edit texts if contact existed
	 * */
	public void initData() {
		if (progress == null) {
			// display progress dialog like this
			progress = new ProgressDialog(mContext);
			progress.setCancelable(false);
			progress.setMessage(mContext.getResources().getString(
					R.string.processing));
		}
		SharedReference ref = new SharedReference();
		DatabaseHelper dbHelper = DatabaseHelper
				.getSharedDatabaseHelper(mContext);
		if (composeType == DatabaseHelper.NEW) {
			view.titleBar.tv_name.setText(mContext.getResources().getString(
					R.string.add_contact));
			int newParticipantID = dbHelper.getNextParticipantID();
			int ownerid = ref.getCurrentOwnerId(mContext);
			thisParticipant = new Participant(newParticipantID, null, null,
					null, ownerid);
			view.btn_remove_contact.setVisibility(View.GONE);
			setEdittextEditable(view.et_email, true);
			setEdittextEditable(view.et_mobile, true);
			setEdittextEditable(view.et_name, true);
			view.titleBar.layout_next.setVisibility(View.GONE);
			view.titleBar.layout_save.setVisibility(View.VISIBLE);
			view.titleBar.layout_edit.setVisibility(View.GONE);
		} else if (composeType == DatabaseHelper.EXISTED) {
			view.titleBar.tv_name.setText(mContext.getResources().getString(
					R.string.edit_participant));
			thisParticipant = dbHelper.getParticipant(selectedParticipantID);
			view.btn_remove_contact.setVisibility(View.VISIBLE);
			setEdittextEditable(view.et_email, false);
			setEdittextEditable(view.et_mobile, false);
			setEdittextEditable(view.et_name, false);
			view.titleBar.layout_next.setVisibility(View.GONE);
			view.titleBar.layout_edit.setVisibility(View.VISIBLE);
			view.titleBar.layout_save.setVisibility(View.GONE);

			if (thisParticipant != null) {
				view.et_email.setText(thisParticipant.getEmail());
				view.et_mobile.setText(thisParticipant.getMobile());
				view.et_name.setText(thisParticipant.getName());
			}
		}

	}
}
