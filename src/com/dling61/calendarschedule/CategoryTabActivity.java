package com.dling61.calendarschedule;

import java.util.ArrayList;
import java.util.List;

import com.dling61.calendarschedule.adapter.MyPagerAdapter;
import com.dling61.calendarschedule.adapter.MyTabFactory;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.fragments.AccountFragment;
import com.dling61.calendarschedule.fragments.ActivityFragment;
import com.dling61.calendarschedule.fragments.ContactFragment;
import com.dling61.calendarschedule.fragments.ScheduleFragment;
import com.dling61.calendarschedule.views.MenuAppView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
		menuApp=(MenuAppView)findViewById(R.id.menuTop);
		// Tab Initialization
		initialiseTabHost();

		// Fragments and ViewPager Initialization
		List<Fragment> fragments = getFragments();
		pageAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(pageAdapter);
		mViewPager.setOnPageChangeListener(CategoryTabActivity.this);
		mViewPager.setOffscreenPageLimit(5);
	}

	// Method to add a TabHost
	private static void addTabRight(CategoryTabActivity activity,
			TabHost tabHost, TabHost.TabSpec tabSpec, String title) {
		MyTabFactory myTab = new MyTabFactory(activity);
		tabSpec.setContent(myTab);
		tabSpec.setIndicator(myTab.createTabRightContent(title));
		tabHost.addTab(tabSpec);
	}

	private static void addTab(CategoryTabActivity activity, TabHost tabHost,
			TabHost.TabSpec tabSpec, String title) {
		MyTabFactory myTab = new MyTabFactory(activity);
		tabSpec.setContent(myTab);
		tabSpec.setIndicator(myTab.createTabContent(title));
		tabHost.addTab(tabSpec);
	}

	// Manages the Tab changes, synchronizing it with Pages
	public void onTabChanged(String tag) {
		int pos = this.mTabHost.getCurrentTab();
		this.mViewPager.setCurrentItem(pos);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// Manages the Page changes, synchronizing it with Tabs
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		int pos = this.mViewPager.getCurrentItem();
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
	// Tabs Creation
	private void initialiseTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		// TODO Put here your Tabs
		CategoryTabActivity.addTabRight(CategoryTabActivity.this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab1").setIndicator("Tab1"),
				"Activity");

		CategoryTabActivity.addTabRight(CategoryTabActivity.this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab2").setIndicator("Tab2"),
				"Participant");
		CategoryTabActivity.addTabRight(CategoryTabActivity.this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab3").setIndicator("Tab3"),
				"Schedule");
		CategoryTabActivity.addTabRight(CategoryTabActivity.this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab3").setIndicator("Tab3"),
				"Account");
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.ic_menu_line);
		mTabHost.setOnTabChangedListener(this);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
	}
	
}
