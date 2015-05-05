package com.example.madeeha.smarttampon.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.example.madeeha.smarttampon.app.Day;
import com.example.madeeha.smarttampon.app.FlowDatabase;
import com.example.madeeha.smarttampon.app.madeehaDate;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Madeeha on 4/30/2015.
 */
public class DatabaseTest extends AndroidTestCase {

    private FlowDatabase db;
    private int[] ids = new int[4];
    private int[][] months = {{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0}};

    public void setUpDB() {
        db = new FlowDatabase(mContext);

        db.deleteAll();

        Day one = new Day(1993,1,1);
        ids[0] = 19930101;

        Day two = new Day(2001,5,7);
        ids[1] = 20010507;

        Day three = new Day(2015,3,31);
        three.setTimeFull(Time.valueOf("05:00:00"));
        three.setFertile(true);
        ids[3] = 20150331;

        Day four = new Day(2007,12,20);
        ids[2] = 20071220;

        one.setOnPeriod(2);
        two.setTimeFull(new Time((long)30030));
        two.setOnPeriod(2);
        four.setOnPeriod(true);
        four.setFertile(true);

        db.addDay(one);
        db.addDay(two);
        db.addDay(three);
        db.addDay(four);



        for (int i=0; i<4; i++) {
            assertNotNull(db.getDay(ids[i]));
        }

        for (int i=1; i<13;i++){
            int id = 2015*10000+i*100+1;

            Random rand = new Random();

            int period_len = rand.nextInt((7 - 3) + 1) + 3;
            int cycle_len = rand.nextInt((45 - 21) + 1) + 21;
            months[i] = new int[] {id,period_len,cycle_len};
            db.addMonthData(id,period_len,cycle_len);
        }
    }


    public void testAddDayGetDay(){
        setUpDB();

        int id = ids[3];
        Day d2 = db.getDay(id);

        assertNotNull(d2);
        assertEquals(id,d2.getDBkey());
        assertEquals(Time.valueOf("05:00:00"),d2.getTimeFull());
        assertEquals(Time.valueOf("05:00:00").getTime(),d2.getTimeFullLong());
        assertEquals(true,d2.isFertile());
        assertEquals(1,d2.isFertileInt());
        assertEquals(false,d2.isOnPeriod());
        assertEquals(0,d2.isOnPeriodInt());
    }

    // Get All Days
    public void testGetAllDays() {
        setUpDB();

        List<Day> res = db.getAllDays();

        assertEquals(4,res.size());
        assertEquals(ids[0],res.get(0).getDBkey());
        assertEquals(ids[1],res.get(1).getDBkey());
        assertEquals(ids[2],res.get(2).getDBkey());
        assertEquals(ids[3],res.get(3).getDBkey());

        //TODO: are we assuming that just checking IDs is enough?
    }

    // Get All Date Range
    public void testGetDayRange() {
        setUpDB();
        int start = ids[1];
        int end = 20080101;

        List<Day> res = db.getDayRange(start,end);

        assertEquals(2,res.size());
        assertEquals(ids[1],res.get(0).getDBkey());
        assertEquals(ids[2],res.get(1).getDBkey());

    }

    // Get All Days When On Period
    public void testGetAllPeriodDays() {
        setUpDB();
        List<Day> res = db.getAllPeriodDays();

        assertEquals(res.size(),3);
        assertEquals(res.get(0).getDBkey(),ids[0]);
        assertEquals(res.get(1).getDBkey(), ids[1]);
    }

    // Updating single day
    public void testUpdateDay() {
        setUpDB();

        Day three = new Day(2015,3,31);

        three.setFertile(false);
        three.setOnPeriod(true);

        Day original = db.getDay(ids[3]);
        assertNotNull(original);

        int a = db.updateDay(three);

        Day res = db.getDay(ids[3]);
        //TODO: this is an issue. It works about 50% of the time. And I don't know why.
        assertEquals(1,a);
        assertNotNull(res);
        assertEquals(res.isOnPeriod(),true);
        List<Day> list = db.getAllDays();
        assertEquals(4,list.size());
    }

    // Deleting single day
    public void testDeleteDay() {
        //TODO
    }

    public void testGetOldest(){
        setUpDB();
        Day res = db.getOldest();
        assertNotNull(res);
        assertEquals(ids[0],res.getDBkey());
    }

    public void testGetMonthData(){
        setUpDB();
        int march = 20150301;
        int[] res = db.getMonthData(march);
        assertNotNull(res);
        assertEquals(res[0],months[3][0]);
        assertEquals(res[1],months[3][1]);
        assertEquals(res[2],months[3][2]);
    }

    // Get All Days
    public void testGetAllMonthData() {
        setUpDB();
        List<int[]> res = db.getAllMonthData();
        assertNotNull(res);
        for (int i=0; i<12;i++){
            assertEquals(months[i+1][0],res.get(i)[0]);
            assertEquals(months[i+1][1],res.get(i)[1]);
            assertEquals(months[i+1][2],res.get(i)[2]);
        }
    }

    public void testGetClosestPeriodStartDay(){
//        String query = "SELECT * FROM FlowDB WHERE KEY_ID <" + d.getDBkey() + "AND KEY_ONPERIOD == 2 ORDER BY ROWID  DESC LIMIT 1";
//        //TODO: <= or < in query?
//        String log = "getClosestPeriodStartDay("+d.getDBkey()+")";
//
//        List<Day> result = getListFromQuery(query,log);
//        //TODO: assert that list is of length 1.
//        //grab all days before d
//        //order them by KEY_ID
//        //order them by KEY_ONPERIOD
//        //pick the one where KEY_ONPERIOD == 2
//        return result.get(0);

        setUpDB();

        Day one = new Day(1993,1,1);

        Day two = new Day(2001,5,7);

        Day one_close = new Day(1995,3,7);
        Day two_close = new Day(2015,5,13);

        Day res1 = db.getClosestPeriodStartDay(one_close);
        Day res2 = db.getClosestPeriodStartDay(two_close);

        assertEquals(19930101,res1.getDBkey());
        assertEquals(20010507,res2.getDBkey());

    }
}
