package com.example.madeeha.smarttampon.app;

import android.util.Log;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Madeeha on 4/30/2015.
 */
public class Day {

    private madeehaDate date;
    private Time timeFull;

    //representing 0= not on period, 1=on period, 2=period started today
    private int onPeriod = 0;
    private int isFertile = 0;

    public Day(int year, int month, int day){
        try {
            madeehaDate d = new madeehaDate(year, month, day);
            this.date = d;
            if (date.isAfter(madeehaDate.today())) {
                PredictOnPeriod(this);
                PredictFertility(this);
            }
        } catch (Exception e){
            Log.e(e.getMessage(), "" + month + "," + day + "," + year);
        }
    }

    public Day(madeehaDate d){
        if (d.isValid()) {
            this.date = d;
            if (date.isAfter(madeehaDate.today())) {
                PredictOnPeriod(this);
                PredictFertility(this);
            }
        }
    }


    /**
     * Will return true or false depending on whether or not user is either on their period (if date is in the past) or will be on their period according to
     * our predictive algorithm (if date is in the future).
     * @return if date in past, will return whether or not user was on period on that day. if day in future, returns whether or not the algorithm thinks user will be on period that day.
     */
    public boolean isOnPeriod() {
        if (this.onPeriod >= 1){
            return true;
        } else {
            return false;
        }
    }

    public int isOnPeriodInt() {
        return this.onPeriod;
    }

    /**
     * Will return true or false depending on whether or not user is fertile according to best guess by predictive algorithm
     * @return true/false depending on predictive algorithm
    **/
    public boolean isFertile(){
        if (this.isFertile == 1){
            return true;
        } else {
            return false;
        }
    }

    public int isFertileInt() {
        return this.isFertile;
    }

    public void setOnPeriod(boolean onPeriod) {
        if (onPeriod){
            setOnPeriod(1);
        } else {
            setOnPeriod(0);
        } //TODO: this will cause collisions with periodStarted() due to poorly designed system that facilitates gross user errors
    }

    public void periodStarted(){
        setOnPeriod(2);
    }

    public void setOnPeriod(int i){
        if (i == 0){
            this.onPeriod = 0;
        } else if (i == 1){
            this.onPeriod = 1;
        } else if (i==2){
            this.onPeriod = 2;
        } else {
            //throw exception
        }
    }

    public void setFertile(boolean isFertile) {
        if (isFertile){
            this.isFertile = 1;
        } else {
            this.isFertile = 0;
        }
    }

    public void setFertile(int i){
        if (i == 0){
            this.isFertile = 0;
        } else if (i == 1){
            this.isFertile = 1;
        }
    }

    public void setTimeFull(Time timeFull) {
        this.timeFull = timeFull;
    }

    public Time getTimeFull() {
        return timeFull;
    }

    public madeehaDate getDate() {
        return date;
    }

    public int getDBkey() { return date.getIntRepresentation(); }

//    public String getDateString(){
//       return DateToString(this.date);
//    }
//
//    public String getTimeFullString(){
//        return TimeToString(this.timeFull);
//    }

//    public static String TimeToString(Time t){
//        return Long.toString(t.getTime());
//    }
//
//    public static String DateToString(Date d){
//        return Long.toString(d.getTime());
//    }

    public static void PredictOnPeriod(Day d){
        //TODO: run predictive algorithm on day and call setOnPeriod;
        //get last period day
        //get predicted cycle length
    }

    public static void PredictFertility(Day d){
        //TODO: run predictive algo
    }

    public long getTimeFullLong() {
        if (this.timeFull == null){
            return -1;
        }
        return this.timeFull.getTime();
    }

//    public static Date StringToDate(String s) {
//        return new Date(Long.parseLong(s));
//    }
//
//    public static Time StringToTime(String s) {
//        return new Time(Long.parseLong(s));
//    }
}
