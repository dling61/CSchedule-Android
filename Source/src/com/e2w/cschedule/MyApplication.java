package com.e2w.cschedule;

import java.util.ArrayList;
import com.loopj.android.http.AsyncHttpClient;
import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application {

	Activity act;
	ArrayList<Activity> listAct = new ArrayList<Activity>();
	public static final String TAG = MyApplication.class.getSimpleName();
	private static MyApplication instance;
	private static AsyncHttpClient clientRequest;
	final static String USER_AGENT_STRING = "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> Mobile Safari/<WebKit Rev>";
	@Override
	public void onCreate() {
		super.onCreate();		
		instance = this;
	}

	public static MyApplication getInstance() {
		return instance;
	}

	public void add(Activity _act) {
		act = _act;
		listAct.add(act);
	}

	public void finishAll() {
		for (int i = 0; i < listAct.size(); i++) {
			if (!listAct.get(i).isFinishing())
				listAct.get(i).finish();
		}
	}

	public static AsyncHttpClient clientRequest() {
		if (clientRequest == null) {
			clientRequest = new AsyncHttpClient();
			clientRequest.setTimeout(10000);
			clientRequest.setMaxRetriesAndTimeout(1,10000);
			clientRequest.addHeader("Accept", "application/json");
//			clientRequest.addHeader("Content-type", "application/json");
			
			clientRequest.addHeader("User-Agent", USER_AGENT_STRING);
		}
		return clientRequest;
	}
}
