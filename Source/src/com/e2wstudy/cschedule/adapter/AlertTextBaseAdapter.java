package com.e2wstudy.cschedule.adapter;

import java.util.ArrayList;

import com.e2wstudy.cschedule.models.Alert;
import com.e2wstudy.cschedule.models.TimeZoneModel;
import com.e2wstudy.cschedule.views.TextItemView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author Huyen
 * 
 * */

public class AlertTextBaseAdapter extends BaseAdapter {
	private Context activity;
	private ArrayList<Alert> data;
	public AlertTextBaseAdapter(Context ctx, ArrayList<Alert> data) {
		this.activity = ctx;
		this.data = data;	
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		TextItemView holder;
		//
		if (convertView==null) {
			holder = new TextItemView(activity);
			convertView = holder;
			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (TextItemView) convertView.getTag();

		}
		holder.title.setText(data.get(position).getAname());

		return holder;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}