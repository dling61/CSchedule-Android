package com.e2wstudy.cschedule;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.e2wstudy.cschedule.adapter.MyPagerAdapter;
import com.e2wstudy.cschedule.adapter.MyTabFactory;
import com.e2wstudy.cschedule.db.DatabaseHelper;
import com.e2wstudy.cschedule.fragments.AccountFragment;
import com.e2wstudy.cschedule.fragments.ActivityFragment;
import com.e2wstudy.cschedule.fragments.ContactFragment;
import com.e2wstudy.cschedule.fragments.ScheduleFragment;
import com.e2wstudy.cschedule.models.ActivityTable;
import com.e2wstudy.cschedule.models.Alert;
import com.e2wstudy.cschedule.models.Schedule;
import com.e2wstudy.cschedule.models.TimeZoneModel;
import com.e2wstudy.cschedule.net.WebservicesHelper;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.MyDate;
import com.e2wstudy.cschedule.utils.SharedReference;
import com.e2wstudy.cschedule.views.ConfirmDialog;
import com.e2wstudy.cschedule.views.CustomViewPager;
import com.e2wstudy.cschedule.views.LoadingPopupViewHolder;
import com.e2wstudy.cschedule.views.MenuAppView;
import com.e2wstudy.cschedule.views.ToastDialog;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

/**
 * @author Huyen
 * 
 */
