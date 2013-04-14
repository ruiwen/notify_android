package com.thoughtmonkeys.notify;


import com.google.analytics.tracking.android.EasyTracker;
import android.os.Bundle;
import android.app.Activity;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}

	@Override
	protected void onStart() {
	
		super.onStart();		
		EasyTracker.getInstance().activityStart(this);
	}


	@Override
	public void onStop() {
		super.onStop();
		
		EasyTracker.getInstance().activityStop(this);
	}

}
