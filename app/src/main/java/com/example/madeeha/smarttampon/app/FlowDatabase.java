package com.example.madeeha.smarttampon.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.sql.Time;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Madeeha on 4/30/2015.
 */
public class FlowDatabase extends SQLiteOpenHelper {

    // Days table name
    private static final String TABLE_FLOW = "flow";

    // Days Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_NUM_FILLED = "totalFill";
    private static final String KEY_TOTAL_FILL = "numFill";

    private static final String KEY_ONPERIOD = "onPeriod";
    private static final String KEY_FERT = "fertile";

    private static final String[] COLUMNS = {KEY_ID,KEY_TIME,KEY_ONPERIOD,KEY_FERT};


    // Days table name
    private static final String TABLE_PERIOD = "monthlydata";

    // Days Table Columns names
    private static final String KEY_PERIOD_ID = "id";
    private static final String KEY_PERIOD_LEN = "period_len";
    private static final String KEY_CYCLE_LEN = "cycle_len";

    private static final String[] COLUMNS_PERIOD = {KEY_PERIOD_ID, KEY_PERIOD_LEN, KEY_CYCLE_LEN};
    
    // Database Version
    private static final int DATABASE_VERSION = 5;
    // Database Name
    private static final String DATABASE_NAME = "FlowDB";

    private final String CREATE_FLOW_TABLE = "CREATE TABLE "+ TABLE_FLOW +" ( " +
            "id INTEGER PRIMARY KEY, " +
            "startTime INTEGER, "+
            "endTime INTEGER, "+
            "totalFill INTEGER, "+
            "numFill INTEGER, "+
            "onPeriod INTEGER, "+
            "fertile INTEGER )";

    // create flow table


    private final String CREATE_PERIOD_TABLE = "CREATE TABLE "+TABLE_PERIOD+" ( " +
            "id INTEGER PRIMARY KEY, " +
            "period_len INTEGER, "+
            "cycle_len INTEGER )";

