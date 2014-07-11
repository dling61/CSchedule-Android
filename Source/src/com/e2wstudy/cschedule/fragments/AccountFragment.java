/**
 * 
 */
package com.e2wstudy.cschedule.fragments;

import com.e2wstudy.cschedule.FeedBackActivity;
import com.e2wstudy.cschedule.R;
import com.e2wstudy.cschedule.db.DatabaseHelper;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.SharedReference;
import com.e2wstudy.cschedule.utils.Utils;
import com.e2wstudy.cschedule.views.AccountView;
import com.e2wstudy.cschedule.views.ConfirmDialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
		
		onClickListener();
	}

	public static AccountFragment getInstance() {
		return AccountFragment.getInstance();
	}

	private void onClickListener() {
		view.btn_feedback.setOnClickListener(this);
		view.btn_signout_account.setOnClickListener(this);
		view.btn_rate_us.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == view.btn_feedback) {
			feedback();
		} else if (v == view.btn_signout_account) {
			signout();
		}
		else if(v==view.btn_rate_us)
		{
			Utils.goToGooglePlay(getActivity(), getActivity().getResources().getString(R.string.package_name));
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
		Log.d("on resume","account");
		initData();
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
				.getNumberActivity()));
		view.number_schedules_tv.setText(String.valueOf(dbHelper
				.getNumberSchedule()));
		dbHelper.close();
	}
	
	BroadcastReceiver scheduleReadyComplete = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			Log.d("add schedule", "receiver");
			initData();
		}

	};

	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		IntentFilter filterRefreshUpdate = new IntentFilter();
		filterRefreshUpdate.addAction(CommConstant.DELETE_SCHEDULE_COMPLETE);
		filterRefreshUpdate.addAction(CommConstant.SCHEDULE_READY);
		filterRefreshUpdate.addAction(CommConstant.UPDATE_SCHEDULE);
		filterRefreshUpdate.addAction(CommConstant.CHANGE_CONFIRM_STATUS_SUCCESSFULLY);
		getActivity().registerReceiver(scheduleReadyComplete,
				filterRefreshUpdate);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		getActivity().unregisterReceiver(scheduleReadyComplete);
	}

	/**
	 * Sign out
	 * */
	private void signout() {
		final ConfirmDialog dialog = new ConfirmDialog(mContext,
				"Do you want to log out?");
		dialog.show();
		dialog.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				String email=new SharedReference().getEmail(mContext);
				DatabaseHelper dbHelper = DatabaseHelper
						.getSharedDatabaseHelper(mContext);
				dbHelper.evacuateDatabase();
				new SharedReference().setUsername(mContext, email);
				new SharedReference().setEmail(mContext, email);
				new SharedReference().setLastestParticipantLastModifiedTime(mContext, "");
				new SharedReference().setLastestScheduleLastModifiedTime(mContext, "");
				new SharedReference().setLastestServiceLastModifiedTime(mContext, "");
				new SharedReference().setTimeZone(mContext, "");
				new SharedReference().setOwnerId(mContext, "");
				new SharedReference().setAccount(mContext, "");
				((Activity) mContext).finish();
				System.exit(0);
			}
		});

	}

	/**
	 * About
	 * */
	private void feedback() {
//		Utils.pushRightToLeft(getActivity());
		Intent aboutintent = new Intent(mContext, FeedBackActivity.class);
		this.startActivity(aboutintent);
	}
}
