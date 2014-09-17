package com.e2w.cschedule;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.e2w.cschedule.adapter.ActivityNameAdapter;
import com.e2w.cschedule.adapter.AlertTextBaseAdapter;
import com.e2w.cschedule.adapter.TextBaseAdapter;
import com.e2w.cschedule.db.DatabaseHelper;
import com.e2w.cschedule.interfaces.AddUpdateScheduleInterface;
import com.e2w.cschedule.interfaces.GetServerSettingInterface;
import com.e2w.cschedule.interfaces.LoadingInterface;
import com.e2w.cschedule.models.ActivityTable;
import com.e2w.cschedule.models.Alert;
import com.e2w.cschedule.models.Confirm;
import com.e2w.cschedule.models.MyActivity;
import com.e2w.cschedule.models.OndutyTable;
import com.e2w.cschedule.models.Schedule;
import com.e2w.cschedule.models.ScheduleTable;
import com.e2w.cschedule.models.Sharedmember;
import com.e2w.cschedule.models.TimeZoneModel;
import com.e2w.cschedule.net.WebservicesHelper;
import com.e2w.cschedule.utils.CommConstant;
import com.e2w.cschedule.utils.MyDate;
import com.e2w.cschedule.utils.SharedReference;
import com.e2w.cschedule.utils.Utils;
import com.e2w.cschedule.views.AddScheduleView;
import com.e2w.cschedule.views.ConfirmDialog;
import com.e2w.cschedule.views.LoadingPopupViewHolder;
import com.e2w.cschedule.views.PopupDialog;
import com.e2w.cschedule.views.ToastDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author khoahuyen
 * @category add new schedule
 * */
