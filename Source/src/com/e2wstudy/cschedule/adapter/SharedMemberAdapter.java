package com.e2wstudy.cschedule.adapter;

import java.util.ArrayList;

import com.e2wstudy.cschedule.R;
import com.e2wstudy.cschedule.models.Sharedmember;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SharedMemberAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public ArrayList<Sharedmember> sharedMembers;

	Context mContext;
	boolean isCheck = false;
	boolean isShowFull = false;// show full information or breif
	boolean isAttachRoleName = false;

	public SharedMemberAdapter(Context context,
			ArrayList<Sharedmember> participants, boolean isCheck,
			boolean isShowFull, boolean isAttachRoleName) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.sharedMembers = participants;
		this.isCheck = isCheck;
		this.isShowFull = isShowFull;
		this.isAttachRoleName = isAttachRoleName;

	}

	public void setParticipants(ArrayList<Sharedmember> participants) {
		this.sharedMembers = participants;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sharedMembers.size();
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

		ParticipantViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.participantcell, null);
			viewHolder = new ParticipantViewHolder();
			viewHolder.name_tv = (TextView) convertView
					.findViewById(R.id.participant_name_tv);
			viewHolder.email_tv = (TextView) convertView
					.findViewById(R.id.participant_email_tv);
			viewHolder.mobile_tv = (TextView) convertView
					.findViewById(R.id.participant_mobile_tv);
			viewHolder.cb_check = (ImageView) convertView
					.findViewById(R.id.cb_check);
//			viewHolder.name_tv.setTypeface(Utils.getTypeFace(mContext));
			viewHolder.email_tv.setTypeface(Utils.getTypeFace(mContext));
			viewHolder.mobile_tv.setTypeface(Utils.getTypeFace(mContext));
			viewHolder.tv_role = (TextView) convertView
					.findViewById(R.id.tv_role);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ParticipantViewHolder) convertView.getTag();
		}

		final Sharedmember participant = sharedMembers.get(position);
		if (isCheck) {
			viewHolder.cb_check.setVisibility(View.VISIBLE);

			if (participant.isChecked) {

				viewHolder.cb_check
						.setImageResource(R.drawable.check_box_selected);

			} else {
				viewHolder.cb_check
						.setImageResource(R.drawable.check_box_unselected);
			}

		} else {
			viewHolder.cb_check.setVisibility(View.GONE);
		}

		String participant_name = participant.getName();
		String role="";
		if (isAttachRoleName) {
			if (participant.getRole() != CommConstant.PARTICIPANT) {
				role="("+getRole(participant.getRole())+")";
				
//				role+="Partipant";
//				viewHolder.tv_role.setText("(" + getRole(participant.getRole())
//						+ ")");
//				viewHolder.tv_role.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tv_role.setVisibility(View.GONE);
			}
		}
		viewHolder.name_tv.setText(getStringWithRole(participant_name, role));

		if (isShowFull) {
			viewHolder.email_tv.setText(participant.getEmail());

			viewHolder.mobile_tv.setText(participant.getMobile());

			viewHolder.email_tv.setVisibility(View.VISIBLE);
			viewHolder.mobile_tv.setVisibility(View.VISIBLE);
		} else {
			viewHolder.email_tv.setVisibility(View.GONE);
			viewHolder.mobile_tv.setVisibility(View.GONE);
		}
		final ParticipantViewHolder view = viewHolder;
		// convertView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (isCheck) {
		// participant.isChecked = !participant.isChecked;
		// participants.add(position, participant);
		// view.cb_check.setChecked(participant.isChecked);
		// }
		// if(isShowFull){
		// Intent inforActivityIntent = new Intent(mContext,
		// AddNewContactActivity.class);
		// inforActivityIntent.putExtra(CommConstant.TYPE,
		// DatabaseHelper.EXISTED);
		// inforActivityIntent.putExtra(CommConstant.CONTACT_ID,
		// participant.getID());
		// mContext.startActivity(inforActivityIntent);
		// }
		// }
		// });
		return convertView;
	}

	public String getRole(int role) {
		switch (role) {
		case 0:
			return "Creator";

		case 1:
			return "Organizer";

		default:
			return "";
		}
	}

	static class ParticipantViewHolder {
		TextView name_tv;
		TextView email_tv;
		TextView mobile_tv;
		ImageView cb_check;
		TextView tv_role;
	}
	
	public SpannableString getStringWithRole(String str, String role)
	{
		
        // this is the text we'll be operating on  
        SpannableString title = new SpannableString(str+role);
//        title.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0 ,
//                str.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        // make "key" (characters 0 to key.length()) White Bold
//        title.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), str.length() ,
//         str.length() + role.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setSpan(new RelativeSizeSpan(0.7f), str.length() ,
                str.length() + role.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int from=str.length();
        int to=str.length()+role.length();
//        title.setSpan(new TypefaceSpan ("fonts/Roboto-Bold.ttf"), 0, from, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        title.setSpan(new TypefaceSpan ("fonts/Roboto-Regular.ttf"), from, to, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        SpannableStringBuilder ssb = new SpannableStringBuilder(title);
//     
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
//        StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
        title.setSpan(boldSpan,from,to, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ssb.setSpan(boldSpan2, from, to, 0);
        
 
         
        return title;
	}

}
