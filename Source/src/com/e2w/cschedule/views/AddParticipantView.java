package com.e2w.cschedule.views;

import com.e2w.cschedule.R;

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
public class AddParticipantView extends RelativeLayout {
	Context context;
	public EditText et_email;
	public EditText et_name;
	public EditText et_mobile;
//	public TextView tv_title;
	public RelativeLayout layout;
//	public LinearLayout layout_back;
//	public LinearLayout layout_done;
	public Button btn_remove_contact;
//	public LinearLayout layout_edit;
	
	public TitleBarView titleBar;
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
		layout=(RelativeLayout) View.inflate(context, R.layout.add_new_participant, this);
		titleBar=(TitleBarView)findViewById(R.id.titleBar);
		
		et_email = (EditText)findViewById(R.id.compose_participant_email_et);
		et_name = (EditText)findViewById(R.id.compose_participant_name_et);
		et_mobile = (EditText)findViewById(R.id.compose_participant_mobile_et);
		
		titleBar.tv_name.setText(getResources().getString(R.string.contact));
		
//		tv_title = (TextView)findViewById(R.id.compose_participant_toptitle);
//		layout_back=(LinearLayout)findViewById(R.id.layout_back);
//		layout_done=(LinearLayout)findViewById(R.id.layout_done);
//		layout_edit=(LinearLayout)findViewById(R.id.layout_edit);
		btn_remove_contact=(Button)findViewById(R.id.btm_remove_contact);
		titleBar.img_next.setImageResource(R.drawable.edit);
	}
}