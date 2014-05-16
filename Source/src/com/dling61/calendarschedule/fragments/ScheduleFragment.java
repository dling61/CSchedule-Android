package com.dling61.calendarschedule.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.MyDate;
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
	int type = ALL;
	boolean isToday = false;
	long now = 0;
	List<Date> dates;
	int group_scroll = 0;
	String FORMAT_MMM_DD_YYYY = "MMM dd, yyyy";// format MMM-dd-yyyy
	String FORMAT_FULL_DATE = "yyyy-MM-dd HH:mm:ss";// yyyy-MM-dd HH:mm:ss

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		initData();
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
			// processDataForAdapterListview();
			view.btn_refresh.setEnabled(false);
			initData();

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

	private void initData() {
		// get all data after that, go to tab

		WebservicesHelper ws = new WebservicesHelper(mContext);
		ws.getAllActivitys();
		ws.getParticipantsFromWeb();
		ws.getAllSchedule();
		now = new Date().getTime();
		dates = new ArrayList<Date>();

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
		dates.clear();

		SimpleDateFormat fullDatetimeFormat = new SimpleDateFormat(
				FORMAT_FULL_DATE);
		SimpleDateFormat formatMmmDdYyyy = new SimpleDateFormat(
				FORMAT_MMM_DD_YYYY);

		switch (type) {
		case ME:
			view.btn_me.setBackgroundResource(R.drawable.me_selected);
			view.btn_all.setBackgroundResource(R.drawable.me_unselected);
			schedules = dbHelper.getMeSchedule();
			break;
		case ALL:
			view.btn_me.setBackgroundResource(R.drawable.me_unselected);
			view.btn_all.setBackgroundResource(R.drawable.me_selected);
			schedules = dbHelper.getAllSchedules();
			break;
		default:
			break;
		}

		if (schedules != null && schedules.size() > 0) {
			Date today = new Date();
			String todayStr = fullDatetimeFormat.format(today);

			String todayStrLess = formatMmmDdYyyy.format(today);

			// group schedule same date
			for (Schedule schedule : schedules) {

				fullDatetimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				Date date;
				try {
					date = (Date) fullDatetimeFormat.parse(schedule
							.getStarttime());

					String dateString = formatMmmDdYyyy.format(date);

					String dateStr = fullDatetimeFormat.format(date);
					// only add today or future day
					if (!MyDate.IsFirstDateLaterThanSecondDate(dateStr,
							todayStr)) {
						dates.add(date);
					}
					if ((!isToday)
							|| (isToday && todayStrLess
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

						// MyObject myObject=new MyObject();
						// myObject.setDateTime(new Date(strDateTime));
						// dateGroupList.add(myObject);
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
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Set<String> setKeys = listScheduleByDay.keySet();
			List<String> list = new ArrayList<String>(setKeys);
			listDateString = (ArrayList<String>) list;

			try

			{
				if (listDateString != null && listDateString.size() > 0) {
					// sort listDateString by date
					// sort group
					Collections.sort(listDateString, new Comparator<String>() {
						public int compare(String date1, String date2) {
							if (date1 != null && date2 != null) {
								String[] date_time_1 = date1.split(";");
								String[] date_time_2 = date2.split(";");
								if (date_time_1 != null && date_time_2 != null) {

									return (new Date(date_time_1[1]))
											.compareTo(new Date(date_time_2[1]));
								}
							}
							return 0;
						}
					});
					long min = Long.MAX_VALUE;
					for (int pos = 0; pos < listDateString.size(); pos++) {
						String date_time_str = listDateString.get(pos);
						if (date_time_str != null) {
							String[] date_time = date_time_str.split(";");

							if (date_time != null) {

								try {
									Date date1 = formatMmmDdYyyy
											.parse(date_time[1]);
									//

									long diff1 = Math
											.abs(date1.getTime() - now);
									if (min > diff1) {
										min = diff1;
										group_scroll = pos;
									}
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

					}

					ExpandableListScheduleAdapter adapter = new ExpandableListScheduleAdapter(
							getActivity(), listDateString, listScheduleByDay);
					view.expand_list_schedule.setAdapter(adapter);
					// adapter.setNearestDate(closest);
					Log.d("scroll position", group_scroll + "");

					view.expand_list_schedule
							.setOnGroupClickListener(new OnGroupClickListener() {
								@Override
								public boolean onGroupClick(
										ExpandableListView parent, View v,
										int groupPosition, long id) {
									return true; // This way the expander cannot
													// be
													// collapsed
								}
							});
					int count = adapter.getGroupCount();
					for (int position = 1; position <= count; position++)
						view.expand_list_schedule.expandGroup(position - 1);

					// scroll to nearest
					view.expand_list_schedule.setSelectedGroup(group_scroll);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			view.expand_list_schedule.setVisibility(View.VISIBLE);
			view.layout_top.setVisibility(View.VISIBLE);
			view.btn_refresh.setVisibility(View.VISIBLE);
			view.btn_me.setVisibility(View.VISIBLE);
			view.btn_all.setVisibility(View.VISIBLE);
			view.btn_today.setVisibility(View.VISIBLE);
			view.layout_no_schedule.setVisibility(View.GONE);

		} else {

			ExpandableListScheduleAdapter adapter = new ExpandableListScheduleAdapter();
			view.expand_list_schedule.setAdapter(adapter);

			ArrayList<Schedule> listSchedules = new ArrayList<>();
			switch (type) {
			case ME:
				listSchedules = dbHelper.getAllSchedules();
				break;
			case ALL:
				listSchedules = dbHelper.getMeSchedule();
				break;
			default:
				break;
			}
			if (listSchedules == null || listSchedules.size() == 0) {
				try {
					view.expand_list_schedule.setVisibility(View.GONE);
					view.layout_no_schedule.setVisibility(View.VISIBLE);
					view.layout_top.setVisibility(View.VISIBLE);
					view.btn_refresh.setVisibility(View.VISIBLE);
					view.btn_me.setVisibility(View.GONE);
					view.btn_all.setVisibility(View.GONE);
					view.btn_today.setVisibility(View.GONE);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	BroadcastReceiver scheduleReadyComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			Log.d("add schedule", "receiver");
			processDataForAdapterListview();
			view.btn_refresh.setEnabled(true);
		}

	};

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}