package com.e2wstudy.cschedule.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.devsmart.android.ui.HorizontalListView;
import com.e2wstudy.cschedule.CategoryTabActivity;
import com.e2wstudy.cschedule.CreateNewScheduleActivity;
import com.e2wstudy.cschedule.R;
import com.e2wstudy.cschedule.db.DatabaseHelper;
import com.e2wstudy.cschedule.models.Confirm;
import com.e2wstudy.cschedule.models.MyActivity;
import com.e2wstudy.cschedule.models.Schedule;
import com.e2wstudy.cschedule.models.Sharedmember;
import com.e2wstudy.cschedule.net.WebservicesHelper;
import com.e2wstudy.cschedule.utils.CommConstant;
import com.e2wstudy.cschedule.utils.MyDate;
import com.e2wstudy.cschedule.utils.SharedReference;
import com.e2wstudy.cschedule.utils.Utils;
import com.e2wstudy.cschedule.views.DutyScheduleView;
import com.e2wstudy.cschedule.views.LoadingPopupViewHolder;
import com.e2wstudy.cschedule.views.ParticipantInforDialog;
import com.e2wstudy.schedule.interfaces.LoadingInterface;
import com.e2wstudy.schedule.interfaces.UpdateConfirmStatusInterface;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ExpandableListScheduleAdapter extends BaseExpandableListAdapter {

	private Context context;
	public Map<String, ArrayList<Schedule>> scheduleCollection;
	public ArrayList<String> listSchedulesByDay;
	private LayoutInflater mInflater;
	// DatabaseHelper dbHelper;
	boolean isToday;
	Date nearestDate;// date nearest current date
	public int group_position_scrolled = 0;// scroll to nearest date
	public static LoadingPopupViewHolder loadingPopup;
	UpdateConfirmStatusInterface iConfirm;
	LoadingInterface loadingInterface;

	public ExpandableListScheduleAdapter() {

	}

	public ExpandableListScheduleAdapter(Context context,
			ArrayList<String> listSchedulesByDay,
			Map<String, ArrayList<Schedule>> scheduleCollection,
			UpdateConfirmStatusInterface iConfirm,
			LoadingInterface loadingInterface) {
		this.context = context;
		this.scheduleCollection = scheduleCollection;
		this.listSchedulesByDay = listSchedulesByDay;

		mInflater = LayoutInflater.from(context);
		this.iConfirm = iConfirm;
		this.loadingInterface = loadingInterface;
	}

	public void refreshData() {

	}

	public void setNearestDate(Date nearestDate) {
		this.nearestDate = nearestDate;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return scheduleCollection.get(listSchedulesByDay.get(groupPosition))
				.get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ScheduleViewHolder viewHolder;
		if ((convertView == null)) {
			viewHolder = new ScheduleViewHolder();
			convertView = mInflater.inflate(R.layout.schedulecell, null);
			viewHolder.service_TV = (TextView) convertView
					.findViewById(R.id.schedule_servicename_tv);
			viewHolder.time_TV = (TextView) convertView
					.findViewById(R.id.schedule_date_tv);
			viewHolder.participants_TV = (TextView) convertView
					.findViewById(R.id.schedule_participants_tv);
			viewHolder.listview = (HorizontalListView) convertView
					.findViewById(R.id.listview);
			// viewHolder.listview=(ListView)convertView.findViewById(R.id.listview);
			viewHolder.service_TV.setTypeface(Utils.getTypeFace(context));
			viewHolder.time_TV.setTypeface(Utils.getTypeFace(context));
			viewHolder.participants_TV.setTypeface(Utils.getTypeFace(context));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ScheduleViewHolder) convertView.getTag();
		}

		final Schedule schedule = (Schedule) getChild(groupPosition,
				childPosition);
		if (schedule != null) {
			MyActivity activity =
			DatabaseHelper.getSharedDatabaseHelper(context)
					.getActivity(schedule.getService_ID());
			String activity_name = activity != null ? activity
					.getActivity_name() : "";
			String date = MyDate.getTimeWithAPMFromUTCTime(schedule
					.getStarttime())
					+ " to "
					+ MyDate.getTimeWithAPMFromUTCTime(schedule.getEndtime());

			viewHolder.service_TV.setText(activity_name);
			viewHolder.time_TV.setText(date.toLowerCase());

			Log.d("activityname", activity.getActivity_name());
			List<Confirm> memberids = DatabaseHelper.getSharedDatabaseHelper(context)
						.getParticipantsForSchedule(schedule.getSchedule_ID());
		
			if (memberids != null && memberids.size() > 0) {

				Log.d("memberid", memberids.toString());
				OnDutyMemberAdapter adapter = new OnDutyMemberAdapter(schedule,
						memberids, schedule.getService_ID()
						);
				viewHolder.listview.setAdapter(adapter);
				// viewHolder.listview.setVisibility(View.VISIBLE);

			} else {
				OnDutyMemberAdapter adapter = new OnDutyMemberAdapter(schedule,
						null, schedule.getService_ID());
				viewHolder.listview.setAdapter(adapter);
			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent inforActivityIntent = new Intent(context,
							CreateNewScheduleActivity.class);
					inforActivityIntent.putExtra(CommConstant.TYPE,
							DatabaseHelper.EXISTED);
					inforActivityIntent.putExtra(CommConstant.SCHEDULE_ID,
							schedule.getSchedule_ID());
					inforActivityIntent.putExtra(CommConstant.ACTIVITY_ID,
							schedule.getService_ID());
					inforActivityIntent.putExtra(CommConstant.CREATOR,
							schedule.getOwner_ID());
					context.startActivity(inforActivityIntent);
					Utils.pushRightToLeft(context);
				}
			});
		}
		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		if (scheduleCollection != null && listSchedulesByDay != null) {
			return scheduleCollection
					.get(listSchedulesByDay.get(groupPosition)).size();
		}
		return 0;
	}

	public Object getGroup(int groupPosition) {
		if (listSchedulesByDay == null) {
			return 0;
		}
		return listSchedulesByDay.get(groupPosition);
	}

	public int getGroupCount() {
		if (listSchedulesByDay == null) {
			return 0;
		}
		return listSchedulesByDay.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		HeaderViewHolder viewHolder;
		if ((convertView == null)
				|| (convertView.getTag().getClass().getName()
						.contains("ScheduleViewHolder"))) {
			viewHolder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.headercell, null);
			viewHolder.weekday_TV = (TextView) convertView
					.findViewById(R.id.schedule_weekday_tv);
			viewHolder.date_TV = (TextView) convertView
					.findViewById(R.id.schedule_date_tv);

			viewHolder.weekday_TV.setTypeface(Utils.getTypeFace(context));
			viewHolder.date_TV.setTypeface(Utils.getTypeFace(context));
			convertView.setTag(viewHolder);
		} else {
			// Log.i("class name", convertView.getTag().getClass().getName());
			viewHolder = (HeaderViewHolder) convertView.getTag();
		}
		// Schedule schedule=(Schedule) getGroup(groupPosition);
		// String weekday =
		// MyDate.getWeekdayFromUTCTime(listSchedulesByDay.get(groupPosition));
		// String date =
		// MyDate.transformUTCTimeToCustomStyle(this.getHeader(position));
		String date_time_str = listSchedulesByDay.get(groupPosition);

		if (date_time_str != null) {
			String[] date_time = date_time_str.split(";");

			if (date_time != null) {

				viewHolder.weekday_TV
						.setText(date_time[0] != null ? date_time[0] : "");
				viewHolder.date_TV.setText(date_time[1] != null ? date_time[1]
						: "");
				viewHolder.weekday_TV.setVisibility(View.VISIBLE);
				viewHolder.date_TV.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	static class ScheduleViewHolder {
		TextView service_TV;
		TextView time_TV;
		TextView participants_TV;
		HorizontalListView listview;// = (HorizontalListView)
									// findViewById(R.id.listview);
		// ListView listview;
	}

	static class HeaderViewHolder {
		TextView weekday_TV;
		TextView date_TV;
	}

	private class OnDutyMemberAdapter extends BaseAdapter {
		List<Confirm> listParticipantId;
		String activity_id = "";
		Schedule schedule;
		

		public OnDutyMemberAdapter(Schedule schedule,
				List<Confirm> listParticipantId, String activity_id) {
			this.listParticipantId = listParticipantId;
			this.activity_id = activity_id;
			this.schedule = schedule;

		}

		@Override
		public int getCount() {
			if (listParticipantId == null) {
				return 0;
			}
			return listParticipantId.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DutyScheduleView holder;
			//
			if (convertView == null) {
				holder = new DutyScheduleView(context);
				convertView = holder;
				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (DutyScheduleView) convertView.getTag();

			}
			final Confirm member = listParticipantId.get(position);
			if (member != null) {
				final Sharedmember sm = DatabaseHelper.getSharedDatabaseHelper(context).getSharedmember(member.getMemberId(),activity_id);
				if (sm != null) {
					holder.title.setText(sm.getName());

					switch (member.getConfirm()) {
					case CommConstant.CONFIRM_UNKNOWN:
						holder.layoutTitle
								.setBackgroundResource(R.drawable.onduty_border);
						holder.title.setTextColor(context.getResources()
								.getColor(R.color.on_duty_text));
						break;
					case CommConstant.CONFIRM_CONFIRMED:
						holder.layoutTitle
								.setBackgroundResource(R.drawable.onduty_border_green);
						holder.title.setTextColor(Color.WHITE);
						break;
					case CommConstant.CONFIRM_DENIED:
						holder.layoutTitle
								.setBackgroundResource(R.drawable.onduty_border_red);
						holder.title.setTextColor(Color.WHITE);
						break;
					default:
						holder.layoutTitle
								.setBackgroundResource(R.drawable.onduty_border_gray);
						holder.title.setTextColor(context.getResources()
								.getColor(R.color.on_duty_text));
						break;
					}
					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							participantInforDialog(sm, member, schedule);
						}
					});
				}
			} else {
				holder.setVisibility(View.GONE);
				convertView.setVisibility(View.GONE);
			}
			return holder;
		}

	};

	private void participantInforDialog(final Sharedmember participant,
			final Confirm confirmStatus, final Schedule schedule) {
		String[] array = context.getResources().getStringArray(
				R.array.onduty_member_infor_array);
		boolean isCurrentUser = false;
		if (participant != null) {

			// String currentParticipant = new SharedReference()
			// .getCurrentParticipant(context);
			int member_id = participant.getID();
			String currentParticipant = new SharedReference().getEmail(context);
			String email = participant.getEmail();

			// if user login is not owner
			if (currentParticipant.equals(email)) {
				isCurrentUser = true;
			}
			// if (currentParticipant.equals(String.valueOf(member_id))) {
			// isCurrentUser = true;
			// }
			if (isCurrentUser) {
				if (confirmStatus.getConfirm() == CommConstant.CONFIRM_UNKNOWN) {
					array = context.getResources().getStringArray(
							R.array.onduty_unknown_array);
				} else if (confirmStatus.getConfirm() == CommConstant.CONFIRM_DENIED) {
					array = context.getResources().getStringArray(
							R.array.onduty_deny_array);
				} else if (confirmStatus.getConfirm() == CommConstant.CONFIRM_CONFIRMED) {
					array = context.getResources().getStringArray(
							R.array.onduty_confirm_array);
				}
			}

			final int arraySize = array.length;
			if (arraySize >= 3) {
				for (int i = 0; i < arraySize; i++) {
					array[i] += " " + participant.getName();
				}
			}
			TextViewBaseAdapter adapter = new TextViewBaseAdapter(context,
					array);

			final ParticipantInforDialog dialog = new ParticipantInforDialog(
					context);
			dialog.show();
			dialog.list_item.setAdapter(adapter);

			if (arraySize < 3) {
				dialog.tvTitle.setText(context.getResources().getString(
						R.string.confirmation));
			} else {
				dialog.tvTitle.setText(context.getResources().getString(
						R.string.participant_infor));
			}
			dialog.list_item.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					switch (position) {
					case 0:
						if (arraySize < 3) {
							Confirm confirm = confirmStatus;
							WebservicesHelper ws = WebservicesHelper
									.getInstance();

							if (confirmStatus.getConfirm() == CommConstant.CONFIRM_UNKNOWN) {
								confirm.setConfirm(CommConstant.CONFIRM_CONFIRMED);
								ws.updateConfirmStatus(context, schedule,
										confirm, loadingInterface, iConfirm);
							} else if (confirmStatus.getConfirm() == CommConstant.CONFIRM_DENIED) {
								confirm.setConfirm(CommConstant.CONFIRM_CONFIRMED);
								ws.updateConfirmStatus(context, schedule,
										confirm, loadingInterface, iConfirm);
							} else if (confirmStatus.getConfirm() == CommConstant.CONFIRM_CONFIRMED) {
								confirm.setConfirm(CommConstant.CONFIRM_DENIED);
								ws.updateConfirmStatus(context, schedule,
										confirm, loadingInterface, iConfirm);
							}
						} else {
							Utils.makeAPhoneCall(context,
									participant.getMobile());
						}
						break;
					case 1:
						if (arraySize < 3) {
							Confirm confirm = confirmStatus;
							WebservicesHelper ws = WebservicesHelper
									.getInstance();
							if (confirmStatus.getConfirm() == CommConstant.CONFIRM_UNKNOWN) {
								confirm.setConfirm(CommConstant.CONFIRM_DENIED);
								ws.updateConfirmStatus(context, schedule,
										confirm, loadingInterface, iConfirm);
							}
						} else {
							Utils.sendAMessage(context, participant.getMobile());
						}
						break;
					case 2:
						Utils.sendAnEmail(context, participant.getEmail());
						break;
					default:
						break;
					}
					dialog.dismiss();
				}
			});
			dialog.btn_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
		}
	}

	// show loading
	public void showLoading(Context mContext) {
		if (loadingPopup == null) {
			loadingPopup = new LoadingPopupViewHolder(mContext,
					CategoryTabActivity.DIALOG_LOADING_THEME);
		}
		loadingPopup.setCancelable(true);
		if (!loadingPopup.isShowing()) {
			loadingPopup.show();
		}
	}

	public void dimissDialog() {
		if (loadingPopup != null && loadingPopup.isShowing()) {
			loadingPopup.dismiss();
		}
	}

	public void clearAdapter() {
		try {
			if (listSchedulesByDay != null) {
				listSchedulesByDay.clear();
			}
			if (scheduleCollection != null) {
				scheduleCollection.clear();
			}
			notifyDataSetChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
