/**
 * Develop by Antking
 * */
package com.dling61.calendarschedule;
import com.dling61.calendarschedule.net.WebservicesHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

/**
 * Class name: MainActivity
 * Author: Huyen Nguyen Date: April 8th, 2014 This
 * class will show first when launch app
 * */
public class MainActivity extends Activity implements View.OnClickListener{
	Button btn_create_account;
	Button btn_sign_in;
Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext=this;
		findViewById();
		onClickListener();
	}

	/**
	 * Find view by id
	 * */
	private void findViewById() {
		btn_sign_in = (Button) findViewById(R.id.btn_sign_in);
		btn_create_account = (Button) findViewById(R.id.btn_create_account);
	}

	/**
	 * On click view listener
	 * */
	private void onClickListener()
	{
		btn_create_account.setOnClickListener(this);
		btn_sign_in.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==btn_sign_in)
		{
			signInPressed();
		}
		else if(v==btn_create_account)
		{
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
	 * on click when click sign in button
	 * finish this activity and start activity login
	 * */
	private void signInPressed() {
		finish();
		Intent intent = new Intent(this, LoginActivity.class);
		this.startActivity(intent);
	}

	/**
	 * on click when click create account button
	 * finish this activity and start activity create new account
	 * */
	private void createAccountPressed() {
		Intent intent = new Intent(this, CreateNewAccountActivity.class);
		this.startActivity(intent);
	}

}
