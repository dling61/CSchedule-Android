package com.dling61.calendarschedule;

import java.util.ArrayList;
import java.util.List;

import com.dling61.calendarschedule.adapter.ShareMemberAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
import com.dling61.calendarschedule.models.Participant;
import com.dling61.calendarschedule.models.Sharedmember;
import com.dling61.calendarschedule.net.WebservicesHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class ShareActivity extends Activity implements OnItemClickListener  {
	
	private ListView sharedMemberList;
	private DatabaseHelper dbHelper;
	private List<Sharedmember> sharedmembers;
	private List<Integer> newsharedMembers;
	private ProgressDialog mDialog;
	private ShareMemberAdapter smAdapter;
	private int current_activityid;

	Context mContext;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.shareactivity);
		mContext=this;
		sharedMemberList = (ListView) this.findViewById(R.id.shareListView);
		sharedMemberList.setOnItemClickListener(this);
		dbHelper = DatabaseHelper.getSharedDatabaseHelper(this);
		sharedmembers = new ArrayList<Sharedmember>();
		newsharedMembers = new ArrayList<Integer>();
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("Please wait");
		mDialog.setCancelable(false);
		smAdapter = new ShareMemberAdapter (this,this.getLayoutInflater(), sharedmembers);
		
		Intent myIntent = this.getIntent();
		
		current_activityid = myIntent.getIntExtra("serviceid", 0);
		if (current_activityid > 0)
		{
			mDialog.show();
			
			WebservicesHelper ws=new WebservicesHelper(mContext);
//			ws.getSharedmembersForActivity(current_activityid, sharedmembers);
		}
		
		IntentFilter filter = new IntentFilter("cschedule.sharedmembersready");
		BroadcastReceiver receiver = new BroadcastReceiver ()
		{
			public void onReceive(Context arg0, Intent arg1)
			{
				unregisterReceiver(this);
				ShareActivity current_activity = (ShareActivity) arg0;
				current_activity.combineSharedmembersWithContacts();
				smAdapter.setSharedmembers(sharedmembers);
				sharedMemberList.setAdapter(smAdapter);
				smAdapter.notifyDataSetChanged();
				mDialog.dismiss();
			}
		};
		this.registerReceiver(receiver, filter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void combineSharedmembersWithContacts ()
	{
	//	Toast.makeText(this, "Combination", Toast.LENGTH_SHORT).show();
		List <Participant> myContacts = dbHelper.getParticipants();
		for (int i = 0; i < myContacts.size(); i++)
		{
			Participant p = myContacts.get(i);
			if (this.containsContact(p) == false)
			{
				Sharedmember newsm = new Sharedmember (p.getID(),p.getName(),p.getEmail(),p.getMobile(),DatabaseHelper.NOSHARE,current_activityid);
				sharedmembers.add(newsm);
			}
		}
	}
	
	public boolean containsContact (Participant p)
	{
		for (int i = 0; i < sharedmembers.size(); i++)
		{
			Sharedmember sm = sharedmembers.get(i);
			if (sm.getID() == p.getID())
				return true;
		}
		newsharedMembers.add(p.getID());
		return false;
	}
	
	public void itemSelected(View v)
	{
		ImageButton imgbtn = (ImageButton) v;
		int position = (Integer) v.getTag();
		Sharedmember sm = sharedmembers.get(position);
		if (sm.getRole() != 0)
		{
			int newrole =   sm.getRole() + 1 ;
			if (newrole > 4)
				newrole = 1;
			sm.setRole(newrole);
			switch (sm.getRole())
			{
				case 1:
					imgbtn.setImageDrawable(this.getResources().getDrawable(R.drawable.organizer));
					break;
				case 2:
					imgbtn.setImageDrawable(this.getResources().getDrawable(R.drawable.participant));
					break;
				case 3:
					imgbtn.setImageDrawable(this.getResources().getDrawable(R.drawable.viewer));
					break;
				case 4:
					imgbtn.setImageDrawable(this.getResources().getDrawable(R.drawable.noshare));
					break;
				default:
					
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	public void shareDone (View v)
	{
		for (int i = 0; i < sharedmembers.size(); i++)
		{
			Sharedmember sm = sharedmembers.get(i);
			if (newsharedMembers.contains(sm.getID())) 
			{
				if (sm.getRole() != DatabaseHelper.NOSHARE)
				{
					WebservicesHelper ws=new WebservicesHelper(mContext);
					ws.postSharedmemberToActivity(sm.getID(), sm.getRole(), current_activityid+"");
				}
			}
			else
			{
				if (sm.getRole() == DatabaseHelper.NOSHARE)
				{
					WebservicesHelper ws=new WebservicesHelper(mContext);
//					ws.deleteSharedmemberOfActivity(sm.getID(), current_activityid);
				}
				else
				{
					WebservicesHelper ws=new WebservicesHelper(mContext);
					ws.alterSharedmemberToActivity(sm.getID(), sm.getRole(), current_activityid);
				}
			}
		}
		
		Intent resultIntent = new Intent();
//		resultIntent.putExtra("id", thisActivity.get_ID());
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
		
		
	}
}
