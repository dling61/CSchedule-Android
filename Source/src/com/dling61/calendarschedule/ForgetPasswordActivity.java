/**
 * Develop by Antking
 * */
package com.dling61.calendarschedule;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.dling61.calendarschedule.net.BaseUrl;
import com.dling61.calendarschedule.net.JSONParser;
import com.dling61.calendarschedule.utils.Utils;
import com.dling61.calendarschedule.views.TitleBarView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @class ForgetPasswordActivity
 * @author Huyen Nguyen
 * @version 1.0
 * */
public class ForgetPasswordActivity extends Activity implements OnClickListener {
	Button btn_reset_password;
	Context mContext;
	EditText txt_email;
//	RelativeLayout layout_back;
	String email;
	TitleBarView titleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.forget_password);
		findViewById();
		onClickListener();
	}

	/**
	 * Find view by Id
	 * */
	private void findViewById() {
		btn_reset_password = (Button) findViewById(R.id.btn_reset_password);
		txt_email = (EditText) findViewById(R.id.txt_email);
//		layout_back = (RelativeLayout) findViewById(R.id.layout_back);
		titleBar=(TitleBarView)findViewById(R.id.titleBar);
		titleBar.tv_name.setText(getResources().getString(R.string.forgot_password));
		titleBar.layout_next.setVisibility(View.GONE);
	}

	/**
	 * On Click listener
	 * */
	private void onClickListener() {
		btn_reset_password.setOnClickListener(this);
		titleBar.layout_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btn_reset_password) {
			String email = txt_email.getText().toString().trim();
			
			if (email.equals("")) {
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.username_blank)
								+ "\n", Toast.LENGTH_LONG).show();

				return;
			}
			new ForgetPasswordTask(mContext,email).execute();
			
		} else if (v == titleBar.layout_back) {
			Utils.hideKeyboard(ForgetPasswordActivity.this,txt_email);
			
			finish();
		}
	}

	// edit information
		class ForgetPasswordTask extends AsyncTask<String, Void, String> {

			ProgressDialog dialog;
			Context mContext;

			String email = "";

			public ForgetPasswordTask(Context mContext, String email) {
				this.mContext = mContext;
				dialog = ProgressDialog.show(mContext, "", mContext.getResources()
						.getString(R.string.processing), true);
				this.email = email;

			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				dialog.show();
			}

			@Override
			protected String doInBackground(String... params) {
				// if register successfully, it's logged in automatically on server
				try {
					ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
					param.add(new BasicNameValuePair("email", email));
					String feedbackLink = BaseUrl.BASEURL + "resetpw" + "?"
							+ BaseUrl.URL_POST_FIX;
					return JSONParser.getJsonFromURLPostNameValuePair(feedbackLink,
							param);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();

				}
				return "";

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(String result) {
				try {
					if (result != null) {
						if (result.contains("200")) {
							Toast.makeText(
									mContext,
									mContext.getResources().getString(
											R.string.reset_password_succesfully),
									Toast.LENGTH_LONG).show();
						} else if (result.contains("201")) {
							Toast.makeText(mContext,
									mContext.getResources().getString(
											R.string.email_does_not_exist),
									Toast.LENGTH_LONG).show();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();

				}
				
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				finish();
			}
		}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(mContext, MainActivity.class);
		mContext.startActivity(intent);
		finish();
	}
}
