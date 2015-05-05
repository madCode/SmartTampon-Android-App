package com.example.madeeha.smarttampon.app;

import android.content.Context;

import java.sql.Date;
import java.util.List;

/**
 * Created by Madeeha on 4/30/2015.
 * This class will take care of incoming and past data and present it as a nice chart.
 */
public class FlowChart {

    private FlowDatabase db;

    public FlowChart(Context c, FlowDatabase db){
        this.db=db;
    }

    /**
     * get data from storage for specific time interval
     * @param start
     * @param end
     */
    private void readInData(Date start, Date end){
//        List<Day> days = db.getDayRange(start.getTime(),end.getTime());
        //TODO: do something with data
    }
    /**
     * returns a chart object with all data from start date to end date
     * @param start date data should start from. inclusive
     * @param end date data should end at. inclusive
     */
    public void showChart(Date start,Date end){

    }
}