// @SuppressLint("NewApi")
public class CreateNewScheduleActivity extends Activity implements
		OnDateSetListener, OnTimeSetListener, OnMenuItemClickListener,
		OnClickListener {
	static private final int START = 0;
	static private final int END = 1;
	private Schedule thisSchedule;
	private int composeType;
	private int StartOrEnd;
	
	// private ArrayList<Confirm> pins;

	AddScheduleView view;
	String activity_id = "";
	Context mContext;
	MyActivity myActivity;
	int startMonth, startYear, startDate, startHour, startMin;
	int endMonth, endYear, endDate, endHour, endMin;

	int REQUEST_CODE = 15;
	int schedule_id = -1;
	ProgressDialog progress = null;
	// creator of this schedule
	int creator;

	ArrayList<TimeZoneModel> listTimezone = null;
	ArrayList<Alert> listAlert = null;

	int type = -1;// type=repeat, alert, timezone
	static final int REPEAT = 0;
	static final int ALERT = 1;
	static final int TIMEZONE = 2;
	ArrayList<Confirm> listPins;

	boolean isShowPopupTimeZone = false;
	boolean isShowPopupAlert = false;

	public LoadingPopupViewHolder loadingPopup;
	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mContext = this;
		view = new AddScheduleView(mContext);
		this.setContentView(view.layout);
		view.et_new_activity_description.requestFocus();
		Intent myIntent = this.getIntent();
		composeType = myIntent.getIntExtra(CommConstant.TYPE, -1);
		activity_id = myIntent.getStringExtra(CommConstant.ACTIVITY_ID);

		

		creator = new SharedReference().getCurrentOwnerId(mContext);
		// view.et_new_activity_description.setFocusable(false);
		if (activity_id != null && (!activity_id.equals(""))) {
			myActivity = DatabaseHelper.getSharedDatabaseHelper(mContext).getActivity(activity_id);
		} else {
			// TODO:make activityname is suggestion activity in system
		}
		if (composeType == DatabaseHelper.EXISTED) {
			schedule_id = myIntent.getIntExtra(CommConstant.SCHEDULE_ID, -1);
			creator = myIntent.getIntExtra(CommConstant.CREATOR,
					new SharedReference().getCurrentOwnerId(mContext));
		}
		initViewValues();

		onClickListener();
	}

	


	

	/**
	 * Return popup activityname have shared role owner or organizer
	 * */
	public void popUp() {
		DatabaseHelper dbHelper = DatabaseHelper
				.getSharedDatabaseHelper(mContext);
		final ArrayList<MyActivity> listActivity = dbHelper
				.getActivitiesOwnerOrOrganizer(new SharedReference()
						.getCurrentOwnerId(mContext) + "");
		ActivityNameAdapter adapter = new ActivityNameAdapter(mContext,
				listActivity);
		final PopupDialog dialog = new PopupDialog(mContext, mContext
				.getResources().getString(R.string.activity));
		dialog.show();
		dialog.list_item.setAdapter(adapter);
		dialog.list_item.setVisibility(View.VISIBLE);
		dialog.list_item.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				myActivity = listActivity.get(position);
				if (myActivity != null) {
					activity_id = myActivity.getActivity_ID();
					view.et_new_activity_name.setText(myActivity
							.getActivity_name());
					int tz = myActivity.getTimezoneId();
					int alertId = myActivity.getAlertId();
					TimeZoneModel timeZoneModel = DatabaseHelper
							.getSharedDatabaseHelper(mContext).getTimeZone(tz);
					Alert alertModel = DatabaseHelper.getSharedDatabaseHelper(
							mContext).getAlert(alertId);
					if (timeZoneModel != null) {
						String timeZoneName = timeZoneModel.getTzname();
						view.et_new_activity_time_zone.setText(timeZoneName);
					}
					if (alertModel != null) {
						String alertType = alertModel.getAname();
						view.et_new_activity_alert.setText(alertType);
					}
				}
				dialog.dismiss();

			}
		});

	}

	BroadcastReceiver updateSchedule = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {

			initViewValues();
		}

	};
	
	GetServerSettingInterface iServerSetting=new GetServerSettingInterface() {
		
		@Override
		public void onError(String error) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onComplete() {
			listAlert = DatabaseHelper.getSharedDatabaseHelper(mContext)
					.getAlerts();
			listTimezone = DatabaseHelper.getSharedDatabaseHelper(mContext)
					.getTimeZone();
			if (listAlert != null && listAlert.size() > 0) {
				view.et_new_activity_alert.setText(listAlert.get(0).getAname());
				if (isShowPopupTimeZone) {
					popUp(listTimezone);
					isShowPopupTimeZone = false;
				}
			}
			if (listTimezone != null && listTimezone.size() > 0) {
				view.et_new_activity_alert.setText(listAlert.get(0).getAname());
				if (isShowPopupAlert) {
					popUpAlert(listAlert);
					isShowPopupAlert = false;
				}
			}
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * get shared member of activity
	 * */
	private void setParticipantOfActivity(List<Confirm> pins) {
		if (activity_id != null && (!activity_id.equals(""))) {
//			DatabaseHelper dbHelper = DatabaseHelper
//					.getSharedDatabaseHelper(mContext);
			ArrayList<Sharedmember> list_participant = new ArrayList<Sharedmember>();

			if (pins != null && pins.size() > 0) {
				for (int i = 0; i < pins.size(); i++) {
					int member_id = pins.get(i).getMemberId();
					Sharedmember p = DatabaseHelper.getSharedDatabaseHelper(mContext).getSharedmember(member_id,
							activity_id);
					if (p != null) {
						p.isChecked = true;
						list_participant.add(p);
						String currentParticipant = new SharedReference()
								.getEmail(mContext);
						String email = p.getEmail();

//						 if user login is not owner
//						if (currentParticipant.equals(email)) {
//							int confirm = pins.get(i).getConfirm();
//							switch (confirm) {
//							case CommConstant.CONFIRM_CONFIRMED:
//								view.btnConfirm.setVisibility(View.GONE);
//								view.btnDeny.setVisibility(View.VISIBLE);
//								break;
//							case CommConstant.CONFIRM_DENIED:
//								view.btnDeny.setVisibility(View.GONE);
//								view.btnConfirm.setVisibility(View.VISIBLE);
//								break;
//							case CommConstant.CONFIRM_UNKNOWN:
//								view.btnConfirm.setVisibility(View.VISIBLE);
//								view.btnDeny.setVisibility(View.VISIBLE);
//								break;
//							default:
//								view.btnConfirm.setVisibility(View.GONE);
//								view.btnDeny.setVisibility(View.GONE);
//								break;
//							}
//						}
//						if (p.getRole() == CommConstant.OWNER) {
//							view.btnConfirm.setVisibility(View.GONE);
//							view.btnDeny.setVisibility(View.GONE);
//						}
					}

				}
			}

			if (list_participant != null && list_participant.size() > 0) {
//				final SharedMemberAdapter adapter = new SharedMemberAdapter(
//						mContext, list_participant, false, false, true);

				String participant = "";
				int size = list_participant.size();
				if (size > 3) {
					size = 3;
				}
				for (int i = 0; i < size; i++) {
					participant += list_participant.get(i).getName() + ", ";
				}
				try {
					if (list_participant.size() <= 3) {
						if (participant.endsWith(", ")) {
							participant = participant.substring(0,
									participant.length() - 2);
						}
					}
				} catch (Exception exx) {
					exx.printStackTrace();
				}
				if (list_participant.size() > 3) {
					participant += ", ...";
				}
				view.tvOnduty.setText(participant);
//				view.layout_onduty.setVisibility(View.VISIBLE);
			} else {
				view.tvOnduty.setText("");
			}
		}
	}

	private void onClickListener() {
		view.et_endDate.setOnClickListener(this);
		view.et_startDate.setOnClickListener(this);
		view.et_endTime.setOnClickListener(this);
		view.et_startTime.setOnClickListener(this);
		view.et_on_duty.setOnClickListener(this);
		view.titleBar.layout_next.setOnClickListener(this);
		view.btn_remove_schedule.setOnClickListener(this);
		view.titleBar.layout_back.setOnClickListener(this);
		view.titleBar.layout_save.setOnClickListener(this);
		view.et_new_activity_name.setOnClickListener(this);
		view.et_new_activity_description.setOnClickListener(this);
		// view.tv_participant.setOnClickListener(this);
		view.layoutAlert.setOnClickListener(this);
		view.layoutTimeZone.setOnClickListener(this);

		view.layout_onduty.setOnClickListener(this);

		// view.btn_change_on_duty.setOnClickListener(this);
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
		view.btnConfirm.setOnClickListener(this);
		view.btnDeny.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Utils.hideKeyboard(CreateNewScheduleActivity.this,
				view.et_new_activity_description);
		if (v == view.et_startDate) {
			// setStartDate();
			if (!view.et_new_activity_name.getText().toString().trim()
					.equals("")) {
				setStartDate();
			} else {
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(
								R.string.select_activity_schedule));
				dialog.show();
				dialog.btnOk.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		} else if (v == view.et_endDate) {
			if (!view.et_new_activity_name.getText().toString().trim()
					.equals("")) {
				setEndDate();
			} else {
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(
								R.string.select_activity_schedule));
				dialog.show();
				dialog.btnOk.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		} else if (v == view.et_endTime) {
			if (!view.et_new_activity_name.getText().toString().trim()
					.equals("")) {
				setEndTime();
			} else {
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(
								R.string.select_activity_schedule));
				dialog.show();
				dialog.btnOk.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		} else if (v == view.et_startTime) {
			if (!view.et_new_activity_name.getText().toString().trim()
					.equals("")) {
				setStartTime();
			} else {
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(
								R.string.select_activity_schedule));
				dialog.show();

				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		}

//		else if (v == view.btnConfirm) {
//			WebservicesHelper ws = WebservicesHelper.getInstance();
//			try {
//				int member_id = DatabaseHelper
//						.getSharedDatabaseHelper(mContext)
//						.getSharedMemberOfEmail(
//								new SharedReference().getEmail(mContext));
//				if (member_id != -1) {
//					Confirm confirm = new Confirm(member_id,
//							CommConstant.CONFIRM_CONFIRMED);
//					ws.updateConfirmStatus(thisSchedule, confirm);
//				}
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		} else if (v == view.btnDeny) {
//			WebservicesHelper ws = new WebservicesHelper(mContext);
//			try {
//
//				int member_id = DatabaseHelper
//						.getSharedDatabaseHelper(mContext)
//						.getSharedMemberOfEmail(
//								new SharedReference().getEmail(mContext));
//				if (member_id != -1) {
//					Confirm confirm = new Confirm(member_id,
//							CommConstant.CONFIRM_DENIED);
//					ws.updateConfirmStatus(thisSchedule, confirm);
//				}
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		} 
		else if (v == view.et_on_duty) {
			if (!view.et_new_activity_name.getText().toString().trim()
					.equals("")) {

				Intent intent = new Intent(mContext, ParticipantActivity.class);
				intent.putExtra(CommConstant.ACTIVITY_ID, activity_id);
				intent.putExtra(CommConstant.TYPE,
						CommConstant.TYPE_PARTICIPANT);
				intent.putExtra(CommConstant.SCHEDULE_ID,
						thisSchedule != null ? thisSchedule.getSchedule_ID()
								: -1);

				if (listPins != null && listPins.size() > 0) {
					ArrayList<String> listPinString = new ArrayList<String>();
					for (Confirm c : listPins) {
						listPinString.add(c.getMemberId() + ";"
								+ c.getConfirm());
					}
					intent.putStringArrayListExtra("pins", listPinString);
				}

				startActivityForResult(intent, REQUEST_CODE);
				Utils.pushRightToLeft(mContext);
			} else {
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(
								R.string.select_activity_schedule));
				dialog.show();

				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}

		} else if (v == view.titleBar.layout_next) {
			// createNewSchedule();
			// Utils.isNetworkAvailable(createNewScheduleHandle);

			if (Utils.isNetworkOnline(mContext)) {
				// code if connected
				createNewSchedule();
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
		} else if (v == view.btn_remove_schedule) {

			// Utils.isNetworkAvailable(deleteScheduleHandle);
			if (Utils.isNetworkOnline(mContext)) {
				// code if connected
				deleteSchedule();
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
		} else if (v == view.titleBar.layout_back) {
			((Activity) mContext).finish();
			// Utils.postLeftToRight(mContext);
		} else if (v == view.titleBar.layout_save) {
			if (composeType == DatabaseHelper.EXISTED) {
				// editSchedule();
				// Utils.isNetworkAvailable(editScheduleHandle);
				if (Utils.isNetworkOnline(mContext)) {
					// code if connected
					editSchedule();
				} else {
					final ToastDialog dialog = new ToastDialog(mContext,
							mContext.getResources().getString(
									R.string.no_network));
					dialog.show();
					dialog.btnOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}
			} else if (composeType == DatabaseHelper.NEW) {
				// createNewSchedule();
				// Utils.isNetworkAvailable(createNewScheduleHandle);
				if (Utils.isNetworkOnline(mContext)) {
					// code if connected
					createNewSchedule();
				} else {
					final ToastDialog dialog = new ToastDialog(mContext,
							mContext.getResources().getString(
									R.string.no_network));
					dialog.show();
					dialog.btnOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}
			}
		} else if (v == view.et_new_activity_name) {
			if (composeType == DatabaseHelper.NEW) {
				popUp();
			}
		} else if (v == view.et_new_activity_description) {
			SharedReference ref = new SharedReference();
			int owner_id = ref.getCurrentOwnerId(mContext);
			if (creator == owner_id) {
				Utils.openKeyboard(mContext, view.et_new_activity_description);
			}
			if (!view.et_new_activity_name.getText().toString().trim()
					.equals("")) {

				// show an activity to edit description
				// Intent intent = new Intent(mContext,
				// EditDescriptionActivity.class);
				// intent.putExtra(CommConstant.ACTIVITY_DESCRIPTION,
				// thisSchedule.getDesp());
				// startActivityForResult(intent, 0);
				// Utils.slideUpDown(mContext);
			} else {
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(
								R.string.select_activity_schedule));
				dialog.show();

				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		}

		else if (v == view.layout_onduty) {
			// else if (v == view.btn_change_on_duty) {
			if (!view.et_new_activity_name.getText().toString().trim()
					.equals("")) {
				Utils.pushRightToLeft(mContext);
				Intent intent = new Intent(mContext, ParticipantActivity.class);
				intent.putExtra(CommConstant.ACTIVITY_ID, activity_id);
				intent.putExtra(CommConstant.TYPE,
						CommConstant.TYPE_PARTICIPANT);
				intent.putExtra(CommConstant.SCHEDULE_ID,
						thisSchedule != null ? thisSchedule.getSchedule_ID()
								: -1);
				// List<Integer> pins = new ArrayList<Integer>();
				// if (composeType == DatabaseHelper.EXISTED) {
				// pins = dbHelper.getParticipantsForSchedule(thisSchedule
				// .getSchedule_ID());
				// }

				if (listPins != null && listPins.size() > 0) {
					ArrayList<String> listPinString = new ArrayList<String>();
					for (Confirm c : listPins) {
						listPinString.add(c.getMemberId() + ";"
								+ c.getConfirm());
					}
					intent.putStringArrayListExtra("pins", listPinString);
				}
				startActivityForResult(intent, REQUEST_CODE);
			} else {
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(
								R.string.select_activity_schedule));
				dialog.show();

				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}

		} else if (v == view.layoutTimeZone) {
			// if owner, can modify/delete else if is participant, only view
			// if (shared_role == CommConstant.OWNER) {
			// SharedReference ref = new SharedReference();
			// String time_zone = ref.getTimeZone(mContext);
			// Log.d("time zone", time_zone + "");
			// if (time_zone != null && (!time_zone.contains(";"))
			// && composeType == DatabaseHelper.NEW) {
			SharedReference ref = new SharedReference();
			int owner_id = ref.getCurrentOwnerId(mContext);

			// can create/modify/delete

			if ((composeType == DatabaseHelper.NEW) || (creator == owner_id)) {
				if (listTimezone != null && listTimezone.size() > 0) {
					popUp(listTimezone);
				} else {
					isShowPopupTimeZone = true;
					isShowPopupAlert = false;

					if (!CommConstant.DOWNLOAD_SETTING) {
						WebservicesHelper ws = WebservicesHelper.getInstance();
						ws.getServerSetting(mContext,iServerSetting,loadingInterface);
					}
				}
			}
			// }

		} else if (v == view.layoutAlert) {
			// && composeType == DatabaseHelper.NEW) {
			SharedReference ref = new SharedReference();
			int owner_id = ref.getCurrentOwnerId(mContext);

			// can create/modify/delete

			if ((composeType == DatabaseHelper.NEW) || (creator == owner_id)) {
				if (listAlert != null && listAlert.size() > 0) {
					popUpAlert(listAlert);
				} else {
					isShowPopupAlert = true;
					isShowPopupTimeZone = false;
					if (!CommConstant.DOWNLOAD_SETTING) {
						WebservicesHelper ws = WebservicesHelper.getInstance();
						ws.getServerSetting(mContext,iServerSetting,loadingInterface);
					}
				}
			}
		}
	}

	public void initViewValues() {
		try {

			listPins = new ArrayList<Confirm>();
			if (progress == null) {
				// display progress dialog like this
				progress = new ProgressDialog(mContext);
				progress.setCancelable(false);
				progress.setMessage(mContext.getResources().getString(
						R.string.processing));
			}
			listTimezone = DatabaseHelper.getSharedDatabaseHelper(mContext)
					.getTimeZone();
			listAlert = DatabaseHelper.getSharedDatabaseHelper(mContext)
					.getAlerts();
			if (composeType == DatabaseHelper.NEW) {
				Log.i("next service id", "is " + DatabaseHelper.getSharedDatabaseHelper(mContext).getNextActivityID());
				int tz = 0;
				String tzName = "UTC";
				if (listTimezone != null && listTimezone.size() > 0) {
					tz = listTimezone.get(0).getId();
					tzName = listTimezone.get(0).getTzname();
				}
				thisSchedule = new Schedule(
						new SharedReference().getCurrentOwnerId(mContext),
						DatabaseHelper.getSharedDatabaseHelper(mContext).getNextScheduleID(), activity_id == null ? "0"
								: activity_id,
						MyDate.transformLocalDateTimeToUTCFormat(tzName,
								MyDate.getCurrentDateTime(tzName)),
						MyDate.transformLocalDateTimeToUTCFormat(tzName,
								MyDate.getCurrentDateTime(tzName)), "",
						CommConstant.ALERT_DEFAULT, tz, tzName);
				view.btn_remove_schedule.setVisibility(View.GONE);
				view.titleBar.layout_next.setVisibility(View.GONE);
				view.titleBar.layout_save.setVisibility(View.VISIBLE);
				view.titleBar.tv_name.setText(mContext.getResources()
						.getString(R.string.add_schedule));
				schedule_id = thisSchedule.getSchedule_ID();

				DatabaseHelper dbHelper = DatabaseHelper
						.getSharedDatabaseHelper(mContext);
				final ArrayList<MyActivity> listActivity = dbHelper
						.getActivitiesOwnerOrOrganizer(new SharedReference()
								.getCurrentOwnerId(mContext) + "");

				// have no activity before and show popup
				if (listActivity == null || listActivity.size() == 0) {
					final ConfirmDialog dialog = new ConfirmDialog(mContext,
							mContext.getResources().getString(
									R.string.no_activity_for_create_schedule));
					dialog.show();
					dialog.btnOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							finish();
							Utils.postLeftToRight(mContext);
//							CategoryTabActivity.currentPage = CategoryTabActivity.TAB_ACTIVITY;
							// Intent intent=new Intent("goToActivity");
							// sendBroadcast(intent);

						}
					});
					dialog.btnCancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							finish();
						}
					});
				}

				/**
				 * timezone
				 * */

				if (listTimezone != null && listTimezone.size() > 0) {
					view.et_new_activity_time_zone.setText(listTimezone.get(0)
							.getDisplayname());
					view.layoutTimeZone.setClickable(true);
					try {
						thisSchedule.setTzid(listTimezone.get(0).getId());
						// view.layoutTimeZone.setClickable(false);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					// download from server, after download success set
					// first item to textview timezone

					if (!CommConstant.DOWNLOAD_SETTING) {
						WebservicesHelper ws = WebservicesHelper.getInstance();
						// ws.getTimezoneSetting();
						ws.getServerSetting(mContext,iServerSetting,loadingInterface);
					}
				}

				/**
				 * alert
				 * */

				if (listAlert != null && listAlert.size() > 0) {
					view.et_new_activity_alert.setText(listAlert.get(0)
							.getAname());

				} else {
					// download from server, after download success set
					// first item to textview timezone
					WebservicesHelper ws = WebservicesHelper.getInstance();
					// ws.getTimezoneSetting();
					ws.getServerSetting(mContext,new GetServerSettingInterface() {
						
						@Override
						public void onError(String error) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onComplete() {
							// TODO Auto-generated method stub
							
						}
					},new LoadingInterface() {
						
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							
						}
					});
				}

			} else if (composeType == DatabaseHelper.EXISTED) {

				if (schedule_id > 0) {
					thisSchedule = DatabaseHelper.getSharedDatabaseHelper(mContext).getScheduleSortedByID(schedule_id);
				}

				TimeZoneModel timeZoneModel = DatabaseHelper
						.getSharedDatabaseHelper(mContext).getTimeZone(
								thisSchedule.getTzid());
				if (timeZoneModel != null) {
					thisSchedule.setTzName(timeZoneModel.getTzname());
				}
				view.btn_remove_schedule.setVisibility(View.VISIBLE);
				view.titleBar.layout_next.setVisibility(View.GONE);
				view.titleBar.layout_save.setVisibility(View.VISIBLE);
				view.titleBar.tv_name.setText(mContext.getResources()
						.getString(R.string.edit_schedule));
				if (thisSchedule != null) {
					listPins = (ArrayList<Confirm>) DatabaseHelper.getSharedDatabaseHelper(mContext)
							.getParticipantsForSchedule(thisSchedule
									.getSchedule_ID());

//					if (listPins != null && listPins.size() > 0) {
//						// view.layout_on_duty.setVisibility(View.VISIBLE);
//						view.layout_onduty.setVisibility(View.VISIBLE);
//					}
					
					setParticipantOfActivity(listPins);
				}

			}

			/**
			 * timezone
			 * */
			int timeZone = thisSchedule.getTzid();
			TimeZoneModel currentTimeZone = DatabaseHelper
					.getSharedDatabaseHelper(mContext).getTimeZone(timeZone);
			if (currentTimeZone != null) {
				view.et_new_activity_time_zone.setText(currentTimeZone
						.getTzname());
			}

			/**
			 * alert
			 * */
			Alert alert = DatabaseHelper.getSharedDatabaseHelper(mContext)
					.getAlert(thisSchedule.getAlert());
			if (alert != null) {
				view.et_new_activity_alert.setText(alert.getAname());
			}

			view.et_new_activity_name.setText(myActivity != null ? myActivity
					.getActivity_name() : "");

