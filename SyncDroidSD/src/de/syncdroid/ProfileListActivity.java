package de.syncdroid;

import de.syncdroid.service.SyncService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class ProfileListActivity extends Activity {
	static final String TAG = "ProfileListActivity";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_list_activity);
        
		Intent myIntent = new Intent(this, SyncService.class);
		myIntent.setAction(SyncService.INTENT_START_TIMER);
		startService(myIntent);

    }
	
	public void onButtonAddProfileClick(View view) {
        Log.i(TAG, "onButtonSyncItClick()");
        
		Intent intent = new Intent(this, ProfileTypeActivity.class);
		startActivity(intent);  
	}

}