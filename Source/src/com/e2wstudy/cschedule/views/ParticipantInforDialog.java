package com.e2wstudy.cschedule.views;

import com.e2wstudy.cschedule.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author Huyen
 * 
 */
public class ParticipantInforDialog extends Dialog {

	public Context mContext;
	public Dialog d;
	public ListView list_item;
	public Button btn_cancel;

	public ParticipantInforDialog(Context mContext) {
		super(mContext);
		this.mContext = mContext;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.participant_infor_dialog);
		setCanceledOnTouchOutside(true);
		list_item = (ListView) findViewById(R.id.list_item);
		btn_cancel=(Button)findViewById(R.id.btn_cancel);

	}

}