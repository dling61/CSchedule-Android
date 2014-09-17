package com.e2w.cschedule.interfaces;

public interface SignUpInterface {
	public void onComplete(String username, String pass);
	public void onFailure(String error);
}