//			String startdate = thisSchedule != null ? thisSchedule
//					.getStarttime() : "";

			// change starttime and endtime follow current timezone
			String startTime = thisSchedule.getStarttime();
			Log.d("start time init", startTime);
			String startDate = MyDate.getWeekdayUTCFromLocal(
					currentTimeZone.getTzname(), startTime);
			Log.d("start time timezone=", currentTimeZone.getTzname() + "/"
					+ startDate);
			view.et_startDate.setText(startDate);
			view.et_startTime.setText(MyDate.getTimeFromUTCToLocalTime(
					startTime, currentTimeZone.getTzname()));

			String endTime = thisSchedule.getEndtime();
			String endDate = MyDate.getWeekdayUTCFromLocal(
					currentTimeZone.getTzname(), endTime);
			view.et_endDate.setText(endDate);
			view.et_endTime.setText(MyDate.getTimeFromUTCToLocalTime(endTime,
					currentTimeZone.getTzname()));

			view.et_new_activity_description
					.setText(thisSchedule != null ? thisSchedule.getDesp() : "");

			SharedReference ref = new SharedReference();
			int owner_id = ref.getCurrentOwnerId(mContext);

			// can create/modify/delete
			if (creator == owner_id) {
				view.et_endDate.setEnabled(true);
				view.et_endTime.setEnabled(true);
				view.et_startDate.setEnabled(true);
				view.et_startTime.setEnabled(true);
				view.et_on_duty.setEnabled(true);
				view.et_new_activity_description.setEnabled(true);
				view.et_new_activity_description.setFocusableInTouchMode(true);
				view.titleBar.layout_save.setEnabled(true);
				view.titleBar.layout_next.setEnabled(true);
				view.layout_onduty.setEnabled(true);
				view.layout_onduty.setVisibility(View.VISIBLE);
			} else {
				// only view
				view.et_endDate.setEnabled(false);
				view.et_endTime.setEnabled(false);
				view.et_startDate.setEnabled(false);
				view.et_startTime.setEnabled(false);
				view.et_on_duty.setEnabled(false);
				view.et_new_activity_description.setEnabled(false);
				view.et_new_activity_name.setEnabled(false);
				view.btn_remove_schedule.setVisibility(View.GONE);
				view.titleBar.layout_save.setEnabled(false);
				view.titleBar.layout_next.setEnabled(false);
				view.layout_onduty.setEnabled(false);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setStartDate() {
		String[] startdatetime = MyDate.convertUTCDateToCustomTimezone(
				thisSchedule.getTzName(), MyDate.STANDARD,
				thisSchedule.getStarttime()).split(" ");
		String[] datecomponents = startdatetime[0].split("-");
		int year = Integer.valueOf(datecomponents[0]);
		int month = Integer.valueOf(datecomponents[1]) - 1;
		int day = Integer.valueOf(datecomponents[2]);
		DatePickerDialog dialog = new DatePickerDialog(this, this, year, month,
				day);
		dialog.setTitle("Set Start Date");
		// dialog.getDatePicker().setTag("startdate");
		dialog.show();
		StartOrEnd = START;
	}

	public void setEndDate() {
		String[] enddatetime = MyDate.convertUTCDateToCustomTimezone(
				thisSchedule.getTzName(), MyDate.STANDARD,
				thisSchedule.getEndtime()).split(" ");
		String[] datecomponents = enddatetime[0].split("-");
		int year = Integer.valueOf(datecomponents[0]);
		int month = Integer.valueOf(datecomponents[1]) - 1;
		int day = Integer.valueOf(datecomponents[2]);
		DatePickerDialog dialog = new DatePickerDialog(this, this, year, month,
				day);
		dialog.setTitle("Set End Date");
		// dialog.getDatePicker().setTag("enddate");
		dialog.show();
		StartOrEnd = END;
	}

	public void setStartTime() {
		Log.d(" set start time utc", thisSchedule.getStarttime());
		String[] startdatetime = MyDate
				.getTimeFromUTCToTimeZoneStartEnd(thisSchedule.getStarttime(),
						thisSchedule.getTzName()).replace("am", "")
				.replace("pm", "").replace("AM", "").replace("PM", "").trim()
				.split(":");

		int hour = Integer.valueOf(startdatetime[0]);
		int minute = Integer.valueOf(startdatetime[1]);
		TimePickerDialog dialog = new TimePickerDialog(this, this, hour,
				minute, false);
		dialog.setTitle("Set Start Time");
		dialog.show();
		StartOrEnd = START;
	}

	public void setEndTime() {
		String[] enddatetime = MyDate
				.getTimeFromUTCToTimeZoneStartEnd(thisSchedule.getEndtime(),
						thisSchedule.getTzName()).replace("am", "")
				.replace("pm", "").replace("AM", "").replace("PM", "").trim()
				.split(":");
		int hour = Integer.valueOf(enddatetime[0]);
		int minute = Integer.valueOf(enddatetime[1]);
		TimePickerDialog dialog = new TimePickerDialog(this, this, hour,
				minute, false);
		dialog.setTitle("Set End Time");
		dialog.show();
		StartOrEnd = END;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		int month = monthOfYear + 1;
		String monthstr = null;
		String daystr = null;
		if (month < 10)
			monthstr = "0" + month;
		else
			monthstr = String.valueOf(month);

		if (dayOfMonth < 10)
			daystr = "0" + dayOfMonth;
		else
			daystr = String.valueOf(dayOfMonth);
		String hourMinute = "";
		if (StartOrEnd == START) {
			hourMinute = this.view.et_startTime.getText().toString().trim();

		} else {
			hourMinute = this.view.et_endTime.getText().toString().trim();
		}
//		String amPm="am";
//		if(hourMinute.contains("PM"))
//		{
//			amPm="pm";
//		}
		hourMinute = hourMinute.replace("AM", "").replace("PM", "");
		hourMinute = hourMinute.trim() + ":00";
//		hourMinute+=" "+amPm;
		Log.d("hourMinute", hourMinute);
		String weekday = MyDate.getWeekdayTimeZone(year + "-" + monthstr + "-"
				+ daystr + " "
				+ (hourMinute.equals("") ? "00:00:00" : hourMinute));
Log.d("weekday",weekday);
		String fulldate = weekday;
		if (StartOrEnd == START) {
			this.view.et_startDate.setText(fulldate);
			thisSchedule
					.setStarttime(MyDate.transformLocalDateTimeToUTCFormat(
							thisSchedule.getTzName(),
							year
									+ "-"
									+ monthstr
									+ "-"
									+ daystr
									+ " "
									+ MyDate.convertUTCDateToCustomTimezone(
											thisSchedule.getTzName(),
											MyDate.STANDARD,
											thisSchedule.getStarttime()).split(
											" ")[1]));

		} else {

			this.view.et_endDate.setText(fulldate);
			thisSchedule
					.setEndtime(MyDate.transformLocalDateTimeToUTCFormat(
							thisSchedule.getTzName(),
							year
									+ "-"
									+ monthstr
									+ "-"
									+ daystr
									+ " "
									+ MyDate.convertUTCDateToCustomTimezone(
											thisSchedule.getTzName(),
											MyDate.STANDARD,
											thisSchedule.getStarttime()).split(
											" ")[1]));
		}
		if (MyDate.IsFirstDateLaterThanSecondDate(
				this.thisSchedule.getStarttime(),
				this.thisSchedule.getEndtime())) {
			this.thisSchedule.setEndtime(this.thisSchedule.getStarttime());
			this.view.et_endDate.setText(this.view.et_startDate.getText());
			this.view.et_endTime.setText(this.view.et_startTime.getText());
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onTimeSet(TimePicker arg0, int hour, int minute) {
		// TODO Auto-generated method stub
		String hourstr = String.valueOf(hour);
		Log.d("hour set", hour + "");
		if (hour < 10)
			hourstr = "0" + hourstr;
		String minutestr = String.valueOf(minute);
		if (minute < 10)
			minutestr = "0" + minutestr;
		String time = MyDate.getTimeFromTimeZone("0000-00-00 " + hourstr + ":"
				+ minutestr + ":" + "00");

		if (StartOrEnd == START) {
			this.view.et_startTime.setText(time);
			String startDate = view.et_startDate.getText().toString().trim();
			startDate = MyDate.getDateFromUTCToTimeZone(startDate,
					thisSchedule.getTzName());
			String startTime = startDate + " " + hourstr + ":" + minutestr
					+ ":" + "00";
			thisSchedule.setStarttime(MyDate.getTimeFromTimeZoneToUTC(
					startTime, thisSchedule.getTzName()));
			Log.d("start time set utc", thisSchedule.getStarttime());

		} else {

			this.view.et_endTime.setText(time);
			String endDate = view.et_startDate.getText().toString().trim();
			endDate = MyDate.getDateFromUTCToTimeZone(endDate,
					thisSchedule.getTzName());
			String endTime = endDate + " " + hourstr + ":" + minutestr + ":"
					+ "00";
			thisSchedule.setEndtime(MyDate.getTimeFromTimeZoneToUTC(endTime,
					thisSchedule.getTzName()));

			if (MyDate.IsFirstDateLaterThanSecondDate(
					this.thisSchedule.getStarttime(),
					this.thisSchedule.getEndtime())) {
				this.thisSchedule.setEndtime(this.thisSchedule.getStarttime());
				this.view.et_endDate.setText(this.view.et_startDate.getText());
				this.view.et_endTime.setText(this.view.et_startTime.getText());

			} else {

			}
		}
		if (MyDate.IsFirstDateLaterThanSecondDate(
				this.thisSchedule.getStarttime(),
				this.thisSchedule.getEndtime())) {
			this.thisSchedule.setEndtime(this.thisSchedule.getStarttime());
			this.view.et_endDate.setText(this.view.et_startDate.getText());
			this.view.et_endTime.setText(this.view.et_startTime.getText());

		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		thisSchedule.setService_ID(id + "");
		String name = item.getTitle().toString();
		view.et_new_activity_name.setText(name);
		return false;
	}

	/**
	 * Check&set data, return true if valid else return false
	 * */
	private boolean checkAndSetData() {
		if (myActivity == null) {
			final ToastDialog dialog = new ToastDialog(mContext, mContext
					.getResources()
					.getString(R.string.select_activity_schedule));
			dialog.show();

			dialog.btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			return false;
		}
		if (thisSchedule.getTzid() == 0) {
			final ToastDialog dialog = new ToastDialog(mContext, mContext
					.getResources().getString(R.string.please_select_time_zone));
			dialog.show();

			dialog.btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			return false;
		}
		thisSchedule.setDesp(view.et_new_activity_description.getText()
				.toString());
		thisSchedule.setService_ID(myActivity.getActivity_ID());
		return true;
	}

	private void insertToLocalDatabase() {
		ContentValues cv = new ContentValues();
		cv.put(ScheduleTable.own_ID, thisSchedule.getOwner_ID());
		cv.put(ScheduleTable.schedule_ID, thisSchedule.getSchedule_ID());
		cv.put(ScheduleTable.last_Modified, "upload");
		cv.put(ScheduleTable.start_Time, thisSchedule.getStarttime());
		cv.put(ScheduleTable.schedule_Description, thisSchedule.getDesp());
		cv.put(ScheduleTable.end_Time, thisSchedule.getEndtime());
		cv.put(ScheduleTable.service_ID, thisSchedule.getService_ID());
		cv.put(ScheduleTable.is_Deleted, 0);
		cv.put(ScheduleTable.is_Synchronized, 0);
		cv.put(ScheduleTable.user_login,
				new SharedReference().getCurrentOwnerId(mContext));
		cv.put(ScheduleTable.alert, thisSchedule.getAlert());
		cv.put(ScheduleTable.timeZone, thisSchedule.getTzid());
		DatabaseHelper.getSharedDatabaseHelper(mContext).insertSchedule(cv);

		ContentValues contentValues = new ContentValues();
		contentValues.put(ActivityTable.alertId, thisSchedule.getAlert());
		contentValues.put(ActivityTable.timeZoneId, thisSchedule.getTzid());
		// contentValues.put(ActivityTable.service_ID,
		// thisSchedule.getService_ID());
		DatabaseHelper.getSharedDatabaseHelper(mContext).updateActivity(thisSchedule.getService_ID(), contentValues);
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

		
LoadingInterface loadingInterface=new LoadingInterface() {
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		showLoading(CreateNewScheduleActivity.this);
	}
	
	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		dimissDialog();
	}
};
		
		
	/**
	 * Create new schedule
	 * */
	private void createNewSchedule() {
		if (checkAndSetData() && (composeType == DatabaseHelper.NEW)) {
			if (listPins != null) {
				int size = listPins.size();
				if (size > 0) {
					insertToLocalDatabase();
					for (int i = 0; i < size; i++) {

						Confirm pin = listPins.get(i);
						if (pin != null) {

							ContentValues onduty = new ContentValues();
							onduty.put(OndutyTable.schedule_ID,
									thisSchedule.getSchedule_ID());

							onduty.put(OndutyTable.service_ID,
									thisSchedule.getService_ID());
							onduty.put(OndutyTable.participant_ID,
									pin.getMemberId());
							onduty.put(OndutyTable.confirm, pin.getConfirm());
							onduty.put(OndutyTable.last_Modified, "");
							onduty.put(OndutyTable.is_Deleted, 0);
							onduty.put(OndutyTable.is_Synchronized, 0);
							DatabaseHelper.getSharedDatabaseHelper(mContext).insertOnduty(onduty);
						}
					}
					WebservicesHelper ws = WebservicesHelper.getInstance();
					ws.addSchedule(mContext,thisSchedule, listPins,loadingInterface,iSchedule);
				} else {
					popUpNoParticipant();
				}
			} else {
				popUpNoParticipant();
			}

		}

	}

	AddUpdateScheduleInterface iSchedule=new AddUpdateScheduleInterface() {
		
		@Override
		public void onError(String error) {
			final ToastDialog dialog = new ToastDialog(
					mContext, error);
			dialog.show();
			dialog.btnOk
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(
								View v) {
							dialog.dismiss();
						}
					});
		}
		
		@Override
		public void onComplete() {
			 ((Activity) mContext).finish();
			 Utils.postLeftToRight(mContext);
		}
	};
	/**
	 * Pop up without participant
	 * */
	private void popUpNoParticipant() {
		String title = "";
		if (composeType == DatabaseHelper.NEW) {
			title = mContext.getResources().getString(
					R.string.schedule_without_participant);
		} else if (composeType == DatabaseHelper.EXISTED) {
			title = mContext.getResources().getString(
					R.string.edit_schedule_without_participant);
		}
		final ConfirmDialog dialog = new ConfirmDialog(mContext, title);
		dialog.show();
		dialog.btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (composeType == DatabaseHelper.NEW) {
					insertToLocalDatabase();
					WebservicesHelper ws =WebservicesHelper.getInstance();
					ws.addSchedule(mContext,thisSchedule, listPins,loadingInterface,iSchedule);
				} else if (composeType == DatabaseHelper.EXISTED) {
					editScheduleLocalDatabase();
					WebservicesHelper ws = WebservicesHelper.getInstance();
					ws.updateSchedule(mContext,thisSchedule, listPins,loadingInterface,iSchedule);
				}

			}
		});
		dialog.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				// finish();
			}
		});
	}

	/**
	 * Return popup time zone
	 * */
	public void popUp(final ArrayList<TimeZoneModel> array) {
		TextBaseAdapter adapter = new TextBaseAdapter(mContext, array);
		String title = getResources().getString(R.string.time_zone);
		final PopupDialog dialog = new PopupDialog(mContext, title);
		dialog.show();
		dialog.list_item.setAdapter(adapter);
		dialog.list_item.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				SharedReference ref = new SharedReference();
				ref.setTimeZone(mContext, array.get(position).getId() + ";"
						+ array.get(position).getDisplayname());
				String displayName = array.get(position).getTzname();
				try {
					int index = displayName.indexOf(")");
					displayName = displayName.substring(index + 1,
							displayName.length());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				view.et_new_activity_time_zone.setText(displayName);

//				int timeZoneId = thisSchedule.getTzid();
//				TimeZoneModel lastTimeZone = DatabaseHelper
//						.getSharedDatabaseHelper(mContext).getTimeZone(
//								timeZoneId);
				TimeZoneModel currentTimeZone = array.get(position);
				thisSchedule.setTzid(array.get(position).getId());
				thisSchedule.setTzName(array.get(position).getTzname());
				dialog.dismiss();

				// change starttime and endtime follow current timezone
				String startTime = thisSchedule.getStarttime();
				String startDate = MyDate.getWeekdayUTCFromLocal(
						currentTimeZone.getTzname(), startTime);
				view.et_startDate.setText(startDate);
				view.et_startTime.setText(MyDate.getTimeFromUTCToLocalTime(
						startTime, currentTimeZone.getTzname()));

				String endTime = thisSchedule.getEndtime();
				String endDate = MyDate.getWeekdayUTCFromLocal(
						currentTimeZone.getTzname(), endTime);
				view.et_endDate.setText(endDate);
				view.et_endTime.setText(MyDate.getTimeFromUTCToLocalTime(
						endTime, currentTimeZone.getTzname()));

			}
		});

	}

	/**
	 * Return popup alert
	 * */
	public void popUpAlert(final ArrayList<Alert> array) {
		AlertTextBaseAdapter adapter = new AlertTextBaseAdapter(mContext, array);
		String title = getResources().getString(R.string.alert);
		final PopupDialog dialog = new PopupDialog(mContext, title);
		dialog.show();
		dialog.list_item.setAdapter(adapter);
		dialog.list_item.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				thisSchedule.setAlert(array.get(position).getId());
				view.et_new_activity_alert.setText(array.get(position)
						.getAname());
				dialog.dismiss();
			}
		});

	}

	private void editScheduleLocalDatabase() {
		ContentValues cv = new ContentValues();
		cv.put(ScheduleTable.start_Time, thisSchedule.getStarttime());
		cv.put(ScheduleTable.schedule_Description, thisSchedule.getDesp());
		cv.put(ScheduleTable.end_Time, thisSchedule.getEndtime());
		cv.put(ScheduleTable.service_ID, thisSchedule.getService_ID());
		cv.put(ScheduleTable.is_Deleted, 0);
		cv.put(ScheduleTable.is_Synchronized, 0);
		cv.put(ScheduleTable.timeZone, thisSchedule.getTzid());
		cv.put(ScheduleTable.alert, thisSchedule.getAlert());
		DatabaseHelper.getSharedDatabaseHelper(mContext).updateSchedule(thisSchedule.getSchedule_ID(), cv);
		DatabaseHelper.getSharedDatabaseHelper(mContext).deleteRelatedOnduty(thisSchedule.getSchedule_ID());

		ContentValues contentValues = new ContentValues();
		contentValues.put(ActivityTable.alertId, thisSchedule.getAlert());
		contentValues.put(ActivityTable.timeZoneId, thisSchedule.getTzid());
		// contentValues.put(ActivityTable.service_ID,
		// thisSchedule.getService_ID());
		DatabaseHelper.getSharedDatabaseHelper(mContext).updateActivity(thisSchedule.getService_ID(), contentValues);
	}

	/**
	 * Edit schedule
	 * */
	private void editSchedule() {
		if (checkAndSetData() && (composeType == DatabaseHelper.EXISTED)) {
			if (listPins != null) {
				int size = listPins.size();
				if (size > 0) {
					editScheduleLocalDatabase();
					for (int i = 0; i < size; i++) {
						ContentValues onduty = new ContentValues();
						onduty.put(OndutyTable.schedule_ID,
								thisSchedule.getSchedule_ID());
						onduty.put(OndutyTable.service_ID,
								thisSchedule.getSchedule_ID());
						onduty.put(OndutyTable.participant_ID, listPins.get(i)
								.getMemberId());
						onduty.put(OndutyTable.confirm, listPins.get(i)
								.getConfirm());
						onduty.put(OndutyTable.last_Modified, "");
						onduty.put(OndutyTable.is_Deleted, 0);
						onduty.put(OndutyTable.is_Synchronized, 0);
						DatabaseHelper.getSharedDatabaseHelper(mContext).insertOnduty(onduty);
					}
					WebservicesHelper ws = WebservicesHelper.getInstance();
					ws.updateSchedule(mContext,thisSchedule, listPins,loadingInterface,iSchedule);
				} else {
					popUpNoParticipant();
				}
			} else {
				popUpNoParticipant();
			}

		}
	}

	JsonHttpResponseHandler deleteScheduleHandler = new JsonHttpResponseHandler() {
		// public void onSuccess(JSONObject response) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			// TODO Auto-generated method stub
			super.onSuccess(statusCode, headers, response);
			try {
				if (response.getString("lastmodified") != null) {

					DatabaseHelper.getSharedDatabaseHelper(mContext).deleteRelatedOnduty(schedule_id);
					DatabaseHelper.getSharedDatabaseHelper(mContext).deleteSchedule(schedule_id);
					Log.i("delete schedule", "successfully");
					finish();
					Utils.postLeftToRight(mContext);
					Intent intent = new Intent(
							CommConstant.DELETE_SCHEDULE_COMPLETE);
					mContext.sendBroadcast(intent);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			progress.show();
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			super.onFinish();
			try {
				if (progress.isShowing()) {
					progress.dismiss();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String response, Throwable throwable) {
			// TODO Auto-generated method stub
			super.onFailure(statusCode, headers, response, throwable);
			try {
				if (progress.isShowing()) {
					progress.dismiss();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			final ToastDialog dialog = new ToastDialog(mContext, mContext
					.getResources().getString(R.string.delete_schedule_error)
					+ "\n" + response.toString());
			dialog.show();
			dialog.btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}
	};

	/**
	 * delete schedule update to database
	 * */
	public void deleteSchedule() {

		final ConfirmDialog dialog = new ConfirmDialog(mContext, mContext
				.getResources().getString(R.string.delete_schedule));
		dialog.show();
		dialog.btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					ContentValues scv = new ContentValues();
					scv.put(ScheduleTable.is_Deleted, 1);
					scv.put(ScheduleTable.is_Synchronized, 0);

					int schedule_id = thisSchedule.getSchedule_ID();
					DatabaseHelper.getSharedDatabaseHelper(mContext).updateSchedule(schedule_id, scv);
					List<Integer> onduties = DatabaseHelper.getSharedDatabaseHelper(mContext)
							.getOndutyRecordsForSchedule(schedule_id);
					for (int j = 0; j < onduties.size(); j++) {
						ContentValues ocv = new ContentValues();
						ocv.put(OndutyTable.is_Deleted, 1);
						ocv.put(OndutyTable.is_Synchronized, 0);
						int onduty_id = onduties.get(j);
						DatabaseHelper.getSharedDatabaseHelper(mContext).updateSchedule(onduty_id, ocv);
					}

					WebservicesHelper ws = WebservicesHelper.getInstance();
					ws.deleteSchedule(mContext,thisSchedule,iSchedule,loadingInterface);

					dialog.dismiss();

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		dialog.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 333) {
			String activity_id = data.getStringExtra(CommConstant.ACTIVITY_ID);
			if (activity_id.equalsIgnoreCase(this.activity_id)) {
				ArrayList<String> listPinString = data
						.getStringArrayListExtra(CommConstant.ON_DUTY_ITEM_SELECTED);
				listPins = new ArrayList<Confirm>();
				if (listPinString != null) {
					int size = listPinString.size();
					if (size > 0) {
						for (String pin : listPinString) {
							if (pin != null && (!pin.equals(""))) {
								String[] pins = pin.split(";");
								if (pins != null && (pins.length >= 2)) {
									listPins.add(new Confirm(Integer
											.parseInt(pins[0]), Integer
											.parseInt(pins[1])));
								}
							}
						}
					}

				}

				// String members = "";
				// if (pins != null && pins.size() > 0) {
				// for (int i = 0; i < pins.size(); i++) {
				// Sharedmember p = dbHelper.getSharedmember(pins.get(i),
				// activity_id);
				// if (p != null) {
				// if (i == 0)
				// members = members + p.getName();
				// else
				// members = members + "," + p.getName();
				// }
				// }
				// }
				// view.et_on_duty.setText(members);

				setParticipantOfActivity(listPins);
			}
		} else if (resultCode == 222) {
			String activity_decription = data
					.getStringExtra(CommConstant.ACTIVITY_DESCRIPTION);
			view.et_new_activity_description
					.setText(activity_decription != null ? activity_decription
							: view.et_new_activity_description.getText()
									.toString());
		}
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Utils.hideKeyboard(CreateNewScheduleActivity.this,
				view.et_new_activity_description);
	}
}
