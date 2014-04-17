package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 */
public class TabItemView extends RelativeLayout {
	Context context;
	private LayoutInflater mInflater;
	RelativeLayout row;
	public TextView txtNameApp;
	public View right;

	public void setText(String text) {
		txtNameApp.setText(text);
	}

	public TabItemView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public TabItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public TabItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(Context context) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		row = (RelativeLayout) mInflater.inflate(R.layout.tab_item, null);
		addView(row);
		txtNameApp = (TextView) row.findViewById(R.id.txtNameApp);
		right = (View) row.findViewById(R.id.right);
	}
}
