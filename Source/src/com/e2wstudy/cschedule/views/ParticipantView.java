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
import android.widget.TextView;

/**
 * @author Huyen
 * 
 * */
public class ParticipantView extends RelativeLayout {
	Context context;
	public ListView list_contact;
	public TitleBarView titleBar;
	public ParticipantView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public ParticipantView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public ParticipantView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		View.inflate(context, R.layout.participant_view, this);
		list_contact=(ListView)findViewById(R.id.list_contact);
		titleBar=(TitleBarView)findViewById(R.id.titleBar);
		titleBar.layout_back.setVisibility(View.GONE);
	}
}
