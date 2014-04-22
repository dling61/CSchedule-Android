package com.dling61.calendarschedule.adapter;

import java.util.ArrayList;
import java.util.List;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.MyActivity;
import com.dling61.calendarschedule.views.TextItemView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * @author khoahuyen
 * @class ActivityAdapter
 * {@code create adapter for activity}
 * */
public class ActivityNameAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	public List<MyActivity> mItems = new ArrayList<MyActivity>();
	Context mContext;

	ArrayList<MyActivity> listItem;

	public ActivityNameAdapter(Context context, ArrayList<MyActivity> listItem) {
		// HERE WE CACHE THE INFLATOR FOR EFFICIENCY
		mInflater = LayoutInflater.from(context);
		mContext = context;
		DatabaseHelper db = DatabaseHelper.getSharedDatabaseHelper(mContext);
		this.listItem=listItem;
	}

	public void setItems(List<MyActivity> items) {
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
		TextItemView holder;
		if (convertView==null) {
			holder = new TextItemView(mContext);
			convertView = holder;
			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (TextItemView) convertView.getTag();

		}
		holder.title.setText(listItem.get(position).getActivity_name());

		return holder;
	}

	static class ActivityViewHolder {
		TextView service_name_tv;
		TextView service_time;
		TextView service_desp;
		TextView tv_participant;
	}

}
