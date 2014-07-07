package com.e2wstudy.cschedule.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.e2wstudy.cschedule.AddNewActivity;
import com.e2wstudy.cschedule.R;
import com.e2wstudy.cschedule.db.DatabaseHelper;
import com.e2wstudy.cschedule.models.MyActivity;
import com.e2wstudy.cschedule.models.Sharedmember;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * @author khoahuyen
 * @class ActivityAdapter
 * {@code create adapter for activity}
 * */
public class ActivityAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	public List<MyActivity> mItems = new ArrayList<MyActivity>();
	Context mContext;

	HashMap<String, ArrayList<Sharedmember>> listParticipant = new HashMap<String, ArrayList<Sharedmember>>();
	HashMap<String, String> listParticipantName = new HashMap<String, String>();

	public ActivityAdapter(Context context, List<MyActivity> items) {
		// HERE WE CACHE THE INFLATOR FOR EFFICIENCY
		mInflater = LayoutInflater.from(context);
		mItems = items;
		mContext = context;
		DatabaseHelper db = DatabaseHelper.getSharedDatabaseHelper(mContext);
		// get all participant of activity
		for (MyActivity activity : mItems) {
			ArrayList<Sharedmember> arrParticipant = db
					.getParticipantsOfActivity(activity.getActivity_ID());
			listParticipant.put(activity.getActivity_ID(), arrParticipant);
			listParticipantName.put(activity.getActivity_ID(),
					Utils.getStringNameArrParticipant(arrParticipant));
		}
	}

	public void setItems(List<MyActivity> items) {
		this.mItems = items;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ActivityViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.activitycell, null);
			viewHolder = new ActivityViewHolder();
			viewHolder.service_name_tv = (TextView) convertView
					.findViewById(R.id.service_name_tv);
			viewHolder.service_time = (TextView) convertView
					.findViewById(R.id.service_time_tv);
			viewHolder.service_desp = (TextView) convertView
					.findViewById(R.id.service_descrpition_tv);
			viewHolder.tv_participant = (TextView) convertView
					.findViewById(R.id.tv_participant);
			viewHolder.service_name_tv.setTypeface(Utils.getTypeFace(mContext));
			viewHolder.service_time.setTypeface(Utils.getTypeFace(mContext));
			viewHolder.service_desp.setTypeface(Utils.getTypeFace(mContext));
			viewHolder.tv_participant.setTypeface(Utils.getTypeFace(mContext));
			viewHolder.service_time.setVisibility(View.GONE);
			viewHolder.service_desp.setVisibility(View.GONE);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ActivityViewHolder) convertView.getTag();
		}

		final MyActivity activity = mItems.get(position);
		viewHolder.service_name_tv.setText(activity.getActivity_name());
//		String startWeekday = MyDate.getWeekdayFromUTCTime(activity
//				.getStarttime());
//		String startDate = MyDate.transformUTCTimeToCustomStyle(activity
//				.getStarttime());
//		String startTime = startWeekday + "," + " " + startDate;
//		String endWeekday = MyDate.getWeekdayFromUTCTime(activity.getEndtime());
//		String endDate = MyDate.transformUTCTimeToCustomStyle(activity
//				.getEndtime());
//		String endTime = endWeekday + "," + " " + endDate;
//		viewHolder.service_time.setText(startTime + " to " + endTime);
//		viewHolder.service_desp.setText(activity.getDesp());
		
		
		viewHolder.tv_participant.setText(listParticipantName.get(activity
				.getActivity_ID()));

	
		final int role=activity.getRole();
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent inforActivityIntent = new Intent(mContext,
						AddNewActivity.class);
				inforActivityIntent.putExtra(CommConstant.TYPE,
						DatabaseHelper.EXISTED);
				inforActivityIntent.putExtra(CommConstant.ACTIVITY_ID,
						activity.getActivity_ID());
				inforActivityIntent.putExtra(CommConstant.ROLE,role);
				mContext.startActivity(inforActivityIntent);
				Utils.pushRightToLeft(mContext);

			}
		});

		return convertView;
	}

	static class ActivityViewHolder {
		TextView service_name_tv;
		TextView service_time;
		TextView service_desp;
		TextView tv_participant;
	}

}
