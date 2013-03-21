package com.thoughtmonkeys.notify;

import com.google.analytics.tracking.android.EasyTracker;
import com.thoughtmonkeys.notify.donate.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;


public class NotifyMainActivity extends Activity implements OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notify_main);
		
	}
	
	@Override
	protected void onStart() {
	
		super.onStart();
		
		EasyTracker.getInstance().activityStart(this);
		
		String servicesEnabled = Settings.Secure.getString(this.getContentResolver(),android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
		
		if(servicesEnabled != null) {
			Log.d("Notify", "Services: " + servicesEnabled);
			Log.d("Notify", "Matches: " + servicesEnabled.matches(".*" + getPackageName() + "/com.thoughtmonkeys.notify.NotificationService.*"));
			TextView accessibilityOnOff = (TextView)findViewById(R.id.accessibilityOnOff);

			accessibilityOnOff.setText(
			    (servicesEnabled.matches(".*" + getPackageName() + "/com.thoughtmonkeys.notify.NotificationService.*")) ?
			         R.string.pref_accessibility_enabled : R.string.pref_accessibility_disabled);
		}
	}

	
	@Override
	public void onStop() {
		super.onStop();
		
		EasyTracker.getInstance().activityStop(this);
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
	
	public void goToAccessibilityServices(View view) {
		Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
		startActivityForResult(intent, 0);
	}

}
