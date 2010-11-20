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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ProfileActivity extends Activity {
	private static final String TAG = "ProfileActivity";
	
	private EditText txtLocalDirectory;
	private EditText txtFtpHost;
	private EditText txtFtpUsername;
	private EditText txtFtpPassword;
	private EditText txtFtpPath;
	
	private static final String PREF_LOCAL_DIRECTORY = "pref_local_directory";
	private static final String PREF_FTP_HOST = "pref_ftp_host";
	private static final String PREF_FTP_USERNAMAE = "pref_ftp_username";
	private static final String PREF_FTP_PASSWORD = "pref_ftp_password";
	private static final String PREF_FTP_PATH = "pref_ftp_path";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        
        

        // short access for text edit controls
        txtLocalDirectory = (EditText) findViewById(R.id.EditText01);
        txtFtpHost = (EditText) findViewById(R.id.EditText02);
        txtFtpUsername = (EditText) findViewById(R.id.EditText03);
        txtFtpPassword = (EditText) findViewById(R.id.EditText04);
        txtFtpPath = (EditText) findViewById(R.id.EditText05);
                
        // read saved form values from preferences
        SharedPreferences prefs = getSharedPreferences(TAG,MODE_PRIVATE);
        txtLocalDirectory.setText(prefs.getString(PREF_LOCAL_DIRECTORY, ""));
        txtFtpHost.setText(prefs.getString(PREF_FTP_HOST, ""));
        txtFtpUsername.setText(prefs.getString(PREF_FTP_USERNAMAE, ""));
        txtFtpPassword.setText(prefs.getString(PREF_FTP_PASSWORD, ""));
        txtFtpPath.setText(prefs.getString(PREF_FTP_PATH, ""));
    }

    protected void onPause() {
        Log.i(TAG, "onPause()");
        super.onPause();

     // save form values to preferences
        SharedPreferences.Editor ed = getSharedPreferences(TAG, MODE_PRIVATE).edit();
        ed.putString(PREF_LOCAL_DIRECTORY, txtLocalDirectory.getText().toString());
        ed.putString(PREF_FTP_HOST, txtFtpHost.getText().toString());
        ed.putString(PREF_FTP_USERNAMAE, txtFtpUsername.getText().toString());
        ed.putString(PREF_FTP_PASSWORD, txtFtpPassword.getText().toString());
        ed.putString(PREF_FTP_PATH, txtFtpPath.getText().toString());
        ed.commit();
    }
	public void onButtonSyncItClick(View view) {
		String localDirectory = txtLocalDirectory.getText().toString();
		String host = txtFtpHost.getText().toString();
		String user = txtFtpUsername.getText().toString();
		String password = txtFtpPassword.getText().toString();
		String path = txtFtpPath.getText().toString();
		
		try {
			// connect to ftp server
			FTPClient ftpClient = new FTPClient();
			ftpClient.connect(InetAddress.getByName(host));
			ftpClient.login(user, password);
			ftpClient.changeWorkingDirectory(path);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			
			File localFile = new File(localDirectory);
			
			BufferedInputStream inputStream=null;
			inputStream = new BufferedInputStream(
					new FileInputStream(localFile));
			ftpClient.enterLocalPassiveMode();
			ftpClient.storeFile(localFile.getName(), inputStream);
			inputStream.close();
			
			// disconnect from ftp server
			ftpClient.logout();
			ftpClient.disconnect();
		} catch(Exception e) {

			Log.e(TAG, "whoa, exception: " + e);
			
			 AlertDialog.Builder adb=new AlertDialog.Builder(ProfileActivity.this);
			 
			 Throwable cause = e;
			 while(cause.getCause() != null) {
				 cause = cause.getCause();
			 }
			 
			 adb.setMessage(cause.getClass().getSimpleName() + ": " + cause.getMessage());
			 adb.setNegativeButton("whatever", null);
			 adb.show();
		}

	}
}