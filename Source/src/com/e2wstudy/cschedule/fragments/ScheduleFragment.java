package com.e2wstudy.cschedule.fragments;

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
import com.e2wstudy.cschedule.CategoryTabActivity;
import com.e2wstudy.cschedule.CreateNewScheduleActivity;
import com.e2wstudy.cschedule.R;
import com.e2wstudy.cschedule.adapter.ExpandableListScheduleAdapter;
import com.e2wstudy.cschedule.db.DatabaseHelper;
import com.e2wstudy.cschedule.models.Schedule;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.MyDate;
import com.e2wstudy.cschedule.utils.Utils;
import com.e2wstudy.cschedule.views.LoadingPopupViewHolder;
import com.e2wstudy.cschedule.views.ScheduleView;
import com.e2wstudy.cschedule.views.ToastDialog;
import com.e2wstudy.schedule.interfaces.LoadingInterface;
import com.e2wstudy.schedule.interfaces.UpdateConfirmStatusInterface;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

	String FORMAT_MMM_DD_YYYY = "MMM dd, yyyy";// format MMM-dd-yyyy
	String FORMAT_FULL_DATE = "yyyy-MM-dd HH:mm:ss";// yyyy-MM-dd HH:mm:ss
	ExpandableListScheduleAdapter adapter = null;
	public static LoadingPopupViewHolder loadingPopup;

	// ArrayList<String> listDateString = new ArrayList<String>();
	// HashMap<String, ArrayList<Schedule>> listScheduleByDay = new
	// HashMap<String, ArrayList<Schedule>>();
	// ArrayList<Schedule> schedules = new ArrayList<Schedule>();

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
			Utils.slideUpDown(mContext);
		} else if (v == view.btn_all) {
			type = ALL;
			// processDataForAdapterListview();
			new LoadScheduleFromDbTask().execute();
			view.btn_all.setBackgroundResource(R.drawable.me_unselected);
			view.btn_me
					.setBackgroundResource(R.drawable.btn_schedule_unselected);
			view.btn_all.setTextColor(mContext.getResources().getColor(
					R.color.white));
			view.btn_me.setTextColor(mContext.getResources().getColor(
					R.color.me_unselected));

			// view.btn_today.setBackgroundResource(R.drawable.today_border);
		} else if (v == view.btn_me) {
			type = ME;
			// processDataForAdapterListview();
			new LoadScheduleFromDbTask().execute();
			view.btn_all
					.setBackgroundResource(R.drawable.btn_schedule_unselected);
			view.btn_me.setBackgroundResource(R.drawable.me_unselected);
			view.btn_all.setTextColor(mContext.getResources().getColor(
					R.color.me_unselected));
			view.btn_me.setTextColor(mContext.getResources().getColor(
					R.color.white));
		}
		// will show all schedule for all day
		else if (v == view.btn_refresh) {
			isToday = false;
			// processDataForAdapterListview();
			new LoadScheduleFromDbTask().execute();
			// initData();

			view.btn_today.setBackgroundResource(R.drawable.today_border);
			view.btn_today.setTextColor(mContext.getResources().getColor(
					R.color.me_unselected));
		} else if (v == view.btn_today) {
			isToday = !isToday;
			if (isToday) {
				view.btn_today.setBackgroundResource(R.drawable.me_unselected);
				view.btn_today.setTextColor(mContext.getResources().getColor(
						R.color.btn_schedule_unselected));
			} else {
				view.btn_today.setBackgroundResource(R.drawable.today_border);
				view.btn_today.setTextColor(mContext.getResources().getColor(
						R.color.text_today_schedule));
			}
			// processDataForAdapterListview();
			new LoadScheduleFromDbTask().execute();
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
	public void onResume() {
		super.onResume();
		scheduleDownloadComplete();
	}

	// show loading
	public void showLoading(Context mContext) {
		if (loadingPopup == null) {
			loadingPopup = new LoadingPopupViewHolder(mContext,
					CategoryTabActivity.DIALOG_LOADING_THEME);
		}
		loadingPopup.setCancelable(true);
		if (!loadingPopup.isShowing()) {
			loadingPopup.show();
		}
	}

	public void dimissDialog() {
		if (loadingPopup != null && loadingPopup.isShowing()) {
			loadingPopup.dismiss();
		}
	}

	private void initData() {
		// get all data after that, go to tab

		// WebservicesHelper ws = new WebservicesHelper(mContext);
		// ws.getAllActivitys();
		// ws.getParticipantsFromWeb();
		// ws.getAllSchedule();
		now = new Date().getTime();
		dates = new ArrayList<Date>();

	}

	LoadingInterface iLoading = new LoadingInterface() {

		@Override
		public void onStart() {
			showLoading(getActivity());
		}

		@Override
		public void onFinish() {
			dimissDialog();
		}
	};
	UpdateConfirmStatusInterface iConfirm = new UpdateConfirmStatusInterface() {

		@Override
		public void onError(String error) {
			final ToastDialog errorToast = new ToastDialog(mContext, error);
			errorToast.show();
			errorToast.btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					errorToast.dismiss();
				}
			});
		}

		@Override
		public void onComplete() {
			scheduleDownloadComplete();
		}
	};

	/**
	 * Load schedule from db
	 * */
	class LoadScheduleFromDbTask extends
			AsyncTask<String, Void, ArrayList<Integer>> {
		public LoadScheduleFromDbTask() {
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showLoading(mContext);
		}

		@Override
		protected ArrayList<Integer> doInBackground(String... params) {
			return processDataForAdapterListview();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(ArrayList<Integer> result) {
			Log.d("schedule result", result + "");
			if (result != null && result.size() > 0) {
				if (result.size() == 2) {
					int count = result.get(0);
					int groupScroll = result.get(1);
					setViewData(count, groupScroll);
				}

				else {
					if (adapter != null) {
						adapter.listSchedulesByDay.clear();
						adapter.scheduleCollection.clear();
						if (view.expand_list_schedule.getAdapter() != null) {

							adapter.notifyDataSetChanged();
						} else {
							view.expand_list_schedule.setAdapter(adapter);
						}
					} else {
						// adapter.notifyDataSetChanged();
					}

					if (result.size() > 0) {
						if (result.get(0) == 10) {
							setViewNoData();
						}

					}
				}

			} else {
				if (adapter != null) {
					adapter.listSchedulesByDay.clear();
					adapter.scheduleCollection.clear();
					if (view.expand_list_schedule.getAdapter() != null) {

						adapter.notifyDataSetChanged();
					} else {
						view.expand_list_schedule.setAdapter(adapter);
					}
				}
			}
			dimissDialog();
		}
	}

	/**
	 * process data for adapter to set expandablelistview
	 * */
	private ArrayList<Integer> processDataForAdapterListview() {

		ArrayList<String> listDateString = new ArrayList<String>();
		HashMap<String, ArrayList<Schedule>> listScheduleByDay = new HashMap<String, ArrayList<Schedule>>();
		ArrayList<Schedule> schedules = new ArrayList<Schedule>();
		ArrayList<Integer> returnValue = new ArrayList<Integer>();
		int group_scroll = 0;
		dates.clear();

		SimpleDateFormat fullDatetimeFormat = new SimpleDateFormat(
				FORMAT_FULL_DATE);
		fullDatetimeFormat.setTimeZone(TimeZone.getDefault());

		SimpleDateFormat formatMmmDdYyyy = new SimpleDateFormat(
				FORMAT_MMM_DD_YYYY);
		fullDatetimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		switch (type) {
		case ME:
			schedules = DatabaseHelper.getSharedDatabaseHelper(mContext)
					.getMeSchedule();
			try {
				view.btn_all
						.setBackgroundResource(R.drawable.btn_schedule_unselected);
				view.btn_me.setBackgroundResource(R.drawable.me_unselected);
				view.btn_all.setTextColor(mContext.getResources().getColor(
						R.color.text_today_schedule));
				view.btn_me.setTextColor(mContext.getResources().getColor(
						R.color.white));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case ALL:
			schedules = DatabaseHelper.getSharedDatabaseHelper(mContext)
					.getAllSchedules();
			try {
				view.btn_all.setBackgroundResource(R.drawable.me_unselected);
				view.btn_me
						.setBackgroundResource(R.drawable.btn_schedule_unselected);
				view.btn_all.setTextColor(mContext.getResources().getColor(
						R.color.white));
				view.btn_me.setTextColor(mContext.getResources().getColor(
						R.color.text_today_schedule));
			} catch (Exception e) {
				e.printStackTrace();
			}
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

					// if not today or istoday&& datestring is today
					if ((!isToday)
							|| (isToday && todayStrLess
									.equalsIgnoreCase(dateString))) {
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
							break;
						case Calendar.SUNDAY:
							dateInWeekString = "Sunday";
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

					// if (adapter == null) {
					// adapter = new ExpandableListScheduleAdapter(
					// getActivity(), listDateString,
					// listScheduleByDay, iConfirm, iLoading);
					// view.expand_list_schedule.setAdapter(adapter);
					// } else {
					// adapter.listSchedulesByDay = listDateString;
					// adapter.scheduleCollection = listScheduleByDay;
					// adapter.notifyDataSetChanged();
					// }
					// // adapter.setNearestDate(closest);
					// Log.d("scroll position", group_scroll + "");
					//
					// view.expand_list_schedule
					// .setOnGroupClickListener(new OnGroupClickListener() {
					// @Override
					// public boolean onGroupClick(
					// ExpandableListView parent, View v,
					// int groupPosition, long id) {
					// return true; // This way the expander cannot
					// // be
					// // collapsed
					// }
					// });
					// int count = adapter.getGroupCount();
					// for (int position = 1; position <= count; position++)
					// view.expand_list_schedule.expandGroup(position - 1);
					//
					// // scroll to nearest
					// view.expand_list_schedule.setSelectedGroup(group_scroll);
					// } else {
					// if (adapter != null) {
					// adapter.listSchedulesByDay.clear();
					// adapter.scheduleCollection.clear();
					// adapter.notifyDataSetChanged();
					// }

					// setViewData(listDateString, listScheduleByDay,
					// schedules);

					if (adapter == null) {
						adapter = new ExpandableListScheduleAdapter(
								getActivity(), listDateString,
								listScheduleByDay, iConfirm, iLoading);
						// view.expand_list_schedule.setAdapter(adapter);
					} else {
						adapter.listSchedulesByDay = listDateString;
						adapter.scheduleCollection = listScheduleByDay;
						// adapter.notifyDataSetChanged();
					}

					int count = adapter.getGroupCount();
					// for (int position = 1; position <= count; position++)
					// view.expand_list_schedule.expandGroup(position - 1);
					// scroll to nearest
					// view.expand_list_schedule.setSelectedGroup(group_scroll);

					returnValue.add(count);
					returnValue.add(group_scroll);

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// view.expand_list_schedule.setVisibility(View.VISIBLE);
			// view.layout_top.setVisibility(View.VISIBLE);
			// view.btn_refresh.setVisibility(View.VISIBLE);
			// view.btn_me.setVisibility(View.VISIBLE);
			// view.btn_all.setVisibility(View.VISIBLE);
			// view.btn_today.setVisibility(View.VISIBLE);
			// view.layout_no_schedule.setVisibility(View.GONE);
		} else {

			ArrayList<Schedule> listSchedules = new ArrayList<Schedule>();
			switch (type) {
			case ME:
				listSchedules = DatabaseHelper
						.getSharedDatabaseHelper(mContext).getAllSchedules();
				break;
			case ALL:
				listSchedules = DatabaseHelper
						.getSharedDatabaseHelper(mContext).getMeSchedule();
				break;
			default:
				break;
			}
			if (listSchedules == null || listSchedules.size() == 0) {
				returnValue.add(10);
			} else {
				returnValue.add(20);
			}
		}
		return returnValue;
	}

	public void setViewNoData() {
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

	public void setViewData(int count, int groupScroll) {

		view.expand_list_schedule.setVisibility(View.VISIBLE);
		view.layout_top.setVisibility(View.VISIBLE);
		view.btn_refresh.setVisibility(View.VISIBLE);
		view.btn_me.setVisibility(View.VISIBLE);
		view.btn_all.setVisibility(View.VISIBLE);
		view.btn_today.setVisibility(View.VISIBLE);
		view.layout_no_schedule.setVisibility(View.GONE);
		if (view.expand_list_schedule.getAdapter() == null) {
			// if(adapter!=null)
			// {
			view.expand_list_schedule.setAdapter(adapter);
			// }
		} else {
			adapter.notifyDataSetChanged();
		}
		Log.d("scroll position", groupScroll + "");
		view.expand_list_schedule
				.setOnGroupClickListener(new OnGroupClickListener() {
					@Override
					public boolean onGroupClick(ExpandableListView parent,
							View v, int groupPosition, long id) {
						return true; // This way the expander cannot
										// be
										// collapsed
					}
				});
		int size = 0;
		ExpandableListScheduleAdapter ada=(ExpandableListScheduleAdapter) view.expand_list_schedule.getExpandableListAdapter();
		if (ada != null) {
			
			size = ada.getGroupCount();
		}
		Log.d("size", size + "");
		if (size > 0) {
			for (int position = 0; position < size; position++) {
				view.expand_list_schedule.expandGroup(position);
				Log.d("position", position + "");
			}
		}
		if (groupScroll < size) {
			// scroll to nearest
			view.expand_list_schedule.setSelectedGroup(groupScroll);
		}
	}

	public void scheduleDownloadComplete() {
		// processDataForAdapterListview();

		new LoadScheduleFromDbTask().execute();
		view.btn_refresh.setEnabled(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
