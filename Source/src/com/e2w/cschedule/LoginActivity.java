/**
 * Develop by Antking
 * */
package com.e2w.cschedule;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;

import com.bugsense.trace.BugSenseHandler;
import com.e2w.cschedule.interfaces.LoadingInterface;
import com.e2w.cschedule.interfaces.LoginInterface;
import com.e2w.cschedule.interfaces.SetTokenInterface;
import com.e2w.cschedule.net.WebservicesHelper;
import com.e2w.cschedule.utils.CommConstant;
import com.e2w.cschedule.utils.SharedReference;
import com.e2w.cschedule.utils.Utils;
import com.e2w.cschedule.views.LoadingPopupViewHolder;
import com.e2w.cschedule.views.TitleBarView;
import com.e2w.cschedule.views.ToastDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
public class LoginActivity extends Activity implements OnClickListener {
	Button signin_btn;
	Context mContext;
	EditText txt_email;
	EditText txt_password;
	String username, password = "";
	LinearLayout layoutForgetPassword;
	TitleBarView titleBar;

	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	// String SENDER_ID = "Your-Sender-ID";

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "LoginActivity";
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	String regid;
	public static LoadingPopupViewHolder loadingPopup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, CommConstant.BUGSENSE_KEY);
		mContext = this;
		setContentView(R.layout.log_in);
		Intent intent = getIntent();
		if (intent != null) {
			username = intent.getStringExtra(CommConstant.EMAIL);
			password = intent.getStringExtra(CommConstant.PASSWORD);
		}
		findViewById();

		onClickListener();

		// Check device for Play Services APK.
		if (checkPlayServices()) {
			// If this check succeeds, proceed with normal processing.
			// Otherwise, prompt user to get valid Play Services APK.
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = new SharedReference().getRegistrationId(mContext);

			// if (regid.equals("")) {
			new RegisterGCMTask().execute();
			// }
		}

	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */

	class RegisterGCMTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			String msg = "";
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(mContext);
				}
				regid = gcm.register(CommConstant.SENDER_ID);
				Log.d("registration id", regid);
				msg = "Device registered, registration ID=" + regid;

				// You should send the registration ID to your server over HTTP,
				// so it can use GCM/HTTP or CCS to send messages to your app.
				// The request to your server should be authenticated if your
				// app
				// is using accounts.

				// sendRegistrationIdToBackend();

				// For this demo: we don't need to send it because the device
				// will send upstream messages to a server that echo back the
				// message using the 'from' address in the message.

				// Persist the regID - no need to register again.
				// storeRegistrationId(context, regid);
				new SharedReference().storeRegistrationId(mContext, regid);
			} catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
				// If there is an error, don't just keep trying to register.
				// Require the user to click a button again, or perform
				// exponential back-off.
			}
			return msg;
		}

		@Override
		protected void onPostExecute(String msg) {
			// finish();
		}
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		Log.d("google service code",
				GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE + "");
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						CommConstant.PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("GCM Information", "This device is not supported.");
				// finish();
			}
			return false;
		}
		return true;
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

			// WebservicesHelper ws=new WebservicesHelper(mContext);
			// ws.getServerSetting();

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


	LoadingInterface loadingInterface=new LoadingInterface() {
		
		@Override
		public void onStart() {
			showLoading(LoginActivity.this);
			
		}
		
		@Override
		public void onFinish() {
			dimissDialog();
			
		}
	};
	
	LoginInterface loginInterface = new LoginInterface() {

		@Override
		public void onError(String error) {
			Log.d("login error",error);
			if(error==null||error.equals(""))
			{
				error="Invalid username or password";
			}
			final ToastDialog errorToast = new ToastDialog(
					mContext,
					error);
			errorToast.show();
			errorToast.btnOk
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							errorToast.dismiss();
						}
					});


		}

		@Override
		public void onComplete() {
			Log.d("login success","success");
			
			finish();
			Utils.postLeftToRight(mContext);
			Intent intent = new Intent(mContext,
					CategoryTabActivity.class);
			startActivity(intent);
			
			SetTokenInterface setTokenInterface=new SetTokenInterface() {
				
				@Override
				public void onError(String error) {
					final ToastDialog dialog = new ToastDialog(mContext,
							error);
					dialog.show();

					dialog.btnOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}
				
				@Override
				public void onComplete() {
					
					
				}
			};
			//set Token
			try {
				WebservicesHelper.getInstance().setToken(mContext,new SharedReference().getOwnerId(mContext),
						Utils.getDeviceId(mContext),
						new SharedReference()
								.getRegistrationId(mContext), setTokenInterface, loadingInterface);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
	};
	
	

	// show loading
	public void showLoading(Context Context) {
		try {
			if (loadingPopup == null) {
				loadingPopup = new LoadingPopupViewHolder(LoginActivity.this,
						CategoryTabActivity.DIALOG_LOADING_THEME);
			}
			loadingPopup.setCancelable(true);
			loadingPopup.show();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void dimissDialog() {
		try {
			// if (loadingPopup != null && loadingPopup.isShowing()) {
			loadingPopup.dismiss();
			loadingPopup.cancel();
			// }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Login
	 * */
	public void login(String email, String password) {
		Utils.hideKeyboard((Activity) mContext, txt_email);
		Utils.hideKeyboard((Activity) mContext, txt_password);
		WebservicesHelper helper =WebservicesHelper.getInstance();
		try {
			helper.login(mContext,txt_email.getText().toString(), txt_password.getText()
					.toString(), loginInterface,loadingInterface);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
