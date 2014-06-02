package com.e2wstudy.cschedule.adapter;

import java.util.ArrayList;
import java.util.List;

import com.e2wstudy.cschedule.R;
import com.e2wstudy.cschedule.db.DatabaseHelper;
import com.e2wstudy.cschedule.models.MyActivity;
import com.e2wstudy.cschedule.models.Schedule;
import com.e2wstudy.cschedule.models.Sharedmember;
import com.e2wstudy.cschedule.utils.MyDate;

import android.app.Service;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter {
	
	static final int HEADER = 0;
	static final int SCHEDULE = 1;
	
	private LayoutInflater mInflater;
	private ArrayList<Schedule> schedules;
	private DatabaseHelper dbHelper;
	
	public ScheduleAdapter (Context context,DatabaseHelper dbHelper, ArrayList<Schedule> schedules)
	{
//		this.inflater = layoutInflater;
		mInflater = LayoutInflater.from(context);
		this.dbHelper = dbHelper;
		this.schedules = schedules;
		
	}
	
	public int getTotalSchedulesNumber()
	{
	return schedules.size();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return schedules.size();
	}
	
	

	@Override
	public Object getItem(int arg0) {
		
		return schedules.get(arg0);
	}
	
	

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
//		
//			HeaderViewHolder viewHolder;
//			if ((convertView == null) || (convertView.getTag().getClass().getName().contains("ScheduleViewHolder")))
//			{
//				viewHolder = new HeaderViewHolder();
//				convertView = inflater.inflate(R.layout.headercell,null);
//				viewHolder.weekday_TV = (TextView) convertView.findViewById(R.id.schedule_weekday_tv);
//				viewHolder.Date_TV = (TextView) convertView.findViewById(R.id.schedule_date_tv);
//				convertView.setTag(viewHolder);
//			}
//			else
//			{
////				Log.i("class name", convertView.getTag().getClass().getName());
//				viewHolder = (HeaderViewHolder) convertView.getTag();
//			}
//			String weekday = MyDate.getWeekdayFromUTCTime(this.getHeader(position));
//			String date = MyDate.transformUTCTimeToCustomStyle(this.getHeader(position));
//			viewHolder.weekday_TV.setText(weekday);
//			viewHolder.Date_TV.setText(date);
//			
//			return convertView;
//		}
//		
//		else
//		{
			ScheduleViewHolder viewHolder;
			if ( (convertView == null))
			{
				viewHolder = new ScheduleViewHolder();
				convertView = mInflater.inflate(R.layout.schedulecell,null);
				viewHolder.service_TV = (TextView) convertView.findViewById(R.id.schedule_servicename_tv);
				viewHolder.time_TV = (TextView) convertView.findViewById(R.id.schedule_date_tv);
				viewHolder.participants_TV = (TextView) convertView.findViewById(R.id.schedule_participants_tv);
				convertView.setTag(viewHolder);
			}
			else
			{
//				Log.i("class name", convertView.getTag().getClass().getName());
				viewHolder = (ScheduleViewHolder) convertView.getTag();
			}
			Schedule schedule =schedules.get(position);
			MyActivity activity = dbHelper.getActivity(schedule.getService_ID());
			String activity_name = activity!=null?activity.getActivity_name():"";
			String date = MyDate.getTimeWithAPMFromUTCTime(schedule.getStarttime()) + " to " + 
					MyDate.getTimeWithAPMFromUTCTime(schedule.getEndtime());
			List<Integer> memberids = dbHelper.getParticipantsForSchedule(schedule.getSchedule_ID());
			String members = "";
			for (int i = 0; i < memberids.size(); i++)
			{
				Sharedmember sm = dbHelper.getSharedmember(memberids.get(i), schedule.getService_ID());
				if (sm != null)
				{
					if (i == 0)
					{
						members = members + sm.getName();
					}
					else
					{
						members = members + "|" + sm.getName();
					}
				}
			}
			
			viewHolder.service_TV.setText(activity_name);
			viewHolder.time_TV.setText(date);
			viewHolder.participants_TV.setText(members);
			return convertView;
//		}
	}
	
	static class ScheduleViewHolder
	{
		TextView service_TV;
		TextView time_TV;
		TextView participants_TV;
	}
	
	static class HeaderViewHolder
	{
		TextView weekday_TV;
		TextView Date_TV;
	}

}
