package com.e2w.cschedule.fragments;

import java.util.ArrayList;
import java.util.Date;

import com.e2w.cschedule.AddNewContactActivity;
import com.e2w.cschedule.CreateNewScheduleActivity;
import com.e2w.cschedule.R;
import com.e2w.cschedule.adapter.ParticipantAdapter;
import com.e2w.cschedule.adapter.SharedMemberAdapter;
import com.e2w.cschedule.db.DatabaseHelper;
import com.e2w.cschedule.interfaces.LoadingInterface;
import com.e2w.cschedule.interfaces.SharedMemberInterface;
import com.e2w.cschedule.models.Confirm;
import com.e2w.cschedule.models.MyActivity;
import com.e2w.cschedule.models.Participant;
import com.e2w.cschedule.models.SharedMemberTable;
import com.e2w.cschedule.models.Sharedmember;
import com.e2w.cschedule.net.WebservicesHelper;
import com.e2w.cschedule.utils.CommConstant;
import com.e2w.cschedule.utils.SharedReference;
import com.e2w.cschedule.utils.Utils;
import com.e2w.cschedule.views.ConfirmDialog;
import com.e2w.cschedule.views.LoadingPopupViewHolder;
import com.e2w.cschedule.views.ParticipantView;

import android.app.Activity;
import android.content.ContentValues;
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
 * @author Huyen {@This page looks like �Contact� page. But it�s
 *         different. It shows all participants with role name attached and has
 *         a select button for each participant. The role �participant� is Not
 *         displayed. A user can assign some participants for a schedule. We
 *         call it �On Duty� *}
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
	public static LoadingPopupViewHolder loadingPopup;
	public static final int DIALOG_LOADING_THEME = android.R.style.Theme_Translucent_NoTitleBar;
	ArrayList<Sharedmember> activityParticipant;

	ArrayList<Sharedmember> arrSharemember;
	ArrayList<Confirm> selectedParticipant;
	Participant participantSelected;

	public void setActivity_id(String activity_id) {
		this.activity_id = activity_id;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setSelectedParticipant(ArrayList<Confirm> selectedParticipant) {
		this.selectedParticipant = selectedParticipant;
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		onClickListener();
	}

	SharedMemberInterface sharedMember = new SharedMemberInterface() {

		@Override
		public void onError(String error) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete() {
			// TODO Auto-generated method stub

		}
	};

	private void initData() {

		DatabaseHelper db = DatabaseHelper.getSharedDatabaseHelper(mContext);
		if (activity_id != null && (!activity_id.equals(""))) {
			myActivity = db.getActivity(activity_id);
		}
		if (activity_id != null && (!activity_id.equals(""))) {
			final DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			if (type == CommConstant.TYPE_PARTICIPANT) {

				view.add_new_contact.setVisibility(View.GONE);
				view.titleBar.tv_name.setText(mContext.getResources()
						.getString(R.string.select_participant));
				view.titleBar.layout_save.setVisibility(View.VISIBLE);
				view.titleBar.layout_next.setVisibility(View.GONE);
				arrSharemember = dbHelper
						.getSharedMemberForActivity(activity_id);

				if (arrSharemember != null && arrSharemember.size() > 0) {
					if (selectedParticipant != null
							&& selectedParticipant.size() > 0) {

						int shareMemberSize = arrSharemember.size();
						for (int i = 0; i < shareMemberSize; i++) {
							Sharedmember s = arrSharemember.get(i);
							for (Confirm sp : selectedParticipant) {
								if (s.getID() == sp.getMemberId()) {
									// set this participant is selected
									s.isChecked = true;
									arrSharemember.set(i, s);
								}
							}

						}
					}
					final SharedMemberAdapter adapter = new SharedMemberAdapter(
							mContext, arrSharemember, true, false, true);
					view.list_contact.setAdapter(adapter);
					view.list_contact
							.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, final int position, long id) {
									final Sharedmember shareMember = adapter.sharedMembers
											.get(position);

									// TODO Auto-generated method stub
									shareMember.isChecked = !shareMember.isChecked;
									adapter.sharedMembers.set(position,
											shareMember);
									adapter.notifyDataSetChanged();

								}
							});

				}
			} else if (type == CommConstant.TYPE_CONTACT) {
				view.add_new_contact.setVisibility(View.VISIBLE);
				view.titleBar.tv_name.setText(mContext.getResources()
						.getString(R.string.add_participant));

				arrParticipant = dbHelper.getParticipants();
				activityParticipant = dbHelper
						.getParticipantsOfActivity(activity_id);

				if (arrParticipant != null && arrParticipant.size() > 0) {
					int owner_id = new SharedReference()
							.getCurrentOwnerId(mContext);
					if (myActivity.getOwner_ID() == owner_id) {
						String owner_email = new SharedReference()
								.getEmail(mContext);
						for (Participant ap : arrParticipant) {
							// remove if owner

							if (ap.getEmail().equals(owner_email)) {
								arrParticipant.remove(ap);
								break;
							}
						}
					}
				}
				if (activityParticipant != null
						&& activityParticipant.size() > 0) {

					// remove item have at activityParticipant to arrParticipant
					for (Sharedmember ap : activityParticipant) {

						if (arrParticipant != null && arrParticipant.size() > 0) {

							int size = arrParticipant.size();
							for (int i = 0; i < size; i++) {
								Participant p = arrParticipant.get(i);
								if (p.getID() == ap.getID()) {
									arrParticipant.remove(p);
									break;
								}

							}
						}
					}
				}
				adapter = new ParticipantAdapter(mContext, arrParticipant,
						false, false);
				view.list_contact.setAdapter(adapter);
				view.list_contact
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, final int position, long id) {
								participantSelected = adapter.participants
										.get(position);
								String activity_name = myActivity != null ? myActivity
										.getActivity_name() : " my activity";
								String title = mContext.getResources()
										.getString(R.string.confirm_title)
										+ " "
										+ participantSelected.getName()
										+ " "
										+ mContext.getResources().getString(
												R.string.into)
										+ " "
										+ activity_name;
								final ConfirmDialog confirmDialog = new ConfirmDialog(
										mContext, title);
								confirmDialog.show();
								confirmDialog.btnOk
										.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												confirmDialog.dismiss();
												// if this activity haven't
												// contain of this
												// participant=> add this
												// participant for this
												// activity
												// else ignore
												if (!activityParticipant
														.contains(participantSelected)) {

													ContentValues contentValues = new ContentValues();
													contentValues
															.put(SharedMemberTable.service_id,
																	activity_id);
													contentValues
															.put(SharedMemberTable.smid,
																	String.valueOf(new Date()
																			.getTime()));
													contentValues
															.put(SharedMemberTable.role,
																	CommConstant.PARTICIPANT);
													contentValues
															.put(SharedMemberTable.member_name,
																	participantSelected
																			.getName());
													contentValues
															.put(SharedMemberTable.member_mobile,
																	participantSelected
																			.getMobile());
													contentValues
															.put(SharedMemberTable.member_id,
																	participantSelected
																			.getID());
													contentValues
															.put(SharedMemberTable.is_Deleted,
																	0);
													contentValues
															.put(SharedMemberTable.is_Synced,
																	0);
													contentValues
															.put(SharedMemberTable.member_email,
																	participantSelected
																			.getEmail());
													dbHelper.insertSharedmember(contentValues);

													WebservicesHelper
															.getInstance()
															.postSharedmemberToActivity(mContext,
																	participantSelected
																			.getID(),
																	CommConstant.ROLE_SHARE_MEMBER_ACTIVITY,
																	activity_id,
																	loadingInterface,sharedMemberInterface);

												}

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

	
	SharedMemberInterface sharedMemberInterface=new SharedMemberInterface() {
		
		@Override
		public void onError(String error) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onComplete() {
			initData();
		}
	};
	
	LoadingInterface loadingInterface = new LoadingInterface() {

		@Override
		public void onStart() {
			showLoading(getActivity());
		}

		@Override
		public void onFinish() {
			dimissDialog();
		}
	};

	public static ParticipantFragment getInstance() {
		return ParticipantFragment.getInstance();
	}

	private void onClickListener() {
		view.titleBar.layout_next.setOnClickListener(this);
		view.titleBar.layout_save.setOnClickListener(this);
		view.layout_add_new_contact.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == view.titleBar.layout_next) {
			// share schedule
			if (type == CommConstant.TYPE_CONTACT) {

				((Activity) mContext).finish();
				Intent intent = new Intent(mContext,
						CreateNewScheduleActivity.class);
				intent.putExtra(CommConstant.TYPE, DatabaseHelper.NEW);
				intent.putExtra(CommConstant.ACTIVITY_ID, activity_id);
				mContext.startActivity(intent);
				Utils.slideUpDown(mContext);
			} else if (type == CommConstant.TYPE_PARTICIPANT) {
				// if (activity_id != null && (!activity_id.equals(""))) {
				// ArrayList<Integer> listParticipantOnDuty = new
				// ArrayList<Integer>();
				// for (Sharedmember member : arrSharemember) {
				// if (member.isChecked) {
				// listParticipantOnDuty.add(member.getID());
				// }
				// }
				//
				// Intent i = getActivity().getIntent(); // gets the intent
				// // that called this
				// // intent
				// i.putExtra(CommConstant.ACTIVITY_ID, activity_id);
				// i.putIntegerArrayListExtra(
				// CommConstant.ON_DUTY_ITEM_SELECTED,
				// listParticipantOnDuty);
				// getActivity().setResult(333, i);
				// ((Activity) mContext).finish();
				// Utils.postLeftToRight(mContext);
				// }
			}
		} else if (v == view.titleBar.layout_save) {
			if (activity_id != null && (!activity_id.equals(""))) {
				ArrayList<String> listParticipantOnDuty = new ArrayList<String>();
				for (Sharedmember member : arrSharemember) {
					if (member.isChecked) {
						listParticipantOnDuty.add(member.getID() + ";"
								+ CommConstant.CONFIRM_UNKNOWN);
					}
				}

				Intent i = getActivity().getIntent(); // gets the intent
														// that called this
														// intent
				i.putExtra(CommConstant.ACTIVITY_ID, activity_id);
				i.putStringArrayListExtra(CommConstant.ON_DUTY_ITEM_SELECTED,
						listParticipantOnDuty);
				getActivity().setResult(333, i);
				((Activity) mContext).finish();
				Utils.postLeftToRight(mContext);
			}
		} else if (v == view.layout_add_new_contact) {
			Intent intent = new Intent(mContext, AddNewContactActivity.class);
			intent.putExtra("type", DatabaseHelper.NEW);
			mContext.startActivity(intent);
			Utils.slideUpDown(mContext);
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
		initData();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	// show loading
	public void showLoading(Context mContext) {
		if (loadingPopup == null) {
			loadingPopup = new LoadingPopupViewHolder(mContext,
					DIALOG_LOADING_THEME);
		}
		loadingPopup.setCancelable(true);
		if (!loadingPopup.isShowing()) {
			loadingPopup.show();
		}
	}

	public void dimissDialog() {
		if (loadingPopup != null && loadingPopup.isShowing()) {
			loadingPopup.dismiss();
		}
	}
}
