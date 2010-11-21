package de.syncdroid.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;
import de.syncdroid.FtpCopyJob;
import de.syncdroid.Job;
import de.syncdroid.ProfileListActivity;
import de.syncdroid.R;
import de.syncdroid.SyncBroadcastReceiver;

public class SyncService extends Service {
	private static final String TAG = "SyncService";
	private static final int POLL_INTERVALL = 5000;
	
	public static final String TIMER_TICK = "de.syncdroid.TIMER_TICK";
	public static final String INTENT_START_TIMER = "de.syncdroid.INTENT_START_TIMER";
	public static final String INTENT_COLLECT_CELL_IDS = "de.syncdroid.COLLECT_CELL_IDS";
	
	private Queue<Job> jobs = new ConcurrentLinkedQueue<Job>();
	private Set<GsmCellLocation> collectedLocations = new HashSet<GsmCellLocation>();

    /** For showing and hiding our notification. */
    NotificationManager mNM;
    /** Keeps track of all current registered clients. */
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    /** Holds last value set by a client. */
    int mValue = 0;

    /**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    public static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    public static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command to service to set a new value.  This can be sent to the
     * service to supply a new value, and will be sent by the service to
     * any registered clients with the new value.
     */
    public static final int FOUND_NEW_CELL = 3;

    @Override
    public void onStart(Intent intent, int startId) {
		// handle intents
		if( intent != null && intent.getAction() != null ) 
		{
			if( intent.getAction().equals(TIMER_TICK)  )
			{
				Log.d(TAG, "TIMER_TICK");
//				syncIt();
				
				TelephonyManager tm = (TelephonyManager) getSystemService(Activity.TELEPHONY_SERVICE); 
		        GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
		        if (!collectedLocations.contains(location)) {
		        	Log.i(TAG, "new cell location: " + location);
		        	collectedLocations.add(location);
		        }
		        sendMessageToClients(FOUND_NEW_CELL, location);
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
				

//				Intent i2=new Intent(this, SyncBroadcastReceiver.class);
//				i2.setAction("SYNC_IT");
//				PendingIntent pi2=PendingIntent.getBroadcast(this, 0, i2, 0);
//				mgr.cancel(pi2);

			}
			else if(intent.getAction().equals(INTENT_COLLECT_CELL_IDS))
			{
			}
			else if(intent.getAction().equals("de.syncdroid.INTENT_SYNC_IT"))
			{

			} else {
				Log.d(TAG, "unknown intent:");
				Log.d(TAG, "Receive intent= " + intent );
				Log.d(TAG, "action= " + intent.getAction() );
			}
		}
    }

    private void sendMessageToClients(int what, Object obj) {
        mValue = 4711;
        for (int i=mClients.size()-1; i>=0; i--) {
            try {
                mClients.get(i).send(Message.obtain(null,
                		what, obj));
//                mClients.get(i).send(Message.obtain(null,
//                		MSG_SET_VALUE, mValue, 0));
            } catch (RemoteException e) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
            	Log.w(TAG, "removing client: " + i);
                mClients.remove(i);
            }
        }

    }
    
	private void syncIt() {
		Log.d(TAG, "syncIt()");
		
		Job job = new FtpCopyJob(this);
		job.execute();
		jobs.add(job);
	}

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG, "msg.what: " + msg.what);
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case FOUND_NEW_CELL:
                    mValue = msg.arg1;
                    for (int i=mClients.size()-1; i>=0; i--) {
                        try {
                            mClients.get(i).send(Message.obtain(null,
                                    FOUND_NEW_CELL, mValue, 0));
                        } catch (RemoteException e) {
                            // The client is dead.  Remove it from the list;
                            // we are going through the list from back to front
                            // so this is safe to do inside the loop.
                            mClients.remove(i);
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.
        showNotification();
    }

    @Override
    public void onDestroy() {
    	Log.d(TAG,"onDestroy()");
    	
        // Cancel the persistent notification.
        mNM.cancel(R.string.remote_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.remote_service_stopped, Toast.LENGTH_SHORT).show();
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.remote_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.icon, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ProfileListActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.remote_service_label),
                       text, contentIntent);

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.remote_service_started, notification);
    }
	
}