public class CategoryTabActivity extends FragmentActivity implements
		OnTabChangeListener, OnPageChangeListener {
	MyPagerAdapter pageAdapter;
	private static CustomViewPager mViewPager;
	private static TabHost mTabHost;
	Context mContext;
	// HorizontalScrollView horizontalView;
	public MenuAppView menuApp;
	public static CategoryTabActivity sharedTab;
	public static int currentPage = 0;
	boolean isActivityDownloadDone = false;
	boolean isScheduleDownloadDone = false;
	public static int TAB_SCHEDULE = 0;
	public static int TAB_ACTIVITY = 2;
	public static LoadingPopupViewHolder loadingPopup;
	public static final int DIALOG_LOADING_THEME = android.R.style.Theme_Translucent_NoTitleBar;
	public static boolean flag_schedule = false;
	public static boolean flag_activity = false;
	public static boolean flag_contact = false;
	ArrayList<TimeZoneModel> listTimeZone=new ArrayList<TimeZoneModel>();
	ArrayList<Alert>listAlert=new ArrayList<Alert>();
	int tz=1;
	int alert=1;
	
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
		loadingPopup = new LoadingPopupViewHolder(mContext,
				DIALOG_LOADING_THEME);
		loadingPopup.setCancelable(false);

		mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
		menuApp = (MenuAppView) findViewById(R.id.menuTop);
		

		if(listTimeZone!=null&&listTimeZone.size()>0)
		{
			tz=listTimeZone.get(0).getId();
		}
		if(listAlert!=null&&listAlert.size()>0)
		{
			alert=listAlert.get(0).getId();
		}
		
		// Tab Initialization
		initialiseTabHost();
		initData();

		// Fragments and ViewPager Initialization
		List<Fragment> fragments = getFragments();
		pageAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(pageAdapter);
		mViewPager.setOnPageChangeListener(CategoryTabActivity.this);
		mViewPager.setOffscreenPageLimit(4);
		try {
			IntentFilter filterRefreshUpdate = new IntentFilter();
			filterRefreshUpdate.addAction("goToActivity");
			registerReceiver(goToActivity, filterRefreshUpdate);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//
	// @Override
	// protected void onPause() {
	// try {
	// unregisterReceiver(goToActivity);
	//
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// super.onPause();
	// };

	protected void onDestroy() {
		try {
			unregisterReceiver(goToActivity);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	};

	/**
	 * receiver when have no activity
	 * */
	BroadcastReceiver goToActivity = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			moveToPage(TAB_ACTIVITY);
		}
	};

	JsonHttpResponseHandler activityDownloadCompleteHandler = new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(int statusCode,
				Header[] headers, JSONObject response) {
			// TODO Auto-generated method stub
			super.onSuccess(statusCode, headers, response);
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

				WebservicesHelper ws = new WebservicesHelper(mContext);
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
					String description = service.getString("desp");
					newActivity.put(ActivityTable.service_description,
							description);
					Log.i("getActivitiesFromWeb service_description ",
							description + "");

					int is_deleted = 0;
					newActivity.put(ActivityTable.is_Deleted, is_deleted);
					int is_synchronized = 1;
					newActivity.put(ActivityTable.is_Synchronized,
							is_synchronized);
					newActivity.put(ActivityTable.user_login,
							new SharedReference().getCurrentOwnerId(mContext));
					String last_modified = service.getString("lastmodified");
					newActivity.put(ActivityTable.last_ModifiedTime,
							last_modified);
				
					Log.i("getActivitiesFromWeb lastmodified ", last_modified
							+ "");
					
					
					if (dbHelper.isActivityExisted(activityid) == false) {
						newActivity.put(ActivityTable.alertId, alert);
						newActivity.put(ActivityTable.timeZoneId, tz);
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
					// TODO: will delete if service get all schedule implemented
					ws.getSchedulesForActivity(activityid);

				}
				// SEND broadcast to activity
				Intent intent = new Intent(
						CommConstant.ACTIVITY_DOWNLOAD_SUCCESS);
				mContext.sendBroadcast(intent);
				ref.setLastestServiceLastModifiedTime(mContext, MyDate
						.transformPhoneDateTimeToUTCFormat(MyDate
								.getCurrentDateTime()));
				isActivityDownloadDone = true;
				if (dbHelper.getNumberActivity() > 0) {
					moveToPage(TAB_SCHEDULE);
				} else {
					moveToPage(TAB_ACTIVITY);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

//		public void onFailure(Throwable e, String response) {
		@Override
		public void onFailure(int statusCode,
				Header[] headers, String response,
				Throwable throwable) {
			// TODO Auto-generated method stub
			super.onFailure(statusCode, headers,
					response, throwable);
//			dimissDialog();
			CategoryTabActivity.flag_activity = true;
			if (flag_activity && flag_contact && flag_schedule
					&& loadingPopup.isShowing()) {
				dimissDialog();
			}
			final ToastDialog dialog = new ToastDialog(mContext, mContext
					.getResources().getString(R.string.error_load_activity));
			dialog.show();
			dialog.btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}

		// public void onStart() {
		// showLoading(mContext);
		// };
		//
		public void onFinish() {
			CategoryTabActivity.flag_activity = true;
			if (flag_activity && flag_contact && flag_schedule
					&& loadingPopup.isShowing()) {
				dimissDialog();
			}
		};
	};

	private void initData() {
		// get all data after that, go to tab
		WebservicesHelper ws = new WebservicesHelper(mContext);
		
		ws.getAllActivitys(activityDownloadCompleteHandler);
		ws.getParticipantsFromWeb();
//		ws.getAllSchedule();
//		ws.getServerSetting();
//		if (!CommConstant.DOWNLOAD_SETTING) {
			
//		}
	}

	// show loading
	public void showLoading(Context mContext) {
		if (loadingPopup == null) {
			loadingPopup = new LoadingPopupViewHolder(mContext,
					DIALOG_LOADING_THEME);
		}
		loadingPopup.setCancelable(false);
		loadingPopup.show();
	}

	public void dimissDialog() {
		if (loadingPopup != null && loadingPopup.isShowing()) {
			loadingPopup.dismiss();
		}
	}

	// Method to add a TabHost
	private static void addTabRight(CategoryTabActivity activity,
			TabHost tabHost, TabHost.TabSpec tabSpec, String title, int imageId) {
		MyTabFactory myTab = new MyTabFactory(activity, imageId);
		tabSpec.setContent(myTab);
		View v = myTab.createTabContent(title);
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
		// if (tabView != null) {
		// final int width = horizontalView.getWidth();
		// final int scrollPos = tabView.getLeft()
		// - (width - tabView.getWidth()) / 2;
		// horizontalView.scrollTo(scrollPos, 0);
		// } else {
		// horizontalView.scrollBy(positionOffsetPixels, 0);
		// }

	}

	public static void moveToPage(int page) {

		mViewPager.setCurrentItem(page);
	}

	public static void moveToTab(int page) {
		int pos = mTabHost.getCurrentTab();
		mViewPager.setCurrentItem(pos);
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
				"Schedule", R.drawable.btn_schedule_selector);
		CategoryTabActivity.addTabRight(CategoryTabActivity.this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab2").setIndicator("Tab2"), mContext
						.getResources().getString(R.string.contact),
				R.drawable.btn_contact_selector);
		// TODO Put here your Tabs
		CategoryTabActivity.addTabRight(CategoryTabActivity.this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab1").setIndicator("Tab1"),
				"Activity", R.drawable.btn_activity_selector);

		CategoryTabActivity.addTabRight(CategoryTabActivity.this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab3").setIndicator("Tab3"),
				"Account", R.drawable.btn_account_selector);
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
		final ConfirmDialog dialog = new ConfirmDialog(
				CategoryTabActivity.this, mContext.getResources().getString(
						R.string.sure_to_exit));
		dialog.show();
		dialog.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				SharedReference ref=new SharedReference();
				ref.setLastestParticipantLastModifiedTime(mContext, CommConstant.DEFAULT_DATE);
				ref.setLastestScheduleLastModifiedTime(mContext, CommConstant.DEFAULT_DATE);
				ref.setLastestServiceLastModifiedTime(mContext, CommConstant.DEFAULT_DATE);
				DatabaseHelper.getSharedDatabaseHelper(mContext).deleteTablesExitApp();
				System.exit(0);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		moveToPage(currentPage);

	}

}
