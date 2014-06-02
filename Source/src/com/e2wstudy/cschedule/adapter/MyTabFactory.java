package com.e2wstudy.cschedule.adapter;

/**
 * @author Huyen
 *
 */
import com.e2wstudy.cschedule.views.TabItemView;

import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

public class MyTabFactory implements TabContentFactory {

	private final Context mContext;
	int imageId;

	public MyTabFactory(Context context, int imageId) {
		mContext = context;
		this.imageId = imageId;
	}

	public View createTabRightContent(String tag, int imageId) {
		TabItemView v = new TabItemView(mContext);
//		tag = tag.toUpperCase();
		v.setData(tag, imageId);
		v.setMinimumWidth(0);
		v.setMinimumHeight(0);
		return v;
	}

	public View createTabContent(String tag, int imageId) {
		TabItemView v = new TabItemView(mContext);
//		tag = tag.toUpperCase();
//		v.right.setVisibility(View.GONE);
		v.setData(tag, imageId);
		v.setMinimumWidth(0);
		v.setMinimumHeight(0);
		return v;
	}

	@Override
	public View createTabContent(String tag) {
		// TODO Auto-generated method stub
		return createTabRightContent(tag, imageId);
	
	}
}