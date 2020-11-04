package org.timecrafters.TimeCraftersConfigurationTool.dialogs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;
import org.timecrafters.TimeCraftersConfigurationTool.ui.editor.GroupsFragment;

import java.util.ArrayList;

public class AddFromPresetDialog extends TimeCraftersDialog {
    final String TAG = "AddFromPresetDialog";
    LinearLayout container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        final TextView title = root.findViewById(R.id.dialog_title);
        final LinearLayout view = root.findViewById(R.id.dialog_content);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_add_from_preset, null));
        this.container = root.findViewById(R.id.container);

        title.setText(R.string.add_from_preset);

        if (getArguments().getBoolean("show_actions", false)) {
            populateActionsOptions();
        } else {
            populateGroupsOptions();
        }

        return root;
    }

    public void populateGroupsOptions() {
        container.removeAllViews();

        int i = 0;
        for (Group group : Backend.instance().getConfig().getPresets().getGroups()) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_add_from_preset, null);
            Button name = view.findViewById(R.id.name);
            TextView description = view.findViewById(R.id.description);
            name.setText(group.name);
            description.setVisibility(View.GONE);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            final int index = i;
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CloneDialog dialog = new CloneDialog();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("is_cloning_preset", true);
                    bundle.putInt("group_index", index);
                    dialog.setArguments(bundle);
                    dialog.show(getFragmentManager(), "clone_group_preset");

                    dismiss();
                }
            });

            container.addView(view);
            i++;
        }
    }

    public void populateActionsOptions() {
        container.removeAllViews();

        int i = 0;
        for (Action action : Backend.instance().getConfig().getPresets().getActions()) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_add_from_preset, null);
            Button name = view.findViewById(R.id.name);
            TextView description = view.findViewById(R.id.description);
            name.setText(action.name);
            description.setText(action.comment);

            final int index = i;
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CloneDialog dialog = new CloneDialog();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("is_cloning_preset", true);
                    if (getArguments().getBoolean("group_is_preset", false)) {
                        bundle.putBoolean("group_is_preset", true);
                    }
                    bundle.putInt("group_index", getArguments().getInt("group_index"));
                    bundle.putInt("action_index", index);
                    dialog.setArguments(bundle);
                    dialog.show(getFragmentManager(), "clone_action_preset");

                    dismiss();
                }
            });

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            if (action.comment.length() <= 0) {
                description.setVisibility(View.GONE);
            }

            container.addView(view);
            i++;
        }
    }
}
