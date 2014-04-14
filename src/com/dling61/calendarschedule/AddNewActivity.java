package com.dling61.calendarschedule;

import com.dling61.calendarschedule.adapter.TextViewBaseAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.ActivityTable;
import com.dling61.calendarschedule.models.MyActivity;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.MyDate;
import com.dling61.calendarschedule.utils.SharedReference;
import com.dling61.calendarschedule.views.AddActivityView;
import com.dling61.calendarschedule.views.PopupView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
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
	String time_zone = "";
	int alert_type = 0;
	int repeat_type = 0;
	int type = -1;// type=repeat, alert, timezone
	static final int REPEAT = 0;
	static final int ALERT = 1;
	static final int TIMEZONE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.setContentView(R.layout.composeactivity);
		mContext = this;
		view = new AddActivityView(mContext);
		setContentView(view.layout);

		dbHelper = DatabaseHelper.getSharedDatabaseHelper(mContext);

		Intent myIntent = getIntent();
		composeType = myIntent.getIntExtra("type", 3);
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
			int id = myIntent.getIntExtra("activityid", -1);
			thisActivity = dbHelper.getActivity(id);
		}
		this.initViewValues();
		onClickListener();
	}

	/**
	 * Return popup time zone
	 * */
	public PopupWindow popUp(final String[] array, final int type) {
		// initialize a pop up window type
		final PopupWindow popupWindow = new PopupWindow(this);
		// some other visual settings
		popupWindow.setFocusable(true);
		popupWindow.setWidth(400);
		popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		TextViewBaseAdapter adapter = new TextViewBaseAdapter(mContext, array);

		PopupView popup = new PopupView(mContext);
		switch (type) {
		case TIMEZONE:
			popup.tv_title.setText(getResources().getString(R.string.time_zone));
			break;
		case ALERT:
			popup.tv_title.setText(getResources().getString(R.string.alert));
			break;
		case REPEAT:
			popup.tv_title.setText(getResources().getString(R.string.repeat));
			break;
		default:
			break;
		}
		popup.list_item.setAdapter(adapter);
		popup.list_item.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (type) {
				case TIMEZONE:
					SharedReference ref = new SharedReference();
					ref.setTimeZone(mContext, timezone_value_array[position]);
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
				popupWindow.dismiss();

			}
		});
		// set the list view as pop up window content
		popupWindow.setContentView(popup);

		return popupWindow;
	}

	/**
	 * On Click listener
	 * */
	public void onClickListener() {
		view.btn_new_activity_next.setOnClickListener(this);
		view.et_new_activity_time_zone.setOnClickListener(this);
		view.et_new_activity_alert.setOnClickListener(this);
		view.et_new_activity_repeat.setOnClickListener(this);
		// view.et_new_activity_repeat
		// .setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// PopupWindow popup_menu = popUp(repeat_array, REPEAT);
		// if (hasFocus) {
		// popup_menu.showAtLocation(
		// view.et_new_activity_time_zone,
		// Gravity.CENTER, 0, 0);
		// } else {
		// popup_menu.dismiss();
		// }
		// }
		// });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void initViewValues() {
		alert_array = getResources().getStringArray(R.array.alert_array);
		timezone_array = getResources().getStringArray(R.array.timezone_array);
		timezone_value_array = getResources().getStringArray(
				R.array.timezone_value_array);

		repeat_array = getResources().getStringArray(R.array.repeat_array);

		SharedReference ref = new SharedReference();
		time_zone = ref.getTimeZone(mContext);

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
		view.et_new_activity_alert.setText(this.getAlert(alert));

		// set time zone if used to select

		if (time_zone != null && (!time_zone.equals(""))) {
			view.et_new_activity_time_zone.setText(time_zone);
		}
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
			composeActivityDone();
		} else if (v == view.et_new_activity_time_zone) {
			SharedReference ref=new SharedReference();
			time_zone=ref.getTimeZone(mContext);
			if (time_zone == null || time_zone.equals("")) {
				PopupWindow popup_menu = popUp(timezone_array, TIMEZONE);

				popup_menu.showAtLocation(view.et_new_activity_time_zone,
						Gravity.CENTER, 0, 0);
			}
		} else if (v == view.et_new_activity_repeat) {
			PopupWindow popup_menu = popUp(repeat_array, REPEAT);

			popup_menu.showAtLocation(view.et_new_activity_repeat,
					Gravity.CENTER, 0, 0);
		} else if (v == view.et_new_activity_alert) {

			PopupWindow popup_menu = popUp(alert_array, ALERT);

			popup_menu.showAtLocation(view.et_new_activity_alert,
					Gravity.CENTER, 0, 0);
		}
	}

	public void composeActivityDone() {
		if (view.et_new_activity_name.getText().toString().equalsIgnoreCase("")) {
			Toast.makeText(this, "The activity name should not be empty",
					Toast.LENGTH_LONG).show();
			return;
		}

		thisActivity.setActivity_name(view.et_new_activity_name.getText()
				.toString());
		thisActivity.setDesp(view.et_new_activity_description.getText()
				.toString());
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
				// Toast.makeText(this, "insert activity successfully",
				// Toast.LENGTH_LONG).show();
			}

			WebservicesHelper ws = new WebservicesHelper(mContext);
			ws.addActivity(thisActivity);
			Intent newIntent = new Intent(this, ShareActivity.class);
			newIntent.putExtra("type", DatabaseHelper.NEW);
			newIntent.putExtra("serviceid", thisActivity.getActivity_ID());
			this.startActivityForResult(newIntent, 11);

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

			Intent resultIntent = new Intent();
			resultIntent.putExtra("id", thisActivity.getActivity_ID());
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
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
