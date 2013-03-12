package com.thoughtmonkeys.notify;

import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.util.Log;

public class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load preferences from XML
		//addPreferencesFromResource(R.xml.preferences);
		
//		Log.d("Notify", "Load Preferences");
		
//		PreferenceCategory targetCategory = (PreferenceCategory)findPreference("Allowed apps");
//		// Add the allowed apps
//		SharedPreferences prefs = getActivity().getSharedPreferences("Allowed apps", 0);
//		Log.d("Notify", "Preferences: " + prefs.getAll().entrySet());
//		for(Map.Entry<String,?> entry : prefs.getAll().entrySet()){
//			CheckBoxPreference cbP = new CheckBoxPreference(getActivity());
//			
//			Log.d("Notify", "app: " + entry.getKey());
//			cbP.setTitle(entry.getKey());
//			cbP.setKey(entry.getKey());
//			cbP.setChecked((Boolean) entry.getValue());
//
//			targetCategory.addPreference(cbP);
//		}
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


}
