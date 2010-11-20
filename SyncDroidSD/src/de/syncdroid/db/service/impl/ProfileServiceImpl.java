package de.syncdroid.db.service.impl;

import android.content.ContentValues;
import android.database.Cursor;
import de.syncdroid.db.model.Profile;
import de.syncdroid.db.service.ProfileService;

public class ProfileServiceImpl extends AbstractServiceImpl<Profile> implements ProfileService {
	
	protected String getTableName() {
		return "profiles";
	}
	
	protected Profile read(Cursor cursor) {
		Profile obj = new Profile();
		
		obj.setName(cursor.getString(cursor.getColumnIndex("name")));
		obj.setId(cursor.getLong(cursor.getColumnIndex("id")));
		//obj.setLastSync(cursor.get(cursor.getColumnIndex("lastSync")));
		obj.setOnlyIfWifi(cursor.getInt(cursor.getColumnIndex("onlyIfWifi")) == 1);
		obj.setHostname(cursor.getString(cursor.getColumnIndex("hostname")));
		obj.setUsername(cursor.getString(cursor.getColumnIndex("username")));
		obj.setPassword(cursor.getString(cursor.getColumnIndex("password")));
		obj.setLocalPath(cursor.getString(cursor.getColumnIndex("localPath")));
		obj.setRemotePath(cursor.getString(cursor.getColumnIndex("remotePath")));
		//obj.setPort(cursor.getInt(cursor.getColumnIndex("port")));
		
		return obj;
	}
	
	protected ContentValues write(Profile obj) {
		ContentValues values = new ContentValues();
		values.put("id", obj.getId());
		values.put("name", obj.getName());
		values.put("onlyIfWifi", (obj.getOnlyIfWifi() != null 
				? obj.getOnlyIfWifi() : false) ? 1 : 0);

		values.put("hostname", obj.getHostname());
		values.put("username", obj.getUsername());
		values.put("password", obj.getPassword());
		values.put("localPath", obj.getLocalPath());
		values.put("remotePath", obj.getRemotePath());
		//values.put("port", obj.getPort());
		
		return values;
	}

}
