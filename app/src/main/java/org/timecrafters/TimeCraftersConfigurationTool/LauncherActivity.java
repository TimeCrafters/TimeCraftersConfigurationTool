package org.timecrafters.TimeCraftersConfigurationTool;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.PermissionsRequestDialog;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LauncherActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_PERMISSION = 70;
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

        if (havePermissions()) {
            if (Backend.instance() == null) {
                new Backend();
            }

            if (Backend.instance().getSettings().mobileDisableLauncherDelay) {
                startTimer(timerQuickDelay);
            } else {
                startTimer(timerDelay);
            }
        } else {
            new PermissionsRequestDialog().show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    // Permission granted
                    startTimer(timerDelayAfterPermissionRequest);
                } else {
                    // Permission not given
                    new PermissionsRequestDialog().show(getSupportFragmentManager(), null);
                }
            }
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

    private boolean havePermissions() {
        return ContextCompat.checkSelfPermission(LauncherActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
    }

    public void requestStoragePermissions() {
        ActivityCompat.requestPermissions(LauncherActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_PERMISSION);
    }
}