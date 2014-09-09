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
import com.e2wstudy.cschedule.models.Confirm;
import com.e2wstudy.cschedule.models.MyActivity;
import com.e2wstudy.cschedule.models.Schedule;
import com.e2wstudy.cschedule.models.Sharedmember;
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
	ExpandableListScheduleAdapter adapter = null;
	public static LoadingPopupViewHolder loadingPopup;
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
			processDataForAdapterListview();
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
			processDataForAdapterListview();
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
			view.btn_refresh.setEnabled(false);
			initData();

			// view.btn_all.setBackgroundResource(R.drawable.me_border);
			// view.btn_me.setBackgroundResource(R.drawable.me_border);
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
			processDataForAdapterListview();
			// view.btn_all.setBackgroundResource(R.drawable.me_border);
			// view.btn_me.setBackgroundResource(R.drawable.me_border);

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

	LoadingInterface iLoading=new LoadingInterface() {
		
		@Override
		public void onStart() {
			showLoading(getActivity());
		}
		
		@Override
		public void onFinish() {
			dimissDialog();
		}
	};
	UpdateConfirmStatusInterface iConfirm=new UpdateConfirmStatusInterface() {
		
		@Override
		public void onError(String error) {
			final ToastDialog errorToast = new ToastDialog(
					mContext,
					error);
			errorToast.show();
			errorToast.btnOk
					.setOnClickListener(new OnClickListener() {

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
	 * process data for adapter to set expandablelistview
	 * */
	private void processDataForAdapterListview() {
		ArrayList<String> listDateString = new ArrayList<String>();
		HashMap<String, ArrayList<Schedule>> listScheduleByDay = new HashMap<String, ArrayList<Schedule>>();
		ArrayList<Schedule> schedules = new ArrayList<Schedule>();

		dates.clear();

		SimpleDateFormat fullDatetimeFormat = new SimpleDateFormat(
				FORMAT_FULL_DATE);
		fullDatetimeFormat.setTimeZone(TimeZone.getDefault());

		SimpleDateFormat formatMmmDdYyyy = new SimpleDateFormat(
				FORMAT_MMM_DD_YYYY);
		fullDatetimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		switch (type) {
		case ME:
			view.btn_all
					.setBackgroundResource(R.drawable.btn_schedule_unselected);
			view.btn_me.setBackgroundResource(R.drawable.me_unselected);
			view.btn_all.setTextColor(mContext.getResources().getColor(
					R.color.text_today_schedule));
			view.btn_me.setTextColor(mContext.getResources().getColor(
					R.color.white));
			schedules = DatabaseHelper.getSharedDatabaseHelper(mContext).getMeSchedule();
			break;
		case ALL:
			view.btn_all.setBackgroundResource(R.drawable.me_unselected);
			view.btn_me
					.setBackgroundResource(R.drawable.btn_schedule_unselected);
			view.btn_all.setTextColor(mContext.getResources().getColor(
					R.color.white));
			view.btn_me.setTextColor(mContext.getResources().getColor(
					R.color.text_today_schedule));
			schedules = DatabaseHelper.getSharedDatabaseHelper(mContext).getAllSchedules();
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
						
//						MyActivity activity = DatabaseHelper.getSharedDatabaseHelper(
//								mContext).getActivity(schedule.getService_ID());
//						schedule.setMyActivity(activity);
//						List<Confirm> memberids = DatabaseHelper.getSharedDatabaseHelper(
//								mContext).getParticipantsForSchedule(
//								schedule.getSchedule_ID());
//						schedule.setListMemberId(memberids);
//						
//						if(memberids!=null&&memberids.size()>0)
//						{
//							ArrayList<Sharedmember> listSharedMember=new ArrayList<Sharedmember>();
//							for(Confirm member:memberids)
//							{
//								final Sharedmember sm = DatabaseHelper.getSharedDatabaseHelper(
//										mContext).getSharedmember(member.getMemberId(),
//										activity.getActivity_ID());
//								listSharedMember.add(sm);
//							}
//							schedule.setListSharedMember(listSharedMember);
//						
//						}
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

					if (adapter == null) {
						adapter = new ExpandableListScheduleAdapter(
								getActivity(), listDateString,
								listScheduleByDay,iConfirm,iLoading);
						view.expand_list_schedule.setAdapter(adapter);
					} else {
						adapter.listSchedulesByDay = listDateString;
						adapter.scheduleCollection = listScheduleByDay;
						adapter.notifyDataSetChanged();
					}
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
				} else {
					ExpandableListScheduleAdapter adapter = new ExpandableListScheduleAdapter();
					view.expand_list_schedule.setAdapter(adapter);
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

			ArrayList<Schedule> listSchedules = new ArrayList<Schedule>();
			switch (type) {
			case ME:
				listSchedules = DatabaseHelper.getSharedDatabaseHelper(mContext).getAllSchedules();
				break;
			case ALL:
				listSchedules = DatabaseHelper.getSharedDatabaseHelper(mContext).getMeSchedule();
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

	public void scheduleDownloadComplete() {
		processDataForAdapterListview();
		view.btn_refresh.setEnabled(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
