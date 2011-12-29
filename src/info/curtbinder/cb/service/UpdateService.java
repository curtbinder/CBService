package info.curtbinder.cb.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	// The messages we listen and send
	public static final String CB_MESSAGE_INTENT = "info.curtbinder.cb.service.CB_MESSAGE";
	public static final String CB_MESSAGE_STRING = "CB_MESSAGE_STRING";
	public static final String CB_INT_INTENT = "info.curtbinder.cb.service.CB_INT";
	public static final String CB_INT_INT = "CB_INT_INT";
	
	private static final String TAG = UpdateService.class.getSimpleName();
	
	// Updater thread that runs
	private Updater updater;
	
	// global application
	private static CBApplication cb;
	private static int counter;
	
	// Our broadcast receiver & filter
	private MessageReceiver r;
	private IntentFilter f;

	@Override
	public IBinder onBind(Intent intent) {
		// for other processes to bind to our service
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		updater = new Updater();
		cb = (CBApplication) getApplication();
		r = new MessageReceiver();
		f = new IntentFilter(CB_INT_INTENT);
		

		Log.d(TAG, "onCreate");
	}

	@Override
	public synchronized void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		// start the updater if it's not already running
		if (!cb.isServiceRunning) {
			counter = 0;
			updater.start();
			// tell the system we want to receive our messages
			registerReceiver(r, f);
		}

		Log.d(TAG, "onStart");
	}

	@Override
	public synchronized void onDestroy() {
		super.onDestroy();

		// stop the updater
		if (cb.isServiceRunning) {
			updater.interrupt();
			// tell the system we don't want the messages anymore
			unregisterReceiver(r);
		}

		updater = null;

		Log.d(TAG, "onDestroy");
	}
	
	// updater thread
	class Updater extends Thread {
		// milliseconds
		private static final long DELAY = 3000; // 3s

		public Updater() {
			super("Updater");
		}

		@Override
		public void run() {
			cb.isServiceRunning = true;
			String s = "";
			
			while (cb.isServiceRunning) {
				try {
					// do something
					s = String.format("%d: %s, %s", counter,
							cb.getUsername(),
							cb.getPassword());
					Log.d(TAG, s);
					
					// Broadcast our message to whoever is listening
					Intent i = new Intent(CB_MESSAGE_INTENT);
					i.putExtra(CB_MESSAGE_STRING, s);
					cb.sendBroadcast(i);
					
					counter++;

					// sleep
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					// interrupted
					cb.isServiceRunning = false;
				}
			} // while
		}
	}
	
	
	// our message receiver
	private class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("MessageReceiver", "onReceive");
			// grab the extra data added and update our counter
			counter += intent.getIntExtra(CB_INT_INT, 0);
		}
		
	}

}
