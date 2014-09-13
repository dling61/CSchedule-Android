package com.e2wstudy.cschedule.db;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.e2wstudy.cschedule.models.ActivityTable;
import com.e2wstudy.cschedule.models.Alert;
import com.e2wstudy.cschedule.models.AlertTable;
import com.e2wstudy.cschedule.models.AppVersion;
import com.e2wstudy.cschedule.models.AppVersionTable;
import com.e2wstudy.cschedule.models.Confirm;
import com.e2wstudy.cschedule.models.MyActivity;
import com.e2wstudy.cschedule.models.OndutyTable;
import com.e2wstudy.cschedule.models.Participant;
import com.e2wstudy.cschedule.models.ParticipantTable;
import com.e2wstudy.cschedule.models.Schedule;
import com.e2wstudy.cschedule.models.ScheduleTable;
import com.e2wstudy.cschedule.models.SharedMemberTable;
import com.e2wstudy.cschedule.models.Sharedmember;
import com.e2wstudy.cschedule.models.TimeZoneModel;
import com.e2wstudy.cschedule.models.TimeZoneTable;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.SharedReference;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "cschedule";
	public static final int DB_VERSION = 12;
	public static DatabaseHelper sharedDatabaseHelper;
	public static final int NEW = 0;
	public static final int EXISTED = 1;
	public static final int CREATOR = 0;
	public static final int ORGANIZOR = 1;
	public static final int PARTICIPANT = 2;
	public static final int VIEWER = 3;
	public static final int NOSHARE = 4;

	private Context context;

	DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	public static DatabaseHelper getSharedDatabaseHelper(Context context) {
		if (sharedDatabaseHelper == null) {
			sharedDatabaseHelper = new DatabaseHelper(context);
		}
		return sharedDatabaseHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i("database", "database is initializing");
		db.execSQL("CREATE TABLE "
				+ ParticipantTable.ParticipantTableName
				+ "("
				+ ParticipantTable.participant_ID
				// + " INTEGER PRIMARY KEY NOT NULL,"
				+ " INTEGER PRIMARY KEY NOT NULL UNIQUE,"
				+ ParticipantTable.participant_Name + " TEXT NOT NULL,"
				+ ParticipantTable.participant_Mobile + " TEXT,"
				+ ParticipantTable.participant_Email + " TEXT NOT NULL,"
				+ ParticipantTable.own_ID + " INTEGER NOT NULL,"
				+ ParticipantTable.last_Modified + " TEXT,"
				+ ParticipantTable.is_Deleted + " INTEGER NOT NULL,"
				+ ParticipantTable.is_Registered + " INTEGER NOT NULL,"
				+ ParticipantTable.user_login + " text not null,"
				+ ParticipantTable.is_Sychronized + " INTEGER NOT NULL);");

		db.execSQL("CREATE TABLE " + ActivityTable.ActivityTableName + "("
				+ ActivityTable.service_ID + " INTEGER PRIMARY KEY NOT NULL,"
				+ ActivityTable.own_ID + " INTEGER NOT NULL,"
				+ ActivityTable.service_Name + " TEXT NOT NULL,"
				+ ActivityTable.service_description + " TEXT,"
				+ ActivityTable.sharedrole + " INTEGER NOT NULL,"
				+ ActivityTable.last_ModifiedTime + " TEXT,"
				+ ActivityTable.is_Deleted + " INTEGER NOT NULL,"
				+ ParticipantTable.user_login + " text not null,"
				+ ActivityTable.alertId + " int," + ActivityTable.timeZoneId
				+ " int," + ActivityTable.is_Synchronized
				+ " INTEGER NOT NULL);");

		db.execSQL("CREATE TABLE " + ScheduleTable.ScheduleTableName + "("
				+ ScheduleTable.schedule_ID + " INTEGER PRIMARY KEY NOT NULL,"
				+ ScheduleTable.schedule_Description + " TEXT,"
				+ ScheduleTable.service_ID + " INTEGER NOT NULL,"
				+ ScheduleTable.start_Time + " TEXT NOT NULL,"
				+ ScheduleTable.end_Time + " TEXT NOT NULL,"
				+ ScheduleTable.own_ID + " INTEGER NOT NULL,"
				+ ScheduleTable.last_Modified + " TEXT," + ScheduleTable.alert
				+ " INTEGER NOT NULL," + ScheduleTable.timeZone
				+ " INTEGER NOT NULL," + ScheduleTable.is_Deleted
				+ " INTEGER NOT NULL," + ScheduleTable.user_login
				+ " text not null," + ScheduleTable.is_Synchronized
				+ " INTEGER NOT NULL);");

		db.execSQL("CREATE TABLE " + OndutyTable.OntudyTableName + "("
				+ OndutyTable.onduty_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+ OndutyTable.service_ID + " INTEGER NOT NULL,"
				+ OndutyTable.schedule_ID + " INTEGER NOT NULL,"
				+ OndutyTable.participant_ID + " INTEGER NOT NULL,"
				+ OndutyTable.is_Deleted + " INTEGER NOT NULL,"
				+ OndutyTable.is_Synchronized + " INTEGER NOT NULL,"
				+ OndutyTable.confirm + " INTEGER NOT NULL,"

				+ OndutyTable.last_Modified + " TEXT);");

		db.execSQL("CREATE TABLE " + SharedMemberTable.SharedMemberTableName
				+ "(" + SharedMemberTable.smid
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+ SharedMemberTable.service_id + " INTEGER NOT NULL,"
				+ SharedMemberTable.role + " INTEGER NOT NULL,"
				+ SharedMemberTable.member_id + " INTEGER NOT NULL,"
				+ SharedMemberTable.member_name + " TEXT NOT NULL,"
				+ SharedMemberTable.member_email + " TEXT NOT NULL,"
				+ SharedMemberTable.member_mobile + " TEXT,"
				+ SharedMemberTable.is_Deleted + " INTEGER NOT NULL,"
				+ SharedMemberTable.is_Synced + " INTEGER NOT NULL,"

				+ SharedMemberTable.last_modified + " TEXT);");

		db.execSQL("CREATE TABLE " + TimeZoneTable.TimeZoneTableName + "("
				+ TimeZoneTable.id + " INTEGER PRIMARY KEY NOT NULL,"
				+ TimeZoneTable.abbrtzname + " TEXT NOT NULL,"
				+ TimeZoneTable.displayname + " TEXT NOT NULL,"
				+ TimeZoneTable.displayorder + " TEXT NOT NULL,"
				+ TimeZoneTable.tzname + " TEXT NOT NULL);");

		db.execSQL("CREATE TABLE " + AlertTable.alertTableName + "("
				+ AlertTable.id + " INTEGER PRIMARY KEY NOT NULL,"
				+ AlertTable.aname + " TEXT NOT NULL);");

		db.execSQL("CREATE TABLE " + AppVersionTable.appVersionTable + "("
				+ AppVersionTable.id + " INTEGER PRIMARY KEY NOT NULL,"
				+ AppVersionTable.appversion + " TEXT NOT NULL,"
				+ AppVersionTable.enforce + " INTEGER NOT NULL,"
				+ AppVersionTable.os + " TEXT NOT NULL," + AppVersionTable.msg
				+ " text ," + AppVersionTable.osversion + " TEXT NOT NULL);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.i("database", "Database upgade from " + oldVersion + " to "
				+ newVersion);
		db.execSQL("DROP TABLE IF EXISTS " + ActivityTable.ActivityTableName);
		db.execSQL("DROP TABLE IF EXISTS "
				+ ParticipantTable.ParticipantTableName);
		db.execSQL("DROP TABLE IF EXISTS " + ScheduleTable.ScheduleTableName);
		db.execSQL("DROP TABLE IF EXISTS " + OndutyTable.OntudyTableName);
		db.execSQL("DROP TABLE IF EXISTS "
				+ SharedMemberTable.SharedMemberTableName);
		db.execSQL("DROP TABLE IF EXISTS " + TimeZoneTable.TimeZoneTableName);
		db.execSQL("DROP TABLE IF EXISTS " + AlertTable.alertTableName);
		db.execSQL("DROP TABLE IF EXISTS " + AppVersionTable.appVersionTable);

		onCreate(db);

	}

	/**
	 * get number of activity
	 * */
	public int getNumberActivity() {
		Cursor mCount = null;
		try {
			mCount = this.getWritableDatabase().rawQuery(
					"select count(*) from " + ActivityTable.ActivityTableName
							+ " where " + ActivityTable.is_Deleted + "=0 and "
							+ ActivityTable.user_login + "="
							+ new SharedReference().getCurrentOwnerId(context),
					null);
			if (mCount != null) {
				if (mCount.moveToFirst()) {
					int count = mCount.getInt(0);
					return count;
				}
			}

			mCount.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (mCount != null)
				mCount.close();
		}
		return 0;
	}

	public AppVersion getCurrentVersion() {
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * from " + AppVersionTable.appVersionTable
							+ " where " + AppVersionTable.os + " = 'ANDROID'",
					null);
			while (c != null && c.moveToNext()) {
				String appVersion = "";
				try {
					appVersion = c.getString(c
							.getColumnIndex(AppVersionTable.appversion));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				int appId = -1;
				try {
					appId = Integer.parseInt(c.getString(c
							.getColumnIndex(AppVersionTable.id)));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				String os = c.getString(c.getColumnIndex(AppVersionTable.os));
				String appversion = c.getString(c
						.getColumnIndex(AppVersionTable.appversion));
				int enforce = 0;
				try {
					enforce = Integer.parseInt(c.getString(c
							.getColumnIndex(AppVersionTable.enforce)));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				String osversion = "";
				try {
					osversion = c.getString(c
							.getColumnIndex(AppVersionTable.osversion));
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				String msg = "";
				try {
					msg = c.getString(c.getColumnIndex(AppVersionTable.msg));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				AppVersion version = new AppVersion(appId, appversion, enforce,
						os, osversion, msg);
				return version;
			}
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return null;
	}

	/**
	 * Get all timezone
	 * */
	public ArrayList<TimeZoneModel> getTimeZone() {
		ArrayList<TimeZoneModel> timeZones = new ArrayList<TimeZoneModel>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + TimeZoneTable.TimeZoneTableName
							+ " order by " + TimeZoneTable.displayorder
							+ " asc", null);
			while (c != null && c.moveToNext()) {
				int id = c.getInt(c.getColumnIndex(TimeZoneTable.id));
				String tzname = c.getString(c
						.getColumnIndex(TimeZoneTable.tzname));
				Log.d("timezone name", tzname);
				String displayname = c.getString(c
						.getColumnIndex(TimeZoneTable.displayname));
				Log.d("display name", displayname);
				String displayorder = c.getString(c
						.getColumnIndex(TimeZoneTable.displayorder));
				String abbrtzname = c.getString(c
						.getColumnIndex(TimeZoneTable.abbrtzname));

				TimeZoneModel timeZone = new TimeZoneModel(id, tzname,
						displayname, displayorder, abbrtzname);
				timeZones.add(timeZone);
			}
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return timeZones;
	}

	/**
	 * Get all alerts
	 * */
	public ArrayList<Alert> getAlerts() {
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + AlertTable.alertTableName, null);
			while (c != null && c.moveToNext()) {
				int id = c.getInt(c.getColumnIndex(AlertTable.id));
				String aname = c.getString(c.getColumnIndex(AlertTable.aname));

				Alert alert = new Alert(id, aname);
				alerts.add(alert);
			}
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return alerts;
	}

	public ArrayList<MyActivity> getActivities() {
		ArrayList<MyActivity> activities = new ArrayList<MyActivity>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ActivityTable.ActivityTableName
							+ " where " + ActivityTable.is_Deleted + "=0 and "
							+ ActivityTable.user_login + "='"
							+ new SharedReference().getCurrentOwnerId(context)
							+ "'", null);
			while (c != null && c.moveToNext()) {
				String id = c.getString(c
						.getColumnIndex(ActivityTable.service_ID));
				int ownid = c.getInt(c.getColumnIndex(ActivityTable.own_ID));
				String name = c.getString(c
						.getColumnIndex(ActivityTable.service_Name));
				String desp = c.getString(c
						.getColumnIndex(ActivityTable.service_description));
				int role = c.getInt(c.getColumnIndex(ActivityTable.sharedrole));
				int alertId = c.getInt(c.getColumnIndex(ActivityTable.alertId));
				int timezoneId = c.getInt(c
						.getColumnIndex(ActivityTable.timeZoneId));
				MyActivity newActivity = new MyActivity(id, ownid, name, desp,
						role, alertId, timezoneId);
				activities.add(newActivity);
			}
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return activities;
	}

	/**
	 * Get all activity which haven role: owner or organizer
	 * */
	public ArrayList<MyActivity> getActivitiesOwnerOrOrganizer(String user_id) {
		ArrayList<MyActivity> activities = new ArrayList<MyActivity>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ActivityTable.ActivityTableName
							+ " where " + ActivityTable.is_Deleted + "=0 and "
							+ ActivityTable.user_login + "='"
							+ new SharedReference().getCurrentOwnerId(context)
							+ "'" + " and (" + ActivityTable.sharedrole + " = "
							+ CommConstant.OWNER + " or "
							+ ActivityTable.sharedrole + " = "
							+ CommConstant.ORGANIZER + ")", null);
			while (c != null && c.moveToNext()) {
				String id = c.getString(c
						.getColumnIndex(ActivityTable.service_ID));
				int ownid = c.getInt(c.getColumnIndex(ActivityTable.own_ID));
				String name = c.getString(c
						.getColumnIndex(ActivityTable.service_Name));
				String desp = c.getString(c
						.getColumnIndex(ActivityTable.service_description));
				int role = c.getInt(c.getColumnIndex(ActivityTable.sharedrole));
				int alertId = c.getInt(c.getColumnIndex(ActivityTable.alertId));
				int timezoneId = c.getInt(c
						.getColumnIndex(ActivityTable.timeZoneId));
				MyActivity newActivity = new MyActivity(id, ownid, name, desp,
						role, alertId, timezoneId);
				activities.add(newActivity);
			}
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return activities;
	}

	public List<MyActivity> getUnsyncedNewActivities() {
		List<MyActivity> activities = new ArrayList<MyActivity>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ActivityTable.ActivityTableName
							+ " WHERE " + ActivityTable.is_Synchronized + "= 0"
							+ " "
							+ "AND "
							// + ActivityTable.own_ID + "= "+ ownid + " " +
							// "AND "
							+ ActivityTable.last_ModifiedTime + " LIKE"
							+ "'no%'", null);
			while (c != null && c.moveToNext()) {
				String id = c.getString(c
						.getColumnIndex(ActivityTable.service_ID));
				int ownid = c.getInt(c.getColumnIndex(ActivityTable.own_ID));
				Log.i("getUnsyncedNewActivities ownid ", ownid + "");
				String name = c.getString(c
						.getColumnIndex(ActivityTable.service_Name));
				String desp = c.getString(c
						.getColumnIndex(ActivityTable.service_description));
				int role = c.getInt(c.getColumnIndex(ActivityTable.sharedrole));
				int alertId = c.getInt(c.getColumnIndex(ActivityTable.alertId));
				int timezoneId = c.getInt(c
						.getColumnIndex(ActivityTable.timeZoneId));
				MyActivity newActivity = new MyActivity(id, ownid, name, desp,
						role, alertId, timezoneId);
				activities.add(newActivity);
			}
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return activities;
	}

	public List<MyActivity> getUnsyncedEditedActivities() {
		List<MyActivity> activities = new ArrayList<MyActivity>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ActivityTable.ActivityTableName
							+ " WHERE " + ActivityTable.is_Synchronized + "= 0"
							+ " " + "AND " + ActivityTable.last_ModifiedTime
							+ " LIKE" + "'2%'", null);
			while (c != null && c.moveToNext()) {
				String id = c.getString(c
						.getColumnIndex(ActivityTable.service_ID));
				int ownid = c.getInt(c.getColumnIndex(ActivityTable.own_ID));
				String name = c.getString(c
						.getColumnIndex(ActivityTable.service_Name));
				String desp = c.getString(c
						.getColumnIndex(ActivityTable.service_description));
				int role = c.getInt(c.getColumnIndex(ActivityTable.sharedrole));
				int timeZoneId = c.getInt(c
						.getColumnIndex(ActivityTable.timeZoneId));
				int alertId = c.getInt(c.getColumnIndex(ActivityTable.alertId));

				MyActivity newActivity = new MyActivity(id, ownid, name, desp,
						role, alertId, timeZoneId);
				activities.add(newActivity);
			}
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return activities;
	}

	public MyActivity getActivity(String service_id) {
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ActivityTable.ActivityTableName
							+ " WHERE " + ActivityTable.service_ID + " = "
							+ service_id, null);

			if (c != null && c.moveToNext()) {
				String id = c.getString(c
						.getColumnIndex(ActivityTable.service_ID));
				int ownid = c.getInt(c.getColumnIndex(ActivityTable.own_ID));
				String name = c.getString(c
						.getColumnIndex(ActivityTable.service_Name));
				String desp = c.getString(c
						.getColumnIndex(ActivityTable.service_description));
				int role = c.getInt(c.getColumnIndex(ActivityTable.sharedrole));
				int alertId = c.getInt(c.getColumnIndex(ActivityTable.alertId));
				int timezoneId = c.getInt(c
						.getColumnIndex(ActivityTable.timeZoneId));
				MyActivity newActivity = new MyActivity(id, ownid, name, desp,
						role, alertId, timezoneId);
				return newActivity;
			}
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return null;
	}

	public Schedule getScheduleSortedByID(int id) {
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ScheduleTable.ScheduleTableName
							+ " WHERE " + ScheduleTable.schedule_ID + " = "
							+ id, null);
			while (c != null && c.moveToNext()) {
				int sche_id = c.getInt(c
						.getColumnIndex(ScheduleTable.schedule_ID));
				int owner_id = c.getInt(c.getColumnIndex(ScheduleTable.own_ID));
				int serv_id = c.getInt(c
						.getColumnIndex(ScheduleTable.service_ID));
				String startDate = c.getString(c
						.getColumnIndex(ScheduleTable.start_Time));
				String endDate = c.getString(c
						.getColumnIndex(ScheduleTable.end_Time));
				String desp = c.getString(c
						.getColumnIndex(ScheduleTable.schedule_Description));
				int timeZone = c.getInt(c
						.getColumnIndex(ScheduleTable.timeZone));
				int alert = c.getInt(c.getColumnIndex(ScheduleTable.alert));
				Schedule newSchedule = new Schedule(owner_id, sche_id, serv_id
						+ "", startDate, endDate, desp, alert, timeZone);
				return newSchedule;
			}
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return null;
	}

	public List<Schedule> getSchedulesBelongtoActivity(String id) {
		Cursor c = null;
		List<Schedule> schedules = new ArrayList<Schedule>();
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ScheduleTable.ScheduleTableName
							+ " WHERE " + ScheduleTable.service_ID + " = " + id
							+ " AND " + ScheduleTable.is_Deleted + "=0", null);

			while (c != null && c.moveToNext()) {
				int sche_id = c.getInt(c
						.getColumnIndex(ScheduleTable.schedule_ID));
				int owner_id = c.getInt(c.getColumnIndex(ScheduleTable.own_ID));
				int serv_id = c.getInt(c
						.getColumnIndex(ScheduleTable.service_ID));
				String startDate = c.getString(c
						.getColumnIndex(ScheduleTable.start_Time));
				String endDate = c.getString(c
						.getColumnIndex(ScheduleTable.end_Time));
				String desp = c.getString(c
						.getColumnIndex(ScheduleTable.schedule_Description));

				int timeZone = c.getInt(c
						.getColumnIndex(ScheduleTable.timeZone));
				int alert = c.getInt(c.getColumnIndex(ScheduleTable.alert));

				Schedule newSchedule = new Schedule(owner_id, sche_id, serv_id
						+ "", startDate, endDate, desp, alert, timeZone);
				schedules.add(newSchedule);
			}
			c.close();

		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return schedules;
	}

	public List<List<Schedule>> getScheduleSortedByDate() {
		String columnName = "strftime('%Y-%m-%d'," + ScheduleTable.start_Time
				+ ")";
		List<List<Schedule>> groupedSchedules = new ArrayList<List<Schedule>>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT " + columnName + " FROM "
							+ ScheduleTable.ScheduleTableName + " WHERE "
							+ ScheduleTable.is_Deleted + "=0" + " ORDER BY "
							+ columnName + " ASC", null);
			List<String> dates = new ArrayList<String>();
			List<String> distinctdates = new ArrayList<String>();
			while (c != null && c.moveToNext()) {
				int columnIndex = c.getColumnIndex(columnName);
				String Date = c.getString(columnIndex);
				// Log.i("database", Date);
				dates.add(Date);
			}

			String previousDate = "2";
			for (int i = 0; i < dates.size(); i++) {
				String newDate = dates.get(i);
				if (newDate.equalsIgnoreCase(previousDate) == false) {
					distinctdates.add(newDate);
				}
				previousDate = newDate;
			}

			for (int j = 0; j < distinctdates.size(); j++) {
				List<Schedule> schedules = new ArrayList<Schedule>();
				String date = distinctdates.get(j);
				Cursor c1 = null;
				try {
					c1 = this.getWritableDatabase().rawQuery(
							"SELECT * FROM " + ScheduleTable.ScheduleTableName
									+ " WHERE " + ScheduleTable.start_Time
									+ " LIKE " + "'" + date + "%'", null);
					while (c1.moveToNext()) {
						int startIndex = c1
								.getColumnIndex(ScheduleTable.start_Time);
						String startDate = c1.getString(startIndex);
						int endIndex = c1
								.getColumnIndex(ScheduleTable.end_Time);
						String endDate = c1.getString(endIndex);
						int scheduleIDIndex = c1
								.getColumnIndex(ScheduleTable.schedule_ID);
						int sche_id = c1.getInt(scheduleIDIndex);
						int serviceIndex = c1
								.getColumnIndex(ScheduleTable.service_ID);
						int serv_id = c1.getInt(serviceIndex);
						int ownerIndex = c1
								.getColumnIndex(ScheduleTable.own_ID);
						int owner_id = c1.getInt(ownerIndex);
						int despIndex = c1
								.getColumnIndex(ScheduleTable.schedule_Description);
						String desp = c1.getString(despIndex);
						int timeZone = c.getInt(c
								.getColumnIndex(ScheduleTable.timeZone));
						int alert = c.getInt(c
								.getColumnIndex(ScheduleTable.alert));
						Schedule newSchedule = new Schedule(owner_id, sche_id,
								serv_id + "", startDate, endDate, desp, alert,
								timeZone);
						schedules.add(newSchedule);
					}
					groupedSchedules.add(schedules);
					c1.close();
				} finally {
					// this gets called even if there is an exception somewhere
					// above
					if (c1 != null)
						c1.close();
				}
			}
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return groupedSchedules;
	}

	public ArrayList<Schedule> getAllSchedules() {
		ArrayList<Schedule> allschedules = new ArrayList<Schedule>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ScheduleTable.ScheduleTableName
							+ " WHERE " + ScheduleTable.is_Deleted + "=0 and "
							+ ScheduleTable.user_login + "='"
							+ new SharedReference().getCurrentOwnerId(context)
							+ "' order  by datetime("
							+ ScheduleTable.start_Time + ") ASC", null);
			String sql = "SELECT * FROM " + ScheduleTable.ScheduleTableName
					+ " WHERE " + ScheduleTable.is_Deleted + "=0 and "
					+ ScheduleTable.user_login + "='"
					+ new SharedReference().getCurrentOwnerId(context)
					+ "' order  by datetime(" + ScheduleTable.start_Time
					+ ") ASC";
			Log.d("get all schedule", sql);
			while (c != null && c.moveToNext()) {
				int startIndex = c.getColumnIndex(ScheduleTable.start_Time);
				String startDate = c.getString(startIndex);
				int endIndex = c.getColumnIndex(ScheduleTable.end_Time);
				String endDate = c.getString(endIndex);
				int scheduleIDIndex = c
						.getColumnIndex(ScheduleTable.schedule_ID);
				int sche_id = c.getInt(scheduleIDIndex);
				int serviceIndex = c.getColumnIndex(ScheduleTable.service_ID);
				int serv_id = c.getInt(serviceIndex);
				int ownerIndex = c.getColumnIndex(ScheduleTable.own_ID);
				int owner_id = c.getInt(ownerIndex);
				int despIndex = c
						.getColumnIndex(ScheduleTable.schedule_Description);
				String desp = c.getString(despIndex);
				int timeZone = c.getInt(c
						.getColumnIndex(ScheduleTable.timeZone));
				int alert = c.getInt(c.getColumnIndex(ScheduleTable.alert));
				Schedule newSchedule = new Schedule(owner_id, sche_id, serv_id
						+ "", startDate, endDate, desp, alert, timeZone);
				allschedules.add(newSchedule);
			}

			// If not added,error will occour
			// IllegalStateException: Process 5808 exceeded cursor quota 100,
			// will
			// kill it
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}

		return allschedules;
	}

	/**
	 * Get number of schedule
	 * */
	public int getNumberSchedule() {

		String query = "SELECT * FROM " + ScheduleTable.ScheduleTableName
				+ " WHERE " + ScheduleTable.is_Deleted + "=0 and "
				+ ScheduleTable.user_login + "='"
				+ new SharedReference().getCurrentOwnerId(context) + "'";
		Log.d("query schedule", query);
		Cursor mCount = null;
		try {
			mCount = this.getWritableDatabase().rawQuery(query, null);
			if (mCount != null) {
				// if(mCount.moveToFirst())
				if (mCount.moveToFirst()) {
					int count = mCount.getCount();
					// int count = mCount.getInt(0);
					return count;
				}
			}

			mCount.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (mCount != null)
				mCount.close();
		}
		return 0;
	}

	/**
	 * Get shared member of an email
	 * */
	public int getSharedMemberOfEmail(String email) {
		String query = "select " + SharedMemberTable.SharedMemberTableName
				+ ".* from " + OndutyTable.OntudyTableName + ","
				+ SharedMemberTable.SharedMemberTableName + " WHERE "
				+ OndutyTable.participant_ID + "="
				+ SharedMemberTable.member_id + " and "
				+ SharedMemberTable.member_email + "='"
				+ new SharedReference().getEmail(context) + "'";
		Log.d("sharedmember of email", query);
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(query, null);

			if (c != null && c.moveToNext()) {
				int member_id = c.getInt(c
						.getColumnIndex(SharedMemberTable.member_id));
				return member_id;
			}
			// If not added,error will occour
			// IllegalStateException: Process 5808 exceeded cursor quota 100,
			// will
			// kill it
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return -1;
	}

	/**
	 * Get all schedule create by owner
	 * */
	public ArrayList<Schedule> getMeSchedule() {
		ArrayList<Schedule> allschedules = new ArrayList<Schedule>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ScheduleTable.ScheduleTableName
							+ " WHERE " + ScheduleTable.is_Deleted + "=0 and "
							+ ScheduleTable.user_login + "='"
							+ new SharedReference().getCurrentOwnerId(context)
							+ "' and " + ScheduleTable.own_ID + "<>"
							+ new SharedReference().getCurrentOwnerId(context)
							+ " order  by datetime(" + ScheduleTable.start_Time
							+ ") ASC", null);
			Log.d("me schedule",
					"SELECT * FROM " + ScheduleTable.ScheduleTableName
							+ " WHERE " + ScheduleTable.is_Deleted + "=0 and "
							+ ScheduleTable.user_login + "='"
							+ new SharedReference().getCurrentOwnerId(context)
							+ "' and " + ScheduleTable.own_ID + "<>"
							+ new SharedReference().getCurrentOwnerId(context)
							+ " order  by datetime(" + ScheduleTable.start_Time
							+ ") ASC");
			while (c != null && c.moveToNext()) {
				int startIndex = c.getColumnIndex(ScheduleTable.start_Time);
				String startDate = c.getString(startIndex);
				int endIndex = c.getColumnIndex(ScheduleTable.end_Time);
				String endDate = c.getString(endIndex);
				int scheduleIDIndex = c
						.getColumnIndex(ScheduleTable.schedule_ID);
				int sche_id = c.getInt(scheduleIDIndex);
				int serviceIndex = c.getColumnIndex(ScheduleTable.service_ID);
				int serv_id = c.getInt(serviceIndex);
				int ownerIndex = c.getColumnIndex(ScheduleTable.own_ID);
				int owner_id = c.getInt(ownerIndex);
				int despIndex = c
						.getColumnIndex(ScheduleTable.schedule_Description);
				String desp = c.getString(despIndex);
				int timeZone = c.getInt(c
						.getColumnIndex(ScheduleTable.timeZone));
				int alert = c.getInt(c.getColumnIndex(ScheduleTable.alert));
				Schedule newSchedule = new Schedule(owner_id, sche_id, serv_id
						+ "", startDate, endDate, desp, alert, timeZone);
				allschedules.add(newSchedule);
			}
			// If not added,error will occour
			// IllegalStateException: Process 5808 exceeded cursor quota 100,
			// will
			// kill it
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return allschedules;
	}

	public List<String> getScheduleHeaders() {
		String columnName = "strftime('%Y-%m-%d'," + ScheduleTable.start_Time
				+ ")";
		List<String> dates = new ArrayList<String>();
		List<String> distinctdates = new ArrayList<String>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT " + columnName + " FROM "
							+ ScheduleTable.ScheduleTableName + " WHERE "
							+ ScheduleTable.is_Deleted + "=0" + " ORDER BY "
							+ columnName + " ASC", null);

			while (c != null && c.moveToNext()) {
				int columnIndex = c.getColumnIndex(columnName);
				String Date = c.getString(columnIndex);
				// Log.i("database", Date);
				dates.add(Date);
			}
			c.close();
			String previousDate = "2";
			for (int i = 0; i < dates.size(); i++) {
				String newDate = dates.get(i);
				if (newDate.equalsIgnoreCase(previousDate) == false) {
					String fullNewDate = newDate + " 00:00:00";
					distinctdates.add(fullNewDate);
				}
				previousDate = newDate;
			}

		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}

		return distinctdates;
	}

	public List<Schedule> getUnsyncedNewSchedules() {
		List<Schedule> schedules = new ArrayList<Schedule>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ScheduleTable.ScheduleTableName
							+ " WHERE " + ScheduleTable.is_Synchronized + "= 0"
							+ " " + "AND " + ScheduleTable.last_Modified
							+ " LIKE" + "'no%'", null);
			while (c != null && c.moveToNext()) {
				int startIndex = c.getColumnIndex(ScheduleTable.start_Time);
				String startDate = c.getString(startIndex);
				int endIndex = c.getColumnIndex(ScheduleTable.end_Time);
				String endDate = c.getString(endIndex);
				int scheduleIDIndex = c
						.getColumnIndex(ScheduleTable.schedule_ID);
				int sche_id = c.getInt(scheduleIDIndex);
				int serviceIndex = c.getColumnIndex(ScheduleTable.service_ID);
				int serv_id = c.getInt(serviceIndex);
				int ownerIndex = c.getColumnIndex(ScheduleTable.own_ID);
				int owner_id = c.getInt(ownerIndex);
				int despIndex = c
						.getColumnIndex(ScheduleTable.schedule_Description);
				String desp = c.getString(despIndex);
				int timeZone = c.getInt(c
						.getColumnIndex(ScheduleTable.timeZone));
				int alert = c.getInt(c.getColumnIndex(ScheduleTable.alert));
				Schedule newSchedule = new Schedule(owner_id, sche_id, serv_id
						+ "", startDate, endDate, desp, alert, timeZone);
				schedules.add(newSchedule);
			}
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return schedules;
	}

	public List<Schedule> getUnsyncedEditedSchedules() {
		List<Schedule> schedules = new ArrayList<Schedule>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ScheduleTable.ScheduleTableName
							+ " WHERE " + ScheduleTable.is_Synchronized + "= 0"
							+ " " + "AND " + ScheduleTable.last_Modified
							+ " LIKE" + "'2%'", null);
			while (c != null && c.moveToNext()) {
				int startIndex = c.getColumnIndex(ScheduleTable.start_Time);
				String startDate = c.getString(startIndex);
				int endIndex = c.getColumnIndex(ScheduleTable.end_Time);
				String endDate = c.getString(endIndex);
				int scheduleIDIndex = c
						.getColumnIndex(ScheduleTable.schedule_ID);
				int sche_id = c.getInt(scheduleIDIndex);
				int serviceIndex = c.getColumnIndex(ScheduleTable.service_ID);
				int serv_id = c.getInt(serviceIndex);
				int ownerIndex = c.getColumnIndex(ScheduleTable.own_ID);
				int owner_id = c.getInt(ownerIndex);
				int despIndex = c
						.getColumnIndex(ScheduleTable.schedule_Description);
				String desp = c.getString(despIndex);
				int timeZone = c.getInt(c
						.getColumnIndex(ScheduleTable.timeZone));
				int alert = c.getInt(c.getColumnIndex(ScheduleTable.alert));
				Schedule newSchedule = new Schedule(owner_id, sche_id, serv_id
						+ "", startDate, endDate, desp, alert, timeZone);
				schedules.add(newSchedule);
			}
			c.close();
		} finally {
			// this gets called even if there is an exception somewhere above
			if (c != null)
				c.close();
		}
		return schedules;
	}

	public Participant getParticipant(int member_id) {
		Cursor c = null;
		Participant newParticipant = null;
		try {
			// Cursor c = this.getWritableDatabase().rawQuery("SELECT * FROM " +
			// ParticipantTable.ParticipantTableName + " WHERE " +
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ParticipantTable.ParticipantTableName
							+ " WHERE " + ParticipantTable.participant_ID
							+ " = " + member_id, null);
			if (c != null) {
				while (c != null && c.moveToNext()) {
					int id = c.getInt(c
							.getColumnIndex(ParticipantTable.participant_ID));
					int ownid = c.getInt(c
							.getColumnIndex(ParticipantTable.own_ID));
					String name = c.getString(c
							.getColumnIndex(ParticipantTable.participant_Name));
					String email = c
							.getString(c
									.getColumnIndex(ParticipantTable.participant_Email));
					String mobile = c
							.getString(c
									.getColumnIndex(ParticipantTable.participant_Mobile));
					// Participant newParticipant = new
					// Participant(id,name,email,mobile,ownid);
					newParticipant = new Participant(id, name, email, mobile,
							ownid);
					// Add this to prevent collapse
					// c.close();
					// return newParticipant;
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return newParticipant;
	}

	public Sharedmember getSharedmember(int member_id, String activity_id) {
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + SharedMemberTable.SharedMemberTableName
							+ " WHERE " + SharedMemberTable.member_id + " = "
							+ member_id + " AND "
							+ SharedMemberTable.service_id + "=" + activity_id,
					null);
			while (c != null && c.moveToNext()) {
				int mem_id = c.getInt(c
						.getColumnIndex(SharedMemberTable.member_id));
				int serviceid = c.getInt(c
						.getColumnIndex(SharedMemberTable.service_id));
				int role = c.getInt(c.getColumnIndex(SharedMemberTable.role));
				String name = c.getString(c
						.getColumnIndex(SharedMemberTable.member_name));
				String email = c.getString(c
						.getColumnIndex(SharedMemberTable.member_email));
				String mobile = c.getString(c
						.getColumnIndex(SharedMemberTable.member_mobile));
				Sharedmember newSharedmember = new Sharedmember(mem_id, name,
						email, mobile, role, serviceid);

				return newSharedmember;
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return null;
	}

	/**
	 * Get list participants of an activity
	 * */
	public ArrayList<Sharedmember> getSharedMemberForActivity(String activity_id) {
		Cursor c = null;
		ArrayList<Sharedmember> list_shared_member = new ArrayList<Sharedmember>();
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + SharedMemberTable.SharedMemberTableName
							+ " WHERE " + SharedMemberTable.service_id + "="
							+ activity_id, null);

			while (c != null && c.moveToNext()) {
				int mem_id = c.getInt(c
						.getColumnIndex(SharedMemberTable.member_id));
				int role = c.getInt(c.getColumnIndex(SharedMemberTable.role));
				String name = c.getString(c
						.getColumnIndex(SharedMemberTable.member_name));
				String email = c.getString(c
						.getColumnIndex(SharedMemberTable.member_email));
				String mobile = c.getString(c
						.getColumnIndex(SharedMemberTable.member_mobile));
				int sid = c.getInt(c.getColumnIndex(SharedMemberTable.smid));
				Sharedmember shareMember = new Sharedmember(mem_id, name,
						email, mobile, role, sid);
				list_shared_member.add(shareMember);

			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return list_shared_member;
	}

	/**
	 * Get list participants of an activity without role participant i.e. role
	 * owner and organizer
	 * */
	public ArrayList<Participant> getParticipantsOfActivityWithoutRoleParticipant(
			String activity_id) {
		Cursor c = null;
		ArrayList<Participant> list_participant = new ArrayList<Participant>();
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + SharedMemberTable.SharedMemberTableName
							+ " WHERE " + SharedMemberTable.service_id + "="
							+ activity_id + " and " + SharedMemberTable.role
							+ " <>" + CommConstant.PARTICIPANT, null);

			while (c != null && c.moveToNext()) {
				int mem_id = c.getInt(c
						.getColumnIndex(SharedMemberTable.member_id));
				// int serviceid = c.getInt(c
				// .getColumnIndex(SharedMemberTable.service_id));
				int role = c.getInt(c.getColumnIndex(SharedMemberTable.role));
				String name = c.getString(c
						.getColumnIndex(SharedMemberTable.member_name));
				String email = c.getString(c
						.getColumnIndex(SharedMemberTable.member_email));
				String mobile = c.getString(c
						.getColumnIndex(SharedMemberTable.member_mobile));
				Participant new_participant = new Participant(mem_id, name,
						email, mobile, role);
				list_participant.add(new_participant);

			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return list_participant;
	}

	/**
	 * Get list activity of a member joined in
	 * */
	public ArrayList<String> getListActivity(String member_id) {
		Cursor c = null;
		ArrayList<String> list_activity_id = new ArrayList<String>();
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ActivityTable.ActivityTableName + ", "
							+ SharedMemberTable.SharedMemberTableName
							+ " where " + ActivityTable.is_Deleted + "=0 and "
							+ ActivityTable.service_ID + "="
							+ SharedMemberTable.service_id + " and "
							+ SharedMemberTable.member_id + "=" + member_id
							+ " and ", null);

			while (c != null && c.moveToNext()) {
				String id = c.getString(c
						.getColumnIndex(ActivityTable.service_ID));
				list_activity_id.add(id);

			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return list_activity_id;
	}

	/**
	 * Get number activity involve
	 * */
	public int getNumberActivity(String member_email) {
		int number = 0;
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ActivityTable.ActivityTableName + ", "
							+ SharedMemberTable.SharedMemberTableName
							+ " where " + ActivityTable.is_Deleted + "=0 and "
							+ ActivityTable.service_ID + "="
							+ SharedMemberTable.service_id + " and "
							+ SharedMemberTable.member_email + "='"
							+ member_email + "'", null);

			if (c != null && c.moveToNext()) {
				number = c.getCount();
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return number;
	}

	/**
	 * Get participant of an activity
	 * */
	public ArrayList<Sharedmember> getParticipantsOfActivity(String activity_id) {
		Cursor c = null;
		ArrayList<Sharedmember> list_member = new ArrayList<Sharedmember>();
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + SharedMemberTable.SharedMemberTableName
							+ " WHERE " + SharedMemberTable.service_id + "="
							+ activity_id + " and "
							+ SharedMemberTable.is_Deleted + "=0", null);

			while (c != null && c.moveToNext()) {
				int mem_id = c.getInt(c
						.getColumnIndex(SharedMemberTable.member_id));
				int role = c.getInt(c.getColumnIndex(SharedMemberTable.role));
				String name = c.getString(c
						.getColumnIndex(SharedMemberTable.member_name));
				String email = c.getString(c
						.getColumnIndex(SharedMemberTable.member_email));
				String mobile = c.getString(c
						.getColumnIndex(SharedMemberTable.member_mobile));
				int sid = c.getInt(c.getColumnIndex(SharedMemberTable.smid));
				Sharedmember new_participant = new Sharedmember(mem_id, name,
						email, mobile, role, sid);
				list_member.add(new_participant);

			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return list_member;
	}

	public ArrayList<Participant> getParticipants() {
		SharedReference ref = new SharedReference();
		ArrayList<Participant> participants = new ArrayList<Participant>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ParticipantTable.ParticipantTableName
							+ " WHERE " + ParticipantTable.is_Deleted + "=0"
							+ " AND " + ParticipantTable.own_ID + "="
							+ ref.getCurrentOwnerId(context) + " order by "
							+ ParticipantTable.participant_Name
							+ " COLLATE NOCASE ASC", null);
			while (c != null && c.moveToNext()) {
				int id = c.getInt(c
						.getColumnIndex(ParticipantTable.participant_ID));
				int ownid = c.getInt(c.getColumnIndex(ParticipantTable.own_ID));
				String name = c.getString(c
						.getColumnIndex(ParticipantTable.participant_Name));
				String email = c.getString(c
						.getColumnIndex(ParticipantTable.participant_Email));
				String mobile = c.getString(c
						.getColumnIndex(ParticipantTable.participant_Mobile));
				Participant newParticipant = new Participant(id, name, email,
						mobile, ownid);

				// don't show login user in contact page
				if (!email.equals(new SharedReference().getEmail(context))) {
					participants.add(newParticipant);
				}
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return participants;
	}

	public List<Participant> getUnsyncedNewParticipants() {
		List<Participant> participants = new ArrayList<Participant>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ParticipantTable.ParticipantTableName
							+ " WHERE " + ScheduleTable.is_Synchronized + "= 0"
							+ " " + "AND " + ParticipantTable.last_Modified
							+ " LIKE " + "'no%'", null);
			while (c != null && c.moveToNext()) {
				int id = c.getInt(c
						.getColumnIndex(ParticipantTable.participant_ID));
				int ownid = c.getInt(c.getColumnIndex(ParticipantTable.own_ID));
				String name = c.getString(c
						.getColumnIndex(ParticipantTable.participant_Name));
				String email = c.getString(c
						.getColumnIndex(ParticipantTable.participant_Email));
				String mobile = c.getString(c
						.getColumnIndex(ParticipantTable.participant_Mobile));
				Participant newParticipant = new Participant(id, name, email,
						mobile, ownid);
				participants.add(newParticipant);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return participants;
	}

	public List<Participant> getUnsyncedEditedParticipants() {
		List<Participant> participants = new ArrayList<Participant>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ParticipantTable.ParticipantTableName
							+ " WHERE " + ScheduleTable.is_Synchronized + "= 0"
							+ " AND " + ScheduleTable.is_Deleted + "= 0" + " "
							+ "AND " + ParticipantTable.last_Modified
							+ " LIKE " + "'2%'", null);
			while (c != null && c.moveToNext()) {
				int id = c.getInt(c
						.getColumnIndex(ParticipantTable.participant_ID));
				int ownid = c.getInt(c.getColumnIndex(ParticipantTable.own_ID));
				String name = c.getString(c
						.getColumnIndex(ParticipantTable.participant_Name));
				String email = c.getString(c
						.getColumnIndex(ParticipantTable.participant_Email));
				String mobile = c.getString(c
						.getColumnIndex(ParticipantTable.participant_Mobile));
				Participant newParticipant = new Participant(id, name, email,
						mobile, ownid);
				participants.add(newParticipant);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return participants;
	}

	public List<Confirm> getParticipantsForSchedule(int schedule_id) {
		List<Confirm> memberids = new ArrayList<Confirm>();

		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT " + OndutyTable.participant_ID + ","
							+ OndutyTable.confirm + " FROM "
							+ OndutyTable.OntudyTableName + " WHERE "
							+ OndutyTable.schedule_ID + " = " + schedule_id,
					null);
			while (c != null && c.moveToNext()) {
				int memberid_Index = c
						.getColumnIndex(OndutyTable.participant_ID);
				int memberid = c.getInt(memberid_Index);
				int confirm = c.getInt(c.getColumnIndex(OndutyTable.confirm));
				Confirm con = new Confirm(memberid, confirm);
				Log.d("member_id onduty", memberid + "");
				memberids.add(con);

			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return memberids;
	}

	/**
	 * get timezone
	 * */
	public TimeZoneModel getTimeZone(int timeZoneId) {
		TimeZoneModel timeZoneModel = null;
		Cursor c = null;
		try {
			c = this.getWritableDatabase()
					.rawQuery(
							"SELECT *  FROM " + TimeZoneTable.TimeZoneTableName
									+ " WHERE " + TimeZoneTable.id + " = "
									+ timeZoneId, null);
			if (c != null && c.moveToNext()) {
				int id = c.getInt(c.getColumnIndex(TimeZoneTable.id));
				String tzname = c.getString(c
						.getColumnIndex(TimeZoneTable.tzname));
				String displayname = c.getString(c
						.getColumnIndex(TimeZoneTable.displayname));
				String displayorder = c.getString(c
						.getColumnIndex(TimeZoneTable.displayorder));
				String abbrtzname = c.getString(c
						.getColumnIndex(TimeZoneTable.abbrtzname));
				timeZoneModel = new TimeZoneModel(id, tzname, displayname,
						displayorder, abbrtzname);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return timeZoneModel;
	}

	/**
	 * get repeat
	 * */
	public Alert getAlert(int alerId) {
		Alert alert = null;
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT *  FROM " + AlertTable.alertTableName + " WHERE "
							+ AlertTable.id + " = " + alerId, null);
			if (c != null && c.moveToNext()) {
				int id = c.getInt(c.getColumnIndex(AlertTable.id));
				String aname = c.getString(c.getColumnIndex(AlertTable.aname));
				alert = new Alert(id, aname);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return alert;
	}

	public List<Integer> getOndutyRecordsForSchedule(int id) {
		List<Integer> ondutyids = new ArrayList<Integer>();
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT " + OndutyTable.onduty_ID + " FROM "
							+ OndutyTable.OntudyTableName + " WHERE "
							+ OndutyTable.schedule_ID + " = " + id, null);
			if (c != null && c.moveToNext()) {
				int ondutyid_Index = c.getColumnIndex(OndutyTable.onduty_ID);
				int ondutyid = c.getInt(ondutyid_Index);
				ondutyids.add(ondutyid);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();

		}
		return ondutyids;
	}

	public boolean isTimeZoneExisted(int timeZoneId) {
		Cursor c = null;
		int id = -1;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT " + TimeZoneTable.id + " from "
							+ TimeZoneTable.TimeZoneTableName + " where "
							+ TimeZoneTable.id + " = " + timeZoneId, null);

			if (c != null && c.moveToNext()) {
				id = c.getInt(c.getColumnIndex(TimeZoneTable.id));
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return (id == (-1)) ? false : true;
	}

	public boolean isAlertExisted(int alertId) {
		Cursor c = null;
		int id = -1;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT " + AlertTable.id + " from "
							+ AlertTable.alertTableName + " where "
							+ AlertTable.id + " = " + alertId, null);

			if (c != null && c.moveToNext()) {
				id = c.getInt(c.getColumnIndex(AlertTable.id));
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return (id == (-1)) ? false : true;
	}

	public boolean isVersionExisted(int versionId) {
		int id = -1;
		Cursor c = null;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT " + AppVersionTable.id + " from "
							+ AppVersionTable.appVersionTable + " where "
							+ AppVersionTable.id + " = " + versionId, null);

			if (c != null && c.moveToNext()) {
				id = c.getInt(c.getColumnIndex(AppVersionTable.id));
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return (id == (-1)) ? false : true;
	}

	public boolean isScheduleExisted(int scheduleID) {
		Cursor c = null;
		int id = -1;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT " + ScheduleTable.schedule_ID + " from "
							+ ScheduleTable.ScheduleTableName + " where "
							+ ScheduleTable.schedule_ID + " = " + scheduleID,
					null);
			if (c != null && c.moveToNext()) {
				id = c.getInt(c.getColumnIndex(ScheduleTable.schedule_ID));
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return (id == (-1)) ? false : true;
	}

	public boolean isOndutyExisted(int scheduleID) {
		Cursor c = null;
		int id = -1;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT " + OndutyTable.schedule_ID + " from "
							+ OndutyTable.OntudyTableName + " where "
							+ OndutyTable.schedule_ID + " = " + scheduleID,
					null);

			if (c != null && c.moveToNext()) {
				id = c.getInt(c.getColumnIndex(OndutyTable.schedule_ID));
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return (id == (-1)) ? false : true;
	}

	public boolean isSharedmemberExisted(int memberid, String activityid) {
		Cursor c = null;
		int id = -1;
		try {
			c = this.getWritableDatabase().rawQuery(
					"SELECT " + SharedMemberTable.member_id + " from "
							+ SharedMemberTable.SharedMemberTableName
							+ " where " + SharedMemberTable.member_id + " = "
							+ memberid + " AND " + SharedMemberTable.service_id
							+ "=" + activityid, null);

			if (c != null && c.moveToNext()) {
				// Wrong get method
				// Couldn't read row 0, col -1 from CursorWindow. Make sure the
				// Cursor is initialized correctly before accessing data from
				// it.
				// id = c.getInt(c.getColumnIndex(OndutyTable.schedule_ID));
				id = c.getInt(c.getColumnIndex(SharedMemberTable.member_id));
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return (id == (-1)) ? false : true;
	}

	public boolean insertAppVersion(ContentValues contentValue) {
		long result = this.getWritableDatabase().insertWithOnConflict(
				AppVersionTable.appVersionTable, AppVersionTable.id,
				contentValue, SQLiteDatabase.CONFLICT_REPLACE);
		return (result == -1) ? false : true;
	}

	public boolean updateAppVersion(int id, ContentValues contentValue) {
		String[] whereArgs = new String[] { String.valueOf(id) };
		int result = this.getWritableDatabase().update(
				AppVersionTable.appVersionTable, contentValue,
				AppVersionTable.id + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	public boolean insertAlert(ContentValues alert) {
		long result = this.getWritableDatabase().insertWithOnConflict(
				AlertTable.alertTableName, AlertTable.id, alert,
				SQLiteDatabase.CONFLICT_REPLACE);
		return (result == -1) ? false : true;
	}

	public boolean updateAlert(int id, ContentValues alert) {
		String[] whereArgs = new String[] { String.valueOf(id) };
		int result = this.getWritableDatabase().update(
				AlertTable.alertTableName, alert, AlertTable.id + "=?",
				whereArgs);
		return (result == 1) ? true : false;
	}

	public boolean insertTimeZone(ContentValues timeZone) {
		long result = this.getWritableDatabase().insertWithOnConflict(
				TimeZoneTable.TimeZoneTableName, TimeZoneTable.id, timeZone,
				SQLiteDatabase.CONFLICT_REPLACE);
		return (result == -1) ? false : true;
	}

	public boolean updateTimeZone(int id, ContentValues timeZone) {
		String[] whereArgs = new String[] { String.valueOf(id) };
		int result = this.getWritableDatabase().update(
				TimeZoneTable.TimeZoneTableName, timeZone,
				TimeZoneTable.id + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	public boolean insertParticipant(ContentValues newParticipant) {
		long result = this.getWritableDatabase().insertWithOnConflict(
				ParticipantTable.ParticipantTableName,
				ParticipantTable.participant_ID, newParticipant,
				SQLiteDatabase.CONFLICT_REPLACE);
		return (result == -1) ? false : true;
	}

	public boolean updateParticipant(int id, ContentValues newParticipant) {
		String[] whereArgs = new String[] { String.valueOf(id) };
		int result = this.getWritableDatabase().update(
				ParticipantTable.ParticipantTableName, newParticipant,
				ParticipantTable.participant_ID + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	public boolean deleteParticipant(int id) {
		String[] whereArgs = new String[] { String.valueOf(id) };
		int result = this.getWritableDatabase().delete(
				ParticipantTable.ParticipantTableName,
				ParticipantTable.participant_ID + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	public boolean insertActivity(ContentValues newActivity) {

		long result = this.getWritableDatabase().insertWithOnConflict(
				ActivityTable.ActivityTableName, ActivityTable.service_ID,
				newActivity, SQLiteDatabase.CONFLICT_REPLACE);
		return (result == -1) ? false : true;
	}

	public boolean updateActivity(String id, ContentValues newActivity) {
		String[] whereArgs = new String[] { String.valueOf(id) };
		int result = this.getWritableDatabase().update(
				ActivityTable.ActivityTableName, newActivity,
				ActivityTable.service_ID + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	public boolean deleteActivity(String id) {
		String[] whereArgs = new String[] { String.valueOf(id) };
		int result = this.getWritableDatabase().delete(
				ActivityTable.ActivityTableName,
				ActivityTable.service_ID + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	public boolean insertSchedule(ContentValues newSchedule) {
		long result = this.getWritableDatabase().insertWithOnConflict(
				ScheduleTable.ScheduleTableName, ScheduleTable.schedule_ID,
				newSchedule, SQLiteDatabase.CONFLICT_REPLACE);
		return (result == -1) ? false : true;
	}

	public boolean updateSchedule(int id, ContentValues newSchedule) {
		String[] whereArgs = new String[] { String.valueOf(id) };
		int result = this.getWritableDatabase().update(
				ScheduleTable.ScheduleTableName, newSchedule,
				ScheduleTable.schedule_ID + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	public boolean deleteSchedule(int id) {
		String[] whereArgs = new String[] { String.valueOf(id) };
		int result = this.getWritableDatabase().delete(
				ScheduleTable.ScheduleTableName,
				ScheduleTable.schedule_ID + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	public boolean insertOnduty(ContentValues newOnduty) {
		long result = this.getWritableDatabase().insertWithOnConflict(
				OndutyTable.OntudyTableName, OndutyTable.service_ID, newOnduty,
				SQLiteDatabase.CONFLICT_REPLACE);
		return (result == -1) ? false : true;
	}

	public boolean updateOnduty(int id, ContentValues newOnduty) {
		String[] whereArgs = new String[] { String.valueOf(id) };
		int result = this.getWritableDatabase().update(
				OndutyTable.OntudyTableName, newOnduty,
				OndutyTable.schedule_ID + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	public boolean updateOnduty(int id, int member_id, ContentValues newOnduty) {
		String[] whereArgs = new String[] { String.valueOf(id),
				String.valueOf(member_id) };
		int result = this.getWritableDatabase().update(
				OndutyTable.OntudyTableName,
				newOnduty,
				OndutyTable.schedule_ID + "=? AND "
						+ OndutyTable.participant_ID + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	public boolean updateConfirmStatus(int id, ContentValues newOnduty) {
		String[] whereArgs = new String[] { String.valueOf(id) };
		int result = this.getWritableDatabase().update(
				OndutyTable.OntudyTableName, newOnduty,
				OndutyTable.schedule_ID + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	public boolean deleteRelatedOnduty(int scheduleID) {
		String[] whereArgs = new String[] { String.valueOf(scheduleID) };
		int result = this.getWritableDatabase().delete(
				OndutyTable.OntudyTableName, OndutyTable.schedule_ID + "=?",
				whereArgs);
		return (result > 0);
	}

	public boolean insertSharedmember(ContentValues sharedmember) {
		long result = this.getWritableDatabase().insertWithOnConflict(
				SharedMemberTable.SharedMemberTableName,
				SharedMemberTable.member_id, sharedmember,
				SQLiteDatabase.CONFLICT_REPLACE);
		return (result == -1) ? false : true;
	}

	public boolean deleteSharedmember(int memberid, String activityid) {
		String[] whereArgs = new String[] { String.valueOf(memberid),
				String.valueOf(activityid) };
		int result = this.getWritableDatabase().delete(
				SharedMemberTable.SharedMemberTableName,
				SharedMemberTable.member_id + "=?" + " AND "
						+ SharedMemberTable.service_id + "=?", whereArgs);
		return (result > 0);
	}

	public boolean updateSharedmember(int memberid, String activityid,
			ContentValues sharedmember) {
		String[] whereArgs = new String[] { String.valueOf(memberid),
				String.valueOf(activityid) };
		int result = this.getWritableDatabase().update(
				SharedMemberTable.SharedMemberTableName,
				sharedmember,
				SharedMemberTable.member_id + "=?" + " AND "
						+ SharedMemberTable.service_id + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	/**
	 * Update member infor into sharedmember when owner change member
	 * information in page contact
	 * */
	public boolean updateSharedmember(int memberid, ContentValues sharedmember) {
		String[] whereArgs = new String[] { String.valueOf(memberid) };
		int result = this.getWritableDatabase().update(
				SharedMemberTable.SharedMemberTableName, sharedmember,
				SharedMemberTable.member_id + "=?", whereArgs);
		return (result == 1) ? true : false;
	}

	/**
	 * Get number of schedule user participate in
	 * */
	public int numbersOfSchedulesUserParticipantIn() {
		int scheduleCounter = 0;
		SharedReference ref = new SharedReference();
		String userEmail = ref.getEmail(context);
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT " + OndutyTable.participant_ID + " FROM "
						+ OndutyTable.OntudyTableName, null);
		while (c != null && c.moveToNext()) {
			int index = c.getColumnIndex(OndutyTable.participant_ID);
			int memberid = c.getInt(index);
			Participant p = this.getParticipant(memberid);
			if ((p != null) && (p.getEmail().equalsIgnoreCase(userEmail)))
				scheduleCounter++;
		}
		c.close();
		return scheduleCounter;
	}

	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	// Check whether the mobile number is valid
	public static boolean isMobileValid(String mobile) {
		boolean isValid = false;

		String regex = "^\\+?[0-9. ()-]{10,25}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(mobile);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	public void deleteTablesExitApp() {
		this.getWritableDatabase().delete(
				ParticipantTable.ParticipantTableName, null, null);
		this.getWritableDatabase().delete(
				SharedMemberTable.SharedMemberTableName, null, null);
		this.getWritableDatabase().delete(OndutyTable.OntudyTableName, null,
				null);
		this.getWritableDatabase().delete(ScheduleTable.ScheduleTableName,
				null, null);
		SharedReference ref = new SharedReference();
		ref.setLastestParticipantLastModifiedTime(context,
				CommConstant.DEFAULT_DATE);
		ref.setLastestScheduleLastModifiedTime(context,
				CommConstant.DEFAULT_DATE);
		ref.setLastestServiceLastModifiedTime(context,
				CommConstant.DEFAULT_DATE);
	}

	public void evacuateDatabase() {
		this.getWritableDatabase().delete(ActivityTable.ActivityTableName,
				null, null);
		this.getWritableDatabase().delete(
				ParticipantTable.ParticipantTableName, null, null);
		this.getWritableDatabase().delete(
				SharedMemberTable.SharedMemberTableName, null, null);
		this.getWritableDatabase().delete(OndutyTable.OntudyTableName, null,
				null);
		this.getWritableDatabase().delete(ScheduleTable.ScheduleTableName,
				null, null);
		// this.getWritableDatabase().delete(TimeZoneTable.TimeZoneTableName,
		// null, null);
		// this.getWritableDatabase()
		// .delete(AlertTable.alertTableName, null, null);
		// this.getWritableDatabase().delete(AppVersionTable.appVersionTable,
		// null, null);
		SharedPreferences settings = context.getSharedPreferences(
				SharedReference.MY_PREFERENCE, Context.MODE_PRIVATE);
		settings.edit().clear().commit();
	}

	public int getNextActivityID() {
		SharedPreferences sp = context.getSharedPreferences("MyPreferences", 0);
		int nextid = sp.getInt("nextserviceid", -1);
		Editor editor = sp.edit();
		editor.putInt("nextserviceid", nextid + 1);
		editor.commit();
		return nextid;
	}

	public int getNextParticipantID() {
		SharedPreferences sp = context.getSharedPreferences("MyPreferences", 0);
		int nextid = sp.getInt(CommConstant.NEXT_MEMBER_ID, -1);
		Editor editor = sp.edit();
		editor.putInt(CommConstant.NEXT_MEMBER_ID, nextid + 1);
		editor.commit();
		return nextid;
	}

	public int getNextScheduleID() {
		SharedPreferences sp = context.getSharedPreferences("MyPreferences", 0);
		int nextid = sp.getInt("nextscheduleid", -1);
		Editor editor = sp.edit();
		editor.putInt("nextscheduleid", nextid + 1);
		editor.commit();
		return nextid;
	}
}
