package com.e2w.cschedule.adapter;

import com.e2w.cschedule.R;
import com.e2w.cschedule.views.TextItemView;
import com.google.android.gms.internal.ho;
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
		if (convertView == null) {
			holder = new TextItemView(activity);
			convertView = holder;
			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (TextItemView) convertView.getTag();

		}
		if (data[position].startsWith("Remove")) {
			holder.title.setTextColor(activity.getResources().getColor(
					R.color.red_remove));
		} else {
			holder.title.setTextColor(activity.getResources().getColor(
					R.color.textview));
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