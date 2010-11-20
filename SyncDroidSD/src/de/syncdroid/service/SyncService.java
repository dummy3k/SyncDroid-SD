package de.syncdroid.service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import de.syncdroid.FtpCopyJob;
import de.syncdroid.Job;

public class SyncService extends Service {
	private static final String TAG = "SyncService";
	
	public static final String INTENT_SYNC_IT = "de.syncdroid.INTENT_SYNC_IT";
	
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
				// always toggle openvpn if this was the received intent
				syncIt();
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
