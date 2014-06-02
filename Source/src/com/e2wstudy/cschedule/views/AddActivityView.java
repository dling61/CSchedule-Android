/**
 * 
 */
package com.e2wstudy.cschedule.views;

import com.e2wstudy.cschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 * */
public class AddActivityView extends RelativeLayout {
	Context context;
	public EditText et_new_activity_name;
	public TextView et_new_activity_time_zone;
	public TextView et_new_activity_alert;
	public EditText et_new_activity_repeat;
	public EditText et_new_activity_description;
	public View layout;
	public Button btn_add_paticipant;
	public Button btn_remove_activity;
	public ListView list_participant;
	public TextView tv_participant;
	public TitleBarView titleBar;
	public RelativeLayout layoutAlert;
public RelativeLayout layoutTimeZone;
	public AddActivityView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public AddActivityView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public AddActivityView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		layout = View.inflate(context, R.layout.add_new_activity, this);
		titleBar = (TitleBarView) findViewById(R.id.titleBar);
		et_new_activity_name = (EditText) findViewById(R.id.et_new_activity_name);
		et_new_activity_time_zone = (TextView) findViewById(R.id.et_new_activity_time_zone);
		et_new_activity_alert = (TextView) findViewById(R.id.et_new_activity_alert);
		et_new_activity_repeat = (EditText) findViewById(R.id.et_new_activity_repeat);
		et_new_activity_description = (EditText) findViewById(R.id.et_new_activity_description);
		btn_add_paticipant = (Button) findViewById(R.id.btn_add_paticipant);
		btn_remove_activity = (Button) findViewById(R.id.btn_remove_activity);
		list_participant = (ListView) findViewById(R.id.list_participant);
		tv_participant = (TextView) findViewById(R.id.tv_participant);
		
		titleBar.layout_back.setVisibility(View.VISIBLE);
		layoutAlert=(RelativeLayout)findViewById(R.id.layoutAlert);
		layoutTimeZone=(RelativeLayout)findViewById(R.id.layout_timeZone);
	}
}