package com.dling61.calendarschedule.db;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dling61.calendarschedule.models.ActivityTable;
import com.dling61.calendarschedule.models.MyActivity;
import com.dling61.calendarschedule.models.OndutyTable;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.models.ParticipantTable;
import com.dling61.calendarschedule.models.Schedule;
import com.dling61.calendarschedule.models.ScheduleTable;
import com.dling61.calendarschedule.models.SharedMemberTable;
import com.dling61.calendarschedule.models.Sharedmember;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.SharedReference;

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
	public static final int DB_VERSION = 2;
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
				+ ActivityTable.start_time + " TEXT NOT NULL,"
				+ ActivityTable.end_time + " TEXT NOT NULL,"
				+ ActivityTable.otc_Offset + " INTEGER NOT NULL,"
				+ ActivityTable.alert + " INTEGER NOT NULL,"
				+ ActivityTable.repeat + " INTEGER NOT NULL,"
				+ ActivityTable.sharedrole + " INTEGER NOT NULL,"
				+ ActivityTable.last_ModifiedTime + " TEXT,"
				+ ActivityTable.is_Deleted + " INTEGER NOT NULL,"
				+ ParticipantTable.user_login + " text not null,"
				+ ActivityTable.is_Synchronized + " INTEGER NOT NULL);");

		db.execSQL("CREATE TABLE " + ScheduleTable.ScheduleTableName + "("
				+ ScheduleTable.schedule_ID + " INTEGER PRIMARY KEY NOT NULL,"
				+ ScheduleTable.schedule_Description + " TEXT,"
				+ ScheduleTable.service_ID + " INTEGER NOT NULL,"
				+ ScheduleTable.start_Time + " TEXT NOT NULL,"
				+ ScheduleTable.end_Time + " TEXT NOT NULL,"
				+ ScheduleTable.own_ID + " INTEGER NOT NULL,"
				+ ScheduleTable.last_Modified + " TEXT,"
				+ ScheduleTable.is_Deleted + " INTEGER NOT NULL,"
				+ ScheduleTable.user_login + " text not null,"
				+ ScheduleTable.is_Synchronized + " INTEGER NOT NULL);");

		db.execSQL("CREATE TABLE " + OndutyTable.OntudyTableName + "("
				+ OndutyTable.onduty_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+ OndutyTable.service_ID + " INTEGER NOT NULL,"
				+ OndutyTable.schedule_ID + " INTEGER NOT NULL,"
				+ OndutyTable.participant_ID + " INTEGER NOT NULL,"
				+ OndutyTable.is_Deleted + " INTEGER NOT NULL,"
				+ OndutyTable.is_Synchronized + " INTEGER NOT NULL,"
				
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
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void resetActivity() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(ActivityTable.ActivityTableName, null, null);
		db.close();
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
		onCreate(db);
		
	}

	/**
	 * get number of activity
	 * */
	public int getNumberActivity() {
		Cursor mCount = this.getWritableDatabase().rawQuery(
				"select count(*) from " + ActivityTable.ActivityTableName
						+ " where " + ActivityTable.is_Deleted + "=0 and "
						+ ActivityTable.own_ID + "="
						+ new SharedReference().getCurrentOwnerId(context),
				null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	public ArrayList<MyActivity> getActivities() {
		ArrayList<MyActivity> activities = new ArrayList<MyActivity>();
		// Cursor c = this.getWritableDatabase().rawQuery("SELECT * FROM " +
		// ActivityTable.ActivityTableName + " WHERE "
		// + ActivityTable.is_Deleted + "=0", null);
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ActivityTable.ActivityTableName + " where "
						+ ActivityTable.is_Deleted + "=0 and "
						+ ActivityTable.user_login + "='"
						+ new SharedReference().getCurrentOwnerId(context)
						+ "'", null);
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex(ActivityTable.service_ID));
			int ownid = c.getInt(c.getColumnIndex(ActivityTable.own_ID));
			int alert = c.getInt(c.getColumnIndex(ActivityTable.alert));
			int repeat = c.getInt(c.getColumnIndex(ActivityTable.repeat));
			String name = c.getString(c
					.getColumnIndex(ActivityTable.service_Name));
			String start = c.getString(c
					.getColumnIndex(ActivityTable.start_time));
			String end = c.getString(c.getColumnIndex(ActivityTable.end_time));
			String desp = c.getString(c
					.getColumnIndex(ActivityTable.service_description));
			int otc = c.getInt(c.getColumnIndex(ActivityTable.otc_Offset));
			int role = c.getInt(c.getColumnIndex(ActivityTable.sharedrole));
			MyActivity newActivity = new MyActivity(id, ownid, alert, repeat,
					name, start, end, desp, otc, role);
			activities.add(newActivity);
		}
		c.close();
		return activities;
	}
	
	/**
	 * Get all activity which haven role: owner or organizer
	 * */
	public ArrayList<MyActivity> getActivitiesOwnerOrOrganizer(String user_id) {
		ArrayList<MyActivity> activities = new ArrayList<MyActivity>();
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ActivityTable.ActivityTableName + " where "
						+ ActivityTable.is_Deleted + "=0 and "
						+ ActivityTable.user_login + "='"
						+ new SharedReference().getCurrentOwnerId(context)+"'"
						+" and ("+ActivityTable.sharedrole+" = "+CommConstant.OWNER
						+" or "+ActivityTable.sharedrole+" = "+CommConstant.ORGANIZER+")", null);
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex(ActivityTable.service_ID));
			int ownid = c.getInt(c.getColumnIndex(ActivityTable.own_ID));
			int alert = c.getInt(c.getColumnIndex(ActivityTable.alert));
			int repeat = c.getInt(c.getColumnIndex(ActivityTable.repeat));
			String name = c.getString(c
					.getColumnIndex(ActivityTable.service_Name));
			String start = c.getString(c
					.getColumnIndex(ActivityTable.start_time));
			String end = c.getString(c.getColumnIndex(ActivityTable.end_time));
			String desp = c.getString(c
					.getColumnIndex(ActivityTable.service_description));
			int otc = c.getInt(c.getColumnIndex(ActivityTable.otc_Offset));
			int role = c.getInt(c.getColumnIndex(ActivityTable.sharedrole));
			MyActivity newActivity = new MyActivity(id, ownid, alert, repeat,
					name, start, end, desp, otc, role);
			activities.add(newActivity);
		}
		c.close();
		return activities;
	}

	public List<MyActivity> getUnsyncedNewActivities() {
		List<MyActivity> activities = new ArrayList<MyActivity>();
		// SharedPreferences sp = context.getSharedPreferences("MyPreferences",
		// 0);
		// int ownid = sp.getInt("currentownerid", 0);
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ActivityTable.ActivityTableName + " WHERE "
						+ ActivityTable.is_Synchronized + "= 0" + " " + "AND "
						// + ActivityTable.own_ID + "= "+ ownid + " " + "AND "
						+ ActivityTable.last_ModifiedTime + " LIKE" + "'no%'",
				null);
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex(ActivityTable.service_ID));
			// int ownid = c.getInt(c.getColumnIndex(ActivityTable.own_ID));
			int ownid = c.getInt(c.getColumnIndex(ActivityTable.own_ID));
			Log.i("getUnsyncedNewActivities ownid ", ownid + "");
			int alert = c.getInt(c.getColumnIndex(ActivityTable.alert));
			int repeat = c.getInt(c.getColumnIndex(ActivityTable.repeat));
			String name = c.getString(c
					.getColumnIndex(ActivityTable.service_Name));
			String start = c.getString(c
					.getColumnIndex(ActivityTable.start_time));
			String end = c.getString(c.getColumnIndex(ActivityTable.end_time));
			String desp = c.getString(c
					.getColumnIndex(ActivityTable.service_description));
			int otc = c.getInt(c.getColumnIndex(ActivityTable.otc_Offset));
			int role = c.getInt(c.getColumnIndex(ActivityTable.sharedrole));
			MyActivity newActivity = new MyActivity(id, ownid, alert, repeat,
					name, start, end, desp, otc, role);
			activities.add(newActivity);
		}
		return activities;
	}

	public List<MyActivity> getUnsyncedEditedActivities() {
		List<MyActivity> activities = new ArrayList<MyActivity>();
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ActivityTable.ActivityTableName + " WHERE "
						+ ActivityTable.is_Synchronized + "= 0" + " " + "AND "
						+ ActivityTable.last_ModifiedTime + " LIKE" + "'2%'",
				null);
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex(ActivityTable.service_ID));
			int ownid = c.getInt(c.getColumnIndex(ActivityTable.own_ID));
			int alert = c.getInt(c.getColumnIndex(ActivityTable.alert));
			int repeat = c.getInt(c.getColumnIndex(ActivityTable.repeat));
			String name = c.getString(c
					.getColumnIndex(ActivityTable.service_Name));
			String start = c.getString(c
					.getColumnIndex(ActivityTable.start_time));
			String end = c.getString(c.getColumnIndex(ActivityTable.end_time));
			String desp = c.getString(c
					.getColumnIndex(ActivityTable.service_description));
			int otc = c.getInt(c.getColumnIndex(ActivityTable.otc_Offset));
			int role = c.getInt(c.getColumnIndex(ActivityTable.sharedrole));
			MyActivity newActivity = new MyActivity(id, ownid, alert, repeat,
					name, start, end, desp, otc, role);
			activities.add(newActivity);
		}
		return activities;
	}

	public MyActivity getActivity(String service_id) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ActivityTable.ActivityTableName + " WHERE "
						+ ActivityTable.service_ID + " = " + service_id, null);
		if (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex(ActivityTable.service_ID));
			int ownid = c.getInt(c.getColumnIndex(ActivityTable.own_ID));
			int alert = c.getInt(c.getColumnIndex(ActivityTable.alert));
			int repeat = c.getInt(c.getColumnIndex(ActivityTable.repeat));
			String name = c.getString(c
					.getColumnIndex(ActivityTable.service_Name));
			String start = c.getString(c
					.getColumnIndex(ActivityTable.start_time));
			String end = c.getString(c.getColumnIndex(ActivityTable.end_time));
			String desp = c.getString(c
					.getColumnIndex(ActivityTable.service_description));
			int otc = c.getInt(c.getColumnIndex(ActivityTable.otc_Offset));
			int role = c.getInt(c.getColumnIndex(ActivityTable.sharedrole));
			MyActivity newActivity = new MyActivity(id, ownid, alert, repeat,
					name, start, end, desp, otc, role);
			return newActivity;
		}
		return null;
	}

	public Schedule getScheduleSortedByID(int id) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ScheduleTable.ScheduleTableName + " WHERE "
						+ ScheduleTable.schedule_ID + " = " + id, null);
		while (c.moveToNext()) {
			int sche_id = c.getInt(c.getColumnIndex(ScheduleTable.schedule_ID));
			int owner_id = c.getInt(c.getColumnIndex(ScheduleTable.own_ID));
			int serv_id = c.getInt(c.getColumnIndex(ScheduleTable.service_ID));
			String startDate = c.getString(c
					.getColumnIndex(ScheduleTable.start_Time));
			String endDate = c.getString(c
					.getColumnIndex(ScheduleTable.end_Time));
			String desp = c.getString(c
					.getColumnIndex(ScheduleTable.schedule_Description));
			Schedule newSchedule = new Schedule(owner_id, sche_id,
					serv_id + "", startDate, endDate, desp);
			return newSchedule;
		}
		return null;
	}

	public List<Schedule> getSchedulesBelongtoActivity(String id) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ScheduleTable.ScheduleTableName + " WHERE "
						+ ScheduleTable.service_ID + " = " + id + " AND "
						+ ScheduleTable.is_Deleted + "=0", null);
		List<Schedule> schedules = new ArrayList<Schedule>();
		while (c.moveToNext()) {
			int sche_id = c.getInt(c.getColumnIndex(ScheduleTable.schedule_ID));
			int owner_id = c.getInt(c.getColumnIndex(ScheduleTable.own_ID));
			int serv_id = c.getInt(c.getColumnIndex(ScheduleTable.service_ID));
			String startDate = c.getString(c
					.getColumnIndex(ScheduleTable.start_Time));
			String endDate = c.getString(c
					.getColumnIndex(ScheduleTable.end_Time));
			String desp = c.getString(c
					.getColumnIndex(ScheduleTable.schedule_Description));
			Schedule newSchedule = new Schedule(owner_id, sche_id,
					serv_id + "", startDate, endDate, desp);
			schedules.add(newSchedule);
		}
		return schedules;
	}

	public List<List<Schedule>> getScheduleSortedByDate() {
		String columnName = "strftime('%Y-%m-%d'," + ScheduleTable.start_Time
				+ ")";
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT " + columnName + " FROM "
						+ ScheduleTable.ScheduleTableName + " WHERE "
						+ ScheduleTable.is_Deleted + "=0" + " ORDER BY "
						+ columnName + " ASC", null);
		List<String> dates = new ArrayList<String>();
		List<String> distinctdates = new ArrayList<String>();
		while (c.moveToNext()) {
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

		List<List<Schedule>> groupedSchedules = new ArrayList<List<Schedule>>();
		for (int j = 0; j < distinctdates.size(); j++) {
			List<Schedule> schedules = new ArrayList<Schedule>();
			String date = distinctdates.get(j);
			Cursor c1 = this.getWritableDatabase().rawQuery(
					"SELECT * FROM " + ScheduleTable.ScheduleTableName
							+ " WHERE " + ScheduleTable.start_Time + " LIKE "
							+ "'" + date + "%'", null);
			while (c1.moveToNext()) {
				int startIndex = c1.getColumnIndex(ScheduleTable.start_Time);
				String startDate = c1.getString(startIndex);
				int endIndex = c1.getColumnIndex(ScheduleTable.end_Time);
				String endDate = c1.getString(endIndex);
				int scheduleIDIndex = c1
						.getColumnIndex(ScheduleTable.schedule_ID);
				int sche_id = c1.getInt(scheduleIDIndex);
				int serviceIndex = c1.getColumnIndex(ScheduleTable.service_ID);
				int serv_id = c1.getInt(serviceIndex);
				int ownerIndex = c1.getColumnIndex(ScheduleTable.own_ID);
				int owner_id = c1.getInt(ownerIndex);
				int despIndex = c1
						.getColumnIndex(ScheduleTable.schedule_Description);
				String desp = c1.getString(despIndex);

				Schedule newSchedule = new Schedule(owner_id, sche_id, serv_id
						+ "", startDate, endDate, desp);
				schedules.add(newSchedule);
			}
			groupedSchedules.add(schedules);
		}

		return groupedSchedules;
	}

	public ArrayList<Schedule> getAllSchedules() {
		ArrayList<Schedule> allschedules = new ArrayList<Schedule>();
		Cursor c1 = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ScheduleTable.ScheduleTableName + " WHERE "
						+ ScheduleTable.is_Deleted + "=0 and "
						+ ScheduleTable.user_login + "='"
						+ new SharedReference().getCurrentOwnerId(context)
						+ "'", null);
		while (c1.moveToNext()) {
			int startIndex = c1.getColumnIndex(ScheduleTable.start_Time);
			String startDate = c1.getString(startIndex);
			int endIndex = c1.getColumnIndex(ScheduleTable.end_Time);
			String endDate = c1.getString(endIndex);
			int scheduleIDIndex = c1.getColumnIndex(ScheduleTable.schedule_ID);
			int sche_id = c1.getInt(scheduleIDIndex);
			int serviceIndex = c1.getColumnIndex(ScheduleTable.service_ID);
			int serv_id = c1.getInt(serviceIndex);
			int ownerIndex = c1.getColumnIndex(ScheduleTable.own_ID);
			int owner_id = c1.getInt(ownerIndex);
			int despIndex = c1
					.getColumnIndex(ScheduleTable.schedule_Description);
			String desp = c1.getString(despIndex);

			Schedule newSchedule = new Schedule(owner_id, sche_id,
					serv_id + "", startDate, endDate, desp);
			allschedules.add(newSchedule);
		}
		// If not added,error will occour
		// IllegalStateException: Process 5808 exceeded cursor quota 100, will
		// kill it
		c1.close();
		// sharedDatabaseHelper.close();

		return allschedules;
	}

	/**
	 * Get all schedule create by owner
	 * */
	public ArrayList<Schedule> getMeSchedule() {
		ArrayList<Schedule> allschedules = new ArrayList<Schedule>();
		Cursor c1 = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ScheduleTable.ScheduleTableName + " WHERE "
						+ ScheduleTable.is_Deleted + "=0 and "
						+ ScheduleTable.own_ID + "="
						+ new SharedReference().getCurrentOwnerId(context)
						, null);
		while (c1.moveToNext()) {
			int startIndex = c1.getColumnIndex(ScheduleTable.start_Time);
			String startDate = c1.getString(startIndex);
			int endIndex = c1.getColumnIndex(ScheduleTable.end_Time);
			String endDate = c1.getString(endIndex);
			int scheduleIDIndex = c1.getColumnIndex(ScheduleTable.schedule_ID);
			int sche_id = c1.getInt(scheduleIDIndex);
			int serviceIndex = c1.getColumnIndex(ScheduleTable.service_ID);
			int serv_id = c1.getInt(serviceIndex);
			int ownerIndex = c1.getColumnIndex(ScheduleTable.own_ID);
			int owner_id = c1.getInt(ownerIndex);
			int despIndex = c1
					.getColumnIndex(ScheduleTable.schedule_Description);
			String desp = c1.getString(despIndex);

			Schedule newSchedule = new Schedule(owner_id, sche_id,
					serv_id + "", startDate, endDate, desp);
			allschedules.add(newSchedule);
		}
		// If not added,error will occour
		// IllegalStateException: Process 5808 exceeded cursor quota 100, will
		// kill it
		c1.close();
		// sharedDatabaseHelper.close();

		return allschedules;
	}

	public List<String> getScheduleHeaders() {
		String columnName = "strftime('%Y-%m-%d'," + ScheduleTable.start_Time
				+ ")";
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT " + columnName + " FROM "
						+ ScheduleTable.ScheduleTableName + " WHERE "
						+ ScheduleTable.is_Deleted + "=0" + " ORDER BY "
						+ columnName + " ASC", null);
		List<String> dates = new ArrayList<String>();
		List<String> distinctdates = new ArrayList<String>();
		while (c.moveToNext()) {
			int columnIndex = c.getColumnIndex(columnName);
			String Date = c.getString(columnIndex);
			// Log.i("database", Date);
			dates.add(Date);
		}

		String previousDate = "2";
		for (int i = 0; i < dates.size(); i++) {
			String newDate = dates.get(i);
			if (newDate.equalsIgnoreCase(previousDate) == false) {
				String fullNewDate = newDate + " 00:00:00";
				distinctdates.add(fullNewDate);
			}
			previousDate = newDate;
		}
		return distinctdates;
	}

	public List<Schedule> getUnsyncedNewSchedules() {
		List<Schedule> schedules = new ArrayList<Schedule>();
		Cursor c = this.getWritableDatabase()
				.rawQuery(
						"SELECT * FROM " + ScheduleTable.ScheduleTableName
								+ " WHERE " + ScheduleTable.is_Synchronized
								+ "= 0" + " " + "AND "
								+ ScheduleTable.last_Modified + " LIKE"
								+ "'no%'", null);
		while (c.moveToNext()) {
			int startIndex = c.getColumnIndex(ScheduleTable.start_Time);
			String startDate = c.getString(startIndex);
			int endIndex = c.getColumnIndex(ScheduleTable.end_Time);
			String endDate = c.getString(endIndex);
			int scheduleIDIndex = c.getColumnIndex(ScheduleTable.schedule_ID);
			int sche_id = c.getInt(scheduleIDIndex);
			int serviceIndex = c.getColumnIndex(ScheduleTable.service_ID);
			int serv_id = c.getInt(serviceIndex);
			int ownerIndex = c.getColumnIndex(ScheduleTable.own_ID);
			int owner_id = c.getInt(ownerIndex);
			int despIndex = c
					.getColumnIndex(ScheduleTable.schedule_Description);
			String desp = c.getString(despIndex);

			Schedule newSchedule = new Schedule(owner_id, sche_id,
					serv_id + "", startDate, endDate, desp);
			schedules.add(newSchedule);
		}
		return schedules;
	}

	public List<Schedule> getUnsyncedEditedSchedules() {
		List<Schedule> schedules = new ArrayList<Schedule>();
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ScheduleTable.ScheduleTableName + " WHERE "
						+ ScheduleTable.is_Synchronized + "= 0" + " " + "AND "
						+ ScheduleTable.last_Modified + " LIKE" + "'2%'", null);
		while (c.moveToNext()) {
			int startIndex = c.getColumnIndex(ScheduleTable.start_Time);
			String startDate = c.getString(startIndex);
			int endIndex = c.getColumnIndex(ScheduleTable.end_Time);
			String endDate = c.getString(endIndex);
			int scheduleIDIndex = c.getColumnIndex(ScheduleTable.schedule_ID);
			int sche_id = c.getInt(scheduleIDIndex);
			int serviceIndex = c.getColumnIndex(ScheduleTable.service_ID);
			int serv_id = c.getInt(serviceIndex);
			int ownerIndex = c.getColumnIndex(ScheduleTable.own_ID);
			int owner_id = c.getInt(ownerIndex);
			int despIndex = c
					.getColumnIndex(ScheduleTable.schedule_Description);
			String desp = c.getString(despIndex);

			Schedule newSchedule = new Schedule(owner_id, sche_id,
					serv_id + "", startDate, endDate, desp);
			schedules.add(newSchedule);
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
				while (c.moveToNext()) {
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
				// return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
			return newParticipant;
		}
	}

	public Sharedmember getSharedmember(int member_id, String activity_id) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + SharedMemberTable.SharedMemberTableName
						+ " WHERE " + SharedMemberTable.member_id + " = "
						+ member_id + " AND " + SharedMemberTable.service_id
						+ "=" + activity_id, null);
		while (c.moveToNext()) {
			int mem_id = c
					.getInt(c.getColumnIndex(SharedMemberTable.member_id));
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
		return null;
	}

	/**
	 * Get list participants of an activity 
	 * */
	public ArrayList<Sharedmember> getSharedMemberForActivity(
			String activity_id) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + SharedMemberTable.SharedMemberTableName
						+ " WHERE " + SharedMemberTable.service_id + "="
						+ activity_id, null);
		ArrayList<Sharedmember> list_shared_member = new ArrayList<Sharedmember>();
		while (c.moveToNext()) {
			int mem_id = c
					.getInt(c.getColumnIndex(SharedMemberTable.member_id));
			// int serviceid = c.getInt(c
			// .getColumnIndex(SharedMemberTable.service_id));
			int role = c.getInt(c.getColumnIndex(SharedMemberTable.role));
			String name = c.getString(c
					.getColumnIndex(SharedMemberTable.member_name));
			String email = c.getString(c
					.getColumnIndex(SharedMemberTable.member_email));
			String mobile = c.getString(c
							.getColumnIndex(SharedMemberTable.member_mobile));
			int sid=c.getInt(c
					.getColumnIndex(SharedMemberTable.smid));
			Sharedmember shareMember=new Sharedmember(mem_id, name, email, mobile, role, sid);
			list_shared_member.add(shareMember);

		}
		return list_shared_member;
	}
	
	
	
	/**
	 * Get list participants of an activity without role participant i.e. role
	 * owner and organizer
	 * */
	public ArrayList<Participant> getParticipantsOfActivityWithoutRoleParticipant(
			String activity_id) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + SharedMemberTable.SharedMemberTableName
						+ " WHERE " + SharedMemberTable.service_id + "="
						+ activity_id + " and " + SharedMemberTable.role
						+ " <>" + CommConstant.PARTICIPANT, null);
		ArrayList<Participant> list_participant = new ArrayList<Participant>();
		while (c.moveToNext()) {
			int mem_id = c
					.getInt(c.getColumnIndex(SharedMemberTable.member_id));
			// int serviceid = c.getInt(c
			// .getColumnIndex(SharedMemberTable.service_id));
			int role = c.getInt(c.getColumnIndex(SharedMemberTable.role));
			String name = c.getString(c
					.getColumnIndex(SharedMemberTable.member_name));
			String email = c.getString(c
					.getColumnIndex(SharedMemberTable.member_email));
			String mobile = c.getString(c
					.getColumnIndex(SharedMemberTable.member_mobile));
			Participant new_participant = new Participant(mem_id, name, email,
					mobile, role);
			list_participant.add(new_participant);

		}
		return list_participant;
	}

	/**
	 * Get list activity of a member joined in
	 * */
	public ArrayList<String> getListActivity(String member_id) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ActivityTable.ActivityTableName + ", "
						+ SharedMemberTable.SharedMemberTableName + " where "
						+ ActivityTable.is_Deleted + "=0 and "
						+ ActivityTable.service_ID + "="
						+ SharedMemberTable.service_id + " and "
						+ SharedMemberTable.member_id + "=" + member_id
						+ " and ", null);
		ArrayList<String> list_activity_id = new ArrayList<String>();
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex(ActivityTable.service_ID));
			list_activity_id.add(id);

		}
		return list_activity_id;
	}

	/**
	 * Get participant of an activity
	 * */
	public ArrayList<Sharedmember> getParticipantsOfActivity(String activity_id) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + SharedMemberTable.SharedMemberTableName
						+ " WHERE " + SharedMemberTable.service_id + "="
						+ activity_id+ " and "+SharedMemberTable.is_Deleted+"=0", null);
		ArrayList<Sharedmember> list_member = new ArrayList<Sharedmember>();
		while (c.moveToNext()) {
			int mem_id = c
					.getInt(c.getColumnIndex(SharedMemberTable.member_id));
			int role = c.getInt(c.getColumnIndex(SharedMemberTable.role));
			String name = c.getString(c
					.getColumnIndex(SharedMemberTable.member_name));
			String email = c.getString(c
					.getColumnIndex(SharedMemberTable.member_email));
			String mobile = c.getString(c
					.getColumnIndex(SharedMemberTable.member_mobile));
			int sid=c.getInt(c.getColumnIndex(SharedMemberTable.smid));
			Sharedmember new_participant = new Sharedmember(mem_id, name, email,
					mobile, role,sid);
			list_member.add(new_participant);

		}
		return list_member;
	}

	public ArrayList<Participant> getParticipants() {
		SharedReference ref = new SharedReference();
		ArrayList<Participant> participants = new ArrayList<Participant>();
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ParticipantTable.ParticipantTableName
						+ " WHERE " + ParticipantTable.is_Deleted + "=0"
						+ " AND " + ParticipantTable.own_ID + "="
						+ ref.getCurrentOwnerId(context) + " order by "
						+ ParticipantTable.participant_Name + " COLLATE NOCASE ASC", null);
		while (c.moveToNext()) {
			int id = c
					.getInt(c.getColumnIndex(ParticipantTable.participant_ID));
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
		return participants;
	}

	public List<Participant> getUnsyncedNewParticipants() {
		List<Participant> participants = new ArrayList<Participant>();
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ParticipantTable.ParticipantTableName
						+ " WHERE " + ScheduleTable.is_Synchronized + "= 0"
						+ " " + "AND " + ParticipantTable.last_Modified
						+ " LIKE " + "'no%'", null);
		while (c.moveToNext()) {
			int id = c
					.getInt(c.getColumnIndex(ParticipantTable.participant_ID));
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
		return participants;
	}

	public List<Participant> getUnsyncedEditedParticipants() {
		List<Participant> participants = new ArrayList<Participant>();
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT * FROM " + ParticipantTable.ParticipantTableName
						+ " WHERE " + ScheduleTable.is_Synchronized + "= 0"
						+ " AND " + ScheduleTable.is_Deleted + "= 0" + " "
						+ "AND " + ParticipantTable.last_Modified + " LIKE "
						+ "'2%'", null);
		while (c.moveToNext()) {
			int id = c
					.getInt(c.getColumnIndex(ParticipantTable.participant_ID));
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
		return participants;
	}

	public List<Integer> getParticipantsForSchedule(int schedule_id) {
		List<Integer> memberids = new ArrayList<Integer>();
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT " + OndutyTable.participant_ID + " FROM "
						+ OndutyTable.OntudyTableName + " WHERE "
						+ OndutyTable.schedule_ID + " = " + schedule_id, null);
		while (c.moveToNext()) {
			int memberid_Index = c.getColumnIndex(OndutyTable.participant_ID);
			int memberid = c.getInt(memberid_Index);
			memberids.add(memberid);
		}

		// Add this to prevent collapse
		c.close();
		// sharedDatabaseHelper.close();
		return memberids;
	}

	public List<Integer> getOndutyRecordsForSchedule(int id) {
		List<Integer> ondutyids = new ArrayList<Integer>();
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT " + OndutyTable.onduty_ID + " FROM "
						+ OndutyTable.OntudyTableName + " WHERE "
						+ OndutyTable.schedule_ID + " = " + id, null);
		while (c.moveToNext()) {
			int ondutyid_Index = c.getColumnIndex(OndutyTable.onduty_ID);
			int ondutyid = c.getInt(ondutyid_Index);
			ondutyids.add(ondutyid);
		}
		return ondutyids;
	}

	public boolean isActivityExisted(String activityID) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT " + ActivityTable.service_ID + " from "
						+ ActivityTable.ActivityTableName + " where "
						+ ActivityTable.service_ID + " = " + activityID, null);
		int id = -1;
		while (c.moveToNext())
			id = c.getInt(c.getColumnIndex(ActivityTable.service_ID));
		return (id == (-1)) ? false : true;
	}

	public boolean isParticipantExisted(int participantID) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT " + ParticipantTable.participant_ID + " from "
						+ ParticipantTable.ParticipantTableName + " where "
						+ ParticipantTable.participant_ID + " = "
						+ participantID, null);
		int id = -1;
		while (c.moveToNext())
			id = c.getInt(c.getColumnIndex(ParticipantTable.participant_ID));
		return (id == (-1)) ? false : true;
	}

	public boolean isScheduleExisted(int scheduleID) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT " + ScheduleTable.schedule_ID + " from "
						+ ScheduleTable.ScheduleTableName + " where "
						+ ScheduleTable.schedule_ID + " = " + scheduleID, null);
		int id = -1;
		while (c.moveToNext())
			id = c.getInt(c.getColumnIndex(ScheduleTable.schedule_ID));
		return (id == (-1)) ? false : true;
	}

	public boolean isOndutyExisted(int scheduleID) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT " + OndutyTable.schedule_ID + " from "
						+ OndutyTable.OntudyTableName + " where "
						+ OndutyTable.schedule_ID + " = " + scheduleID, null);
		int id = -1;
		while (c.moveToNext())
			id = c.getInt(c.getColumnIndex(OndutyTable.schedule_ID));
		return (id == (-1)) ? false : true;
	}

	public boolean isSharedmemberExisted(int memberid, String activityid) {
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT " + SharedMemberTable.member_id + " from "
						+ SharedMemberTable.SharedMemberTableName + " where "
						+ SharedMemberTable.member_id + " = " + memberid
						+ " AND " + SharedMemberTable.service_id + "="
						+ activityid, null);
		int id = -1;
		while (c.moveToNext())
			// Wrong get method
			// Couldn't read row 0, col -1 from CursorWindow. Make sure the
			// Cursor is initialized correctly before accessing data from it.
			// id = c.getInt(c.getColumnIndex(OndutyTable.schedule_ID));
			id = c.getInt(c.getColumnIndex(SharedMemberTable.member_id));
		return (id == (-1)) ? false : true;
	}

	public boolean insertParticipant(ContentValues newParticipant) {
		long result = this.getWritableDatabase().insertWithOnConflict(
				ParticipantTable.ParticipantTableName,
				ParticipantTable.participant_ID, newParticipant,
				SQLiteDatabase.CONFLICT_IGNORE);
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
				newActivity, SQLiteDatabase.CONFLICT_IGNORE);
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
				newSchedule, SQLiteDatabase.CONFLICT_IGNORE);
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
				SQLiteDatabase.CONFLICT_IGNORE);
		return (result == -1) ? false : true;
	}

	public boolean updateOnduty(int id, ContentValues newOnduty) {
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
				SQLiteDatabase.CONFLICT_IGNORE);
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

	public int numbersOfSchedulesUserParticipantIn() {
		int scheduleCounter = 0;
		SharedPreferences sp = context.getSharedPreferences("MyPreferences", 0);
		String userEmail = sp.getString("useremail", "");
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT " + OndutyTable.participant_ID + " FROM "
						+ OndutyTable.OntudyTableName, null);
		while (c.moveToNext()) {
			int index = c.getColumnIndex(OndutyTable.participant_ID);
			int memberid = c.getInt(index);
			Participant p = this.getParticipant(memberid);
			if ((p != null) && (p.getEmail().equalsIgnoreCase(userEmail)))
				scheduleCounter++;
		}
		return scheduleCounter;
	}

	public int numberOfActivitiesUserParticipateIn() {
		List<Integer> activities = new ArrayList<Integer>();
		SharedPreferences sp = context.getSharedPreferences("MyPreferences", 0);
		String userEmail = sp.getString("useremail", "");
		Cursor c = this.getWritableDatabase().rawQuery(
				"SELECT " + OndutyTable.participant_ID + ","
						+ OndutyTable.service_ID + " FROM "
						+ OndutyTable.OntudyTableName, null);
		while (c.moveToNext()) {
			int memberindex = c.getColumnIndex(OndutyTable.participant_ID);
			int memberid = c.getInt(memberindex);
			Participant p = this.getParticipant(memberid);
			if ((p != null) && (p.getEmail().equalsIgnoreCase(userEmail))) {
				int activityindex = c.getColumnIndex(OndutyTable.service_ID);
				int activityID = c.getInt(activityindex);
				if (activities.contains(activityID) == false)
					activities.add(activityID);
			}
		}
		return activities.size();
	}

	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
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

	public void evacuateDatabase() {
		this.getWritableDatabase().delete(ActivityTable.ActivityTableName,
				null, null);
		this.getWritableDatabase().delete(
				ParticipantTable.ParticipantTableName, null, null);
		this.getWritableDatabase().delete(
				SharedMemberTable.SharedMemberTableName, null, null);
		this.getWritableDatabase().delete(OndutyTable.OntudyTableName, null,
				null);
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
		int nextid = sp.getInt("nextmemberid", -1);
		Editor editor = sp.edit();
		editor.putInt("nextmemberid", nextid + 1);
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
