package com.dling61.calendarschedule.fragments;

import java.util.ArrayList;

import com.dling61.calendarschedule.CreateNewScheduleActivity;
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
import android.content.Intent;
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
	int type = -1;

	ArrayList<Participant> activityParticipant;

	public void setActivity_id(String activity_id) {
		this.activity_id = activity_id;
	}

	public void setType(int type) {
		this.type = type;
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
		view.layout_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == view.layout_next) {
			// share schedule
			if (type == CommConstant.TYPE_CONTACT) {
				// if (activity_id != null && (!activity_id.equals(""))) {
				// // list participant of this activity
				// ArrayList<Participant> activityParticipant = db
				// .getParticipantsOfActivity(activity_id);
				// for (Participant participant : adapter.participants) {
				//
				// if (participant.isChecked) {
				// // if this activity haven't contain of this
				// // participant=> add this participant for this
				// // activity
				// // else ignore
				// if (!activityParticipant.contains(participant)) {
				// WebservicesHelper ws = new WebservicesHelper(
				// mContext);
				// ws.postSharedmemberToActivity(
				// participant.getID(),
				// CommConstant.ROLE_ASSIGN_MEMBER_SCHEDULE,
				// activity_id);
				// }
				// }
				// }
				// }
				((Activity) mContext).finish();
				Intent intent = new Intent(mContext,
						CreateNewScheduleActivity.class);
				intent.putExtra(CommConstant.TYPE, DatabaseHelper.NEW);
				intent.putExtra(CommConstant.ACTIVITY_ID, activity_id);
				mContext.startActivity(intent);

			} else if (type == CommConstant.TYPE_PARTICIPANT) {
				if (activity_id != null && (!activity_id.equals(""))) {
					// list participant of this activity
					DatabaseHelper dbHelper = DatabaseHelper
							.getSharedDatabaseHelper(mContext);
					ArrayList<Participant> activityParticipant = dbHelper
							.getParticipantsOfActivity(activity_id);
					for (Participant participant : adapter.participants) {

						if (participant.isChecked) {
							// if this activity haven't contain of this
							// participant=> add this participant for this
							// activity
							// else ignore
							if (!activityParticipant.contains(participant)) {
								WebservicesHelper ws = new WebservicesHelper(
										mContext);
								ws.postSharedmemberToActivity(
										participant.getID(),
										CommConstant.ROLE_ASSIGN_MEMBER_SCHEDULE,
										activity_id);
							}
						}
					}
				}
			}
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
			if (type == CommConstant.TYPE_PARTICIPANT) {
				arrParticipant = dbHelper
						.getParticipantsOfActivityWithoutRoleParticipant(activity_id);
				dbHelper.close();
				adapter = new ParticipantAdapter(mContext, arrParticipant,
						true, false);
				view.list_contact.setAdapter(adapter);
				view.list_contact
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, final int position, long id) {
								final Participant participantSelected = adapter.participants
										.get(position);

								// TODO Auto-generated method stub
								participantSelected.isChecked = !participantSelected.isChecked;
								adapter.participants.set(position,
										participantSelected);
								adapter.notifyDataSetChanged();

							}
						});
			} else if (type == CommConstant.TYPE_CONTACT) {
				arrParticipant = dbHelper.getParticipants();
				activityParticipant = dbHelper
						.getParticipantsOfActivity(activity_id);
				adapter = new ParticipantAdapter(mContext, arrParticipant,
						false, false);
				view.list_contact.setAdapter(adapter);
				view.list_contact
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, final int position, long id) {
								final Participant participantSelected = adapter.participants
										.get(position);
								String activity_name = myActivity != null ? myActivity
										.getActivity_name() : " my activity";
								String title = mContext.getResources()
										.getString(R.string.confirm_title)
										+ " "
										+ participantSelected.getName()
										+ " "
										+ mContext.getResources().getString(
												R.string.into) + activity_name;
								final ConfirmDialog confirmDialog = new ConfirmDialog(
										mContext, title);
								confirmDialog.show();
								confirmDialog.btnOk
										.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												// TODO Auto-generated method
												// stub
												// if this activity haven't
												// contain of this
												// participant=> add this
												// participant for this
												// activity
												// else ignore
												if (!activityParticipant
														.contains(participantSelected)) {
													WebservicesHelper ws = new WebservicesHelper(
															mContext);
													ws.postSharedmemberToActivity(
															participantSelected
																	.getID(),
															CommConstant.ROLE_SHARE_MEMBER_ACTIVITY,
															activity_id);
												}
												confirmDialog.dismiss();

											}
										});
								confirmDialog.btnCancel
										.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												confirmDialog.dismiss();
											}
										});

							}
						});

			}

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
