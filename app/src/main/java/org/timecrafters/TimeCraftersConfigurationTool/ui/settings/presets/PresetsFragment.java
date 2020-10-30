package org.timecrafters.TimeCraftersConfigurationTool.ui.settings.presets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;

public class PresetsFragment extends TimeCraftersFragment {
    private LayoutInflater inflater;
    private LinearLayout groupsContainer, actionsContainer;
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        this.root = inflater.inflate(R.layout.fragment_presets, container, false);
        this.groupsContainer = root.findViewById(R.id.groups_container);
        this.actionsContainer = root.findViewById(R.id.actions_container);

        populatePresets();

        return root;
    }

    void populatePresets() {
        groupsContainer.removeAllViews();
        actionsContainer.removeAllViews();

        int i = 0;
        for (Group group : Backend.instance().getConfig().getPresets().getGroups()) {
            View view = inflater.inflate(R.layout.fragment_part_presets, null);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            groupsContainer.addView(view);
            i++;
        }

        i = 0;
        for (Action action : Backend.instance().getConfig().getPresets().getActions()) {
            View view = inflater.inflate(R.layout.fragment_part_presets, null);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            Button name = view.findViewById(R.id.name);
            ImageButton rename = view.findViewById(R.id.rename);
            ImageButton delete = view.findViewById(R.id.delete);

            name.setText(action.name);

            actionsContainer.addView(view);
            i++;
        }
    }
}
