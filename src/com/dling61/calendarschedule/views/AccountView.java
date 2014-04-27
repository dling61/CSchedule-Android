/**
 * 
 */
package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 * */
public class AccountView extends RelativeLayout {
	Context context;
	public TextView account_name_tv;
	public TextView account_email_tv;
	public TextView number_activities_tv;
	public TextView number_schedules_tv;
	public Button btn_about;
	public Button btn_signout_account;
	public AccountView(Context context) {
		super(context);
		this.context = context;
		findViewById(context);
	}

	public AccountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViewById(context);
	}

	public AccountView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		findViewById(context);
	}

	public void findViewById(final Context context) {
		View.inflate(context, R.layout.accountpage, this);
		account_email_tv = (TextView)findViewById(R.id.account_showaccount_tv);
		account_name_tv = (TextView)findViewById(R.id.account_showfullname_tv);
		number_activities_tv = (TextView)findViewById(R.id.account_activity_number_tv);
		number_schedules_tv = (TextView)findViewById(R.id.account_schedule_number_tv);
		btn_about=(Button)findViewById(R.id.btn_about);
		btn_signout_account=(Button)findViewById(R.id.btn_signout_account);
	}
}