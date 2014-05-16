package com.dling61.calendarschedule;

import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.Utils;

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
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class EditDescriptionActivity extends Activity implements
		OnClickListener {
	String description = "";
	Context mContext;
	EditText ed_description;
	LinearLayout layout_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.setContentView(R.layout.composeactivity);
		mContext = this;
		setContentView(R.layout.edit_description_activity);
		ed_description = (EditText) findViewById(R.id.et_activity_description);
		layout_back=(LinearLayout)findViewById(R.id.layout_back);
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
		layout_back.setOnClickListener(this);
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
		if (v == layout_back) {		
			Utils.hideKeyboard(EditDescriptionActivity.this, ed_description);
			Intent i = getIntent(); // gets the intent that called this intent			
			i.putExtra(CommConstant.ACTIVITY_DESCRIPTION, ed_description
					.getText().toString().trim());
			setResult(222, i);
			finish();
		}

	}
}
