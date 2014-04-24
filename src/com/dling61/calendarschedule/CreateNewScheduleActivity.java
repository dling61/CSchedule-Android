package com.dling61.calendarschedule;

import java.util.ArrayList;
import java.util.List;

import com.dling61.calendarschedule.adapter.ActivityNameAdapter;
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
import com.dling61.calendarschedule.views.PopupDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
//import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class CreateNewScheduleActivity extends Activity implements
		OnDateSetListener, OnTimeSetListener, OnMenuItemClickListener,
		OnClickListener {

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
	MyActivity myActivity;
	int startMonth, startYear, startDate, startHour, startMin;
	int endMonth, endYear, endDate, endHour, endMin;

	int REQUEST_CODE = 15;
//	ArrayList<Integer> listParticipantSeleted;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		view = new AddScheduleView(mContext);
		this.setContentView(view.layout);

		Intent myIntent = this.getIntent();
		composeType = myIntent.getIntExtra(CommConstant.TYPE, -1);
		activity_id = myIntent.getStringExtra(CommConstant.ACTIVITY_ID);

		dbHelper = DatabaseHelper.getSharedDatabaseHelper(this);

		if (activity_id != null && (!activity_id.equals(""))) {
			myActivity = dbHelper.getActivity(activity_id);
		} else {
			// TODO:make activityname is suggestion activity in system
		}
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
			view.btn_remove_schedule.setVisibility(View.GONE);
			view.layout_next.setVisibility(View.VISIBLE);
			view.layout_save.setVisibility(View.GONE);
		} else if (composeType == DatabaseHelper.EXISTED) {
			int id = myIntent.getIntExtra(CommConstant.SCHEDULE_ID, -1);
			thisSchedule = dbHelper.getScheduleSortedByID(id);
			view.btn_remove_schedule.setVisibility(View.VISIBLE);
			view.layout_next.setVisibility(View.GONE);
			view.layout_save.setVisibility(View.VISIBLE);
		}
		initViewValues();
		onClickListener();
		try {
			registerReceiver(deleteScheduleComplete, new IntentFilter(
					CommConstant.DELETE_SCHEDULE_COMPLETE));
			registerReceiver(onDutyComplete, new IntentFilter(
					CommConstant.ON_DUTY_ITEM_SELECTED));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Return popup activityname have shared role owner or organizer
	 * */
	public void popUp() {
		DatabaseHelper dbHelper = DatabaseHelper
				.getSharedDatabaseHelper(mContext);
		final ArrayList<MyActivity> listActivity = dbHelper
				.getActivitiesOwnerOrOrganizer(new SharedReference()
						.getCurrentOwnerId(mContext) + "");
		ActivityNameAdapter adapter = new ActivityNameAdapter(mContext,
				listActivity);
		final PopupDialog dialog = new PopupDialog(mContext, mContext
				.getResources().getString(R.string.activity));
		dialog.show();
		dialog.list_item.setAdapter(adapter);
		dialog.list_item.setVisibility(View.VISIBLE);
		dialog.list_item.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				myActivity = listActivity.get(position);
				activity_id = myActivity.getActivity_ID();
				view.et_new_activity_name.setText(myActivity.getActivity_name());
				dialog.dismiss();

			}
		});

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			unregisterReceiver(deleteScheduleComplete);
			unregisterReceiver(onDutyComplete);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void onClickListener() {
		view.et_endDate.setOnClickListener(this);
		view.et_startDate.setOnClickListener(this);
		view.et_endTime.setOnClickListener(this);
		view.et_startTime.setOnClickListener(this);
		view.et_on_duty.setOnClickListener(this);
		view.layout_next.setOnClickListener(this);
		view.btn_remove_schedule.setOnClickListener(this);
		view.layout_back.setOnClickListener(this);
		view.layout_save.setOnClickListener(this);
		view.et_new_activity_name.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == view.et_startDate) {
			// setStartDate();
			setStartDate();
		} else if (v == view.et_endDate) {
			setEndDate();
		} else if (v == view.et_endTime) {
			setEndTime();
		} else if (v == view.et_startTime) {
			setStartTime();
		} else if (v == view.et_on_duty) {
			Intent intent = new Intent(mContext, ParticipantActivity.class);
			intent.putExtra(CommConstant.ACTIVITY_ID, activity_id);
			intent.putExtra(CommConstant.TYPE, CommConstant.TYPE_PARTICIPANT);
			intent.putExtra(CommConstant.SCHEDULE_ID,
					thisSchedule != null ? thisSchedule.getSchedule_ID() : -1);
//			List<Integer> pins = new ArrayList<Integer>();
//			if (composeType == DatabaseHelper.EXISTED) {
//				pins = dbHelper.getParticipantsForSchedule(thisSchedule
//						.getSchedule_ID());
//			}
			intent.putIntegerArrayListExtra("pins", (ArrayList<Integer>) pins);
			startActivityForResult(intent, REQUEST_CODE);
		} else if (v == view.layout_next) {
			createNewSchedule();
		} else if (v == view.btn_remove_schedule) {
			deleteSchedule();
		} else if (v == view.layout_back) {
			((Activity) mContext).finish();
		} else if (v == view.layout_save) {
			editSchedule();
		} else if (v == view.et_new_activity_name) {
			if (composeType == DatabaseHelper.NEW) {
				popUp();
			}
		}
	}

	public void initViewValues() {
		if (composeType == DatabaseHelper.NEW) {
			view.title_tv.setText(mContext.getResources().getString(
					R.string.add_schedule));
		} else {
			view.title_tv.setText(mContext.getResources().getString(
					R.string.edit_schedule));
		}
		view.et_new_activity_name.setText(myActivity != null ? myActivity
				.getActivity_name() : "");
		 if (thisSchedule != null) {
		pins = dbHelper.getParticipantsForSchedule(thisSchedule
				.getSchedule_ID());
		String members = "";
		if (pins != null && pins.size() > 0) {
			for (int i = 0; i < pins.size(); i++) {
				Participant p = dbHelper.getParticipant(pins.get(i));
				if (p != null) {
					if (i == 0)
						members = members + p.getName();
					else
						members = members + "," + p.getName();
					p.isChecked=true;
				}
			}
		}
		
		view.et_on_duty.setText(members);
		 }

		String startdate = thisSchedule!=null?thisSchedule.getStarttime():"";
		String startfulldate = MyDate.getWeekdayFromUTCTime(startdate) + ", "
				+ MyDate.transformUTCTimeToCustomStyle(startdate);
		String starttime = MyDate.getTimeWithAPMFromUTCTime(startdate);
		view.et_startDate.setText(startfulldate);
		view.et_startTime.setText(starttime);

		String enddate = thisSchedule.getEndtime();
		String endfulldate = MyDate.getWeekdayFromUTCTime(enddate) + ", "
				+ MyDate.transformUTCTimeToCustomStyle(enddate);
		String endtime = MyDate.getTimeWithAPMFromUTCTime(enddate);
		view.et_endDate.setText(endfulldate);
		view.et_endTime.setText(endtime);

		view.et_new_activity_description.setText(thisSchedule!=null?thisSchedule.getDesp():"");
		// }
		// }
	}

	public void setStartDate() {
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

	public void setEndDate() {
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

	public void setStartTime() {
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

	public void setEndTime() {
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
		String hourMinute = "";
		if (StartOrEnd == START) {
			hourMinute = this.view.et_startTime.getText().toString().trim();

		} else {
			hourMinute = this.view.et_endTime.getText().toString().trim();
		}
		hourMinute = hourMinute.replace("AM", "").replace("PM", "");
		hourMinute = hourMinute.trim() + ":00";
		Log.d("hourMinute", hourMinute);
		String weekday = MyDate.getWeekdayFromUTCTime(MyDate
				.transformLocalDateTimeToUTCFormat(year + "-" + monthstr + "-"
						+ daystr + " "
						+ (hourMinute.equals("") ? "00:00:00" : hourMinute)));
		String customDate = MyDate.transformUTCTimeToCustomStyle(MyDate
				.transformLocalDateTimeToUTCFormat(year + "-" + monthstr + "-"
						+ daystr + " "
						+ (hourMinute.equals("") ? "00:00:00" : hourMinute)));
		String fulldate = weekday + ", " + customDate;
		if (StartOrEnd == START) {
			this.view.et_startDate.setText(fulldate);
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
				this.view.et_endDate.setText(this.view.et_startDate.getText());
				this.view.et_endTime.setText(this.view.et_startTime.getText());
			}
		} else {

			this.view.et_endDate.setText(fulldate);
			thisSchedule
					.setEndtime(MyDate.transformLocalDateTimeToUTCFormat(year
							+ "-"
							+ monthstr
							+ "-"
							+ daystr
							+ " "
							+ MyDate.transformUTCDateToLocalDate(
									MyDate.STANDARD,
									thisSchedule.getStarttime()).split(" ")[1]));
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
			this.view.et_startTime.setText(time);
			thisSchedule.setStarttime(MyDate
					.transformLocalDateTimeToUTCFormat(MyDate
							.transformUTCDateToLocalDate(MyDate.STANDARD,
									thisSchedule.getStarttime()).split(" ")[0]
							+ " " + hourstr + ":" + minutestr + ":" + "00"));
			if (MyDate.IsFirstDateLaterThanSecondDate(
					this.thisSchedule.getStarttime(),
					this.thisSchedule.getEndtime())) {
				this.thisSchedule.setEndtime(this.thisSchedule.getEndtime());
				this.view.et_endDate.setText(this.view.et_startDate.getText());
				this.view.et_endTime.setText(this.view.et_startTime.getText());

			} else {

			}

		} else {
			this.view.et_endTime.setText(time);
			thisSchedule.setEndtime(MyDate
					.transformLocalDateTimeToUTCFormat(MyDate
							.transformUTCDateToLocalDate(MyDate.STANDARD,
									thisSchedule.getEndtime()).split(" ")[0]
							+ " " + hourstr + ":" + minutestr + ":" + "00"));
			if (MyDate.IsFirstDateLaterThanSecondDate(
					this.thisSchedule.getStarttime(),
					this.thisSchedule.getEndtime())) {
				this.thisSchedule.setEndtime(this.thisSchedule.getEndtime());
				this.view.et_endDate.setText(this.view.et_startDate.getText());
				this.view.et_startTime.setText(this.view.et_endTime.getText());

			} else {

			}
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

	/**
	 * Check&set data, return true if valid else return false
	 * */
	private boolean checkAndSetData() {
		if (myActivity == null) {
			Toast.makeText(this, "Please select an activity to schedule",
					Toast.LENGTH_LONG).show();
			return false;
		}
		thisSchedule.setDesp(view.et_new_activity_description.getText()
				.toString());
		thisSchedule.setService_ID(myActivity.getActivity_ID());
		return true;
	}

	/**
	 * Create new activity
	 * */
	private void createNewSchedule() {
		if (checkAndSetData()) {
			if (composeType == DatabaseHelper.NEW) {
				ContentValues cv = new ContentValues();
				cv.put(ScheduleTable.own_ID, thisSchedule.getOwner_ID());
				cv.put(ScheduleTable.schedule_ID, thisSchedule.getSchedule_ID());
				cv.put(ScheduleTable.last_Modified, "upload");
				cv.put(ScheduleTable.start_Time, thisSchedule.getStarttime());
				cv.put(ScheduleTable.schedule_Description,
						thisSchedule.getDesp());
				cv.put(ScheduleTable.end_Time, thisSchedule.getEndtime());
				cv.put(ScheduleTable.service_ID, thisSchedule.getService_ID());
				cv.put(ScheduleTable.is_Deleted, 0);
				cv.put(ScheduleTable.is_Synchronized, 0);
				cv.put(ScheduleTable.user_login,
						new SharedReference().getCurrentOwnerId(mContext));

				dbHelper.insertSchedule(cv);
				for (int i = 0; i < pins.size(); i++) {
					ContentValues onduty = new ContentValues();
					onduty.put(OndutyTable.schedule_ID,
							thisSchedule.getSchedule_ID());
					onduty.put(OndutyTable.service_ID,
							thisSchedule.getService_ID());
					onduty.put(OndutyTable.participant_ID, pins.get(i));
					onduty.put(OndutyTable.last_Modified, "");
					onduty.put(OndutyTable.is_Deleted, 0);
					onduty.put(OndutyTable.is_Synchronized, 0);
					dbHelper.insertOnduty(onduty);
				}
				WebservicesHelper ws = new WebservicesHelper(mContext);
				ws.addSchedule(thisSchedule, pins);

				// Intent resultIntent = new Intent();
				// setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		}

	}

	/**
	 * Edit activity
	 * */
	private void editSchedule() {
		if (composeType == DatabaseHelper.EXISTED) {
			ContentValues cv = new ContentValues();
			cv.put(ScheduleTable.start_Time, thisSchedule.getStarttime());
			cv.put(ScheduleTable.schedule_Description, thisSchedule.getDesp());
			cv.put(ScheduleTable.end_Time, thisSchedule.getEndtime());
			cv.put(ScheduleTable.service_ID, thisSchedule.getService_ID());
			cv.put(ScheduleTable.is_Deleted, 0);
			cv.put(ScheduleTable.is_Synchronized, 0);
			dbHelper.updateSchedule(thisSchedule.getSchedule_ID(), cv);
			dbHelper.deleteRelatedOnduty(thisSchedule.getSchedule_ID());
			if (pins != null) {
				for (int i = 0; i < pins.size(); i++) {
					ContentValues onduty = new ContentValues();
					onduty.put(OndutyTable.schedule_ID,
							thisSchedule.getSchedule_ID());
					onduty.put(OndutyTable.service_ID,
							thisSchedule.getSchedule_ID());
					onduty.put(OndutyTable.participant_ID,
							pins.get(i));
					onduty.put(OndutyTable.last_Modified, "");
					onduty.put(OndutyTable.is_Deleted, 0);
					onduty.put(OndutyTable.is_Synchronized, 0);
					dbHelper.insertOnduty(onduty);
				}
			}

			WebservicesHelper ws = new WebservicesHelper(mContext);
			ws.updateSchedule(thisSchedule, pins);
			// Intent resultIntent = new Intent();
			// setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}
	}

	/**
	 * delete schedule update to database
	 * */
	public void deleteSchedule() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(mContext.getResources()
				.getString(R.string.caution));
		alertDialog.setMessage(mContext.getResources().getString(
				R.string.delete_schedule));
		alertDialog.setPositiveButton(
				mContext.getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ContentValues scv = new ContentValues();
						scv.put(ScheduleTable.is_Deleted, 1);
						scv.put(ScheduleTable.is_Synchronized, 0);

						int schedule_id = thisSchedule.getSchedule_ID();
						dbHelper.updateSchedule(schedule_id, scv);
						List<Integer> onduties = dbHelper
								.getOndutyRecordsForSchedule(schedule_id);
						for (int j = 0; j < onduties.size(); j++) {
							ContentValues ocv = new ContentValues();
							ocv.put(OndutyTable.is_Deleted, 1);
							ocv.put(OndutyTable.is_Synchronized, 0);
							int onduty_id = onduties.get(j);
							dbHelper.updateSchedule(onduty_id, ocv);
						}

						WebservicesHelper ws = new WebservicesHelper(mContext);
						ws.deleteSchedule(thisSchedule);
					}
				});
		alertDialog.setNegativeButton(
				mContext.getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getApplicationContext(),
								"You clicked on NO", Toast.LENGTH_SHORT).show();
						dialog.cancel();
					}
				});
		alertDialog.show();

	}

	BroadcastReceiver deleteScheduleComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			finish();
		}
	};

	BroadcastReceiver onDutyComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			pins = arg1
					.getIntegerArrayListExtra(CommConstant.ON_DUTY_ITEM_SELECTED);
			String members = "";
			if (pins != null
					&& pins.size() > 0) {
				for (int i = 0; i < pins.size(); i++) {
					Participant p = dbHelper
							.getParticipant(pins.get(i));
					if (p != null) {
						if (i == 0)
							members = members + p.getName();
						else
							members = members + "," + p.getName();
					}
				}
			}
			view.et_on_duty.setText(members);

		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 333) {
			String activity_id = data.getStringExtra(CommConstant.ACTIVITY_ID);
			if (activity_id.equalsIgnoreCase(this.activity_id)) {
				pins = data
						.getIntegerArrayListExtra(CommConstant.ON_DUTY_ITEM_SELECTED);
				String members = "";
				if (pins != null
						&& pins.size() > 0) {
					for (int i = 0; i < pins.size(); i++) {
						Participant p = dbHelper
								.getParticipant(pins.get(i));
						if (p != null) {
							if (i == 0)
								members = members + p.getName();
							else
								members = members + "," + p.getName();
						}
					}
				}
				view.et_on_duty.setText(members);
			}
		}
		// switch (requestCode) {
		// case (11): {
		// Intent resultIntent = new Intent();
		// resultIntent.putExtra("id", thisActivity.getActivity_ID());
		// setResult(Activity.RESULT_OK, resultIntent);
		// finish();
		// break;
		// }
		//
		// }
	}

}
