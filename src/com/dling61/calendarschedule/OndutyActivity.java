package com.dling61.calendarschedule;

import java.util.ArrayList;
import java.util.List;

import com.dling61.calendarschedule.adapter.OndutyAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.Schedule;
import com.dling61.calendarschedule.models.Sharedmember;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.views.OnDutyView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * @author khoahuyen
 * */
public class OndutyActivity extends Activity implements OnClickListener {

	private DatabaseHelper dbHelper;
	private List<Sharedmember> sharedmembers;
	private List<Integer> pins;

	private String activity_id;
	private int schedule_id;
	private OndutyAdapter oa;
	OnDutyView view;

	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		view = new OnDutyView(mContext);
		setContentView(view.layout);

		dbHelper = DatabaseHelper.getSharedDatabaseHelper(this);
		sharedmembers = new ArrayList<Sharedmember>();

		Intent myIntent = this.getIntent();
		schedule_id = myIntent.getIntExtra(CommConstant.SCHEDULE_ID, -1);
		activity_id = myIntent.getStringExtra(CommConstant.ACTIVITY_ID);
		pins = myIntent.getIntegerArrayListExtra("pins");

		new GetSharedMemberTask().execute();
		view.layout_done.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == view.layout_done) {
			if (sharedmembers != null && sharedmembers.size() > 0) {
				ArrayList<Integer> listMember = new ArrayList<Integer>();
				for (Sharedmember member : sharedmembers) {
					if (member.isChecked) {
						// list_member_on_duty+=member.getID()+",";
						listMember.add(member.getID());
					}
				}

				if (listMember != null && listMember.size() > 0) {

					Schedule schedule = dbHelper
							.getScheduleSortedByID(schedule_id);

					// post on duty
					WebservicesHelper ws = new WebservicesHelper(mContext);
					if (schedule_id > 0) {
						ws.updateSchedule(schedule, listMember);
					} else {
						ws.addSchedule(schedule, listMember);
					}
				} else {
					// show Toast
					Toast.makeText(
							mContext,
							"You haven't select any member to assign \"On Duty\" ",
							Toast.LENGTH_LONG).show();
				}
			}

		}
	}

	private class GetSharedMemberTask extends AsyncTask<String, Void, String> {
		private ProgressDialog mDialog;

		@Override
		protected String doInBackground(String... params) {
			DatabaseHelper db = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			sharedmembers = db.getSharedMemberForActivity(activity_id + "");
			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {
			// show on UI
			oa = new OndutyAdapter(mContext, sharedmembers, pins);
			view.ondutyList.setAdapter(oa);
			// view.ondutyList.setOnItemClickListener(OndutyActivity.this);
			mDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			mDialog = new ProgressDialog(mContext);
			mDialog.setMessage("Please wait");
			mDialog.setCancelable(false);

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		// unregisterReceiver(receiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void itemSelected(View v) {
		ImageButton imgbtn = (ImageButton) v;
		int tag = (Integer) v.getTag();
		if (pins.contains(tag)) {
			imgbtn.setImageResource(R.drawable.unchecked);
			pins.remove((Integer) tag);
		} else {
			imgbtn.setImageResource(R.drawable.checked);
			pins.add((Integer) tag);
		}
	}

}
