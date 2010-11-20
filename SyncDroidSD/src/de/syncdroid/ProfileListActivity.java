package de.syncdroid;

import de.syncdroid.service.SyncService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Log.w(TAG, "onCreateOptionsMenu()");
		MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.profile_list_menu, menu);
    	return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d(TAG, "onOptionsItemSelected()");
		switch (item.getItemId()) {
		// We have only one menu option
		case R.id.item01:
			Intent intent = new Intent(this, LocationActivity.class);
			startActivity(intent);
			break;
			
		default:
			Log.d(TAG, "unknown menu");
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

}