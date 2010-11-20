package de.syncdroid;

import de.syncdroid.service.SyncService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncBroadcastReceiver extends BroadcastReceiver{
	private static final String TAG = "SyncBroadcastReceiver";

	public static final String ACTION_BOOT_COMPLETED = "android.intent.action.ACTION_BOOT_COMPLETED";
	public static final String ACTION_POWER_CONNECTED = "android.intent.action.ACTION_POWER_CONNECTED";
	public static final String ACTION_POWER_DISCONNECTED = "android.intent.action.ACTION_POWER_DISCONNECTED";
	
	public void onReceive(Context context, Intent intent ) {
		Log.d(TAG, "Receive intent= " + intent );
		Log.d(TAG, "action= " + intent.getAction() );
		Intent serviceIntent = new Intent(context, SyncService.class);
		if( intent.getAction() != null ) {
			serviceIntent.setAction(intent.getAction());
		}		
		if( intent.getExtras() != null ) {
			serviceIntent.putExtras(intent.getExtras());
		}
		context.startService(serviceIntent);
	}
}
