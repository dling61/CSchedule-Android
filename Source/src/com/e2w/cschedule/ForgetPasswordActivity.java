/**
 * Develop by Antking
 * */
package com.e2w.cschedule;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.e2w.cschedule.net.BaseUrl;
import com.e2w.cschedule.net.JSONParser;
import com.e2w.cschedule.utils.Utils;
import com.e2w.cschedule.views.LoadingPopupViewHolder;
import com.e2w.cschedule.views.TitleBarView;
import com.e2w.cschedule.views.ToastDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * @class ForgetPasswordActivity
 * @author Huyen Nguyen
 * @version 1.0
 * */
public class ForgetPasswordActivity extends BaseActivity implements OnClickListener {
	Button btn_reset_password;
	Context mContext;
	EditText txt_email;
//	RelativeLayout layout_back;
	String email;
	TitleBarView titleBar;
	public static LoadingPopupViewHolder loadingPopup;
	public static final int DIALOG_LOADING_THEME = android.R.style.Theme_Translucent_NoTitleBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.forget_password);
		
		if (loadingPopup == null) {
			loadingPopup = new LoadingPopupViewHolder(mContext,
					DIALOG_LOADING_THEME);
		}
		loadingPopup.setCancelable(false);
		
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
				final ToastDialog dialog = new ToastDialog(mContext,
						mContext.getResources().getString(R.string.username_blank));
				dialog.show();

				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				return;
			}
			Utils.hideKeyboard(ForgetPasswordActivity.this, txt_email);
			new ForgetPasswordTask(mContext,email).execute();
			
		} else if (v == titleBar.layout_back) {
			
			Utils.hideKeyboard(ForgetPasswordActivity.this,txt_email);
		
			finish();
			Utils.postLeftToRight(mContext);
			}
	}

	// edit information
		class ForgetPasswordTask extends AsyncTask<String, Void, String> {

//			ProgressDialog dialog;
			Context mContext;

			String email = "";

			public ForgetPasswordTask(Context mContext, String email) {
				this.mContext = mContext;
//				dialog = ProgressDialog.show(mContext, "", mContext.getResources()
//						.getString(R.string.processing), true);
				this.email = email;

			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
//				dialog.show();
				showLoading(mContext);
			}

			@Override
			protected String doInBackground(String... params) {
				// if register successfully, it's logged in automatically on server
				try {
					ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
					param.add(new BasicNameValuePair("email", email));
					String feedbackLink = BaseUrl.BASEURL + "resetpw" + "?"
							+ BaseUrl.URL_POST_FIX;
					return JSONParser.getJsonFromURLPostNameValuePair(mContext,feedbackLink,
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
							final ToastDialog dialog = new ToastDialog(mContext,
									mContext.getResources().getString(R.string.reset_password_succesfully));
							dialog.show();

							dialog.btnOk.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									dialog.dismiss();
								}
							});
							
							
							
						} else if (result.contains("201")) {
							final ToastDialog dialog = new ToastDialog(mContext,
									mContext.getResources().getString(R.string.email_does_not_exist));
							dialog.show();

							dialog.btnOk.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									dialog.dismiss();
								}
							});
						}
						else if(result.equals("timeout_error"))
						{
							final ToastDialog dialog = new ToastDialog(
									mContext,
									mContext.getResources()
											.getString(
													R.string.title_timetout));
							dialog.show();
							dialog.btnOk
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											dialog.dismiss();
										}
									});

						}
						else if(result.equals("unknowhost_error"))
						{
							final ToastDialog dialog = new ToastDialog(
									mContext,
									mContext.getResources()
											.getString(
													R.string.title_lost_connection));
							dialog.show();
							dialog.btnOk
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											dialog.dismiss();
										}
									});

						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();

				}
				
//				if (dialog != null && dialog.isShowing()) {
//					dialog.dismiss();
//				}
				
				dimissDialog();
				
				
			}
		}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Utils.hideKeyboard(ForgetPasswordActivity.this, txt_email);
		Intent intent = new Intent(mContext, MainActivity.class);
		mContext.startActivity(intent);
		finish();
	}
	
	// show loading
	public void showLoading(Context mContext) {
		try {
			// if (loadingPopup == null) {
			// loadingPopup = new LoadingPopupViewHolder(mContext,
			// CategoryTabActivity.DIALOG_LOADING_THEME);
			// }
			// loadingPopup.setCancelable(false);

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
}
