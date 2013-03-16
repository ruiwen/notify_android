package com.thoughtmonkeys.notify;

import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load preferences from XML
		PreferenceManager prefMgr = getPreferenceManager();
		prefMgr.setSharedPreferencesName(getActivity().getString(R.string.pref_file));
		prefMgr.setSharedPreferencesMode(0);
		
		addPreferencesFromResource(R.xml.preferences);
		
		Log.d("Notify", "Load Preferences");
		
		PreferenceCategory targetCategory = (PreferenceCategory)findPreference(getActivity().getString(R.string.pref_apps));
		// Add the allowed apps
//		SharedPreferences prefs = getActivity().getSharedPreferences("Allowed apps", 0);
		SharedPreferences prefs = prefMgr.getSharedPreferences();
		Log.d("Notify", "Preferences: " + prefs.getAll().entrySet());
		
		PackageManager pm = getActivity().getPackageManager();
		
		for(Map.Entry<String,?> entry : prefs.getAll().entrySet()){
			CheckBoxPreference cbP = new CheckBoxPreference(getActivity());

			String key = entry.getKey();
			ApplicationInfo appInfo;
			try {
				appInfo = pm.getApplicationInfo(key, PackageManager.GET_META_DATA);
				String appName = (String) pm.getApplicationLabel(appInfo);

				Log.d("Notify", "app: " + entry.getKey());
				cbP.setTitle(appName);
				cbP.setKey(entry.getKey());
				cbP.setChecked((Boolean) entry.getValue());

				targetCategory.addPreference(cbP);
			
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
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
