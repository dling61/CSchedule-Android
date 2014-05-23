package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 */
public class EdittextTitleView extends RelativeLayout {
	Context context;
	private LayoutInflater mInflater;
	RelativeLayout row;
	public TextView title;
	public EditText editText;

	

	public EdittextTitleView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public EdittextTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public EdittextTitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(Context context) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		row = (RelativeLayout) mInflater.inflate(R.layout.edittext, null);
		addView(row);
		title = (TextView) row.findViewById(R.id.title);
		editText=(EditText)findViewById(R.id.editText);
	}
}
