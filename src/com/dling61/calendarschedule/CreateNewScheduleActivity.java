package com.dling61.calendarschedule;

import java.util.ArrayList;
import java.util.List;

import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.MyActivity;
import com.dling61.calendarschedule.models.OndutyTable;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.models.Schedule;
import com.dling61.calendarschedule.models.ScheduleTable;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.MyDate;
import com.dling61.calendarschedule.utils.SharedReference;
import com.dling61.calendarschedule.views.AddScheduleView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.DatePicker;
//import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.TimePicker;

@SuppressLint("NewApi")
public class CreateNewScheduleActivity extends Activity implements
		OnDateSetListener, OnTimeSetListener, OnMenuItemClickListener {

	static private final int START = 0;
	static private final int END = 1;

	private Schedule thisSchedule;
	private int composeType;
	private int StartOrEnd;
	private DatabaseHelper dbHelper;
	private List<Integer> pins;

	AddScheduleView view;
	String activity_id = "";
	Context mContext;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		view = new AddScheduleView(mContext);
		this.setContentView(view.layout);

		Intent myIntent = this.getIntent();
		composeType = myIntent.getIntExtra(CommConstant.TYPE, -1);
		activity_id = myIntent.getStringExtra(CommConstant.ACTIVITY_ID);
		dbHelper = DatabaseHelper.getSharedDatabaseHelper(this);
		if (composeType == DatabaseHelper.NEW) {
			Log.i("next service id", "is " + dbHelper.getNextActivityID());
			thisSchedule = new Schedule(
					new SharedReference().getCurrentOwnerId(mContext),
					dbHelper.getNextScheduleID(), activity_id == null ? "0"
							: activity_id,
					MyDate.transformLocalDateTimeToUTCFormat(MyDate
							.getCurrentDateTime()),
					MyDate.transformLocalDateTimeToUTCFormat(MyDate
							.getCurrentDateTime()), "");
		} else {
			int id = myIntent.getIntExtra("scheduleid", -1);
			thisSchedule = dbHelper.getScheduleSortedByID(id);
		}
		initViewValues();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	// switch(requestCode)
	// {
	// case (6) :
	// {
	// if (resultCode == Activity.RESULT_OK)
	// {
	// pins = data.getIntegerArrayListExtra("pins");
	// String members = "";
	// for (int i=0;i < pins.size();i++)
	// {
	// Sharedmember sm = dbHelper.getSharedmember(pins.get(i),
	// thisSchedule.getService_ID());
	// if (i == 0)
	// members = members + sm.getName();
	// else
	// members = members + "," + sm.getName();
	// }
	// this.onduty_btn.setText(members);
	// }
	// }
	// break;
	// }
	// }

	public void initViewValues() {
		if (composeType == DatabaseHelper.NEW) {
			view.title_tv.setText(mContext.getResources().getString(
					R.string.add_schedule));
		} else {
			String service_id = thisSchedule.getService_ID();
			MyActivity activity = dbHelper.getActivity(service_id);
			view.et_new_activity_name.setText(activity.getActivity_name());
			view.title_tv.setText(mContext.getResources().getString(
					R.string.edit_schedule));
			pins = dbHelper.getParticipantsForSchedule(thisSchedule
					.getSchedule_ID());
			String members = "";
			for (int i = 0; i < pins.size(); i++) {
				Participant p = dbHelper.getParticipant(pins.get(i));
				if (i == 0)
					members = members + p.getName();
				else
					members = members + "," + p.getName();
			}
			view.et_on_duty.setText(members);

		}
		String startdate = thisSchedule.getStarttime();
		String startfulldate = MyDate.getWeekdayFromUTCTime(startdate) + ", "
				+ MyDate.transformUTCTimeToCustomStyle(startdate);
		String starttime = MyDate.getTimeWithAPMFromUTCTime(startdate);
		view.et_start.setText(startfulldate);
		// start_time_btn.setText(starttime);

		String enddate = thisSchedule.getEndtime();
		String endfulldate = MyDate.getWeekdayFromUTCTime(enddate) + ", "
				+ MyDate.transformUTCTimeToCustomStyle(enddate);
		String endtime = MyDate.getTimeWithAPMFromUTCTime(enddate);
		view.et_end.setText(endfulldate);
		// end_time_btn.setText(endtime);

		view.et_new_activity_description.setText(thisSchedule.getDesp());
	}

	public void setStartDate(View v) {
		String[] startdatetime = MyDate.transformUTCDateToLocalDate(
				MyDate.STANDARD, thisSchedule.getStarttime()).split(" ");
		String[] datecomponents = startdatetime[0].split("-");
		int year = Integer.valueOf(datecomponents[0]);
		int month = Integer.valueOf(datecomponents[1]) - 1;
		int day = Integer.valueOf(datecomponents[2]);
		DatePickerDialog dialog = new DatePickerDialog(this, this, year, month,
				day);
		dialog.setTitle("Set Start Date");
		// dialog.getDatePicker().setTag("startdate");
		dialog.show();
		StartOrEnd = START;
	}

	public void setEndDate(View v) {
		String[] enddatetime = MyDate.transformUTCDateToLocalDate(
				MyDate.STANDARD, thisSchedule.getEndtime()).split(" ");
		String[] datecomponents = enddatetime[0].split("-");
		int year = Integer.valueOf(datecomponents[0]);
		int month = Integer.valueOf(datecomponents[1]) - 1;
		int day = Integer.valueOf(datecomponents[2]);
		DatePickerDialog dialog = new DatePickerDialog(this, this, year, month,
				day);
		dialog.setTitle("Set End Date");
		// dialog.getDatePicker().setTag("enddate");
		dialog.show();
		StartOrEnd = END;
	}

	public void setStartTime(View v) {
		String[] startdatetime = MyDate.transformUTCDateToLocalDate(
				MyDate.STANDARD, thisSchedule.getStarttime()).split(" ");
		String[] timecomponents = startdatetime[1].split(":");
		int hour = Integer.valueOf(timecomponents[0]);
		int minute = Integer.valueOf(timecomponents[1]);
		TimePickerDialog dialog = new TimePickerDialog(this, this, hour,
				minute, true);
		dialog.setTitle("Set Start Time");
		dialog.show();
		StartOrEnd = START;
	}

	public void setEndTime(View v) {
		String[] enddatetime = MyDate.transformUTCDateToLocalDate(
				MyDate.STANDARD, thisSchedule.getEndtime()).split(" ");
		String[] timecomponents = enddatetime[1].split(":");
		int hour = Integer.valueOf(timecomponents[0]);
		int minute = Integer.valueOf(timecomponents[1]);
		TimePickerDialog dialog = new TimePickerDialog(this, this, hour,
				minute, true);
		dialog.setTitle("Set End Time");
		dialog.show();
		StartOrEnd = END;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		int month = monthOfYear + 1;
		String monthstr = null;
		String daystr = null;
		if (month < 10)
			monthstr = "0" + month;
		else
			monthstr = String.valueOf(month);

		if (dayOfMonth < 10)
			daystr = "0" + dayOfMonth;
		else
			daystr = String.valueOf(dayOfMonth);
		String weekday = MyDate.getWeekdayFromUTCTime(MyDate
				.transformLocalDateTimeToUTCFormat(year + "-" + monthstr + "-"
						+ daystr + " " + "00:00:00"));
		String customDate = MyDate.transformUTCTimeToCustomStyle(MyDate
				.transformLocalDateTimeToUTCFormat(year + "-" + monthstr + "-"
						+ daystr + " " + "00:00:00"));
		String fulldate = weekday + ", " + customDate;
		if (StartOrEnd == START) {
			this.view.et_start.setText(fulldate);
			thisSchedule
					.setStarttime(MyDate.transformLocalDateTimeToUTCFormat(year
							+ "-"
							+ monthstr
							+ "-"
							+ daystr
							+ " "
							+ MyDate.transformUTCDateToLocalDate(
									MyDate.STANDARD,
									thisSchedule.getStarttime()).split(" ")[1]));
			if (MyDate.IsFirstDateLaterThanSecondDate(
					this.thisSchedule.getStarttime(),
					this.thisSchedule.getEndtime())) {
				this.thisSchedule.setEndtime(this.thisSchedule.getStarttime());
				this.view.et_end.setText(this.view.et_start.getText());
				// end_time_btn.setText(start_time_btn.getText());
			}
		} else {
			this.view.et_end.setText(fulldate);
			thisSchedule.setEndtime(MyDate
					.transformLocalDateTimeToUTCFormat(year
							+ "-"
							+ monthstr
							+ "-"
							+ daystr
							+ " "
							+ MyDate.transformUTCDateToLocalDate(
									MyDate.STANDARD, thisSchedule.getEndtime())
									.split(" ")[1]));
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onTimeSet(TimePicker arg0, int hour, int minute) {
		// TODO Auto-generated method stub
		String hourstr = String.valueOf(hour);
		if (hour < 10)
			hourstr = "0" + hourstr;
		String minutestr = String.valueOf(minute);
		if (minute < 10)
			minutestr = "0" + minutestr;
		String time = MyDate.getTimeWithAPMFromUTCTime(MyDate
				.transformLocalDateTimeToUTCFormat("0000-00-00 " + hourstr
						+ ":" + minutestr + ":" + "00"));
		if (StartOrEnd == START) {
			this.view.et_start.setText(time);
			thisSchedule.setStarttime(MyDate
					.transformLocalDateTimeToUTCFormat(MyDate
							.transformUTCDateToLocalDate(MyDate.STANDARD,
									thisSchedule.getStarttime()).split(" ")[0]
							+ " " + hourstr + ":" + minutestr + ":" + "00"));
			if (MyDate.IsFirstDateLaterThanSecondDate(
					this.thisSchedule.getStarttime(),
					this.thisSchedule.getEndtime())) {
				this.thisSchedule.setEndtime(this.thisSchedule.getStarttime());
				this.view.et_end.setText(this.view.et_start.getText());
				// end_time_btn.setText(start_time_btn.getText());
			}
		} else {
			this.view.et_end.setText(time);
			thisSchedule.setEndtime(MyDate
					.transformLocalDateTimeToUTCFormat(MyDate
							.transformUTCDateToLocalDate(MyDate.STANDARD,
									thisSchedule.getEndtime()).split(" ")[0]
							+ " " + hourstr + ":" + minutestr + ":" + "00"));
		}
	}

	@SuppressLint("NewApi")
	public void setActivity(View v) {
		// PopupMenu popup = new PopupMenu(getBaseContext(), v);
		// // popup.getMenuInflater().inflate(R.menu.alertmenu,
		// popup.getMenu());
		// List<MyActivity> activities = dbHelper.getActivities();
		// for (int i = 0; i < activities.size(); i++)
		// {
		// MyActivity a = activities.get(i);
		// popup.getMenu().add(0, a.getActivity_ID(), 0, a.getActivity_name());
		// }
		// popup.setOnMenuItemClickListener(mContext);
		// popup.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		thisSchedule.setService_ID(id + "");
		String name = item.getTitle().toString();
		view.et_new_activity_name.setText(name);
		return false;
	}

	public void setOnduty(View v) {
		Intent newIntent = new Intent(this, OndutyActivity.class);
		List<Integer> pins = new ArrayList<Integer>();
		if (composeType == DatabaseHelper.EXISTED) {
			pins = dbHelper.getParticipantsForSchedule(thisSchedule
					.getSchedule_ID());
		}
		newIntent.putIntegerArrayListExtra("pins", (ArrayList<Integer>) pins);
		newIntent.putExtra("activityid", thisSchedule.getService_ID());
		this.startActivityForResult(newIntent, 6);
	}

	public void composeScheduleDone(View v) {
		if (this.thisSchedule.getService_ID().equals("0")) {
			Toast.makeText(this,
					"You should choose an activity this schedule belongs to",
					Toast.LENGTH_LONG).show();
			return;
		}
		thisSchedule.setDesp(view.et_new_activity_description.getText().toString());
		if (composeType == DatabaseHelper.NEW) {
			ContentValues cv = new ContentValues();
			cv.put(ScheduleTable.own_ID, thisSchedule.getOwner_ID());
			cv.put(ScheduleTable.schedule_ID, thisSchedule.getSchedule_ID());
			cv.put(ScheduleTable.last_Modified, "upload");
			cv.put(ScheduleTable.start_Time, thisSchedule.getStarttime());
			cv.put(ScheduleTable.schedule_Description, thisSchedule.getDesp());
			cv.put(ScheduleTable.end_Time, thisSchedule.getEndtime());
			cv.put(ScheduleTable.service_ID, thisSchedule.getService_ID());
			cv.put(ScheduleTable.is_Deleted, 0);
			cv.put(ScheduleTable.is_Synchronized, 0);
			dbHelper.insertSchedule(cv);
			for (int i = 0; i < pins.size(); i++) {
				ContentValues onduty = new ContentValues();
				onduty.put(OndutyTable.schedule_ID,
						thisSchedule.getSchedule_ID());
				onduty.put(OndutyTable.service_ID, thisSchedule.getService_ID());
				onduty.put(OndutyTable.participant_ID, pins.get(i));
				onduty.put(OndutyTable.last_Modified, "");
				onduty.put(OndutyTable.is_Deleted, 0);
				onduty.put(OndutyTable.is_Synchronized, 0);
				dbHelper.insertOnduty(onduty);
			}
			WebservicesHelper ws = new WebservicesHelper(mContext);
			ws.addSchedule(thisSchedule, pins);

			Intent resultIntent = new Intent();
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		} else {
			ContentValues cv = new ContentValues();
			cv.put(ScheduleTable.start_Time, thisSchedule.getStarttime());
			cv.put(ScheduleTable.schedule_Description, thisSchedule.getDesp());
			cv.put(ScheduleTable.end_Time, thisSchedule.getEndtime());
			cv.put(ScheduleTable.service_ID, thisSchedule.getService_ID());
			cv.put(ScheduleTable.is_Deleted, 0);
			cv.put(ScheduleTable.is_Synchronized, 0);
			dbHelper.updateSchedule(thisSchedule.getSchedule_ID(), cv);
			dbHelper.deleteRelatedOnduty(thisSchedule.getSchedule_ID());
			for (int i = 0; i < pins.size(); i++) {
				ContentValues onduty = new ContentValues();
				onduty.put(OndutyTable.schedule_ID,
						thisSchedule.getSchedule_ID());
				onduty.put(OndutyTable.service_ID,
						thisSchedule.getSchedule_ID());
				onduty.put(OndutyTable.participant_ID, pins.get(i));
				onduty.put(OndutyTable.last_Modified, "");
				onduty.put(OndutyTable.is_Deleted, 0);
				onduty.put(OndutyTable.is_Synchronized, 0);
				dbHelper.insertOnduty(onduty);
			}

			WebservicesHelper ws = new WebservicesHelper(mContext);
			ws.updateSchedule(thisSchedule, pins);
			Intent resultIntent = new Intent();
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}

	}
}
