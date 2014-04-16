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

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
 * @author Huyen return account information by token
 * 
 */
public class ActivityFragment extends Fragment implements OnClickListener {
	ActivityView view;
	Context mContext;
	ArrayList<MyActivity> activities;
	ActivityAdapter adapter;
	DatabaseHelper dbHelper; 
			
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		dbHelper=DatabaseHelper.getSharedDatabaseHelper(mContext);
//		initData();
		onClickListener();
	}

//	private void initData() {
//		dbHelper= DatabaseHelper
//				.getSharedDatabaseHelper(mContext);
//		WebservicesHelper ws = new WebservicesHelper(mContext);
//		ws.getAllActivitys(mContext);
//	}

	public static ActivityFragment getInstance() {
		return ActivityFragment.getInstance();
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
	public void onResume() {
		super.onResume();
		// mContext.registerReceiver(activityDownloadComplete, new IntentFilter(
		// CommConstant.ACTIVITY_DOWNLOAD_SUCCESS));
		
		Log.i("get activity", "activities");
		activities = dbHelper.getActivities();
		dbHelper.close();
		adapter = new ActivityAdapter(mContext, activities);
		view.listview.setAdapter(adapter);
	}

	BroadcastReceiver activityDownloadComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			Log.i("broadcast", "activities are ready");
			ArrayList<MyActivity> activities = dbHelper.getActivities();
			dbHelper.close();
			ActivityAdapter adapter = new ActivityAdapter(mContext, activities);
			view.listview.setAdapter(adapter);

		}
	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater =getActivity().getMenuInflater();
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
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final int position = info.position;
		switch (item.getItemId()) {
			case R.id.edit_activity:
			{
				MyActivity selectedActivity = activities.get(position);
				Intent newIntent = new Intent(mContext,AddNewActivity.class);
				newIntent.putExtra(CommConstant.TYPE, DatabaseHelper.EXISTED);
				newIntent.putExtra(CommConstant.ACTIVITY_ID, selectedActivity.getActivity_ID());
				this.startActivityForResult(newIntent, 3);
				break;
			}			
			
			case R.id.mail_activity:
			{
				MyActivity ma = this.activities.get(position);
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL  , new String[]{});
				i.putExtra(Intent.EXTRA_SUBJECT, ma.getActivity_name());
				i.putExtra(Intent.EXTRA_TEXT   , ma.getDesp() + "\n\n" + "Download form\n" + "www.androidapps.com/CSchedule\n" + "to check more" );
				try {
				    startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			
			case R.id.share_activity:
			{
				MyActivity ma = this.activities.get(position);
//				Intent newIntent = new Intent(mContext,S.class);
//				newIntent.putExtra("type", DatabaseHelper.EXISTED);
//				System.out.println("___Share ID "+ma.get_ID());
//				newIntent.putExtra("serviceid", ma.get_ID());
//				this.startActivityForResult(newIntent, 10);
				break;
			}
			
			case R.id.delete_activity:
			{
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		        alertDialog.setTitle("Caution");
		        alertDialog.setMessage("Are you sure you want delete this activity and related schedules?");
		        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
//		            Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
		            	MyActivity ma = activities.get(position);
		            	activities.remove(position);
		            	adapter.setItems(activities);	  
		            	adapter.notifyDataSetChanged();
		            	ContentValues cv = new ContentValues();
		            	cv.put(ActivityTable.is_Deleted, 1);
		            	cv.put(ActivityTable.is_Synchronized, 0);
		            	dbHelper.updateActivity(ma.getActivity_ID(), cv);
		            	List<Schedule> sbelongtoa = dbHelper.getSchedulesBelongtoActivity(ma.getActivity_ID());
		            	for (int i = 0; i < sbelongtoa.size(); i++)
		            	{
		            		ContentValues scv = new ContentValues();
			            	scv.put(ScheduleTable.is_Deleted, 1);
			            	scv.put(ScheduleTable.is_Synchronized, 0);
			            	int schedule_id = sbelongtoa.get(i).getSchedule_ID();
			            	dbHelper.updateSchedule(schedule_id,scv);
			            	List<Integer> onduties = dbHelper.getOndutyRecordsForSchedule(schedule_id);
			            	for (int j = 0 ; j < onduties.size(); j++)
			            	{
			            		ContentValues ocv = new ContentValues();
				            	ocv.put(OndutyTable.is_Deleted, 1);
				            	ocv.put(OndutyTable.is_Synchronized, 0);
				            	int onduty_id = onduties.get(j);
				            	dbHelper.updateSchedule(onduty_id,ocv);
			            	}
		            	}
		            	
		            	WebservicesHelper ws=new WebservicesHelper(mContext);
		            	ws.deleteActivity(ma);
		            }
		        });
		        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            Toast.makeText(mContext, "You clicked on NO", Toast.LENGTH_SHORT).show();
		            dialog.cancel();
		            }
		        });
		        alertDialog.show();
		        break;
			}
			
		}
		return false;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// mContext.unregisterReceiver(activityDownloadComplete);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