    public FlowDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FLOW_TABLE);
        db.execSQL(CREATE_PERIOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older flow table if existed
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FLOW);

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PERIOD);

        // create fresh flow table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------

    /**
     * CRUD operations (create "add", read "get", update, delete) day + get all flow + delete all flow
     */



    public void addDay(Day day){
        Log.d("addDay", day.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        Log.i("addDay","day key = " + day.getDBkey());
        values.put(KEY_ID, day.getDBkey());

        values.put(KEY_TIME, day.startTime);
        values.put(KEY_END_TIME, day.fillTime);
        values.put(KEY_TOTAL_FILL, day.totalFillTime);
        values.put(KEY_NUM_FILLED, day.numTimesFilled);

        values.put(KEY_ONPERIOD, day.isOnPeriodInt());
        values.put(KEY_FERT, day.isFertileInt());

        // 3. insert
        db.insert(TABLE_FLOW, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Day getDay(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_FLOW, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        int rows = cursor.getCount();
        // 4. build day object
        //TODO:sanitize the outputs. Maybe I should have stored them as blobs?
        Day newDay = null;
        if (rows > 0) {
            Log.i("getDay("+id+")",cursor.getString(0));
            int year=Integer.parseInt(cursor.getString(0).substring(0,4));
            int month=Integer.parseInt(cursor.getString(0).substring(4,6));
            int day=Integer.parseInt(cursor.getString(0).substring(6,8));
            newDay = new Day(year,month,day);
            newDay.numTimesFilled = Integer.parseInt(cursor.getString(4));
            newDay.fillTime = Integer.parseInt(cursor.getString(2));
            newDay.startTime = Integer.parseInt(cursor.getString(1));
            newDay.totalFillTime = Integer.parseInt(cursor.getString(3));

            newDay.setOnPeriod(Integer.parseInt(cursor.getString(5)));
            newDay.setFertile(Integer.parseInt(cursor.getString(6)));

            Log.d("getDay(" + id + ")", newDay.toString());
        }


        // 5. return day
        return newDay;
    }

    // Get All Days
    public List<Day> getAllDays() {
        List<Day> flow = new LinkedList<Day>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_FLOW;

        String log = "getAllDays()";

        return getListFromQuery(query,log);
    }

    // Get All Date Range
    public List<Day> getDayRange(int start, int end) {
        List<Day> flow = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_FLOW + " WHERE " + KEY_ID + " <= " + end
                + " AND " + KEY_ID + " >= " + start;

        String log = "getDayRange()";
        return getListFromQuery(query,log);
    }

    public List<Day> getAllPeriodDays() {
        List<Day> flow = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_FLOW + " WHERE " + KEY_ONPERIOD + " >= 1";
        String log = "getAllPeriodDays()";

        return getListFromQuery(query,log);
    }

    // Get All Days When On Period
    public List<Day> getAllPeriodDaysInDayRange(int start, int end) {
        List<Day> flow = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_FLOW + " WHERE " + KEY_ID + " < " + end
                + " AND " + KEY_ID + " >= " + start+ " AND "+ KEY_ONPERIOD + " >= 1";
        String log = "getAllPeriodDays()";

        return getListFromQuery(query,log);
    }

    // Updating single day
    public int updateDay(Day d) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, d.getDBkey());

        values.put(KEY_TIME, d.startTime);
        values.put(KEY_END_TIME, d.fillTime);
        values.put(KEY_TOTAL_FILL, d.totalFillTime);
        values.put(KEY_NUM_FILLED, d.numTimesFilled);

        values.put(KEY_ONPERIOD, d.isOnPeriodInt());
        values.put(KEY_FERT, d.isFertileInt());

        // 3. updating row
        int i = db.update(TABLE_FLOW, //table
                values, // column/value
                KEY_ID+" = ?", // selections
//                null);
                new String[] { String.valueOf(d.getDBkey()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single day
    public void deleteDay(Day d) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_FLOW,
                KEY_ID+" = ?",
                new String[] { String.valueOf(d.getDBkey()) });

        // 3. close
        db.close();

        Log.d("deleteDay", d.toString());

    }

    public Day getOldest(){
        String query = "SELECT * FROM " + TABLE_FLOW + " ORDER BY ROWID ASC LIMIT 1";
        String log = "getOldest()";

        //TODO: assert that list is of length 1.

        return getListFromQuery(query,log).get(0);
        //TODO: return earliest day and most recent day
    }

    public List<Day> getListFromQuery(String query, String log){
        List<Day> flow = new LinkedList<>();

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build day and add it to list
        Day newDay = null;
        int rows = cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                // 4. build day object
                //TODO:sanitize the outputs. Maybe I should have stored them as blobs?
                if (rows>0) {
                    Log.i("getAll()",cursor.getString(0));
                    int year=Integer.parseInt(cursor.getString(0).substring(0,4));
                    int month=Integer.parseInt(cursor.getString(0).substring(4,6));
                    int day=Integer.parseInt(cursor.getString(0).substring(6,8));
                    newDay = new Day(year,month,day);
                    newDay.numTimesFilled = Integer.parseInt(cursor.getString(4));
                    newDay.fillTime = Integer.parseInt(cursor.getString(2));
                    newDay.startTime = Integer.parseInt(cursor.getString(1));
                    newDay.totalFillTime = Integer.parseInt(cursor.getString(3));

                    newDay.setOnPeriod(Integer.parseInt(cursor.getString(5)));
                    newDay.setFertile(Integer.parseInt(cursor.getString(6)));

                    Log.d("getAll()", newDay.toString());
                }

                // Add day to flow
                flow.add(newDay);
            } while (cursor.moveToNext());
        }

        Log.d(log, flow.toString());

        // return flow
        return flow;
    }


    public void addMonthData(int id, int period_len, int cycle_len){
        Log.d("addMonth", "("+ id +","+ period_len +","+ cycle_len + ")");
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_PERIOD_ID, id);
        values.put(KEY_PERIOD_LEN, period_len);
        values.put(KEY_CYCLE_LEN, cycle_len);

        // 3. insert
        long worked = db.insert(TABLE_PERIOD, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        long a = worked;
        // 4. close
        db.close();
    }

    public int[] getMonthData(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_PERIOD, // a. table
                        COLUMNS_PERIOD, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build day object
        int rows = cursor.getCount();
        // 4. build day object
        //TODO:sanitize the outputs. Maybe I should have stored them as blobs?
        if (rows > 0) {
            int day = Integer.parseInt(cursor.getString(0));
            int period_len = Integer.parseInt(cursor.getString(1));
            int cycle_len = Integer.parseInt(cursor.getString(2));

            Log.d("getMonthData(" + id + ")", "(" + id + "," + period_len + "," + cycle_len + ")");

            // 5. return day
            int[] done = {day, period_len, cycle_len};
            return done;
        }
        return new int[]{-1,-1,-1};
    }

    // Get All Days
    public List<int[]> getAllMonthData() {
        List<int[]> flow = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_PERIOD;

        String log = "getAllMonthData()";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build day and add it to list
        if (cursor.moveToFirst()) {
            do {
                // 4. build day object
                //TODO:sanitize the outputs. Maybe I should have stored them as blobs?
                int day = Integer.parseInt(cursor.getString(0));
                int period_len = Integer.parseInt(cursor.getString(1));
                int cycle_len = Integer.parseInt(cursor.getString(2));


                // 5. return day
                int[] done = {day,period_len,cycle_len};
                // Add day to flow
                flow.add(done);
            } while (cursor.moveToNext());
        }

        Log.d(log, flow.toString());

        // return flow
        return flow;
    }

    public Day getClosestPeriodStartDay(Day d){
        String query = "SELECT * FROM "+TABLE_FLOW+" WHERE "+KEY_ID+" < " + d.getDBkey() + " AND "+KEY_ONPERIOD+" == 2 ORDER BY ROWID  DESC LIMIT 1";
        //TODO: <= or < in query?
        String log = "getClosestPeriodStartDay("+d.getDBkey()+")";

        List<Day> result = getListFromQuery(query,log);
        //TODO: assert that list is of length 1.
        //grab all days before d
        //order them by KEY_ID
        //order them by KEY_ONPERIOD
        //pick the one where KEY_ONPERIOD == 2
        return result.get(0);
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(TABLE_FLOW, null, null);
        db.delete(TABLE_PERIOD, null, null);
    }


}
