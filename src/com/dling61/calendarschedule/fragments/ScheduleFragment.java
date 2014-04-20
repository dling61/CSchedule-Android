package com.dling61.calendarschedule.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import com.dling61.calendarschedule.CreateNewScheduleActivity;
import com.dling61.calendarschedule.adapter.ExpandableListScheduleAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.Schedule;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.views.ScheduleView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

/**
 * @author Huyen return account information by token
 * 
 */
public class ScheduleFragment extends Fragment implements OnClickListener {
	ScheduleView view;
	Context mContext;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		// initData();
		onClickListener();

	}

	public static ScheduleFragment getInstance() {
		return ScheduleFragment.getInstance();
	}

	/**
	 * On click listener
	 * */
	private void onClickListener()
	{
		view.btn_add_schedule.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		if (v == view.btn_add_schedule) {
			Intent intent = new Intent(mContext,
					CreateNewScheduleActivity.class);
			intent.putExtra(CommConstant.TYPE, DatabaseHelper.NEW);
			mContext.startActivity(intent);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = new ScheduleView(getActivity());
		this.view = (ScheduleView) view;
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// IntentFilter filterRefreshUpdate = new IntentFilter();
		// filterRefreshUpdate.addAction(CommConstant.DELETE_SCHEDULE_COMPLETE);
		// filterRefreshUpdate.addAction(CommConstant.SCHEDULE_READY);
		// getActivity().registerReceiver(scheduleReadyComplete,filterRefreshUpdate);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		IntentFilter filterRefreshUpdate = new IntentFilter();
		filterRefreshUpdate.addAction(CommConstant.DELETE_SCHEDULE_COMPLETE);
		filterRefreshUpdate.addAction(CommConstant.SCHEDULE_READY);
		filterRefreshUpdate.addAction(CommConstant.UPDATE_SCHEDULE);
		getActivity().registerReceiver(scheduleReadyComplete,
				filterRefreshUpdate);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		getActivity().unregisterReceiver(scheduleReadyComplete);
	}

	@Override
	public void onResume() {
		super.onResume();
		// IntentFilter filterRefreshUpdate = new IntentFilter();
		// filterRefreshUpdate.addAction(CommConstant.DELETE_ACTIVITY_COMPLETE);
		// filterRefreshUpdate.addAction(CommConstant.SCHEDULE_READY);
		// mContext.registerReceiver(scheduleReadyComplete,
		// filterRefreshUpdate);
		// mContext.registerReceiver(scheduleReadyComplete,filterRefreshUpdate);
		// DatabaseHelper dbHelper = DatabaseHelper
		// .getSharedDatabaseHelper(mContext);
		// ArrayList<Schedule> schedules = dbHelper.getAllSchedules();
		// ArrayList<String> listDateString = new ArrayList<String>();
		// HashMap<String, ArrayList<Schedule>> listScheduleByDay = new
		// HashMap<String, ArrayList<Schedule>>();
		// if (schedules != null && schedules.size() > 0)
		// // group schedule same date
		// for (Schedule schedule : schedules) {
		//
		// SimpleDateFormat dateFormat = new SimpleDateFormat(
		// CommConstant.DATE_TIME_FORMAT);
		// dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Date date;
		// try {
		// date = (Date) dateFormat.parse(schedule.getEndtime());
		//
		// String dateString = new SimpleDateFormat("MMM dd, yyyy")
		// .format(date);
		//
		// Calendar c = Calendar.getInstance();
		//
		// c.setTime(date);
		// String dateInWeekString = "";
		// int dateInWeek = c.get(Calendar.DAY_OF_WEEK);
		// switch (dateInWeek) {
		// case Calendar.MONDAY:
		// dateInWeekString = "Monday";
		// break;
		// case Calendar.TUESDAY:
		// dateInWeekString = "Tuesday";
		// break;
		// case Calendar.WEDNESDAY:
		// dateInWeekString = "Wednesday";
		// break;
		// case Calendar.THURSDAY:
		// dateInWeekString = "Thursday";
		// break;
		// case Calendar.FRIDAY:
		// dateInWeekString = "Friday";
		// break;
		// case Calendar.SATURDAY:
		// dateInWeekString = "Saturday";
		// case Calendar.SUNDAY:
		// dateInWeekString = "Sunday";
		//
		// default:
		// break;
		// }
		//
		// String strDateTime = dateInWeekString + ";" + dateString;
		// ArrayList<Schedule> listSchedule = listScheduleByDay
		// .get(strDateTime);
		// if (listSchedule == null) {
		// listSchedule = new ArrayList<Schedule>();
		// }
		// listSchedule.add(schedule);
		// listScheduleByDay.put(strDateTime, listSchedule);
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// Set<String> setKeys = listScheduleByDay.keySet();
		// List<String> list = new ArrayList<String>(setKeys);
		// listDateString = (ArrayList<String>) list;
		//
		// // ScheduleAdapter adapter = new ScheduleAdapter(mContext, dbHelper,
		// // schedules);
		// // view.list_schedule.setAdapter(adapter);
		// ExpandableListScheduleAdapter adapter = new
		// ExpandableListScheduleAdapter(
		// getActivity(), listDateString, listScheduleByDay);
		// view.expand_list_schedule.setAdapter(adapter);
		// int count = adapter.getGroupCount();
		// for (int position = 1; position <= count; position++)
		// view.expand_list_schedule.expandGroup(position - 1);
	}

	BroadcastReceiver scheduleReadyComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			ArrayList<Schedule> schedules = dbHelper.getAllSchedules();
			ArrayList<String> listDateString = new ArrayList<String>();
			HashMap<String, ArrayList<Schedule>> listScheduleByDay = new HashMap<String, ArrayList<Schedule>>();
			if (schedules != null && schedules.size() > 0)
				// group schedule same date
				for (Schedule schedule : schedules) {

					SimpleDateFormat dateFormat = new SimpleDateFormat(
							CommConstant.DATE_TIME_FORMAT);
					dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
					Date date;
					try {
						date = (Date) dateFormat.parse(schedule.getStarttime());

						String dateString = new SimpleDateFormat("MMM dd, yyyy")
								.format(date);

						Calendar c = Calendar.getInstance();

						c.setTime(date);
						String dateInWeekString = "";
						int dateInWeek = c.get(Calendar.DAY_OF_WEEK);
						switch (dateInWeek) {
						case Calendar.MONDAY:
							dateInWeekString = "Monday";
							break;
						case Calendar.TUESDAY:
							dateInWeekString = "Tuesday";
							break;
						case Calendar.WEDNESDAY:
							dateInWeekString = "Wednesday";
							break;
						case Calendar.THURSDAY:
							dateInWeekString = "Thursday";
							break;
						case Calendar.FRIDAY:
							dateInWeekString = "Friday";
							break;
						case Calendar.SATURDAY:
							dateInWeekString = "Saturday";
						case Calendar.SUNDAY:
							dateInWeekString = "Sunday";

						default:
							break;
						}

						String strDateTime = dateInWeekString + ";"
								+ dateString;
						ArrayList<Schedule> listSchedule = listScheduleByDay
								.get(strDateTime);
						if (listSchedule == null) {
							listSchedule = new ArrayList<Schedule>();
						}
						listSchedule.add(schedule);
						listScheduleByDay.put(strDateTime, listSchedule);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			Set<String> setKeys = listScheduleByDay.keySet();
			List<String> list = new ArrayList<String>(setKeys);
			listDateString = (ArrayList<String>) list;

			// ScheduleAdapter adapter = new ScheduleAdapter(mContext, dbHelper,
			// schedules);
			// view.list_schedule.setAdapter(adapter);
			ExpandableListScheduleAdapter adapter = new ExpandableListScheduleAdapter(
					getActivity(), listDateString, listScheduleByDay);
			view.expand_list_schedule.setAdapter(adapter);
			view.expand_list_schedule
					.setOnGroupClickListener(new OnGroupClickListener() {
						@Override
						public boolean onGroupClick(ExpandableListView parent,
								View v, int groupPosition, long id) {
							return true; // This way the expander cannot be
											// collapsed
						}
					});
			int count = adapter.getGroupCount();
			for (int position = 1; position <= count; position++)
				view.expand_list_schedule.expandGroup(position - 1);
		}
	};

	@Override
	public void onPause() {
		super.onPause();
		// getActivity().unregisterReceiver(scheduleReadyComplete);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
