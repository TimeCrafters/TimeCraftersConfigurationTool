package org.timecrafters.TimeCraftersConfigurationTool;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;

import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.TAC;
import org.timecrafters.TimeCraftersConfigurationTool.backend.TACNET;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.TACNETOnBootReceiver;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.TACNETServerService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_tacnet, R.id.navigation_editor, R.id.navigation_settings,
                R.id.navigation_search)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        if (Backend.instance() == null) {
            new Backend();
        }

        Backend.instance().applicationContext = getApplicationContext();
        Backend.instance().mainActivity = this;

        if (Backend.instance().getSettings().mobileShowNavigationLabels) {
            navView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        }

        // Auto start TACNET server if allowed and device model contains AUTO_START_MODEL
        if (TAC.allowAutoServerStart() && Backend.instance().getServer() == null) {
            Log.i(TAG, "Detected " + Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.HARDWARE + "), starting TACNET Server Service...");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, TACNETServerService.class));
            } else {
                startService(new Intent(this, TACNETServerService.class));
            }
        }

        registerReceiver(new TACNETOnBootReceiver(), new IntentFilter(Intent.ACTION_BOOT_COMPLETED));

        if (getIntent().getBooleanExtra("navigate_to_tacnet", false)) {
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.navigation_tacnet);
        }

        startTACNETStatusIndictator();
    }

    private void startTACNETStatusIndictator() {
        final Handler handler = new Handler(getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final TACNET tacnet = Backend.instance().tacnet();
                final ActionBar actionBar = getSupportActionBar();

                if (tacnet.isConnected()) {
                    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.tacnetConnectionConnected)));
                } else if (tacnet.isConnecting()) {
                    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.tacnetConnectionConnecting)));
                } else if (tacnet.isConnectionError()) {
                    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.tacnetConnectionConnectionError)));
                } else {
                    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                }

                handler.postDelayed(this, 500);
            }
        }, 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    public void close() {
        finish();
    }
}