/*******************************************************************************
 * Copyright 2015 Proxama PLC
 ******************************************************************************/

package com.proxama.tpsdkreferenceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.proxama.tappoint.auth.AuthListener;
import com.proxama.tappoint.auth.Authentication;
import com.proxama.tappoint.error.ApiError;
import com.proxama.tappoint.reporting.Reporting;
import com.proxama.tappoint.reporting.TriggerEventType;
import com.proxama.tappoint.sync.SyncListener;
import com.proxama.tappoint.sync.SyncResult;
import com.proxama.tappoint.sync.Synchronisation;
import com.proxama.tappoint.trigger.Trigger;
import com.proxama.tappoint.trigger.Triggers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Displays beacon events in real-time after successfully synchronising with TapPoint to get required triggers.
 */
public class BleTriggerActivity extends ActionBarActivity implements AuthListener, SyncListener,
        BeaconEventClickListener {

    /** Log tag. */
    private static final String TAG = BleTriggerActivity.class.getSimpleName();

    /** Name of the app as recognised by the TapPoint server. */
    private static final String APP_NAME = "khhellion";

    /** JSON key for the data block found within the payload of a trigger. */
    public static final String JSON_KEY_DATA = "data";

    /** JSON key for the subtitle found within the payload of a trigger. */
    public static final String JSON_KEY_SUBTITLE = "subtitle";

    /** JSON key for the title found within the payload of a trigger. */
    public static final String JSON_KEY_TITLE = "title";

    /** Spinner shown when communicating with the server. */
    private ProgressBar mProgressBar;

    /** Adapter to show beacon events. */
    private BeaconEventAdapter mBeaconAdapter;

    /** Broadcast receiver to receive beacon events. */
    private BeaconEventReceiver mBroadcastReceiver;

    /** Intent filter to listen to the specific beacon event intent. */
    private IntentFilter mBeaconEventFilter;

    /** Indicates if the receiver is attached or not. */
    private boolean isBeaconReceiverAttached;

    /** Map to link beacon titles to images. */
    private HashMap<String, Integer> mImageMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ble_trigger);
        setSupportActionBar((Toolbar) findViewById(R.id.tbToolbar));
        mProgressBar = (ProgressBar) findViewById(R.id.pbProgressIndicator);

        mProgressBar.setVisibility(View.VISIBLE);
        Authentication.getAuthManager(this).authenticate(APP_NAME, this);

        configureBeaconEventList();
        configureBeaconEventListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Here we want to register the receiver to get beacon events.
        if (!isBeaconReceiverAttached) {
            isBeaconReceiverAttached = true;
            registerReceiver(mBroadcastReceiver, mBeaconEventFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Here we want to unregister the receiver to stop receiving get beacon events.
        if (isBeaconReceiverAttached) {
            isBeaconReceiverAttached = false;
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public void onAuthSuccess() {
        Toast.makeText(this, "Auth successful", Toast.LENGTH_LONG).show();

        Synchronisation.getSyncManager(this).synchronise(this);
    }

    @Override
    public void onAuthFailure(ApiError apiError) {
        mProgressBar.setVisibility(View.INVISIBLE);

        Toast.makeText(this, "Auth failed: " + apiError.name(), Toast.LENGTH_LONG).show();
        Log.d(TAG, apiError.getErrorMessage());
    }

    @Override
    public void onSyncSuccess(SyncResult syncResult) {
        mProgressBar.setVisibility(View.INVISIBLE);

        int numberAdded = syncResult.getTriggersAdded().size();
        int numberRemoved = syncResult.getTriggersRemoved().size();

        Toast.makeText(this, "Sync successful. Added triggers: " + numberAdded + ". Removed triggers: " +
                numberRemoved, Toast.LENGTH_LONG).show();
        Triggers.getTriggersManager(this).startMonitoring();
    }

    @Override
    public void onSyncFailure(ApiError apiError) {
        mProgressBar.setVisibility(View.INVISIBLE);

        Toast.makeText(this, "Sync failed: " + apiError.name(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBeaconEventClicked(String beaconId) {
        Reporting.getReportingManager(this).reportTriggerEvent(TriggerEventType.USER_SELECTED, beaconId);
    }

    /**
     * Configures the beacon event listener that will listen for beacon events.
     */
    private void configureBeaconEventListener() {
        mBroadcastReceiver = new BeaconEventReceiver();
        mBeaconEventFilter = new IntentFilter(Triggers.ACTION_TRIGGERS_DETECTED);
    }

    /**
     * Configures the beacon event list view.
     */
    private void configureBeaconEventList() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvBeaconEventList);

        mBeaconAdapter = new BeaconEventAdapter(this, this);
        recyclerView.setAdapter(mBeaconAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(true);

        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mImageMap = new HashMap<String, Integer>();
        mImageMap.put("Beacon 1", R.drawable.image_1);
        mImageMap.put("Beacon 2", R.drawable.image_2);
        mImageMap.put("Beacon 3", R.drawable.image_3);
        mImageMap.put("Beer Gallery", R.drawable.a4);
        mImageMap.put("Multi Qlti Tap Bar", R.drawable.a8);

    }

    /**
     * Adds all beacon events that have recently been raised as a result of monitoring for beacon triggers.
     *
     * @param beacons a list of beacons to add as beacon events.
     */
    private void addBeaconEvents(ArrayList<Trigger> beacons) {
        for (Trigger beacon : beacons) {
            try {
                mBeaconAdapter.addEvent(getBeaconEvent(beacon));
                Toast.makeText(this, "New beer pub was found", Toast.LENGTH_LONG).show();

                Reporting.getReportingManager(this).reportTriggerEvent(TriggerEventType.USER_NOTIFIED,
                        beacon.getTriggerId());
            } catch (JSONException e) {
                Log.d(TAG, "Could not parse JSON from beacon.");
            }
        }
    }

    /**
     * Gets the {@link com.proxama.tpsdkreferenceapp.BeaconEvent} according to the given beacon.
     *
     * @param beacon the beacon to retrieve trigger data from.
     * @return the converted beacon event to display.
     */
    private BeaconEvent getBeaconEvent(Trigger beacon) throws JSONException {
        String triggerId = beacon.getTriggerId();
        JSONObject triggerData = beacon.getTriggerPayload().getJSONObject(JSON_KEY_DATA);

        String title = triggerData.getString("pub");

        return new BeaconEvent(triggerId,triggerData, getBeaconImageId(title));
    }

    /**
     * Returns the beacon image ID given the beacon's title.
     *
     * @param title to use for looking up the correct image resource.
     * @return the image ID used as an image resource.
     */
    private int getBeaconImageId(String title) {
        Integer imageId = mImageMap.get(title);
        return imageId == null ? R.drawable.image_1 : imageId;
    }

    /**
     * {@link android.content.BroadcastReceiver} to listen to beacon events.
     */
    private class BeaconEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Triggers.ACTION_TRIGGERS_DETECTED.equals(intent.getAction())) {
                ArrayList<Trigger> triggers = intent.getParcelableArrayListExtra(Triggers.EXTRA_DETECTED_TRIGGERS);
                addBeaconEvents(triggers);
            }
        }
    }
}
