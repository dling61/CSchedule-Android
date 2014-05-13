package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 */
public class PopupDialog extends Dialog {

	public Context mContext;
	public Dialog d;
	public ListView list_item;
	public TextView tv_title;
	String title = "";

	public PopupDialog(Context mContext, String title) {
		super(mContext);
		this.mContext = mContext;
		this.title = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.popup_layout);
		setCanceledOnTouchOutside(true);
		list_item = (ListView) findViewById(R.id.list_item);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(title);

	}

}