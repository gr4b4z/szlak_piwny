# SDK Reference App

The SDK Reference App demonstrates an end-to-end integration of the TapPoint&reg; SDK into an example application. It shows how to:

1. Connect to TapPoint&reg; (Authenticate)
2. Download trigger information (Synchronise)
3. Detect trigger events and respond to them in the application (Monitor)
4. Send analytics information to TapPoint&reg; (Reporting)

Use the Reference App as a guide for integrating TapPoint&reg; into your own projects.
SDK reference documentation is also available on the [TapPoint developer portal](http://developer.tappoint.com/).

## Getting Started

### Prerequisites

The application requires Android Studio 1.0 or above. To import the project into your IDE use 'File -> Import Project'.

To get trigger events working, you will need either a physical Bluetooth beacon, or use an iPhone as a virtual beacon.

There are ready-made apps in the iTunes app store that can act as virtual beacons. That's the easiest option to get started.

If you do want to dive deeper, then Apple's [AirLocate sample](https://developer.apple.com/library/ios/samplecode/AirLocate/Introduction/Intro.html) sample code shows how to build a virtual beacon on iOS.

Whether you have a virtual or a physical beacon, configure it as follows:

| Property | Value                                  |
| :------: | -----                                  |
| UUID     | `50726f78-616d-4142-8c45-426561636f6e` |
| Major    | `1`                                    |
| Minor    | `1` for test beacon one <br>or `2` for test beacon two <br> or `3` for test beacon three |

It doesn't matter if the letters in the UUID are in upper or lower case.

### Guided tour of the code

The project is configured with the prerequisites as described in the [Getting Started Guide](http://developer.tappoint.com/android/quick-start).

## Prerequisites

TapPoint requires the following to be completed before you start integrating the TapPoint SDK:

- A TapPoint account created
- Your application has been setup on TapPoint

If you have just received access to the developer portal, then these things may be in progress. While you wait, you can install the [reference app](http://developer.tappoint.com/) to get a feel for how to integrate the TapPoint SDK.

The TapPoint SDK is IDE agnostic so you can choose your favourite IDE to create your application in. For the remainder of this guide we'll be using Android Studio. Other IDEs may require more configuration.

**Note:** You'll need an Android device running 4.3.x or higher and which also supports the Bluetooth 4.0 profile to receive beacon events. If the device doesn't have Bluetooth 4.0, or is running a version of Android lower than 4.3.x it will still be able to synchronize data.

## Installing the SDK

Download the SDK from [here](http://developer.tappoint.com/).

Add the following jars to your Android application by copy and pasting them into the libs folder of your Android project:

* tappoint-sdk-2.x.x.jar
* dagger-1.1.0.jar
* dagger-compiler-1.1.0.jar
* javawriter.jar
* javax.inject.jar
* core-1.4.4.jar (SegmentIO SDK)

Update your `AndroidManifest.xml` file by adding the following permissions:

    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.WAKE_LOCK"/>

Inside the `<application>` block add the following lines:

    <uses-feature
        android:name="android.hardware.bluetooth_le"
        android:required="true"/>

    <service android:name="com.proxama.tappointauth.sync.SyncService"/>
    <service android:name="com.proxama.trigger.ble.BleTriggerService"/>
    <service android:name="com.proxama.ble.BleScannerManagerImpl"></service>

    <receiver android:name="com.proxama.ble.BleScannerBroadcastReceiver">
        <intent-filter>
            <action android:name="com.proxama.ble.scanner.ACTION_SCHEDULE_SCAN"></action>
        </intent-filter>
        <intent-filter>
            <action android:name="android.bluetooth.adapter.action.STATE_CHANGED"/>

        </intent-filter>
        <intent-filter>
            <action android:name="android.intent.action.BOOT_COMPLETED" />
        </intent-filter>
        <intent-filter>
            <action android:name="android.intent.action.QUICKBOOT_POWERON" />
        </intent-filter>
    </receiver>

    <receiver android:name="com.proxama.trigger.ble.BleScanEventReceiver"
              android:exported="false" >
        <intent-filter>
            <action android:name="com.proxama.ble.scanner.ACTION_SCAN_COMPLETE" />
        </intent-filter>
    </receiver>

    <provider
        android:name="com.proxama.trigger.ble.model.dao.BleTriggerProvider"
        android:authorities="com.proxama.tpsdkreferenceapp.bletriggerprovider"
        android:exported="false">
    </provider>

    <receiver android:name="com.proxama.tpsdkreferenceapp.BleTriggerReceiver">
        <intent-filter>
            <action android:name="com.proxama.tappoint.action.ACTION_TRIGGERS_DETECTED"/>

        </intent-filter>
    </receiver>
    <receiver android:name="com.proxama.tappoint.internal.trigger.module.BleTriggerEventReceiver" >
        <intent-filter>
            <action android:name="com.proxama.trigger.ble.processor.TRIGGEREVENTS" />
        </intent-filter>
    </receiver>

**Note:** You will need to update the `BleTriggerProvider` authority to use your full app package name. For example, if your package was *com.example.app* then you need to declare the following:

    android:authorities="com.example.app.bletriggerprovider"

## Using the SDK

The TapPoint SDK has a set of APIs that you can use to:

1. Authenticate your application with the TapPoint server
2. Download trigger data (a process called syncing)
3. Monitor for triggers (scanning for beacons and being informed when a beacon is found)
4. Send reporting events to TapPoint

An app containing examples of all these APIs is available to download [here](http://developer.tappoint.com/) which also demonstrates end-to-end integration of the TapPoint SDK. For more information the TapPoint SDK API can be found [here](http://developer.tappoint.com/android/api-docs/). 

**Note:** All API calls made are *asynchronous* and when they complete, will always return to the **UI** thread.

### Authenticating with TapPoint

To ensure your application can retrieve and interact with triggers, you should make sure that you have authenticated before interacting with any other API.

Import the following classes to authenticate with TapPoint:

	import com.proxama.tappoint.auth.AuthListener;
	import com.proxama.tappoint.auth.Authentication;
	import com.proxama.tappoint.error.ApiError;

It is recommended that you call this API in your Activity's `onCreate` method. Pass in the application name provided by Proxama and a listener to be informed of the result. To request additional applications, please contact <support@proxama.com>.

Call the [`authenticate`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/auth/AuthManager.html) method on the [`AuthManager`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/auth/AuthManager.html) to authenticate with TapPoint:

    Authentication.getAuthManager(this).authenticate(APP_NAME, this);
    
This API should be called every time your application launches to ensure that other APIs being called can be used. Implement the [`AuthListener`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/auth/AuthListener.html) interface to determine the result of the authentication request:

    @Override
    public void onAuthSuccess() {
        Toast.makeText(this, "Auth successful", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthFailure(ApiError apiError) {
        Toast.makeText(this, "Auth failed: " + apiError.name(), Toast.LENGTH_LONG).show();
        Log.d(TAG, apiError.getErrorMessage());
    }

### Synchronising Triggers

Once you have successfully authenticated with TapPoint, the next step is to retrieve your trigger data from TapPoint. Firstly you'll need to add trigger data to TapPoint, this is done by using the [TapPoint Beacon Payload Editor](http://connect.tappoint.com/payload/). Synchronising with TapPoint ensures that the trigger data stored on the device is up-to-date with the latest campaign data stored on TapPoint for your app.

Import the following classes to synchronise trigger data:

	import com.proxama.tappoint.sync.SyncListener;
	import com.proxama.tappoint.sync.SyncResult;
	import com.proxama.tappoint.sync.Synchronisation;

Call the [`synchronise`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/sync/SyncManager.html) method on the [`SyncManager`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/sync/SyncManager.html) to sync the trigger data from TapPoint:

    Synchronisation.getSyncManager(this).synchronise(this);

Implement the [`SyncListener`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/sync/SyncListener.html) interface to determine the result of the synchronisation request:
    
    @Override
    public void onSyncSuccess(SyncResult syncResult) {
        int numberAdded = syncResult.getTriggersAdded().size();
        int numberRemoved = syncResult.getTriggersRemoved().size();

        Toast.makeText(this, "Sync successful. Added triggers: " + numberAdded + ". Removed triggers: " +
                numberRemoved, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSyncFailure(ApiError apiError) {
        Toast.makeText(this, "Sync failed: " + apiError.name(), Toast.LENGTH_LONG).show();
    }
    
Upon successful synchronisation with TapPoint, the success callback provides a [`SyncResult`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/sync/SyncResult.html) object that contains arrays of both triggers that have been added, and triggers that have been removed during this synchronisation. This is purely for your information, the triggers will be automatically stored by the TapPoint SDK. In the instance where synchronisation has failed, you can inspect the [`ApiError`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/error/ApiError.html) object to find out the reason for failure where both an error code and error description is provided.

#### Synchronisation Strategy

Once triggers have been successfully synced onto the device, you do not have to sync again, unless the trigger data on TapPoint has changed (e.g. a new campaign has started). This means your application can monitor for triggers **without** needing a network connection.

A simple strategy would be to perform a synchronisation with TapPoint every time the application launches but it is up to you to choose a strategy that fits your needs.

### Trigger Monitoring

This API provides a way to monitor the triggers that have been synchronised from TapPoint. In order to monitor for triggers, you should ensure that you have added some trigger data to TapPoint, see [TapPoint Beacon Payload Editor](https://connect.tappoint.com/login/?next=/payload/), and they have been synchronised to your device. Once this has been completed, you can then start monitoring these triggers to be notified when a trigger event occurs.

Import the following class to start and stop monitoring for triggers:

	import com.proxama.tappoint.trigger.Triggers;
                                               
Call [`startMonitoring`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/trigger/TriggersManager.html) on the [`TriggersManager`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/trigger/TriggersManager.html) to begin scanning for triggers:
    
    Triggers.getTriggersManager(this).startMonitoring();

**Note:** The UUID for a trigger is always the same, the trigger major and minors are assigned to you and should be taken from the [TapPoint Beacon Payload Editor](http://connect.tappoint.com/payload/).

**Note:** If you are monitoring for trigger events, any new triggers subsequently added will automatically be monitored.
    
**Note:** Trigger events can only be detected and notifications sent when the user has both Bluetooth turned on and support for Bluetooth 4.0. If the user should turn off Bluetooth scanning will pause and automatically start monitoring once it is turned on again. There is no need to call `startMonitoring` again to to resume monitoring.
    
If you wish to stop monitoring for trigger events, call the `stopMonitoring` method:

    Triggers.getTriggersManager(this).stopMonitoring();

### Receiving Trigger Events

The TapPoint SDK delivers your payload data by sending a broadcast message to your application when an event is triggered (e.g. coming in range of a beacon). Using this mechanism rather than a callback listener allows your application to be notified of a trigger event even when it is closed.

There are two main ways your app could receive beacon trigger events. Either by using a dynamic broadcast receiver, or declaring a broadcast receiver in the manifest. The first method will enable events to be received when the app is open, the latter will ensure that events are received even when the app is closed.

#### Receiving events in the foreground
To dynamically receive beacon events, import the following classes to receive notifications when the application is open:

	import android.content.BroadcastReceiver;
	import android.content.Context;
	import android.content.Intent;
	import android.content.IntentFilter;

	import com.proxama.tappoint.trigger.Trigger;

To listen for trigger events:

    private class BeaconEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Triggers.ACTION_TRIGGERS_DETECTED.equals(intent.getAction())) {
                ArrayList<Trigger> triggers = intent.getParcelableArrayListExtra(Triggers.EXTRA_DETECTED_TRIGGERS);
                Log.d(TAG, "Received " + triggers.size() + " events.");
            }
        }
    }
   
Then enable and disable the `BroadcastReceiver` in `onResume` and `onPause`, respectively. Make sure that the receiver is listening for the correct action:
	
	com.proxama.tappoint.action.ACTION_TRIGGERS_DETECTED

#### Receiving events in the background

If it is required to receive beacon events even when the app is closed, a `BroadcastReceiver` can be declared in the manifest. This will ensure that your receiver will have access to every beacon event when they happen. To receive beacon events, add the intent filter to your receiver in the manifest:

	<receiver android:name="com.proxama.tpsdkreferenceapp.BleTriggerReceiver">
	    <intent-filter>
	        <action android:name="com.proxama.tappoint.action.ACTION_TRIGGERS_DETECTED"/>
        </intent-filter>
	</receiver>

Then handle the beacon events in your `BroadcastReceiver`:

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Triggers.ACTION_TRIGGERS_DETECTED.equals(intent.getAction())) {
		    ArrayList<Trigger> triggers = intent.getParcelableArrayListExtra(Triggers.EXTRA_DETECTED_TRIGGERS);
	
		    Log.d(TAG, "Received " + triggers.size() + " events.");
		}
	}

#### Handling beacon events

The list of triggers passed back to you will contain the original custom payload you supplied at the time of creation. From this, you will be able to process each trigger in the way best suited for your app.

### Reporting

The SDK provides the ability to send reporting events so that valuable app interaction can be recorded. This allows you to further understand your user behaviour when interacting with a proximity campaign. Integrating the API will allow you to capture a user's engagement with a trigger event. You can report on the following events:

| Event type | Description |
| ---------- | ----------- |
| [`USER_NOTIFIED`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/reporting/TriggerEventType.html) | The user has been notified of the occurrence a trigger event, for example via a visual notification or some other UI element within the application. |
| [`USER_SELECTED`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/reporting/TriggerEventType.html) | After being notified about the occurrence of a trigger event, the user has chosen to find out more information. |

To report on events import the following classes:

	import com.proxama.tappoint.reporting.Reporting;
	import com.proxama.tappoint.reporting.TriggerEventType;

In this example we use the [`ReportingManager`](http://developer.tappoint.com/android/api-docs/com/proxama/tappoint/reporting/ReportingManager.html) to report when a user chooses to find out more information about the trigger event using `USER_SELECTED`:

	Reporting.getReportingManager(this).reportTriggerEvent(TriggerEventType.USER_SELECTED, beaconId);

This will be reflected in the weekly report that is provided to your business once your application is live.


### Running the application

Build and run the application on a device - the emulator doesn't support iBeacons. Then get ready with your physical or virtual beacons.
You don't have to move - simply turn a bluetooth beacon on or off to simulate entering into or leaving a beacon's range. You should see beacon events appear on screen and log statements in the console.