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
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 * */
public class AddParticipantView extends RelativeLayout {
	Context context;
	public EditText et_email;
	public EditText et_name;
	public EditText et_mobile;
	public TextView tv_title;
	public RelativeLayout layout;
	public Button btn_next;
	public AddParticipantView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public AddParticipantView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public AddParticipantView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		layout=(RelativeLayout) View.inflate(context, R.layout.composeparticipant, this);
		et_email = (EditText)findViewById(R.id.compose_participant_email_et);
		et_name = (EditText)findViewById(R.id.compose_participant_name_et);
		et_mobile = (EditText)findViewById(R.id.compose_participant_mobile_et);
		tv_title = (TextView)findViewById(R.id.compose_participant_toptitle);
		btn_next=(Button)findViewById(R.id.btn_next);
	}
}