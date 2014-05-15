package com.dling61.calendarschedule.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.models.Sharedmember;

public class Utils {

	public Utils() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Make a phone call
	 * */
	public static void makeAPhoneCall(Context mContext, String number) {
		String phoneNumber = "tel:" + number;
		Intent callIntent = new Intent(Intent.ACTION_CALL,
				Uri.parse(phoneNumber));
		mContext.startActivity(callIntent);
	}

	/**
	 * Send an email
	 * */
	public static void sendAnEmail(Context mContext, String emailAddress) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddress });
		i.putExtra(Intent.EXTRA_SUBJECT, "");
		i.putExtra(Intent.EXTRA_TEXT, "");
		try {
			mContext.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(mContext, "There are no email clients installed.",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Send a message
	 * */
	public static void sendAMessage(Context mContext, String number) {
		mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
				+ number)));
	}

	// public static boolean isEmailValid(String email) {
	// boolean isValid = false;
	//
	// String expression =
	// "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
	// CharSequence inputStr = email;
	//
	// Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	// Matcher matcher = pattern.matcher(inputStr);
	// if (matcher.matches()) {
	// isValid = true;
	// }
	// return isValid;
	// }

	public final static boolean isEmailValid(CharSequence target) {
		return !TextUtils.isEmpty(target)
				&& android.util.Patterns.EMAIL_ADDRESS.matcher(target)
						.matches();
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

	/** return string name of array participant */
	public static String getStringNameArrParticipant(
			ArrayList<Sharedmember> arrParticipant) {
		String arrParticipantString = "";
		for (Sharedmember participant : arrParticipant) {
			arrParticipantString += participant.getName() + ",";
		}
		if (arrParticipantString.endsWith(",")) {
			arrParticipantString = arrParticipantString.substring(0,
					arrParticipantString.length() - 1);
		}
		return arrParticipantString;
	}

	/*** Set full height for listview */
	public static void setListViewHeightBasedOnChildren(ListView listView,
			BaseAdapter listAdapter) {

		if (listAdapter == null)
			return;

		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, view, listView);
			if (i == 0)
				view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
						LayoutParams.WRAP_CONTENT));

			view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	/**
	 * Get normal typeface
	 * */
	public static Typeface getTypeFace(Context mContext) {
		return Typeface.createFromAsset(mContext.getAssets(),
				"fonts/Roboto-Regular.ttf");
	}

	/**
	 * return true if startdate>enddate else return false
	 * */
	public static boolean isStartGreaterEnddate(String startDate, String endDate) {
		Calendar c1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date start;
			start = sdf1.parse(startDate);
			Date end = sdf1.parse(endDate);

			c1.setTime(start);

			if (c1.getTime().before(end)) {
				return true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	// hide keyboard
	public static void hideKeyboard(Activity activity)
    {
        try
        {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
         }
        catch (Exception e)
        {
            // Ignore exceptions if any
                Log.e("KeyBoardUtil", e.toString(), e);
        }
    }
	
	//check network connection
	public static boolean isNetworkOnline(Context mContext) {
	    boolean status=false;
	    try{
	        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo netInfo = cm.getNetworkInfo(0);
	        if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
	            status= true;
	        }else {
	            netInfo = cm.getNetworkInfo(1);
	            if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
	                status= true;
	        }
	    }catch(Exception e){
	        e.printStackTrace();  
	        return false;
	    }
	    return status;

	    }  
}
