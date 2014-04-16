package com.dling61.calendarschedule.fragments;

import java.util.ArrayList;
import com.dling61.calendarschedule.adapter.ScheduleAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.Schedule;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.views.ScheduleView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * @author Huyen return account information by token
 * 
 */
public class ScheduleFragment extends Fragment implements OnClickListener {
	ScheduleView view;
	Context mContext;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
//		initData();

	}
//	private void initData() {
//		WebservicesHelper ws = new WebservicesHelper(mContext);
//		ws.getAllActivitys(mContext);
//	}

	public static ScheduleFragment getInstance() {
		return ScheduleFragment.getInstance();
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = new ScheduleView(getActivity());
		this.view = (ScheduleView) view;
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		mContext.registerReceiver(scheduleReadyComplete, new IntentFilter(
				CommConstant.SCHEDULE_READY));
	}

	BroadcastReceiver scheduleReadyComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			ArrayList<Schedule> schedules = dbHelper.getAllSchedules();
			dbHelper.close();
			ScheduleAdapter adapter = new ScheduleAdapter(mContext, dbHelper,
					schedules);
			view.list_schedule.setAdapter(adapter);

		}
	};

	@Override
	public void onPause() {
		super.onPause();
		mContext.unregisterReceiver(scheduleReadyComplete);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
