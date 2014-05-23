package com.dling61.calendarschedule;

import com.dling61.calendarschedule.animation.ExpandCollapseAnimation;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.Utils;
import com.dling61.calendarschedule.views.TitleBarView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

@SuppressLint("NewApi")
public class EditDescriptionActivity extends Activity implements
		OnClickListener {
	String description = "";
	Context mContext;
	EditText ed_description;
	
TitleBarView titleBar;
private boolean mActive = false;
ExpandCollapseAnimation animation = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
		super.onCreate(savedInstanceState);
		// this.setContentView(R.layout.composeactivity);
		mContext = this;
		setContentView(R.layout.edit_description_activity);
		ed_description = (EditText) findViewById(R.id.et_activity_description);
		titleBar=(TitleBarView)findViewById(R.id.titleBar);
		titleBar.tv_name.setText(getResources().getString(R.string.description));
		titleBar.layout_next.setVisibility(View.GONE);
		description = getIntent().getStringExtra(
				CommConstant.ACTIVITY_DESCRIPTION);
		ed_description.setText(description);
		onClickListener();
	}

	BroadcastReceiver deleteActivityComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {

		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onPause() {

		super.onPause();
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * On Click listener
	 * */
	public void onClickListener() {
		titleBar.layout_back.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == titleBar.layout_back) {		
			Utils.hideKeyboard(EditDescriptionActivity.this, ed_description);
			
//			overridePendingTransition( R.anim.slide_in_down, R.anim.slide_out_down );
			Intent i = getIntent(); // gets the intent that called this intent			
			i.putExtra(CommConstant.ACTIVITY_DESCRIPTION, ed_description
					.getText().toString().trim());
			setResult(222, i);
			finish();
		}

	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
//		overridePendingTransition( R.anim.slide_in_down, R.anim.slide_out_down );
	}
}
