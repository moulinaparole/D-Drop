package com.example.julien.d_drop;

/**
 * Created by Julien on 2017-03-28.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Julien on 2017-03-22.
 */

public class AlertActivity extends Activity {

    private ProgressBar alertProg;
    private TextView countDown;
    Button helpBut;
    Button fineBut;
    CountDownTimer countMe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_layout);

        alertProg = (ProgressBar)findViewById(R.id.progressBar2);
        alertProg.setMax(30000);


        helpBut = (Button) findViewById(R.id.nurseAlertBut);
        helpBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(MainActivity.prefs.getBoolean("vibrate", false)){
                    MainActivity.vib.vibrate(300);

                }
                countMe.cancel();
                Intent clicky = new Intent(AlertActivity.this,NurseActivity.class);
                startActivity(clicky);
            }
        });

        fineBut = (Button) findViewById(R.id.amFineBut);
        fineBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(MainActivity.prefs.getBoolean("vibrate", false)){
                    MainActivity.vib.vibrate(300);
                }
                countMe.cancel();
                Intent clicky = new Intent(AlertActivity.this,NotNurseActivity.class);
                startActivity(clicky);

            }
        });


        countMe = new CountDownTimer(30000,1000){


            public void onTick(long millisUntilFinished){

                alertProg.setProgress((int)millisUntilFinished);

            }

            public void onFinish(){
                //SEND ALERT RIGHT HERE (WHEN THE TIMER ENDS)
                Intent clicky = new Intent(AlertActivity.this,NurseActivity.class);
                startActivity(clicky);
            }
        }.start();

    }
}
