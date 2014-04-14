/**
 * 
 */
package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * @author Huyen
 * 
 * */
public class ActivityView extends RelativeLayout {
	Context context;
	public ImageButton btn_add_activity;
	public ListView listview;
	public ActivityView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public ActivityView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public ActivityView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		// mInflater = (LayoutInflater) context
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View.inflate(context, R.layout.activitypage, this);
		listview=(ListView)findViewById(R.id.activityListView);
		btn_add_activity=(ImageButton)findViewById(R.id.btn_add_activity);
	}
}