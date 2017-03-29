package com.example.julien.d_drop;

/**
 * Created by Julien on 2017-03-28.
 */


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

public class NotNurseActivity extends WearableActivity{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.nurse_notalerted);



        ImageButton next;
        next = (ImageButton) findViewById(R.id.imageButton);

        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(MainActivity.prefs.getBoolean("vibrate", false)){
                    MainActivity.vib.vibrate(300);
                }

                Intent clicky = new Intent(NotNurseActivity.this,MainActivity.class);
                startActivity(clicky);
            }
        });
    }
}