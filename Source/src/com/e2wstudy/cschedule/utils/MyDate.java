package com.e2wstudy.cschedule.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import android.net.ParseException;

public class MyDate {

	public final static int STANDARD = 1;
	public final static int SIMPLE = 2;

	// public final static int WEEKDAY = 3;

	public static String transformUTCDateToLocalDate(int format, String UTCTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date value = null;
		try {
			value = formatter.parse(UTCTime);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = null;
		switch (format) {
		case STANDARD:
			dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			break;
		case SIMPLE:
			dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			break;
		}

		dateFormatter.setTimeZone(TimeZone.getDefault());
		String dt = dateFormatter.format(value);
		return dt;

	}

	public static String convertUTCDateToCustomTimezone(String tzName,int format, String UTCTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date value = null;
		try {
			value = formatter.parse(UTCTime);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = null;
		switch (format) {
		case STANDARD:
			dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			break;
		case SIMPLE:
			dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			break;
		}

		dateFormatter.setTimeZone(TimeZone.getTimeZone(tzName));
		String dt = dateFormatter.format(value);
		return dt;

	}
	
	
	public static String transformPhoneDateTimeToUTCFormat(String localDatetime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getDefault());
		Date value = null;
		try {
			value = formatter.parse(localDatetime);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utct = dateFormatter.format(value);
		return utct;
	}
	
	public static String transformLocalDateTimeToUTCFormat(String timeZone,String localDatetime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
		Date value = null;
		try {
			value = formatter.parse(localDatetime);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utct = dateFormatter.format(value);
		return utct;
	}
	
	public static String getCurrentDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
		Date d = new Date();
		return df.format(d);
	}
	

	public static String getCurrentDateTime(String timeZone) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		Date d = new Date();
		return df.format(d);
	}

	public static String transformCurrentDateUTCDateToLocalDate(int format) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date value = null;
		try {
			value = formatter.parse(getCurrentDateTime());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = null;
		switch (format) {
		case STANDARD:
			dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			break;
		case SIMPLE:
			dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			break;
		}

		dateFormatter.setTimeZone(TimeZone.getDefault());
		String dt = dateFormatter.format(value);
		return dt;

	}

	public static String getWeekdayFromUTCTime(String UTCTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date value = null;
		try {
			value = formatter.parse(UTCTime);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat("EE");
		dateFormatter.setTimeZone(TimeZone.getDefault());
		String dt = dateFormatter.format(value);
		// Log.i("weekday", dt);
		String dtEn = null;
		if (dt.equalsIgnoreCase("星期一") || dt.equalsIgnoreCase("Mon")) {
			dtEn = "Mon";
		} else if (dt.equalsIgnoreCase("星期二")
				|| dt.equalsIgnoreCase("Tue")) {
			dtEn = "Tue";
		} else if (dt.equalsIgnoreCase("星期三")
				|| dt.equalsIgnoreCase("Wed")) {
			dtEn = "Wed";
		} else if (dt.equalsIgnoreCase("星期四")
				|| dt.equalsIgnoreCase("Thu")) {
			dtEn = "Thu";
		} else if (dt.equalsIgnoreCase("星期五")
				|| dt.equalsIgnoreCase("Fri")) {
			dtEn = "Fri";
		} else if (dt.equalsIgnoreCase("星期六")
				|| dt.equalsIgnoreCase("Sat")) {
			dtEn = "Sat";
		} else if (dt.equalsIgnoreCase("星期日")
				|| dt.equalsIgnoreCase("Sun")) {
			dtEn = "Sun";
		}
		// Log.i("weekday", dtEn);
		return dtEn;
	}

	// transform UTC(yyyy-mm-dd HH:MM:SS) to local(month(En) + " " + day(Num) +
	// ", " + year(Num))
	// eg. 2013-08-02 19:18:17 to Aug 02, 2013
	public static String transformUTCTimeToCustomStyle(String UTCTime) {
		String simpledate = MyDate.transformUTCDateToLocalDate(MyDate.SIMPLE,
				UTCTime);
		String[] timecomponents = simpledate.split("-");
		String monthInt = timecomponents[1];
		String monthEn = null;
		if (monthInt.equalsIgnoreCase("01")) {
			monthEn = "Jan";
		} else if (monthInt.equalsIgnoreCase("02")) {
			monthEn = "Feb";
		} else if (monthInt.equalsIgnoreCase("03")) {
			monthEn = "Mar";
		} else if (monthInt.equalsIgnoreCase("04")) {
			monthEn = "Apr";
		} else if (monthInt.equalsIgnoreCase("05")) {
			monthEn = "May";
		} else if (monthInt.equalsIgnoreCase("06")) {
			monthEn = "Jun";
		} else if (monthInt.equalsIgnoreCase("07")) {
			monthEn = "July";
		} else if (monthInt.equalsIgnoreCase("08")) {
			monthEn = "Aug";
		} else if (monthInt.equalsIgnoreCase("09")) {
			monthEn = "Sep";
		} else if (monthInt.equalsIgnoreCase("10")) {
			monthEn = "Oct";
		} else if (monthInt.equalsIgnoreCase("11")) {
			monthEn = "Nov";
		} else if (monthInt.equalsIgnoreCase("12")) {
			monthEn = "Dec";
		}
		return monthEn + " " + timecomponents[2] + "," + " "
				+ timecomponents[0];
	}

	public static String getTimeWithAPMFromUTCTime(String UTCTime) {
		String datetime = MyDate.transformUTCDateToLocalDate(STANDARD, UTCTime);
		String[] parts = datetime.split(" ");
		String time = parts[1];
		String[] timecomponents = time.split(":");
		String hour = timecomponents[0];
		String minite = timecomponents[1];
		int hourint = Integer.valueOf(hour);
		if (hourint > 12) {
			return (hourint - 12) + ":" + minite + " PM";
		} else {
			return hour + ":" + minite + " AM";
		}
	}

	public static String getTimeFromTimeZone(String UTCTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date value = null;
		try {
			value = formatter.parse(UTCTime);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = null;
			dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dt = dateFormatter.format(value);
		String[] parts = dt.split(" ");
		String time = parts[1];
		String[] timecomponents = time.split(":");
		String hour = timecomponents[0];
		String minite = timecomponents[1];
		int hourint = Integer.valueOf(hour);
		if (hourint > 12) {
			return (hourint - 12) + ":" + minite + " PM";
		} else {
			return hour + ":" + minite + " AM";
		}
	}
	
	
	public static boolean IsFirstDateLaterThanSecondDate(String firstdate,
			String seconddate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			Date date1 = formatter.parse(firstdate);
			Date date2 = formatter.parse(seconddate);
			if (date1.before(date2))
				return false;
			else
				return true;
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * convert a datetime to a timezone
	 * */
	public static String getDateTime(String dateTime, String changeTimeZone) {
		SimpleDateFormat sourceFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date parsed;
		try {
			// => Date is in current time zone now
			parsed = sourceFormat.parse(dateTime);
			TimeZone tz = TimeZone.getTimeZone(changeTimeZone);
			SimpleDateFormat destFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			destFormat.setTimeZone(tz);

			return destFormat.format(parsed);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String transformToCustomUtc(String timeZone, int format,
			String UTCTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
		Date value = null;
		try {
			value = formatter.parse(UTCTime);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = null;
		switch (format) {
		case STANDARD:
			dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			break;
		case SIMPLE:
			dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			break;
		}

		dateFormatter.setTimeZone(TimeZone.getDefault());
		String dt = dateFormatter.format(value);
		return dt;

	}

	public static String getWeekdayTimeZone( String time) {
		String dateFormat = "EEE, MMM dd, yyyy";
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT_STANDARD);
		
		Date value = null;
		try {
			value = formatter.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = null;

		dateFormatter = new SimpleDateFormat(dateFormat);
		String dt = dateFormatter.format(value);
		return dt;

	}
	
	
	public static String getDateFromUTCToTimeZone(String date,String timeZone)
	{
		String dateFormat = "EEE, MMM dd, yyyy";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
		Date value = null;
		try {
			value = formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		formatter = new SimpleDateFormat("yyyy-MM-dd");		
		return formatter.format(value);
	}
	
	public static String getWeekdayUTCFromLocal(String timeZone, String time) {
		String dateFormat = "EEE, MMM dd, yyyy";
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT_STANDARD);
		formatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
		Date value = null;
		try {
			value = formatter.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = null;

		dateFormatter = new SimpleDateFormat(dateFormat);

		dateFormatter.setTimeZone(TimeZone.getTimeZone(timeZone));
		String dt = dateFormatter.format(value);
		return dt;

	}

	// get time from local time to another local time
	public static String getTimeFromLocalToLocalTime(String lastTime,
			String lastTimeZone, String currentTimeZone) {
		String utcTime = convertLocalTimeToUTCTime(lastTime, lastTimeZone);
		String datetime = convertUTCDateToLocalDate(currentTimeZone, STANDARD,
				utcTime);

		String[] parts = datetime.split(" ");
		String time = parts[1];
		String[] timecomponents = time.split(":");
		String hour = timecomponents[0];
		String minite = timecomponents[1];
		int hourint = Integer.valueOf(hour);
		if (hourint > 12) {
			return (hourint - 12) + ":" + minite + " PM";
		} else {
			return hour + ":" + minite + " AM";
		}
	}

	// get time from local time to another local time
	public static String getTimeFromUTCToLocalTime(String lastTime,
			String currentTimeZone) {

		String datetime = convertUTCDateToLocalDate(currentTimeZone, STANDARD,
				lastTime);

		String[] parts = datetime.split(" ");
		String time = parts[1];
		String[] timecomponents = time.split(":");
		String hour = timecomponents[0];
		String minite = timecomponents[1];
		int hourint = Integer.valueOf(hour);
		if (hourint > 12) {
			return (hourint - 12) + ":" + minite + " PM";
		} else {
			return hour + ":" + minite + " AM";
		}
	}
	
	// get time from local time to another local time
		public static String getTimeFromUTCToTimeZone(String lastTime,
				String currentTimeZone) {

			String datetime = convertUTCDateToLocalDate(currentTimeZone, STANDARD,
					lastTime);

			String[] parts = datetime.split(" ");
			String time = parts[1];
			String[] timecomponents = time.split(":");
			String hour = timecomponents[0];
			String minite = timecomponents[1];
			int hourint = Integer.valueOf(hour);
			if (hourint > 12) {
				return (hourint - 12) + ":" + minite;
			} else {
				return hour + ":" + minite;
			}
		}
		
		
		public static String getTimeFromTimeZoneToUTC(String lastTime,
				String currentTimeZone) {

			SimpleDateFormat formatter = new SimpleDateFormat(
					DATE_TIME_FORMAT_STANDARD);
			formatter.setTimeZone(TimeZone.getTimeZone(currentTimeZone));
			Date value = null;
			try {
				value = formatter.parse(lastTime);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SimpleDateFormat dateFormatter = null;			
			dateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT_STANDARD);

			dateFormatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
			String datetime = dateFormatter.format(value);
			return datetime;
//			String[] parts = datetime.split(" ");
//			String time = parts[1];
//			String[] timecomponents = time.split(":");
//			String hour = timecomponents[0];
//			String minite = timecomponents[1];
//			int hourint = Integer.valueOf(hour);
//			if (hourint > 12) {
//				return (hourint - 12) + ":" + minite;
//			} else {
//				return hour + ":" + minite;
//			}
		}
		
		

	public static final String DATE_TIME_FORMAT_STANDARD = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_TIME_FORMAT_SIMPLE = "yyyy-MM-dd";
	public static final String TIME_ZONE_UTC = "UTC";

	// convert local time to UTC time
	public static String convertLocalTimeToUTCTime(String localTime,
			String timeZone) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				DATE_TIME_FORMAT_STANDARD);
		formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
		Date value = null;
		try {
			value = formatter.parse(localTime);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				DATE_TIME_FORMAT_STANDARD);
		dateFormatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
		String utct = dateFormatter.format(value);
		return utct;
	}

	public static String convertUTCDateToLocalDate(String timeZone, int format,
			String UTCTime) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				DATE_TIME_FORMAT_STANDARD);
		formatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
		Date value = null;
		try {
			value = formatter.parse(UTCTime);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = null;
		switch (format) {
		case STANDARD:
			dateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT_STANDARD);
			break;
		case SIMPLE:
			dateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT_SIMPLE);
			break;
		}

		dateFormatter.setTimeZone(TimeZone.getTimeZone(timeZone));
		String dt = dateFormatter.format(value);
		return dt;
	}

	// convert UTC time to local time
	public static String convertUTCDateToLocalDateCustomStyle(String timeZone,
			int format, String UTCTime) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				DATE_TIME_FORMAT_STANDARD);
		formatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
		Date value = null;
		try {
			value = formatter.parse(UTCTime);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat dateFormatter = null;
		switch (format) {
		case STANDARD:
			dateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT_STANDARD);
			break;
		case SIMPLE:
			dateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT_SIMPLE);
			break;
		}

		dateFormatter.setTimeZone(TimeZone.getTimeZone(timeZone));
		String dt = dateFormatter.format(value);
		String[] timecomponents = dt.split("-");
		String monthInt = timecomponents[1];
		String monthEn = null;
		if (monthInt.equalsIgnoreCase("01")) {
			monthEn = "Jan";
		} else if (monthInt.equalsIgnoreCase("02")) {
			monthEn = "Feb";
		} else if (monthInt.equalsIgnoreCase("03")) {
			monthEn = "Mar";
		} else if (monthInt.equalsIgnoreCase("04")) {
			monthEn = "Apr";
		} else if (monthInt.equalsIgnoreCase("05")) {
			monthEn = "May";
		} else if (monthInt.equalsIgnoreCase("06")) {
			monthEn = "Jun";
		} else if (monthInt.equalsIgnoreCase("07")) {
			monthEn = "July";
		} else if (monthInt.equalsIgnoreCase("08")) {
			monthEn = "Aug";
		} else if (monthInt.equalsIgnoreCase("09")) {
			monthEn = "Sep";
		} else if (monthInt.equalsIgnoreCase("10")) {
			monthEn = "Oct";
		} else if (monthInt.equalsIgnoreCase("11")) {
			monthEn = "Nov";
		} else if (monthInt.equalsIgnoreCase("12")) {
			monthEn = "Dec";
		}
		return monthEn + " " + timecomponents[2] + "," + " "
				+ timecomponents[0];

	}

	public static String convertFromLocalTimeToLocalTime(int format,
			String lastTimeZone, String currentTimeZone, String lastDateTime) {
		String currentDateTime = "";
		String localToUtcTime = convertLocalTimeToUTCTime(lastDateTime,
				lastTimeZone);
		currentDateTime = convertUTCDateToLocalDate(currentTimeZone, format,
				localToUtcTime);
		return currentDateTime;
	}
}
