package com.dling61.calendarschedule.fragments;

import java.util.ArrayList;

import com.dling61.calendarschedule.AddNewContactActivity;
import com.dling61.calendarschedule.CreateNewScheduleActivity;
import com.dling61.calendarschedule.adapter.ActivityAdapter;
import com.dling61.calendarschedule.adapter.ParticipantAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.MyActivity;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.views.ContactView;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

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
	MyActivity myActivity = null;

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
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		IntentFilter filterRefreshUpdate = new IntentFilter();
		filterRefreshUpdate.addAction(CommConstant.DELETE_CONTACT_COMPLETE);
		filterRefreshUpdate.addAction(CommConstant.PARTICIPANT_READY);
		filterRefreshUpdate.addAction(CommConstant.ADD_CONTACT_SUCCESS);
		getActivity().registerReceiver(contactDownloadComplete, filterRefreshUpdate);
	}
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		getActivity().unregisterReceiver(contactDownloadComplete);
	}

	private void initData() {
		if (tab) {
			view.btn_add_participant.setVisibility(View.VISIBLE);
			view.btn_next.setVisibility(View.GONE);
		} else {
			view.btn_add_participant.setVisibility(View.GONE);
			view.btn_next.setVisibility(View.VISIBLE);
		}
		DatabaseHelper db = DatabaseHelper.getSharedDatabaseHelper(mContext);
		if (activity_id != null && (!activity_id.equals(""))) {
			myActivity = db.getActivity(activity_id);
		}
		// WebservicesHelper ws = new WebservicesHelper(mContext);
		// ws.getParticipantsFromWeb();
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
			Intent intent = new Intent(mContext, AddNewContactActivity.class);
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
				Intent intent = new Intent(mContext,
						CreateNewScheduleActivity.class);
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
	BroadcastReceiver contactDownloadComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			ArrayList<Participant> participants = dbHelper.getParticipants();
			adapter = new ParticipantAdapter(mContext, participants, tab ? false
					: true, true);
			view.list_contact.setAdapter(adapter);
			view.list_contact.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						final int position, long id) {
					final Participant participantSelected = adapter.participants
							.get(position);

					Intent inforActivityIntent = new Intent(mContext,
							AddNewContactActivity.class);
					inforActivityIntent.putExtra(CommConstant.TYPE,
							DatabaseHelper.EXISTED);
					inforActivityIntent.putExtra(CommConstant.CONTACT_ID,
							participantSelected.getID());
					mContext.startActivity(inforActivityIntent);

				}
			});
		}
	};
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
//		 mContext.unregisterReceiver(contactDownloadComplete);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
}
