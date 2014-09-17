/**
 * 
 */
package com.e2w.cschedule.views;

import com.e2w.cschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 */
public class TitleBarView extends RelativeLayout {
	Context context;
	public LinearLayout layout_back;
	public LinearLayout layout_next;
	public LinearLayout layout_save;
	public ImageView img_next;
	public ImageView img_back;
	public TextView tv_name;
	public LinearLayout layout_edit;
	public TitleBarView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public TitleBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public TitleBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(Context context) {
		View.inflate(context, R.layout.title_bar, this);
		layout_back = (LinearLayout) findViewById(R.id.layout_back);
		layout_next = (LinearLayout) findViewById(R.id.layout_next);
		img_next = (ImageView) findViewById(R.id.btn_next);
		img_back = (ImageView) findViewById(R.id.btn_back);
		tv_name = (TextView) findViewById(R.id.tv_name);
		layout_save=(LinearLayout)findViewById(R.id.layout_save);
		layout_edit=(LinearLayout)findViewById(R.id.layout_edit);
	}
}
