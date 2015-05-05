package com.example.madeeha.smarttampon.app;

import android.content.Context;

import java.sql.Date;
import java.util.List;

/**
 * Created by Madeeha on 4/30/2015.
 */
public class FlowCalendar {
    private FlowDatabase db;
    private List<int[]> data;
    long now;
    //TODO: maybe get table passed in?

    public FlowCalendar(Context c, FlowDatabase db){
        Date d = new Date(java.util.Calendar.getInstance().getTimeInMillis());

        this.now = d.getTime();
        this.db = db;
        this.data = db.getAllMonthData();
    }

    public int predictedLengthOfNextPeriod(){
        //TODO: look at lengths of periods so far and take weighted average.
        //TODO: should we be storing weighted averages instead of straight data and recalculating the weighted average every time?
        //more recent periods should be more indicative of how long the next one will be.
        //TODO: this will fail if user was pregnant. We probably want a pregnancy mode.
        int avg_len=0;
        for (int i=0; i<data.size(); i++){
            avg_len+=data.get(i)[1];
        }
        return avg_len/data.size();
    }

    //your cycle begins on the first day of your period.
    // So next period will begin in cycle_len-period_len days from end of last period.
    public int predictedLengthOfCycle(){
        //TODO: look at lengths of cycles so far and take weighted average
        //more recent cycles should be more indicative of how long the next one will be.
        int avg_len=0;
        for (int i=0; i<data.size(); i++){
            avg_len+=data.get(i)[2];
        }
        return avg_len/data.size();
    }
}
