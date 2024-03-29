/**
 * 
 */
package com.e2w.cschedule.views;

import com.e2w.cschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 * */
public class AddScheduleView extends RelativeLayout {
	Context context;

	public EditText et_new_activity_name;
	public View layout;
	public EditText et_startDate;
	public EditText et_endDate;
	public EditText et_on_duty;
	public EditText et_new_activity_description;
	public EditText et_startTime;
	public EditText et_endTime;
	public Button btn_remove_schedule;
	public TitleBarView titleBar;
//	public ListView list_participant;
//	public TextView tv_participant;
//	public Button btn_change_on_duty;
//	public RelativeLayout layout_on_duty;
//	public LinearLayout layout_list_on_duty;

	public TextView et_new_activity_time_zone;
	public TextView et_new_activity_alert;
	public RelativeLayout layoutAlert;
	public RelativeLayout layoutTimeZone;
	
	public Button btnConfirm;
	public Button btnDeny;
	
	public RelativeLayout layout_onduty;
	public TextView tvOnduty;
	
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
		layout = View.inflate(context, R.layout.ns_add_new_schedule, this);

		et_new_activity_name = (EditText) findViewById(R.id.et_new_activity_name);
		et_startDate = (EditText) findViewById(R.id.et_startDate);
		et_endDate = (EditText) findViewById(R.id.et_endDate);
		et_startTime = (EditText) findViewById(R.id.et_startTime);
		et_endTime = (EditText) findViewById(R.id.et_endTime);
		et_on_duty = (EditText) findViewById(R.id.et_on_duty);
		et_new_activity_description = (EditText) findViewById(R.id.et_new_activity_description);
		btn_remove_schedule = (Button) findViewById(R.id.btn_remove_schedule);
		titleBar = (TitleBarView) findViewById(R.id.titleBar);
//		list_participant = (ListView) findViewById(R.id.list_participant);
//		tv_participant = (TextView) findViewById(R.id.tv_participant);
//		btn_change_on_duty = (Button) findViewById(R.id.btn_change_on_duty);
//		layout_on_duty = (RelativeLayout) findViewById(R.id.layout_on_duty);
//		layout_list_on_duty = (LinearLayout) findViewById(R.id.layout_list_on_duty);
		et_new_activity_time_zone = (TextView) findViewById(R.id.et_new_activity_time_zone);
		et_new_activity_alert = (TextView) findViewById(R.id.et_new_activity_alert);
		layoutAlert = (RelativeLayout) findViewById(R.id.layoutAlert);
		layoutTimeZone = (RelativeLayout) findViewById(R.id.layout_timeZone);
		btnConfirm = (Button) findViewById(R.id.btn_confirm);
		btnDeny = (Button) findViewById(R.id.btn_deny);
		layout_onduty=(RelativeLayout)findViewById(R.id.layout_onduty);
		tvOnduty=(TextView)findViewById(R.id.tvOnduty);
	}
}