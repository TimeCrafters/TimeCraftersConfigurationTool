package org.timecrafters.TimeCraftersConfigurationTool.ui.settings.configurations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.ConfigurationDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;

public class ConfigurationsFragment extends TimeCraftersFragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_configuration, container, false);
        final ScrollView scrollview = root.findViewById(R.id.scrollview);
        final LinearLayout configsContainer = root.findViewById(R.id.container);
        final FloatingActionButton actionButton = root.findViewById(R.id.actionButton);

        floatingActionButtonAutoHide(actionButton, scrollview);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigurationDialog dialog = new ConfigurationDialog();
                dialog.show(getFragmentManager(), null);
            }
        });

        int i = 0;
        for (String configFile : Backend.instance().configsList()) {
            final String config = configFile.replace(".json", "");
            View view = inflater.inflate(R.layout.fragment_part_configuration, null);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            Button configName = view.findViewById(R.id.name);
            configName.setText(config);
            configName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Backend.instance().getSettings().config = config;
                    Backend.instance().loadConfig(config);
                    Backend.instance().saveSettings();
                }
            });

            i++;
            configsContainer.addView(view);
        }

        return root;
    }
}
