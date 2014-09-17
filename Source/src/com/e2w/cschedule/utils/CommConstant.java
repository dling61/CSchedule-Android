package com.e2w.cschedule.utils;

import android.graphics.Color;
/**
 * @author khoahuyen
 * 
 */
public class CommConstant {

	public static final String USERNAME = "username";
	public static final String OWNER_ID = "ownerid";
	public static final String USER_ACCOUNT = "user_account";
	public static final String PASSWORD = "password";
	public static final String EMAIL = "email";
	public static final String MOBILE = "mobile";
	public static final String CURRENT_OWNER_ID = "currentownerid";
	public static final String NEXT_SERVICE_ID = "nextserviceid";
	public static final String NEXT_MEMBER_ID = "nextmemberid";
	public static final String NEXT_SCHEDULE_ID = "nextscheduleid";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	public static final String LAST_ACTIVITY_MODIFY = "lastactivitymodified";
	public static final String LAST_PARTICIPANT_MODIFY = "lastparticipantmodified";
	public static final String LAST_SCHEDULE_MODIFIED = "lastschedulemodified";
	public static final String LAST_UPDATE_TIME = "lastupdatetime";
	public static final String ACTIVITY_DOWNLOAD_SUCCESS = "cschedule.activities.ready";
	public static final String SCHEDULE_READY = "cschedule.schedules.ready";
	public static final String PARTICIPANT_READY = "cschedule.participants.ready";
	public static final String ADD_ACTIVITY_SUCCESS = "add.activity.success";
	public static final String GET_SHARED_MEMBER_ACTIVITY_COMPLETE = "shared.member.activity.complete";
	public static final long END_TIME_ACTIVITY = 30 * 60;
	public static final int ADD_PARTICIPANT_FOR_ACTIVITY = 5;
	public static final String TYPE = "type";
	public static final int ORGANIZER = 1;// (Organizer);
	public static final int PARTICIPANT = 2;// (Participant);
	public static final int VIEWER = 3;// (Viewer)
	public static final String ACTIVITY_ID = "activity_id";
	public static final int ROLE_SHARE_MEMBER_ACTIVITY = PARTICIPANT;
	public static final int ROLE_ASSIGN_MEMBER_SCHEDULE = ORGANIZER;
	public static final String DELETE_ACTIVITY_COMPLETE = "delete.activity.complete";
	public static final String ACTIVITY_DESCRIPTION = "activity_description";
	public static final String CONTACT_ID = "contact_id";
	public static final int TYPE_CONTACT = 1;
	public static final int TYPE_PARTICIPANT = 2;
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DELETE_CONTACT_COMPLETE = "delete.contact.complete";
	public static final String SCHEDULE_ID = "schedule_id";
	public static final String DELETE_SCHEDULE_COMPLETE = "delete.schedule.complete";
	public static final String ADD_CONTACT_SUCCESS = "add.contact.success";
	public static final String UPDATE_SCHEDULE = "update.schedule";
	public static final String DELETE_SHARED_MEMBER_FROM_ACTIVITY = "delete.shared.member.activity";
	public static final String ADD_SHARED_MEMBER_FROM_ACTIVITY = "add.shared.member.activity";
	public static final int OWNER = 0;
	public static final String LIST_PARTICIPANT_ID = "list_participant_id";
	public static final String SELECTED_ON_DUTY = "select.on.duty";
	public static final String ON_DUTY_ITEM_SELECTED = "select.item.on.duty";
	public static final String ROLE = "role";
	public static final String CREATOR = "creator";
	public static final String TIMEZONE_DOWNLOAD_SUCCESSFULLY = "timezone.download.successfully";
	public static final String ALERT_DOWNLOAD_SUCCESSFULLY = "alert.download.successfully";
	public static final int ALERT_DEFAULT = 0;
	public static final int CONFIRM_UNKNOWN = 0;
	public static final int CONFIRM_CONFIRMED = 1;
	public static final int CONFIRM_DENIED = 2;
	public static final int COLOR_UNKNOW=Color.GRAY;
	public static final int COLOR_CONFIRMED=Color.GREEN;
	public static final int COLOR_DENIED=Color.RED;
	public static final String OWNER_PARTICIPANT="owner_participant";
	public static final String SERVER_SETTING_SUCCESSFULLY = "server.setting.successfully";
	public static boolean DOWNLOAD_SETTING=false;
	public static final String CHANGE_CONFIRM_STATUS_SUCCESSFULLY="change.confirm.status.successfully";
	public static final String CONFIRM_STATUS="confirm.status";
	public static final String SENDER_ID="765836326761";
	
	public static boolean UPDATE=false;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	public static final String PROPERTY_APP_VERSION = "appVersion";
	public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public final static String DEFAULT_DATE="1700-01-01 12:00:00";
}
