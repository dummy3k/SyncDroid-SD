package de.syncdroid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class FtpCopyJob implements Job {
	private static final String TAG = "FtpCopyJob";
	
	private String host;
	private String username;
	private String password;
	private String localPath;
	private String remotePath;
	private Long lastSync;
	private Context context;
	
	public static final String PREF_FTP_PATH = "pref_ftp_path";
	public static final String PREF_FTP_PASSWORD = "pref_ftp_password";
	public static final String PREF_FTP_USERNAMAE = "pref_ftp_username";
	public static final String PREF_FTP_HOST = "pref_ftp_host";
	public static final String PREF_LOCAL_DIRECTORY = "pref_local_directory";
	public static final String PREF_LASTSYNC = "pref_last_sync";
	
	public FtpCopyJob(Context context) {
		this.context = context;
		SharedPreferences prefs = context.getSharedPreferences(
				ProfileEditActivity.TAG, Activity.MODE_PRIVATE);

        localPath = prefs.getString(FtpCopyJob.PREF_LOCAL_DIRECTORY, "");
        host = prefs.getString(FtpCopyJob.PREF_FTP_HOST, "");
        username = prefs.getString(FtpCopyJob.PREF_FTP_USERNAMAE, "");
        password = prefs.getString(FtpCopyJob.PREF_FTP_PASSWORD, "");
        remotePath = prefs.getString(FtpCopyJob.PREF_FTP_PATH, "");
        lastSync = prefs.getLong(FtpCopyJob.PREF_LASTSYNC, -1);

        if(remotePath.startsWith("/")) {
        	remotePath = remotePath.substring(1);
        }
	}

	private class RemoteFile {
		public Boolean isDirectory;
		public String name;
		public File source;
		public Long newest;
		public List<RemoteFile> children = new ArrayList<FtpCopyJob.RemoteFile>();
		
//		public long getNewest() {
////			Long newest = 0L;
////			for (RemoteFile item : fileList) {
////				newest = Math.max(newest, item.lastModified);
////			}
//			
//		}
	}
	
	private RemoteFile buildTree(File dir) {
		RemoteFile here = new RemoteFile();
		here.isDirectory = true;
		here.name = dir.getName();
		here.source = dir;
		here.newest = 0L;

		for (String item : dir.list()) {
			File fileItem = new File(dir, item);
			if (fileItem.isDirectory()) {
				RemoteFile tmp = buildTree(fileItem);
				here.children.add(tmp);
				here.newest = Math.max(here.newest, tmp.newest);
			} else {
				RemoteFile aFile = new RemoteFile();
				aFile.isDirectory = false;
				aFile.name = item;
				aFile.source = fileItem;
				here.children.add(aFile);
				here.newest = Math.max(here.newest, fileItem.lastModified());
			}
		}
		
		return here;
	}

	private void uploadFiles(RemoteFile dir, FTPClient ftpClient, Long lastUpload) throws IOException {
		if (!ftpClient.changeWorkingDirectory(dir.name)) {
			if (!ftpClient.makeDirectory(dir.name)) {
				Log.e(TAG, "could not create directory");
				return;
			} else if (!ftpClient.changeWorkingDirectory(dir.name)) {
				Log.e(TAG, "could not change directory");
				return;
			}
		}
		
		for (RemoteFile item:dir.children) {
			if (item.isDirectory) {
				uploadFiles(item, ftpClient, lastUpload);
			} else if (item.source.lastModified() > lastUpload) {
				BufferedInputStream inputStream=null;
				inputStream = new BufferedInputStream(
						new FileInputStream(item.source));
				ftpClient.enterLocalPassiveMode();
				ftpClient.storeFile(item.name, inputStream);
				inputStream.close();
			}
		}
		ftpClient.changeToParentDirectory();
	}
	
	@Override
	public void execute() {
		Log.d(TAG, "lastSync: " + lastSync);
		try {
			RemoteFile rootRemote = buildTree(new File(localPath));

			if (lastSync > 0 && rootRemote.newest <= lastSync) {
				Log.d(TAG, "nothing to do");
				return;
			}

			// connect to ftp server
			FTPClient ftpClient = new FTPClient();
			ftpClient.connect(InetAddress.getByName(host));
			ftpClient.login(username, password);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			uploadFiles(rootRemote, ftpClient, lastSync);
			
			// disconnect from ftp server
			ftpClient.logout();
			ftpClient.disconnect();
		} catch(Exception e) {

			Log.e(TAG, "whoa, exception: " + e);
			
			 /*
			 AlertDialog.Builder adb = new AlertDialog.Builder(this);
			 
			 Throwable cause = e;
			 while(cause.getCause() != null) {
				 cause = cause.getCause();
			 }
			 
			 adb.setMessage(cause.getClass().getSimpleName() + ": " + cause.getMessage());
			 adb.setNegativeButton("whatever", null);
			 adb.show();*/
		}

		Log.d(TAG, "upload success");
	    SharedPreferences.Editor ed = context.getSharedPreferences(ProfileEditActivity.TAG, Activity.MODE_PRIVATE).edit();
	    ed.putLong(FtpCopyJob.PREF_LASTSYNC, new Date().getTime());
	    ed.commit();
	}

	public String getLocalDirectory() {
		return localPath;
	}

	public void setLocalDirectory(String localDirectory) {
		this.localPath = localDirectory;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return username;
	}

	public void setUser(String user) {
		this.username = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPath() {
		return remotePath;
	}

	public void setPath(String path) {
		this.remotePath = path;
	}

}
