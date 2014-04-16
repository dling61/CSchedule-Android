package com.dling61.calendarschedule.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.dling61.calendarschedule.adapter.ParticipantAdapter;
import com.dling61.calendarschedule.models.Participant;

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

	/** return string name of array participant */
	public static String getStringNameArrParticipant(
			ArrayList<Participant> arrParticipant) {
		String arrParticipantString = "";
		for (Participant participant : arrParticipant) {
			arrParticipantString += participant.getName() + ",";
		}
		if (arrParticipantString.endsWith(",")) {
			arrParticipantString
					.substring(0, arrParticipantString.length() - 2);
		}
		return arrParticipantString;
	}
	
	/***Set full height for listview*/
	public static void setListViewHeightBasedOnChildren(ListView listView, BaseAdapter listAdapter) {
	   
	    if (listAdapter == null)
	        return;

	    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
	    int totalHeight = 0;
	    View view = null;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        view = listAdapter.getView(i, view, listView);
	        if (i == 0)
	            view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

	        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	        totalHeight += view.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	    listView.requestLayout();
	}
}
