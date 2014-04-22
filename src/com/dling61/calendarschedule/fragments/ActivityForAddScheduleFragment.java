package com.dling61.calendarschedule.fragments;

import java.util.ArrayList;
import java.util.List;

import com.dling61.calendarschedule.AddNewActivity;
import com.dling61.calendarschedule.R;
import com.dling61.calendarschedule.adapter.ActivityAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.ActivityTable;
import com.dling61.calendarschedule.models.MyActivity;
import com.dling61.calendarschedule.models.OndutyTable;
import com.dling61.calendarschedule.models.Schedule;
import com.dling61.calendarschedule.models.ScheduleTable;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.views.ActivityView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * @author Huyen
 * 
 */
public class ActivityForAddScheduleFragment extends Fragment implements OnClickListener {
	ActivityView view;
	Context mContext;
	ArrayList<MyActivity> activities;
	ActivityAdapter adapter;
	DatabaseHelper dbHelper;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		dbHelper = DatabaseHelper.getSharedDatabaseHelper(mContext);
		// initData();
		onClickListener();
	}
	
	public static ActivityForAddScheduleFragment getInstance() {
		return ActivityForAddScheduleFragment.getInstance();
	}

	private void onClickListener() {
		registerForContextMenu(view.listview);
		view.btn_add_activity.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == view.btn_add_activity) {
			Intent intent = new Intent(mContext, AddNewActivity.class);
			intent.putExtra("type", DatabaseHelper.NEW);
			mContext.startActivity(intent);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = new ActivityView(getActivity());
		this.view = (ActivityView) view;
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	BroadcastReceiver activityDownloadComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			Log.i("broadcast", "activities are ready");
			ArrayList<MyActivity> activities = dbHelper.getActivities();
			
			ActivityAdapter activityAdapter = new ActivityAdapter(mContext,
					activities);
			view.listview.setAdapter(activityAdapter);
		
		}
	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int position = info.position;
		if (v == view.listview) {
			if (activities != null && activities.size() > 0) {
				MyActivity ma = this.activities.get(position);
				int role = ma.getRole();
				switch (role) {
				case 0:
					inflater.inflate(R.menu.activitymenu, menu);
					break;
				case 1:
					inflater.inflate(R.menu.activityorgmenu, menu);
					break;
				case 2:
					inflater.inflate(R.menu.activityparmenu, menu);
					break;
				case 3:
					break;
				}
			}
		}
	}


	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		IntentFilter filterRefreshUpdate = new IntentFilter();
		filterRefreshUpdate.addAction(CommConstant.DELETE_ACTIVITY_COMPLETE);
		filterRefreshUpdate.addAction(CommConstant.ACTIVITY_DOWNLOAD_SUCCESS);
		filterRefreshUpdate.addAction(CommConstant.UPDATE_SCHEDULE);
		filterRefreshUpdate
				.addAction(CommConstant.GET_SHARED_MEMBER_ACTIVITY_COMPLETE);
		filterRefreshUpdate
				.addAction(CommConstant.ADD_SHARED_MEMBER_FROM_ACTIVITY);
		getActivity().registerReceiver(activityDownloadComplete,
				filterRefreshUpdate);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		getActivity().unregisterReceiver(activityDownloadComplete);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
