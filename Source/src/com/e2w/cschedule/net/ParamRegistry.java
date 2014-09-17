package com.e2w.cschedule.net;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.e2w.cschedule.utils.CommConstant;

public class ParamRegistry {

	public static StringEntity paramSignUp(String email, String passWord,
			String userName, String mobile) {
		JSONObject jsonParams = new JSONObject();
		StringEntity entity = null;
		try {
			jsonParams.put(CommConstant.EMAIL, email);
			jsonParams.put(CommConstant.PASSWORD, passWord);
			jsonParams.put(CommConstant.USERNAME, userName);
			jsonParams.put(CommConstant.MOBILE, mobile);
			
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		try {
			entity=new StringEntity(jsonParams.toString());
			Log.d("param registry",jsonParams.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entity;
	}

}
