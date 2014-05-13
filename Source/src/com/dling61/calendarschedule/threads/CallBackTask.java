package com.dling61.calendarschedule.threads;

import org.json.JSONObject;

public interface CallBackTask {
		public abstract void requestDidFail(JSONObject ei);
		public abstract void requestDidLoad(JSONObject ei);
}
