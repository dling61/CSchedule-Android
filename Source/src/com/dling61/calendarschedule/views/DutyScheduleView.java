/**
 * 
 */
package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;

import android.content.Context;
import android.util.AttributeSet;
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
	}
}
