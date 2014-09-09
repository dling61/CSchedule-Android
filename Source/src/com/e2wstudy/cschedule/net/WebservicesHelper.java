/**
 * Develop by Antking
 * */
package com.e2wstudy.cschedule.net;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.e2wstudy.cschedule.CategoryTabActivity;
import com.e2wstudy.cschedule.MyApplication;
import com.e2wstudy.cschedule.R;
import com.e2wstudy.cschedule.db.DatabaseHelper;
import com.e2wstudy.cschedule.models.ActivityTable;
import com.e2wstudy.cschedule.models.Confirm;
import com.e2wstudy.cschedule.models.MyActivity;
import com.e2wstudy.cschedule.models.OndutyTable;
import com.e2wstudy.cschedule.models.Participant;
import com.e2wstudy.cschedule.models.ParticipantTable;
import com.e2wstudy.cschedule.models.Schedule;
import com.e2wstudy.cschedule.models.ScheduleTable;
import com.e2wstudy.cschedule.models.SharedMemberTable;
import com.e2wstudy.cschedule.models.Sharedmember;
import com.e2wstudy.cschedule.models.TimeZoneTable;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.MyDate;
import com.e2wstudy.cschedule.utils.SharedReference;
import com.e2wstudy.cschedule.utils.Utils;
import com.e2wstudy.cschedule.views.LoadingPopupViewHolder;
import com.e2wstudy.cschedule.views.ToastDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.e2wstudy.cschedule.models.AppVersionTable;
import com.e2wstudy.schedule.interfaces.ActvityInterface;
import com.e2wstudy.schedule.interfaces.AddUpdateScheduleInterface;
import com.e2wstudy.schedule.interfaces.ContactInterface;
import com.e2wstudy.schedule.interfaces.FeedbackInterface;
import com.e2wstudy.schedule.interfaces.GetParticipantInterface;
import com.e2wstudy.schedule.interfaces.GetServerSettingInterface;
import com.e2wstudy.schedule.interfaces.LoadingInterface;
import com.e2wstudy.schedule.interfaces.LoginInterface;
import com.e2wstudy.schedule.interfaces.ScheduleInterface;
import com.e2wstudy.schedule.interfaces.SetTokenInterface;
import com.e2wstudy.schedule.interfaces.SharedMemberInterface;
import com.e2wstudy.schedule.interfaces.SignUpInterface;
import com.e2wstudy.schedule.interfaces.UpdateConfirmStatusInterface;
import com.google.android.gms.internal.ac;

/**
 * @class WebservicesHelper
 * @author Huyen Nguyen
 * @version 1.0
 * @Date: April 8th,2014 @ This class helps all process call webservices}
 * */
public class WebservicesHelper {
	ProgressDialog progress = null;
	public static LoadingPopupViewHolder loadingPopup;
	public static final int DIALOG_LOADING_THEME = android.R.style.Theme_Translucent_NoTitleBar;
	String gcmId;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	String TAG = "WebservicesHelper";
	static final String CONTENT_TYPE = "application/json";
	protected static WebservicesHelper instance;
	protected static DefaultHttpClient client;

	/**
	 * Constructor initial progress dialog
	 * */
	public static WebservicesHelper getInstance() {
		if (instance == null) {
			instance = new WebservicesHelper();
			client = new DefaultHttpClient();
		}
		return instance;
	}

	/**
	 * Create account If success, go to login else show toast notify create
	 * account failure
	 * */
	public void createAccount(final Context context, final String email,
			final String passWord, final String userName, String mobile,
			final LoadingInterface loadingInterface,
			final SignUpInterface signUpInterface) {

		String signUpUrl = BaseUrl.BASEURL + "creator?action=register" + "&"
				+ BaseUrl.URL_POST_FIX;
		StringEntity entity = ParamRegistry.paramSignUp(email, passWord,
				userName, mobile);
		if (Utils.isNetworkOnline(context)) {
			MyApplication.clientRequest().post(context, signUpUrl, entity,
					CONTENT_TYPE, new AsyncHttpResponseHandler() {

						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							loadingInterface.onStart();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							loadingInterface.onFinish();

						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							Log.i("successful sign up response",
									String.valueOf(responseText));
							if (statusCode == 200) {
								try {
									JSONObject response = new JSONObject(
											responseText);
									String ownerId = response
											.getString(CommConstant.OWNER_ID);
									if (ownerId != null) {
										new SharedReference().setOwnerId(
												context, ownerId);
										signUpInterface.onComplete(email,
												passWord);

									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									signUpInterface.onFailure(e.getMessage());
								}
							} else {
								JSONObject response;
								try {
									response = new JSONObject(responseText);
									String error = response
											.getString("error message");
									signUpInterface.onFailure(String
											.valueOf(error));
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							signUpInterface.onFailure(String
									.valueOf(responseText));

						}
					});
		} else {
			signUpInterface.onFailure(context.getResources().getString(
					R.string.no_network));

		}

	}

	/**
	 * Login if success go to TabAcivity else show Toast notify login fail
	 * */
	public void login(final Context context, final String email,
			final String passWord, final LoginInterface loginInterface,
			final LoadingInterface loadingInterface) throws JSONException {
		String signInUrl = BaseUrl.BASEURL + "creator?action=signin" + "&"
				+ BaseUrl.URL_POST_FIX;
		Log.i("sign in url is", signInUrl);
		StringEntity entity = ParamLogin.paramLogin(email, passWord);
		if (Utils.isNetworkOnline(context)) {
			MyApplication.clientRequest().post(context, signInUrl, entity,
					CONTENT_TYPE, new AsyncHttpResponseHandler() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							loadingInterface.onStart();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							loadingInterface.onFinish();
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							Log.i("successful login",
									String.valueOf(responseText));
							onLoginSuccessComplete(context, responseText, email);
							loginInterface.onComplete();
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							loginInterface.onError(String.valueOf(responseText));

						}
					});
		} else {

			loginInterface.onError(String.valueOf(context.getResources()
					.getString(R.string.no_network)));

		}

	}

