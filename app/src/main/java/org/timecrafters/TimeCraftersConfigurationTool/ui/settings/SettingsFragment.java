package org.timecrafters.TimeCraftersConfigurationTool.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;

public class SettingsFragment extends TimeCraftersFragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_settings, container, false);
        final Button managePresets = root.findViewById(R.id.manage_presets);
        final Button manageConfigurations = root.findViewById(R.id.manage_configurations);
        final Switch showNavigationLabels = root.findViewById(R.id.show_navigation_labels);
        final Switch disableLauncherDelay = root.findViewById(R.id.disable_launcher_delay);
        final Switch startServerAtBoot = root.findViewById(R.id.start_server_at_boot);

        final BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);

        managePresets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_navigation_settings_to_presetsFragment);
            }
        });

        manageConfigurations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_navigation_settings_to_configurationsFragment);
            }
        });

        showNavigationLabels.setChecked(Backend.instance().getSettings().mobileShowNavigationLabels);
        styleSwitch(showNavigationLabels, Backend.instance().getSettings().mobileShowNavigationLabels);

        disableLauncherDelay.setChecked(Backend.instance().getSettings().mobileDisableLauncherDelay);
        styleSwitch(disableLauncherDelay, Backend.instance().getSettings().mobileDisableLauncherDelay);

        startServerAtBoot.setChecked(Backend.instance().getSettings().mobileStartServerAtBoot);
        styleSwitch(startServerAtBoot, Backend.instance().getSettings().mobileStartServerAtBoot);

        showNavigationLabels.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Backend.instance().getSettings().mobileShowNavigationLabels = isChecked;
                Backend.instance().saveSettings();

                if (isChecked) {
                    navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
                } else {
                    navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
                }

                styleSwitch(buttonView, isChecked);
            }
        });

        disableLauncherDelay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Backend.instance().getSettings().mobileDisableLauncherDelay = isChecked;
                Backend.instance().saveSettings();

                styleSwitch(buttonView, isChecked);
            }
        });

        startServerAtBoot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Backend.instance().getSettings().mobileStartServerAtBoot = isChecked;
                Backend.instance().saveSettings();

                styleSwitch(buttonView, isChecked);
            }
        });

        return root;
    }
}