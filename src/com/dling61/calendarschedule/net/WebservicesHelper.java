/**
 * Develop by Antking
 * */
package com.dling61.calendarschedule.net;

import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dling61.calendarschedule.CategoryTabActivity;
import com.dling61.calendarschedule.LoginActivity;
import com.dling61.calendarschedule.R;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.ActivityTable;
import com.dling61.calendarschedule.models.MyActivity;
import com.dling61.calendarschedule.models.OndutyTable;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.models.ParticipantTable;
import com.dling61.calendarschedule.models.Schedule;
import com.dling61.calendarschedule.models.ScheduleTable;
import com.dling61.calendarschedule.models.SharedMemberTable;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.MyDate;
import com.dling61.calendarschedule.utils.SharedReference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * @class WebservicesHelper
 * @author Huyen Nguyen
 * @version 1.0
 * @Date: April 8th,2014 @ This class helps all process call webservices}
 * */
public class WebservicesHelper {
	private AsyncHttpClient client;
	Context mContext;

	// progress dialog
	ProgressDialog progress = null;
	DatabaseHelper dbHelper;

	/**
	 * Constructor initial progress dialog
	 * */
	public WebservicesHelper(Context context) {

		client = new AsyncHttpClient();
		client.setTimeout(10000);

		this.mContext = context;
		if (progress == null) {
			// display progress dialog like this
			progress = new ProgressDialog(mContext);
			progress.setCancelable(false);
			progress.setMessage(mContext.getResources().getString(
					R.string.processing));
		}
		dbHelper = DatabaseHelper.getSharedDatabaseHelper(context);
	}

