/**
 * 
 */
package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 * */
public class AddScheduleView extends RelativeLayout {
	Context context;
	public TextView title_tv;
	public EditText et_new_activity_name;
	public View layout;
	public EditText et_start;
	public EditText et_end;
	public EditText et_on_duty;
	public EditText et_new_activity_description;

	public AddScheduleView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public AddScheduleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public AddScheduleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		layout = View.inflate(context, R.layout.add_new_schedule, this);
		title_tv = (TextView) findViewById(R.id.tv_title);
		et_new_activity_name = (EditText) findViewById(R.id.et_new_activity_name);
		et_start = (EditText) findViewById(R.id.et_start);
		et_end = (EditText) findViewById(R.id.et_end);
		et_on_duty = (EditText) findViewById(R.id.et_on_duty);
		et_new_activity_description = (EditText) findViewById(R.id.et_new_activity_description);
	}
}