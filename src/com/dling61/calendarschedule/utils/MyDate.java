package com.dling61.calendarschedule.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import android.net.ParseException;

public class MyDate {
	
	public final static int STANDARD = 1;
	public final static int SIMPLE = 2;
//	public final static int WEEKDAY = 3;
	
	public static String transformUTCDateToLocalDate (int format,String UTCTime)
	{
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
        switch (format)
        {
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
	
	public static String transformLocalDateTimeToUTCFormat(String localDatetime)
	{
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
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utct = dateFormatter.format(value);
        return utct;
	}
	
	public static String getCurrentDateTime()
	{
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date d = new Date();
	    return df.format(d);
	}
	
	public static String getWeekdayFromUTCTime (String UTCTime)
	{
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
//        Log.i("weekday", dt);
        String dtEn = null;
        if ( dt.equalsIgnoreCase("星期一") || dt.equalsIgnoreCase("Mon"))
        {
        	dtEn = "Mon";
        }
        else if (dt.equalsIgnoreCase("星期二") || dt.equalsIgnoreCase("Tue"))
        {
        	dtEn = "Tue";
        }
        else if (dt.equalsIgnoreCase("星期三") || dt.equalsIgnoreCase("Wed"))
        {
        	dtEn = "Wed";
        }
        else if (dt.equalsIgnoreCase("星期四") || dt.equalsIgnoreCase("Thu"))
        {
        	dtEn = "Thu";
        }
        else if (dt.equalsIgnoreCase("星期五") || dt.equalsIgnoreCase("Fri"))
        {
        	dtEn = "Fri";
        }
        else if (dt.equalsIgnoreCase("星期六") || dt.equalsIgnoreCase("Sat"))
        {
        	dtEn = "Sat";
        }
        else if (dt.equalsIgnoreCase("星期日") || dt.equalsIgnoreCase("Sun"))
        {
        	dtEn = "Sun";
        }
//        Log.i("weekday", dtEn);
        return dtEn;
	}
	
	
	//transform UTC(yyyy-mm-dd HH:MM:SS) to local(month(En) + " " + day(Num) + ", " + year(Num))
	//eg. 2013-08-02 19:18:17 to Aug 02, 2013
	public static String transformUTCTimeToCustomStyle(String UTCTime)
	{
		String simpledate = MyDate.transformUTCDateToLocalDate(MyDate.SIMPLE, UTCTime);
		String[] timecomponents = simpledate.split("-");
		String monthInt = timecomponents[1];
		String monthEn = null;
		if (monthInt.equalsIgnoreCase("01"))
		{
			monthEn = "Jan";
		}
		else if (monthInt.equalsIgnoreCase("02"))
		{
			monthEn = "Feb";
		}
		else if (monthInt.equalsIgnoreCase("03"))
		{
			monthEn = "Mar";
		}
		else if (monthInt.equalsIgnoreCase("04"))
		{
			monthEn = "Apr";
		}
		else if (monthInt.equalsIgnoreCase("05"))
		{
			monthEn = "Feb";
		}
		else if (monthInt.equalsIgnoreCase("05"))
		{
			monthEn = "May";
		}
		else if (monthInt.equalsIgnoreCase("06"))
		{
			monthEn = "Jun";
		}
		else if (monthInt.equalsIgnoreCase("07"))
		{
			monthEn = "July";
		}
		else if (monthInt.equalsIgnoreCase("08"))
		{
			monthEn = "Aug";
		}
		else if (monthInt.equalsIgnoreCase("09"))
		{
			monthEn = "Sep";
		}
		else if (monthInt.equalsIgnoreCase("10"))
		{
			monthEn = "Oct";
		}
		else if (monthInt.equalsIgnoreCase("11"))
		{
			monthEn = "Nov";
		}
		else if (monthInt.equalsIgnoreCase("12"))
		{
			monthEn = "Dec";
		}
		return monthEn + " " + timecomponents[2] + "," +" " + timecomponents[0];
	}
	
	public static String getTimeWithAPMFromUTCTime(String UTCTime)
	{
		String datetime = MyDate.transformUTCDateToLocalDate(STANDARD, UTCTime);
		String[] parts = datetime.split(" ");
		String time = parts[1];
		String[] timecomponents = time.split(":");
		String hour = timecomponents[0];
		String minite = timecomponents[1];
		int hourint = Integer.valueOf(hour);
		if (hourint > 12)
		{
			return (hourint - 12) + ":" + minite + " PM";
		}
		else
		{
			return hour + ":" + minite + " AM";
		}
	}
	
	public static boolean IsFirstDateLaterThanSecondDate (String firstdate, String seconddate)
	{
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

}
