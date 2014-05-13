/**
 * 
 */
package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * @author Huyen
 * 
 * */
public class ContactView extends RelativeLayout {
	Context context;
	public ListView list_contact;
	public ImageButton btn_add_participant;
	public ImageButton btn_next;
	public ContactView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public ContactView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public ContactView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		View.inflate(context, R.layout.contact_view, this);
		list_contact=(ListView)findViewById(R.id.list_contact);
		btn_add_participant=(ImageButton)findViewById(R.id.btn_add_participant);
		btn_next=(ImageButton)findViewById(R.id.btn_next);
	}
}
