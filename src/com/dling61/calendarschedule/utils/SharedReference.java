package com.dling61.calendarschedule.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedReference {
	final String USERNAME = "username";
	final String OWNER_ID = "owner_id";
	final String USER_ACCOUNT = "user_account";
	final String MY_PREFERENCE = "MyPreferences";
	final String TIMEZONE = "time_zone";

	/**
	 * Set username to shared reference
	 * */
	public void setUsername(Context context, String username) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				MY_PREFERENCE, 0);
		Editor prefsEditor = appSharedPrefs.edit();
		prefsEditor.putString(USERNAME, username);
		prefsEditor.commit();
	}

	/**
	 * Get username logged in shared reference
	 * */
	public String getUsername(Context context) {
		String username = "";
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				MY_PREFERENCE, 0);
		username = appSharedPrefs.getString(USERNAME, "");
		return username;
	}

	/**
	 * Get email logged in shared reference
	 * */
	public String getEmail(Context context) {
		String email = "";
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				MY_PREFERENCE, 0);
		email = appSharedPrefs.getString(CommConstant.EMAIL, "");
		return email;
	}

	/**
	 * Set owner id to shared reference
	 * */
	public void setOwnerId(Context context, String owner_id) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				MY_PREFERENCE, 0);
		Editor prefsEditor = appSharedPrefs.edit();
		prefsEditor.putString(OWNER_ID, owner_id);
		prefsEditor.commit();
	}

	/**
	 * Get username logged in shared reference
	 * */
	public String getOwnerId(Context context) {
		String username = "";
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				MY_PREFERENCE, 0);
		username = appSharedPrefs.getString(OWNER_ID, "");
		return username;
	}

	/**
	 * Set user account
	 * */
	public void setAccount(Context context, String jsonString) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				MY_PREFERENCE, 0);
		Editor prefsEditor = appSharedPrefs.edit();
		prefsEditor.putString(USER_ACCOUNT, jsonString);
		prefsEditor.commit();
	}

	/**
	 * Get current owner id logged in shared reference
	 * */
	public int getCurrentOwnerId(Context context) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				MY_PREFERENCE, 0);
		return appSharedPrefs.getInt(CommConstant.CURRENT_OWNER_ID, -1);
	}

	/**
	 * Set infor related account
	 * */
	public void setInformationUserLogined(Context context, String user_name,
			String useremail, int currentownerid, int nextserviceid,
			int nextmemberid, int nextscheduleid) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				MY_PREFERENCE, 0);
		Editor prefsEditor = appSharedPrefs.edit();
		prefsEditor.putString(CommConstant.USERNAME, user_name);
		prefsEditor.putString(CommConstant.EMAIL, useremail);
		prefsEditor.putInt(CommConstant.CURRENT_OWNER_ID, currentownerid);
		prefsEditor.putInt(CommConstant.NEXT_SERVICE_ID, nextserviceid);
		prefsEditor.putInt(CommConstant.NEXT_MEMBER_ID, nextmemberid);
		prefsEditor.putInt(CommConstant.NEXT_SCHEDULE_ID, nextscheduleid);
		prefsEditor.commit();
	}

	// get the lastest last_modified time from activity table
	public String getLastestServiceLastModifiedTime(Context mContext) {
		SharedPreferences sp = mContext.getSharedPreferences(MY_PREFERENCE, 0);
		String lastActivityModified = sp.getString(
				CommConstant.LAST_ACTIVITY_MODIFY, MyDate
						.transformLocalDateTimeToUTCFormat(MyDate
								.getCurrentDateTime()));
		return lastActivityModified;

	}

	public String getLastestParticipantLastModifiedTime(Context context) {
		SharedPreferences sp = context.getSharedPreferences(MY_PREFERENCE, 0);
		String lastParticipantModified = sp.getString(
		// Data is cleaned after exit
		// Current date should be written
				CommConstant.LAST_PARTICIPANT_MODIFY, MyDate
						.transformLocalDateTimeToUTCFormat(MyDate
								.getCurrentDateTime()));
		return lastParticipantModified;
	}

	public String getLastestScheduleLastModifiedTime(Context context) {
		SharedPreferences sp = context.getSharedPreferences(MY_PREFERENCE, 0);
		String lastScheduleModified = sp.getString(
				CommConstant.LAST_SCHEDULE_MODIFIED, MyDate
						.transformLocalDateTimeToUTCFormat(MyDate
								.getCurrentDateTime()));
		return lastScheduleModified;
	}

	/**
	 * setLastestParticipantLastModifiedTime
	 * */
	public void setLastestParticipantLastModifiedTime(Context context,
			String dateTime) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				MY_PREFERENCE, 0);
		Editor prefsEditor = appSharedPrefs.edit();
		prefsEditor.putString(CommConstant.LAST_PARTICIPANT_MODIFY, dateTime);
		prefsEditor.commit();
	}

	/**
	 * setLastestScheduleLastModifiedTime
	 * */
	public void setLastestScheduleLastModifiedTime(Context context,
			String dateTime) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				MY_PREFERENCE, 0);
		Editor prefsEditor = appSharedPrefs.edit();
		prefsEditor.putString(CommConstant.LAST_SCHEDULE_MODIFIED, dateTime);
		prefsEditor.commit();
	}

	/**
	 * setLastestServiceLastModifiedTime
	 * */
	public void setLastestServiceLastModifiedTime(Context context,
			String dateTime) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				MY_PREFERENCE, 0);
		Editor prefsEditor = appSharedPrefs.edit();
		prefsEditor.putString(CommConstant.LAST_UPDATE_TIME, dateTime);
		prefsEditor.commit();
	}

	/**
	 * setTimeZone
	 * */
	public void setTimeZone(Context context, String timeZone) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				MY_PREFERENCE, 0);
		Editor prefsEditor = appSharedPrefs.edit();
		prefsEditor.putString(CommConstant.LAST_SCHEDULE_MODIFIED, timeZone);
		prefsEditor.commit();
	}

	/**
	 * getTimeZone
	 * */
	public String getTimeZone(Context context) {
		SharedPreferences sp = context.getSharedPreferences(MY_PREFERENCE, 0);
		String timeZone = sp.getString(TIMEZONE, "");
		return timeZone;
	}

}
