package com.dling61.calendarschedule.adapter;

import java.util.ArrayList;
import java.util.List;

import com.dling61.calendarschedule.R;
import com.dling61.calendarschedule.models.Participant;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ParticipantAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<Participant> participants;
	private Typeface thinface;
	private Typeface regularface;
	private Typeface lightface;
	Context mContext;
	public ParticipantAdapter(Context context, ArrayList<Participant> participants)
	{
		mInflater = LayoutInflater.from(context);
		mContext=context;
		this.participants = participants;
	}
	
	public void setParticipants (ArrayList<Participant> participants)
	{
		this.participants = participants;
	}
	
	public void setThinface (Typeface thinface)
	{
		this.thinface = thinface;
	}
	
	public void setRegularface(Typeface regularface)
	{
		this.regularface = regularface;
	}
	
	public void setLightface(Typeface lightface)
	{
		this.lightface = lightface;
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return participants.size();
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		
		ParticipantViewHolder viewHolder;
//		Typeface thinface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
//		Typeface thinface = Typeface.createFromFile("/assets/fonts/Roboto-Thin.ttf");
//		Typeface regularface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.participantcell, null);
			viewHolder = new ParticipantViewHolder();
			viewHolder.name_tv = (TextView)convertView.findViewById(R.id.participant_name_tv);
			viewHolder.email_tv = (TextView)convertView.findViewById(R.id.participant_email_tv);
			viewHolder.mobile_tv = (TextView)convertView.findViewById(R.id.participant_mobile_tv);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ParticipantViewHolder) convertView.getTag();
		}
		Participant participant = participants.get(position);
		viewHolder.name_tv.setText(participant.getName());
		viewHolder.name_tv.setTypeface(lightface);
		viewHolder.email_tv.setText(participant.getEmail());
		viewHolder.email_tv.setTypeface(thinface);
		viewHolder.mobile_tv.setText(participant.getMobile());
		viewHolder.mobile_tv.setTypeface(thinface);
		return convertView;
	}
	
	static class ParticipantViewHolder
	{
		TextView name_tv;
		TextView email_tv;
		TextView mobile_tv;
	}

}
