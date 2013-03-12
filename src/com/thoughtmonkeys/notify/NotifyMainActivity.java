package com.thoughtmonkeys.notify;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class NotifyMainActivity extends Activity implements OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notify_main);
		
//		getFragmentManager().beginTransaction()
//			.replace(R.id.prefsFragment, new PrefsFragment())
//			.commit();
			
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		Log.d("Notify", "Key changed: " + key);
		Log.d("Notify", "New value: " + sharedPreferences.getBoolean(key, false));
		Log.d("Notify", "Prefs: " + sharedPreferences);
//		if (key.equals(KEY_PREF_SYNC_CONN)) {
//			Preference connectionPref = findPreference(key);
//			// Set summary to be the user-description for the selected value
//			connectionPref.setSummary(sharedPreferences.getString(key, ""));
//		}
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
