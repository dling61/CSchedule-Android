/**
 * 
 */
package com.e2wstudy.cschedule.views;

import com.e2wstudy.cschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * @author Huyen
 * 
 * */
public class OnDutyView extends RelativeLayout {
	Context context;
	public ListView ondutyList;
	public View layout;
	public LinearLayout layout_done;
	public OnDutyView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public OnDutyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public OnDutyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		// mInflater = (LayoutInflater) context
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout=View.inflate(context, R.layout.ondutypage, this);
		ondutyList = (ListView)findViewById(R.id.ondutyListView);
		layout_done=(LinearLayout)findViewById(R.id.layout_done);
	}
}