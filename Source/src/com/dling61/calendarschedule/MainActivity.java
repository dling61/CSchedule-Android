/**
 * Develop by Antking
 * */
package com.dling61.calendarschedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.dling61.calendarschedule.db.DatabaseHelper;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

/**
 * Class name: MainActivity Author: Huyen Nguyen Date: April 8th, 2014 This
 * class will show first when launch app
 * */
public class MainActivity extends Activity implements View.OnClickListener {
	Button btn_create_account;
	Button btn_sign_in;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		findViewById();
		onClickListener();
//		WebservicesHelper ws = new WebservicesHelper(mContext);
// 		ws.loginKarFarm();
//		postData();
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void postData() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.karfarm.com/users/sign_in");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("email",
					"edjhwang@gmail.com"));
			nameValuePairs.add(new BasicNameValuePair("password", "password"));

			// nameValuePairs.add(new
			// BasicNameValuePair("csrf_token","gjHIQ0EQzYcuLfPPR2MYhMDZifx9F+b+4BdkG4Ki2ww="));
			// nameValuePairs.add(new
			// BasicNameValuePair("csrf_token","EjCc4cWtUc8RBArsCs+TLvwNUivlPS5sDP9H42tgO9U="));
			JSONObject jsonParams = new JSONObject();
			try {
				jsonParams.put("email", "example@gmail.com");
				jsonParams.put("password", "Password");
				jsonParams.put("csrf-token",
						"gjHIQ0EQzYcuLfPPR2MYhMDZifx9F+b+4BdkG4Ki2ww=");

				httppost.setEntity(new ByteArrayEntity(jsonParams.toString()
						.getBytes("UTF8")));

				// httppost.setHeader("X-CSRF-Token","gjHIQ0EQzYcuLfPPR2MYhMDZifx9F+b+4BdkG4Ki2ww=");
				// Execute HTTP Post Request
				// HttpResponse response = httpclient.execute(httppost);
				// ResponseHandler<String> handler = new BasicResponseHandler();
				// String content = httpclient.execute(httppost, handler);
				HttpClient client = new DefaultHttpClient();
				HttpResponse resp = client.execute(httppost);
				HttpEntity entity = resp.getEntity();

				String response = EntityUtils.toString(entity);
				Log.d("aaa", response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	private void onClickListener() {
		btn_create_account.setOnClickListener(this);
		btn_sign_in.setOnClickListener(this);
	}

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
		Intent intent = new Intent(this, LoginActivity.class);
		this.startActivity(intent);
	}

	/**
	 * on click when click create account button finish this activity and start
	 * activity create new account
	 * */
	private void createAccountPressed() {
		finish();
		Intent intent = new Intent(this, CreateNewAccountActivity.class);
		this.startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		new AlertDialog.Builder(this).setTitle("Sure to Exit?")  
	    .setIcon(android.R.drawable.ic_dialog_info)  
	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
	  
	        @Override  
	        public void onClick(DialogInterface dialog, int which) {
	        SharedPreferences sp = getSharedPreferences("MyPreferences", 0)	;
	        Editor editor = sp.edit();
	        editor.clear();
	        editor.commit();
	        deleteDatabase(DatabaseHelper.DB_NAME);
	        System.exit(0);
	        }  
	    })  
	    .setNegativeButton("No", new DialogInterface.OnClickListener() {  
	  
	        @Override  
	        public void onClick(DialogInterface dialog, int which) {  
	        }  
	    }).show();  
	}
}
