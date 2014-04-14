package com.dling61.calendarschedule;

import com.dling61.calendarschedule.views.MenuPopupView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

public class HomeActivity extends Activity {
	Context mContext;
	Button menu;
	FrameLayout content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);
		mContext = this;
		menu = (Button) findViewById(R.id.menu);

		menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindowDogs().showAsDropDown(v, -5, 0);
			}
		});
	}

	public PopupWindow popupWindowDogs() {
		// initialize a pop up window type
		PopupWindow popupWindow = new PopupWindow(this);
		// some other visual settings
		popupWindow.setFocusable(true);
		popupWindow.setWidth(250);
		popupWindow.setHeight(WindowManager.LayoutParams.FILL_PARENT);

		// set the list view as pop up window content
		popupWindow.setContentView(new MenuPopupView(mContext));

		return popupWindow;
	}
}
