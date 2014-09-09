package com.e2wstudy.cschedule.net;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.e2wstudy.cschedule.models.MyActivity;
import com.e2wstudy.cschedule.utils.CommConstant;

public class ParamAddNewActivitty {

	public static StringEntity paramAddNewActivity(MyActivity activity) {
		
		JSONObject params = new JSONObject();
		StringEntity entity = null;
		try {
			JSONObject activityParams = new JSONObject();
			activityParams.put("servicename", activity.getActivity_name());
			activityParams.put("serviceid", activity.getActivity_ID());
			activityParams.put("desp", activity.getDesp());

	
			params.put("ownerid", activity.getOwner_ID());
			params.put("services", activityParams);
			Log.d("add activity", params.toString());

		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		try {
			entity = new StringEntity(params.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entity;
	}
}
