package com.dling61.calendarschedule.fragments;

import java.util.ArrayList;

import com.dling61.calendarschedule.adapter.ParticipantAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.views.ContactView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		initData();

	}
	private void initData() {
		WebservicesHelper ws = new WebservicesHelper(mContext);
		ws.getParticipantsFromWeb();
	}

	public static ContactFragment getInstance() {
		return ContactFragment.getInstance();
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
		View view = new ContactView(getActivity());
		this.view = (ContactView) view;
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		mContext.registerReceiver(activityDownloadComplete, new IntentFilter(
				CommConstant.PARTICIPANT_READY));
	}

	BroadcastReceiver activityDownloadComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			Log.i("broadcast", "activities are ready");
			ArrayList<Participant> activities = dbHelper.getParticipants();
			dbHelper.close();
			ParticipantAdapter adapter = new ParticipantAdapter(mContext, activities);
			view.list_contact.setAdapter(adapter);

		}
	};

	@Override
	public void onPause() {
		super.onPause();
		mContext.unregisterReceiver(activityDownloadComplete);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
