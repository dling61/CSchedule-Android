package com.e2wstudy.cschedule.models;

public class AppVersion {
	int appId = -1;
	String appversion = "";
	int enforce = 0;
	String os = "";
	String osversion = "";
	String msg = "";

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getAppversion() {
		return appversion;
	}

	public void setAppversion(String appversion) {
		this.appversion = appversion;
	}

	public AppVersion(int appId, String appversion, int enforce, String os,
			String osversion, String msg) {
		super();
		this.appId = appId;
		this.appversion = appversion;
		this.enforce = enforce;
		this.os = os;
		this.osversion = osversion;
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getEnforce() {
		return enforce;
	}

	public void setEnforce(int enforce) {
		this.enforce = enforce;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsversion() {
		return osversion;
	}

	public void setOsversion(String osversion) {
		this.osversion = osversion;
	}
}
