package org.timecrafters.TimeCraftersConfigurationTool.ui.settings.configurations;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.timecrafters.TimeCraftersConfigurationTool.R;

public class ConfigurationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_configurations);
        LinearLayout v = findViewById(R.id.configurations);
        v.setBackgroundColor(getResources().getColor(R.color.list_even));

        View vv = v.inflate(getApplicationContext(), R.layout.fragment_configuration, null);
        v.addView(vv);
    }
}
