package com.e2wstudy.cschedule;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.e2wstudy.cschedule.adapter.ActivityNameAdapter;
import com.e2wstudy.cschedule.adapter.AlertTextBaseAdapter;
import com.e2wstudy.cschedule.adapter.SharedMemberAdapter;
import com.e2wstudy.cschedule.adapter.TextBaseAdapter;
import com.e2wstudy.cschedule.db.DatabaseHelper;
import com.e2wstudy.cschedule.models.Alert;
import com.e2wstudy.cschedule.models.Confirm;
import com.e2wstudy.cschedule.models.MyActivity;
import com.e2wstudy.cschedule.models.OndutyTable;
import com.e2wstudy.cschedule.models.Schedule;
import com.e2wstudy.cschedule.models.ScheduleTable;
import com.e2wstudy.cschedule.models.Sharedmember;
import com.e2wstudy.cschedule.models.TimeZoneModel;
import com.e2wstudy.cschedule.net.WebservicesHelper;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.MyDate;
import com.e2wstudy.cschedule.utils.SharedReference;
import com.e2wstudy.cschedule.utils.Utils;
import com.e2wstudy.cschedule.views.AddScheduleView;
import com.e2wstudy.cschedule.views.ConfirmDialog;
import com.e2wstudy.cschedule.views.PopupDialog;
import com.e2wstudy.cschedule.views.ToastDialog;
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
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private DatabaseHelper dbHelper;
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

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mContext = this;
		view = new AddScheduleView(mContext);
		this.setContentView(view.layout);
		view.et_new_activity_description.requestFocus();
		Intent myIntent = this.getIntent();
		composeType = myIntent.getIntExtra(CommConstant.TYPE, -1);
		activity_id = myIntent.getStringExtra(CommConstant.ACTIVITY_ID);

		dbHelper = DatabaseHelper.getSharedDatabaseHelper(this);

		creator = new SharedReference().getCurrentOwnerId(mContext);
		// view.et_new_activity_description.setFocusable(false);
		if (activity_id != null && (!activity_id.equals(""))) {
			myActivity = dbHelper.getActivity(activity_id);
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
		try {
			// registerReceiver(getTimeZoneComplete, new IntentFilter(
			// CommConstant.TIMEZONE_DOWNLOAD_SUCCESSFULLY));
			registerReceiver(getServerSettingComplete, new IntentFilter(
					CommConstant.ALERT_DOWNLOAD_SUCCESSFULLY));

			registerReceiver(updateSchedule, new IntentFilter(
					CommConstant.CHANGE_CONFIRM_STATUS_SUCCESSFULLY));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	Handler createNewScheduleHandle = new Handler() {

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
				createNewSchedule();
			}

		}
	};

	Handler editScheduleHandle = new Handler() {

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
				editSchedule();
			}

		}
	};

	Handler deleteScheduleHandle = new Handler() {

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
				deleteSchedule();
			}

		}
	};

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
				activity_id = myActivity.getActivity_ID();
				view.et_new_activity_name.setText(myActivity.getActivity_name());
				dialog.dismiss();

			}
		});

	}

	//
	// BroadcastReceiver getTimeZoneComplete = new BroadcastReceiver() {
	// public void onReceive(Context arg0, Intent arg1) {
	// listTimezone = DatabaseHelper.getSharedDatabaseHelper(mContext)
	// .getTimeZone();
	// if (listTimezone != null && listTimezone.size() > 0) {
	// view.et_new_activity_time_zone.setText(listTimezone.get(0)
	// .getDisplayname());
	// view.layoutTimeZone.setClickable(true);
	// if (isShowPopupTimeZone) {
	// popUp(listTimezone);
	// }
	// }
	// }
	// };

	// BroadcastReceiver getAlertComplete = new BroadcastReceiver() {
	// public void onReceive(Context arg0, Intent arg1) {
	// listAlert = DatabaseHelper.getSharedDatabaseHelper(mContext)
	// .getAlerts();
	// if (listAlert != null && listAlert.size() > 0) {
	// view.et_new_activity_alert.setText(listAlert.get(0).getAname());
	// if (isShowPopupAlert) {
	// popUpAlert(listAlert);
	// }
	// }
	// }
	// };
	BroadcastReceiver updateSchedule = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {

			initViewValues();
		}

	};
	BroadcastReceiver getServerSettingComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			listAlert = DatabaseHelper.getSharedDatabaseHelper(mContext)
					.getAlerts();
			listTimezone = DatabaseHelper.getSharedDatabaseHelper(mContext)
					.getTimeZone();
			if (listAlert != null && listAlert.size() > 0) {
				view.et_new_activity_alert.setText(listAlert.get(0).getAname());
				if (isShowPopupTimeZone) {
					popUp(listTimezone);
				}
			}
			if (listTimezone != null && listTimezone.size() > 0) {
				view.et_new_activity_alert.setText(listAlert.get(0).getAname());
				if (isShowPopupAlert) {
					popUpAlert(listAlert);
				}
			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			// unregisterReceiver(getTimeZoneComplete);
			// unregisterReceiver(getAlertComplete);
			unregisterReceiver(getServerSettingComplete);
			unregisterReceiver(updateSchedule);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			// unregisterReceiver(deleteScheduleComplete);
			// unregisterReceiver(onDutyComplete);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

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
			DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);
			ArrayList<Sharedmember> list_participant = new ArrayList<Sharedmember>();

			if (pins != null && pins.size() > 0) {
				for (int i = 0; i < pins.size(); i++) {
					int member_id = pins.get(i).getMemberId();
					Sharedmember p = dbHelper.getSharedmember(member_id,
							activity_id);
					if (p != null) {
						p.isChecked = true;
						list_participant.add(p);
						String currentParticipant = new SharedReference()
								.getEmail(mContext);
						String email = p.getEmail();

						// if user login is not owner
						if (currentParticipant.equals(email)) {
							int confirm = pins.get(i).getConfirm();
							switch (confirm) {
							case CommConstant.CONFIRM_CONFIRMED:
								view.btnConfirm.setVisibility(View.GONE);
								view.btnDeny.setVisibility(View.VISIBLE);
								break;
							case CommConstant.CONFIRM_DENIED:
								view.btnDeny.setVisibility(View.GONE);
								view.btnConfirm.setVisibility(View.VISIBLE);
								break;
							case CommConstant.CONFIRM_UNKNOWN:
								view.btnConfirm.setVisibility(View.VISIBLE);
								view.btnDeny.setVisibility(View.VISIBLE);
								break;
							default:
								view.btnConfirm.setVisibility(View.GONE);
								view.btnDeny.setVisibility(View.GONE);
								break;
							}
						}
						if (p.getRole() == CommConstant.OWNER) {
							view.btnConfirm.setVisibility(View.GONE);
							view.btnDeny.setVisibility(View.GONE);
						}
					}

				}
			}

			if (list_participant != null && list_participant.size() > 0) {
				final SharedMemberAdapter adapter = new SharedMemberAdapter(
						mContext, list_participant, false, false, true);
				view.list_participant.setAdapter(adapter);
				Utils.setListViewHeightBasedOnChildren(view.list_participant,
						adapter);
				view.list_participant.setVisibility(View.VISIBLE);
				view.layout_list_on_duty.setVisibility(View.VISIBLE);
				view.layout_on_duty.setVisibility(View.VISIBLE);
				view.tv_participant.setVisibility(View.VISIBLE);
				view.list_participant
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, final int position, long id) {
								final Sharedmember participantSelected = adapter.sharedMembers
										.get(position);
								// participantInforDialog(participantSelected);

							}
						});
			}

			else {
				view.list_participant.setVisibility(View.GONE);
				view.layout_list_on_duty.setVisibility(View.GONE);
				view.layout_on_duty.setVisibility(View.GONE);
				view.tv_participant.setVisibility(View.GONE);
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
		view.btn_change_on_duty.setOnClickListener(this);
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

		else if (v == view.btnConfirm) {
			WebservicesHelper ws = new WebservicesHelper(mContext);
			try {
				int member_id = DatabaseHelper
						.getSharedDatabaseHelper(mContext)
						.getSharedMemberOfEmail(
								new SharedReference().getEmail(mContext));
				if (member_id != -1) {
					Confirm confirm = new Confirm(member_id,
							CommConstant.CONFIRM_CONFIRMED);
					ws.updateConfirmStatus(thisSchedule, confirm);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (v == view.btnDeny) {
			WebservicesHelper ws = new WebservicesHelper(mContext);
			try {

				int member_id = DatabaseHelper
						.getSharedDatabaseHelper(mContext)
						.getSharedMemberOfEmail(
								new SharedReference().getEmail(mContext));
				if (member_id != -1) {
					Confirm confirm = new Confirm(member_id,
							CommConstant.CONFIRM_DENIED);
					ws.updateConfirmStatus(thisSchedule, confirm);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (v == view.et_on_duty) {
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
			Utils.isNetworkAvailable(createNewScheduleHandle);
		} else if (v == view.btn_remove_schedule) {
			// deleteSchedule();
			Utils.isNetworkAvailable(deleteScheduleHandle);
		} else if (v == view.titleBar.layout_back) {
			((Activity) mContext).finish();
			// Utils.postLeftToRight(mContext);
		} else if (v == view.titleBar.layout_save) {
			if (composeType == DatabaseHelper.EXISTED) {
				// editSchedule();
				Utils.isNetworkAvailable(editScheduleHandle);
			} else if (composeType == DatabaseHelper.NEW) {
				// createNewSchedule();
				Utils.isNetworkAvailable(createNewScheduleHandle);
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

		else if (v == view.btn_change_on_duty) {
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
			SharedReference ref = new SharedReference();
			String time_zone = ref.getTimeZone(mContext);
			Log.d("time zone", time_zone + "");
			if (time_zone != null && (!time_zone.contains(";"))
					&& composeType == DatabaseHelper.NEW) {

				if (listTimezone != null && listTimezone.size() > 0) {
					popUp(listTimezone);
				} else {
					isShowPopupTimeZone = true;
					isShowPopupAlert = false;

					if (!CommConstant.DOWNLOAD_SETTING) {
						WebservicesHelper ws = new WebservicesHelper(mContext);
						// ws.getTimezoneSetting();
						ws.getServerSetting();
					}
				}
			}

		} else if (v == view.layoutAlert) {
			if (myActivity.getRole() == CommConstant.OWNER) {
				if (listAlert != null && listAlert.size() > 0) {
					popUpAlert(listAlert);
				} else {
					isShowPopupAlert = true;
					isShowPopupTimeZone = false;
					if (!CommConstant.DOWNLOAD_SETTING) {
						WebservicesHelper ws = new WebservicesHelper(mContext);
						// ws.getTimezoneSetting();
						ws.getServerSetting();
					}
				}
			}
		}
	}

	public void initViewValues() {
		try {
			// try {
			// TimeZone tz = TimeZone.getDefault();
			// String currentTimezoneName = tz.getDisplayName(false,
			// TimeZone.SHORT);
			// String timezoneCurrent = currentTimezoneName.substring(3, 6);
			// Log.d("currentTimezoneId", currentTimezoneName + "");
			// String currentTimeZone = tz.getID() + "-(" + currentTimezoneName
			// + ") ";
			// timezone_array[0] = currentTimeZone;// current device timezone
			// Log.d("timezone name", tz.getID() + "-(" + currentTimezoneName
			// + ") ");
			//
			// timezone_value_array[0] = timezoneCurrent;
			// } catch (Exception ex) {
			// ex.printStackTrace();
			// }

			listPins = new ArrayList<Confirm>();
			// if (pins != null) {
			// for (int i = 0; i < pins.size(); i++) {
			// listPins.add(pins.get(i).getMemberId() + ";"
			// + pins.get(i).getConfirm());
			// }
			// }

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
				Log.i("next service id", "is " + dbHelper.getNextActivityID());

				String timeZone = new SharedReference().getTimeZone(mContext);
				int tz = 0;
				if (timeZone != null && timeZone.contains(";")) {
					String[] timeZoneList = timeZone.split(";");
					if (timeZoneList != null && timeZoneList.length >= 2) {
						tz = Integer.parseInt(timeZoneList[0]);
					}
				}

				thisSchedule = new Schedule(
						new SharedReference().getCurrentOwnerId(mContext),
						dbHelper.getNextScheduleID(), activity_id == null ? "0"
								: activity_id,
						MyDate.transformLocalDateTimeToUTCFormat(MyDate
								.getCurrentDateTime()),
						MyDate.transformLocalDateTimeToUTCFormat(MyDate
								.getCurrentDateTime()), "",
						CommConstant.ALERT_DEFAULT, tz);
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
							CategoryTabActivity.currentPage = CategoryTabActivity.TAB_ACTIVITY;
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
				String timezone = new SharedReference().getTimeZone(mContext);
				if (timezone.contains(";")) {
					String[] timeZones = timezone.split(";");
					if (timeZones != null) {
						if (timeZones.length == 2) {
							view.et_new_activity_time_zone
									.setText(timeZones[1]);
							try {
								thisSchedule.setTzid(Integer
										.parseInt(timeZones[0]));
								// view.layoutTimeZone.setClickable(false);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				} else {

					if (listTimezone != null && listTimezone.size() > 0) {
						view.et_new_activity_time_zone.setText(listTimezone
								.get(0).getDisplayname());
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
							WebservicesHelper ws = new WebservicesHelper(
									mContext);
							// ws.getTimezoneSetting();
							ws.getServerSetting();
						}
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
					WebservicesHelper ws = new WebservicesHelper(mContext);
					// ws.getAlertSetting();
					ws.getServerSetting();
				}

			} else if (composeType == DatabaseHelper.EXISTED) {

				if (schedule_id > 0) {
					thisSchedule = dbHelper.getScheduleSortedByID(schedule_id);
				}
				view.btn_remove_schedule.setVisibility(View.VISIBLE);
				view.titleBar.layout_next.setVisibility(View.GONE);
				view.titleBar.layout_save.setVisibility(View.VISIBLE);
				view.titleBar.tv_name.setText(mContext.getResources()
						.getString(R.string.edit_schedule));
				if (thisSchedule != null) {
					listPins = (ArrayList<Confirm>) dbHelper
							.getParticipantsForSchedule(thisSchedule
									.getSchedule_ID());

					if (listPins != null && listPins.size() > 0) {
						view.layout_on_duty.setVisibility(View.VISIBLE);
					} else {
						view.layout_on_duty.setVisibility(View.GONE);
					}
					setParticipantOfActivity(listPins);
				}

			}

			/**
			 * timezone
			 * */
			int timeZone = thisSchedule.getTzid();
			TimeZoneModel timeZoneModel = DatabaseHelper
					.getSharedDatabaseHelper(mContext).getTimeZone(timeZone);
			if (timeZoneModel != null) {
				view.et_new_activity_time_zone.setText(timeZoneModel
						.getDisplayname());
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

			String startdate = thisSchedule != null ? thisSchedule
					.getStarttime() : "";
			String startfulldate = MyDate.getWeekdayFromUTCTime(startdate)
					+ ", " + MyDate.transformUTCTimeToCustomStyle(startdate);
			String starttime = MyDate.getTimeWithAPMFromUTCTime(startdate);
			view.et_startDate.setText(startfulldate);
			view.et_startTime.setText(starttime);

			String enddate = thisSchedule.getEndtime();
			String endfulldate = MyDate.getWeekdayFromUTCTime(enddate) + ", "
					+ MyDate.transformUTCTimeToCustomStyle(enddate);
			String endtime = MyDate.getTimeWithAPMFromUTCTime(enddate);
			view.et_endDate.setText(endfulldate);
			view.et_endTime.setText(endtime);

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
				// view.et_new_activity_name.setEnabled(true);
				// view.et_new_activity_name.setEnabled(false);
				// view.btn_remove_schedule.setVisibility(View.VISIBLE);
				view.titleBar.layout_save.setEnabled(true);
				view.titleBar.layout_next.setEnabled(true);
				view.btn_change_on_duty.setEnabled(true);
				view.btn_change_on_duty.setVisibility(View.VISIBLE);
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
				view.btn_change_on_duty.setEnabled(false);
				view.btn_change_on_duty.setVisibility(View.GONE);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setStartDate() {
		String[] startdatetime = MyDate.transformUTCDateToLocalDate(
				MyDate.STANDARD, thisSchedule.getStarttime()).split(" ");
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
		String[] enddatetime = MyDate.transformUTCDateToLocalDate(
				MyDate.STANDARD, thisSchedule.getEndtime()).split(" ");
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
		String[] startdatetime = MyDate.transformUTCDateToLocalDate(
				MyDate.STANDARD, thisSchedule.getStarttime()).split(" ");
		String[] timecomponents = startdatetime[1].split(":");
		int hour = Integer.valueOf(timecomponents[0]);
		int minute = Integer.valueOf(timecomponents[1]);
		TimePickerDialog dialog = new TimePickerDialog(this, this, hour,
				minute, true);
		dialog.setTitle("Set Start Time");
		dialog.show();
		StartOrEnd = START;
	}

	public void setEndTime() {
		String[] enddatetime = MyDate.transformUTCDateToLocalDate(
				MyDate.STANDARD, thisSchedule.getEndtime()).split(" ");
		String[] timecomponents = enddatetime[1].split(":");
		int hour = Integer.valueOf(timecomponents[0]);
		int minute = Integer.valueOf(timecomponents[1]);
		TimePickerDialog dialog = new TimePickerDialog(this, this, hour,
				minute, true);
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
		hourMinute = hourMinute.replace("AM", "").replace("PM", "");
		hourMinute = hourMinute.trim() + ":00";
		Log.d("hourMinute", hourMinute);
		String weekday = MyDate.getWeekdayFromUTCTime(MyDate
				.transformLocalDateTimeToUTCFormat(year + "-" + monthstr + "-"
						+ daystr + " "
						+ (hourMinute.equals("") ? "00:00:00" : hourMinute)));
		String customDate = MyDate.transformUTCTimeToCustomStyle(MyDate
				.transformLocalDateTimeToUTCFormat(year + "-" + monthstr + "-"
						+ daystr + " "
						+ (hourMinute.equals("") ? "00:00:00" : hourMinute)));
		String fulldate = weekday + ", " + customDate;
		if (StartOrEnd == START) {
			this.view.et_startDate.setText(fulldate);
			thisSchedule
					.setStarttime(MyDate.transformLocalDateTimeToUTCFormat(year
							+ "-"
							+ monthstr
							+ "-"
							+ daystr
							+ " "
							+ MyDate.transformUTCDateToLocalDate(
									MyDate.STANDARD,
									thisSchedule.getStarttime()).split(" ")[1]));

		} else {

			this.view.et_endDate.setText(fulldate);
			thisSchedule
					.setEndtime(MyDate.transformLocalDateTimeToUTCFormat(year
							+ "-"
							+ monthstr
							+ "-"
							+ daystr
							+ " "
							+ MyDate.transformUTCDateToLocalDate(
									MyDate.STANDARD,
									thisSchedule.getStarttime()).split(" ")[1]));
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
		if (hour < 10)
			hourstr = "0" + hourstr;
		String minutestr = String.valueOf(minute);
		if (minute < 10)
			minutestr = "0" + minutestr;
		String time = MyDate.getTimeWithAPMFromUTCTime(MyDate
				.transformLocalDateTimeToUTCFormat("0000-00-00 " + hourstr
						+ ":" + minutestr + ":" + "00"));
		if (StartOrEnd == START) {
			this.view.et_startTime.setText(time);
			thisSchedule.setStarttime(MyDate
					.transformLocalDateTimeToUTCFormat(MyDate
							.transformUTCDateToLocalDate(MyDate.STANDARD,
									thisSchedule.getStarttime()).split(" ")[0]
							+ " " + hourstr + ":" + minutestr + ":" + "00"));

		} else {
			this.view.et_endTime.setText(time);
			thisSchedule.setEndtime(MyDate
					.transformLocalDateTimeToUTCFormat(MyDate
							.transformUTCDateToLocalDate(MyDate.STANDARD,
									thisSchedule.getEndtime()).split(" ")[0]
							+ " " + hourstr + ":" + minutestr + ":" + "00"));
			if (MyDate.IsFirstDateLaterThanSecondDate(
					this.thisSchedule.getStarttime(),
					this.thisSchedule.getEndtime())) {
				this.thisSchedule.setEndtime(this.thisSchedule.getEndtime());
				this.view.et_endDate.setText(this.view.et_startDate.getText());
				this.view.et_startTime.setText(this.view.et_endTime.getText());

			} else {

			}
		}
		if (MyDate.IsFirstDateLaterThanSecondDate(
				this.thisSchedule.getStarttime(),
				this.thisSchedule.getEndtime())) {
			this.thisSchedule.setEndtime(this.thisSchedule.getEndtime());
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
		dbHelper.insertSchedule(cv);
	}

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
							dbHelper.insertOnduty(onduty);
						}
					}
					WebservicesHelper ws = new WebservicesHelper(mContext);
					ws.addSchedule(thisSchedule, listPins);
				} else {
					popUpNoParticipant();
				}
			} else {
				popUpNoParticipant();
			}

		}

	}

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
		final ConfirmDialog dialog = new ConfirmDialog(mContext,title);
		dialog.show();
		dialog.btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (composeType == DatabaseHelper.NEW) {
					insertToLocalDatabase();
					WebservicesHelper ws = new WebservicesHelper(mContext);
					ws.addSchedule(thisSchedule, listPins);
				} else if (composeType == DatabaseHelper.EXISTED) {
					editScheduleLocalDatabase();
					WebservicesHelper ws = new WebservicesHelper(mContext);
					ws.updateSchedule(thisSchedule, listPins);
				}

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
				view.et_new_activity_time_zone.setText(array.get(position)
						.getDisplayname());

				thisSchedule.setTzid(array.get(position).getId());
				dialog.dismiss();
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
		dbHelper.updateSchedule(thisSchedule.getSchedule_ID(), cv);
		dbHelper.deleteRelatedOnduty(thisSchedule.getSchedule_ID());
	}

	/**
	 * Edit schedule
	 * */
	private void editSchedule() {
		if (checkAndSetData() && (composeType == DatabaseHelper.EXISTED)) {
			// ContentValues cv = new ContentValues();
			// cv.put(ScheduleTable.start_Time, thisSchedule.getStarttime());
			// cv.put(ScheduleTable.schedule_Description,
			// thisSchedule.getDesp());
			// cv.put(ScheduleTable.end_Time, thisSchedule.getEndtime());
			// cv.put(ScheduleTable.service_ID, thisSchedule.getService_ID());
			// cv.put(ScheduleTable.is_Deleted, 0);
			// cv.put(ScheduleTable.is_Synchronized, 0);
			// cv.put(ScheduleTable.timeZone, thisSchedule.getTzid());
			// cv.put(ScheduleTable.alert, thisSchedule.getAlert());
			// dbHelper.updateSchedule(thisSchedule.getSchedule_ID(), cv);
			// dbHelper.deleteRelatedOnduty(thisSchedule.getSchedule_ID());
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
						dbHelper.insertOnduty(onduty);
					}
					WebservicesHelper ws = new WebservicesHelper(mContext);
					ws.updateSchedule(thisSchedule, listPins);
				} else {
					popUpNoParticipant();
				}
			} else {
				popUpNoParticipant();
			}

		}
	}

	JsonHttpResponseHandler deleteScheduleHandler = new JsonHttpResponseHandler() {
//		public void onSuccess(JSONObject response) {
		
		@Override
		public void onSuccess(int statusCode,
				Header[] headers, JSONObject response) {
			// TODO Auto-generated method stub
			super.onSuccess(statusCode, headers, response);
			try {
				if (response.getString("lastmodified") != null) {

					dbHelper.deleteRelatedOnduty(schedule_id);
					dbHelper.deleteSchedule(schedule_id);
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
		public void onFailure(int statusCode,
				Header[] headers, String response,
				Throwable throwable) {
			// TODO Auto-generated method stub
			super.onFailure(statusCode, headers,
					response, throwable);
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
					dbHelper.updateSchedule(schedule_id, scv);
					List<Integer> onduties = dbHelper
							.getOndutyRecordsForSchedule(schedule_id);
					for (int j = 0; j < onduties.size(); j++) {
						ContentValues ocv = new ContentValues();
						ocv.put(OndutyTable.is_Deleted, 1);
						ocv.put(OndutyTable.is_Synchronized, 0);
						int onduty_id = onduties.get(j);
						dbHelper.updateSchedule(onduty_id, ocv);
					}

					WebservicesHelper ws = new WebservicesHelper(mContext);
					ws.deleteSchedule(thisSchedule, deleteScheduleHandler);

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
