/*******************************************************************************
 * Copyright 2015 Proxama PLC
 ******************************************************************************/

package com.proxama.tpsdkreferenceapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Model holding all information for a beacon event.
 */
public class BeaconEvent {

    /** Beacon event trigger ID. */
     public String mTriggerId;

    /** Beacon event title. */
     public String mTitle;

     public String mBeer1Name;
     public String mBeer2Name;
     public String mBeer2alc;
     public String mBeer2type;
     public String mBeer2rb;

     public String mBeer1alc;
     public String mBeer1type;


     public String mBeer1rb;

    /** Resource image ID. */
     public int mImageResource;

    /** Timestamp of the beacon event. */
     public String mTimeStamp;

    public BeaconEvent(String triggerId, JSONObject obj, int imageResource) {
        mTriggerId = triggerId;
        try {
            mTitle = obj.getString("pub");
            JSONArray beers = obj.getJSONArray("beers");

            mBeer1Name = beers.getJSONObject(0).getString("name") + "("+beers.getJSONObject(0).getString("brewery")+")";
            mBeer1alc = beers.getJSONObject(0).getString("alcohol");
            mBeer1rb = beers.getJSONObject(0).getString("rb");
            mBeer1type = beers.getJSONObject(0).getString("type");

            mBeer2Name =  beers.getJSONObject(1).getString("name") + "("+ beers.getJSONObject(1).getString("brewery")+")";
            mBeer2alc =  beers.getJSONObject(1).getString("alcohol");
            mBeer2rb =  beers.getJSONObject(1).getString("rb");
            mBeer2type =  beers.getJSONObject(1).getString("type");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        mImageResource = imageResource;
        mTimeStamp = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date());
    }

    /**
     * Returns the beacon event trigger ID.
     *
     * @return the trigger ID.
     */
    public String getTriggerId() {
        return mTriggerId;
    }

    /**
     * Returns the beacon event title.
     *
     * @return the title.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the beacon event subtitle.
     *
     * @return the subtitle.
     */

    /**
     * Returns the beacon event image resource ID.
     *
     * @return the resource ID.
     */
    public int getImageResource() {
        return mImageResource;
    }

    /**
     * Returns the beacon time stamp.
     *
     * @return the time of the event.
     */
    public String getTimeStamp() {
        return mTimeStamp;
    }
}
