package com.dling61.calendarschedule.net;

import com.dling61.calendarschedule.models.MyActivity;

import android.util.Log;

public class WebservicesAPI {

	private JSONParser jsonParser;
	
	// constructor
	public WebservicesAPI() {
		jsonParser = new JSONParser();
	}

	/**
	 * Get detail of campaign by id
	 * */
	public String deleteActivity(MyActivity activity) {
		String ActivityUrl = BaseUrl.BASEURL + "services/"
				+ activity.getActivity_ID() + "?" + BaseUrl.URL_POST_FIX;

		String json = jsonParser.getJsonFromURL(ActivityUrl);
		Log.d("delete activity",json);
		return json;
	}


	
}
