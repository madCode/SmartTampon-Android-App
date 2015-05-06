package com.example.madeeha.smarttampon.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.madeeha.smarttampon.R;

import java.util.Calendar;

public class Chart extends ActionBarActivity {

    private FlowDatabase db;
    private madeehaDate showDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        db = new FlowDatabase(getApplicationContext());
        Calendar c = Calendar.getInstance();
        try {
            showDay = new madeehaDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, 1);
            //showChart(date);
        } catch (Exception e){

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
