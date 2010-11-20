package de.syncdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ProfileTypeActivity extends Activity {
	static final String TAG = "ProfileTypeActivity";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_list_activity);

    }

	public void onButtonAddProfileClick(View view) {
        Log.i(TAG, "onButtonSyncItClick()");
        
		Intent intent = new Intent(this, ProfileTypeActivity.class);
		startActivity(intent);  
	}

}