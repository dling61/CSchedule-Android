/**
 * Develop by Antking
 * */
package com.e2w.cschedule.net;

/**
 * Class name: BaseUrl Author: Huyen Nguyen Date: April 8th, 2014 This class
 * define common variables for download JSON web-services
 * */
public class BaseUrl {
	// public static final String BASEURL =
	// "http://apitest2.servicescheduler.net/";
	// public static final String BASEURL =
	// "http://apitest1.servicescheduler.net/";
	public static final String BASEURL = "http://api.servicescheduler.net/";
	public static final String DEVICE = "ANDROID";
	// public static final String SCODE = "28e336ac6c9423d946ba02d19c6a2633";
	public static final String SCODE = "28e336ac6c9423d946cd02d19c6a2633";
	public static final String VERSION = "1.2.2";
	public static final String URL_POST_FIX = "d=" + DEVICE + "&" + "sc="
			+ SCODE + "&" + "v=" + VERSION;
}
