/**
 * Develop by Antking
 * */
package com.dling61.calendarschedule;

import org.json.JSONException;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.CommConstant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @class LoginActivity
 * @author Huyen Nguyen
 * @version 1.0
 * @Date April 8th,2014 @ This class helps users login to app}
 * */
public class LoginActivity extends Activity implements OnClickListener {
	Button signin_btn;
	Context mContext;
	TextView txt_email;
	TextView txt_password;
	RelativeLayout layout_back;
	String username, password = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.log_in);
		Intent intent = getIntent();
		if (intent != null) {
			username = intent.getStringExtra(CommConstant.USERNAME);
			password = intent.getStringExtra(CommConstant.PASSWORD);
		}
		findViewById();
		onClickListener();
	}

	/**
	 * Find view by Id
	 * */
	private void findViewById() {
		signin_btn = (Button) findViewById(R.id.signin_btn);
		txt_email = (TextView) findViewById(R.id.txt_email);
		txt_password = (TextView) findViewById(R.id.txt_password);
		layout_back = (RelativeLayout) findViewById(R.id.layout_back);
		if (username != null && username.equals("")) {
			txt_email.setText(username);
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
		layout_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == signin_btn) {
			String username = txt_email.getText().toString().trim();
			String password = txt_password.getText().toString().trim();
			login(username, password);
		} else if (v == layout_back) {
			finish();
		}
	}

	/**
	 * Login
	 * */
	public void login(String email, String password) {

		WebservicesHelper helper = new WebservicesHelper(mContext);
		try {
			helper.login(email, password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
