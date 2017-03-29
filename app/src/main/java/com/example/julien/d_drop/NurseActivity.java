package com.example.julien.d_drop;


import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Julien on 2017-03-27.
 */

public class NurseActivity extends WearableActivity{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nurse_alerted);

        ImageButton next;
        next = (ImageButton) findViewById(R.id.imageButton);

        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(MainActivity.prefs.getBoolean("vibrate", false)){
                    MainActivity.vib.vibrate(300);
                }

                Intent clicky = new Intent(NurseActivity.this,MainActivity.class);
                startActivity(clicky);
            }
        });
    }
}
