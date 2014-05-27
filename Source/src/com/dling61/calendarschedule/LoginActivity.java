/**
 * Develop by Antking
 * */
package com.dling61.calendarschedule;

import org.json.JSONException;

import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;
import com.dling61.calendarschedule.utils.SharedReference;
import com.dling61.calendarschedule.utils.Utils;
import com.dling61.calendarschedule.views.TitleBarView;

import android.app.Activity;
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
 * @Date April 8th,2014 @ This class helps users login to app}
 * */
public class LoginActivity extends BaseActivity implements OnClickListener {
	Button signin_btn;
	Context mContext;
	EditText txt_email;
	EditText txt_password;

	String username, password = "";
	LinearLayout layoutForgetPassword;
	TitleBarView titleBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.log_in);
		Intent intent = getIntent();
		if (intent != null) {
			username = intent.getStringExtra(CommConstant.EMAIL);
			password = intent.getStringExtra(CommConstant.PASSWORD);
		}
		findViewById();
		onClickListener();
	}

	/**
	 * Find view by Id
	 * */
	private void findViewById() {
		titleBar=(TitleBarView)findViewById(R.id.titleBar);
		titleBar.tv_name.setText(getResources().getString(R.string.signin));
		titleBar.layout_next.setVisibility(View.GONE);
		signin_btn = (Button) findViewById(R.id.signin_btn);
		txt_email = (EditText) findViewById(R.id.txt_email);
		txt_password = (EditText) findViewById(R.id.txt_password);
		
		layoutForgetPassword=(LinearLayout)findViewById(R.id.layoutForgetPassword);
		if (username != null && (!username.equals(""))) {
			txt_email.setText(username);
		} else {
			SharedReference ref = new SharedReference();
			String email = ref.getEmail(mContext);
			txt_email.setText(email);
		}
		if (password != null && !password.equals("")) {
			txt_password.setText(password);
		}
	}

	/**
	 * On Click listener
	 * */
	private void onClickListener() {
		signin_btn.setOnClickListener(this);
		titleBar.layout_back.setOnClickListener(this);
		layoutForgetPassword.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == signin_btn) {
			String username = txt_email.getText().toString().trim();
			String password = txt_password.getText().toString().trim();
			if (username.equals("")) {
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.username_blank)
								+ "\n", Toast.LENGTH_LONG).show();

				return;
			}
			if (password.equals("")) {
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.password_blank)
								+ "\n", Toast.LENGTH_LONG).show();
				return;
			}
			login(username, password);
		} else if (v == titleBar.layout_back) {
			Utils.hideKeyboard((Activity)mContext,txt_email);
			Utils.hideKeyboard((Activity)mContext, txt_password);
			Intent intent = new Intent(mContext, MainActivity.class);
			mContext.startActivity(intent);
			finish();
			Utils.postLeftToRight(mContext);
		}
		else if(v==layoutForgetPassword)
			
		{
			Utils.hideKeyboard((Activity)mContext,txt_email);
			Utils.hideKeyboard((Activity)mContext, txt_password);

			Intent intent = new Intent(mContext, ForgetPasswordActivity.class);
			mContext.startActivity(intent);
			Utils.pushRightToLeft(mContext);
//			finish();
		}
	}

	/**
	 * Login
	 * */
	public void login(String email, String password) {

		Utils.hideKeyboard((Activity)mContext,txt_email);
		Utils.hideKeyboard((Activity)mContext, txt_password);
		
		WebservicesHelper helper = new WebservicesHelper(mContext);
		try {
			helper.login(email, password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Utils.hideKeyboard((Activity)mContext,txt_email);
		Utils.hideKeyboard((Activity)mContext, txt_password);
		if(WebservicesHelper.loadingPopup!=null&&WebservicesHelper.loadingPopup.isShowing())
		{
			WebservicesHelper.loadingPopup.dismiss();
		}
		Utils.postLeftToRight(mContext);
		finish();
		Intent intent = new Intent(mContext, MainActivity.class);
		mContext.startActivity(intent);
		
	}
}
