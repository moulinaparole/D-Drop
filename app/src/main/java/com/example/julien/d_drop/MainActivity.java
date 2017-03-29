package com.example.julien.d_drop;

/**
 * Created by Julien on 2017-03-28.
 */


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.cscao.apps.gmswearapi.GmsApi;


import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.sqrt;

public class MainActivity extends WearableActivity implements SensorEventListener{

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    public  static SharedPreferences prefs;
    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private Button mCallButton;
    private Button mSettingsButton;


    private SensorManager mSensorManager;
    private Sensor AccelSensor;
    private Sensor HeartSensor;
    public static Vibrator vib;// = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];

    private static final String MSG_CAPABILITY_NAME = "msg_capability";
    private final String MESSAGE1_PATH = "/p11";
    private Handler handler;
    private GmsApi gmsApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);

        handler = new Handler();
        gmsApi = new GmsApi(this, handler, MSG_CAPABILITY_NAME);

        mCallButton = (Button) findViewById(R.id.callButton);
        mCallButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(MainActivity.prefs.getBoolean("vibrate", false)){
                    vib.vibrate(300);
                }


                gmsApi.sendMsg(MESSAGE1_PATH, getString(R.string.call).getBytes());
                Intent clicky = new Intent(MainActivity.this,NurseActivity.class);
                startActivity(clicky);
            }
        });

        mSettingsButton = (Button) findViewById(R.id.settingButton);
        mSettingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent clicky = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(clicky);


            }
        });

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        AccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        HeartSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_BEAT);


        mSensorManager.registerListener(this, AccelSensor,SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,HeartSensor,SensorManager.SENSOR_DELAY_FASTEST);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


        Log.v("Nono - gravity - 0", "onAccuracyChanged: "  + String.valueOf(sensor.getName()));
    }

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private float last_bps;
    private static final int SHAKE_THRESHOLD = 1600;
    private double SMVi;

    private float xAccel;
    private float yAccel;
    private float zAccel;

    /* And here the previous ones */
    private float xPreviousAccel;
    private float yPreviousAccel;
    private float zPreviousAccel;

    /* Used to suppress the first shaking */
    private boolean firstUpdate = true;

    /*What acceleration difference would we assume as a rapid movement? */
    private final float shakeThreshold = 1.5f;

    /* Has a shaking motion been started (one direction) */
    private boolean shakeInitiated = false;


    /* If the values of acceleration have changed on at least two axises, we are probably in a shake motion */
    private boolean isAccelerationChanged() {
        float deltaX = abs(xPreviousAccel - xAccel);
        float deltaY = abs(yPreviousAccel - yAccel);
        float deltaZ = abs(zPreviousAccel - zAccel);
        return (deltaX > shakeThreshold && deltaY > shakeThreshold)
                || (deltaX > shakeThreshold && deltaZ > shakeThreshold)
                || (deltaY > shakeThreshold && deltaZ > shakeThreshold);
    }



    private void updateAccelParameters(float xNewAccel, float yNewAccel,
                                       float zNewAccel) {
                /* we have to suppress the first change of acceleration, it results from first values being initialized with 0 */
        if (firstUpdate) {
            xPreviousAccel = xNewAccel;
            yPreviousAccel = yNewAccel;
            zPreviousAccel = zNewAccel;
            firstUpdate = false;
        } else {
            xPreviousAccel = xAccel;
            yPreviousAccel = yAccel;
            zPreviousAccel = zAccel;
        }
        xAccel = xNewAccel;
        yAccel = yNewAccel;
        zAccel = zNewAccel;
    }



    public void onSensorChanged(SensorEvent event){
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.


        final float alpha = 0.8f;
        final double SMV = 50.0f;
        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];
/*
        Log.v("Nono - gravity - 0", String.valueOf(gravity[0]));
        Log.v("Nono - gravity - 1", String.valueOf(gravity[1]));
        Log.v("Nono - gravity - 2", String.valueOf(gravity[2]));

        Log.v("Nono acceleration - 0", String.valueOf(gravity[0]));
        Log.v("Nono acceleration - 1", String.valueOf(gravity[1]));
        Log.v("Nono acceleration - 2", String.valueOf(gravity[2]));
*/

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 1100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                SMVi = sqrt((linear_acceleration[0]*linear_acceleration[0])+(linear_acceleration[1]*linear_acceleration[1])+
                      (linear_acceleration[2]*linear_acceleration[2]));

                if(SMVi > SMV){
                  Intent clicky = new Intent(MainActivity.this,AlertActivity.class);
                  startActivity(clicky);
                }

                last_x = x;
                last_y = y;
                last_z = z;

                Log.v("Nono last_x", String.valueOf(last_x));
                Log.v("Nono last_y", String.valueOf(last_y));
                Log.v("Nono last_z", String.valueOf(last_z));

                /*TextView mEdit   = (TextView)findViewById(R.id.text);
                mEdit.setText("X: " + String.valueOf(last_x) + "\r\nY: " + String.valueOf(last_y) + "\r\nZ:" + String.valueOf(last_z));
                  */
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_HEART_BEAT) {
            float HeartRate = event.values[0];

            last_bps = HeartRate;
            Log.v("Nono HeartBeat", String.valueOf(last_bps));

        }
        /*TextView mEdit   = (TextView)findViewById(R.id.text);
        mEdit.setText("X: " + String.valueOf(last_x) + "\r\nY: " + String.valueOf(last_y) + "\r\nZ:" + String.valueOf(last_z) + "\r\nBPS: "+String.valueOf(last_bps));
        */
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, AccelSensor,SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,HeartSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));



        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));

        }
    }



}
