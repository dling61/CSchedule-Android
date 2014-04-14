package com.dling61.calendarschedule.adapter;

import com.dling61.calendarschedule.views.TextItemView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author Huyen
 * 
 * */

public class TextViewBaseAdapter extends BaseAdapter {
	private Context activity;
	private String[] data;
	public TextViewBaseAdapter(Context ctx, String[] data) {
		this.activity = ctx;
		this.data = data;	
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		TextItemView holder;
		//
		if (null == convertView) {
			holder = new TextItemView(activity);
			convertView = holder;
			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (TextItemView) convertView.getTag();

		}
		holder.title.setText(data[position]);

		return holder;
	}

	@Override
	public int getCount() {
		return data.length;
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