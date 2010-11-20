package de.syncdroid;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.inject.Inject;

import de.syncdroid.db.service.ProfileService;
import de.syncdroid.service.SyncService;

public class ProfileConfigureFtpActivity extends GuiceActivity  {
	static final String TAG = "ProfileActivity";
	
	@InjectView(R.id.EditText01)             EditText txtLocalDirectory;
	@InjectView(R.id.EditText02)             EditText txtFtpHost;
	@InjectView(R.id.EditText03)             EditText txtFtpUsername;
	@InjectView(R.id.EditText04)             EditText txtFtpPassword;
	@InjectView(R.id.EditText05)             EditText txtFtpPath;
	

    @Inject                            		 ProfileService profileService; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_configure_ftp_activity);
        startService(new Intent(this, SyncService.class));                
        readPrefereces();
    }

	private void readPrefereces() {
		// read saved form values from preferences
        SharedPreferences prefs = getSharedPreferences(TAG,MODE_PRIVATE);
        txtLocalDirectory.setText(prefs.getString(FtpCopyJob.PREF_LOCAL_DIRECTORY, ""));
        txtFtpHost.setText(prefs.getString(FtpCopyJob.PREF_FTP_HOST, ""));
        txtFtpUsername.setText(prefs.getString(FtpCopyJob.PREF_FTP_USERNAMAE, ""));
        txtFtpPassword.setText(prefs.getString(FtpCopyJob.PREF_FTP_PASSWORD, ""));
        txtFtpPath.setText(prefs.getString(FtpCopyJob.PREF_FTP_PATH, ""));
	}

    protected void onPause() {
        Log.i(TAG, "onPause()");
        super.onPause();

    	writePreferences();
    }

	private void writePreferences() {
		// save form values to preferences
		    SharedPreferences.Editor ed = getSharedPreferences(TAG, MODE_PRIVATE).edit();
		    ed.putString(FtpCopyJob.PREF_LOCAL_DIRECTORY, txtLocalDirectory.getText().toString());
		    ed.putString(FtpCopyJob.PREF_FTP_HOST, txtFtpHost.getText().toString());
		    ed.putString(FtpCopyJob.PREF_FTP_USERNAMAE, txtFtpUsername.getText().toString());
		    ed.putString(FtpCopyJob.PREF_FTP_PASSWORD, txtFtpPassword.getText().toString());
		    ed.putString(FtpCopyJob.PREF_FTP_PATH, txtFtpPath.getText().toString());
		    ed.commit();
	}
    
	public void onButtonSyncItClick(View view) {
        Log.i(TAG, "onButtonSyncItClick()");
        writePreferences();
        
		Intent myIntent = new Intent(this, SyncService.class);
		myIntent.setAction(SyncService.INTENT_START_TIMER);
		startService(myIntent);
	}
}