	/**
	 * Login if success go to TabAcivity else show Toast notify login fail
	 * */
	public void setToken(final Context context, String userId, String deviceId,
			String registrationId, final SetTokenInterface setTokenInterface,
			final LoadingInterface loadingInterface) throws JSONException {
		String setToken = BaseUrl.BASEURL + "creator?action=settoken" + "&"
				+ BaseUrl.URL_POST_FIX;
		Log.i("set token url is", setToken);
		StringEntity entity = ParamSetToken.paramSetToken(userId, deviceId,
				registrationId);
		if (Utils.isNetworkOnline(context)) {
			MyApplication.clientRequest().post(context, setToken, entity,
					CONTENT_TYPE, new AsyncHttpResponseHandler() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							loadingInterface.onStart();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							loadingInterface.onFinish();
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							Log.i("successful set token",
									String.valueOf(responseText));
							if (statusCode == 201) {
								// final ToastDialog dialog = new
								// ToastDialog(Context,
								// "The token can’t be inserted");
								// dialog.show();
								//
								// dialog.btnOk.setOnClickListener(new
								// OnClickListener() {
								//
								// @Override
								// public void onClick(View v) {
								// dialog.dismiss();
								// }
								// });
								setTokenInterface
										.onError("The token can’t be inserted");
							} else if (statusCode == 202) {
								setTokenInterface
										.onError("The token can’t be updated");
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							setTokenInterface.onError(String
									.valueOf(responseText));

						}
					});
		} else {

			setTokenInterface.onError(String.valueOf(context.getResources()
					.getString(R.string.no_network)));

		}

	}

	/**
	 * get all activity of current owner id
	 * */
	public void getAllActivitys(final Context context,
			final LoadingInterface loginInterface,
			final ActvityInterface activityInterface,
			final ScheduleInterface scheduleInterface,
			final SharedMemberInterface sharedMemberInterface) {
		String activityUrl = BaseUrl.BASEURL + "services" + "?"
				+ BaseUrl.URL_POST_FIX;
		Log.i("get all activity url is :", activityUrl);
		final SharedReference ref = new SharedReference();
		int currentOwnerID = ref.getCurrentOwnerId(context);
		RequestParams params = new RequestParams();
		params.put(CommConstant.OWNER_ID, String.valueOf(currentOwnerID));
		params.put(CommConstant.LAST_UPDATE_TIME,
				ref.getLastestServiceLastModifiedTime(context));
		// params.put(CommConstant.LAST_UPDATE_TIME, "2014-04-20 04:17:26");
		Log.d("param activity all", params.toString());

		try {
			if (Utils.isNetworkOnline(context)) {

				MyApplication.clientRequest().get(activityUrl, params,
						new AsyncHttpResponseHandler() {

							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								super.onStart();
								loadingInterface.onStart();
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								super.onFinish();
								loadingInterface.onFinish();

							}

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] responseBody) {
								Log.d("load contact", "success");
								String responseText = Utils
										.convertBytesArrayToString(responseBody);

								final SharedReference ref = new SharedReference();

								Log.i("get all activity",
										responseText.toString());
								try {
									// deleted services and schedule
									// relationship with
									// this
									// service
									JSONObject response = new JSONObject(
											responseText);
									JSONArray deleted_services = response
											.getJSONArray("deletedservices");
									int deleted_services_count = deleted_services
											.length();
									if (deleted_services_count > 0) {
										for (int i = 0; i < deleted_services_count; i++) {
											String id = deleted_services
													.getString(i);
											List<Schedule> sbelongtoa = DatabaseHelper
													.getSharedDatabaseHelper(
															context)
													.getSchedulesBelongtoActivity(
															id);
											for (int j = 0; j < sbelongtoa
													.size(); j++) {
												Schedule schedule = sbelongtoa
														.get(j);
												DatabaseHelper
														.getSharedDatabaseHelper(
																context)
														.deleteRelatedOnduty(
																schedule.getSchedule_ID());
												DatabaseHelper
														.getSharedDatabaseHelper(
																context)
														.deleteSchedule(
																schedule.getSchedule_ID());
											}
											DatabaseHelper
													.getSharedDatabaseHelper(
															context)
													.deleteActivity(id);
										}
									}

									// services
									JSONArray services = response
											.getJSONArray("services");
									int service_count = services.length();

									for (int i = 0; i < service_count; i++) {
										JSONObject service = services
												.getJSONObject(i);
										ContentValues newActivity = new ContentValues();
										int ownid = service.getInt("creatorid");
										newActivity.put(ActivityTable.own_ID,
												ownid);
										Log.i("getActivitiesFromWeb own_ID ",
												ownid + "");
										String activityid = service
												.getString("serviceid");

										String serviceName = service
												.getString("servicename");
										newActivity.put(
												ActivityTable.service_Name,
												serviceName);
										Log.i("getActivitiesFromWeb service_Name ",
												serviceName + "");
										int role = service.getInt("sharedrole");
										newActivity.put(
												ActivityTable.sharedrole, role);
										Log.i("getActivitiesFromWeb sharedrole ",
												role + "");
										String description = service
												.getString("desp");
										newActivity
												.put(ActivityTable.service_description,
														description);

										Log.i("getActivitiesFromWeb service_description ",
												description + "");

										int is_deleted = 0;
										newActivity.put(
												ActivityTable.is_Deleted,
												is_deleted);
										int is_synchronized = 1;
										newActivity.put(
												ActivityTable.is_Synchronized,
												is_synchronized);
										newActivity
												.put(ActivityTable.user_login,
														new SharedReference()
																.getCurrentOwnerId(context));
										String last_modified = service
												.getString("lastmodified");
										newActivity
												.put(ActivityTable.last_ModifiedTime,
														last_modified);
										Log.i("getActivitiesFromWeb lastmodified ",
												last_modified + "");

										// if (DatabaseHelper
										// .getSharedDatabaseHelper(
										// context)
										// .isActivityExisted(activityid) ==
										// false) {
										newActivity.put(
												ActivityTable.service_ID,
												activityid);
										Log.i("getActivitiesFromWeb service_ID ",
												activityid + "");
										if (DatabaseHelper
												.getSharedDatabaseHelper(
														context)
												.insertActivity(newActivity))
											Log.i("database", "insert service "
													+ serviceName
													+ " successfully!");
										// } else {
										// if (DatabaseHelper
										// .getSharedDatabaseHelper(
										// context)
										// .updateActivity(activityid,
										// newActivity))
										// Log.i("database",
										// "update service "
										// + serviceName
										// + " successfully!");
										// }
										getSharedmembersForActivity(context,
												activityid, loadingInterface,
												sharedMemberInterface);

										// TODO: will delete if service get all
										// schedule
										// implemented

										getSchedulesForActivity(context,
												activityid, loadingInterface,
												scheduleInterface);

									}

									ref.setLastestServiceLastModifiedTime(
											context,
											MyDate.transformPhoneDateTimeToUTCFormat(MyDate
													.getCurrentDateTime()));

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] responseBody,
									Throwable error) {
								activityInterface.onError(context
										.getResources().getString(
												R.string.error_load_activity));
							}
						});
			} else {
				final ToastDialog dialog = new ToastDialog(context, context
						.getResources().getString(R.string.no_network));
				dialog.show();
				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void onLoginSuccessComplete(final Context context,
			String responseText, String email) {
		try {
			JSONObject response = new JSONObject(responseText);
			String username = response.getString("username");
			SharedReference sharedReference = new SharedReference();
			String lastUsername = sharedReference.getUsername(context);
			if (!lastUsername.equals(username)) {
				sharedReference.setLastestServiceLastModifiedTime(context,
						CommConstant.DEFAULT_DATE);
				sharedReference.setLastestParticipantLastModifiedTime(context,
						CommConstant.DEFAULT_DATE);
				sharedReference.setLastestScheduleLastModifiedTime(context,
						CommConstant.DEFAULT_DATE);
			}
			sharedReference.setUsername(context, username);

			sharedReference.setAccount(context, response.toString());
			int ownerid = response.getInt("ownerid");
			Log.i("SignIn ownerid ", ownerid + "");
			int idCostant = ownerid * 10000;

			sharedReference.setCurrentParticipant(context, idCostant + "");

			// Check id
			int nextserviceidOriginal = response.getInt("serviceid");
			Log.i("nextserviceidOriginal", nextserviceidOriginal + "");
			int nextmemberidOriginal = response.getInt("memberid");
			Log.i("nextmemberidOriginal", nextmemberidOriginal + "");
			int nextscheduleidOriginal = response.getInt("scheduleid");
			Log.i("nextscheduleidOriginal", nextscheduleidOriginal + "");
			int nextserviceid = -1;
			// If it is the first time to establish an
			// activity
			if (nextserviceidOriginal == 0) {
				nextserviceid = idCostant + nextserviceidOriginal + 1;

			}
			// If activities has established
			else {
				nextserviceid = nextserviceidOriginal + 1;
				Log.i("SignIn nextserviceid ", nextserviceid + "");
			}
			int nextmemberid = -1;
			// If it is the first time to establish a
			// member
			if (nextmemberidOriginal == 0) {
				nextmemberid = idCostant + nextmemberidOriginal + 1;
				Log.i("SignIn nextserviceid ", nextmemberid + "");
			}
			// If members has established
			else {
				nextmemberid = nextmemberidOriginal + 1;
				Log.i("SignIn nextserviceid ", nextmemberid + "");
			}
			int nextscheduleid = -1;
			// If it is the first time to establish a
			// schedule
			if (nextscheduleidOriginal == 0) {
				nextscheduleid = idCostant + nextscheduleidOriginal + 1;
				Log.i("SignIn nextscheduleid ", nextscheduleid + "");
			} else {
				nextscheduleid = nextscheduleidOriginal + 1;
				Log.i("SignIn nextscheduleid ", nextscheduleid + "");
			}

			int nextSharedMemberId = -1;
			// If it is the first time to establish a
			// schedule
			if (nextscheduleidOriginal == 0) {
				nextscheduleid = idCostant + nextscheduleidOriginal + 1;
				Log.i("SignIn nextscheduleid ", nextscheduleid + "");
			} else {
				nextscheduleid = nextscheduleidOriginal + 1;
				Log.i("SignIn nextscheduleid ", nextscheduleid + "");
			}

			sharedReference.setInformationUserLogined(context, username, email,
					ownerid, nextserviceid, nextmemberid, nextscheduleid);
			uploadRecentEditedActivitiesToWeb(context);
			uploadRecentEditedParticipantsToWeb(context);
			uploadRecentNewActivitiesToWeb(context);
			uploadRecentNewParticipantsToWeb(context);
			uploadRecentNewSchedulesToWeb(context);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get schedule for activity
	 * */
	public void getSchedules(final Context context,
			final LoadingInterface loadingInterface,
			final ScheduleInterface scheduleInterface) {
		String schedulesUrl = BaseUrl.BASEURL + "schedules" + "?"
				+ BaseUrl.URL_POST_FIX;
		final SharedReference ref = new SharedReference();
		int currentOwnerID = ref.getCurrentOwnerId(context);
		Log.i("schedules url:", schedulesUrl);
		String lastestScheduleLastModifiedTime = ref
				.getLastestScheduleLastModifiedTime(context);
		RequestParams params = ParamScheduleForActivity
				.paramScheduleForActivity(String.valueOf(currentOwnerID),
						lastestScheduleLastModifiedTime);
		try {

			if (Utils.isNetworkOnline(context)) {
				MyApplication.clientRequest().get(schedulesUrl, params,
						new AsyncHttpResponseHandler() {

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] responseBody) {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								Log.i("get schedule ", responseText);
								getScheduleForActivityComplete(context,
										responseText);
								scheduleInterface.onComplete();
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] responseBody,
									Throwable error) {
								Log.i("get schedule", "error");
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								scheduleInterface.onError(responseText);

							}

							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								super.onStart();
								loadingInterface.onStart();
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								super.onFinish();
								loadingInterface.onFinish();
							}
						});

			} else {
				scheduleInterface.onError(context.getResources().getString(
						R.string.no_network));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Get schedule for activity
	 * */
	public void getSchedulesForActivity(final Context context,
			final String activityid, final LoadingInterface loadingInterface,
			final ScheduleInterface scheduleInterface) {
		String schedulesUrl = BaseUrl.BASEURL + "services/" + activityid
				+ "/schedules" + "?" + BaseUrl.URL_POST_FIX;
		final SharedReference ref = new SharedReference();
		int currentOwnerID = ref.getCurrentOwnerId(context);
		Log.i("schedules url:", schedulesUrl);
		String lastestScheduleLastModifiedTime = ref
				.getLastestScheduleLastModifiedTime(context);
		RequestParams params = ParamScheduleForActivity
				.paramScheduleForActivity(String.valueOf(currentOwnerID),
						lastestScheduleLastModifiedTime);
		try {

			if (Utils.isNetworkOnline(context)) {
				MyApplication.clientRequest().get(schedulesUrl, params,
						new AsyncHttpResponseHandler() {

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] responseBody) {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								Log.i("get schedule item with activity="
										+ activityid, responseText);
								getScheduleForActivityComplete(context,
										responseText);
								scheduleInterface.onComplete();
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] responseBody,
									Throwable error) {
								Log.i("get schedule item with activity="
										+ activityid, "error");
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								scheduleInterface.onError(responseText);

							}

							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								super.onStart();
								loadingInterface.onStart();
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								super.onFinish();
								loadingInterface.onFinish();
							}
						});

			} else {
				scheduleInterface.onError(context.getResources().getString(
						R.string.no_network));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void getScheduleForActivityComplete(final Context context,
			String responseText) {
		try {
			JSONObject response = new JSONObject(responseText);
			JSONArray deletedschedules = response
					.getJSONArray("deletedschedules");
			int deleted_services_count = deletedschedules.length();
			if (deleted_services_count > 0) {
				for (int i = 0; i < deleted_services_count; i++) {
					int id = deletedschedules.getInt(i);
					// if (DatabaseHelper.getSharedDatabaseHelper(context)
					// .isOndutyExisted(id)) {
					if (DatabaseHelper.getSharedDatabaseHelper(context)
							.deleteRelatedOnduty(id)) {
						// }
					}
					DatabaseHelper.getSharedDatabaseHelper(context)
							.deleteSchedule(id);
				}
			}

			JSONArray schedules = response.getJSONArray("schedules");
			int scheudule_count = schedules.length();
			for (int i = 0; i < scheudule_count; i++) {
				JSONObject schedule = schedules.getJSONObject(i);
				ContentValues cv = new ContentValues();
				cv.put(ScheduleTable.own_ID, schedule.getInt("creatorid"));

				cv.put(ScheduleTable.last_Modified,
						schedule.getString("lastmodified"));
				cv.put(ScheduleTable.start_Time,
						schedule.getString("startdatetime"));
				cv.put(ScheduleTable.schedule_Description,
						schedule.getString("desp"));
				cv.put(ScheduleTable.end_Time,
						schedule.getString("enddatetime"));
				cv.put(ScheduleTable.service_ID, schedule.getInt("serviceid"));
				cv.put(ScheduleTable.is_Deleted, 0);
				cv.put(ScheduleTable.is_Synchronized, 1);
				cv.put(ActivityTable.user_login,
						new SharedReference().getCurrentOwnerId(context));
				cv.put(ScheduleTable.timeZone, schedule.getString("tzid"));
				cv.put(ScheduleTable.alert, schedule.getString("alert"));

				int scheduleID = schedule.getInt("scheduleid");
				// if (DatabaseHelper.getSharedDatabaseHelper(context)
				// .isScheduleExisted(scheduleID) == false) {
				cv.put(ScheduleTable.schedule_ID, schedule.getInt("scheduleid"));
				Log.i("scheduleid", "= " + schedule.getInt("scheduleid"));
				if (DatabaseHelper.getSharedDatabaseHelper(context)
						.insertSchedule(cv)) {
				}
				// } else {
				// if (DatabaseHelper.getSharedDatabaseHelper(context)
				// .updateSchedule(scheduleID, cv)) {
				// }
				// }

				// if (DatabaseHelper.getSharedDatabaseHelper(context)
				// .isOndutyExisted(scheduleID)) {
				if (DatabaseHelper.getSharedDatabaseHelper(context)
						.deleteRelatedOnduty(scheduleID)) {
					// }
				}

				JSONArray members = schedule.getJSONArray("members");
				if (members != null) {
					int size = members.length();
					for (int j = 0; j < size; j++) {
						JSONObject obj = members.getJSONObject(j);

						int memberId = obj.getInt("memberid");
						int confirmId = obj.getInt("confirm");
						ContentValues newOnduty = new ContentValues();
						newOnduty.put(OndutyTable.service_ID,
								schedule.getInt("serviceid"));
						newOnduty.put(OndutyTable.participant_ID, memberId);
						newOnduty.put(OndutyTable.confirm, confirmId);
						newOnduty.put(OndutyTable.schedule_ID,
								schedule.getInt("scheduleid"));
						newOnduty.put(OndutyTable.last_Modified,
								schedule.getString("lastmodified"));
						newOnduty.put(OndutyTable.is_Deleted, 0);
						newOnduty.put(OndutyTable.is_Synchronized, 1);

						if (DatabaseHelper.getSharedDatabaseHelper(context)
								.insertOnduty(newOnduty)) {

						}

					}
				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getParticipantsFromWeb(final Context context,
			final GetParticipantInterface getParticipantInterface,
			final LoadingInterface loadingInterface) {
		String participantUrl = BaseUrl.BASEURL + "members" + "?"
				+ BaseUrl.URL_POST_FIX;
		Log.i("url getParticipantsFromWeb is :", participantUrl);
		final SharedReference ref = new SharedReference();
		int currentOwnerID = ref.getCurrentOwnerId(context);
		String lastestParticipantLastModifiedTime = ref
				.getLastestParticipantLastModifiedTime(context);
		RequestParams params = ParamContact.paramContact(
				String.valueOf(currentOwnerID),
				lastestParticipantLastModifiedTime);
		Log.d("param load contact", params.toString());
		try {

			if (Utils.isNetworkOnline(context)) {

				MyApplication.clientRequest().get(participantUrl, params,
						new AsyncHttpResponseHandler() {

							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								super.onStart();
								loadingInterface.onStart();
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								super.onFinish();
								loadingInterface.onFinish();

							}

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] responseBody) {

								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								Log.d("load contact", responseText);
								getParticipantFromWebComplete(context,
										responseText);

								getParticipantInterface.onComplete();
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] responseBody,
									Throwable error) {
								Log.d("load contact", "error");
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								getParticipantInterface.onError(context
										.getResources().getString(
												R.string.error_load_contact)
										+ ": " + responseText);

							}
						});
			} else {
				final ToastDialog dialog = new ToastDialog(context, context
						.getResources().getString(R.string.no_network));
				dialog.show();
				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void getParticipantFromWebComplete(final Context context,
			String responseText) {
		Log.i("successful response get participant from web",
				responseText.toString());
		try {

			// deleted member, delete member from
			// activity which that
			// member joined in
			JSONObject response = new JSONObject(responseText);
			JSONArray deleted_member = response.getJSONArray("deletedmembers");
			int delete_members_count = deleted_member.length();
			if (delete_members_count > 0) {

				for (int i = 0; i < delete_members_count; i++) {
					int id = deleted_member.getInt(i);
					DatabaseHelper.getSharedDatabaseHelper(context)
							.deleteParticipant(id);

				}
			}

			JSONArray participants = response.getJSONArray("members");
			int participant_count = participants.length();
			for (int i = 0; i < participant_count; i++) {
				JSONObject participant = participants.getJSONObject(i);
				int ownerid = participant.getInt("creatorid");

				// Owner is included in JSON Response
				// "members"
				// Should not appear in Participants

				ContentValues cv = new ContentValues();
				cv.put(ParticipantTable.last_Modified,
						participant.getString("lastmodified"));
				cv.put(ParticipantTable.participant_Name,
						participant.getString("membername"));
				cv.put(ParticipantTable.own_ID, ownerid);
				Log.i("getParticipantsFromWeb own_ID ", ownerid + "");

				String participant_Mobile = participant
						.getString("mobilenumber");
				cv.put(ParticipantTable.participant_Mobile, participant_Mobile);
				Log.i("getParticipantsFromWeb participant_Mobile ",
						participant_Mobile);
				String participant_Email = participant.getString("memberemail");
				Log.i("getParticipantsFromWeb participant_Email ",
						participant_Email);
				cv.put(ParticipantTable.participant_Email, participant_Email);
				cv.put(ParticipantTable.is_Registered, 1);
				cv.put(ParticipantTable.is_Deleted, 0);
				cv.put(ParticipantTable.is_Sychronized, 1);
				cv.put(ActivityTable.user_login,
						new SharedReference().getCurrentOwnerId(context));
				int participantID = participant.getInt("memberid");

				// if (DatabaseHelper.getSharedDatabaseHelper(context)
				// .isParticipantExisted(participantID) == false) {
				cv.put(ParticipantTable.participant_ID, participantID);
				Log.i("getParticipantsFromWeb participant_ID ", participantID
						+ "");
				if (DatabaseHelper.getSharedDatabaseHelper(context)
						.insertParticipant(cv))
					Log.i("database",
							"insert participant "
									+ participant.getString("membername")
									+ " successfully!");
				// } else {
				// if (DatabaseHelper.getSharedDatabaseHelper(context)
				// .updateParticipant(participantID, cv))
				// Log.i("database",
				// "update participant "
				// + participant.getString("membername")
				// + " successfully!");
				// }
			}

			if (participant_count > 0) {
				new SharedReference().setLastestParticipantLastModifiedTime(
						context, MyDate
								.transformPhoneDateTimeToUTCFormat(MyDate
										.getCurrentDateTime()));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get server setting from server
	 * */
	public void getServerSetting(final Context context,
			final GetServerSettingInterface serverSettingInterface,
			final LoadingInterface loadingInterface) {
		String serverSetting = BaseUrl.BASEURL + "serversetting" + "?"
				+ BaseUrl.URL_POST_FIX;
		Log.i("url server setting is=", serverSetting);
		final SharedReference ref = new SharedReference();
		int currentOwnerID = ref.getCurrentOwnerId(context);
		RequestParams params = ParamServerSetting.paramServerSetting(
				String.valueOf(currentOwnerID),
				ref.getLastestParticipantLastModifiedTime(context));
		new RequestParams();
		try {
			if (Utils.isNetworkOnline(context)) {

				MyApplication.clientRequest().get(serverSetting, params,
						new AsyncHttpResponseHandler() {

							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								super.onStart();
								loadingInterface.onStart();
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								super.onFinish();
								loadingInterface.onFinish();

							}

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] responseBody) {

								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								Log.d("server setting success", responseText);
								getServerSettingComplete(context, responseText);

							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] responseBody,
									Throwable error) {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								serverSettingInterface.onError(responseText);

							}
						});
			} else {
				serverSettingInterface.onError(context.getResources()
						.getString(R.string.no_network));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void getServerSettingComplete(final Context context,
			String responseText) {
		try {
			JSONObject response = new JSONObject(responseText);
			JSONArray timeZoneList = response.getJSONArray("timezones");
			int timeZoneListSize = timeZoneList.length();
			for (int i = 0; i < timeZoneListSize; i++) {
				JSONObject timeZone = timeZoneList.getJSONObject(i);
				int id = timeZone.getInt("id");

				String timeZoneName = timeZone.getString("tzname");
				String displayname = timeZone.getString("displayname");
				String displayorder = timeZone.getString("displayorder");
				String abbrtzname = timeZone.getString("abbrtzname");
				String tzname = timeZone.getString("tzname");
				ContentValues cv = new ContentValues();

				cv.put(TimeZoneTable.abbrtzname, abbrtzname);
				cv.put(TimeZoneTable.displayname, displayname);

				cv.put(TimeZoneTable.displayorder, displayorder);
				cv.put(TimeZoneTable.tzname, tzname);

				// if (DatabaseHelper.getSharedDatabaseHelper(context)
				// .isTimeZoneExisted(id) == false) {
				cv.put(TimeZoneTable.id, id);
				if (DatabaseHelper.getSharedDatabaseHelper(context)
						.insertTimeZone(cv))
					Log.i("database", "insert timezone " + timeZoneName
							+ " successfully!");
				// } else {
				// if (DatabaseHelper.getSharedDatabaseHelper(context)
				// .updateTimeZone(id, cv))
				// Log.i("database", "update timezone " + timeZoneName
				// + " successfully!");
				// }
			}

			/**
			 * Alerts
			 * */
			JSONArray alertList = response.getJSONArray("alerts");
			int alertSize = alertList.length();
			for (int i = 0; i < alertSize; i++) {
				JSONObject timeZone = alertList.getJSONObject(i);
				int id = timeZone.getInt("id");

				String alert = timeZone.getString("aname");

				ContentValues cv = new ContentValues();
				cv.put(com.e2wstudy.cschedule.models.AlertTable.aname, alert);
				// if (DatabaseHelper.getSharedDatabaseHelper(context)
				// .isAlertExisted(id) == false) {
				cv.put(com.e2wstudy.cschedule.models.AlertTable.id, id);
				if (DatabaseHelper.getSharedDatabaseHelper(context)
						.insertAlert(cv))
					Log.i("database", "insert timezone " + alert
							+ " successfully!");
				// } else {
				// if (DatabaseHelper.getSharedDatabaseHelper(context)
				// .updateAlert(id, cv))
				// Log.i("database", "update timezone " + alert
				// + " successfully!");
				// }
			}

			/**
			 * app version
			 * */
			JSONArray appVersions = response.getJSONArray("appversions");
			int appVersionSize = appVersions.length();
			for (int i = 0; i < appVersionSize; i++) {
				JSONObject version = appVersions.getJSONObject(i);
				int id = version.getInt("id");
				String appversion = version.getString("appversion");
				int enforce = version.getInt("enforce");
				String os = version.getString("os");
				String osversion = version.getString("osversion");
				ContentValues cv = new ContentValues();
				cv.put(AppVersionTable.appversion, appversion);
				cv.put(AppVersionTable.os, os);
				cv.put(AppVersionTable.osversion, osversion);
				cv.put(AppVersionTable.enforce, enforce);

				// if (DatabaseHelper.getSharedDatabaseHelper(context)
				// .isVersionExisted(id) == false) {
				cv.put(AppVersionTable.id, id);
				if (DatabaseHelper.getSharedDatabaseHelper(context)
						.insertAppVersion(cv))
					Log.i("database", "insert appversion " + osversion
							+ " successfully!");
				// } else {
				// if (DatabaseHelper.getSharedDatabaseHelper(context)
				// .updateAppVersion(id, cv))
				// Log.i("database", "update appversion " + osversion
				// + " successfully!");
				// }
			}

			CommConstant.DOWNLOAD_SETTING = true;
			//
			// do {
			// Utils.checkCurrentVersion(context);
			// } while (!CommConstant.UPDATE);
			//
			// Intent intent = new
			// Intent(CommConstant.SERVER_SETTING_SUCCESSFULLY);
			// context.sendBroadcast(intent);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Add new activity A request body { “ownerid”: 123434, “services”: {
	 * “serviceid”: “2222222”, “servicename”: “Food Service”, “desp”: “This is a
	 * cleaning service”, } }
	 * */
	public void addActivity(final Context context, final MyActivity activity,
			final LoadingInterface loadingInterface,
			final ActvityInterface addNewActivityInterface) {
		String activityUrl = BaseUrl.BASEURL + "services" + "?"
				+ BaseUrl.URL_POST_FIX;
		Log.d("url add new activity", activityUrl);

		StringEntity entity = ParamAddNewActivitty
				.paramAddNewActivity(activity);

		if (Utils.isNetworkOnline(context)) {
			MyApplication.clientRequest().post(context, activityUrl, entity,
					CONTENT_TYPE, new AsyncHttpResponseHandler() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							loadingInterface.onStart();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							loadingInterface.onFinish();
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							try {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								Log.d("add new activity success", responseText);
								ContentValues cv = new ContentValues();
								JSONObject response = new JSONObject(
										responseText);
								String last_modified = response
										.getString("lastmodified");
								cv.put(ActivityTable.last_ModifiedTime,
										last_modified);
								cv.put(ActivityTable.is_Synchronized, 1);
								DatabaseHelper.getSharedDatabaseHelper(context)
										.updateActivity(
												activity.getActivity_ID(), cv);
								Log.i("last_modified", last_modified);
								if (DatabaseHelper.getSharedDatabaseHelper(
										context).updateActivity(
										activity.getActivity_ID(), cv)) {
									// Toast.makeText(this,
									// "insert activity successfully",
									// Toast.LENGTH_LONG).show();
								}

								SharedReference ref = new SharedReference();
								ref.setLastestServiceLastModifiedTime(context,
										last_modified);

								// Intent intent = new Intent(
								// CommConstant.ACTIVITY_DOWNLOAD_SUCCESS);
								// context.sendBroadcast(intent);

								addNewActivityInterface.onComplete();
								getSharedmembersForActivity(context,
										activity.getActivity_ID(),
										loadingInterface,
										new SharedMemberInterface() {

											@Override
											public void onError(String error) {
												// TODO Auto-generated method
												// stub

											}

											@Override
											public void onComplete() {
												Intent intent = new Intent(
														CommConstant.GET_SHARED_MEMBER_ACTIVITY_COMPLETE);
												context.sendBroadcast(intent);
											}
										});

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								addNewActivityInterface
										.onError("Have an error occur");
							}

						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							addNewActivityInterface.onError(String
									.valueOf(responseText));

						}
					});
		} else {
			addNewActivityInterface.onError(context.getResources().getString(
					R.string.no_network));

		}

	}

	/**
	 * add new schedule
	 * 
	 * */
	public void addSchedule(final Context context, Schedule schedule,
			List<Confirm> pins, final LoadingInterface loadingInterface,
			final AddUpdateScheduleInterface addNewScheduleInterface) {
		String scheduleUrl = BaseUrl.BASEURL + "schedules" + "?"
				+ BaseUrl.URL_POST_FIX;
		Log.d("create new scheduleUrl", scheduleUrl);
		final int id = schedule.getSchedule_ID();
		StringEntity entity = ParamAddNewSchedule.paramAddNewSchedule(schedule,
				pins);
		if (Utils.isNetworkOnline(context)) {
			MyApplication.clientRequest().post(context, scheduleUrl, entity,
					CONTENT_TYPE, new AsyncHttpResponseHandler() {

						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							loadingInterface.onStart();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							loadingInterface.onFinish();

						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							Log.d("add new schedule",
									String.valueOf(responseText));
							try {
								JSONObject response = new JSONObject(
										responseText);
								ContentValues cv = new ContentValues();
								String last_modified = response
										.getString("lastmodified");
								cv.put(ScheduleTable.last_Modified,
										last_modified);
								cv.put(ScheduleTable.is_Synchronized, 1);
								DatabaseHelper.getSharedDatabaseHelper(context)
										.updateSchedule(id, cv);
								Log.i("last_modified", last_modified);

								// go to schedule
								// if (CategoryTabActivity.currentPage !=
								// CategoryTabActivity.TAB_SCHEDULE) {
								CategoryTabActivity.currentPage = CategoryTabActivity.TAB_SCHEDULE;
								addNewScheduleInterface.onComplete();

								// Intent intent = new Intent(
								// CommConstant.UPDATE_SCHEDULE);
								// context.sendBroadcast(intent);
								//
								// ((Activity) context).finish();
								// Utils.postLeftToRight(context);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								addNewScheduleInterface
										.onError("Have an error occur");
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							addNewScheduleInterface.onError(context
									.getResources().getString(
											R.string.create_schedule_error)
									+ " " + String.valueOf(responseText));
						}
					});

		} else {
			addNewScheduleInterface.onError(context.getResources().getString(
					R.string.no_network));
		}
	}

	/**
	 * update schedule
	 * */
	public void updateSchedule(final Context context, Schedule schedule,
			List<Confirm> pins, final LoadingInterface loadingInterface,
			final AddUpdateScheduleInterface addUpdateScheduleInterface) {
		String scheduleUrl = BaseUrl.BASEURL + "schedules/"
				+ schedule.getSchedule_ID() + "?" + BaseUrl.URL_POST_FIX;
		Log.d("update schedule url", scheduleUrl);
		final int id = schedule.getSchedule_ID();
		StringEntity entity = ParamUpdateSchedule.paramUpdateSchedule(schedule,
				pins);

		if (Utils.isNetworkOnline(context)) {
			MyApplication.clientRequest().put(context, scheduleUrl, entity,
					CONTENT_TYPE, new AsyncHttpResponseHandler() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							// showLoading(context);
							loadingInterface.onStart();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							loadingInterface.onFinish();

						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							Log.d("update schedule success", responseText);
							try {

								JSONObject response = new JSONObject(
										responseText);
								ContentValues cv = new ContentValues();
								String last_modified = response
										.getString("lastmodified");
								cv.put(ScheduleTable.last_Modified,
										last_modified);
								cv.put(ScheduleTable.is_Synchronized, 1);
								DatabaseHelper.getSharedDatabaseHelper(context)
										.updateSchedule(id, cv);

								addUpdateScheduleInterface.onComplete();

								CategoryTabActivity.currentPage = CategoryTabActivity.TAB_SCHEDULE;

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								addUpdateScheduleInterface
										.onError("Have an error occur");
							}

						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							addUpdateScheduleInterface.onError(responseText);

						}
					});
		} else {
			addUpdateScheduleInterface.onError(context.getResources()
					.getString(R.string.no_network));
		}
	}

	public void updateConfirmStatus(final Context context,
			final Schedule schedule, final Confirm pin,
			final LoadingInterface loadingInterface,
			final UpdateConfirmStatusInterface updateConfirmStatusInterface) {
		String updateConfirmStatus = BaseUrl.BASEURL + "schedules/"
				+ schedule.getSchedule_ID() + "/onduty/" + pin.getMemberId()
				+ "?" + BaseUrl.URL_POST_FIX;
		Log.d("update confirm status url", updateConfirmStatus);
		final int id = schedule.getSchedule_ID();
		StringEntity entity = ParamUpdateConfirmStatus
				.paramUpdateConfirmStatus(String.valueOf(new SharedReference()
						.getCurrentOwnerId(context)), pin.getConfirm());
		if (Utils.isNetworkOnline(context)) {
			MyApplication.clientRequest().put(context, updateConfirmStatus,
					entity, "", new AsyncHttpResponseHandler() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							loadingInterface.onStart();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							loadingInterface.onFinish();

						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							Log.d("updateConfirmStatus success", responseText);
							try {
								JSONObject response = new JSONObject(
										responseText);
								ContentValues cv = new ContentValues();
								String last_modified = response
										.getString("lastmodified");
								cv.put(OndutyTable.last_Modified, last_modified);
								cv.put(OndutyTable.is_Synchronized, 1);
								cv.put(OndutyTable.confirm, pin.getConfirm());
								cv.put(OndutyTable.schedule_ID,
										schedule.getSchedule_ID());
								DatabaseHelper.getSharedDatabaseHelper(context)
										.updateOnduty(
												schedule.getSchedule_ID(),
												pin.getMemberId(), cv);
								updateConfirmStatusInterface.onComplete();

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								updateConfirmStatusInterface
										.onError("Have an error occur");
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							updateConfirmStatusInterface.onError(context
									.getResources().getString(
											R.string.confirm_error)
									+ " " + responseText.toString());
						}
					});
		} else {
			updateConfirmStatusInterface.onError(context.getResources()
					.getString(R.string.no_network));
		}
	}

	/**
	 * delete schedule
	 * */
	public void deleteSchedule(final Context context, Schedule schedule,
			final AddUpdateScheduleInterface scheduleInterface,
			final LoadingInterface loadingInterface) {
		String scheduleUrl = BaseUrl.BASEURL + "schedules/"
				+ schedule.getSchedule_ID() + "?" + BaseUrl.URL_POST_FIX;
		final int schedule_id = schedule.getSchedule_ID();
		if (Utils.isNetworkOnline(context)) {
			MyApplication.clientRequest().delete(context, scheduleUrl,
					new AsyncHttpResponseHandler() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							loadingInterface.onStart();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							loadingInterface.onFinish();

						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							Log.d("delete schedule success", responseText);
							try {
								JSONObject response = new JSONObject(
										responseText);
								if (response.getString("lastmodified") != null) {

									DatabaseHelper.getSharedDatabaseHelper(
											context).deleteRelatedOnduty(
											schedule_id);
									DatabaseHelper.getSharedDatabaseHelper(
											context)
											.deleteSchedule(schedule_id);
									scheduleInterface.onComplete();

									// Intent intent = new Intent(
									// CommConstant.DELETE_SCHEDULE_COMPLETE);
									// context.sendBroadcast(intent);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							scheduleInterface.onError(context.getResources()
									.getString(R.string.delete_schedule_error)
									+ " " + responseText.toString());
						}
					});
		} else {
			scheduleInterface.onError(context.getResources().getString(
					R.string.no_network));
		}
	}

	/**
	 * send feedback
	 * */
	public void sendFeedBack(final Context context, String feedBackStr,
			final FeedbackInterface feedbackInterface,
			final LoadingInterface loadingInterface) {

		String feedback = BaseUrl.BASEURL + "feedback" + "?"
				+ BaseUrl.URL_POST_FIX;
		Log.d("feedback link", feedback);
		try {
			JSONObject sharedmemberParams = new JSONObject();
			sharedmemberParams.put("ownerid",
					new SharedReference().getCurrentOwnerId(context));
			sharedmemberParams.put("feedback", feedBackStr);

			StringEntity entity = new StringEntity(
					sharedmemberParams.toString());
			Log.d("body feedback", sharedmemberParams.toString());
			if (Utils.isNetworkOnline(context)) {
				MyApplication.clientRequest().post(context, feedback, entity,
						CONTENT_TYPE, new AsyncHttpResponseHandler() {
							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								super.onStart();
								loadingInterface.onStart();
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								super.onFinish();
								loadingInterface.onFinish();

							}

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] responseBody) {
								if (statusCode == 200) {
									feedbackInterface
											.onComplete(context
													.getResources()
													.getString(
															R.string.send_feedback_success));

								} else if (statusCode == 201) {
									feedbackInterface
											.onError(context
													.getResources()
													.getString(
															R.string.invalid_content_feedback));
								}
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] responseBody,
									Throwable error) {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								feedbackInterface.onError(context
										.getResources().getString(
												R.string.send_feedback_error));
							}
						});
			} else {
				feedbackInterface.onError(context.getResources().getString(
						R.string.no_network));
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void uploadRecentNewActivitiesToWeb(final Context context) {
		List<MyActivity> unsyncedActivities = DatabaseHelper
				.getSharedDatabaseHelper(context).getUnsyncedNewActivities();
		ActvityInterface iActivity = new ActvityInterface() {

			@Override
			public void onError(String error) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub

			}
		};
		for (int i = 0; i < unsyncedActivities.size(); i++) {
			MyActivity ma = unsyncedActivities.get(i);

			addActivity(context, ma, loadingInterface, iActivity);
		}
	}

	ActvityInterface activityInterface = new ActvityInterface() {

		@Override
		public void onError(String error) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete() {
			// TODO Auto-generated method stub

		}
	};

	public void uploadRecentEditedActivitiesToWeb(final Context context) {
		try {
			List<MyActivity> unsyncedActivities = DatabaseHelper
					.getSharedDatabaseHelper(context)
					.getUnsyncedEditedActivities();
			for (int i = 0; i < unsyncedActivities.size(); i++) {
				MyActivity ma = unsyncedActivities.get(i);
				updateActivity(context, ma, loadingInterface, activityInterface);
			}
		} catch (Exception ex) {

		}
	}

	public void uploadRecentNewParticipantsToWeb(final Context context) {
		List<Participant> unsyncedParticipants = DatabaseHelper
				.getSharedDatabaseHelper(context).getUnsyncedNewParticipants();
		for (int i = 0; i < unsyncedParticipants.size(); i++) {
			Participant p = unsyncedParticipants.get(i);
			addParticipant(context, p, loadingInterface, contactInterface);
		}
	}

	ContactInterface contactInterface = new ContactInterface() {

		@Override
		public void onError(String error) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete() {
			// TODO Auto-generated method stub

		}
	};

	public void uploadRecentEditedParticipantsToWeb(final Context context) {
		List<Participant> unsyncedParticipants = DatabaseHelper
				.getSharedDatabaseHelper(context)
				.getUnsyncedEditedParticipants();
		for (int i = 0; i < unsyncedParticipants.size(); i++) {
			Participant p = unsyncedParticipants.get(i);
			this.updateParticipant(context, p, loadingInterface,
					contactInterface);
		}
	}

	LoadingInterface loadingInterface = new LoadingInterface() {

		@Override
		public void onStart() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub

		}
	};
	AddUpdateScheduleInterface addUpdateScheduleInterface = new AddUpdateScheduleInterface() {

		@Override
		public void onError(String error) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete() {
			// TODO Auto-generated method stub

		}
	};

	public void uploadRecentNewSchedulesToWeb(final Context context) {
		List<Schedule> unsyncedSchedules = DatabaseHelper
				.getSharedDatabaseHelper(context).getUnsyncedNewSchedules();

		for (int i = 0; i < unsyncedSchedules.size(); i++) {
			Schedule s = unsyncedSchedules.get(i);
			List<Confirm> members = DatabaseHelper.getSharedDatabaseHelper(
					context).getParticipantsForSchedule(s.getSchedule_ID());
			addSchedule(context, s, members, loadingInterface,
					addUpdateScheduleInterface);
		}
	}

	public void uploadRecentEditedSchedulesToWeb(final Context context) {
		List<Schedule> unsyncedSchedules = DatabaseHelper
				.getSharedDatabaseHelper(context).getUnsyncedEditedSchedules();
		for (int i = 0; i < unsyncedSchedules.size(); i++) {
			Schedule s = unsyncedSchedules.get(i);
			List<Confirm> members = DatabaseHelper.getSharedDatabaseHelper(
					context).getParticipantsForSchedule(s.getSchedule_ID());
			updateSchedule(context, s, members, loadingInterface,
					addUpdateScheduleInterface);
		}
	}

	/**
	 * Get member shared for actiivty
	 * */
	public void getSharedmembersForActivity(final Context context,
			final String activity_id, final LoadingInterface loginInterface,
			final SharedMemberInterface sharedMemberInterface) {
		String sharedmembersUrl = BaseUrl.BASEURL + "services/" + activity_id
				+ "/sharedmembers" + "?" + BaseUrl.URL_POST_FIX;
		Log.i("shared member url :", sharedmembersUrl);
		int currentOwnerID = context.getSharedPreferences("MyPreferences", 0)
				.getInt("currentownerid", 0);
		RequestParams params = new RequestParams();
		params.put("ownerid", String.valueOf(currentOwnerID));
		params.put("lastupdatetime", "");
		if (Utils.isNetworkOnline(context)) {
			MyApplication.clientRequest().get(sharedmembersUrl, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							Log.i("successful get sharedmemberforactivity",
									responseText.toString());
							try {
								JSONObject response = new JSONObject(
										responseText);
								JSONArray deleteMember = response
										.getJSONArray("deletedsmembers");
								int deleted_member_count = deleteMember
										.length();
								if (deleted_member_count > 0) {
									for (int i = 0; i < deleted_member_count; i++) {
										int id = deleteMember.getInt(i);
										DatabaseHelper.getSharedDatabaseHelper(
												context).deleteSharedmember(id,
												activity_id);
									}
								}

								JSONArray JSharedmembers = response
										.getJSONArray("sharedmembers");
								int sm_count = JSharedmembers.length();
								for (int i = 0; i < sm_count; i++) {
									JSONObject JSharedmember = JSharedmembers
											.getJSONObject(i);
									int sm_id = JSharedmember
											.getInt("memberid");
									String sm_email = JSharedmember
											.getString("memberemail");
									String sm_number = JSharedmember
											.getString("mobilenumber");
									String sm_name = JSharedmember
											.getString("membername");
									int sm_role = JSharedmember
											.getInt("sharedrole");
									String sm_lastmdf = JSharedmember
											.getString("lastmodified");

									ContentValues cv = new ContentValues();

									cv.put(SharedMemberTable.member_email,
											sm_email);
									cv.put(SharedMemberTable.member_name,
											sm_name);
									cv.put(SharedMemberTable.member_mobile,
											sm_number);
									cv.put(SharedMemberTable.service_id,
											activity_id);
									cv.put(SharedMemberTable.role, sm_role);

									cv.put(SharedMemberTable.last_modified,
											sm_lastmdf);
									cv.put(SharedMemberTable.is_Deleted, 0);
									cv.put(SharedMemberTable.is_Synced, 1);
									 if
									 (DatabaseHelper.getSharedDatabaseHelper(
									 context).isSharedmemberExisted(
									 sm_id, activity_id)) {
									 DatabaseHelper.getSharedDatabaseHelper(
									 context).updateSharedmember(
									 sm_id, activity_id, cv);
									 } else {
									cv.put(SharedMemberTable.member_id, sm_id);
									DatabaseHelper.getSharedDatabaseHelper(
											context).insertSharedmember(cv);
									 }
								}

								sharedMemberInterface.onComplete();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							loadingInterface.onStart();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							loadingInterface.onFinish();

						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							sharedMemberInterface.onError(context
									.getResources().getString(
											R.string.get_shared_member_error)
									+ ": " + responseText);
						}
					});
		} else {
			sharedMemberInterface.onError(context.getResources().getString(
					R.string.no_network));
		}
	}

	/**
	 * Share member to activity
	 * */
	public void postSharedmemberToActivity(final Context context,
			final int memberid, final int role, final String activityid,
			final LoadingInterface loadingInterface,
			final SharedMemberInterface sharedMemberInterface) {
		String sharedmemberUrl = BaseUrl.BASEURL + "services/" + activityid
				+ "/" + "sharedmembers" + "?" + BaseUrl.URL_POST_FIX;
		Log.d("share member url", sharedmemberUrl);
		try {
			JSONObject sharedmemberParams = new JSONObject();
			sharedmemberParams.put("ownerid",
					new SharedReference().getCurrentOwnerId(context));
			sharedmemberParams.put("sharedrole", role);
			sharedmemberParams.put("memberid", memberid);

			Log.d("post shared member", sharedmemberParams.toString());

			// Log.i("add participant", sharedmemberParams.toString());
			StringEntity entity = new StringEntity(
					sharedmemberParams.toString());

			if (Utils.isNetworkOnline(context)) {
				MyApplication.clientRequest().post(context, sharedmemberUrl,
						entity, CONTENT_TYPE, new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] responseBody) {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								Log.d("post shared member success",
										responseText);
								int code = 0;

								try {
									String lastmodify = "";
									JSONObject response = new JSONObject(
											responseText);
									lastmodify = response
											.getString("lastmodified");
									Participant member = DatabaseHelper
											.getSharedDatabaseHelper(context)
											.getParticipant(memberid);
									ContentValues cv = new ContentValues();
									cv.put(SharedMemberTable.member_name,
											member.getName());
									cv.put(SharedMemberTable.member_id,
											member.getID());
									cv.put(SharedMemberTable.member_email,
											member.getEmail());
									cv.put(SharedMemberTable.member_mobile,
											member.getMobile());
									cv.put(SharedMemberTable.role, role);
									cv.put(SharedMemberTable.is_Deleted, 0);
									cv.put(SharedMemberTable.last_modified,
											lastmodify);
									cv.put(SharedMemberTable.is_Synced, 1);
									DatabaseHelper.getSharedDatabaseHelper(
											context).updateSharedmember(
											member.getID(), cv);
									// Intent intent = new Intent(
									// CommConstant.GET_SHARED_MEMBER_ACTIVITY_COMPLETE);
									// intent.putExtra(CommConstant.ACTIVITY_ID,
									// activityid);
									// context.sendBroadcast(intent);
									sharedMemberInterface.onComplete();

									code = response.getInt("code");
									if (code != 200) {
										// Toast.makeText(context,
										// response.getString("message"),
										// Toast.LENGTH_LONG).show();
										sharedMemberInterface.onError(response
												.getString("message"));
									}

								} catch (JSONException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] responseBody,
									Throwable error) {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								Log.d("post shared member error",
										String.valueOf(responseText));
								sharedMemberInterface.onError(responseText);

							}

							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								super.onStart();
								loadingInterface.onStart();
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								super.onFinish();
								loadingInterface.onFinish();
							}
						});
			} else {
				final ToastDialog dialog = new ToastDialog(context, context
						.getResources().getString(R.string.no_network));
				dialog.show();
				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
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
	public void deleteSharedmemberOfActivity(final Context context,
			final int memberid, final String activityid,
			final LoadingInterface loadingInterface,
			final SharedMemberInterface sharedMemberInterface) {
		String sharedmemberUrl = BaseUrl.BASEURL + "services/" + activityid
				+ "/" + "sharedmembers/" + memberid + "?"
				+ BaseUrl.URL_POST_FIX;
		if (Utils.isNetworkOnline(context)) {
			MyApplication.clientRequest().delete(context, sharedmemberUrl,
					new AsyncHttpResponseHandler() {

						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							Log.e("remove device monitor", "" + statusCode);
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							Log.i("delete shared member of activity",
									responseText.toString());
							try {
								JSONObject response = new JSONObject(
										responseText);
								String lastmodified = response
										.getString("lastmodified");
								DatabaseHelper.getSharedDatabaseHelper(context)
										.deleteSharedmember(memberid,
												activityid);
								SharedReference ref = new SharedReference();
								ref.setLastestServiceLastModifiedTime(context,
										lastmodified);
								// Intent intent = new Intent(
								// CommConstant.GET_SHARED_MEMBER_ACTIVITY_COMPLETE);
								// context.sendBroadcast(intent);
								sharedMemberInterface.onComplete();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							loadingInterface.onStart();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							loadingInterface.onFinish();

						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							sharedMemberInterface.onError(context
									.getResources().getString(
											R.string.delete_participant_error)
									+ ": " + responseText);

						}
					});
		} else {
			sharedMemberInterface.onError(context.getResources().getString(
					R.string.no_network));
		}
	}

	// public void alterSharedmemberToActivity(int memberid, int role,
	// int activityid) {
	// String sharedmemberUrl = BaseUrl.BASEURL + "services/" + activityid
	// + "/" + "sharedmembers/" + memberid + "?"
	// + BaseUrl.URL_POST_FIX;
	// try {
	// JSONObject params = new JSONObject();
	// params.put("ownerid",
	// new SharedReference().getCurrentOwnerId(Context));
	// params.put("sharedrole", role);
	//
	// // client.addHeader("Content-type", "application/json");
	// Log.i("update participant", params.toString());
	// StringEntity entity = new StringEntity(params.toString());
	// if (Utils.isNetworkOnline(Context)) {
	// client.put(null, sharedmemberUrl, entity, "application/json",
	// new JsonHttpResponseHandler() {
	// public void onSuccess(JSONObject response) {
	// Log.i("successful response",
	// response.toString());
	//
	// }
	//
	// public void onFailure(Throwable e, String response) {
	// // Response failed :(
	//
	// Log.i("failure response", response);
	// Log.i("fail", e.toString());
	//
	// }
	//
	// @Override
	// public void onStart() {
	// // TODO Auto-generated method stub
	// super.onStart();
	// showLoading(Context);
	// }
	//
	// @Override
	// public void onFinish() {
	// // TODO Auto-generated method stub
	// super.onFinish();
	// try {
	// // if (progress.isShowing()) {
	// // progress.dismiss();
	// // }
	// dimissDialog();
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	//
	// }
	// });
	// } else {
	// final ToastDialog dialog=new ToastDialog(Context,
	// Context.getResources().getString(R.string.no_network));
	// dialog.show();
	// dialog.btnOk.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dialog.dismiss();
	// }
	// });
	// }
	// } catch (UnsupportedEncodingException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	public void addParticipant(final Context context, Participant participant,
			final LoadingInterface loginInterface,
			final ContactInterface contactInterface) {
		String url = BaseUrl.BASEURL + "members" + "?" + BaseUrl.URL_POST_FIX;
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

			// client.addHeader("Content-type", "application/json");
			Log.i("add participant", params.toString());
			StringEntity entity = new StringEntity(params.toString());

			if (Utils.isNetworkOnline(context)) {
				MyApplication.clientRequest().post(context, url, entity,
						CONTENT_TYPE, new AsyncHttpResponseHandler() {
							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								super.onStart();
								loginInterface.onStart();
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								super.onFinish();
								loginInterface.onFinish();
							}

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] responseBody) {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								Log.i("add participant response",
										responseText.toString());
								try {
									JSONObject response = new JSONObject(
											responseText);
									String last_modified = response
											.getString("lastmodified");
									if (last_modified != null
											&& (!last_modified.equals(""))) {
										ContentValues cv = new ContentValues();
										cv.put(ParticipantTable.last_Modified,
												last_modified);
										cv.put(ParticipantTable.is_Sychronized,
												1);

										DatabaseHelper.getSharedDatabaseHelper(
												context).updateParticipant(id,
												cv);
									} else {
										Toast.makeText(context,
												response.toString(),
												Toast.LENGTH_LONG).show();
									}
									// ((Activity) context).finish();
									// Utils.postLeftToRight(context);
									// Intent intent = new Intent(
									// CommConstant.ADD_CONTACT_SUCCESS);
									// context.sendBroadcast(intent);
									contactInterface.onComplete();

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] responseBody,
									Throwable error) {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								contactInterface.onError(context.getResources()
										.getString(
												R.string.add_participant_error)
										+ ": " + responseText);
							}
						});
			} else {
				contactInterface.onError(context.getResources().getString(
						R.string.no_network));
			}
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
	public void updateParticipant(final Context context,
			final Participant participant,
			final LoadingInterface loadingInterface,
			final ContactInterface contactInterface) {
		String url = BaseUrl.BASEURL + "members/" + participant.getID() + "?"
				+ BaseUrl.URL_POST_FIX;
		Log.i("updateParticipant ParticipantUrl ", url);
		final int id = participant.getID();
		try {
			JSONObject participantParams = new JSONObject();
			participantParams.put("email", participant.getEmail());
			participantParams.put("mobile", participant.getMobile());
			participantParams.put("membername", participant.getName());

			JSONObject params = new JSONObject();
			params.put("ownerid", participant.getOwnerID());
			params.put("members", participantParams);

			// client.addHeader("Content-type", "application/json");
			Log.i("update participant", params.toString());
			StringEntity entity = new StringEntity(params.toString());
			if (Utils.isNetworkOnline(context)) {
				MyApplication.clientRequest().put(context, url, entity,
						CONTENT_TYPE, new AsyncHttpResponseHandler() {
							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								super.onStart();
								loadingInterface.onStart();
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								super.onFinish();
								loadingInterface.onFinish();

							}

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] responseBody) {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								Log.d("update participant",
										responseText.toString());
								try {
									JSONObject response = new JSONObject(
											responseText);
									ContentValues cv = new ContentValues();
									String last_modified = response
											.getString("lastmodified");
									cv.put(ParticipantTable.last_Modified,
											last_modified);
									cv.put(ParticipantTable.is_Sychronized, 1);
									DatabaseHelper.getSharedDatabaseHelper(
											context).updateParticipant(id, cv);

									ContentValues contentValues = new ContentValues();
									contentValues.put(
											SharedMemberTable.member_email,
											participant.getEmail());
									contentValues.put(
											SharedMemberTable.member_mobile,
											participant.getMobile());
									contentValues.put(
											SharedMemberTable.member_name,
											participant.getName());
									DatabaseHelper.getSharedDatabaseHelper(
											context).updateSharedmember(
											participant.getID(), contentValues);

									// ((Activity) context).finish();
									// Utils.postLeftToRight(context);
									// Intent intent = new Intent(
									// CommConstant.ADD_CONTACT_SUCCESS);
									// context.sendBroadcast(intent);
									contactInterface.onComplete();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] responseBody,
									Throwable error) {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								contactInterface
										.onError(context
												.getResources()
												.getString(
														R.string.update_participant_error)
												+ ": " + responseText);
							}
						});
			} else {
				contactInterface.onError(context.getResources().getString(
						R.string.no_network));
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteParticipant(final Context context,
			final Participant participant,
			final LoadingInterface loadingInterface,
			final ContactInterface contactInterface) {
		String urlDeleteContact = BaseUrl.BASEURL + "members/"
				+ participant.getID() + "?" + BaseUrl.URL_POST_FIX;
		Log.d("delete contact", urlDeleteContact);
		final int id = participant.getID();
		if (Utils.isNetworkOnline(context)) {
			MyApplication.clientRequest().delete(context, urlDeleteContact,
					new AsyncHttpResponseHandler() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							loadingInterface.onStart();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							loadingInterface.onFinish();

						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							Log.d("delete str", responseText.toString());
							try {
								JSONObject response = new JSONObject(
										responseText);
								if (response.getString("lastmodified") != null) {

									ContentValues cv = new ContentValues();
									cv.put(ParticipantTable.is_Deleted, 1);
									cv.put(ParticipantTable.is_Sychronized, 1);

									DatabaseHelper.getSharedDatabaseHelper(
											context).deleteParticipant(id);

								}
								// ((Activity) context).finish();
								// Utils.postLeftToRight(context);
								// Intent intent = new Intent(
								// CommConstant.DELETE_CONTACT_COMPLETE);
								// context.sendBroadcast(intent);
								contactInterface.onComplete();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							contactInterface.onError(context.getResources()
									.getString(R.string.delete_contact_error)
									+ " " + responseText);
						}
					});
		} else {
			contactInterface.onError(context.getResources().getString(
					R.string.no_network));
		}
	}

	public void updateActivity(final Context context, MyActivity activity,
			final LoadingInterface loadingInterface,
			final ActvityInterface activityInterface) {
		String activityUrl = BaseUrl.BASEURL + "services/"
				+ activity.getActivity_ID() + "?" + BaseUrl.URL_POST_FIX;
		final String id = activity.getActivity_ID();
		try {
			JSONObject activityParams = new JSONObject();

			activityParams.put("servicename", activity.getActivity_name());
			activityParams.put("desp", activity.getDesp());

			JSONObject params = new JSONObject();
			params.put("ownerid", activity.getOwner_ID());
			params.put("services", activityParams);
			Log.i("update activity", params.toString());
			StringEntity entity = new StringEntity(params.toString());
			if (Utils.isNetworkOnline(context)) {
				MyApplication.clientRequest().put(context, activityUrl, entity,
						CONTENT_TYPE, new AsyncHttpResponseHandler() {
							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								super.onStart();
								loadingInterface.onStart();
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								super.onFinish();
								loadingInterface.onFinish();
							}

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] responseBody) {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								Log.i("update activity result", responseText);
								try {
									JSONObject response = new JSONObject(
											responseText);
									ContentValues cv = new ContentValues();
									String last_modified = response
											.getString("lastmodified");
									cv.put(ActivityTable.last_ModifiedTime,
											last_modified);
									cv.put(ActivityTable.is_Synchronized, 1);
									DatabaseHelper.getSharedDatabaseHelper(
											context).updateActivity(id, cv);
									Log.i("last_modified", last_modified);
									SharedReference ref = new SharedReference();
									ref.setLastestServiceLastModifiedTime(
											context, last_modified);

									// ((Activity) context).finish();
									// Utils.postLeftToRight(context);
									// Intent intent = new Intent(
									// CommConstant.ACTIVITY_DOWNLOAD_SUCCESS);
									// context.sendBroadcast(intent);
									activityInterface.onComplete();

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									activityInterface
											.onError("Have an error occur");
								}
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] responseBody,
									Throwable error) {
								String responseText = Utils
										.convertBytesArrayToString(responseBody);
								activityInterface.onError(context
										.getResources().getString(
												R.string.edit_activity_error)
										+ " " + responseText);

							}
						});
			} else {
				activityInterface.onError(context.getResources().getString(
						R.string.no_network));
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * delete activity
	 * */
	public void deleteActivity(final Context context, MyActivity activity,
			final LoadingInterface loadingInterface,
			final ActvityInterface activityInterface) {
		String url = BaseUrl.BASEURL + "services/" + activity.getActivity_ID()
				+ "?" + BaseUrl.URL_POST_FIX;
		final String id = activity.getActivity_ID();
		if (Utils.isNetworkOnline(context)) {
			MyApplication.clientRequest().delete(context, url,
					new AsyncHttpResponseHandler() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							loadingInterface.onStart();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							loadingInterface.onFinish();
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							Log.d("delete activity", responseText.toString());
							try {
								JSONObject response = new JSONObject(
										responseText);
								if (response.getString("lastmodified") != null) {
									ArrayList<Sharedmember> listSharedMemberOfActivity = DatabaseHelper
											.getSharedDatabaseHelper(context)
											.getSharedMemberForActivity(id);
									if (listSharedMemberOfActivity != null
											&& listSharedMemberOfActivity
													.size() > 0) {
										for (Sharedmember sharedMember : listSharedMemberOfActivity) {
											DatabaseHelper
													.getSharedDatabaseHelper(
															context)
													.deleteSharedmember(
															sharedMember
																	.getID(),
															id);
										}
									}

									List<Schedule> sbelongtoa = DatabaseHelper
											.getSharedDatabaseHelper(context)
											.getSchedulesBelongtoActivity(id);
									for (int i = 0; i < sbelongtoa.size(); i++) {
										ContentValues scv = new ContentValues();
										scv.put(ScheduleTable.is_Deleted, 1);
										scv.put(ScheduleTable.is_Synchronized,
												0);
										int schedule_id = sbelongtoa.get(i)
												.getSchedule_ID();
										// (sharedMember.getID(),id);

										List<Integer> onduties = DatabaseHelper
												.getSharedDatabaseHelper(
														context)
												.getOndutyRecordsForSchedule(
														schedule_id);
										for (int j = 0; j < onduties.size(); j++) {
											DatabaseHelper
													.getSharedDatabaseHelper(
															context)
													.deleteRelatedOnduty(
															schedule_id);
										}
										DatabaseHelper.getSharedDatabaseHelper(
												context).deleteSchedule(
												Integer.parseInt(id));
									}

									DatabaseHelper.getSharedDatabaseHelper(
											context).deleteActivity(id);

									Log.i("delete activity", "successfully");
									// Intent intent = new Intent(
									// CommConstant.DELETE_ACTIVITY_COMPLETE);
									// context.sendBroadcast(intent);

									activityInterface.onComplete();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							String responseText = Utils
									.convertBytesArrayToString(responseBody);
							activityInterface.onError(context.getResources()
									.getString(R.string.delete_activity_error)
									+ " " + responseText);
						}
					});
		} else {
			activityInterface.onError(context.getResources().getString(
					R.string.no_network));
		}
	}

}
