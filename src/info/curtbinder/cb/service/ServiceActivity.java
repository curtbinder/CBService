package info.curtbinder.cb.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ServiceActivity extends Activity implements OnClickListener {

	private final static String TAG = ServiceActivity.class.getSimpleName();
	private TextView tv;
	CBApplication cb;
	
	// our message receiver
	UpdateDataReceiver receiver;
	IntentFilter filter;

	class UpdateDataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("UpdateDataReceiver", "onReceive");
			updateDisplay(intent.getStringExtra(UpdateService.CB_MESSAGE_STRING));
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		createReceivers();
		Button b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(this);
		tv = (TextView) findViewById(R.id.textView1);
		tv.setText("");

		cb = (CBApplication) getApplication();
		String username = cb.getUsername();
		String password = cb.getPassword();
		Log.d(TAG, "Stored info: (" + username + ", " + password + ")");
	}

	// Message Receiver Stuff
	private void createReceivers() {
		receiver = new UpdateDataReceiver();
		filter = new IntentFilter(UpdateService.CB_MESSAGE_INTENT);
	}

	private void registerReceivers() {
		registerReceiver(receiver, filter);
	}

	private void unregisterReceivers() {
		unregisterReceiver(receiver);
	}

	private void updateDisplay(String msg) {
		// updates the display with the message from the service
		tv.setText(msg);
	}

	private void forceUpdate() {
		// send a message to the service to force an update
		Intent i = new Intent(UpdateService.CB_INT_INTENT);
		i.putExtra(UpdateService.CB_INT_INT, 42);
		sendBroadcast(i);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceivers();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceivers();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1: {
			// Button click sends the message to the service
			Log.d(TAG, "Send Command");
			forceUpdate();
		}
		}
	}

	// Create the menu 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Handle menu selections
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemPreferences:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
		case R.id.itemServiceToggle:
			if (cb.isServiceRunning) {
				stopService(new Intent(this, UpdateService.class));
			} else {
				startService(new Intent(this, UpdateService.class));
			}
			break;
		}
		return true;
	}

	// Do something when the menu is opened/displayed
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// we will toggle the menu choice between Start & Stop service
		// based on the status of the service
		MenuItem itemToggle = menu.findItem(R.id.itemServiceToggle);

		if (cb.isServiceRunning) {
			itemToggle.setTitle(R.string.titleServiceStop);
			itemToggle.setIcon(android.R.drawable.ic_media_pause);
		} else {
			itemToggle.setTitle(R.string.titleServiceStart);
			itemToggle.setIcon(android.R.drawable.ic_media_play);
		}
		return true;
	}
}