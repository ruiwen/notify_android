package com.thoughtmonkeys.notify;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class NotifyMainActivity extends Activity implements OnSharedPreferenceChangeListener {

	// GA tracking
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notify_main);
		
	}
	
	@Override
	protected void onStart() {
	
		super.onStart();
		
		EasyTracker.getInstance().activityStart(this);
		
		// Initialise other bits
		mGaInstance = GoogleAnalytics.getInstance(this);
		mGaTracker = mGaInstance.getTracker(getString(R.string.ga_trackingId));
		
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
	
		// Track menu show
		mGaTracker.sendEvent("app_action", "menu_click", "", 1L);
	
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notify_main, menu);
		return true;
	}
	
	
	public void launchAbout(MenuItem item) {
	
		// Track about show
		mGaTracker.sendEvent("app_action", "about_show", "", 1L);
	
		// Launch the AboutActivity
		Intent aboutIntent = new Intent(this, AboutActivity.class);
		startActivity(aboutIntent);
	}
	
	
	public void goToAccessibilityServices(View view) {
	
		// Track clicks on the GoToAccessibilityServices button
		mGaTracker.sendEvent("app_action", "accessibility_button", "", 1L);
	
		// Launch the Accessibility Services screen
		Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
		startActivityForResult(intent, 0);
	}

}
