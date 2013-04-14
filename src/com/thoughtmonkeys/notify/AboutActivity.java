package com.thoughtmonkeys.notify;


import com.thoughtmonkeys.notify.donate.R;

import com.google.analytics.tracking.android.EasyTracker;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.content.Intent;

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


	public void sendFeedback(View view) {

		// Track the send feedback button click
		EasyTracker.getTracker().sendEvent("app_action", "send_feedback", "", 1L);
	
		// Kick off a new Intent to send feedback
		Intent feedbackIntent = new Intent(android.content.Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.feedback_email), null));
		feedbackIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
		feedbackIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.feedback_body));
		startActivity(Intent.createChooser(feedbackIntent, getString(R.string.feedback_chooser_title)));

	}
	

	public void yesDonate(View view) {
	
		// Track the donate button click
		EasyTracker.getTracker().sendEvent("app_action", "donate_intent", "", 1L);
		
		// Kick off a new Intent to show the app page
		try {
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getString(R.string.donate_app_name))));
		} catch (android.content.ActivityNotFoundException anfe) {
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getString(R.string.donate_app_name))));
		}
	}
	

}
