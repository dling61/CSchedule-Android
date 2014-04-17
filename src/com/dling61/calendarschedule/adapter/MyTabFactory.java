
package com.dling61.calendarschedule.adapter;

/**
 * @author Huyen
 *
 */
import com.dling61.calendarschedule.views.TabItemView;

import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

public class MyTabFactory implements TabContentFactory {

	private final Context mContext;

	public MyTabFactory(Context context) {
		mContext = context;
	}

	public View createTabRightContent(String tag) {
		TabItemView v = new TabItemView(mContext);
		tag = tag.toUpperCase();
		v.setText(tag);
		v.setMinimumWidth(0);
		v.setMinimumHeight(0);
		return v;
	}

	public View createTabContent(String tag) {
		TabItemView v = new TabItemView(mContext);
		tag = tag.toUpperCase();
		v.right.setVisibility(View.GONE);
		v.setText(tag);
		v.setMinimumWidth(0);
		v.setMinimumHeight(0);
		return v;
	}
}