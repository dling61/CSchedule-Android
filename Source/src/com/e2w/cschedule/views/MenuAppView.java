
package com.e2w.cschedule.views;

import com.e2w.cschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

/**
 * @author Huyen
 * 
 */
public class MenuAppView extends RelativeLayout implements OnClickListener {
	Context context;

	public RelativeLayout layoutMenu;

	boolean isClickMenu = false;
	public TextView tvTitle;
public ImageButton btnAdd;
	public MenuAppView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public MenuAppView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public MenuAppView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(Context context) {
		View.inflate(context, R.layout.menu_app_view, this);
		layoutMenu = (RelativeLayout) findViewById(R.id.layout_menu);		
		tvTitle = (TextView) findViewById(R.id.tv_title);
		btnAdd=(ImageButton)findViewById(R.id.btn_add);
		layoutMenu.setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		

	}
}
