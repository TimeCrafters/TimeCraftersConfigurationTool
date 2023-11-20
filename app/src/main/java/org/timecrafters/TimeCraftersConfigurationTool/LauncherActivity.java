package org.timecrafters.TimeCraftersConfigurationTool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;

public class LauncherActivity extends AppCompatActivity {
//    private static final int REQUEST_WRITE_PERMISSION = 70;
    private static final String TAG = "LauncherActivity";
    private static final long timerDelay = 2_000;
    private static final long timerQuickDelay = 250; // Give LauncherActivity enough time to do first paint
    private static final long timerDelayAfterPermissionRequest = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (Backend.instance() == null) {
            new Backend(getApplicationContext());
        }

        if (Backend.instance().getSettings().mobileDisableLauncherDelay) {
            startTimer(timerQuickDelay);
        } else {
            startTimer(timerDelay);
        }
    }

    private void startTimer(long milliseconds) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, milliseconds);
    }
}