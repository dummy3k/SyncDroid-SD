package de.syncdroid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import de.syncdroid.service.SyncService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ProfileActivity extends Activity {
	static final String TAG = "ProfileActivity";
	
	public static EditText txtLocalDirectory;
	public static EditText txtFtpHost;
	public static EditText txtFtpUsername;
	public static EditText txtFtpPassword;
	public static EditText txtFtpPath;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        startService(new Intent(this, SyncService.class));

        // short access for text edit controls
        txtLocalDirectory = (EditText) findViewById(R.id.EditText01);
        txtFtpHost = (EditText) findViewById(R.id.EditText02);
        txtFtpUsername = (EditText) findViewById(R.id.EditText03);
        txtFtpPassword = (EditText) findViewById(R.id.EditText04);
        txtFtpPath = (EditText) findViewById(R.id.EditText05);
                
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