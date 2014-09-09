/**
 * Develop by Antking
 * */
package com.e2wstudy.cschedule;

import com.e2wstudy.cschedule.db.DatabaseHelper;
import com.e2wstudy.cschedule.net.WebservicesHelper;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.SharedReference;
import com.e2wstudy.cschedule.utils.Utils;
import com.e2wstudy.cschedule.views.ConfirmDialog;
import com.e2wstudy.cschedule.views.LoadingPopupViewHolder;
import com.e2wstudy.cschedule.views.TitleBarView;
import com.e2wstudy.schedule.interfaces.GetServerSettingInterface;
import com.e2wstudy.schedule.interfaces.LoadingInterface;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Class name: MainActivity Author: Huyen Nguyen Date: April 8th, 2014 This
 * class will show first when launch app
 * */
public class MainActivity extends BaseActivity implements View.OnClickListener {
	Button btn_create_account;
	Button btn_sign_in;
	Context mContext;
	TitleBarView titleBar;
	public static LoadingPopupViewHolder loadingPopup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		mContext = this;

		// serverSetting();

		WebservicesHelper ws = WebservicesHelper.getInstance();
		ws.getServerSetting(mContext, new GetServerSettingInterface() {

			@Override
			public void onComplete() {
				Utils.checkCurrentVersion(mContext);

			}

			@Override
			public void onError(String error) {
				// TODO Auto-generated method stub

			}
		}, new LoadingInterface() {

			@Override
			public void onStart() {
				showLoading(MainActivity.this);
			}

			@Override
			public void onFinish() {
				dimissDialog();
			}
		});

		findViewById();
		onClickListener();

	}

	// show loading
	public void showLoading(Context mContext) {
		try {
			if (loadingPopup == null) {
				loadingPopup = new LoadingPopupViewHolder(mContext,
						CategoryTabActivity.DIALOG_LOADING_THEME);
			}
			loadingPopup.setCancelable(true);
			if (!loadingPopup.isShowing()) {
				loadingPopup.show();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void dimissDialog() {
		try {
			if (loadingPopup != null && loadingPopup.isShowing()) {
				loadingPopup.dismiss();
			}
		} catch (Exception exx) {
			exx.printStackTrace();
		}
	}

	/**
	 * Find view by id
	 * */
	private void findViewById() {
		btn_sign_in = (Button) findViewById(R.id.btn_sign_in);
		btn_create_account = (Button) findViewById(R.id.btn_create_account);
		titleBar = (TitleBarView) findViewById(R.id.titleBar);
		titleBar.tv_name.setText(getResources().getString(R.string.app_name));
		titleBar.layout_back.setVisibility(View.GONE);
		titleBar.layout_next.setVisibility(View.GONE);
	}

	/**
	 * On click view listener
	 * */
	private void onClickListener() {
		btn_create_account.setOnClickListener(this);
		btn_sign_in.setOnClickListener(this);
	}

	// /**
	// * load server setting
	// * */
	// private void serverSetting()
	// {
	// WebservicesHelper ws = new WebservicesHelper(mContext);
	// ws.getServerSetting();
	// }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btn_sign_in) {

			signInPressed();
		} else if (v == btn_create_account) {
			createAccountPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * on click when click sign in button finish this activity and start
	 * activity login
	 * */
	private void signInPressed() {
		finish();
		Utils.pushRightToLeft(mContext);

		Intent intent = new Intent(this, LoginActivity.class);
		this.startActivity(intent);
	}

	/**
	 * on click when click create account button finish this activity and start
	 * activity create new account
	 * */
	private void createAccountPressed() {
		finish();
		Utils.pushRightToLeft(mContext);
		Intent intent = new Intent(this, CreateNewAccountActivity.class);
		this.startActivity(intent);
	}

	@Override
	public void onBackPressed() {

		final ConfirmDialog dialog = new ConfirmDialog(MainActivity.this,
				mContext.getResources().getString(R.string.sure_to_exit));
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
				SharedReference ref = new SharedReference();
				ref.setLastestParticipantLastModifiedTime(mContext,
						CommConstant.DEFAULT_DATE);
				ref.setLastestScheduleLastModifiedTime(mContext,
						CommConstant.DEFAULT_DATE);
				ref.setLastestServiceLastModifiedTime(mContext,
						CommConstant.DEFAULT_DATE);
				DatabaseHelper.getSharedDatabaseHelper(mContext)
						.deleteTablesExitApp();
				System.exit(0);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Utils.checkCurrentVersion(MainActivity.this);
	}
}
