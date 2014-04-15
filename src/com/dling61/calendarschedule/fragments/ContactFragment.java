package com.dling61.calendarschedule.fragments;

import java.util.ArrayList;

import com.dling61.calendarschedule.AddNewParticipantActivity;
import com.dling61.calendarschedule.CreateNewScheduleActivity;
import com.dling61.calendarschedule.adapter.ParticipantAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.views.ContactView;

import android.content.Context;
import android.content.Intent;
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
public class ContactFragment extends Fragment implements OnClickListener {
	ContactView view;
	Context mContext;
	boolean tab = false;// if fragment inside tab
	ParticipantAdapter adapter;

	String activity_id = "";

	public void setActivity_id(String activity_id) {
		this.activity_id = activity_id;
	}

	public void setInSideTab(boolean tab) {
		this.tab = tab;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();

		initData();

		onClickListener();
	}

	private void initData() {
		if (tab) {
			view.btn_add_participant.setVisibility(View.VISIBLE);
			view.btn_next.setVisibility(View.GONE);
		} else {
			view.btn_add_participant.setVisibility(View.GONE);
			view.btn_next.setVisibility(View.VISIBLE);
		}
		WebservicesHelper ws = new WebservicesHelper(mContext);
		ws.getParticipantsFromWeb();
	}

	public static ContactFragment getInstance() {
		return ContactFragment.getInstance();
	}

	private void onClickListener() {
		view.btn_add_participant.setOnClickListener(this);
		view.btn_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == view.btn_add_participant) {
			Intent intent = new Intent(mContext, AddNewParticipantActivity.class);
			intent.putExtra("type", DatabaseHelper.NEW);
			mContext.startActivity(intent);
		}
		/**
		 * When clicking on “next”, call API to create shared members for this
		 * activity in the server
		 */
		else if (v == view.btn_next) {
			// get list participant checked, share with viewer role
			if (adapter != null && adapter.participants != null) {

				// ArrayList<Participant> listParticipantSelected=new
				// ArrayList<Participant>();
				WebservicesHelper ws = new WebservicesHelper(mContext);
				for (Participant participant : adapter.participants) {
					if (participant.isChecked) {
						ws.postSharedmemberToActivity(participant.getID(),
								CommConstant.VIEWER, activity_id);
					}

				}
				Intent intent=new Intent(mContext,CreateNewScheduleActivity.class);
				intent.putExtra(CommConstant.TYPE, DatabaseHelper.NEW);
				intent.putExtra(CommConstant.ACTIVITY_ID, activity_id);
				mContext.startActivity(intent);
			}
			// ws.alterSharedmemberToActivity(memberid, role, activityid);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = new ContactView(getActivity());
		this.view = (ContactView) view;
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// mContext.registerReceiver(activityDownloadComplete, new IntentFilter(
		// CommConstant.PARTICIPANT_READY));
		DatabaseHelper dbHelper = DatabaseHelper
				.getSharedDatabaseHelper(mContext);
		ArrayList<Participant> activities = dbHelper.getParticipants();
		dbHelper.close();

		adapter = new ParticipantAdapter(mContext, activities, tab ? false
				: true);
		view.list_contact.setAdapter(adapter);
	}

	// BroadcastReceiver activityDownloadComplete = new BroadcastReceiver() {
	// public void onReceive(Context arg0, Intent arg1) {
	// DatabaseHelper dbHelper = DatabaseHelper
	// .getSharedDatabaseHelper(mContext);
	// Log.i("broadcast", "activities are ready");
	// ArrayList<Participant> activities = dbHelper.getParticipants();
	// dbHelper.close();
	// ParticipantAdapter adapter = new ParticipantAdapter(mContext,
	// activities);
	// view.list_contact.setAdapter(adapter);
	//
	// }
	// };

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
