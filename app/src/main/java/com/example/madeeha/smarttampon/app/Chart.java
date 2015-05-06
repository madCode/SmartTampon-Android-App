package com.example.madeeha.smarttampon.app;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.madeeha.smarttampon.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

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
        Calendar c = Calendar.getInstance();
        try {
            showDay = new madeehaDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, 1);
            c.add(Calendar.MONTH,1);
            madeehaDate nextMonth = new madeehaDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, 1);
            showChart(showDay, nextMonth);
        } catch (Exception e){

        }

    }

    private void showChart(madeehaDate start, madeehaDate end) {
        List<Day> periodDays = db.getAllPeriodDaysInDayRange(start.getIntRepresentation(), end.getIntRepresentation());

        if (periodDays == null) {
            Log.e("showChart","nothing in database!");
        }
        XYSeries series = new XYSeries("average tampon fill time per day");

        for (int i=0; i<periodDays.size();i++){
            series.add(i,periodDays.get(i).getAverageFillTime());
        }

        // Now we create the renderer
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(Color.RED);
        // Include low and max value
        renderer.setDisplayBoundingPoints(true);
        // we add point markers
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(3);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);

        // We want to avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        // transparent margins // Disable Pan on two axis
        mRenderer.setPanEnabled(false, false);
        mRenderer.setYAxisMax(10);
        mRenderer.setYAxisMin(0);
        mRenderer.setShowGrid(true); // we show the grid

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
}
