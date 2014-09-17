package com.e2w.cschedule.net;

import android.util.Log;
import com.loopj.android.http.RequestParams;

public class ParamContact {

	public static RequestParams paramContact(String ownerId, String lastestParticipantLastModifiedTime) {
		RequestParams params = new RequestParams();
		try {
			params.put("ownerid",ownerId);
			params.put("lastupdatetime",
					lastestParticipantLastModifiedTime);

			Log.d("param contact", params.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return params;
	}
}
