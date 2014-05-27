package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 */
public class ToastDialog extends Dialog {

	public Context mContext;
	public Dialog d;
	public Button btnOk;

	String title = "";
	public TextView tvTitle;

	public ToastDialog(Context mContext, String title) {
		super(mContext);
		this.mContext = mContext;
		this.title = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.toast);
		setCanceledOnTouchOutside(true);
		btnOk = (Button) findViewById(R.id.btn_ok);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(title);
	}

}