package com.dling61.calendarschedule.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	public Utils() {
		// TODO Auto-generated constructor stub
	}

	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	// Check whether the mobile number is valid
	public static boolean isMobileValid(String mobile) {
		boolean isValid = false;

		String regex = "^\\+?[0-9. ()-]{10,25}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(mobile);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

}
