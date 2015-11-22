/*******************************************************************************
 * Copyright 2015 Proxama PLC
 ******************************************************************************/

package com.proxama.tpsdkreferenceapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Simple {@link android.support.v7.widget.RecyclerView.ViewHolder} describing the beacon event view.
 */
public class BeaconEventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /** Component to display the title. */
    private TextView mTitle;

    /** Component to display the subtitle. */
    private TextView mBeer1Name;
    private TextView mBeer2Name;
    private TextView mBeer2alc;
    private TextView mBeer2type;
    private TextView mBeer2rb;

    private TextView mBeer1alc;
    private TextView mBeer1type;


    private TextView mBeer1rb;

    /** Component to display the image. */
    private ImageView mImage;

    /** Component to display the time stamp. */
    private TextView mTimeStamp;

    /** The beacon ID, returned when clicked. */
    private String mBeaconId;

    /** Listener used to report click events. */
    private BeaconEventClickListener mListener;

    /**
     * Creates a new instance.
     *
     * @param itemView the inflated view.
     */
    public BeaconEventViewHolder(View itemView, BeaconEventClickListener listener) {
        super(itemView);
        
        mListener = listener;

        mTitle = (TextView) itemView.findViewById(R.id.pubTitle);
        mBeer2Name = (TextView) itemView.findViewById(R.id.beer2Name);
        mBeer1Name = (TextView) itemView.findViewById(R.id.beer1Name);
        mBeer1alc = (TextView) itemView.findViewById(R.id.beer1alc);
        mBeer1rb = (TextView) itemView.findViewById(R.id.beer1rb);
        mBeer1type = (TextView) itemView.findViewById(R.id.beer1type);

        mBeer2alc = (TextView) itemView.findViewById(R.id.beer2alc);
        mBeer2rb = (TextView) itemView.findViewById(R.id.beer2rb);
        mBeer2type = (TextView) itemView.findViewById(R.id.beer2type);

        mImage = (ImageView) itemView.findViewById(R.id.ivBeaconImage);

        itemView.findViewById(R.id.rlHolder).setOnClickListener(this);
    }

    /**
     * Sets the title of this view.
     *
     * @param title the title to use.
     */
    public void setTitle(String title) {
        mTitle.setText(title);
    }
    public void setBeer1Name(String name) {mBeer1Name.setText(name);    }
    public void setBeer2Name(String name) {mBeer2Name.setText(name);    }
    public void setBeer1alc(String name) {mBeer1alc.setText(name);    }
    public void setBeer1type(String name) {mBeer1type.setText(name);    }
    public void setBeer1rb(String name) {mBeer1rb.setText(name);    }
    public void setBeer2alc(String name) {mBeer2alc.setText(name);    }
    public void setBeer2type(String name) {mBeer2type.setText(name);    }
    public void setBeer2rb(String name) {mBeer2rb.setText(name);    }

    /**
     * Sets the image for view.
     *
     * @param imageResource the image resource to use.
     */
    public void setImageResource(int imageResource) {
        mImage.setImageResource(imageResource);
    }

    public void setBeaconId(String beaconId) {
        mBeaconId = beaconId;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onBeaconEventClicked(mBeaconId);
        }
    }
}
