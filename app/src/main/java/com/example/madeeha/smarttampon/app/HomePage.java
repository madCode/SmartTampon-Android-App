package com.example.madeeha.smarttampon.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.example.madeeha.smarttampon.R;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HomePage extends Activity implements OnClickListener, BluetoothAdapter.LeScanCallback {

    private FlowDatabase db;

    private Button b;
    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    private Button newTampon;
    public TextView text;
    private long startTime;
    private long interval;
    private long newTime, savedTime;

    private Day today;


    // State machine
    final private static int STATE_BLUETOOTH_OFF = 1;
    final private static int STATE_DISCONNECTED = 2;
    final private static int STATE_CONNECTING = 3;
    final private static int STATE_CONNECTED = 4;

    private int state;

    private boolean scanStarted;
    private boolean scanning;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;

    private RFduinoService rfduinoService;

    private Button scanButton;
    private TextView connectionStatusText;
    private LinearLayout dataLayout;

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            if (state == BluetoothAdapter.STATE_ON) {
                upgradeState(STATE_DISCONNECTED);
            } else if (state == BluetoothAdapter.STATE_OFF) {
                downgradeState(STATE_BLUETOOTH_OFF);
            }
        }
    };

    private final BroadcastReceiver scanModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            scanning = (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_NONE);
            scanStarted &= scanning;
            updateUi();
        }
    };

    private final ServiceConnection rfduinoServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            rfduinoService = ((RFduinoService.LocalBinder) service).getService();
            if (rfduinoService.initialize()) {
                if (rfduinoService.connect(bluetoothDevice.getAddress())) {
                    upgradeState(STATE_CONNECTING);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            rfduinoService = null;
            downgradeState(STATE_DISCONNECTED);
        }
    };

    private final BroadcastReceiver rfduinoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (RFduinoService.ACTION_CONNECTED.equals(action)) {
                upgradeState(STATE_CONNECTED);
            } else if (RFduinoService.ACTION_DISCONNECTED.equals(action)) {
                downgradeState(STATE_DISCONNECTED);
            } else if (RFduinoService.ACTION_DATA_AVAILABLE.equals(action)) {
                addData(intent.getByteArrayExtra(RFduinoService.EXTRA_DATA));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
//        ActionBar actionBar = getActionBar().hide();
//        getActionBar().hide();

        db = new FlowDatabase(getApplicationContext());

        Calendar c = Calendar.getInstance();
        today = new Day(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DATE));

        TextView txtTimer = (TextView) findViewById(R.id.timer_font);
        Typeface tfTimer = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
        txtTimer.setTypeface(tfTimer);

        startTime = 28800 * 1000;
        interval = 1 * 1000;

        b = (Button) findViewById(R.id.button);
        b.setOnClickListener(this);

        newTampon = (Button) findViewById(R.id.newtamponbutton);
        newTampon.setOnClickListener(this);

        text = (TextView) findViewById(R.id.timer_font);
        countDownTimer = new MyCountDownTimer(startTime, interval);
        text.setText(text.getText() + String.valueOf(startTime / 1000));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Find Device
        scanButton = (Button) this.findViewById(R.id.connectbutton);
        connectionStatusText = (TextView) findViewById(R.id.connectionStatus);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanStarted = true;
                bluetoothAdapter.startLeScan(
                        new UUID[]{ RFduinoService.UUID_SERVICE },
                        HomePage.this);
            }
        });

