/**
 * 
 */
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
 * */
public class TextItemView extends RelativeLayout {
	Context context;
	public TextView title;
//	private LayoutInflater mInflater;
//	RelativeLayout row;
	
	public TextItemView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public TextItemView(Context context, String text) {
		super(context);
		this.context = context;
		findViewById(context);
		title.setText(text);
	}

	public TextItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public TextItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
//		mInflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		row = (RelativeLayout) mInflater.inflate(R.layout.textview_item, null);
//		addView(row);
//		title = (TextView) row.findViewById(R.id.title);
		
		
		View.inflate(context, R.layout.textview_item, this);
		title = (TextView) findViewById(R.id.title);
	}
}
