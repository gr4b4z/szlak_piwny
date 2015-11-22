/*******************************************************************************
 * Copyright 2015 Proxama PLC
 ******************************************************************************/

package com.proxama.tpsdkreferenceapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link android.support.v7.widget.RecyclerView.Adapter} to display beacon events.
 */
public class BeaconEventAdapter extends RecyclerView.Adapter<BeaconEventViewHolder> {

    /** The click listener used for each view within the recycler view. */
    private BeaconEventClickListener mClickListener;

    /** List of {@link com.proxama.tpsdkreferenceapp.BeaconEvent}s to display. */
    private List<BeaconEvent> mEvents;

    /** Context used to inflate views. */
    private Context mContext;

    /**
     * Create a new instance.
     *
     * @param context the context used to inflate views.
     * @param clickListener the listener to use for handling click events.
     */
    public BeaconEventAdapter(Context context, BeaconEventClickListener clickListener) {
        mContext = context;
        mClickListener = clickListener;
        mEvents = new ArrayList<BeaconEvent>();
    }

    @Override
    public BeaconEventViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_beacon_event, null);
        return new BeaconEventViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(BeaconEventViewHolder beaconEventViewHolder, int position) {
        BeaconEvent beaconEvent = mEvents.get(position);

        beaconEventViewHolder.setBeaconId(beaconEvent.getTriggerId());
        beaconEventViewHolder.setImageResource(beaconEvent.getImageResource());
        beaconEventViewHolder.setTitle(beaconEvent.getTitle());
        beaconEventViewHolder.setBeer1Name(beaconEvent.mBeer1Name);
        beaconEventViewHolder.setBeer1alc(beaconEvent.mBeer1alc);
        beaconEventViewHolder.setBeer1type(beaconEvent.mBeer1type);
        beaconEventViewHolder.setBeer1rb(beaconEvent.mBeer1rb);
        beaconEventViewHolder.setBeer2Name(beaconEvent.mBeer2Name);
        beaconEventViewHolder.setBeer2alc(beaconEvent.mBeer2alc);
        beaconEventViewHolder.setBeer2type(beaconEvent.mBeer2type);
        beaconEventViewHolder.setBeer2rb(beaconEvent.mBeer2rb);

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    /**
     * Adds a beacon event to the list.
     *
     * @param event the event to add.
     */
    public void addEvent(BeaconEvent event) {
        mEvents.add(event);
        notifyItemInserted(mEvents.size());
    }

}
