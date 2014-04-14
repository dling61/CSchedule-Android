/**
 * 
 */
package com.dling61.calendarschedule.fragments;

import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.utils.SharedReference;
import com.dling61.calendarschedule.views.AccountView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * @author Huyen return account information by token
 * 
 */
public class AccountFragment extends Fragment implements OnClickListener {
	AccountView view;
	Context mContext;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		initData();
	}

	public static AccountFragment getInstance() {
		return AccountFragment.getInstance();
	}

	
	@Override
	public void onClick(View v) {
	
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = new AccountView(getActivity());
		this.view = (AccountView) view;
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		
	}

	@Override
	public void onPause() {
		super.onPause();
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
	
	/**
	 * Init data
	 * */
	private void initData()
	{
		SharedReference ref =  new SharedReference();
		view.account_name_tv.setText(ref.getUsername(mContext));
		view.account_email_tv.setText(ref.getUsername(mContext));
		DatabaseHelper dbHelper=DatabaseHelper.getSharedDatabaseHelper(mContext);
		view.number_activities_tv.setText(String.valueOf(dbHelper.numberOfActivitiesUserParticipateIn()));
		view.number_schedules_tv.setText(String.valueOf(dbHelper.numbersOfSchedulesUserParticipantIn()));
		dbHelper.close();
	}
}
