/**
 * Develop by Antking
 * */
package com.dling61.calendarschedule;

import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.Utils;
import com.dling61.calendarschedule.views.TitleBarView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * @class LoginActivity
 * @author Huyen Nguyen
 * @version 1.0
 * @Date April 8th,2014 @ This class will show when click button create new
 *       account}
 * */
public class CreateNewAccountActivity extends BaseActivity
		implements OnClickListener {
	Context mContext;
	EditText name_tv;
	EditText email_tv;
	EditText passwd_tv;
	EditText mobile_tv;
	Button btn_create;
	ProgressDialog mDialog;
TitleBarView titleBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mContext = this;
		this.setContentView(R.layout.create_new_account);

		createProgressBar();
		findViewById();
		onClickListener();

	}

	/**
	 * create progress bar dialog
	 * */
	private void createProgressBar() {
		mDialog = new ProgressDialog(this);
		mDialog.setMessage(getResources().getString(R.string.please_wait));
		mDialog.setCancelable(false);
	}

	/**
	 * Find view by id
	 * */
	private void findViewById() {
		name_tv = (EditText) findViewById(R.id.account_name_input);
		email_tv = (EditText) findViewById(R.id.account_mail_input);
		passwd_tv = (EditText) findViewById(R.id.account_password_input);
		mobile_tv = (EditText) findViewById(R.id.account_mobile_input);
		btn_create = (Button) findViewById(R.id.btn_create);
		btn_create.setTypeface(Utils.getTypeFace(mContext));
		name_tv.setTypeface(Utils.getTypeFace(mContext));
		email_tv.setTypeface(Utils.getTypeFace(mContext));
		mobile_tv.setTypeface(Utils.getTypeFace(mContext));
		titleBar=(TitleBarView)findViewById(R.id.titleBar);
		
		titleBar.tv_name.setText(getResources().getString(R.string.create_account));
		titleBar.layout_next.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btn_create) {
			// mDialog.show();
			createNewAccount();
			// mDialog.dismiss();
		} else if (v == titleBar.layout_back) {
			finish();
			Intent intent=new Intent(mContext,MainActivity.class);
			mContext.startActivity(intent);
		}

	}

	/**
	 * On click listener
	 * */
	private void onClickListener() {
		btn_create.setOnClickListener(this);
		titleBar.layout_back.setOnClickListener(this);
	}

	/**
	 * create new account
	 * */
	private void createNewAccount() {
		String username = name_tv.getText().toString().trim();
		String email = email_tv.getText().toString().trim();
		String password = passwd_tv.getText().toString().trim();

		String mobile = mobile_tv.getText().toString().trim();
		boolean isNameOK = !username.equals("");
		boolean isEmailOK = (Utils.isEmailValid(email))&&(!username.equals(""));
		boolean isPasswordOK = password.length() >= 1;
		boolean isMobileOK = Utils.isMobileValid(mobile);

		String createLog = (isNameOK == false ? "\n"
				+ getResources().getString(R.string.username_empty) : "")
				+ (isEmailOK == false ? "\n"
						+ getResources().getString(R.string.email_invalid) : "")
				+ (isPasswordOK == false ? "\n"
						+ getResources().getString(
								R.string.password_is_not_blank) : "");
		if (!createLog.equals("")) {
			Toast.makeText(this, createLog, Toast.LENGTH_LONG).show();
			return;
		}
		// Name. email and password are necessary
		if (isNameOK & isEmailOK && isPasswordOK) {
			try {

				WebservicesHelper helper = new WebservicesHelper(mContext);
				helper.createAccount(email,password,username,mobile);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			return;
		}

	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent=new Intent(mContext,MainActivity.class);
		mContext.startActivity(intent);
		finish();
	}
	
}
