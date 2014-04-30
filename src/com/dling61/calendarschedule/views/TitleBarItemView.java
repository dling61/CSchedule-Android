package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 */
public class TitleBarItemView extends RelativeLayout {
	Context context;
	private LayoutInflater mInflater;
	RelativeLayout row;
	public TextView tv_name;
	public LinearLayout layout_back;
	public LinearLayout layout_next;

	public void setData(String text, int img_id) {
	}

	public TitleBarItemView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public TitleBarItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public TitleBarItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(Context context) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		row = (RelativeLayout) mInflater.inflate(R.layout.title_bar, null);
		addView(row);
		tv_name = (TextView) findViewById(R.id.tv_name);
		layout_back = (LinearLayout) findViewById(R.id.layout_back);
		layout_next = (LinearLayout) findViewById(R.id.layout_next);
	}
}
