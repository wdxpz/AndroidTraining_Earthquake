package com.sw.tain.earthquake.Model;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by home on 2016/12/28.
 */

public class Quake {
    private Date mDate;
    private String mDetails;
    private Location mLocation;
    private double mMagnitude;
    private String mLink;

    public Quake(Date date, String details, String link, Location location, double magnitude) {
        mDate = date;
        mDetails = details;
        mLink = link;
        mLocation = location;
        mMagnitude = magnitude;
    }

    public Date getDate() {
        return mDate;
    }

    public String getDetails() {
        return mDetails;
    }

    public String getLink() {
        return mLink;
    }

    public Location getLocation() {
        return mLocation;
    }

    public double getMagnitude() {
        return mMagnitude;
    }

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("HH.mm");
        String dateString = format.format(mDate);

        return dateString + ": " + mMagnitude + " " + mDetails;
    }
}
