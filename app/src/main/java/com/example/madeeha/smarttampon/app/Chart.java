package com.example.madeeha.smarttampon.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.madeeha.smarttampon.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Chart extends ActionBarActivity {

    private FlowDatabase db;
    private madeehaDate showDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        db = new FlowDatabase(getApplicationContext());
        //setUpDatabase();
        Calendar c = Calendar.getInstance();
        try {
            showDay = new madeehaDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, 1);
            c.add(Calendar.MONTH,1);
            madeehaDate nextMonth = new madeehaDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, 1);
            showChart(showDay, nextMonth);
            updateTitle(showDay);
        } catch (Exception e){

        }

    }

    private void updateTitle(madeehaDate currDate) {
        TextView t = (TextView)findViewById(R.id.title);
        TextView subt = (TextView)findViewById(R.id.subtitle);
        Typeface tfTimer = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
        t.setTypeface(tfTimer);
        subt.setTypeface(tfTimer);
        t.setText("Average Flow in "+getMonth(currDate));
        t.setTextSize(20);
        subt.setText("(hours it takes for tampon to fill)");
        subt.setTextSize(10);

    }

    private String getMonth(madeehaDate currDate) {
        switch(currDate.getMonth()){
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "Not a Month";
        }
    }

    private void showChart(madeehaDate start, madeehaDate end) {
        List<Day> periodDays = db.getAllPeriodDaysInDayRange(start.getIntRepresentation(), end.getIntRepresentation());
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);

        renderer.setColor(getResources().getColor(R.color.periodRed));
        // Include low and max value
        renderer.setDisplayBoundingPoints(true);
        // we add point markers
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(3);


        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.setXLabels(0);
        if (periodDays.size() <= 0) {
            Log.e("showChart","nothing in database!");
        }
        XYSeries series = new XYSeries("average tampon fill time per day");

        for (int i=0; i<periodDays.size();i++){
            Day a = periodDays.get(i);
            series.add(i,a.getAverageFillTime());
            mRenderer.addXTextLabel(i, getDayLabel(a.getDate().getDayOfWeek()));
        }

//        for (int i=0; i<7;i++){
//            double a = 7;
//            series.add(i,a);
//        }

        // Now we create the renderer


        mRenderer.setTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf"));
        mRenderer.addSeriesRenderer(renderer);

        // We want to avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        // transparent margins // Disable Pan on two axis
        mRenderer.setPanEnabled(false, false);
        mRenderer.setYAxisMax(10);
        mRenderer.setYAxisMin(0);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setYLabelsPadding((float)20);
        mRenderer.setYLabelsVerticalPadding((float)-20);

//        mRenderer.setXLabelsAlign(Paint.Align.RIGHT);
//        mRenderer.setYLabelsAlign(Paint.Align.RIGHT);

        mRenderer.setShowLegend(false);

        mRenderer.setZoomLimits(new double[] { 0, 10, 0, 10 });

//        mRenderer.setChartTitle("Flow (avg. hours before tampon fills)");
        mRenderer.setBackgroundColor(getResources().getColor(R.color.background));
        mRenderer.setShowGrid(true); // we show the grid
        //mRenderer.setYLabelsColor(1,Color.BLACK);

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);
        GraphicalView chartView = ChartFactory.getLineChartView(this, dataset, mRenderer);

        ((LinearLayout)findViewById(R.id.chart)).addView(chartView,0);

        //- See more at: http://www.survivingwithandroid.com/2014/06/android-chart-tutorial-achartengine.html#sthash.J1ySSia7.dpuf

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

    private void setUpDatabase(){
        db.deleteAll();

        int jan = 20150101;
        int feb = 20150201;
        int mar = 20150301;
        int apr = 20150401;
        int may = 20150501;
        db.addMonthData(jan,8,25);
        db.addMonthData(feb,7,40);
        db.addMonthData(mar,7,26);
        db.addMonthData(apr,9,33);
        db.addMonthData(may,8,0);

        List<Day> periodDays = new ArrayList<>();

        Day janD = new Day(2015,1,1);
        janD.setOnPeriod(2);

        for (int i=1;i<9;i++){
            Day p = new Day(2015,1,1+i);
            p.setOnPeriod(true);
            p.totalFillTime = 5;
            p.numTimesFilled = 1;
            periodDays.add(p);
        }

        Day febD = new Day(2015,1,26);
        febD.setOnPeriod(2);

        Day marchD = new Day(2015,3,6);
        marchD.setOnPeriod(2);

        for (int i=1;i<9;i++){
            Day p = new Day(2015,3,6+i);
            p.setOnPeriod(true);
            p.totalFillTime = 5;
            p.numTimesFilled = 1;
            periodDays.add(p);
        }

        Day aprilD = new Day(2015,4,1);
        aprilD.setOnPeriod(2);

        for (int i=1;i<9;i++){
            Day p = new Day(2015,4,1+i);
            p.totalFillTime = 5;
            p.numTimesFilled = 1;
            periodDays.add(p);
        }

        Day mayD = new Day(2015,5,4);
        mayD.setOnPeriod(2);
        mayD.totalFillTime = 5;
        mayD.numTimesFilled = 1;

        for (int i=1;i<9;i++){
            Day p = new Day(2015,5,4+i);
            p.setOnPeriod(true);
            p.totalFillTime = 5;
            p.numTimesFilled = 1;
            periodDays.add(p);
        }

        db.addDay(janD);
        db.addDay(febD);
        db.addDay(marchD);
        db.addDay(aprilD);
        db.addDay(mayD);

        for (int i=0; i<periodDays.size();i++){
            db.addDay(periodDays.get(i));
        }
    }

    public String getDayLabel(int d){
        switch(d){
            case 0:
                return "Su";
            case 1:
                return "M";
            case 2:
                return "Tu";
            case 3:
                return "W";
            case 4:
                return "Th";
            case 5:
                return "F";
            case 6:
                return "Sa";
            case 7:
                return "Su";
            default:
                return "Not a day";
        }

    }
}
