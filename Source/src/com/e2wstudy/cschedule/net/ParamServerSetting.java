package com.e2wstudy.cschedule.net;

import android.util.Log;
import com.loopj.android.http.RequestParams;

public class ParamServerSetting {

	public static RequestParams paramServerSetting(String ownerId, String lastestParticipantLastModifiedTime) {
		RequestParams jsonParams = new RequestParams();
		try {

			RequestParams params = new RequestParams();
			params.put("ownerid",ownerId);
			params.put("lastupdatetime",
					lastestParticipantLastModifiedTime);

			Log.d("param server setting", jsonParams.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jsonParams;
	}
}
