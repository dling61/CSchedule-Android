/**
 * Develop by Antking
 * */
package com.e2wstudy.cschedule;

import org.json.JSONException;

import com.e2wstudy.cschedule.net.WebservicesHelper;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.SharedReference;
import com.e2wstudy.cschedule.utils.Utils;
import com.e2wstudy.cschedule.views.TitleBarView;
import com.e2wstudy.cschedule.views.ToastDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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
		titleBar = (TitleBarView) findViewById(R.id.titleBar);
		titleBar.tv_name.setText(getResources().getString(R.string.signin));
		titleBar.layout_next.setVisibility(View.GONE);
		signin_btn = (Button) findViewById(R.id.signin_btn);
		txt_email = (EditText) findViewById(R.id.txt_email);
		txt_password = (EditText) findViewById(R.id.txt_password);

		layoutForgetPassword = (LinearLayout) findViewById(R.id.layoutForgetPassword);
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
			
//			WebservicesHelper ws=new WebservicesHelper(mContext);
//			ws.getServerSetting();
			
			String username = txt_email.getText().toString().trim();
			String password = txt_password.getText().toString().trim();
			if (username.equals("")) {
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(R.string.username_blank));
				dialog.show();

				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				return;
			}
			if (password.equals("")) {
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(R.string.password_blank));
				dialog.show();

				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				return;
			}
			
			
			login(username, password);
		} else if (v == titleBar.layout_back) {
			Utils.hideKeyboard((Activity) mContext, txt_email);
			Utils.hideKeyboard((Activity) mContext, txt_password);
			Intent intent = new Intent(mContext, MainActivity.class);
			mContext.startActivity(intent);
			finish();
			Utils.postLeftToRight(mContext);
		} else if (v == layoutForgetPassword)

		{
			Utils.hideKeyboard((Activity) mContext, txt_email);
			Utils.hideKeyboard((Activity) mContext, txt_password);

			Intent intent = new Intent(mContext, ForgetPasswordActivity.class);
			mContext.startActivity(intent);
			Utils.pushRightToLeft(mContext);
			// finish();
		}
	}

	
	Handler loginHandler = new Handler() {

	    @Override
	    public void handleMessage(Message msg) {

	        if (msg.what != 1) { // code if not connected
	        	final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(R.string.no_network));
				dialog.show();
				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	        } else { // code if connected
	        	WebservicesHelper helper = new WebservicesHelper(mContext);
	        	try {
					helper.login(txt_email.getText().toString(), txt_password.getText().toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }

	    }
	};
	/**
	 * Login
	 * */
	public void login(String email, String password) {
		Utils.hideKeyboard((Activity) mContext, txt_email);
		Utils.hideKeyboard((Activity) mContext, txt_password);
		Utils.isNetworkAvailable(loginHandler);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Utils.hideKeyboard((Activity) mContext, txt_email);
		Utils.hideKeyboard((Activity) mContext, txt_password);
		if (WebservicesHelper.loadingPopup != null
				&& WebservicesHelper.loadingPopup.isShowing()) {
			WebservicesHelper.loadingPopup.dismiss();
		}
		Utils.postLeftToRight(mContext);
		finish();
		Intent intent = new Intent(mContext, MainActivity.class);
		mContext.startActivity(intent);

	}
}
