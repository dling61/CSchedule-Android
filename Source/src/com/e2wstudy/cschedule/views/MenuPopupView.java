/**
 * 
 */
package com.e2wstudy.cschedule.views;

import com.e2wstudy.cschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author Huyen
 * 
 * */
public class MenuPopupView extends RelativeLayout {
	Context context;
	public MenuPopupView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public MenuPopupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public MenuPopupView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		View.inflate(context, R.layout.overflow_layout, this);	
	}
}