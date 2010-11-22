package de.syncdroid.db.service.impl;

import android.content.ContentValues;
import android.database.Cursor;
import de.syncdroid.db.model.Location;

public class LocationServiceImpl extends AbstractServiceImpl<Location> {

	@Override
	protected Location read(Cursor cursor) {
		Location obj = new Location();
		obj.setName(cursor.getString(cursor.getColumnIndex("name")));
		return obj;
	}

	@Override
	protected ContentValues write(Location obj) {
		ContentValues values = new ContentValues();
		values.put("id", obj.getId());
		values.put("name", obj.getName());
		return values;
	}

	@Override
	protected String getTableName() {
		return "locations";
	}

}
