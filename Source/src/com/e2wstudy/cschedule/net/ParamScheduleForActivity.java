package com.e2wstudy.cschedule.net;

import android.util.Log;
import com.loopj.android.http.RequestParams;

public class ParamScheduleForActivity {

	public static RequestParams paramScheduleForActivity(String ownerId, String lastestScheduleLastModifiedTime) {
		RequestParams jsonParams = new RequestParams();
		try {
			jsonParams.put("ownerid",ownerId);
			jsonParams.put("lastupdatetime",
				lastestScheduleLastModifiedTime);
			Log.d("param ScheduleForActivity", jsonParams.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jsonParams;
	}
}
