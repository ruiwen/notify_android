package com.thoughtmonkeys.notify;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class NotifyMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notify_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notify_main, menu);
		return true;
	}


	// Notify Button
	public void btnNotifyClick(View v) {
		EditText txtNotification = (EditText)findViewById(R.id.txtNotification);

		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("My notification")
		        .setContentText(txtNotification.getText().toString())
		        .setTicker(txtNotification.getText().toString());
		
		Log.d("Notify", txtNotification.getText().toString());
		
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int mId = 0;
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());

		Log.d("Notify", "Notified");
		
	}

}
