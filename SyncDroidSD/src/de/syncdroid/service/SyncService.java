package de.syncdroid.service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import de.syncdroid.FtpCopyJob;
import de.syncdroid.Job;

public class SyncService extends Service {
	private static final String TAG = "SyncService";
	
	public static final String INTENT_SYNC_IT = "de.syncdroid.INTENT_SYNC_IT";
	public static final String INTENT_START_TIMER = "de.syncdroid.INTENT_START_TIMER";
	
	private Queue<Job> jobs = new ConcurrentLinkedQueue<Job>();

    @Override
    public void onStart(Intent intent, int startId) {
		Log.d(TAG, "onStart()");
		Log.d(TAG, "Receive intent= " + intent );
		Log.d(TAG, "action= " + intent.getAction() );

		// handle intents
		if( intent != null && intent.getAction() != null ) 
		{
			if( intent.getAction().equals(INTENT_SYNC_IT)  )
			{
				Log.d(TAG, "INTENT_SYNC_IT");
				//syncIt();
			}
			if( intent.getAction().equals(INTENT_START_TIMER)  &&
				intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
			{
				Log.d(TAG, "set timer");
				AlarmManager mgr = (AlarmManager) 
						getSystemService(Context.ALARM_SERVICE);
				
				Intent i = new Intent(this, SyncService.class);
				i.setAction(INTENT_SYNC_IT);
				PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

				mgr.setRepeating(AlarmManager.RTC_WAKEUP, 
						System.currentTimeMillis() + 1000, 1000, pi);
//				mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
//						SystemClock.elapsedRealtime(), 1000, pi);

			}
		}
    }

	private void syncIt() {
		Log.d(TAG, "syncIt()");
		
		Job job = new FtpCopyJob(this);
		job.execute();
		jobs.add(job);
	}

	@Override
    public IBinder onBind(Intent intent) {
        return null;
    }
	
}
