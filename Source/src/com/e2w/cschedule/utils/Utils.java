package com.e2w.cschedule.utils;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.e2w.cschedule.R;
import com.e2w.cschedule.db.DatabaseHelper;
import com.e2w.cschedule.models.AppVersion;
import com.e2w.cschedule.models.Sharedmember;
import com.e2w.cschedule.views.ConfirmDialog;
import com.e2w.cschedule.views.UpdateDialog;

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
			arrParticipantString += participant.getName() + ", ";
		}
		if (arrParticipantString.endsWith(", ")) {
			arrParticipantString = arrParticipantString.substring(0,
					arrParticipantString.length() - 2);
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

	public static Typeface getTypeFaceBold(Context mContext) {
		return Typeface.createFromAsset(mContext.getAssets(),
				"fonts/Roboto-Bold.ttf");
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
	public static void hideKeyboard(Activity activity, EditText myEditText) {
		try {
			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
		} catch (Exception e) {
			// Ignore exceptions if any
			Log.e("KeyBoardUtil", e.toString(), e);
		}
	}

	/*
	 * // ping google check have internet connection public static void
	 * isNetworkAvailable(final Handler handler) {
	 * 
	 * // ask fo message '0' (not connected) or '1' (connected) on 'handler' //
	 * the answer must be send before before within the 'timeout' (in //
	 * milliseconds) final int timeout = 1000; new Thread() {
	 * 
	 * private boolean responded = false;
	 * 
	 * @Override public void run() {
	 * 
	 * // set 'responded' to TRUE if is able to connect with google // mobile
	 * (responds fast)
	 * 
	 * new Thread() {
	 * 
	 * @Override public void run() { HttpGet requestForTest = new HttpGet(
	 * "http://m.google.com"); try { new
	 * DefaultHttpClient().execute(requestForTest); // can // last... responded
	 * = true;
	 * 
	 * } catch (Exception e) {
	 * 
	 * } }
	 * 
	 * }.start();
	 * 
	 * try { int waited = 0; while (!responded && (waited < timeout)) {
	 * sleep(100); if (!responded) { waited += 100; } } } catch
	 * (InterruptedException e) { } // do nothing finally { if (!responded) {
	 * handler.sendEmptyMessage(0); } else { handler.sendEmptyMessage(1); } }
	 * 
	 * }
	 * 
	 * }.start();
	 * 
	 * }
	 */
	public static boolean isNetworkOnline(Context mContext) {

		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable() && cm
				.getActiveNetworkInfo().isConnected());
	}

	public static void pushRightToLeft(Context mContext) {
		((Activity) mContext).overridePendingTransition(R.anim.push_left_in,
				R.anim.push_left_out);
	}

	public static void postLeftToRight(Context mContext) {
		((Activity) mContext).overridePendingTransition(R.anim.animation_enter,
				R.anim.animation_leave);
	}

	public static void slideUpDown(Context mContext) {
		((Activity) mContext).overridePendingTransition(R.anim.in_from_bottom,
				R.anim.stand_by);
	}

	/**
	 * go to app on google play
	 * */
	public static void goToGooglePlay(Context mContext, String appName) {
		try {
			mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("market://details?id=" + appName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("http://play.google.com/store/apps/details?id="
							+ appName)));
		}
	}

	/**
	 * open keyboard
	 * */
	public static void openKeyboard(Context mContext, EditText ed) {
		InputMethodManager inputMethodManager = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInputFromWindow(
				ed.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED,
				0);
	}

	public static String getVersionName(Context mContext) {
		PackageInfo pInfo;
		try {
			pInfo = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void checkCurrentVersion(final Context mContext) {
		try {
			AppVersion appversion = DatabaseHelper.getSharedDatabaseHelper(
					mContext).getCurrentVersion();
			// check version
			if (appversion == null) {
				return;
			}
			String currentVersion = getVersionName(mContext).replace(".", "");
			String serverVersion = appversion.getAppversion().replace(".", "");
			long currentVer = Long.parseLong(currentVersion);
			long serverVer = Long.parseLong(serverVersion);
			if (currentVer < serverVer) {
				if (appversion.getEnforce() == 0) {
					if (!CommConstant.UPDATE) {
						CommConstant.UPDATE=true;
						final ConfirmDialog dialog = new ConfirmDialog(
								mContext, appversion.getMsg());
						dialog.show();
						dialog.btnCancel.setText("Don't update");
						dialog.btnOk.setText("Update");
						dialog.btnCancel
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										dialog.dismiss();
//										CommConstant.UPDATE=false;
									}
								});
						dialog.btnOk.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								// go to google play
								Utils.goToGooglePlay(
										mContext,
										mContext.getResources().getString(
												R.string.package_name));
//								CommConstant.UPDATE=false;
							}
						});
					}
				} else if (appversion.getEnforce() == 1) {
					if (!CommConstant.UPDATE) {
						CommConstant.UPDATE=true;
					final UpdateDialog dialog = new UpdateDialog(mContext);
					dialog.show();

					dialog.btnOk.setText("Update CSchedule");
					dialog.btnOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							CommConstant.UPDATE=false;
							dialog.dismiss();

							// go to google play
							Utils.goToGooglePlay(
									mContext,
									mContext.getResources().getString(
											R.string.package_name));

						}
					});
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	public static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Get device id
	 * */
	public static String getDeviceId(Context mContext) {
		try {
			String androidId = Secure.getString(mContext.getContentResolver(),
					Secure.ANDROID_ID);
			return androidId;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static String convertBytesArrayToString(byte[] responseBody) {
		try {
			return String.valueOf(new String(responseBody, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		
	}

}