//        // Receive
//        clearButton = (Button) findViewById(R.id.clearData);
//        clearButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dataLayout.removeAllViews();
//            }
//        });

        dataLayout = (LinearLayout) findViewById(R.id.dataLayout);
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
            noti.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(0, noti);
        }

        if (v == newTampon) {
            int worked = -1;
            today.setOnPeriod(true);
            if (!timerHasStarted) {
                countDownTimer = new MyCountDownTimer(startTime, interval);
                countDownTimer.start();
                timerHasStarted = true;
            } else {
                countDownTimer.cancel();
                timerHasStarted = false;
                newTime = savedTime;
                startTime = newTime;
                double t = 8-TimeUnit.MILLISECONDS.toHours(newTime);
                today.totalFillTime+= t;
                today.numTimesFilled+=1;

                if (db.getDay(today.getDBkey()) != null) {
                    worked = db.updateDay(today);
                    int a = 0;
                } else {
                    db.addDay(today);
                }
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
            String time = String.format("%d hr, %d min, %d sec",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
            );
            text.setText(time);
            savedTime = millisUntilFinished;
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

    public void connect(){
        Intent rfduinoIntent = new Intent(HomePage.this, RFduinoService.class);
        bindService(rfduinoIntent, rfduinoServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(scanModeReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
        registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(rfduinoReceiver, RFduinoService.getIntentFilter());

        updateState(bluetoothAdapter.isEnabled() ? STATE_DISCONNECTED : STATE_BLUETOOTH_OFF);
    }

    @Override
    protected void onStop() {
        super.onStop();

        bluetoothAdapter.stopLeScan(this);

        unregisterReceiver(scanModeReceiver);
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(rfduinoReceiver);
    }

    private void upgradeState(int newState) {
        if (newState > state) {
            updateState(newState);
        }
    }

    private void downgradeState(int newState) {
        if (newState < state) {
            updateState(newState);
        }
    }

    private void updateState(int newState) {
        state = newState;
        updateUi();
    }

    private void updateUi() {

        // Scan
        if (scanStarted && scanning) {
            Toast.makeText(getApplicationContext(), "Scanning...",
                    Toast.LENGTH_SHORT).show();
        } else if (scanStarted) {
            Toast.makeText(getApplicationContext(), "Scan Started.",
                    Toast.LENGTH_SHORT).show();
        }

        // Connect
        boolean connected = false;
        if (state == STATE_CONNECTING) {
            Toast.makeText(getApplicationContext(), "Connecting...",
                    Toast.LENGTH_SHORT).show();

        } else if (state == STATE_CONNECTED) {
            connected = true;
            Toast.makeText(getApplicationContext(), "Connected.",
                    Toast.LENGTH_SHORT).show();

        }
//        connectionStatusText.setText(connectionText);
        //TODO: show green light or something
    }

    private void addData(byte[] dataFlipped) {
        countDownTimer.cancel();
        timerHasStarted = false;
        newTime = savedTime;
        startTime = newTime;
        today.totalFillTime += TimeUnit.MILLISECONDS.toHours(newTime);
        today.numTimesFilled += 1;

        if (db.getDay(today.getDBkey()) != null) {
            db.updateDay(today);
        } else {
            db.addDay(today);
        }

        //Notification
        Intent intent = new Intent();
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification noti = new Notification.Builder(this)
                .setTicker("Your tampon is full!")
                .setContentTitle("my.Flow")
                .setContentText("Your tampon is full! Time to change your tampon!")
                .setSmallIcon(R.drawable.calendarbutton)
                .setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, noti);

    }

//        byte[] data = FlipData(dataFlipped);
//        View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, dataLayout, false);
//
//        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
////        String hex = HexAsciiHelper.bytesToHex(data);
//
//        BigInteger bi = new BigInteger(data);
//        text1.setText(bi.toString());
//
//        dataLayout.addView(
//                view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


    private byte[] FlipData(byte[] data){
        int len = data.length;
        byte[] newData = new byte[len];
        for (int i=0; i < len; i++){
            newData[len-i-1]= data[i];
        }
        return newData;
    }

    private String toBinary( byte[] bytes )
    {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    @Override
    public void onLeScan(BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        bluetoothAdapter.stopLeScan(this);
        bluetoothDevice = device;
//

        HomePage.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                deviceInfoText.setText(
//                        BluetoothHelper.getDeviceInfoText(bluetoothDevice, rssi, scanRecord));
                updateUi();
                connect();
            }
        });
    }

    public void gotoChart(View v){
        Intent intent = new Intent(this, Chart.class);
        startActivity(intent);
    }

}
