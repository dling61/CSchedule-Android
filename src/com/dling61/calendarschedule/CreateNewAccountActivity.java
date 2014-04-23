/**
 * Develop by Antking
 * */
package com.dling61.calendarschedule;

import java.util.ArrayList;

import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
/**
 * @class LoginActivity
 * @author Huyen Nguyen
 * @version 1.0
 * @Date April 8th,2014 @ This class will show when click button create new
 *       account}
 * */
public class CreateNewAccountActivity extends Activity
		implements OnClickListener {
	Context mContext;
	EditText name_tv;
	EditText email_tv;
	EditText passwd_tv;
	EditText mobile_tv;
	LinearLayout layout_back;
	Button btn_create;
	ProgressDialog mDialog;

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
		layout_back = (LinearLayout) findViewById(R.id.layout_back);
		btn_create.setTypeface(Utils.getTypeFace(mContext));
		name_tv.setTypeface(Utils.getTypeFace(mContext));
		email_tv.setTypeface(Utils.getTypeFace(mContext));
		mobile_tv.setTypeface(Utils.getTypeFace(mContext));
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btn_create) {
			// mDialog.show();
			createNewAccount();
			// mDialog.dismiss();
		} else if (v == layout_back) {
			finish();
		}

	}

	/**
	 * On click listener
	 * */
	private void onClickListener() {
		btn_create.setOnClickListener(this);
		layout_back.setOnClickListener(this);
	}

	/**
	 * create new account
	 * */
	private void createNewAccount() {
		String username = name_tv.getText().toString();
		String email = email_tv.getText().toString();
		String password = passwd_tv.getText().toString();

		String mobile = mobile_tv.getText().toString();
		boolean isNameOK = !username.equals("");
		boolean isEmailOK = Utils.isEmailValid(email);
		boolean isPasswordOK = password.length() >= 6;
		boolean isMobileOK = Utils.isMobileValid(mobile);

		String createLog = (isNameOK == false ? "\n"
				+ getResources().getString(R.string.username_empty) : "")
				+ (isEmailOK == false ? "\n"
						+ getResources().getString(R.string.email_invalid) : "")
				+ (isPasswordOK == false ? "\n"
						+ getResources().getString(
								R.string.password_length_should_be_6) : "");
		if (!createLog.equals("")) {
			Toast.makeText(this, createLog, Toast.LENGTH_LONG).show();
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
	
	
}
