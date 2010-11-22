package de.syncdroid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class LocationListActivity extends Activity {
	static final String TAG = "LocationListActivity";

	private ListView lv1;
	List<String> itemList = new ArrayList<String>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list_activity);

        lv1 = (ListView)findViewById(R.id.ListView01);
    }
	
	public void onAddNewClick(View view) {
        Log.i(TAG, "onAddNewClick()");
		Intent intent = new Intent(this, LocationActivity.class);
		startActivity(intent);
	}

}
