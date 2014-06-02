/**
 * 
 */
package com.e2wstudy.cschedule.views;

import com.e2wstudy.cschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * @author Huyen
 * 
 * */
public class ScheduleView extends RelativeLayout {
	Context context;
//	public ListView list_schedule;
	public ExpandableListView expand_list_schedule;
	
	public ImageButton btn_add_schedule;
	public ImageButton btn_refresh;
	public Button btn_today;
	public Button btn_all;
	public Button btn_me;
	public RelativeLayout layout_no_schedule;
	public RelativeLayout layout_top;
	public ScheduleView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public ScheduleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public ScheduleView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		// mInflater = (LayoutInflater) context
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View.inflate(context, R.layout.schedulepage, this);
//		list_schedule=(ListView)findViewById(R.id.list_schedule);
		expand_list_schedule=(ExpandableListView)findViewById(R.id.list_expandable_schedule);
	
		btn_add_schedule=(ImageButton)findViewById(R.id.btn_add_activity);
		btn_refresh=(ImageButton)findViewById(R.id.btn_refresh);
		btn_today=(Button)findViewById(R.id.btn_today);
		btn_all=(Button)findViewById(R.id.btn_all);
		btn_me=(Button)findViewById(R.id.btn_me);
		layout_no_schedule=(RelativeLayout)findViewById(R.id.layout_no_schedule);
		layout_top=(RelativeLayout)findViewById(R.id.layout_top);
	}
}