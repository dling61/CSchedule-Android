package com.e2wstudy.cschedule.views;

import com.e2wstudy.cschedule.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
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
//	public TextView tv_title;
	public TitleBarView titleBar;
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
		titleBar=(TitleBarView)findViewById(R.id.titleBar);
		titleBar.layout_back.setVisibility(View.GONE);
		titleBar.layout_next.setVisibility(View.GONE);
		titleBar.tv_name.setText(title);
//		tv_title = (TextView) findViewById(R.id.tv_title);
//		tv_title.setText(title);

	}

}