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
import com.dling61.calendarschedule.R;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

/**
 * @author Huyen
 * 
 */
public class ScheduleFragment extends Fragment implements OnClickListener {
	ScheduleView view;
	Context mContext;

	final int ALL = 1;
	final int ME = 2;
	final int TODAY = 3;
	int REFRESH = 4;
	int type = ME;
	boolean isToday = false;

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
	private void onClickListener() {
		view.btn_add_schedule.setOnClickListener(this);
		view.btn_me.setOnClickListener(this);
		view.btn_refresh.setOnClickListener(this);
		view.btn_all.setOnClickListener(this);
		view.btn_today.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v == view.btn_add_schedule) {
			Intent intent = new Intent(mContext,
					CreateNewScheduleActivity.class);
			intent.putExtra(CommConstant.TYPE, DatabaseHelper.NEW);
			mContext.startActivity(intent);
		} else if (v == view.btn_all) {
			type = ALL;
			processDataForAdapterListview();
			view.btn_all.setBackgroundResource(R.drawable.me_selected);
			view.btn_me.setBackgroundResource(R.drawable.me_border);
			// view.btn_today.setBackgroundResource(R.drawable.today_border);
		} else if (v == view.btn_me) {
			type = ME;
			processDataForAdapterListview();
			view.btn_all.setBackgroundResource(R.drawable.me_border);
			view.btn_me.setBackgroundResource(R.drawable.me_selected);
			// view.btn_today.setBackgroundResource(R.drawable.today_border);
		}
		// will show all schedule for all day
		else if (v == view.btn_refresh) {
			isToday = false;
			processDataForAdapterListview();
			// view.btn_all.setBackgroundResource(R.drawable.me_border);
			// view.btn_me.setBackgroundResource(R.drawable.me_border);
			view.btn_today.setBackgroundResource(R.drawable.today_border);
		} else if (v == view.btn_today) {
			isToday = true;
			processDataForAdapterListview();
			// view.btn_all.setBackgroundResource(R.drawable.me_border);
			// view.btn_me.setBackgroundResource(R.drawable.me_border);
			view.btn_today.setBackgroundResource(R.drawable.me_selected);
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

	}

	/**
	 * process data for adapter to set expandablelistview
	 * */
	private void processDataForAdapterListview() {
		ArrayList<String> listDateString = new ArrayList<String>();
		HashMap<String, ArrayList<Schedule>> listScheduleByDay = new HashMap<String, ArrayList<Schedule>>();
		DatabaseHelper dbHelper = DatabaseHelper
				.getSharedDatabaseHelper(mContext);
		ArrayList<Schedule> schedules = new ArrayList<Schedule>();// =
																	// dbHelper.getAllSchedules();
		switch (type) {
		case ME:
			schedules = dbHelper.getMeSchedule();
			break;
		case ALL:
			schedules = dbHelper.getAllSchedules();
			break;
		default:
			break;
		}

		if (schedules != null && schedules.size() > 0) {
			Date today = new Date();
			String todayStr = new SimpleDateFormat("MMM dd, yyyy")
					.format(today);
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

					if ((!isToday)
							|| (isToday && todayStr
									.equalsIgnoreCase(dateString))) {
						Calendar c = Calendar.getInstance();

						c.setTime(date);
						String dateInWeekString = "";
						int dateInWeek = c.get(Calendar.DAY_OF_WEEK);
						switch (dateInWeek) {
						case Calendar.MONDAY:
							dateInWeekString = "Mon";
							break;
						case Calendar.TUESDAY:
							dateInWeekString = "Tue";
							break;
						case Calendar.WEDNESDAY:
							dateInWeekString = "Wed";
							break;
						case Calendar.THURSDAY:
							dateInWeekString = "Thu";
							break;
						case Calendar.FRIDAY:
							dateInWeekString = "Fri";
							break;
						case Calendar.SATURDAY:
							dateInWeekString = "Sat";
							break;
						case Calendar.SUNDAY:
							dateInWeekString = "Sun";
							break;
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
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Set<String> setKeys = listScheduleByDay.keySet();
			List<String> list = new ArrayList<String>(setKeys);
			listDateString = (ArrayList<String>) list;

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

			view.expand_list_schedule.setVisibility(View.VISIBLE);
			view.layout_top.setVisibility(View.VISIBLE);
			view.layout_no_schedule.setVisibility(View.GONE);
			
		} else {
			// try {
			// ExpandableListScheduleAdapter adapter =
			// (ExpandableListScheduleAdapter) view.expand_list_schedule
			// .getAdapter();
			// if (adapter != null) {
			// adapter.clearAdapter();
			// }
			// } catch (Exception ex) {
			// ex.printStackTrace();
			// }
			try {
				ExpandableListScheduleAdapter adapter = new ExpandableListScheduleAdapter();
				view.expand_list_schedule.setAdapter(adapter);
				view.expand_list_schedule.setVisibility(View.GONE);
				view.layout_no_schedule.setVisibility(View.VISIBLE);
				view.layout_top.setVisibility(View.GONE);
			
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	BroadcastReceiver scheduleReadyComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			Log.d("add schedule", "receiver");
			processDataForAdapterListview();
		}

	};

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
