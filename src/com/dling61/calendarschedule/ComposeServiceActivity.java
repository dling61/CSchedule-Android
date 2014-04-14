package com.dling61.calendarschedule;

import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.ActivityTable;
import com.dling61.calendarschedule.models.MyActivity;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.MyDate;
import com.dling61.calendarschedule.utils.SharedReference;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ComposeServiceActivity extends Activity
		implements
			OnDateSetListener,
			OnTimeSetListener,
			OnMenuItemClickListener {

	static private final int START = 0;
	static private final int END = 1;

	private MyActivity thisActivity;
	private int composeType;
	private DatabaseHelper dbHelper;
	private TextView title_tv;
	private EditText desp_et;
	private EditText name_et;
	private Button start_date_btn;
	private Button end_date_btn;
	private Button start_time_btn;
	private Button end_time_btn;
	private Button alt_btn;
	private int StartOrEnd;

	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.composeactivity);
		mContext = this;
		dbHelper = DatabaseHelper.getSharedDatabaseHelper(this);
		Intent myIntent = this.getIntent();
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
		this.findViews();
		this.initViewValues();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void findViews() {
		title_tv = (TextView) this
				.findViewById(R.id.compose_activity_toptitle_tv);
		desp_et = (EditText) this
				.findViewById(R.id.compose_activity_description_et);
		name_et = (EditText) this.findViewById(R.id.compose_activity_name_et);
		start_date_btn = (Button) this
				.findViewById(R.id.compose_activity_startdate_btn);
		end_date_btn = (Button) this
				.findViewById(R.id.compose_activity_enddate_btn);
		start_time_btn = (Button) this
				.findViewById(R.id.compose_activity_starttime_btn);
		end_time_btn = (Button) this
				.findViewById(R.id.compose_activity_endtime_btn);
		alt_btn = (Button) this
				.findViewById(R.id.compose_activity_showalert_btn);
	}

	public void initViewValues() {
		if (composeType == DatabaseHelper.NEW)
			title_tv.setText("Add Activity");
		else
			title_tv.setText("Edit Activity");
		name_et.setText(thisActivity.getActivity_name());
		desp_et.setText(thisActivity.getDesp());
		/*
		 * String[] startdatetime = thisActivity.get_Start().split(" ");
		 * start_date_btn.setText(startdatetime[0]);
		 * start_time_btn.setText(startdatetime[1].split(":")[0] + ":" +
		 * startdatetime[1].split(":")[1]); String[] enddatetime =
		 * thisActivity.get_End().split(" ");
		 * end_date_btn.setText(enddatetime[0]);
		 * end_time_btn.setText(enddatetime[1].split(":")[0] + ":" +
		 * enddatetime[1].split(":")[1]);
		 */
		String startdate = thisActivity.getStarttime();
		String startfulldate = MyDate.getWeekdayFromUTCTime(startdate) + ", "
				+ MyDate.transformUTCTimeToCustomStyle(startdate);
		String starttime = MyDate.getTimeWithAPMFromUTCTime(startdate);
		start_date_btn.setText(startfulldate);
		start_time_btn.setText(starttime);

		String enddate = thisActivity.getEndtime();
		String endfulldate = MyDate.getWeekdayFromUTCTime(enddate) + ", "
				+ MyDate.transformUTCTimeToCustomStyle(enddate);
		String endtime = MyDate.getTimeWithAPMFromUTCTime(enddate);
		end_date_btn.setText(endfulldate);
		end_time_btn.setText(endtime);

		alt_btn.setText(this.getAlert(thisActivity.getAlert()));
	}

	public String getAlert(int alt) {
		switch (alt) {
			case 0 :
				return "No alert";
			case 1 :
				return "5 minutes before";
			case 2 :
				return "15 minutes before";
			case 3 :
				return "30 minutes before";
			case 4 :
				return "1 hour before";
			case 5 :
				return "2 hours before";
			case 6 :
				return "1 day before";
			case 7 :
				return "2 days before";
			case 8 :
				return "On date of event";
			default :
				return "None";
		}
	}

	public int getAlertIndex(String alt) {
		if (alt.equalsIgnoreCase("No Alert"))
			return 0;
		else if (alt.equalsIgnoreCase("5 minutes"))
			return 1;
		else if (alt.equalsIgnoreCase("15 minutes"))
			return 2;
		else if (alt.equalsIgnoreCase("30 minutes"))
			return 3;
		else if (alt.equalsIgnoreCase("1 hour"))
			return 4;
		else if (alt.equalsIgnoreCase("2 hours"))
			return 5;
		else if (alt.equalsIgnoreCase("1 day"))
			return 6;
		else if (alt.equalsIgnoreCase("2 days"))
			return 7;
		else if (alt.equalsIgnoreCase("On date of event"))
			return 8;
		else
			return 0;
	}

	public void setStartDate(View v) {
		String[] startdatetime = MyDate.transformUTCDateToLocalDate(
				MyDate.STANDARD, thisActivity.getStarttime()).split(" ");
		String[] datecomponents = startdatetime[0].split("-");
		int year = Integer.valueOf(datecomponents[0]);
		int month = Integer.valueOf(datecomponents[1]) - 1;
		int day = Integer.valueOf(datecomponents[2]);
		DatePickerDialog dialog = new DatePickerDialog(this, this, year, month,
				day);
		dialog.setTitle("Set Start Date");
//		dialog.getDatePicker().setTag("startdate");
		dialog.show();
		StartOrEnd = START;
	}

	public void setEndDate(View v) {
		String[] enddatetime = MyDate.transformUTCDateToLocalDate(
				MyDate.STANDARD, thisActivity.getEndtime()).split(" ");
		String[] datecomponents = enddatetime[0].split("-");
		int year = Integer.valueOf(datecomponents[0]);
		int month = Integer.valueOf(datecomponents[1]) - 1;
		int day = Integer.valueOf(datecomponents[2]);
		DatePickerDialog dialog = new DatePickerDialog(this, this, year, month,
				day);
		dialog.setTitle("Set End Date");
//		dialog.getDatePicker().setTag("enddate");
		dialog.show();
		StartOrEnd = END;
	}

	public void setStartTime(View v) {
		String[] startdatetime = MyDate.transformUTCDateToLocalDate(
				MyDate.STANDARD, thisActivity.getStarttime()).split(" ");
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
				MyDate.STANDARD, thisActivity.getEndtime()).split(" ");
		String[] timecomponents = enddatetime[1].split(":");
		int hour = Integer.valueOf(timecomponents[0]);
		int minute = Integer.valueOf(timecomponents[1]);
		TimePickerDialog dialog = new TimePickerDialog(this, this, hour,
				minute, true);
		dialog.setTitle("Set End Time");
		dialog.show();
		StartOrEnd = END;
	}

	public void setAlert(View v) {
//		PopupMenu popup = new PopupMenu(getBaseContext(), v);
//		popup.getMenuInflater().inflate(R.menu.alertmenu, popup.getMenu());
//		popup.setOnMenuItemClickListener(this);
//		popup.show();
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
			start_date_btn.setText(fulldate);
			thisActivity.setStarttime(MyDate.transformLocalDateTimeToUTCFormat(year
					+ "-"
					+ monthstr
					+ "-"
					+ daystr
					+ " "
					+ MyDate.transformUTCDateToLocalDate(MyDate.STANDARD,
							thisActivity.getStarttime()).split(" ")[1]));
			if (MyDate.IsFirstDateLaterThanSecondDate(
					this.thisActivity.getStarttime(), this.thisActivity.getEndtime())) {
				this.thisActivity.setEndtime(this.thisActivity.getStarttime());
				end_date_btn.setText(start_date_btn.getText());
				end_time_btn.setText(start_time_btn.getText());
			}
		} else {
			end_date_btn.setText(fulldate);
			thisActivity.setEndtime(MyDate.transformLocalDateTimeToUTCFormat(year
					+ "-"
					+ monthstr
					+ "-"
					+ daystr
					+ " "
					+ MyDate.transformUTCDateToLocalDate(MyDate.STANDARD,
							thisActivity.getEndtime()).split(" ")[1]));
		}
	}

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
			start_time_btn.setText(time);
			thisActivity.setStarttime(MyDate
					.transformLocalDateTimeToUTCFormat(MyDate
							.transformUTCDateToLocalDate(MyDate.STANDARD,
									thisActivity.getStarttime()).split(" ")[0]
							+ " " + hourstr + ":" + minutestr + ":" + "00"));
			if (MyDate.IsFirstDateLaterThanSecondDate(
					this.thisActivity.getStarttime(), this.thisActivity.getEndtime())) {
				this.thisActivity.setEndtime(this.thisActivity.getStarttime());
				end_date_btn.setText(start_date_btn.getText());
				end_time_btn.setText(start_time_btn.getText());
			}
		} else {
			end_time_btn.setText(time);
			thisActivity.setEndtime(MyDate.transformLocalDateTimeToUTCFormat(MyDate
					.transformUTCDateToLocalDate(MyDate.STANDARD,
							thisActivity.getEndtime()).split(" ")[0]
					+ " " + hourstr + ":" + minutestr + ":" + "00"));
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		// Toast.makeText(getBaseContext(), "You selected the action : " +
		// item.getTitle(), Toast.LENGTH_SHORT).show;
		String title = item.getTitle().toString();
		thisActivity.setAlert(this.getAlertIndex(title));
		alt_btn.setText(title);
		return true;
	}

	public void composeActivityDone(View v) {
		if (name_et.getText().toString().equalsIgnoreCase("")) {
			Toast.makeText(this, "The activity name should not be empty",
					Toast.LENGTH_LONG).show();
			return;
		}

		thisActivity.setActivity_name(name_et.getText().toString());
		thisActivity.setDesp(desp_et.getText().toString());
		if (composeType == DatabaseHelper.NEW) {
			ContentValues newActivity = new ContentValues();;
			newActivity.put(ActivityTable.service_ID, thisActivity.getActivity_ID());
			newActivity.put(ActivityTable.own_ID, thisActivity.getOwner_ID());
			newActivity
					.put(ActivityTable.service_Name, thisActivity.getActivity_name());
			newActivity.put(ActivityTable.alert, thisActivity.getAlert());
			newActivity.put(ActivityTable.repeat, thisActivity.getRepeat());
			newActivity.put(ActivityTable.sharedrole, thisActivity.getRole());
			newActivity.put(ActivityTable.start_time, thisActivity.getStarttime());
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
			
			
			WebservicesHelper ws=new WebservicesHelper(mContext);
			ws.addActivity(thisActivity);
			Intent newIntent = new Intent(this, ShareActivity.class);
			newIntent.putExtra("type", DatabaseHelper.NEW);
			newIntent.putExtra("serviceid", thisActivity.getActivity_ID());
			this.startActivityForResult(newIntent, 11);

		} else {
			ContentValues cv = new ContentValues();;
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
			
			WebservicesHelper ws=new WebservicesHelper(mContext);
			ws.updateActivity(thisActivity);
			
			Intent resultIntent = new Intent();
			resultIntent.putExtra("id", thisActivity.getActivity_ID());
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}

		/*
		 * Intent resultIntent = new Intent(); resultIntent.putExtra("id",
		 * thisActivity.get_ID()); setResult(Activity.RESULT_OK, resultIntent);
		 * finish();
		 */
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case (11) : {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("id", thisActivity.getActivity_ID());
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
				break;
			}
		}
	}

}
