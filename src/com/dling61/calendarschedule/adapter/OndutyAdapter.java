package com.dling61.calendarschedule.adapter;

import java.util.List;

import com.dling61.calendarschedule.R;
import com.dling61.calendarschedule.models.Sharedmember;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class OndutyAdapter extends BaseAdapter {
	
	private List<Sharedmember> sharedmembers;
	private List<Integer> existedParticipants;
	private LayoutInflater inflater;
	
	public OndutyAdapter (Context context,List<Sharedmember> sms,List<Integer> existedParticipants)
	{
		this.sharedmembers = sms;
		this.existedParticipants = existedParticipants;
		inflater = LayoutInflater.from(context);
	}
	
	public void setExistedParticipants(List<Integer> existedParticipants)
	{
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ondutyHolder holder;
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.ondutycell, null);
			holder = new ondutyHolder();
			holder.checkbox_imgbtn = (ImageButton) convertView.findViewById(R.id.onduty_imgbtn);
			holder.name_tv = (TextView) convertView.findViewById(R.id.onduty_tv);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ondutyHolder) convertView.getTag();
		}
		
		Sharedmember sm = sharedmembers.get(position);
		holder.name_tv.setText(sm.getName());
		holder.checkbox_imgbtn.setTag(sm.getID());
		if (this.existedParticipants.contains(sm.getID()))
		{
			holder.checkbox_imgbtn.setImageResource(R.drawable.checked);
		}
		else
		{
			holder.checkbox_imgbtn.setImageResource(R.drawable.unchecked);
		}
		
		return convertView;
	}
	
	static class ondutyHolder
	{
		ImageButton checkbox_imgbtn;
		TextView	name_tv;
	}
}
