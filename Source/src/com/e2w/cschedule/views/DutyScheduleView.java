/**
 * 
 */
package com.e2w.cschedule.views;

import com.e2w.cschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 * */
public class DutyScheduleView extends RelativeLayout {
	Context context;
	public TextView title;
	public RelativeLayout layoutTitle;

	// private LayoutInflater mInflater;
	// RelativeLayout row;

	public DutyScheduleView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public DutyScheduleView(Context context, String text) {
		super(context);
		this.context = context;
		findViewById(context);
		title.setText(text);
	}

	public DutyScheduleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public DutyScheduleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		View.inflate(context, R.layout.duty_schedule_item, this);
		title = (TextView) findViewById(R.id.title);
		layoutTitle = (RelativeLayout) findViewById(R.id.layoutTitle);
		// mInflater = (LayoutInflater) context
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// row = (RelativeLayout) mInflater.inflate(R.layout.duty_schedule_item,
		// null);
		// addView(row);
		// title = (TextView) row.findViewById(R.id.title);
	}
}
