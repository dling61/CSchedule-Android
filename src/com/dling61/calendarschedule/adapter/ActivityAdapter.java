package com.dling61.calendarschedule.adapter;

import java.util.ArrayList;
import java.util.List;

import com.dling61.calendarschedule.R;
import com.dling61.calendarschedule.models.MyActivity;
import com.dling61.calendarschedule.utils.MyDate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ActivityAdapter extends BaseAdapter 
{
	private LayoutInflater mInflater;
	private List<MyActivity> mItems = new ArrayList<MyActivity>();
	
	public ActivityAdapter(Context context,
		       List<MyActivity> items) {
		           // HERE WE CACHE THE INFLATOR FOR EFFICIENCY
		mInflater = LayoutInflater.from(context);
		mItems = items;
	}
	
	public void setItems (List<MyActivity> items)
	{
		this.mItems = items;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ActivityViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.activitycell,null);
			viewHolder = new ActivityViewHolder();
			viewHolder.service_name_tv = (TextView)convertView.findViewById(R.id.service_name_tv);
			viewHolder.service_time = (TextView)convertView.findViewById(R.id.service_time_tv);
			viewHolder.service_desp = (TextView)convertView.findViewById(R.id.service_descrpition_tv);
			convertView.setTag(viewHolder);
		}
		else
		{
			 viewHolder = (ActivityViewHolder) convertView.getTag();
		}
		
		MyActivity activity = mItems.get(position);
		viewHolder.service_name_tv.setText(activity.getActivity_name());
		String startWeekday = MyDate.getWeekdayFromUTCTime(activity.getStarttime());
		String startDate = MyDate.transformUTCTimeToCustomStyle(activity.getStarttime());
		String startTime = startWeekday + "," + " " + startDate;
		String endWeekday = MyDate.getWeekdayFromUTCTime(activity.getEndtime());
		String endDate = MyDate.transformUTCTimeToCustomStyle(activity.getEndtime());
		String endTime = endWeekday + "," + " " + endDate;
		viewHolder.service_time.setText(startTime + " to " + endTime);
		viewHolder.service_desp.setText(activity.getDesp());
		
		return convertView;
	}
	
	static class ActivityViewHolder {
        TextView service_name_tv;
        TextView service_time;
        TextView service_desp;
    }
	
}
