package com.e2w.cschedule;

import org.json.JSONObject;

import com.e2w.cschedule.net.BaseUrl;
import com.e2w.cschedule.net.JSONParser;
import com.e2w.cschedule.utils.SharedReference;
import com.e2w.cschedule.utils.Utils;
import com.e2w.cschedule.views.LoadingPopupViewHolder;
import com.e2w.cschedule.views.TitleBarView;
import com.e2w.cschedule.views.ToastDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class FeedBackActivity extends BaseActivity implements OnClickListener {

	EditText edFeedback;
	// LinearLayout layout_back;
	// LinearLayout layout_send;
	Context mContext;
	TitleBarView titleBar;
	public static LoadingPopupViewHolder loadingPopup;
	public static final int DIALOG_LOADING_THEME = android.R.style.Theme_Translucent_NoTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutpage);
		mContext = this;
		edFeedback = (EditText) findViewById(R.id.ed_feedback);
		titleBar = (TitleBarView) findViewById(R.id.titleBar);
		titleBar.tv_name.setText(getResources().getString(R.string.feedback));
		titleBar.layout_save.setVisibility(View.VISIBLE);
		titleBar.layout_next.setVisibility(View.GONE);

		onClickListener();
	}

	private void onClickListener() {
		titleBar.layout_back.setOnClickListener(this);
		titleBar.layout_save.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// Utils.hideKeyboard((Activity)mContext,edFeedback);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == titleBar.layout_back) {
			Utils.hideKeyboard(FeedBackActivity.this, edFeedback);

			finish();
			// Utils.postLeftToRight(mContext);

		} else if (v == titleBar.layout_save) {
			Utils.hideKeyboard((Activity) mContext, edFeedback);
			String feedback = edFeedback.getText().toString().trim();
			if (feedback != null && (!feedback.equals(""))) {

				new FeedbackTask(mContext, feedback).execute();
			} else {
				final ToastDialog dialog = new ToastDialog(mContext, mContext
						.getResources().getString(
								R.string.please_enter_feedback));
				dialog.show();

				dialog.btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		}
	}

	// edit information
	class FeedbackTask extends AsyncTask<String, Void, String> {

		// ProgressDialog dialog;
		Context mContext;

		String feedback = "";

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

		public FeedbackTask(Context mContext, String feedback) {
			this.mContext = mContext;
			// dialog = ProgressDialog.show(mContext, "",
			// mContext.getResources()
			// .getString(R.string.processing), true);
			this.feedback = feedback;
			if (loadingPopup == null) {
				loadingPopup = new LoadingPopupViewHolder(mContext,
						DIALOG_LOADING_THEME);
			}
			loadingPopup.setCancelable(true);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// dialog.show();
			showLoading(mContext);
		}

		@Override
		protected String doInBackground(String... params) {
			// if register successfully, it's logged in automatically on server
			try {
				JSONObject sharedmemberParams = new JSONObject();
				sharedmemberParams.put("ownerid",
						new SharedReference().getCurrentOwnerId(mContext));
				sharedmemberParams.put("feedback", feedback);

				Log.d("param", sharedmemberParams.toString());
				String feedbackLink = BaseUrl.BASEURL + "feedback" + "?"
						+ BaseUrl.URL_POST_FIX;
				return JSONParser.getJsonFromURLPostNameValuePair(feedbackLink,
						sharedmemberParams.toString());
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
								mContext.getResources().getString(
										R.string.send_feedback_success));
						dialog.show();

						dialog.btnOk.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								finish();
							}
						});
					} else if (result.contains("201")) {
						final ToastDialog dialog = new ToastDialog(mContext,
								mContext.getResources().getString(
										R.string.invalid_content_feedback));
						dialog.show();

						dialog.btnOk.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
					} else if (result.equals("timeout_error")) {
						final ToastDialog dialog = new ToastDialog(mContext,
								mContext.getResources().getString(
										R.string.title_timetout));
						dialog.show();
						dialog.btnOk.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});

					} else if (result.equals("unknowhost_error")) {
						final ToastDialog dialog = new ToastDialog(mContext,
								mContext.getResources().getString(
										R.string.title_lost_connection));
						dialog.show();
						dialog.btnOk.setOnClickListener(new OnClickListener() {

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

			// if (dialog != null && dialog.isShowing()) {
			// dialog.dismiss();
			// }
			dimissDialog();

			// finish();
			// Utils.postLeftToRight(mContext);
		}
	}

}
