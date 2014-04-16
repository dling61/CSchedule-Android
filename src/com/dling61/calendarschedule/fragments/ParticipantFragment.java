package com.dling61.calendarschedule.fragments;

import java.util.ArrayList;

import com.dling61.calendarschedule.R;
import com.dling61.calendarschedule.adapter.ParticipantAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.MyActivity;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.views.ConfirmDialog;
import com.dling61.calendarschedule.views.ParticipantView;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Huyen {@This page looks like “Contact” page. But it’s
 *         different. It shows all participants with role name attached and has
 *         a select button for each participant. The role “participant” is Not
 *         displayed. A user can assign some participants for a schedule. We
 *         call it “On Duty” *}
 * 
 */
public class ParticipantFragment extends Fragment implements OnClickListener {
	Context mContext;
	ParticipantAdapter adapter;
	String activity_id = "";
	MyActivity myActivity = null;
	ParticipantView view;
	ArrayList<Participant> arrParticipant;

	public void setActivity_id(String activity_id) {
		this.activity_id = activity_id;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();

		initData();

		onClickListener();
	}

	private void initData() {

		DatabaseHelper db = DatabaseHelper.getSharedDatabaseHelper(mContext);
		if (activity_id != null && (!activity_id.equals(""))) {
			myActivity = db.getActivity(activity_id);
		}
		// WebservicesHelper ws = new WebservicesHelper(mContext);
		// ws.getParticipantsFromWeb();
	}

	public static ParticipantFragment getInstance() {
		return ParticipantFragment.getInstance();
	}

	private void onClickListener() {
		view.btn_done.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == view.btn_done) {
			// share schedule
			if (arrParticipant != null && arrParticipant.size() > 0
					&& activity_id != null && (!activity_id.equals(""))) {
				for (Participant participant : arrParticipant) {
					if (participant.isChecked) {
						WebservicesHelper ws = new WebservicesHelper(mContext);
						ws.postSharedmemberToActivity(participant.getID(),
								CommConstant.ROLE_ASSIGN_MEMBER_SCHEDULE,
								activity_id);
					}
				}
			}
			((Activity) mContext).finish();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = new ParticipantView(getActivity());
		this.view = (ParticipantView) view;
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (activity_id != null && (!activity_id.equals(""))) {
			DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			arrParticipant = dbHelper.getParticipantsOfActivity(activity_id);
			dbHelper.close();
			adapter = new ParticipantAdapter(mContext, arrParticipant, true,
					true);
			view.list_contact.setAdapter(adapter);
			view.list_contact.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						final int position, long id) {
					final Participant participantSelected = adapter.participants
							.get(position);
					participantSelected.isChecked = !participantSelected.isChecked;
					arrParticipant.add(position, participantSelected);

				}
			});
		}
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
