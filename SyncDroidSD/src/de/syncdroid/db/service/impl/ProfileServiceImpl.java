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
		
		return obj;
	}
	
	protected ContentValues write(Profile obj) {
		ContentValues values = new ContentValues();
		values.put("id", obj.getId());
		values.put("name", obj.getName());
		values.put("onlyIfWifi", (obj.getOnlyIfWifi() != null 
				? obj.getOnlyIfWifi() : false) ? 1 : 0);
		
		return values;
	}

}
