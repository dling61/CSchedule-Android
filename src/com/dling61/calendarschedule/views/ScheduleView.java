/**
 * 
 */
package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * @author Huyen
 * 
 * */
public class ScheduleView extends RelativeLayout {
	Context context;
//	public ListView list_schedule;
	public ExpandableListView expand_list_schedule;
	public ScheduleView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public ScheduleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public ScheduleView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		// mInflater = (LayoutInflater) context
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View.inflate(context, R.layout.schedulepage, this);
//		list_schedule=(ListView)findViewById(R.id.list_schedule);
		expand_list_schedule=(ExpandableListView)findViewById(R.id.list_expandable_schedule);
	}
}