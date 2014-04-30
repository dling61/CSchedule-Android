/**
 * 
 */
package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
	public EditText et_startDate;
	public EditText et_endDate;
	public EditText et_on_duty;
	public EditText et_new_activity_description;
	public EditText et_startTime;
	public EditText et_endTime;
	public LinearLayout layout_next;
	public LinearLayout layout_back;
	public LinearLayout layout_save;
	public Button btn_remove_schedule;

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
		et_startDate = (EditText) findViewById(R.id.et_startDate);
		et_endDate = (EditText) findViewById(R.id.et_endDate);
		et_startTime = (EditText) findViewById(R.id.et_startTime);
		et_endTime = (EditText) findViewById(R.id.et_endTime);
		et_on_duty = (EditText) findViewById(R.id.et_on_duty);
		et_new_activity_description = (EditText) findViewById(R.id.et_new_activity_description);
		layout_next=(LinearLayout)findViewById(R.id.layout_next);
		layout_back=(LinearLayout)findViewById(R.id.layout_back);
		layout_save=(LinearLayout)findViewById(R.id.layout_save);
		btn_remove_schedule=(Button)findViewById(R.id.btn_remove_schedule);
	}
}