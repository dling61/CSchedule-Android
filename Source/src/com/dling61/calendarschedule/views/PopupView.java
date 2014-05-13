/**
 * 
 */
package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Huyen
 * @description show popup like timezone, alert,...
 * */
public class PopupView extends RelativeLayout {
	Context context;
	public ListView list_item;
	public TextView tv_title;
	public PopupView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public PopupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public PopupView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		View.inflate(context, R.layout.popup_layout, this);	
		list_item=(ListView)findViewById(R.id.list_item);
		tv_title=(TextView)findViewById(R.id.tv_title);
	}
}