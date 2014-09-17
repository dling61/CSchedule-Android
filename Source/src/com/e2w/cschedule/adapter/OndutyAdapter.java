package com.e2w.cschedule.adapter;

import java.util.List;

import com.e2w.cschedule.R;
import com.e2w.cschedule.adapter.ParticipantAdapter.ParticipantViewHolder;
import com.e2w.cschedule.models.Sharedmember;
import com.e2w.cschedule.utils.Utils;
import com.e2w.cschedule.views.OnDutyView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class OndutyAdapter extends BaseAdapter {

	private List<Sharedmember> sharedmembers;
	private List<Integer> existedParticipants;
	private LayoutInflater inflater;
	Context mContext;

	public OndutyAdapter(Context context, List<Sharedmember> sms,
			List<Integer> existedParticipants) {
		this.sharedmembers = sms;
		this.existedParticipants = existedParticipants;
		inflater = LayoutInflater.from(context);
		mContext = context;
	}

	public void setExistedParticipants(List<Integer> existedParticipants) {
		this.existedParticipants = existedParticipants;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sharedmembers.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		OndutyViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.participantcell, null);
			viewHolder = new OndutyViewHolder();
			viewHolder.name_tv = (TextView) convertView
					.findViewById(R.id.participant_name_tv);
			viewHolder.email_tv = (TextView) convertView
					.findViewById(R.id.participant_email_tv);
			viewHolder.mobile_tv = (TextView) convertView
					.findViewById(R.id.participant_mobile_tv);
			viewHolder.cb_check = (ImageView) convertView
					.findViewById(R.id.cb_check);
			viewHolder.email_tv.setVisibility(View.GONE);
			viewHolder.mobile_tv.setVisibility(View.GONE);
			viewHolder.name_tv.setTypeface(Utils.getTypeFace(mContext));
			viewHolder.email_tv.setTypeface(Utils.getTypeFace(mContext));
			viewHolder.mobile_tv.setTypeface(Utils.getTypeFace(mContext));
			viewHolder.cb_check.setVisibility(View.VISIBLE);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (OndutyViewHolder) convertView.getTag();
		}

		final Sharedmember sm = sharedmembers.get(position);
		viewHolder.name_tv.setText(sm.getName());
		viewHolder.cb_check.setVisibility(View.VISIBLE);

		if (sm.isChecked) {
			viewHolder.cb_check.setImageResource(R.drawable.check_box_selected);
		} else {
			viewHolder.cb_check
					.setImageResource(R.drawable.check_box_unselected);
		}
		final OndutyViewHolder view=viewHolder;
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sm.isChecked = !sm.isChecked;
				sharedmembers.set(position, sm);
				if (sm.isChecked) {
					view.cb_check.setImageResource(R.drawable.check_box_selected);
				} else {
					view.cb_check
							.setImageResource(R.drawable.check_box_unselected);
				}
			}
		});
		return convertView;
	}

	static class OndutyViewHolder {
		TextView name_tv;
		TextView email_tv;
		TextView mobile_tv;
		ImageView cb_check;
	}
}
