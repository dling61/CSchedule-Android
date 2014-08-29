package com.e2wstudy.cschedule.views;

import com.e2wstudy.cschedule.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Huyen
 * 
 */
public class ConfirmDialog extends Dialog {

	public Context mContext;
	public Dialog d;
	public Button btnOk;
	public Button btnCancel;
	String title = "";
	public TextView tvTitle;

	public ConfirmDialog(Context mContext, String title) {
		super(mContext);
		this.mContext = mContext;
		this.title = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.confirm_add_participant_dialog);
		setCanceledOnTouchOutside(false);
		setCancelable(false);
		btnOk = (Button) findViewById(R.id.btn_ok);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(title);
		this.dismiss();
	}
	
//	public static AlertDialog.Builder showMessageAlertOK(Context context,
//			String tittle, String message, String positiveButtonText,
//			DialogInterface.OnClickListener positiveButtonlistener) {
//
//		AlertDialog.Builder alertbox = new AlertDialog.Builder(
//				new ContextThemeWrapper(context,
//						com.fashory.R.style.Theme_DialogCustom));
//		alertbox.setTitle(tittle);
//		alertbox.setMessage(message);
//		alertbox.setCancelable(false);
//
//		alertbox.setPositiveButton(positiveButtonText, positiveButtonlistener);
//		alertbox.show();
//		return alertbox;
//	}

}