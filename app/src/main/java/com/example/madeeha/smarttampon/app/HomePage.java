package com.example.madeeha.smarttampon.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.os.CountDownTimer;

import com.example.madeeha.smarttampon.R;

public class HomePage extends ActionBarActivity implements OnClickListener {

    private Button b;
    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    private Button startB;
    public TextView text;
    private final long startTime = 2880 * 1000;
    private final long interval = 1 * 1000;
    private long AvgTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        TextView txtTimer = (TextView) findViewById(R.id.timer_font);
        Typeface tfTimer = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
        txtTimer.setTypeface(tfTimer);

        b = (Button) findViewById(R.id.button);
        b.setOnClickListener(this);

        startB = (Button) this.findViewById(R.id.newtamponbutton);
        startB.setOnClickListener(this);
        text = (TextView) this.findViewById(R.id.timer_font);
        countDownTimer = new MyCountDownTimer(startTime, interval);
        text.setText(text.getText() + String.valueOf(startTime / 1000));
    }

    @Override
    public void onClick(View v) {
        if (v == b) {
            Intent intent = new Intent();
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            Notification noti = new Notification.Builder(this)
                    .setTicker("Your tampon is full!")
                    .setContentTitle("my.Flow")
                    .setContentText("Your tampon is full! Time to change your tampon!")
                    .setSmallIcon(R.drawable.calendarbutton)
                    .setContentIntent(pIntent).build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, noti);
        }

        if (v == startB) {
            if (!timerHasStarted) {
                countDownTimer.start();
                timerHasStarted = true;
            } else {
                countDownTimer.cancel();
                timerHasStarted = false;
            }
        }
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            text.setText("Tampon is full!");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            text.setText("" + millisUntilFinished / 1000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
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

    public void gotoCalendarPage(View view) {
        Intent intent = new Intent(this, CalendarView.class);
        startActivity(intent);
    }

}
