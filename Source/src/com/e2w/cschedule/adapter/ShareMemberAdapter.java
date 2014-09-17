package com.e2w.cschedule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import com.e2w.cschedule.R;
import com.e2w.cschedule.models.Sharedmember;

import android.widget.BaseAdapter;

public class ShareMemberAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private List<Sharedmember> sharedmembers;
	private Context context;
	
	public void setSharedmembers (List<Sharedmember> sms)
	{
		this.sharedmembers = sms;
	}
	
	public ShareMemberAdapter (Context cxt, LayoutInflater li, List<Sharedmember> sms)
	{
		this.inflater = li;
		this.sharedmembers = sms;
		this.context = cxt;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		sharedMemberHolder holder;
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.sharemembercell, null);
			holder = new sharedMemberHolder();
			holder.sharedMember_role_imgbtn = (ImageButton) convertView.findViewById(R.id.sharedmember_imgbtn);
			holder.sharedMember_role_imgbtn.setTag(position);
			holder.sharedMember_name_tv = (TextView) convertView.findViewById(R.id.sharedmember_tv);
			convertView.setTag(holder);
		}
		else
		{
			holder = (sharedMemberHolder) convertView.getTag();
		}
		Sharedmember sm = sharedmembers.get(position);
		holder.sharedMember_name_tv.setText(sm.getName());
		switch (sm.getRole())
		{
			case 0:
				holder.sharedMember_role_imgbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.creator));
				break;
			case 1:
				holder.sharedMember_role_imgbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.organizer));
				break;
			case 2:
				holder.sharedMember_role_imgbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.participant));
				break;
			case 3:
				holder.sharedMember_role_imgbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.viewer));
				break;
			case 4:
				holder.sharedMember_role_imgbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.noshare));
				break;
		}
		
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sharedmembers.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	static class sharedMemberHolder
	{
		ImageButton sharedMember_role_imgbtn;
		TextView	sharedMember_name_tv;
	}
}
