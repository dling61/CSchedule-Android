package com.e2w.cschedule.net;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.e2w.cschedule.utils.CommConstant;

public class ParamSetToken {

	public static StringEntity paramSetToken( String userId,
			 String deviceId, String registrationId) {
		JSONObject jsonParams = new JSONObject();
		StringEntity entity = null;
		try {
			jsonParams.put("userid", userId);
			jsonParams.put("udid", deviceId);
			jsonParams.put("token", registrationId);
			Log.d("gcm param", jsonParams.toString());

		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		try {
			entity = new StringEntity(jsonParams.toString());			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entity;
	}
}
