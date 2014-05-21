package com.dling61.calendarschedule.views;

import com.dling61.calendarschedule.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;


public class LoadingPopupViewHolder extends Dialog{

	private ImageView ivLoading;
	private TextView tvTitle;
	private Animation rotation;
	
	public LoadingPopupViewHolder(Context ctx, int theme) {
		super(ctx, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_loading);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		initUI();
	}

	private void initUI() {
		ivLoading = (ImageView) findViewById(R.id.ivLoading);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		
		rotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anim);
		rotation.setDuration(600);
		rotation.setInterpolator(new Interpolator() {
			private final int frameCount = 10;

			@Override
			public float getInterpolation(float input) {
				return (float) Math.floor(input * frameCount) / frameCount;
			}
		});
		ivLoading.startAnimation(rotation);
	}
	
	public void setTitle(String title) {
		tvTitle.setText(title);
	}
	
}
