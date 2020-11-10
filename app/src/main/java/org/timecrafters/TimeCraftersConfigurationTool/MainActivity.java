package org.timecrafters.TimeCraftersConfigurationTool;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.TAC;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.TACNETServerService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String AUTO_START_MODEL = "pixel"; // "rev hub" /* LOWERCASE */
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

        // Auto start TACNET server if allowed and device model contains AUTO_START_MODEL
        if (!TAC.COMPETITION_MODE && Backend.instance().getServer() == null && Build.MODEL.toLowerCase().contains(AUTO_START_MODEL)) {
            Log.i(TAG, "Detected REV Robotics Control Hub, attempting to auto-start TACNET Server Service...");

            startService(new Intent(this, TACNETServerService.class));
        }

        if (getIntent().getBooleanExtra("navigate_to_tacnet", false)) {
            Log.i(TAG, "Navigatingg to tacnet...");
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.navigation_tacnet);
        }
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