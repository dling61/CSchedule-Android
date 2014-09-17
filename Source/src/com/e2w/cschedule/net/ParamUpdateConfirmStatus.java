package com.e2w.cschedule.net;

import java.io.UnsupportedEncodingException;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class ParamUpdateConfirmStatus {

	public static StringEntity paramUpdateConfirmStatus(String ownerId,int confirm) {
		JSONObject params = new JSONObject();
		StringEntity entity = null;
		try {
			
			params.put("ownerid",
					ownerId);
			params.put("confirm",confirm);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		try {
			entity = new StringEntity(params.toString());
			Log.d("param update confirm status", params.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entity;
	}
}
