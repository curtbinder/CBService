package info.curtbinder.cb.service;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CBApplication extends Application {

	private SharedPreferences prefs;
	boolean isServiceRunning = false;

	@Override
	public void onCreate() {
		super.onCreate();

		// do stuff specific to entire application
		// created whenever ANY object (activity/service) of the project is
		// started

		// store the preferences handle
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	public String getUsername() {
		return prefs.getString("username", "");
	}
	
	public String getPassword() {
		return prefs.getString("password", "");
	}
}
