package com.dling61.calendarschedule;

import org.json.JSONObject;

import com.dling61.calendarschedule.net.BaseUrl;
import com.dling61.calendarschedule.net.JSONParser;
import com.dling61.calendarschedule.utils.SharedReference;
import com.dling61.calendarschedule.utils.Utils;
import com.dling61.calendarschedule.views.TitleBarView;

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
import android.widget.Toast;

public class FeedBackActivity extends BaseActivity implements OnClickListener {

	EditText edFeedback;
//	LinearLayout layout_back;
//	LinearLayout layout_send;
	Context mContext;
TitleBarView titleBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutpage);
		mContext = this;
		edFeedback = (EditText) findViewById(R.id.ed_feedback);
		titleBar=(TitleBarView)findViewById(R.id.titleBar);
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
//		Utils.hideKeyboard((Activity)mContext,edFeedback);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == titleBar.layout_back) {
			Utils.hideKeyboard(FeedBackActivity.this,edFeedback);
			
			finish();
//			Utils.postLeftToRight(mContext);
		
		} else if (v == titleBar.layout_save) {
			Utils.hideKeyboard((Activity)mContext,edFeedback);
			String feedback = edFeedback.getText().toString().trim();
			if (feedback != null && (!feedback.equals(""))) {
				
				new FeedbackTask(mContext, feedback).execute();
			} else {
				Toast.makeText(
						FeedBackActivity.this,
						getResources()
								.getString(R.string.please_enter_feedback),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	// edit information
	class FeedbackTask extends AsyncTask<String, Void, String> {

		ProgressDialog dialog;
		Context mContext;

		String feedback = "";

		public FeedbackTask(Context mContext, String feedback) {
			this.mContext = mContext;
			dialog = ProgressDialog.show(mContext, "", mContext.getResources()
					.getString(R.string.processing), true);
			this.feedback = feedback;

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
				JSONObject sharedmemberParams = new JSONObject();
				sharedmemberParams.put("ownerid",
						new SharedReference().getCurrentOwnerId(mContext));
				sharedmemberParams.put("feedback", feedback);
				
				Log.d("param",sharedmemberParams.toString());
				String feedbackLink = BaseUrl.BASEURL + "feedback" + "?"
						+ BaseUrl.URL_POST_FIX;
				return JSONParser.getJsonFromURLPostNameValuePair(feedbackLink,sharedmemberParams.toString());
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
										R.string.send_feedback_success),
								Toast.LENGTH_LONG).show();
					} else if (result.contains("201")) {
						Toast.makeText(mContext,
								"Not valid content in the feedback",
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
			Utils.postLeftToRight(mContext);
		}
	}

}
