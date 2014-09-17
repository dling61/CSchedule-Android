package com.e2w.cschedule.utils;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.e2w.cschedule.R;

import android.content.Context;

public class ErrorRequest {

	public interface ExceptionType {
		String UNKNOWN_HOST_EXCEPTION = "UnknownHostException";
		String TIMEOUT_EXCEPTION = "TimeoutException";
	}

	public static String[] getErrorMessage(Context context, byte[] response,
			Throwable ex) {
		String errorTitle = "";
		String errorMsg = "";
		String errorTemp = "";
		if (response != null) {
//			errorTitle = context.getResources().getString(
//					R.string.title_something_wrong);
			try {
				errorTemp = new String(response, "UTF-8");
				JSONObject jsonObj = new JSONObject(errorTemp);
				if (jsonObj.has("error")) {
					errorMsg = jsonObj.optString("error");
				} else if (jsonObj.has("message")) {
					errorMsg = jsonObj.optString("message");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try
			{
			errorTemp = ex.getMessage();
			if (errorTemp.contains(ExceptionType.UNKNOWN_HOST_EXCEPTION)) {
				errorTitle = context.getResources().getString(
						R.string.title_lost_connection);
				errorMsg = context.getResources().getString(
						R.string.no_network);
			} else if (errorTemp.contains(ExceptionType.TIMEOUT_EXCEPTION)) {
				errorTitle = context.getResources().getString(
						R.string.title_timetout);
				errorMsg = context.getResources().getString(
						R.string.message_timeout);
			}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return new String[] { errorTitle, errorMsg };
	}

}
