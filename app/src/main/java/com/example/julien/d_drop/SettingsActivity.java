package com.example.julien.d_drop;


import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import static android.R.attr.id;


public class SettingsActivity extends Activity{

    TextView helloText;
    AudioManager audioManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);


        ToggleButton vibState = (ToggleButton) findViewById(R.id.toggleVib);


        vibState.setChecked(MainActivity.prefs.getBoolean("vibrate", false));
        //   audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }

    public void changeVibrateState(View view){
        boolean checked = ((ToggleButton)view).isChecked();
        SharedPreferences.Editor editor = MainActivity.prefs.edit();

        //  audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

        editor.putBoolean("vibrate", checked);
        if (checked){
            MainActivity.vib.vibrate(300);
        }


        //  audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        editor.commit();
    }
}
