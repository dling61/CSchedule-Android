package com.dling61.calendarschedule;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dling61.calendarschedule.adapter.MyPagerAdapter;
import com.dling61.calendarschedule.adapter.MyTabFactory;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.fragments.AccountFragment;
import com.dling61.calendarschedule.fragments.ActivityFragment;
import com.dling61.calendarschedule.fragments.ContactFragment;
import com.dling61.calendarschedule.fragments.ScheduleFragment;
import com.dling61.calendarschedule.models.ActivityTable;
import com.dling61.calendarschedule.models.Schedule;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.MyDate;
import com.dling61.calendarschedule.utils.SharedReference;
import com.dling61.calendarschedule.views.MenuAppView;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

/**
 * @author Huyen
 * 
 */
public class CategoryTabActivity extends FragmentActivity implements
		OnTabChangeListener, OnPageChangeListener {

	MyPagerAdapter pageAdapter;
	private static ViewPager mViewPager;
	private TabHost mTabHost;
	Context mContext;
	HorizontalScrollView horizontalView;
	public MenuAppView menuApp;
	public static CategoryTabActivity sharedTab;
	public static int currentPage = 0;
	boolean isActivityDownloadDone = false;
	boolean isScheduleDownloadDone = false;
	public static int TAB_SCHEDULE=0;
	public static int TAB_CONTACT=2;

	public static CategoryTabActivity getTab(Context context) {
		if (sharedTab == null) {
			sharedTab = new CategoryTabActivity();
		}
		return sharedTab;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_tab_view);
		mContext = this;
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		horizontalView = (HorizontalScrollView) findViewById(R.id.horizontalView);
		menuApp = (MenuAppView) findViewById(R.id.menuTop);
		// Tab Initialization
		initialiseTabHost();
		initData();

		// Fragments and ViewPager Initialization
		List<Fragment> fragments = getFragments();
		pageAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(pageAdapter);
		mViewPager.setOnPageChangeListener(CategoryTabActivity.this);
		mViewPager.setOffscreenPageLimit(4);
	}

	JsonHttpResponseHandler activityDownloadCompleteHandler = new JsonHttpResponseHandler() {
		public void onSuccess(JSONObject response) {
			final SharedReference ref = new SharedReference();
			final DatabaseHelper dbHelper = DatabaseHelper
					.getSharedDatabaseHelper(mContext);

			Log.i("get all activity", response.toString());
			try {
				// deleted services and schedule relationship with this
				// service
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
							dbHelper.deleteRelatedOnduty(schedule
									.getSchedule_ID());
							dbHelper.deleteSchedule(schedule.getSchedule_ID());
						}
						dbHelper.deleteActivity(id);
					}
				}

				// services
				JSONArray services = response.getJSONArray("services");
				int service_count = services.length();

				WebservicesHelper ws=new WebservicesHelper(mContext);
				for (int i = 0; i < service_count; i++) {
					JSONObject service = services.getJSONObject(i);
					ContentValues newActivity = new ContentValues();
					int ownid = service.getInt("creatorid");
					newActivity.put(ActivityTable.own_ID, ownid);
					Log.i("getActivitiesFromWeb own_ID ", ownid + "");
					String activityid = service.getString("serviceid");

					String serviceName = service.getString("servicename");
					newActivity.put(ActivityTable.service_Name, serviceName);
					Log.i("getActivitiesFromWeb service_Name ", serviceName
							+ "");
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
					newActivity.put(ActivityTable.service_description,
							description);
					Log.i("getActivitiesFromWeb service_description ",
							description + "");
//					int otc = new SharedReference().getTimeZone(mContext);
					String otc=service.getString("utcoff");
					newActivity.put(ActivityTable.otc_Offset, otc);
					int is_deleted = 0;
					newActivity.put(ActivityTable.is_Deleted, is_deleted);
					int is_synchronized = 1;
					newActivity.put(ActivityTable.is_Synchronized,
							is_synchronized);
					newActivity.put(ActivityTable.user_login, new SharedReference().getCurrentOwnerId(mContext));
					String last_modified = service.getString("lastmodified");
					newActivity.put(ActivityTable.last_ModifiedTime,
							last_modified);
					Log.i("getActivitiesFromWeb lastmodified ", last_modified
							+ "");

					if (dbHelper.isActivityExisted(activityid) == false) {
						newActivity.put(ActivityTable.service_ID, activityid);
						Log.i("getActivitiesFromWeb service_ID ", activityid
								+ "");
						if (dbHelper.insertActivity(newActivity))
							Log.i("database", "insert service " + serviceName
									+ " successfully!");
					} else {
						if (dbHelper.updateActivity(activityid, newActivity))
							Log.i("database", "update service " + serviceName
									+ " successfully!");
					}
					
					ws.getSharedmembersForActivity(activityid);
					//TODO: will delete if service get all schedule implemented
					ws.getSchedulesForActivity(activityid);
					
					
				}
				// SEND broadcast to activity
				Intent intent = new Intent(
						CommConstant.ACTIVITY_DOWNLOAD_SUCCESS);
				mContext.sendBroadcast(intent);
				ref.setLastestServiceLastModifiedTime(mContext, MyDate
						.transformLocalDateTimeToUTCFormat(MyDate
								.getCurrentDateTime()));
				isActivityDownloadDone = true;
				if (dbHelper.getNumberActivity() > 0) {
					moveToPage(TAB_SCHEDULE);
				}
				else 
				{
					moveToPage(TAB_CONTACT);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void onFailure(Throwable e, String response) {
			// Response failed :(
			Log.i("webservice", "Get Activities failed");
		}
	};

	private void initData() {
		// get all data after that, go to tab

		WebservicesHelper ws = new WebservicesHelper(mContext);
		ws.getAllActivitys(activityDownloadCompleteHandler);
		ws.getParticipantsFromWeb();
		ws.getAllSchedule();
	
	}

	// Method to add a TabHost
	private static void addTabRight(CategoryTabActivity activity,
			TabHost tabHost, TabHost.TabSpec tabSpec, String title, int imageId) {
		MyTabFactory myTab = new MyTabFactory(activity,imageId);
		tabSpec.setContent(myTab);
		View v=myTab.createTabContent(title);
		tabSpec.setIndicator(v);
		tabHost.addTab(tabSpec);
	}

	// Manages the Tab changes, synchronizing it with Pages
	public void onTabChanged(String tag) {
		int pos = this.mTabHost.getCurrentTab();
		mViewPager.setCurrentItem(pos);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// Manages the Page changes, synchronizing it with Tabs
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		int pos = mViewPager.getCurrentItem();
		currentPage = pos;
		this.mTabHost.setCurrentTab(pos);
		View tabView = mTabHost.getTabWidget().getChildAt(pos);
		if (tabView != null) {
			final int width = horizontalView.getWidth();
			final int scrollPos = tabView.getLeft()
					- (width - tabView.getWidth()) / 2;
			horizontalView.scrollTo(scrollPos, 0);
		} else {
			horizontalView.scrollBy(positionOffsetPixels, 0);
		}

	}

	public static void moveToPage(int page) {
		mViewPager.setCurrentItem(page);
	}

	@Override
	public void onPageSelected(int arg0) {

	}

	private List<Fragment> getFragments() {
		List<Fragment> fList = new ArrayList<Fragment>();

		// TODO Put here your Fragments
		ScheduleFragment schedule = new ScheduleFragment();
		ContactFragment contact = new ContactFragment();
		contact.setInSideTab(true);
		ActivityFragment activity = new ActivityFragment();
		
		
		AccountFragment account = new AccountFragment();
		fList.add(schedule);
		fList.add(contact);
		fList.add(activity);	
		fList.add(account);

		return fList;
	}

	// Tabs Creation
	private void initialiseTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		CategoryTabActivity.addTabRight(CategoryTabActivity.this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab3").setIndicator("Tab3"),
				"Schedule",R.drawable.btn_schedule_selector);
		CategoryTabActivity.addTabRight(CategoryTabActivity.this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab2").setIndicator("Tab2"),
				mContext.getResources().getString(R.string.contact),R.drawable.btn_contact_selector);
		// TODO Put here your Tabs
		CategoryTabActivity.addTabRight(CategoryTabActivity.this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab1").setIndicator("Tab1"),
				"Activity",R.drawable.btn_activity_selector);
	
		CategoryTabActivity.addTabRight(CategoryTabActivity.this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab3").setIndicator("Tab3"),
				"Account",R.drawable.btn_account_selector);
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.ic_menu_line);
		mTabHost.setOnTabChangedListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
//		new SharedReference().setLastestParticipantLastModifiedTime(mContext,"2014-01-01 00:00:00");
//		new SharedReference().setLastestScheduleLastModifiedTime(mContext, "2014-01-01 00:00:00");
//		new SharedReference().setLastestServiceLastModifiedTime(mContext, "2014-01-01 00:00:00");
		
		new AlertDialog.Builder(this).setTitle("Sure to Exit?")  
	    .setIcon(android.R.drawable.ic_dialog_info)  
	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
	  
	        @Override  
	        public void onClick(DialogInterface dialog, int which) {
	        SharedPreferences sp = getSharedPreferences("MyPreferences", 0)	;
	        Editor editor = sp.edit();
	        editor.clear();
	        editor.commit();
	        deleteDatabase(DatabaseHelper.DB_NAME);
	        System.exit(0);
	        }  
	    })  
	    .setNegativeButton("No", new DialogInterface.OnClickListener() {  
	  
	        @Override  
	        public void onClick(DialogInterface dialog, int which) {  
	        }  
	    }).show();  

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		moveToPage(currentPage);
	}

}