	/**
	 * Create account If success, go to login else show toast notify create
	 * account failure
	 * */
	public void createAccount(final String email, final String password,
			String username, String mobile) {
		String signUpUrl = BaseUrl.BASEURL + "creator?action=register" + "&"
				+ BaseUrl.URL_POST_FIX;
		try {
			JSONObject jsonParams = new JSONObject();
			jsonParams.put(CommConstant.EMAIL, email);
			jsonParams.put(CommConstant.PASSWORD, password);
			jsonParams.put(CommConstant.USERNAME, username);
			jsonParams.put(CommConstant.MOBILE, mobile);

			client.addHeader("Content-type", "application/json");
			StringEntity entity = new StringEntity(jsonParams.toString());
			client.post(null, signUpUrl, entity, "application/json",
					new JsonHttpResponseHandler() {
						public void onSuccess(JSONObject response) {
							Log.i("successful response", response.toString());

							if (response != null) {
								Log.d("go there", "success not null");
								// TODO Auto-generated method stub
								try {
									if (!response.get("message")
											.toString().startsWith("200")) {
										Toast.makeText(
												mContext,
												response.get("error message")
														.toString(),
												Toast.LENGTH_LONG).show();
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								try {
									if (response.get(CommConstant.OWNER_ID) != null) {
										((Activity) mContext).finish();
										Intent intent = new Intent(mContext,
												LoginActivity.class);
										intent.putExtra(CommConstant.EMAIL,
												email);
										intent.putExtra(CommConstant.PASSWORD,
												password);
										mContext.startActivity(intent);
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

						public void onFailure(Throwable e, String response) {
							if (response != null) {
								Log.d("failure response", response.toString());
								Toast.makeText(
										mContext,
										mContext.getResources().getString(
												R.string.create_acc_failure)
												+ "\n" + response.toString(),
										Toast.LENGTH_LONG).show();
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
							progress.dismiss();
						}

					});

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * Login if success go to TabAcivity else show Toast notify login fail
	 * */
	public void login(final String email, final String password)
			throws JSONException {
		String signInUrl = BaseUrl.BASEURL + "creator?action=signin" + "&"
				+ BaseUrl.URL_POST_FIX;
		Log.i("sign in url is", signInUrl);
		try {
			JSONObject jsonParams = new JSONObject();
			jsonParams.put(CommConstant.PASSWORD, password);
			jsonParams.put(CommConstant.EMAIL, email);
			client.addHeader("Content-type", "application/json");
			StringEntity entity = new StringEntity(jsonParams.toString());
			client.post(null, signInUrl, entity, "application/json",
					new JsonHttpResponseHandler() {
						public void onSuccess(JSONObject response) {
							Log.i("successful response", response.toString());
							try {
								String username = response
										.getString("username");
								SharedReference sharedReference = new SharedReference();
								sharedReference.setUsername(mContext, username);

								sharedReference.setAccount(mContext,
										response.toString());
								int ownerid = response.getInt("ownerid");
								Log.i("SignIn ownerid ", ownerid + "");
								int idCostant = ownerid * 10000;

								// Check id
								int nextserviceidOriginal = response
										.getInt("serviceid");
								Log.i("nextserviceidOriginal",
										nextserviceidOriginal + "");
								int nextmemberidOriginal = response
										.getInt("memberid");
								Log.i("nextmemberidOriginal",
										nextmemberidOriginal + "");
								int nextscheduleidOriginal = response
										.getInt("scheduleid");
								Log.i("nextscheduleidOriginal",
										nextscheduleidOriginal + "");
								int nextserviceid = -1;
								// If it is the first time to establish an
								// activity
								if (nextserviceidOriginal == 0) {
									nextserviceid = idCostant
											+ nextserviceidOriginal + 1;

								}
								// If activities has established
								else {
									nextserviceid = nextserviceidOriginal + 1;
									Log.i("SignIn nextserviceid ",
											nextserviceid + "");
								}
								int nextmemberid = -1;
								// If it is the first time to establish a member
								if (nextmemberidOriginal == 0) {
									nextmemberid = idCostant
											+ nextmemberidOriginal + 1;
									Log.i("SignIn nextserviceid ", nextmemberid
											+ "");
								}
								// If members has established
								else {
									nextmemberid = nextmemberidOriginal + 1;
									Log.i("SignIn nextserviceid ", nextmemberid
											+ "");
								}
								int nextscheduleid = -1;
								// If it is the first time to establish a
								// schedule
								if (nextscheduleidOriginal == 0) {
									nextscheduleid = idCostant
											+ nextscheduleidOriginal + 1;
									Log.i("SignIn nextscheduleid ",
											nextscheduleid + "");
								} else {
									nextscheduleid = nextscheduleidOriginal + 1;
									Log.i("SignIn nextscheduleid ",
											nextscheduleid + "");
								}
								sharedReference.setInformationUserLogined(
										mContext, username, email, ownerid,
										nextserviceid, nextmemberid,
										nextscheduleid);

								uploadRecentEditedActivitiesToWeb();
								uploadRecentEditedParticipantsToWeb();
								uploadRecentNewActivitiesToWeb();
								uploadRecentNewParticipantsToWeb();
								uploadRecentNewSchedulesToWeb();

								((Activity) mContext).finish();
								Intent intent = new Intent(mContext,
										CategoryTabActivity.class);
								// Intent intent = new Intent(mContext,
								// HomeActivity.class);
								mContext.startActivity(intent);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						public void onFailure(Throwable e, String response) {
							Toast.makeText(
									mContext,
									mContext.getResources().getString(
											R.string.login_fail)
											+ "\n" + response.toString(),
									Toast.LENGTH_LONG).show();
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
							progress.dismiss();
						}
					});
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * parser activity
	 * */
	public void parseActivity(JSONObject response) {
		try {
			JSONArray deleted_services = response
					.getJSONArray("deletedservices");
			int deleted_services_count = deleted_services.length();
			if (deleted_services_count > 0) {
				for (int i = 0; i < deleted_services_count; i++) {
					String id = deleted_services.getString(i);
					List<Schedule> sbelongtoa = dbHelper
							.getSchedulesBelongtoActivity(id);
					for (int j = 0; j < sbelongtoa.size(); j++) {
						Schedule schedule = sbelongtoa.get(j);
						dbHelper.deleteRelatedOnduty(schedule.getSchedule_ID());
						dbHelper.deleteSchedule(schedule.getSchedule_ID());
					}
					dbHelper.deleteActivity(id);
				}
			}

			// services
			JSONArray services = response.getJSONArray("services");
			int service_count = services.length();

			for (int i = 0; i < service_count; i++) {
				JSONObject service = services.getJSONObject(i);
				ContentValues newActivity = new ContentValues();
				int ownid = service.getInt("creatorid");
				newActivity.put(ActivityTable.own_ID, ownid);
				Log.i("getActivitiesFromWeb own_ID ", ownid + "");
				String activityid = service.getString("serviceid");

				String serviceName = service.getString("servicename");
				newActivity.put(ActivityTable.service_Name, serviceName);
				Log.i("getActivitiesFromWeb service_Name ", serviceName + "");
				int role = service.getInt("sharedrole");
				newActivity.put(ActivityTable.sharedrole, role);
				Log.i("getActivitiesFromWeb sharedrole ", role + "");
				int alert = service.getInt("alert");
				newActivity.put(ActivityTable.alert, alert);
				Log.i("getActivitiesFromWeb alert ", alert + "");
				int repeat = service.getInt("repeat");
				newActivity.put(ActivityTable.repeat, repeat);
				Log.i("getActivitiesFromWeb repeat ", repeat + "");
				String starttime = service.getString("startdatetime");
				newActivity.put(ActivityTable.start_time, starttime);
				Log.i("getActivitiesFromWeb start_time ", starttime + "");
				String endtime = service.getString("enddatetime");
				newActivity.put(ActivityTable.end_time, endtime);
				Log.i("getActivitiesFromWeb end_time ", endtime + "");
				String description = service.getString("desp");
				newActivity.put(ActivityTable.service_description, description);
				Log.i("getActivitiesFromWeb service_description ", description
						+ "");
				int otc = new SharedReference().getTimeZone(mContext);
				newActivity.put(ActivityTable.otc_Offset, otc);
				int is_deleted = 0;
				newActivity.put(ActivityTable.is_Deleted, is_deleted);
				int is_synchronized = 1;
				newActivity.put(ActivityTable.is_Synchronized, is_synchronized);
				String last_modified = service.getString("lastmodified");
				newActivity.put(ActivityTable.last_ModifiedTime, last_modified);
				Log.i("getActivitiesFromWeb lastmodified ", last_modified + "");

				if (dbHelper.isActivityExisted(activityid) == false) {
					newActivity.put(ActivityTable.service_ID, activityid);
					Log.i("getActivitiesFromWeb service_ID ", activityid + "");
					if (dbHelper.insertActivity(newActivity))
						Log.i("database", "insert service " + serviceName
								+ " successfully!");
				} else {
					if (dbHelper.updateActivity(activityid, newActivity))
						Log.i("database", "update service " + serviceName
								+ " successfully!");
				}

			}

			// SEND broadcast to activity
			Intent intent = new Intent(CommConstant.ACTIVITY_DOWNLOAD_SUCCESS);
			mContext.sendBroadcast(intent);

			new SharedReference().setLastestServiceLastModifiedTime(mContext,
					MyDate.transformLocalDateTimeToUTCFormat(MyDate
							.getCurrentDateTime()));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * get all activity of current owner id
	 * */
	public void getAllActivitys(JsonHttpResponseHandler handler) {
		String activityUrl = BaseUrl.BASEURL + "services" + "?"
				+ BaseUrl.URL_POST_FIX;
		Log.i("url is :", activityUrl);
		final SharedReference ref = new SharedReference();
		int currentOwnerID = ref.getCurrentOwnerId(mContext);
		RequestParams params = new RequestParams();
		params.put(CommConstant.OWNER_ID, String.valueOf(currentOwnerID));
		params.put(CommConstant.LAST_UPDATE_TIME,
				ref.getLastestServiceLastModifiedTime(mContext));
		client.addHeader("Content-type", "application/json");
		client.get(activityUrl, params, handler);
		// client.get(activityUrl, params, new JsonHttpResponseHandler() {
		// public void onSuccess(JSONObject response) {
		// Log.i("successful response", response.toString());
		// try {
		// // deleted services and schedule relationship with this
		// // service
		// JSONArray deleted_services = response
		// .getJSONArray("deletedservices");
		// int deleted_services_count = deleted_services.length();
		// if (deleted_services_count > 0) {
		// for (int i = 0; i < deleted_services_count; i++) {
		// String id = deleted_services.getString(i);
		// List<Schedule> sbelongtoa = dbHelper
		// .getSchedulesBelongtoActivity(id);
		// for (int j = 0; j < sbelongtoa.size(); j++) {
		// Schedule schedule = sbelongtoa.get(j);
		// dbHelper.deleteRelatedOnduty(schedule
		// .getSchedule_ID());
		// dbHelper.deleteSchedule(schedule
		// .getSchedule_ID());
		// }
		// dbHelper.deleteActivity(id);
		// }
		// }
		//
		// // services
		// JSONArray services = response.getJSONArray("services");
		// int service_count = services.length();
		//
		// for (int i = 0; i < service_count; i++) {
		// JSONObject service = services.getJSONObject(i);
		// ContentValues newActivity = new ContentValues();
		// int ownid = service.getInt("creatorid");
		// newActivity.put(ActivityTable.own_ID, ownid);
		// Log.i("getActivitiesFromWeb own_ID ", ownid + "");
		// String activityid = service.getString("serviceid");
		//
		// String serviceName = service.getString("servicename");
		// newActivity
		// .put(ActivityTable.service_Name, serviceName);
		// Log.i("getActivitiesFromWeb service_Name ", serviceName
		// + "");
		// int role = service.getInt("sharedrole");
		// newActivity.put(ActivityTable.sharedrole, role);
		// Log.i("getActivitiesFromWeb sharedrole ", role + "");
		// int alert = service.getInt("alert");
		// newActivity.put(ActivityTable.alert, alert);
		// Log.i("getActivitiesFromWeb alert ", alert + "");
		// int repeat = service.getInt("repeat");
		// newActivity.put(ActivityTable.repeat, repeat);
		// Log.i("getActivitiesFromWeb repeat ", repeat + "");
		// String starttime = service.getString("startdatetime");
		// newActivity.put(ActivityTable.start_time, starttime);
		// Log.i("getActivitiesFromWeb start_time ", starttime
		// + "");
		// String endtime = service.getString("enddatetime");
		// newActivity.put(ActivityTable.end_time, endtime);
		// Log.i("getActivitiesFromWeb end_time ", endtime + "");
		// String description = service.getString("desp");
		// newActivity.put(ActivityTable.service_description,
		// description);
		// Log.i("getActivitiesFromWeb service_description ",
		// description + "");
		// int otc = new SharedReference().getTimeZone(mContext);
		// newActivity.put(ActivityTable.otc_Offset, otc);
		// int is_deleted = 0;
		// newActivity.put(ActivityTable.is_Deleted, is_deleted);
		// int is_synchronized = 1;
		// newActivity.put(ActivityTable.is_Synchronized,
		// is_synchronized);
		// String last_modified = service
		// .getString("lastmodified");
		// newActivity.put(ActivityTable.last_ModifiedTime,
		// last_modified);
		// Log.i("getActivitiesFromWeb lastmodified ",
		// last_modified + "");
		//
		// if (dbHelper.isActivityExisted(activityid) == false) {
		// newActivity.put(ActivityTable.service_ID,
		// activityid);
		// Log.i("getActivitiesFromWeb service_ID ",
		// activityid + "");
		// if (dbHelper.insertActivity(newActivity))
		// Log.i("database", "insert service "
		// + serviceName + " successfully!");
		// } else {
		// if (dbHelper
		// .updateActivity(activityid, newActivity))
		// Log.i("database", "update service "
		// + serviceName + " successfully!");
		// }
		//
		// // getSchedulesForActivity(activityid);
		//
		// }
		//
		// // SEND broadcast to activity
		// Intent intent = new Intent(
		// CommConstant.ACTIVITY_DOWNLOAD_SUCCESS);
		// mContext.sendBroadcast(intent);
		//
		// ref.setLastestServiceLastModifiedTime(mContext, MyDate
		// .transformLocalDateTimeToUTCFormat(MyDate
		// .getCurrentDateTime()));
		//
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		//
		// public void onFailure(Throwable e, String response) {
		// // Response failed :(
		// Log.i("webservice", "Get Activities failed");
		// }
		// });
	}

	/**
	 * Get all schedule of owner id
	 * */
	public void getAllSchedule() {
		String scheduleUrl = BaseUrl.BASEURL + "schedules" + "?"
				+ BaseUrl.URL_POST_FIX;
		final SharedReference ref = new SharedReference();
		int currentOwnerID = ref.getCurrentOwnerId(mContext);
		Log.i("schedules url:", scheduleUrl);
		RequestParams params = new RequestParams();
		params.put("ownerid", String.valueOf(currentOwnerID));
		params.put("lastupdatetime",
				ref.getLastestScheduleLastModifiedTime(mContext));
		client.get(scheduleUrl, params, new JsonHttpResponseHandler() {
			public void onSuccess(JSONObject response) {
				Log.i("successful response", response.toString());
				try {
					JSONArray deletedschedules = response
							.getJSONArray("deletedschedules");
					int deleted_services_count = deletedschedules.length();
					if (deleted_services_count > 0) {
						for (int i = 0; i < deleted_services_count; i++) {
							int id = deletedschedules.getInt(i);
							if (dbHelper.isOndutyExisted(id)) {
								// Log.i("database", "schedule " + scheduleID +
								// " already exists");
								if (dbHelper.deleteRelatedOnduty(id)) {
									// Log.i("database", "delete schedule " +
									// scheduleID + " successfully!");
								}
							}
							dbHelper.deleteSchedule(id);
						}
					}

					JSONArray schedules = response.getJSONArray("schedules");
					int scheudule_count = schedules.length();
					for (int i = 0; i < scheudule_count; i++) {
						JSONObject Schedule = schedules.getJSONObject(i);
						ContentValues cv = new ContentValues();
						cv.put(ScheduleTable.own_ID,
								Schedule.getInt("creatorid"));

						cv.put(ScheduleTable.last_Modified,
								Schedule.getString("lastmodified"));
						cv.put(ScheduleTable.start_Time,
								Schedule.getString("startdatetime"));
						cv.put(ScheduleTable.schedule_Description,
								Schedule.getString("desp"));
						cv.put(ScheduleTable.end_Time,
								Schedule.getString("enddatetime"));
						cv.put(ScheduleTable.service_ID,
								Schedule.getInt("serviceid"));
						cv.put(ScheduleTable.is_Deleted, 0);
						cv.put(ScheduleTable.is_Synchronized, 1);

						int scheduleID = Schedule.getInt("scheduleid");

						if (dbHelper.isScheduleExisted(scheduleID) == false) {
							cv.put(ScheduleTable.schedule_ID,
									Schedule.getInt("scheduleid"));
							Log.i("scheduleid",
									"= " + Schedule.getInt("scheduleid"));
							if (dbHelper.insertSchedule(cv)) {
								// Log.i("database", "insert schedule " +
								// scheduleID + " successfully!");
							}
						} else {
							if (dbHelper.updateSchedule(scheduleID, cv))
								;
							{
								// Log.i("database", "update schedule " +
								// scheduleID + " successfully!");
							}
						}

						if (dbHelper.isOndutyExisted(scheduleID)) {
							// Log.i("database", "schedule " + scheduleID +
							// " already exists");
							if (dbHelper.deleteRelatedOnduty(scheduleID)) {
								// Log.i("database", "delete schedule " +
								// scheduleID + " successfully!");
							}
						}

						Log.i("getschedule", "done");

						String[] members = Schedule.getString("members").split(
								",");
						// Log.i("Sync", "schedule " + scheduleID + " has " +
						// members.length + " members");
						for (int j = 0; j < members.length; j++) {
							if (!members[j].equalsIgnoreCase("")) {
								int memberid = Integer.valueOf(members[j]);
								ContentValues newOnduty = new ContentValues();
								newOnduty.put(OndutyTable.service_ID,
										Schedule.getInt("serviceid"));
								newOnduty.put(OndutyTable.participant_ID,
										memberid);
								newOnduty.put(OndutyTable.schedule_ID,
										Schedule.getInt("scheduleid"));
								newOnduty.put(OndutyTable.last_Modified,
										Schedule.getString("lastmodified"));
								newOnduty.put(OndutyTable.is_Deleted, 0);
								newOnduty.put(OndutyTable.is_Synchronized, 1);

								if (dbHelper.insertOnduty(newOnduty)) {
									// Log.i("database", "insert member " +
									// memberid + " to schedule " + scheduleID
									// +" successfully");
								}
							}
						}
						Log.i("getmembers", "done");
					}

					// update time lastest update for schedule
					ref.setLastestScheduleLastModifiedTime(mContext, MyDate
							.transformLocalDateTimeToUTCFormat(MyDate
									.getCurrentDateTime()));

					Intent intent = new Intent(CommConstant.SCHEDULE_READY);
					mContext.sendBroadcast(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable e, String response) {
				// Response failed :(
				Log.i("webservice", "Get Schedules failed");
			}
		});
	}

	public void getSchedulesForActivity(String activityid) {
		String SchedulesUrl = BaseUrl.BASEURL + "services/" + activityid
				+ "/schedules" + "?" + BaseUrl.URL_POST_FIX;
		final SharedReference ref = new SharedReference();
		int currentOwnerID = ref.getCurrentOwnerId(mContext);
		// String SchedulesUrl = BASEURL + "services/" + currentOwnerID +
		// "/schedules" + "?" + URLPostfix;
		Log.i("schedules url:", SchedulesUrl);
		RequestParams params = new RequestParams();
		params.put("ownerid", String.valueOf(currentOwnerID));
		params.put("lastupdatetime",
				ref.getLastestScheduleLastModifiedTime(mContext));
		client.get(SchedulesUrl, params, new JsonHttpResponseHandler() {
			public void onSuccess(JSONObject response) {
				Log.i("successful response", response.toString());
				try {
					JSONArray deletedschedules = response
							.getJSONArray("deletedschedules");
					int deleted_services_count = deletedschedules.length();
					if (deleted_services_count > 0) {
						for (int i = 0; i < deleted_services_count; i++) {
							int id = deletedschedules.getInt(i);
							if (dbHelper.isOndutyExisted(id)) {
								// Log.i("database", "schedule " + scheduleID +
								// " already exists");
								if (dbHelper.deleteRelatedOnduty(id)) {
									// Log.i("database", "delete schedule " +
									// scheduleID + " successfully!");
								}
							}
							dbHelper.deleteSchedule(id);
						}
					}

					JSONArray schedules = response.getJSONArray("schedules");
					int scheudule_count = schedules.length();
					for (int i = 0; i < scheudule_count; i++) {
						JSONObject Schedule = schedules.getJSONObject(i);
						ContentValues cv = new ContentValues();
						cv.put(ScheduleTable.own_ID,
								Schedule.getInt("creatorid"));

						cv.put(ScheduleTable.last_Modified,
								Schedule.getString("lastmodified"));
						cv.put(ScheduleTable.start_Time,
								Schedule.getString("startdatetime"));
						cv.put(ScheduleTable.schedule_Description,
								Schedule.getString("desp"));
						cv.put(ScheduleTable.end_Time,
								Schedule.getString("enddatetime"));
						cv.put(ScheduleTable.service_ID,
								Schedule.getInt("serviceid"));
						cv.put(ScheduleTable.is_Deleted, 0);
						cv.put(ScheduleTable.is_Synchronized, 1);

						int scheduleID = Schedule.getInt("scheduleid");
						// Log.i("Webservice","schedule " + scheduleID +
						// " has members " + Schedule.getString("members"));
						if (dbHelper.isScheduleExisted(scheduleID) == false) {
							cv.put(ScheduleTable.schedule_ID,
									Schedule.getInt("scheduleid"));
							Log.i("scheduleid",
									"= " + Schedule.getInt("scheduleid"));
							if (dbHelper.insertSchedule(cv)) {
								// Log.i("database", "insert schedule " +
								// scheduleID + " successfully!");
							}
						} else {
							if (dbHelper.updateSchedule(scheduleID, cv))
								;
							{
								// Log.i("database", "update schedule " +
								// scheduleID + " successfully!");
							}
						}

						if (dbHelper.isOndutyExisted(scheduleID)) {
							// Log.i("database", "schedule " + scheduleID +
							// " already exists");
							if (dbHelper.deleteRelatedOnduty(scheduleID)) {
								// Log.i("database", "delete schedule " +
								// scheduleID + " successfully!");
							}
						}

						Log.i("getschedule", "done");

						String[] members = Schedule.getString("members").split(
								",");
						// Log.i("Sync", "schedule " + scheduleID + " has " +
						// members.length + " members");
						for (int j = 0; j < members.length; j++) {
							if (!members[j].equalsIgnoreCase("")) {
								int memberid = Integer.valueOf(members[j]);
								ContentValues newOnduty = new ContentValues();
								newOnduty.put(OndutyTable.service_ID,
										Schedule.getInt("serviceid"));
								newOnduty.put(OndutyTable.participant_ID,
										memberid);
								newOnduty.put(OndutyTable.schedule_ID,
										Schedule.getInt("scheduleid"));
								newOnduty.put(OndutyTable.last_Modified,
										Schedule.getString("lastmodified"));
								newOnduty.put(OndutyTable.is_Deleted, 0);
								newOnduty.put(OndutyTable.is_Synchronized, 1);

								if (dbHelper.insertOnduty(newOnduty)) {
									// Log.i("database", "insert member " +
									// memberid + " to schedule " + scheduleID
									// +" successfully");
								}
							}
						}
						Log.i("getmembers", "done");
					}

					// update time lastest update for schedule
					ref.setLastestScheduleLastModifiedTime(mContext, MyDate
							.transformLocalDateTimeToUTCFormat(MyDate
									.getCurrentDateTime()));

					Intent intent = new Intent(CommConstant.SCHEDULE_READY);
					mContext.sendBroadcast(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable e, String response) {
				// Response failed :(
				Log.i("webservice", "Get Schedules failed");
			}
		});
	}

	public void getParticipantsFromWeb() {
		String ParticipantUrl = BaseUrl.BASEURL + "members" + "?"
				+ BaseUrl.URL_POST_FIX;
		Log.i("url is :", ParticipantUrl);
		final SharedReference ref = new SharedReference();
		int currentOwnerID = ref.getCurrentOwnerId(mContext);
		RequestParams params = new RequestParams();
		params.put("ownerid", String.valueOf(currentOwnerID));
		params.put("lastupdatetime",
				ref.getLastestParticipantLastModifiedTime(mContext));
		client.get(ParticipantUrl, params, new JsonHttpResponseHandler() {
			public void onSuccess(JSONObject response) {
				Log.i("successful response", response.toString());
				try {

					// deleted member, delete member from activity which that
					// member joined in
					JSONArray deleted_member = response
							.getJSONArray("deletedmembers");
					int delete_members_count = deleted_member.length();
					if (delete_members_count > 0) {

						for (int i = 0; i < delete_members_count; i++) {
							int id = deleted_member.getInt(i);
							// ArrayList<String>list_activity_id=dbHelper.getListActivity(id);
							// for (int j = 0; j < list_activity_id.size(); j++)
							// {
							// String activity_id = list_activity_id.get(j);
							//
							// }
							dbHelper.deleteParticipant(id);

						}
					}

					JSONArray participants = response.getJSONArray("members");
					int participant_count = participants.length();
					for (int i = 0; i < participant_count; i++) {
						JSONObject Participant = participants.getJSONObject(i);
						int ownerid = Participant.getInt("creatorid");

						// Owner is included in JSON Response "members"
						// Should not appear in Participants

						ContentValues cv = new ContentValues();
						cv.put(ParticipantTable.last_Modified,
								Participant.getString("lastmodified"));
						cv.put(ParticipantTable.participant_Name,
								Participant.getString("membername"));
						// cv.put(ParticipantTable.own_ID,
						// Participant.getInt("creatorid"));
						cv.put(ParticipantTable.own_ID, ownerid);
						Log.i("getParticipantsFromWeb own_ID ", ownerid + "");

						String participant_Mobile = Participant
								.getString("mobilenumber");
						cv.put(ParticipantTable.participant_Mobile,
								participant_Mobile);
						Log.i("getParticipantsFromWeb participant_Mobile ",
								participant_Mobile);
						String participant_Email = Participant
								.getString("memberemail");
						Log.i("getParticipantsFromWeb participant_Email ",
								participant_Email);
						cv.put(ParticipantTable.participant_Email,
								participant_Email);
						cv.put(ParticipantTable.is_Registered, 1);
						cv.put(ParticipantTable.is_Deleted, 0);
						cv.put(ParticipantTable.is_Sychronized, 1);
						int participantID = Participant.getInt("memberid");

						if (dbHelper.isParticipantExisted(participantID) == false) {
							int participant_ID = (Participant
									.getInt("memberid") + 1);
							cv.put(ParticipantTable.participant_ID,
									participant_ID);
							Log.i("getParticipantsFromWeb participant_ID ",
									participant_ID + "");
							if (dbHelper.insertParticipant(cv))
								Log.i("database", "insert participant "
										+ Participant.getString("membername")
										+ " successfully!");
						} else {
							if (dbHelper.updateParticipant(participantID, cv))
								Log.i("database", "update participant "
										+ Participant.getString("membername")
										+ " successfully!");
						}
					}

					ref.setLastestParticipantLastModifiedTime(mContext, MyDate
							.transformLocalDateTimeToUTCFormat(MyDate
									.getCurrentDateTime()));

					Intent intent = new Intent(CommConstant.PARTICIPANT_READY);
					mContext.sendBroadcast(intent);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable e, String response) {
				// Response failed :(
				Log.i("webservice", "Get Activities failed");
			}
		});
	}

	/**
	 * Add new activity
	 * */
	public void addActivity(final MyActivity activity) {
		String ActivityUrl = BaseUrl.BASEURL + "services" + "?"
				+ BaseUrl.URL_POST_FIX;
		try {
			JSONObject activityParams = new JSONObject();
			activityParams.put("alert", activity.getAlert());
			activityParams.put("repeat", activity.getRepeat());
			activityParams.put("servicename", activity.getActivity_name());
			activityParams.put("serviceid", activity.getActivity_ID());
			activityParams.put("desp", activity.getDesp());
			activityParams.put("startdatetime", activity.getStarttime());
			activityParams.put("enddatetime", activity.getEndtime());

			JSONObject params = new JSONObject();
			params.put("ownerid", activity.getOwner_ID());
			params.put("services", activityParams);

			client.addHeader("Content-type", "application/json");
			Log.i("add activity", params.toString());
			StringEntity entity = new StringEntity(params.toString());
			client.post(null, ActivityUrl, entity, "application/json",
					new JsonHttpResponseHandler() {
						public void onSuccess(JSONObject response) {
							Log.i("Add activity " + activity.getActivity_name(),
									response.toString());
							try {
								ContentValues cv = new ContentValues();
								String last_modified = response
										.getString("lastmodified");
								cv.put(ActivityTable.last_ModifiedTime,
										last_modified);
								cv.put(ActivityTable.is_Synchronized, 1);
								dbHelper.updateActivity(
										activity.getActivity_ID(), cv);
								Log.i("last_modified", last_modified);
								if (dbHelper.updateActivity(
										activity.getActivity_ID(), cv)) {
									// Toast.makeText(this,
									// "insert activity successfully",
									// Toast.LENGTH_LONG).show();
								}

								SharedReference ref = new SharedReference();
								ref.setLastestServiceLastModifiedTime(mContext,
										last_modified);

								((Activity) mContext).finish();
								Intent intent = new Intent(
										CommConstant.ACTIVITY_DOWNLOAD_SUCCESS);
								mContext.sendBroadcast(intent);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						public void onFailure(Throwable e, String response) {
							// Response failed :(

							Toast.makeText(
									mContext,
									mContext.getResources().getString(
											R.string.create_activity_error)
											+ "\n" + response.toString(),
									Toast.LENGTH_LONG).show();

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
							progress.dismiss();
						}
					});
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addSchedule(Schedule schedule, List<Integer> pins) {
		// String ScheduleUrl = BASEURL + "schedules";
		String ScheduleUrl = BaseUrl.BASEURL + "schedules" + "?"
				+ BaseUrl.URL_POST_FIX;
		;
		final int id = schedule.getSchedule_ID();
		try {
			JSONObject scheduleParams = new JSONObject();
			scheduleParams.put("scheduleid", schedule.getSchedule_ID());
			scheduleParams.put("desp", schedule.getDesp());
			scheduleParams.put("startdatetime", schedule.getStarttime());
			scheduleParams.put("enddatetime", schedule.getEndtime());
			scheduleParams.put("utcoffset", 0);
			JSONArray jpins = new JSONArray();
			for (int i = 0; i < pins.size(); i++) {
				jpins.put(pins.get(i));
			}
			scheduleParams.put("members", jpins);
			JSONObject params = new JSONObject();
			params.put("ownerid", schedule.getOwner_ID());
			params.put("serviceid", schedule.getService_ID());
			params.put("schedules", scheduleParams);

			client.addHeader("Content-type", "application/json");
			Log.i("add schedule", params.toString());
			StringEntity entity = new StringEntity(params.toString());
			client.post(null, ScheduleUrl, entity, "application/json",
					new JsonHttpResponseHandler() {
						public void onSuccess(JSONObject response) {
							Log.i("successful response", response.toString());
							try {
								ContentValues cv = new ContentValues();
								String last_modified = response
										.getString("lastmodified");
								cv.put(ScheduleTable.last_Modified,
										last_modified);
								cv.put(ScheduleTable.is_Synchronized, 1);
								dbHelper.updateSchedule(id, cv);
								Log.i("last_modified", last_modified);

								// go to schedule
								CategoryTabActivity.currentPage = 2;
								Intent intent = new Intent(
										CommConstant.UPDATE_SCHEDULE);
								mContext.sendBroadcast(intent);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						public void onFailure(Throwable e, String response) {
							// Response failed :(

							Log.i("failure response", response);
							Log.i("fail", e.toString());
							Toast.makeText(
									mContext,
									mContext.getResources().getString(
											R.string.create_schedule_error)
											+ "\n" + response.toString(),
									Toast.LENGTH_LONG).show();

						}
					});
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateSchedule(Schedule schedule, List<Integer> pins) {
		// String ScheduleUrl = BASEURL + "schedules/" +
		// schedule.getScheduleID();
		String ScheduleUrl = BaseUrl.BASEURL + "schedules/"
				+ schedule.getSchedule_ID() + "?" + BaseUrl.URL_POST_FIX;
		final int id = schedule.getSchedule_ID();
		try {
			JSONObject scheduleParams = new JSONObject();
			scheduleParams.put("desp", schedule.getDesp());
			scheduleParams.put("startdatetime", schedule.getStarttime());
			scheduleParams.put("enddatetime", schedule.getEndtime());
			scheduleParams.put("utcoffset", 0);
			JSONArray jpins = new JSONArray();
			for (int i = 0; i < pins.size(); i++) {
				jpins.put(pins.get(i));
			}
			scheduleParams.put("members", jpins);

			JSONObject params = new JSONObject();
			params.put("ownerid", schedule.getOwner_ID());
			params.put("serviceid", schedule.getService_ID());
			params.put("schedules", scheduleParams);

			client.addHeader("Content-type", "application/json");
			Log.i("add activity", params.toString());
			StringEntity entity = new StringEntity(params.toString());
			client.put(null, ScheduleUrl, entity, "application/json",
					new JsonHttpResponseHandler() {
						public void onSuccess(JSONObject response) {
							Log.i("update schedule success",
									response.toString());
							try {
								ContentValues cv = new ContentValues();
								String last_modified = response
										.getString("lastmodified");
								cv.put(ScheduleTable.last_Modified,
										last_modified);
								cv.put(ScheduleTable.is_Synchronized, 1);
								dbHelper.updateSchedule(id, cv);
								Log.i("last_modified", last_modified);
								Intent intent = new Intent(
										CommConstant.UPDATE_SCHEDULE);
								mContext.sendBroadcast(intent);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						public void onFailure(Throwable e, String response) {
							// Response failed :(

							Log.i("failure response", response);
							Log.i("fail", e.toString());

						}
					});
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteSchedule(Schedule schedule) {
		// String ScheduleUrl = BASEURL + "schedules/" +
		// schedule.getScheduleID();
		String ScheduleUrl = BaseUrl.BASEURL + "schedules/"
				+ schedule.getSchedule_ID() + "?" + BaseUrl.URL_POST_FIX;
		final int id = schedule.getSchedule_ID();
		client.delete(ScheduleUrl, new JsonHttpResponseHandler() {
			public void onSuccess(JSONObject response) {
				try {
					if (response.getString("lastmodified") != null) {

						dbHelper.deleteRelatedOnduty(id);
						dbHelper.deleteSchedule(id);
						Log.i("delete schedule", "successfully");
						Intent intent = new Intent(
								CommConstant.DELETE_SCHEDULE_COMPLETE);
						mContext.sendBroadcast(intent);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
				if (progress.isShowing()) {
					progress.dismiss();
				}
			}

			public void onFailure(Throwable e, String response) {
				// Response failed :
				Log.i("failure response", response);
				Log.i("fail", e.toString());
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.delete_schedule_error)
								+ "\n" + response.toString(), Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	public void uploadRecentNewActivitiesToWeb() {
		List<MyActivity> unsyncedActivities = dbHelper
				.getUnsyncedNewActivities();
		for (int i = 0; i < unsyncedActivities.size(); i++) {
			MyActivity ma = unsyncedActivities.get(i);
			this.addActivity(ma);
		}
	}

	public void uploadRecentEditedActivitiesToWeb() {
		List<MyActivity> unsyncedActivities = dbHelper
				.getUnsyncedEditedActivities();
		for (int i = 0; i < unsyncedActivities.size(); i++) {
			MyActivity ma = unsyncedActivities.get(i);
			updateActivity(ma);
		}
	}

	public void uploadRecentNewParticipantsToWeb() {
		List<Participant> unsyncedParticipants = dbHelper
				.getUnsyncedNewParticipants();
		for (int i = 0; i < unsyncedParticipants.size(); i++) {
			Participant p = unsyncedParticipants.get(i);
			addParticipant(p);
		}
	}

	public void uploadRecentEditedParticipantsToWeb() {
		List<Participant> unsyncedParticipants = dbHelper
				.getUnsyncedEditedParticipants();
		for (int i = 0; i < unsyncedParticipants.size(); i++) {
			Participant p = unsyncedParticipants.get(i);
			this.updateParticipant(p);
		}
	}

	public void uploadRecentNewSchedulesToWeb() {
		List<Schedule> unsyncedSchedules = dbHelper.getUnsyncedNewSchedules();
		for (int i = 0; i < unsyncedSchedules.size(); i++) {
			Schedule s = unsyncedSchedules.get(i);
			List<Integer> members = dbHelper.getParticipantsForSchedule(s
					.getSchedule_ID());
			this.addSchedule(s, members);
		}
	}

	public void uploadRecentEditedSchedulesToWeb() {
		List<Schedule> unsyncedSchedules = dbHelper
				.getUnsyncedEditedSchedules();
		for (int i = 0; i < unsyncedSchedules.size(); i++) {
			Schedule s = unsyncedSchedules.get(i);
			List<Integer> members = dbHelper.getParticipantsForSchedule(s
					.getSchedule_ID());
			this.updateSchedule(s, members);
		}
	}

	/**
	 * Get member join into actiivty
	 * */
	public void getSharedmembersForActivity(final String activity_id) {
		// final ArrayList<Sharedmember> sharedmembers=new
		// ArrayList<Sharedmember>();
		String SharedmembersUrl = BaseUrl.BASEURL + "services/" + activity_id
				+ "/sharedmembers" + "?" + BaseUrl.URL_POST_FIX;
		Log.i("url is :", SharedmembersUrl);
		int currentOwnerID = mContext.getSharedPreferences("MyPreferences", 0)
				.getInt("currentownerid", 0);
		RequestParams params = new RequestParams();
		params.put("ownerid", String.valueOf(currentOwnerID));
		params.put("lastupdatetime", "");
		client.get(SharedmembersUrl, params, new JsonHttpResponseHandler() {
			public void onSuccess(JSONObject response) {
				Log.i("successful response", response.toString());
				try {
					JSONArray deleteMember=response.getJSONArray("deletedsmembers");
					int deleted_member_count = deleteMember.length();
					if (deleted_member_count > 0) {
						for (int i = 0; i < deleted_member_count; i++) {
							int id = deleteMember.getInt(i);
							dbHelper.deleteSharedmember(id, activity_id);
						}
					}
					
					
					JSONArray JSharedmembers = response
							.getJSONArray("sharedmembers");
					int sm_count = JSharedmembers.length();
					for (int i = 0; i < sm_count; i++) {
						JSONObject JSharedmember = JSharedmembers
								.getJSONObject(i);
						int sm_id = JSharedmember.getInt("memberid");
						String sm_email = JSharedmember
								.getString("memberemail");
						String sm_number = JSharedmember
								.getString("mobilenumber");
						String sm_name = JSharedmember.getString("membername");
						int sm_role = JSharedmember.getInt("sharedrole");
						String sm_lastmdf = JSharedmember
								.getString("lastmodified");
						// Sharedmember newsm = new Sharedmember(sm_id, sm_name,
						// sm_email, sm_number, sm_role, activity_id);
						// sharedmembers.add(newsm);

						ContentValues cv = new ContentValues();

						cv.put(SharedMemberTable.member_email, sm_email);
						cv.put(SharedMemberTable.member_name, sm_name);
						cv.put(SharedMemberTable.member_mobile, sm_number);
						cv.put(SharedMemberTable.service_id, activity_id);
						cv.put(SharedMemberTable.role, sm_role);
						// cv.put(SharedMemberTable.last_modified,
						// JSharedmember.getString("lastmodified"));
						cv.put(SharedMemberTable.last_modified, sm_lastmdf);
						cv.put(SharedMemberTable.is_Deleted, 0);
						cv.put(SharedMemberTable.is_Synced, 1);
						if (dbHelper.isSharedmemberExisted(sm_id, activity_id)) {
							dbHelper.updateSharedmember(sm_id, activity_id, cv);
						} else {
							cv.put(SharedMemberTable.member_id, sm_id);
							dbHelper.insertSharedmember(cv);
						}
					}

					// SharedPreferences sp = mContext.getSharedPreferences(
					// "MyPreferences", 0);
					// Editor editor = sp.edit();
					// editor.putString("lastparticipantmodified", MyDate
					// .transformLocalDateTimeToUTCFormat(MyDate
					// .getCurrentDateTime()));
					// editor.commit();
					SharedReference ref = new SharedReference();
					ref.setLastestParticipantLastModifiedTime(mContext, MyDate
							.transformLocalDateTimeToUTCFormat(MyDate
									.getCurrentDateTime()));

					Intent intent = new Intent();
					intent.setAction(CommConstant.GET_SHARED_MEMBER_ACTIVITY_COMPLETE);
					mContext.sendBroadcast(intent);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable e, String response) {
				// Response failed :(
				Log.i("webservice", "Get Activities failed");
			}
		});
	}

	public void postSharedmemberToActivity(final int memberid, final int role,
			final String activityid) {
		String sharedmemberUrl = BaseUrl.BASEURL + "services/" + activityid
				+ "/" + "sharedmembers" + "?" + BaseUrl.URL_POST_FIX;
		try {
			JSONObject sharedmemberParams = new JSONObject();
			sharedmemberParams.put("ownerid",
					new SharedReference().getCurrentOwnerId(mContext));
			sharedmemberParams.put("sharedrole", role);
			sharedmemberParams.put("memberid", memberid);

			// client.addHeader("Content-type", "application/json");
			// Log.i("add participant", sharedmemberParams.toString());
			StringEntity entity = new StringEntity(
					sharedmemberParams.toString());
			client.post(null, sharedmemberUrl, entity, "application/json",
					new JsonHttpResponseHandler() {
						public void onSuccess(JSONObject response) {
							Log.i("successful sharedmember",
									response.toString());
							// Intent intent = new Intent(mContext,
							// CreateNewScheduleActivity.class);
							// intent.putExtra(CommConstant.TYPE,
							// DatabaseHelper.NEW);
							// intent.putExtra(CommConstant.ACTIVITY_ID,
							// activityid);
							// mContext.startActivity(intent);
							int code = 0;
							try {
								code = response.getInt("code");
								if (code != 200) {
									Toast.makeText(mContext,
											response.getString("message"),
											Toast.LENGTH_LONG).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							try {
								String lastmodify = "";

								lastmodify = response.getString("lastmodified");
								Participant member = dbHelper
										.getParticipant(memberid);
								ContentValues cv = new ContentValues();
								cv.put(SharedMemberTable.is_Synced, 1);
								cv.put(SharedMemberTable.is_Deleted, 0);
								cv.put(SharedMemberTable.service_id, activityid);
								cv.put(SharedMemberTable.role, role);
								cv.put(SharedMemberTable.member_name,
										member.getName());
								cv.put(SharedMemberTable.member_id,
										member.getID());
								cv.put(SharedMemberTable.member_email,
										member.getEmail());
								cv.put(SharedMemberTable.member_mobile,
										member.getMobile());
								cv.put(SharedMemberTable.last_modified,
										lastmodify);
								dbHelper.insertSharedmember(cv);
								Intent intent=new Intent(CommConstant.GET_SHARED_MEMBER_ACTIVITY_COMPLETE);
								intent.putExtra(CommConstant.ACTIVITY_ID,activityid);
								mContext.sendBroadcast(intent);

							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						}

						public void onFailure(Throwable e, String response) {
							// Response failed :(

							Log.i("failure response", response);
							Log.i("fail", e.toString());

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
							if (progress.isShowing()) {
								progress.dismiss();
							}
						}
					});
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Delete shared member activity
	 * */
	public void deleteSharedmemberOfActivity(final int memberid,final String activityid) {
		String sharedmemberUrl = BaseUrl.BASEURL + "services/" + activityid
				+ "/" + "sharedmembers/" + memberid + "?"
				+ BaseUrl.URL_POST_FIX;
		client.delete(sharedmemberUrl, new JsonHttpResponseHandler() {
			public void onSuccess(JSONObject response) {
				Log.i("delete shared member of activity", response.toString());
				try {
					String lastmodified = response.getString("lastmodified");
					dbHelper.deleteSharedmember(memberid, activityid);
					SharedReference ref = new SharedReference();
					ref.setLastestServiceLastModifiedTime(mContext,
							lastmodified);
					Intent intent=new Intent(CommConstant.GET_SHARED_MEMBER_ACTIVITY_COMPLETE);
					mContext.sendBroadcast(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable e, String response) {
				// Response failed :
				Log.i("failure response", response);
				Log.i("fail", e.toString());
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.delete_participant)
								+ "\n" + response.toString(), Toast.LENGTH_LONG)
						.show();
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
				if (progress.isShowing()) {
					progress.dismiss();
				}
			}
		});
	}

	public void alterSharedmemberToActivity(int memberid, int role,
			int activityid) {
		String sharedmemberUrl = BaseUrl.BASEURL + "services/" + activityid
				+ "/" + "sharedmembers/" + memberid + "?"
				+ BaseUrl.URL_POST_FIX;
		try {
			JSONObject params = new JSONObject();
			params.put("ownerid",
					new SharedReference().getCurrentOwnerId(mContext));
			params.put("sharedrole", role);

			// client.addHeader("Content-type", "application/json");
			Log.i("update participant", params.toString());
			StringEntity entity = new StringEntity(params.toString());
			client.put(null, sharedmemberUrl, entity, "application/json",
					new JsonHttpResponseHandler() {
						public void onSuccess(JSONObject response) {
							Log.i("successful response", response.toString());

						}

						public void onFailure(Throwable e, String response) {
							// Response failed :(

							Log.i("failure response", response);
							Log.i("fail", e.toString());

						}
					});
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addParticipant(Participant participant) {
		String ParticipantUrl = BaseUrl.BASEURL + "members" + "?"
				+ BaseUrl.URL_POST_FIX;
		final int id = participant.getID();
		try {
			JSONObject participantParams = new JSONObject();
			participantParams.put("email", participant.getEmail());
			participantParams.put("mobile", participant.getMobile());
			participantParams.put("membername", participant.getName());
			participantParams.put("memberid", participant.getID());

			JSONObject params = new JSONObject();
			params.put("ownerid", participant.getOwnerID());
			params.put("members", participantParams);

			client.addHeader("Content-type", "application/json");
			Log.i("add participant", params.toString());
			StringEntity entity = new StringEntity(params.toString());
			client.post(null, ParticipantUrl, entity, "application/json",
					new JsonHttpResponseHandler() {
						public void onSuccess(JSONObject response) {
							Log.i("successful response", response.toString());
							try {
								
								ContentValues cv = new ContentValues();
								String last_modified = response
										.getString("lastmodified");
								cv.put(ParticipantTable.last_Modified,
										last_modified);
								cv.put(ParticipantTable.is_Sychronized, 1);

								dbHelper.updateParticipant(id, cv);
								
								((Activity) mContext).finish();
								Intent intent = new Intent(
										CommConstant.ADD_CONTACT_SUCCESS);
								mContext.sendBroadcast(intent);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						public void onFailure(Throwable e, String response) {
							// Response failed :(

							Log.i("failure response", response);
							Log.i("fail", e.toString());
							Toast.makeText(
									mContext,
									mContext.getResources().getString(
											R.string.add_participant_error)
											+ "\n" + response.toString(),
									Toast.LENGTH_LONG).show();
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
							progress.dismiss();
						}
					});
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Update participant: update table ParticipantTable and SharedMemberTable
	 * */
	public void updateParticipant(final Participant participant) {
		String ParticipantUrl = BaseUrl.BASEURL + "members/"
				+ participant.getID() + "?" + BaseUrl.URL_POST_FIX;
		Log.i("updateParticipant ParticipantUrl ", ParticipantUrl);
		final int id = participant.getID();
		try {
			JSONObject participantParams = new JSONObject();
			participantParams.put("email", participant.getEmail());
			participantParams.put("mobile", participant.getMobile());
			participantParams.put("membername", participant.getName());

			JSONObject params = new JSONObject();
			params.put("ownerid", participant.getOwnerID());
			params.put("members", participantParams);

			client.addHeader("Content-type", "application/json");
			Log.i("update participant", params.toString());
			StringEntity entity = new StringEntity(params.toString());
			client.put(null, ParticipantUrl, entity, "application/json",
					new JsonHttpResponseHandler() {
						public void onSuccess(JSONObject response) {
							Log.d("update participant", response.toString());
							try {

								ContentValues cv = new ContentValues();
								String last_modified = response
										.getString("lastmodified");
								cv.put(ParticipantTable.last_Modified,
										last_modified);
								cv.put(ParticipantTable.is_Sychronized, 1);
								dbHelper.updateParticipant(id, cv);
								
								ContentValues contentValues=new ContentValues();
								contentValues.put(SharedMemberTable.member_email, participant.getEmail());
								contentValues.put(SharedMemberTable.member_mobile, participant.getMobile());
								contentValues.put(SharedMemberTable.member_name, participant.getName());
								dbHelper.updateSharedmember(participant.getID(), contentValues);
								
								((Activity) mContext).finish();
								Intent intent = new Intent(
										CommConstant.ADD_CONTACT_SUCCESS);
								mContext.sendBroadcast(intent);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						public void onFailure(Throwable e, String response) {
							// Response failed :(

							Log.i("failure response", response);
							Log.i("fail", e.toString());
							Toast.makeText(
									mContext,
									mContext.getResources().getString(
											R.string.update_participant_error)
											+ "\n" + response.toString(),
									Toast.LENGTH_LONG).show();

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
							progress.dismiss();
						}
					});
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteParticipant(final Participant participant) {
		String urlDeleteContact = BaseUrl.BASEURL + "members/"
				+ participant.getID() + "?" + BaseUrl.URL_POST_FIX;
		Log.d("delete contact", urlDeleteContact);
		final int id = participant.getID();
		client.delete(urlDeleteContact, new JsonHttpResponseHandler() {
			public void onSuccess(JSONObject response) {
				Log.d("delete str", response.toString());
				try {
					if (response.getString("lastmodified") != null) {

						ContentValues cv = new ContentValues();
						cv.put(ParticipantTable.is_Deleted, 1);
						cv.put(ParticipantTable.is_Sychronized, 1);
						// dbHelper.updateParticipant(id, cv);
						dbHelper.deleteParticipant(id);
						((Activity) mContext).finish();
						Intent intent = new Intent(
								CommConstant.DELETE_CONTACT_COMPLETE);
						mContext.sendBroadcast(intent);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable e, String response) {
				// Response failed :
				Log.i("failure response", response);
				Log.i("fail", e.toString());
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.delete_contact_error)
								+ "\n" + response.toString(), Toast.LENGTH_LONG)
						.show();
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
				progress.dismiss();
			}
		});
	}

	public void updateActivity(MyActivity activity) {
		String ActivityUrl = BaseUrl.BASEURL + "services/"
				+ activity.getActivity_ID() + "?" + BaseUrl.URL_POST_FIX;
		final String id = activity.getActivity_ID();
		try {
			JSONObject activityParams = new JSONObject();
			activityParams.put("alert", activity.getAlert());
			activityParams.put("repeat", activity.getRepeat());
			activityParams.put("servicename", activity.getActivity_name());
			activityParams.put("desp", activity.getDesp());
			activityParams.put("startdatetime", activity.getStarttime());
			activityParams.put("enddatetime", activity.getEndtime());

			JSONObject params = new JSONObject();
			params.put("ownerid", activity.getOwner_ID());
			params.put("services", activityParams);

			client.addHeader("Content-type", "application/json");
			Log.i("update activity", params.toString());
			StringEntity entity = new StringEntity(params.toString());
			client.put(null, ActivityUrl, entity, "application/json",
					new JsonHttpResponseHandler() {
						public void onSuccess(JSONObject response) {
							Log.i("update activity", response.toString());
							try {
								ContentValues cv = new ContentValues();
								String last_modified = response
										.getString("lastmodified");
								cv.put(ActivityTable.last_ModifiedTime,
										last_modified);
								cv.put(ActivityTable.is_Synchronized, 1);
								dbHelper.updateActivity(id, cv);
								Log.i("last_modified", last_modified);
								SharedReference ref = new SharedReference();
								ref.setLastestServiceLastModifiedTime(mContext,
										last_modified);
								((Activity) mContext).finish();
								Intent intent = new Intent(
										CommConstant.ACTIVITY_DOWNLOAD_SUCCESS);
								mContext.sendBroadcast(intent);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						public void onFailure(Throwable e, String response) {
							// Response failed :(

							Log.i("failure response", response);
							Log.i("fail", e.toString());
							Toast.makeText(
									mContext,
									mContext.getResources().getString(
											R.string.edit_activity_error)
											+ "\n" + response.toString(),
									Toast.LENGTH_LONG).show();
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
							progress.dismiss();
						}
					});
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteActivity(MyActivity activity) {
		String ActivityUrl = BaseUrl.BASEURL + "services/"
				+ activity.getActivity_ID() + "?" + BaseUrl.URL_POST_FIX;
		final String id = activity.getActivity_ID();
		client.addHeader("Content-type", "application/json");
		client.delete(ActivityUrl, new JsonHttpResponseHandler() {
			public void onSuccess(JSONObject response) {
				Log.d("delete activity", response.toString());
				try {
					if (response.getString("lastmodified") != null) {
						List<Schedule> sbelongtoa = dbHelper
								.getSchedulesBelongtoActivity(id);
						for (int i = 0; i < sbelongtoa.size(); i++) {
							Schedule schedule = sbelongtoa.get(i);
							dbHelper.deleteRelatedOnduty(schedule
									.getSchedule_ID());
							dbHelper.deleteSchedule(schedule.getSchedule_ID());
						}
						dbHelper.deleteActivity(id);

						Log.i("delete activity", "successfully");
						Intent intent = new Intent(
								CommConstant.DELETE_ACTIVITY_COMPLETE);
						mContext.sendBroadcast(intent);

						// ((Activity) mContext).finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
				if (progress.isShowing()) {
					progress.dismiss();
				}
			}

			public void onFailure(Throwable e, String response) {
				// Response failed :
				Log.i("failure response", response);
				Log.i("fail", e.toString());
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.delete_activity_error)
								+ "\n" + response.toString(), Toast.LENGTH_LONG)
						.show();
			}
		});

	}

}
