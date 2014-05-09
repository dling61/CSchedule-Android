package com.dling61.calendarschedule;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.dling61.calendarschedule.net.BaseUrl;
import com.dling61.calendarschedule.net.JSONParser;
import com.dling61.calendarschedule.net.WebservicesHelper;
import com.dling61.calendarschedule.utils.SharedReference;

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
import android.widget.LinearLayout;
import android.widget.Toast;

public class FeedBackActivity extends Activity implements OnClickListener {

	EditText edFeedback;
	LinearLayout layout_back;
	LinearLayout layout_send;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutpage);
		mContext = this;
		edFeedback = (EditText) findViewById(R.id.ed_feedback);
		layout_back = (LinearLayout) findViewById(R.id.layout_back);
		layout_send = (LinearLayout) findViewById(R.id.layout_send);

		onClickListener();
	}

	private void onClickListener() {
		layout_back.setOnClickListener(this);
		layout_send.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == layout_back) {
			finish();
		} else if (v == layout_send) {
			String feedback = edFeedback.getText().toString().trim();
			if (feedback != null && (!feedback.equals(""))) {
				// WebservicesHelper ws = new WebservicesHelper(
				// FeedBackActivity.this);
				// ws.sendFeedBack(feedback);
				// ArrayList<NameValuePair> params=new
				// ArrayList<NameValuePair>();
				// params.add(new BasicNameValuePair("ownerid",new
				// SharedReference().getCurrentOwnerId(FeedBackActivity.this)+""));
				// params.add(new BasicNameValuePair("feedback",feedback));
				// String feedbackLink = BaseUrl.BASEURL + "feedback"+ "?" +
				// BaseUrl.URL_POST_FIX;
				// JSONParser.getJsonFromURLPostNameValuePair(feedbackLink,
				// params);

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
				ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
				param.add(new BasicNameValuePair("ownerid",
						new SharedReference()
								.getCurrentOwnerId(FeedBackActivity.this) + ""));
				param.add(new BasicNameValuePair("feedback", feedback));
				String feedbackLink = BaseUrl.BASEURL + "feedback" + "?"
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
		}
	}

}
