package com.dling61.calendarschedule;

import java.util.ArrayList;

import com.dling61.calendarschedule.adapter.ParticipantAdapter;
import com.dling61.calendarschedule.adapter.TextViewBaseAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.ActivityTable;
import com.dling61.calendarschedule.models.MyActivity;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.MyDate;
import com.dling61.calendarschedule.utils.SharedReference;
import com.dling61.calendarschedule.views.AddActivityView;
import com.dling61.calendarschedule.views.PopupDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

@SuppressLint("NewApi")
public class AddNewActivity extends Activity implements OnClickListener {
	private MyActivity thisActivity;
	private int composeType;
	private DatabaseHelper dbHelper;
	Context mContext;
	AddActivityView view;
	String[] alert_array = null;
	String[] timezone_array = null;
	String[] timezone_value_array = null;
	String[] repeat_array = null;
	int time_zone = 0;// timezone position
	int alert_type = 0;
	int repeat_type = 0;
	int type = -1;// type=repeat, alert, timezone
	static final int REPEAT = 0;
	static final int ALERT = 1;
	static final int TIMEZONE = 2;
	String activity_id = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.setContentView(R.layout.composeactivity);
		mContext = this;
		view = new AddActivityView(mContext);
		setContentView(view.layout);

		dbHelper = DatabaseHelper.getSharedDatabaseHelper(mContext);

		thisActivity = new MyActivity(dbHelper.getNextActivityID(),
				new SharedReference().getCurrentOwnerId(mContext), 0, 0, "",
				MyDate.transformLocalDateTimeToUTCFormat(MyDate
						.getCurrentDateTime()),
				MyDate.transformLocalDateTimeToUTCFormat(MyDate
						.getCurrentDateTime()), "", 0, 0);

