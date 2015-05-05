package com.example.madeeha.smarttampon.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Madeeha on 5/4/2015.
 */
public class madeehaDate {


    //get Int representation
//store day of week
//year, month, day
//before and after comparators
    private int month;
    private int year;
    private int date;
    private int dayOfWeek;

    public boolean isValid() {
        return valid;
    }

    private boolean valid = true; //TODO: remove once error-ing has been solidified

    public madeehaDate (int year, int month, int date)throws Exception {
        this.year = year;
        if (month >0 && month <= 12){
            this.month=month;
            if (validDate(year,month,date)){
                this.date = date;
            } else {
                valid = false;
                throw new Exception("date not valid");
            }
        } else {
            valid = false;
            //TODO: throw an error
        }
        if (valid) {
            this.dayOfWeek = setDayOfWeek();
        }
    }

    public int getIntRepresentation(){
        int res = year*10000 + month*100 + date;
        return res;
    }

    public boolean isBefore(madeehaDate d){
        if (this.getYear() == d.getYear()){
            if (this.getMonth() == d.getMonth()){
                return  this.getDate() < d.getDate();
            } else {
                return this.getMonth() < d.getMonth();
            }
        } else {
            return this.getYear() < d.getYear();
        }
    }

    public boolean isAfter(madeehaDate d){
        if (this.getYear() == d.getYear()){
            if (this.getMonth() == d.getMonth()){
                return  this.getDate() > d.getDate();
            } else {
                return this.getMonth() > d.getMonth();
            }
        } else {
            return this.getYear() > d.getYear();
        }
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getDate() {
        return date;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    private int setDayOfWeek() {
        int[] months = {-1, 0, 3, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5};

        int a = getCenturyCode(year);
        int b = year % 100;//last two digits of year
        int c = (int) Math.floor(year / 4);
        int d = months[month];
        int e = a + b + c + d;
        int f = e % 7;
        return f;

        //from http://java.dzone.com/articles/algorithm-week-how-determine
    }

    private int getCenturyCode(int year){
        if (1700 <= year && year <= 1799) {
            return 4;
        }
        if (1800 <= year && year <= 1899) {
            return 2;
        }
        if (1900 <= year && year <= 1999) {
            return 0;
        }
        if (2000 <= year && year <= 2099) {
            return 6;
        }
        if (2100 <= year && year <= 2199) {
            return 4;
        }
        if (2200 <= year && year <= 2299) {
            return 2;
        }
        if (2300 <= year && year <= 2399) {
            return 0;
        }
        if (2400 <= year && year <= 2499) {
            return 6;
        }
        if (2500 <= year && year <= 2599) {
            return 4;
        }
        if (2600 <= year && year <= 2699) {
            return 2;
        }
        return -1;
    }


    private boolean validDate(int year, int month, int date){
        if (date < 1){
            return false;
        }
        if (month == 2){
            if (year%100==0){ //if centennial year
                if (year%400==0){ //if divisible by 400
                    return date <= 29; //then a leap year
                } else {
                    return date <= 28; //otherwise not
                }
            }
            if (year%4 == 0){ //if not centennial year, and divisible by 4
                return date <= 29; //leap year
            }
            return date <= 28; //otherwise not a leap year
        }
         else if (month == 9 || month == 6 || month == 4 || month == 11){
            return date <= 30;
        } else {
            return date <= 31;
        }
    }

    public static madeehaDate today(){
        try {
        Calendar c = Calendar.getInstance();
        return new madeehaDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DATE));
        } catch(Exception e){
            Log.e("today()",e.getMessage());
        }
        return null;
    }
}
