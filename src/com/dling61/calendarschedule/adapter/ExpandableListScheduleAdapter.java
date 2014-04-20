package com.dling61.calendarschedule.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dling61.calendarschedule.AddNewActivity;
import com.dling61.calendarschedule.CreateNewScheduleActivity;
import com.dling61.calendarschedule.R;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.MyActivity;
import com.dling61.calendarschedule.models.Schedule;
import com.dling61.calendarschedule.models.Sharedmember;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.MyDate;
import com.dling61.calendarschedule.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListScheduleAdapter extends BaseExpandableListAdapter {

	private Context context;
	private Map<String, ArrayList<Schedule>> scheduleCollection;
	private ArrayList<String> listSchedulesByDay;
	private LayoutInflater mInflater;
	DatabaseHelper dbHelper;

	public ExpandableListScheduleAdapter(Context context,
			ArrayList<String> listSchedulesByDay,
			Map<String, ArrayList<Schedule>> scheduleCollection) {
		this.context = context;
		this.scheduleCollection = scheduleCollection;
		this.listSchedulesByDay = listSchedulesByDay;
		mInflater = LayoutInflater.from(context);
		dbHelper = DatabaseHelper.getSharedDatabaseHelper(context);
	}

	public Object getChild(int groupPosition, int childPosition) {
		return scheduleCollection.get(listSchedulesByDay.get(groupPosition))
				.get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ScheduleViewHolder viewHolder;
		if ((convertView == null)) {
			viewHolder = new ScheduleViewHolder();
			convertView = mInflater.inflate(R.layout.schedulecell, null);
			viewHolder.service_TV = (TextView) convertView
					.findViewById(R.id.schedule_servicename_tv);
			viewHolder.time_TV = (TextView) convertView
					.findViewById(R.id.schedule_date_tv);
			viewHolder.participants_TV = (TextView) convertView
					.findViewById(R.id.schedule_participants_tv);
			viewHolder.service_TV.setTypeface(Utils.getTypeFace(context));
			viewHolder.time_TV.setTypeface(Utils.getTypeFace(context));
			viewHolder.participants_TV.setTypeface(Utils.getTypeFace(context));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ScheduleViewHolder) convertView.getTag();
		}

		final Schedule schedule = (Schedule) getChild(groupPosition, childPosition);
		MyActivity activity = dbHelper.getActivity(schedule.getService_ID());
		String activity_name = activity != null ? activity.getActivity_name()
				: "";
		String date = MyDate.getTimeWithAPMFromUTCTime(schedule.getStarttime())
				+ " to "
				+ MyDate.getTimeWithAPMFromUTCTime(schedule.getEndtime());
		List<Integer> memberids = dbHelper.getParticipantsForSchedule(schedule
				.getSchedule_ID());
		String members = "";
		for (int i = 0; i < memberids.size(); i++) {
			Sharedmember sm = dbHelper.getSharedmember(memberids.get(i),
					schedule.getService_ID());
			if (sm != null) {
				if (i == 0) {
					members = members + sm.getName();
				} else {
					members = members + "|" + sm.getName();
				}
			}
		}

		viewHolder.service_TV.setText(activity_name);
		viewHolder.time_TV.setText(date);
		viewHolder.participants_TV.setText(members);
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent inforActivityIntent = new Intent(context,
						CreateNewScheduleActivity.class);
				inforActivityIntent.putExtra(CommConstant.TYPE,
						DatabaseHelper.EXISTED);
				inforActivityIntent.putExtra(CommConstant.SCHEDULE_ID,
						schedule.getSchedule_ID());
				inforActivityIntent.putExtra(CommConstant.ACTIVITY_ID, schedule.getService_ID());
				context.startActivity(inforActivityIntent);
			}
		});
		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return scheduleCollection.get(listSchedulesByDay.get(groupPosition))
				.size();
	}

	public Object getGroup(int groupPosition) {
		return listSchedulesByDay.get(groupPosition);
	}

	public int getGroupCount() {
		return listSchedulesByDay.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		HeaderViewHolder viewHolder;
		if ((convertView == null)
				|| (convertView.getTag().getClass().getName()
						.contains("ScheduleViewHolder"))) {
			viewHolder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.headercell, null);
			viewHolder.weekday_TV = (TextView) convertView
					.findViewById(R.id.schedule_weekday_tv);
			viewHolder.date_TV = (TextView) convertView
					.findViewById(R.id.schedule_date_tv);

			viewHolder.weekday_TV.setTypeface(Utils.getTypeFace(context));
			viewHolder.date_TV.setTypeface(Utils.getTypeFace(context));
			convertView.setTag(viewHolder);
		} else {
			// Log.i("class name", convertView.getTag().getClass().getName());
			viewHolder = (HeaderViewHolder) convertView.getTag();
		}
		// Schedule schedule=(Schedule) getGroup(groupPosition);
		// String weekday =
		// MyDate.getWeekdayFromUTCTime(listSchedulesByDay.get(groupPosition));
		// String date =
		// MyDate.transformUTCTimeToCustomStyle(this.getHeader(position));
		String date_time_str = listSchedulesByDay.get(groupPosition);
		String[] date_time = date_time_str.split(";");
		if (date_time != null) {
			viewHolder.weekday_TV.setText(date_time[0] != null ? date_time[0]
					: "");
			viewHolder.date_TV
					.setText(date_time[1] != null ? date_time[1] : "");
		}
		return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	static class ScheduleViewHolder {
		TextView service_TV;
		TextView time_TV;
		TextView participants_TV;
	}

	static class HeaderViewHolder {
		TextView weekday_TV;
		TextView date_TV;
	}
}
