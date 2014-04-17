/**
 * 
 */
package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;

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
public class ParticipantView extends RelativeLayout {
	Context context;
	public ListView list_contact;
	public LinearLayout layout_next;
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
		layout_next=(LinearLayout)findViewById(R.id.layout_next);
	}
}
