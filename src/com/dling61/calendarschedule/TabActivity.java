package com.dling61.calendarschedule;

import java.util.ArrayList;
import java.util.List;

import com.dling61.calendarschedule.adapter.MyPagerAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.fragments.AccountFragment;
import com.dling61.calendarschedule.fragments.ActivityFragment;
import com.dling61.calendarschedule.fragments.ContactFragment;
import com.dling61.calendarschedule.fragments.ScheduleFragment;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.views.MenuAppView;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;

public class TabActivity extends FragmentActivity implements
		View.OnClickListener, OnPageChangeListener {
	private LinearLayout llTabActivity, llTabPaticipant, llTabSchedule,
			llTabAccount;
	// private ImageView activityImgV, memberImgV, scheduleImgV, accountImgV;
	private ViewPager viewpager;
	// private ProgressDialog mDialog;
	DatabaseHelper dbHelper;
	Context mContext;
	public MenuAppView menuApp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabpage);
		mContext = this;
		this.findViewById();
		onClickListener();
		this.initAdapters();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("tab activity", "activity resumes");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// mDialog.dismiss();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = this.getMenuInflater();
		// get all data from server
		initData();

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final int position = info.position;
		switch (item.getItemId()) {

		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	public void registerForBroadcasts() {

	}

	// public void initallImages() {
	// activityImgV.setImageDrawable(getResources().getDrawable(
	// R.drawable.tab_activity_normal));
	// memberImgV.setImageDrawable(getResources().getDrawable(
	// R.drawable.tab_member_normal));
	// scheduleImgV.setImageDrawable(getResources().getDrawable(
	// R.drawable.tab_schedule_normal));
	// accountImgV.setImageDrawable(getResources().getDrawable(
	// R.drawable.tab_account_normal));
	// }

	// find view by id
	public void findViewById() {
		llTabActivity = (LinearLayout) this.findViewById(R.id.ll_tab_activity);

		llTabPaticipant = (LinearLayout) this
				.findViewById(R.id.ll_tab_participant);

		llTabSchedule = (LinearLayout) this.findViewById(R.id.ll_tab_schedule);

		llTabAccount = (LinearLayout) this.findViewById(R.id.ll_tab_account);

		// activityImgV = (ImageView) this.findViewById(R.id.tab_imgv_activity);
		//
		// memberImgV = (ImageView) this.findViewById(R.id.tab_imgv_member);
		//
		// scheduleImgV = (ImageView) this.findViewById(R.id.tab_imgv_schedule);
		//
		// accountImgV = (ImageView) this.findViewById(R.id.tab_imgv_account);
		//
		viewpager = (ViewPager) this.findViewById(R.id.tab_viewpager);
	}

	// onclicklistener
	private void onClickListener() {
		llTabActivity.setOnClickListener(this);
		llTabPaticipant.setOnClickListener(this);
		llTabSchedule.setOnClickListener(this);
		llTabAccount.setOnClickListener(this);
	}

	private void initData() {
		dbHelper = DatabaseHelper.getSharedDatabaseHelper(mContext);
		WebservicesHelper ws = new WebservicesHelper(mContext);
		ws.getAllActivitys(new JsonHttpResponseHandler());
		ws.getParticipantsFromWeb();
		ws.getAllSchedule();
	}

	private List<Fragment> getFragments() {
		List<Fragment> fList = new ArrayList<Fragment>();

		// TODO Put here your Fragments
		ActivityFragment activity = new ActivityFragment();
		ContactFragment contact = new ContactFragment();
		contact.setInSideTab(true);
		ScheduleFragment schedule = new ScheduleFragment();
		AccountFragment account = new AccountFragment();

		fList.add(activity);
		fList.add(contact);
		fList.add(schedule);
		fList.add(account);

		return fList;
	}

	public void initAdapters() {
		// Fragments and ViewPager Initialization
		List<Fragment> fragments = getFragments();

		MyPagerAdapter pageAdapter = new MyPagerAdapter(
				TabActivity.this.getSupportFragmentManager(), fragments);
		viewpager.setAdapter(pageAdapter);
		viewpager.setOnPageChangeListener(TabActivity.this);
		viewpager.setOffscreenPageLimit(4);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// Manages the Page changes, synchronizing it with Tabs
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int arg0) {
		// activityImgV.setImageDrawable(getResources().getDrawable(
		// R.drawable.tab_activity_normal));
		// memberImgV.setImageDrawable(getResources().getDrawable(
		// R.drawable.tab_member_normal));
		// scheduleImgV.setImageDrawable(getResources().getDrawable(
		// R.drawable.tab_schedule_normal));
		// accountImgV.setImageDrawable(getResources().getDrawable(
		// R.drawable.tab_account_normal));
		// switch (arg0) {
		// case 0:
		// activityImgV.setImageDrawable(getResources().getDrawable(
		// R.drawable.tab_activity_pressed));
		// break;
		// case 1:
		// memberImgV.setImageDrawable(getResources().getDrawable(
		// R.drawable.tab_member_pressed));
		// break;
		// case 2:
		// scheduleImgV.setImageDrawable(getResources().getDrawable(
		// R.drawable.tab_schedule_pressed));
		//
		// break;
		// case 3:
		// accountImgV.setImageDrawable(getResources().getDrawable(
		// R.drawable.tab_account_pressed));
		// // ((TabActivity) thisContext).initAccountViews();
		// break;
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int viewId = v.getId();
		// ImageView imgv = (ImageView)v;
		// this.initallImages();
		switch (viewId) {
		// case R.id.tab_imgv_activity:
		case R.id.ll_tab_activity:
			// activityImgV.setImageDrawable(this.getResources().getDrawable(
			// R.drawable.tab_activity_pressed));
			viewpager.setCurrentItem(0);
			break;
		// case R.id.tab_imgv_member:
		case R.id.ll_tab_participant:
			// memberImgV.setImageDrawable(this.getResources().getDrawable(
			// R.drawable.tab_member_pressed));
			viewpager.setCurrentItem(1);
			break;
		// case R.id.tab_imgv_schedule:
		case R.id.ll_tab_schedule:
			// scheduleImgV.setImageDrawable(this.getResources().getDrawable(
			// R.drawable.tab_schedule_pressed));
			viewpager.setCurrentItem(2);
			break;
		// case R.id.tab_imgv_account:
		case R.id.ll_tab_account:
			// accountImgV.setImageDrawable(this.getResources().getDrawable(
			// R.drawable.tab_account_pressed));
			viewpager.setCurrentItem(3);
			break;
		}
	}

}
