package com.e2w.cschedule.net;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.e2w.cschedule.models.Confirm;
import com.e2w.cschedule.models.Schedule;

public class ParamAddNewSchedule {

	public static StringEntity paramAddNewSchedule(Schedule schedule,List<Confirm> pins) {
		JSONObject params = new JSONObject();
		StringEntity entity = null;
		try {
			JSONObject scheduleParams = new JSONObject();
			scheduleParams.put("scheduleid", schedule.getSchedule_ID());
			scheduleParams.put("desp", schedule.getDesp());
			scheduleParams.put("startdatetime", schedule.getStarttime());
			scheduleParams.put("enddatetime", schedule.getEndtime());
			scheduleParams.put("tzid", schedule.getTzid());
			scheduleParams.put("alert", schedule.getAlert());
			
			params.put("ownerid", schedule.getOwner_ID());
			params.put("serviceid", schedule.getService_ID());
			JSONArray jpins = new JSONArray();
			if (pins != null && pins.size() > 0) {
				for (Confirm pin : pins) {
					JSONObject obj = new JSONObject();
					obj.put("memberid", pin.getMemberId());
					obj.put("confirm", pin.getConfirm());
					jpins.put(obj);
				}
			}
			if (jpins != null && jpins.length() > 0) {
				scheduleParams.put("members", jpins);
			}
			params.put("schedules", scheduleParams);			
			Log.d("add schedule", params.toString());

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
