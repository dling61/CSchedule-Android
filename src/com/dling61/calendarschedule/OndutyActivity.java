package com.dling61.calendarschedule;

import java.util.ArrayList;
import java.util.List;

import com.dling61.calendarschedule.adapter.OndutyAdapter;
import com.dling61.calendarschedule.db.DatabaseHelper;
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
import android.widget.Toast;

public class OndutyActivity extends Activity implements OnItemClickListener {
	
	private ListView ondutyList;
	private DatabaseHelper dbHelper;
	private List<Sharedmember> sharedmembers;
	private List<Integer> pins;
	private ProgressDialog mDialog;
	private int activity_id;
	private OndutyAdapter oa;
	private BroadcastReceiver receiver;
	
	Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ondutypage);
		mContext=this;
		ondutyList = (ListView) this.findViewById(R.id.ondutyListView);
		ondutyList.setOnItemClickListener(this);
		dbHelper = DatabaseHelper.getSharedDatabaseHelper(this);
		sharedmembers = new ArrayList<Sharedmember>();
		
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("Please wait");
		mDialog.setCancelable(false);
		
		
		Intent myIntent = this.getIntent();
		pins = myIntent.getIntegerArrayListExtra("pins");
		activity_id = myIntent.getIntExtra("activityid", -1);
		
		mDialog.show();
		
		WebservicesHelper ws=new WebservicesHelper(mContext);
//		ws.getSharedmembersForActivity(activity_id, sharedmembers);
		
		IntentFilter filter = new IntentFilter("cschedule.sharedmembersready");
		
		receiver = new BroadcastReceiver ()
		{
			public void onReceive(Context arg0, Intent arg1)
			{
				OndutyActivity current_activity = (OndutyActivity) arg0;
				current_activity.oa = new OndutyAdapter(mContext,sharedmembers,pins);
				ondutyList.setAdapter(current_activity.oa);
				current_activity.oa.notifyDataSetChanged();
				mDialog.dismiss();
			}
		};
		
		this.registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	public void itemSelected(View v)
	{
		ImageButton imgbtn = (ImageButton) v;
		int tag = (Integer) v.getTag();
		if(pins.contains(tag))
		{
			imgbtn.setImageResource(R.drawable.unchecked);
			pins.remove((Integer)tag);
		}
		else
		{
			imgbtn.setImageResource(R.drawable.checked);
			pins.add((Integer)tag);
		}
	}
	
	public void ondutyDone (View v)
	{
		Intent resultIntent = new Intent();
		resultIntent.putIntegerArrayListExtra("pins", (ArrayList<Integer>) pins);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
	
}