		Intent myIntent = getIntent();
		composeType = myIntent.getIntExtra(CommConstant.TYPE, 3);
		if (composeType == DatabaseHelper.NEW) {
			Log.i("next service id", "is " + dbHelper.getNextActivityID());
			// MyActivity(int activity_ID, int owner_ID, int alert, int repeat,
			// String activity_name,
			// String starttime, String endtime, String desp, int otc_offset,
			// int role);
			thisActivity = new MyActivity(dbHelper.getNextActivityID(),
					new SharedReference().getCurrentOwnerId(mContext), 0, 0,
					"", MyDate.transformLocalDateTimeToUTCFormat(MyDate
							.getCurrentDateTime()),
					MyDate.transformLocalDateTimeToUTCFormat(MyDate
							.getCurrentDateTime()), "", 0, 0);
		} else if (composeType == DatabaseHelper.EXISTED) {
			activity_id = myIntent.getStringExtra(CommConstant.ACTIVITY_ID);

			thisActivity = dbHelper.getActivity(activity_id);
			// set visible when edit
			view.btn_add_paticipant.setVisibility(View.VISIBLE);
			view.btn_remove_activity.setVisibility(View.VISIBLE);
			WebservicesHelper ws = new WebservicesHelper(mContext);
			ws.getSharedmembersForActivity(activity_id);
		}
		this.initViewValues();
		onClickListener();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (activity_id != null && (!activity_id.equals(""))) {
			DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			Log.i("broadcast", "activities are ready");
			ArrayList<Participant> list_participant = dbHelper
					.getParticipantsOfActivity(activity_id);
			dbHelper.close();
			ParticipantAdapter adapter = new ParticipantAdapter(mContext,
					list_participant, true);
			view.list_participant.setAdapter(adapter);
		}
	}

	@Override
	protected void onPause() {
		// mContext.unregisterReceiver(activityGetSharedMemberComplete);
		super.onPause();
	};

	BroadcastReceiver activityGetSharedMemberComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			Log.i("broadcast", "activities are ready");
			ArrayList<Participant> list_participant = dbHelper
					.getParticipantsOfActivity(activity_id);
			dbHelper.close();
			ParticipantAdapter adapter = new ParticipantAdapter(mContext,
					list_participant, true);
			view.list_participant.setAdapter(adapter);

		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * Return popup time zone
	 * */
	public void popUp(final String[] array, final int type) {
		TextViewBaseAdapter adapter = new TextViewBaseAdapter(mContext, array);

		String title = "";
		switch (type) {
		case TIMEZONE:
			title = getResources().getString(R.string.time_zone);
			break;
		case ALERT:
			title = getResources().getString(R.string.alert);
			break;
		case REPEAT:
			title = getResources().getString(R.string.repeat);
			break;
		default:
			break;
		}

		final PopupDialog dialog = new PopupDialog(mContext, title);
		dialog.show();
		dialog.list_item.setAdapter(adapter);
		dialog.list_item.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (type) {
				case TIMEZONE:
					SharedReference ref = new SharedReference();
					ref.setTimeZone(mContext, position);
					time_zone = position;
					view.et_new_activity_time_zone.setText(array[position]);
					break;
				case ALERT:
					alert_type = getAlertIndex(array[position]);
					view.et_new_activity_alert.setText(array[position]);
					break;
				case REPEAT:
					repeat_type = getAlertIndex(array[position]);
					view.et_new_activity_repeat.setText(array[position]);
					break;
				default:
					break;
				}
				dialog.dismiss();

			}
		});

	}

	/**
	 * On Click listener
	 * */
	public void onClickListener() {
		view.btn_new_activity_next.setOnClickListener(this);
		view.et_new_activity_time_zone.setOnClickListener(this);
		view.et_new_activity_alert.setOnClickListener(this);
		view.et_new_activity_repeat.setOnClickListener(this);
		view.btn_add_paticipant.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Init values
	 * */
	public void initViewValues() {
		alert_array = getResources().getStringArray(R.array.alert_array);
		timezone_array = getResources().getStringArray(R.array.timezone_array);
		timezone_value_array = getResources().getStringArray(
				R.array.timezone_value_array);

		repeat_array = getResources().getStringArray(R.array.repeat_array);

		// timezone saved is position in array timezone
		SharedReference ref = new SharedReference();
		time_zone = ref.getTimeZone(mContext);
		// set time zone if used to select
		if (time_zone <= 0) {
			view.et_new_activity_time_zone.setText(timezone_array[0]);
		} else {
			view.et_new_activity_time_zone.setText(timezone_array[time_zone]);
		}

		if (composeType == DatabaseHelper.NEW)
			view.title_tv.setText(mContext.getResources().getString(
					R.string.add_activity));
		else
			view.title_tv.setText(mContext.getResources().getString(
					R.string.edit_activity));

		String activity_name = thisActivity != null ? thisActivity
				.getActivity_name() : "";
		view.et_new_activity_name.setText(activity_name);

		String desp = thisActivity != null ? thisActivity.getDesp() : "";
		view.et_new_activity_description.setText(desp);

		int alert = thisActivity != null ? thisActivity.getAlert() : 0;
		view.et_new_activity_alert.setText(getAlert(alert));

		int repeat = thisActivity != null ? thisActivity.getRepeat() : 0;
		view.et_new_activity_repeat.setText(getAlert(repeat));

	}

	/**
	 * Return string alert
	 * */
	public String getAlert(int alt) {
		try {
			return alert_array[alt];
		} catch (ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();

		}
		return "None";
	}

	/**
	 * Get alert index
	 * */
	public int getAlertIndex(String alt) {
		int size = alert_array.length;
		for (int i = 0; i < size; i++) {
			if (alert_array[i].equalsIgnoreCase(alt)) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == view.btn_new_activity_next) {
			createNewActivity();
		} else if (v == view.et_new_activity_time_zone) {
			SharedReference ref = new SharedReference();
			time_zone = ref.getTimeZone(mContext);
			if (time_zone <= 0) {
				popUp(timezone_array, TIMEZONE);
			}
		} else if (v == view.et_new_activity_repeat) {
			popUp(repeat_array, REPEAT);
		} else if (v == view.et_new_activity_alert) {
			popUp(alert_array, ALERT);
		} else if (v == view.btn_add_paticipant) {
			Intent intent = new Intent(mContext, ParticipantActivity.class);
			intent.putExtra(CommConstant.TYPE,
					CommConstant.ADD_PARTICIPANT_FOR_ACTIVITY);
			mContext.startActivity(intent);
		}
	}

	/**
	 * Create new activity
	 * */
	public void createNewActivity() {
		if (view.et_new_activity_name.getText().toString().equalsIgnoreCase("")) {
			Toast.makeText(this, "The activity name should not be empty",
					Toast.LENGTH_LONG).show();
			return;
		}

		String activity_name = view.et_new_activity_name.getText().toString()
				.trim();
		String activity_description = view.et_new_activity_description
				.getText().toString().trim();

		if (activity_name == null || activity_name.equals("")) {
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.input_activity_name)
							+ "\n", Toast.LENGTH_LONG).show();
			return;
		}
		if (activity_description == null || activity_description.equals("")) {
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.input_activity_description)
							+ "\n", Toast.LENGTH_LONG).show();
			return;
		}

		thisActivity.setActivity_name(activity_name);
		thisActivity.setDesp(activity_description);
		thisActivity.setAlert(alert_type);
		thisActivity.setRepeat(repeat_type);
		thisActivity.setOtc_offset((int) (Float
				.parseFloat(timezone_value_array[time_zone]) * 3600));

		// WebservicesHelper ws = new WebservicesHelper(mContext);
		// ws.addActivity(thisActivity);

		if (composeType == DatabaseHelper.NEW) {
			ContentValues newActivity = new ContentValues();
			newActivity.put(ActivityTable.service_ID,
					thisActivity.getActivity_ID());
			newActivity.put(ActivityTable.own_ID, thisActivity.getOwner_ID());
			newActivity.put(ActivityTable.service_Name,
					thisActivity.getActivity_name());
			newActivity.put(ActivityTable.alert, thisActivity.getAlert());
			newActivity.put(ActivityTable.repeat, thisActivity.getRepeat());
			newActivity.put(ActivityTable.sharedrole, thisActivity.getRole());
			newActivity.put(ActivityTable.start_time,
					thisActivity.getStarttime());
			newActivity.put(ActivityTable.end_time, thisActivity.getEndtime());
			newActivity.put(ActivityTable.service_description,
					thisActivity.getDesp());
			newActivity.put(ActivityTable.otc_Offset,
					thisActivity.getOtc_offset());
			newActivity.put(ActivityTable.is_Deleted, 0);
			newActivity.put(ActivityTable.is_Synchronized, 0);
			newActivity.put(ActivityTable.last_ModifiedTime, "nouploaded");
			if (dbHelper.insertActivity(newActivity)) {

			}

			WebservicesHelper ws = new WebservicesHelper(mContext);
			ws.addActivity(thisActivity);
			// Intent newIntent = new Intent(this, ShareActivity.class);
			// newIntent.putExtra("type", DatabaseHelper.NEW);
			// newIntent.putExtra("serviceid", thisActivity.getActivity_ID());
			// this.startActivityForResult(newIntent, 11);

		} else {
			ContentValues cv = new ContentValues();
			cv.put(ActivityTable.service_Name, thisActivity.getActivity_name());
			cv.put(ActivityTable.alert, thisActivity.getAlert());
			cv.put(ActivityTable.repeat, thisActivity.getRepeat());
			cv.put(ActivityTable.start_time, thisActivity.getStarttime());
			cv.put(ActivityTable.end_time, thisActivity.getEndtime());
			cv.put(ActivityTable.service_description, thisActivity.getDesp());
			cv.put(ActivityTable.otc_Offset, thisActivity.getOtc_offset());
			cv.put(ActivityTable.sharedrole, thisActivity.getRole());
			cv.put(ActivityTable.is_Deleted, 0);
			cv.put(ActivityTable.is_Synchronized, 0);
			dbHelper.updateActivity(thisActivity.getActivity_ID(), cv);

			WebservicesHelper ws = new WebservicesHelper(mContext);
			ws.updateActivity(thisActivity);

			// Intent resultIntent = new Intent();
			// resultIntent.putExtra("id", thisActivity.getActivity_ID());
			// setResult(Activity.RESULT_OK, resultIntent);
			// finish();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (11): {
			Intent resultIntent = new Intent();
			resultIntent.putExtra("id", thisActivity.getActivity_ID());
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
			break;
		}
		}
	}

}
