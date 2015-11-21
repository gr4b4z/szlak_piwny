package com.becons.the.piwnyszlak;

/**
 * Created by rr196081 on 21/11/2015.
 */

public class Place {
    public Place(double x, double y, String name){
        this.Location = new double[]{x,y};
        this.Name = name;
    }
    public double[] Location;
    public String Name;
    public Object BeaconId;
}