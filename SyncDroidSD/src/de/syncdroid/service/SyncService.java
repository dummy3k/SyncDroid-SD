package de.syncdroid.service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import de.syncdroid.FtpCopyJob;
import de.syncdroid.Job;
import de.syncdroid.SyncBroadcastReceiver;

public class SyncService extends Service {
	private static final String TAG = "SyncService";
	private static final int POLL_INTERVALL = 5000;
	
	public static final String TIMER_TICK = "de.syncdroid.TIMER_TICK";
	public static final String INTENT_START_TIMER = "de.syncdroid.INTENT_START_TIMER";
	public static final String INTENT_COLLECT_CELL_IDS = "de.syncdroid.COLLECT_CELL_IDS";
	
	private Queue<Job> jobs = new ConcurrentLinkedQueue<Job>();
	private Set<GsmCellLocation> collectedLocations = new HashSet<GsmCellLocation>();

    @Override
    public void onStart(Intent intent, int startId) {

		// handle intents
		if( intent != null && intent.getAction() != null ) 
		{
			if( intent.getAction().equals(TIMER_TICK)  )
			{
//				Log.d(TAG, "TIMER_TICK");
//				syncIt();
				
				TelephonyManager tm = (TelephonyManager) getSystemService(Activity.TELEPHONY_SERVICE); 
		        GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
		        if (!collectedLocations.contains(location)) {
		        	Log.i(TAG, "new cell location: " + location);
		        	collectedLocations.add(location);
		        }
			}
			else if(intent.getAction().equals(INTENT_START_TIMER) ||
				intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
			{
				Log.d(TAG, "set timer");
				AlarmManager mgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
				Intent i=new Intent(this, SyncBroadcastReceiver.class);
				i.setAction(TIMER_TICK);
				
				// get a Calendar object with current time
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.SECOND, 4);

				PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);
				mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
						SystemClock.elapsedRealtime(), POLL_INTERVALL, pi);
				

				Intent i2=new Intent(this, SyncBroadcastReceiver.class);
				i2.setAction("SYNC_IT");
				PendingIntent pi2=PendingIntent.getBroadcast(this, 0, i2, 0);
				mgr.cancel(pi2);

			}
			else if(intent.getAction().equals(INTENT_COLLECT_CELL_IDS))
			{
//			}
//			else if(intent.getAction().equals("de.syncdroid.INTENT_SYNC_IT"))
//			{

			} else {
				Log.d(TAG, "unknown intent:");
				Log.d(TAG, "Receive intent= " + intent );
				Log.d(TAG, "action= " + intent.getAction() );
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
		Log.d(TAG, "onBind()");
        return null;
    }
	
}
