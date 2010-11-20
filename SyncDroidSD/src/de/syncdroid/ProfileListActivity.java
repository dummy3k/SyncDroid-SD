package de.syncdroid;

import java.util.List;

import roboguice.activity.GuiceActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.inject.Inject;

import de.syncdroid.db.model.Profile;
import de.syncdroid.db.service.ProfileService;
import de.syncdroid.service.SyncService;

public class ProfileListActivity extends GuiceActivity {
	static final String TAG = "ProfileListActivity";
	
	@Inject private ProfileService profileService;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_list);
        
		Intent myIntent = new Intent(this, SyncService.class);
		myIntent.setAction(SyncService.INTENT_START_TIMER);
		startService(myIntent);
		
		dumpProfiles();
		
    }

    protected void onPause() {
        Log.i(TAG, "onPause()");
        super.onPause();

        dumpProfiles();
    }
    
	private void dumpProfiles() {
		List<Profile> profiles = profileService.list();
		
		Log.i(TAG, "-------- Profile DUMP ---------");
		for(Profile profile : profiles) {
			Log.i(TAG, "profile #" + profile.getId() + ": " 
					+ profile.getName());
		}
		Log.i(TAG, "-------------------------------");
	}
	
	public void onButtonAddProfileClick(View view) {
        Log.d(TAG, "onButtonSyncItClick()");
        
		Intent intent = new Intent(this, ProfileEditActivity.class);
		intent.putExtra(ProfileEditActivity.PARAM_ACTION, 
				ProfileEditActivity.ACTION_CREATE);
		startActivity(intent);  
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu()");
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