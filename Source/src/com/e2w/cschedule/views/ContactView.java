/**
 * 
 */
package com.e2w.cschedule.views;

import com.e2w.cschedule.R;

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

	public RelativeLayout layout_no_contact;
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
	
		layout_no_contact=(RelativeLayout)findViewById(R.id.layout_no_contact);
	}
}
