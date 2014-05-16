/**
 * 
 */
package com.dling61.calendarschedule.fragments;

import com.dling61.calendarschedule.FeedBackActivity;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.utils.SharedReference;
import com.dling61.calendarschedule.views.AccountView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
		onClickListener();
	}

	public static AccountFragment getInstance() {
		return AccountFragment.getInstance();
	}

	private void onClickListener() {
		view.btn_feedback.setOnClickListener(this);
		view.btn_signout_account.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == view.btn_feedback) {
			feedback();
		} else if (v == view.btn_signout_account) {
			signout();
		}
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
	private void initData() {
		SharedReference ref = new SharedReference();
		String email = ref.getEmail(mContext);
		view.account_name_tv.setText(ref.getUsername(mContext));
		view.account_email_tv.setText(email);
		DatabaseHelper dbHelper = DatabaseHelper
				.getSharedDatabaseHelper(mContext);
		view.number_activities_tv.setText(String.valueOf(dbHelper
				.getNumberSchedule()));
		view.number_schedules_tv.setText(String.valueOf(dbHelper
				.getNumberActivity(email)));
		dbHelper.close();
	}

	/**
	 * Sign out
	 * */
	private void signout() {
		new AlertDialog.Builder(mContext).setTitle("Do you want to log out?")  
	    .setIcon(android.R.drawable.ic_dialog_info)  
	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
	  
	        @Override  
	        public void onClick(DialogInterface dialog, int which) {
	        	DatabaseHelper dbHelper = DatabaseHelper
	    				.getSharedDatabaseHelper(mContext);
	    		dbHelper.evacuateDatabase();
	    		((Activity) mContext).finish();
	        System.exit(0);
	        }  
	    })  
	    .setNegativeButton("No", new DialogInterface.OnClickListener() {  
	  
	        @Override  
	        public void onClick(DialogInterface dialog, int which) {  
	        }  
	    }).show();  
		
	
	}

	/**
	 * About
	 * */
	private void feedback() {
		Intent aboutintent = new Intent(mContext, FeedBackActivity.class);
		this.startActivity(aboutintent);
	}
}