/*******************************************************************************
 * Copyright 2015 Proxama PLC
 ******************************************************************************/

package com.proxama.tpsdkreferenceapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.proxama.tappoint.trigger.Trigger;
import com.proxama.tappoint.trigger.Triggers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A {@link android.content.BroadcastReceiver} that receives new BLE trigger events and displays a notification relating
 * to that BLE trigger event.
 */
public class BleTriggerReceiver extends BroadcastReceiver {

    /** Log tag. */
    private static final String TAG = BleTriggerReceiver.class.getSimpleName();

    /** Action main for launching the same intent. */
    public static final String ANDROID_INTENT_ACTION_MAIN = "android.intent.action.MAIN";

    /** Intent category for launching the same intent. */
    public static final String ANDROID_INTENT_CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Triggers.ACTION_TRIGGERS_DETECTED.equals(intent.getAction())) {
            ArrayList<Trigger> triggers = intent.getParcelableArrayListExtra(Triggers.EXTRA_DETECTED_TRIGGERS);

            for (Trigger trigger : triggers) {
                sendNotificationForTrigger(context, trigger);
            }
        }
    }

    /**
     * Sends a notification with information about a trigger that has been detected by this receiver.
     *
     * @param context the application context used for sending the notification.
     * @param trigger the trigger object to send a notification for.
     */
    private void sendNotificationForTrigger(Context context, Trigger trigger) {
        String title = getTitleFromTrigger(trigger);
        String subtitle = getSubtitleFromTrigger(trigger);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context).setSmallIcon(R
                .drawable.ic_launcher).setContentTitle(title).setContentText(subtitle).setDefaults(NotificationCompat
                .DEFAULT_ALL);

        Intent intent = new Intent(context, BleTriggerActivity.class);
        intent.setAction(ANDROID_INTENT_ACTION_MAIN);
        intent.addCategory(ANDROID_INTENT_CATEGORY_LAUNCHER);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationBuilder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);
        notificationManager.notify(getCurrentTimeInMilliseconds(), notificationBuilder.build());
    }

    /**
     * Retrieves the current time in milliseconds.
     *
     * @return the current time.
     */
    private int getCurrentTimeInMilliseconds() {
        return (int) System.currentTimeMillis();
    }

    /**
     * Retrieves the title from a {@link com.proxama.tappoint.trigger.Trigger}.
     *
     * @param trigger the trigger to retrieve the content text from.
     * @return the content text if found in the trigger, or an empty string if not found.
     */
    private String getTitleFromTrigger(Trigger trigger) {
        JSONObject triggerPayload = trigger.getTriggerPayload();

        String title = "";

        try {
            JSONObject triggerData = triggerPayload.getJSONObject(BleTriggerActivity.JSON_KEY_DATA);
            title = triggerData.optString(BleTriggerActivity.JSON_KEY_TITLE);
        } catch (JSONException ex) {
            Log.d(TAG, "Unable to retrieve title from payload.");
        }

        return title;
    }

    /**
     * Retrieves the content text from a {@link com.proxama.tappoint.trigger.Trigger}.
     *
     * @param trigger the trigger to retrieve the subtitle text from.
     * @return the subtitle text if found in the trigger, or an empty string if not found.
     */
    private String getSubtitleFromTrigger(Trigger trigger) {
        JSONObject triggerPayload = trigger.getTriggerPayload();

        String subtitle = "";

        try {
            JSONObject triggerData = triggerPayload.getJSONObject(BleTriggerActivity.JSON_KEY_DATA);
            subtitle = triggerData.optString(BleTriggerActivity.JSON_KEY_SUBTITLE);
        } catch (JSONException ex) {
            Log.d(TAG, "Unable to retrieve subtitle from payload.");
        }

        return subtitle;
    }

}
