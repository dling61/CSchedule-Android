package com.e2wstudy.schedule.interfaces;

public interface SignUpInterface {
	public void onComplete(String username, String pass);
	public void onFailure(String error);
}
