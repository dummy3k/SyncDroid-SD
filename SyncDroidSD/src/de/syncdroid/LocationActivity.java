package de.syncdroid;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Binding;

import de.syncdroid.service.SyncService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class LocationActivity extends Activity {
	static final String TAG = "LocationActivity";

	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;
	/** Some text view we are using to show state information. */
	TextView mCallbackText;

	private ListView lv1;
	List<String> itemList = new ArrayList<String>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);
        
		Intent myIntent = new Intent(this, SyncService.class);
		myIntent.setAction(SyncService.INTENT_COLLECT_CELL_IDS);
		bindService(myIntent, mConnection, 0);
		
        lv1 = (ListView)findViewById(R.id.ListView01);
		AddItem("foo");
    }
	
    protected void onPause() {
        Log.i(TAG, "onPause()");
        super.onPause();

        try {
            Message msg = Message.obtain(null,
                    SyncService.MSG_UNREGISTER_CLIENT);
            msg.replyTo = mMessenger;
            mService.send(msg);
        } catch (RemoteException e) {
            // In this case the service has crashed before we could even
            // do anything with it; we can count on soon being
            // disconnected (and then reconnected if it can be restarted)
            // so there is no need to do anything here.
        }
}

	private void AddItem(String item) {
		itemList.add(0, item);
		ListAdapter adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, 
				itemList.toArray(new String[]{}));
		lv1.setAdapter(adapter);
		lv1.refreshDrawableState();
	}

	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	            case SyncService.FOUND_NEW_CELL:
//	                mCallbackText.setText("Received from service: " + msg.arg1);
	            	GsmCellLocation location = (GsmCellLocation)msg.obj;
	            	Log.d(TAG, "msg.arg1: " + msg.arg1);
	            	Log.d(TAG, "msg.obj: " + location);
	            	AddItem(location.toString());
	                break;
	            default:
	                super.handleMessage(msg);
	        }
	    }
	}

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className,
	            IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  We are communicating with our
	        // service through an IDL interface, so get a client-side
	        // representation of that from the raw service object.
	        mService = new Messenger(service);
//	        mCallbackText.setText("Attached.");
	        Log.d(TAG, "Attached");

	        // We want to monitor the service for as long as we are
	        // connected to it.
	        try {
	            Message msg = Message.obtain(null,
	                    SyncService.MSG_REGISTER_CLIENT);
	            msg.replyTo = mMessenger;
	            mService.send(msg);

//	            // Give it some value as an example.
//	            msg = Message.obtain(null,
//	                    SyncService.MSG_SET_VALUE, this.hashCode(), 0);
//	            mService.send(msg);
	        } catch (RemoteException e) {
	            // In this case the service has crashed before we could even
	            // do anything with it; we can count on soon being
	            // disconnected (and then reconnected if it can be restarted)
	            // so there is no need to do anything here.
	        }

	        // As part of the sample, tell the user what happened.
	        Toast.makeText(LocationActivity.this, R.string.remote_service_connected,
	                Toast.LENGTH_SHORT).show();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        mService = null;
	        mCallbackText.setText("Disconnected.");

	        // As part of the sample, tell the user what happened.
	        Toast.makeText(LocationActivity.this, R.string.remote_service_disconnected,
	                Toast.LENGTH_SHORT).show();
	    }
	};

	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because there is no reason to be able to let other
	    // applications replace our component.
	    bindService(new Intent(LocationActivity.this, 
	            SyncService.class), mConnection, Context.BIND_AUTO_CREATE);
	    mIsBound = true;
	    mCallbackText.setText("Binding.");
	}

	void doUnbindService() {
	    if (mIsBound) {
	        // If we have received the service, and hence registered with
	        // it, then now is the time to unregister.
	        if (mService != null) {
	            try {
	                Message msg = Message.obtain(null,
	                		SyncService.MSG_UNREGISTER_CLIENT);
	                msg.replyTo = mMessenger;
	                mService.send(msg);
	            } catch (RemoteException e) {
	                // There is nothing special we need to do if the service
	                // has crashed.
	            }
	        }

	        // Detach our existing connection.
	        unbindService(mConnection);
	        mIsBound = false;
	        mCallbackText.setText("Unbinding.");
	    }
	}
}
