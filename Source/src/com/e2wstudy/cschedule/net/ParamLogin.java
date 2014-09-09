package com.e2wstudy.cschedule.net;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.e2wstudy.cschedule.utils.CommConstant;

public class ParamLogin {

	public static StringEntity paramLogin(String email, String passWord) {
		JSONObject jsonParams = new JSONObject();
		StringEntity entity = null;
		try {
			jsonParams.put(CommConstant.PASSWORD, passWord);
			jsonParams.put(CommConstant.EMAIL, email);

		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		try {
			entity = new StringEntity(jsonParams.toString());
			Log.d("param login", jsonParams.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entity;
	}
}
