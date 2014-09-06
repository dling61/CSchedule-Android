package com.e2wstudy.cschedule;

import java.util.ArrayList;
import java.util.List;

import com.e2wstude.schedule.interfaces.LoginInterface;
import com.e2wstudy.cschedule.adapter.SharedMemberAdapter;
import com.e2wstudy.cschedule.adapter.TextViewBaseAdapter;
import com.e2wstudy.cschedule.db.DatabaseHelper;
import com.e2wstudy.cschedule.models.ActivityTable;
import com.e2wstudy.cschedule.models.Alert;
import com.e2wstudy.cschedule.models.MyActivity;
import com.e2wstudy.cschedule.models.OndutyTable;
import com.e2wstudy.cschedule.models.ParticipantTable;
import com.e2wstudy.cschedule.models.Schedule;
import com.e2wstudy.cschedule.models.ScheduleTable;
import com.e2wstudy.cschedule.models.SharedMemberTable;
import com.e2wstudy.cschedule.models.Sharedmember;
import com.e2wstudy.cschedule.models.TimeZoneModel;
import com.e2wstudy.cschedule.net.WebservicesHelper;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.SharedReference;
import com.e2wstudy.cschedule.utils.Utils;
import com.e2wstudy.cschedule.views.AddActivityView;
import com.e2wstudy.cschedule.views.ConfirmDialog;
import com.e2wstudy.cschedule.views.LoadingPopupViewHolder;
import com.e2wstudy.cschedule.views.ParticipantInforDialog;
import com.e2wstudy.cschedule.views.ToastDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class AddNewActivity extends Activity implements OnClickListener {
	private MyActivity thisActivity;
	private int composeType;
	private DatabaseHelper dbHelper;
	Context mContext;
	AddActivityView view;
	String activity_id = "";
	
	// shared role for privacy
	int shared_role = CommConstant.OWNER;
	public LoadingPopupViewHolder loadingPopup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mContext = this;
		view = new AddActivityView(mContext);
		setContentView(view.layout);

		dbHelper = DatabaseHelper.getSharedDatabaseHelper(mContext);
		
		ArrayList<TimeZoneModel> listTimeZone=new ArrayList<TimeZoneModel>();
		ArrayList<Alert>listAlert=new ArrayList<Alert>();
		int tz=1;
		int alert=1;
		if(listTimeZone!=null&&listTimeZone.size()>0)
		{
			tz=listTimeZone.get(0).getId();
		}
		
		if(listAlert!=null&&listAlert.size()>0)
		{
			alert=listAlert.get(0).getId();
		}

		thisActivity = new MyActivity(dbHelper.getNextActivityID() + "",
				new SharedReference().getCurrentOwnerId(mContext), "", "", 0,alert,tz);

		Intent myIntent = getIntent();
		composeType = myIntent.getIntExtra(CommConstant.TYPE, 3);
		// view.et_new_activity_description.setFocusable(false);
		view.titleBar.layout_save.setVisibility(View.VISIBLE);
		view.titleBar.layout_next.setVisibility(View.GONE);
		if (composeType == DatabaseHelper.NEW) {
			Log.i("next service id", "is " + dbHelper.getNextActivityID());
			thisActivity = new MyActivity(dbHelper.getNextActivityID() + "",
					new SharedReference().getCurrentOwnerId(mContext), "", "",
					CommConstant.OWNER,alert,tz);
			view.titleBar.layout_save.setVisibility(View.GONE);
			view.titleBar.layout_next.setVisibility(View.VISIBLE);
		} else if (composeType == DatabaseHelper.EXISTED) {
			activity_id = myIntent.getStringExtra(CommConstant.ACTIVITY_ID);
			shared_role = myIntent.getIntExtra(CommConstant.ROLE,
					CommConstant.OWNER);
			thisActivity = dbHelper.getActivity(activity_id);
			// set visible when edit
			view.btn_add_paticipant.setVisibility(View.VISIBLE);
			view.btn_remove_activity.setVisibility(View.VISIBLE);
			view.titleBar.layout_next.setVisibility(View.GONE);
			view.titleBar.layout_save.setVisibility(View.VISIBLE);
		}
		this.initViewValues();
		onClickListener();

		try {
			registerReceiver(activityGetSharedMemberComplete, new IntentFilter(
					CommConstant.GET_SHARED_MEMBER_ACTIVITY_COMPLETE));
			registerReceiver(deleteActivityComplete, new IntentFilter(
					CommConstant.DELETE_ACTIVITY_COMPLETE));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// show loading
	public void showLoading(Context mContext) {
		if (loadingPopup == null) {
			loadingPopup = new LoadingPopupViewHolder(mContext,
					CategoryTabActivity.DIALOG_LOADING_THEME);
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

	LoginInterface loginInterface = new LoginInterface() {

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			showLoading(AddNewActivity.this);
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			dimissDialog();
		}

		@Override
		public void onError() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete() {
			// TODO Auto-generated method stub

		}
	};

	
	BroadcastReceiver deleteActivityComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {

			finish();
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);
			Utils.postLeftToRight(mContext);
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {

		super.onPause();
	};

	/**
	 * get shared member of activity
	 * */
	private void setParticipantOfActivity() {
		if (activity_id != null && (!activity_id.equals(""))) {
			DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			ArrayList<Sharedmember> list_participant = dbHelper
					.getParticipantsOfActivity(activity_id);
			final SharedMemberAdapter adapter = new SharedMemberAdapter(
					mContext, list_participant, false, false, true);
			view.list_participant.setAdapter(adapter);
			Utils.setListViewHeightBasedOnChildren(view.list_participant,
					adapter);
			view.list_participant.setVisibility(View.VISIBLE);

			view.tv_participant.setVisibility(View.VISIBLE);
			view.list_participant
					.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, final int position, long id) {
							final Sharedmember participantSelected = adapter.sharedMembers
									.get(position);
							participantInforDialog(participantSelected);

						}
					});
		}

	}

	BroadcastReceiver activityGetSharedMemberComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			setParticipantOfActivity();
		}
	};

	private void participantInforDialog(final Sharedmember participant) {
		String[] array = getResources().getStringArray(
				R.array.owner_infor_array);

		if (shared_role != CommConstant.OWNER
				|| (participant.getEmail().equals(new SharedReference()
						.getEmail(mContext)))) {
			array = getResources().getStringArray(
					R.array.participant_infor_array);
		}

		int length = array.length;
		for (int i = 0; i < length; i++) {
			array[i] += " " + participant.getName();
		}
		TextViewBaseAdapter adapter = new TextViewBaseAdapter(mContext, array);

		final ParticipantInforDialog dialog = new ParticipantInforDialog(
				mContext);
		dialog.show();
		dialog.topBar.setVisibility(View.GONE);
		dialog.list_item.setAdapter(adapter);
		dialog.list_item.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if (shared_role == CommConstant.OWNER
						&& (!participant.getEmail().equals(
								new SharedReference().getEmail(mContext)))) {
					switch (position) {
					case 0:
						Utils.makeAPhoneCall(mContext, participant.getMobile());
						break;
					case 1:
						Utils.sendAMessage(mContext, participant.getMobile());
						break;
					case 2:
						if (shared_role == CommConstant.OWNER) {
							// remove participant from activity
							deleteParticipantFromActivity(participant);
						}
						break;
					case 3:
						break;
					default:
						break;
					}
				} else {
					switch (position) {
					case 0:
						Utils.makeAPhoneCall(mContext, participant.getMobile());
						break;
					case 1:
						Utils.sendAMessage(mContext, participant.getMobile());
						break;

					default:
						break;
					}
				}
				dialog.dismiss();
			}
		});
		dialog.btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	// delete participant from activity dialog
	public void deleteParticipantFromActivity(final Sharedmember participant) {
		final ConfirmDialog dialog = new ConfirmDialog(mContext, mContext
				.getResources().getString(
						R.string.delete_participant_from_activity)
				+ " "
				+ participant.getName()
				+ " from "
				+ thisActivity.getActivity_name());
		dialog.show();
		dialog.btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				removeParticipant(participant);
			}
		});

		dialog.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

	}

	// delete participant from activity
	public void removeParticipant(Sharedmember participant) {
		WebservicesHelper ws = new WebservicesHelper(mContext);
		ws.deleteSharedmemberOfActivity(participant.getID(), activity_id);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(deleteActivityComplete);
			unregisterReceiver(activityGetSharedMemberComplete);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * On Click listener
	 * */
	public void onClickListener() {
		view.btn_add_paticipant.setOnClickListener(this);
		view.btn_remove_activity.setOnClickListener(this);
		view.titleBar.layout_next.setOnClickListener(this);
		view.titleBar.layout_back.setOnClickListener(this);
		view.et_new_activity_description.setOnClickListener(this);
		view.titleBar.layout_save.setOnClickListener(this);
		view.et_new_activity_description
				.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(final View v,
							final MotionEvent motionEvent) {

						v.getParent().requestDisallowInterceptTouchEvent(true);
						switch (motionEvent.getAction()
								& MotionEvent.ACTION_MASK) {
						case MotionEvent.ACTION_UP:
							v.getParent().requestDisallowInterceptTouchEvent(
									false);
							break;
						}
						// }
						return false;
					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Init values
	 * */
	public void initViewValues() {
		if (composeType == DatabaseHelper.NEW) {
			view.titleBar.tv_name.setText(mContext.getResources().getString(
					R.string.add_activity));
			view.titleBar.layout_next.setVisibility(View.GONE);
			view.titleBar.layout_save.setVisibility(View.VISIBLE);

		} else if (composeType == DatabaseHelper.EXISTED) {
			view.titleBar.tv_name.setText(mContext.getResources().getString(
					R.string.edit_activity));

			view.et_new_activity_name.setSingleLine(false);
			setParticipantOfActivity();

			if (shared_role != CommConstant.OWNER) {
				view.et_new_activity_description.setEnabled(false);
				view.et_new_activity_name.setEnabled(false);
				view.btn_add_paticipant.setVisibility(View.GONE);
				view.btn_remove_activity.setVisibility(View.GONE);
				view.titleBar.layout_save.setEnabled(false);
			} else {
				view.et_new_activity_description.setEnabled(true);
				view.et_new_activity_description.setFocusableInTouchMode(true);
				view.et_new_activity_description.requestFocus();
				view.et_new_activity_name.setEnabled(true);
				view.btn_add_paticipant.setVisibility(View.VISIBLE);
				view.btn_remove_activity.setVisibility(View.VISIBLE);
				view.titleBar.layout_save.setEnabled(true);

			}

		}

		String activity_name = thisActivity != null ? thisActivity
				.getActivity_name() : "";
		view.et_new_activity_name.setText(activity_name);

		String desp = thisActivity != null ? thisActivity.getDesp() : "";
		view.et_new_activity_description.setText(desp);
	}

	@Override
	public void onClick(View v) {
		Utils.hideKeyboard(AddNewActivity.this, view.et_new_activity_name);
		Utils.hideKeyboard(AddNewActivity.this,
				view.et_new_activity_description);
		// TODO Auto-generated method stub
		if (v == view.titleBar.layout_next) {
			if (composeType == DatabaseHelper.NEW) {
//				Utils.isNetworkAvailable(createNewActivityHandle);
				// createNewActivity();
				
				if (Utils.isNetworkOnline(mContext)) {
					// code if connected
					createNewActivity();
				} else {
					final ToastDialog dialog = new ToastDialog(mContext, mContext
							.getResources().getString(R.string.no_network));
					dialog.show();
					dialog.btnOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}
				
			}
		} else if (v == view.btn_add_paticipant) {
			if (composeType == DatabaseHelper.EXISTED) {

				finish();

				Intent intent = new Intent(mContext, ParticipantActivity.class);
				intent.putExtra(CommConstant.TYPE,
						CommConstant.ADD_PARTICIPANT_FOR_ACTIVITY);
				intent.putExtra(CommConstant.ACTIVITY_ID, activity_id);
				intent.putExtra(CommConstant.TYPE, CommConstant.TYPE_CONTACT);
				mContext.startActivity(intent);
				Utils.pushRightToLeft(mContext);
			}
		} else if (v == view.btn_remove_activity) {
			dialogDeleteActivity();
		} else if (v == view.titleBar.layout_back) {
			((Activity) mContext).finish();
			// Utils.postLeftToRight(mContext);
		} else if (v == view.et_new_activity_description) {
			// show an activity to edit description
			if (shared_role == CommConstant.OWNER) {
				Utils.openKeyboard(mContext, view.et_new_activity_description);
			}

		} else if (v == view.titleBar.layout_save) {
			if (composeType == DatabaseHelper.EXISTED) {
//				Utils.isNetworkAvailable(editActivityHandle);
				// editActivity();
				
				if (Utils.isNetworkOnline(mContext)) {
					// code if connected
					editActivity();
				} else {
					final ToastDialog dialog = new ToastDialog(mContext, mContext
							.getResources().getString(R.string.no_network));
					dialog.show();
					dialog.btnOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}
			} else if (composeType == DatabaseHelper.NEW) {
//				Utils.isNetworkAvailable(createNewActivityHandle);
				// createNewActivity();
				if (Utils.isNetworkOnline(mContext)) {
					// code if connected
					createNewActivity();
				} else {
					final ToastDialog dialog = new ToastDialog(mContext, mContext
							.getResources().getString(R.string.no_network));
					dialog.show();
					dialog.btnOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}
			}
		}
	}

	/**
	 * delete activity
	 * */
	private void deleteActivity() {

		ArrayList<Sharedmember> listSharedMemberOfActivity = dbHelper
				.getSharedMemberForActivity(activity_id);
		if (listSharedMemberOfActivity != null
				&& listSharedMemberOfActivity.size() > 0) {
			for (Sharedmember sharedMember : listSharedMemberOfActivity) {
				ContentValues cv = new ContentValues();
				cv.put(SharedMemberTable.is_Deleted, 1);
				cv.put(SharedMemberTable.is_Synced, 0);
				dbHelper.updateSharedmember(sharedMember.getID(), activity_id,
						cv);
				WebservicesHelper ws = new WebservicesHelper(mContext);
				ws.deleteSharedmemberOfActivity(sharedMember.getID(),
						activity_id);
			}
		}

		List<Schedule> sbelongtoa = dbHelper
				.getSchedulesBelongtoActivity(activity_id);
		for (int i = 0; i < sbelongtoa.size(); i++) {
			ContentValues scv = new ContentValues();
			scv.put(ScheduleTable.is_Deleted, 1);
			scv.put(ScheduleTable.is_Synchronized, 0);
			int schedule_id = sbelongtoa.get(i).getSchedule_ID();
			// dbHelper.updateSchedule(schedule_id, scv);
			List<Integer> onduties = dbHelper
					.getOndutyRecordsForSchedule(schedule_id);
			for (int j = 0; j < onduties.size(); j++) {
				ContentValues ocv = new ContentValues();
				ocv.put(OndutyTable.is_Deleted, 1);
				ocv.put(OndutyTable.is_Synchronized, 0);
				// int onduty_id = onduties.get(j);
				dbHelper.updateOnduty(schedule_id, ocv);
			}
			dbHelper.updateSchedule(schedule_id, scv);
			WebservicesHelper ws = new WebservicesHelper(mContext);
			ws.deleteSchedule(sbelongtoa.get(i));
		}

		ContentValues cv = new ContentValues();
		cv.put(ActivityTable.is_Deleted, 1);
		cv.put(ActivityTable.is_Synchronized, 0);
		dbHelper.updateActivity(activity_id, cv);

		WebservicesHelper ws = new WebservicesHelper(mContext);
		ws.deleteActivity(thisActivity);
	}

	/**
	 * Delete activity
	 * */
	private void dialogDeleteActivity() {

		Utils.hideKeyboard(AddNewActivity.this, view.et_new_activity_name);
		Utils.hideKeyboard(AddNewActivity.this,
				view.et_new_activity_description);

		final ConfirmDialog dialog = new ConfirmDialog(mContext, mContext
				.getResources().getString(R.string.delete_activity));
		dialog.show();
		dialog.btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// deleteActivity();
//				Utils.isNetworkAvailable(deleteActivityHandle);
				
				if (Utils.isNetworkOnline(mContext)) {
					// code if connected
					deleteActivity();
				} else {
					final ToastDialog dialog = new ToastDialog(mContext, mContext
							.getResources().getString(R.string.no_network));
					dialog.show();
					dialog.btnOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}
				
				dialog.dismiss();
			}
		});
		dialog.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * Edit activity
	 * */
	private void editActivity() {
		if (setAndCheckDataForActivity()) {
			if (composeType == DatabaseHelper.EXISTED) {
				ContentValues cv = new ContentValues();
				cv.put(ActivityTable.service_Name,
						thisActivity.getActivity_name());

				// cv.put(ActivityTable.start_time,
				// thisActivity.getStarttime());
				// cv.put(ActivityTable.end_time, thisActivity.getEndtime());
				cv.put(ActivityTable.service_description,
						thisActivity.getDesp());

				cv.put(ActivityTable.sharedrole, thisActivity.getRole());
				cv.put(ActivityTable.is_Deleted, 0);
				cv.put(ActivityTable.is_Synchronized, 0);
				dbHelper.updateActivity(thisActivity.getActivity_ID(), cv);

				WebservicesHelper ws = new WebservicesHelper(mContext);
				ws.updateActivity(thisActivity,loginInterface);

			}
		}
	}

	/**
	 * Set and check data invalid for activity to add or edit
	 * */
	private boolean setAndCheckDataForActivity() {
		if (view.et_new_activity_name.getText().toString().equalsIgnoreCase("")) {
			final ToastDialog dialog = new ToastDialog(mContext, mContext
					.getResources().getString(R.string.activity_name_not_blank));
			dialog.show();
			dialog.btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			return false;
		}

		String activity_name = view.et_new_activity_name.getText().toString()
				.trim();
		String activity_description = view.et_new_activity_description
				.getText().toString().trim();

		if (activity_name == null || activity_name.equals("")) {
			final ToastDialog dialog = new ToastDialog(mContext, mContext
					.getResources().getString(R.string.input_activity_name));
			dialog.show();
			dialog.btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			return false;
		}
		if (activity_description == null || activity_description.equals("")) {
			final ToastDialog dialog = new ToastDialog(mContext, mContext
					.getResources().getString(
							R.string.input_activity_description));
			dialog.show();
			dialog.btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			return false;
		}

		thisActivity.setActivity_name(activity_name);
		thisActivity.setDesp(activity_description);
		return true;
	}

	/**
	 * Create new activity
	 * */
	private void createNewActivity() {

		if (setAndCheckDataForActivity()) {
			if (composeType == DatabaseHelper.NEW) {
				ContentValues newActivity = new ContentValues();
				newActivity.put(ActivityTable.service_ID,
						thisActivity.getActivity_ID());
				newActivity.put(ActivityTable.own_ID,
						thisActivity.getOwner_ID());
				newActivity.put(ActivityTable.service_Name,
						thisActivity.getActivity_name());

				newActivity.put(ActivityTable.sharedrole,
						thisActivity.getRole());
				// newActivity.put(ActivityTable.start_time,
				// thisActivity.getStarttime());
				// newActivity.put(ActivityTable.end_time,
				// thisActivity.getEndtime());
				newActivity.put(ActivityTable.service_description,
						thisActivity.getDesp());

				newActivity.put(ActivityTable.is_Deleted, 0);
				newActivity.put(ActivityTable.is_Synchronized, 0);
				newActivity.put(ActivityTable.last_ModifiedTime, "nouploaded");
				newActivity.put(ParticipantTable.user_login,
						new SharedReference().getCurrentOwnerId(mContext));
				
				ArrayList<TimeZoneModel> listTimeZone=DatabaseHelper.getSharedDatabaseHelper(mContext).getTimeZone();
				ArrayList<Alert> listAlert=DatabaseHelper.getSharedDatabaseHelper(mContext).getAlerts();
				if(listAlert!=null&&listAlert.size()>0)
				{
					int alertId=listAlert.get(0).getId();
					newActivity.put(ActivityTable.alertId, alertId);
				}
				if(listTimeZone!=null&&listTimeZone.size()>0)
				{
					int timeZoneId=listTimeZone.get(0).getId();
					newActivity.put(ActivityTable.timeZoneId, timeZoneId);
				}								
				
				if (dbHelper.insertActivity(newActivity)) {

				}
				WebservicesHelper ws = new WebservicesHelper(mContext);
				ws.addActivity(thisActivity,loginInterface);
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 222) {
			String activity_decription = data
					.getStringExtra(CommConstant.ACTIVITY_DESCRIPTION);
			view.et_new_activity_description
					.setText(activity_decription != null ? activity_decription
							: view.et_new_activity_description.getText()
									.toString());
		}
		switch (requestCode) {
		case (11): {
			Utils.slideUpDown(mContext);
			Intent resultIntent = new Intent();
			resultIntent.putExtra("id", thisActivity.getActivity_ID());

			setResult(Activity.RESULT_OK, resultIntent);
			finish();
			break;
		}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Utils.hideKeyboard(AddNewActivity.this,
				view.et_new_activity_description);
		Utils.hideKeyboard(AddNewActivity.this, view.et_new_activity_name);
	}

	Handler createNewActivityHandle = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what != 1) { // code if not connected
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(R.string.no_network));
				dialog.show();
				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			} else { // code if connected
				createNewActivity();
			}

		}
	};

	Handler editActivityHandle = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what != 1) { // code if not connected
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(R.string.no_network));
				dialog.show();
				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			} else { // code if connected
				editActivity();
			}

		}
	};

	Handler deleteActivityHandle = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what != 1) { // code if not connected
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(R.string.no_network));
				dialog.show();
				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			} else { // code if connected
				deleteActivity();
			}

		}
	};
}
