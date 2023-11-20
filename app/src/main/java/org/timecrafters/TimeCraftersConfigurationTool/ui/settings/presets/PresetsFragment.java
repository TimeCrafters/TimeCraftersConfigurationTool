package org.timecrafters.TimeCraftersConfigurationTool.ui.settings.presets;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.ConfirmationDialog;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.PresetDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialogRunnable;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;
import org.timecrafters.TimeCraftersConfigurationTool.ui.editor.ActionsFragment;
import org.timecrafters.TimeCraftersConfigurationTool.ui.settings.configurations.ConfigurationsFragment;

public class PresetsFragment extends TimeCraftersFragment {
    private LayoutInflater inflater;
    private LinearLayout groupsContainer, actionsContainer;
    private View root;
    private String deletePresetKey = "deletePresetKey";

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

    public void populatePresets() {
        populateGroups();
        populateActions();
    }

    private void populateGroups() {
        groupsContainer.removeAllViews();

        int i = 0;
        for (final Group group : Backend.instance().getConfig().getPresets().getGroups()) {
            final int group_index = i;
            View view = inflater.inflate(R.layout.fragment_part_presets, null);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            Button name = view.findViewById(R.id.name);
            ImageButton rename = view.findViewById(R.id.rename);
            ImageButton delete = view.findViewById(R.id.delete);

            name.setText(group.name);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("group_is_preset", true);
                    bundle.putInt("group_index", group_index);
                    Navigation.findNavController(v).navigate(R.id.actions_fragment, bundle);
                }
            });

            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PresetDialog dialog = new PresetDialog();
                    Bundle bundle = new Bundle();
                    bundle.putInt("group_index", group_index);
                    dialog.setArguments(bundle);
                    dialog.show(getParentFragmentManager(), "preset_dialog");
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmationDialog dialog = new ConfirmationDialog();
                    Bundle bundle = new Bundle();

                    bundle.putString("message", "Delete group preset " + group.name + "?");
                    bundle.putString("action", deletePresetKey);
                    TimeCraftersDialogRunnable action = new TimeCraftersDialogRunnable() {
                        @Override
                        public void run(TimeCraftersDialog dialog) {
                            Backend.getStorage().remove(deletePresetKey);

                            if (Backend.instance().getConfig().getPresets().getGroups().get(group_index) != null) {
                                Backend.instance().getConfig().getPresets().getGroups().remove(group_index);

                                Backend.instance().sortGroupPresets();
                                Backend.instance().configChanged();
                            }

                            PresetsFragment fragment = (PresetsFragment) dialog.getParentFragmentManager().getPrimaryNavigationFragment();
                            if (fragment != null) {
                                fragment.populatePresets();
                            }
                        }
                    } ;
                    Backend.getStorage().put(deletePresetKey, action);
                    dialog.setArguments(bundle);

                    dialog.show(getParentFragmentManager(), deletePresetKey);
                }
            });

            groupsContainer.addView(view);
            i++;
        }
    }

    private void populateActions() {
        actionsContainer.removeAllViews();

        int i = 0;
        for (final Action action : Backend.instance().getConfig().getPresets().getActions()) {
            final int action_index = i;
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
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("action_is_preset", true);
                    bundle.putInt("action_index", action_index);
                    Navigation.findNavController(v).navigate(R.id.variables_fragment, bundle);
                }
            });

            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PresetDialog dialog = new PresetDialog();
                    Bundle bundle = new Bundle();
                    bundle.putInt("action_index", action_index);
                    dialog.setArguments(bundle);
                    dialog.show(getParentFragmentManager(), "preset_dialog");
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmationDialog dialog = new ConfirmationDialog();
                    Bundle bundle = new Bundle();

                    bundle.putString("message", "Delete action preset " + action.name + "?");
                    bundle.putString("action", deletePresetKey);
                    TimeCraftersDialogRunnable action = new TimeCraftersDialogRunnable() {
                        @Override
                        public void run(TimeCraftersDialog dialog) {
                            Backend.getStorage().remove(deletePresetKey);

                            if (Backend.instance().getConfig().getPresets().getActions().get(action_index) != null) {
                                Backend.instance().getConfig().getPresets().getActions().remove(action_index);

                                Backend.instance().sortActionsPresets();
                                Backend.instance().configChanged();
                            }

                            PresetsFragment fragment = (PresetsFragment) dialog.getParentFragmentManager().getPrimaryNavigationFragment();
                            if (fragment != null) {
                                fragment.populatePresets();
                            }
                        }
                    } ;
                    Backend.getStorage().put(deletePresetKey, action);
                    dialog.setArguments(bundle);

                    dialog.show(getParentFragmentManager(), deletePresetKey);
                }
            });

            actionsContainer.addView(view);
            i++;
        }
    }
}
