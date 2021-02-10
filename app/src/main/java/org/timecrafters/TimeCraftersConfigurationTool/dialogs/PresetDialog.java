package org.timecrafters.TimeCraftersConfigurationTool.dialogs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;
import org.timecrafters.TimeCraftersConfigurationTool.ui.editor.ActionsFragment;
import org.timecrafters.TimeCraftersConfigurationTool.ui.editor.GroupsFragment;
import org.timecrafters.TimeCraftersConfigurationTool.ui.settings.presets.PresetsFragment;

import java.util.ArrayList;

public class PresetDialog extends TimeCraftersDialog {
    final String TAG = "PresetDialog";
    private Group group;
    private Action action;
    private boolean isNewPreset = false;
    private TextView nameError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);

        View root = super.onCreateView(inflater, container, savedInstanceState);

        if (getArguments() != null) {
            isNewPreset = getArguments().getBoolean("is_new_preset", false);

            if (isNewPreset) {
                this.group = Backend.instance().getConfig().getGroups().get(getArguments().getInt("group_index"));

                if (getArguments().getInt("action_index", -1) != -1) {
                    this.action = group.getActions().get(getArguments().getInt("action_index"));
                }
            } else {
                if (getArguments().getInt("action_index", -1) != -1) {
                    this.action = Backend.instance().getConfig().getPresets().getActions().get(getArguments().getInt("action_index"));
                } else {
                    this.group = Backend.instance().getConfig().getPresets().getGroups().get(getArguments().getInt("group_index"));
                }
            }
        }

        final TextView title = root.findViewById(R.id.dialog_title);
        final LinearLayout view = root.findViewById(R.id.dialog_content);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_edit_group, null));
        final EditText name = view.findViewById(R.id.name);
        nameError = view.findViewById(R.id.name_error);

        final Button cancel = view.findViewById(R.id.cancel);
        final Button mutate = view.findViewById(R.id.mutate);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (!isNewPreset) {
            if (action != null) {
                title.setText("Editing " + action.name);
                name.setText(action.name);
            } else {
                title.setText("Editing " + group.name);
                name.setText(group.name);
            }
            mutate.setText(getResources().getString(R.string.dialog_update));
        } else {
            title.setText("Add Preset");
        }

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validated(name.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mutate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final String presetName = name.getText().toString().trim();

            if (action != null) {
                handleAction(presetName);
            } else {
                handleGroup(presetName);
            }
            }
        });

        return root;
    }

    private boolean validated(String name) {
        String message = "";
        boolean nameUnique = true;

        if (action != null) {
            ArrayList<Action> actions = Backend.instance().getConfig().getPresets().getActions();

            for (Action a : actions) {
                if (a.name.equals(name)) {
                    nameUnique = false;
                    break;
                }
            }
        } else {
            ArrayList<Group> groups = Backend.instance().getConfig().getPresets().getGroups();

            for (Group g : groups) {
                if (g.name.equals(name)) {
                    nameUnique = false;
                    break;
                }
            }
        }

        if (!nameUnique) {
            message += "Name is not unique!";

        } else if (name.length() <= 0) {
            message += "Name cannot be blank!";

        }

        if (message.length() > 0) {
            nameError.setVisibility(View.VISIBLE);
            nameError.setText(message);
            return false;
        } else {
            nameError.setVisibility(View.GONE);
            return true;
        }
    }

    private Group deepCopyGroup(Group group) {
        String json = Backend.instance().gsonForConfig().toJson(group);

        return Backend.instance().gsonForConfig().fromJson(json, Group.class);
    }

    private Action deepCopyAction(Action action) {
        String json = Backend.instance().gsonForConfig().toJson(action);

        return Backend.instance().gsonForConfig().fromJson(json, Action.class);
    }

    private void handleGroup(String presetName) {
        Group groupClone = deepCopyGroup(group);
        if (!isNewPreset && groupClone.name.equals(presetName)) {
            dismiss();
        }

        if (validated(presetName)) {
            if (group.name != presetName) {
                if (isNewPreset) {
                    groupClone.name = presetName;
                } else {
                    group.name = presetName;
                }
            }

            if (isNewPreset) {
                Backend.instance().getConfig().getPresets().getGroups().add(groupClone);
                Backend.instance().sortGroupPresets();

                GroupsFragment fragment = (GroupsFragment) getFragmentManager().getPrimaryNavigationFragment();
                Snackbar.make(fragment.getActivity().findViewById(R.id.snackbar_host), "Saved group preset: " + presetName, Snackbar.LENGTH_LONG).show();
            } else { // Don't repopulate presets when it is not possible
                PresetsFragment fragment = (PresetsFragment) getFragmentManager().getPrimaryNavigationFragment();
                if (fragment != null) {
                    Backend.instance().sortGroupPresets();
                    fragment.populatePresets();
                }
            }

            Backend.instance().configChanged();

            dismiss();
        }
    }
    private void handleAction(String presetName) {
        Action actionClone = deepCopyAction(action);
        if (!isNewPreset && actionClone.name.equals(presetName)) {
            dismiss();
        }

        if (validated(presetName)) {
            if (action.name != presetName) {
                if (isNewPreset) {
                    actionClone.name = presetName;
                } else {
                    action.name = presetName;
                }
            }

            if (isNewPreset) {
                Backend.instance().getConfig().getPresets().getActions().add(actionClone);
                Backend.instance().sortActionsPresets();

                ActionsFragment fragment = (ActionsFragment) getFragmentManager().getPrimaryNavigationFragment();
                Snackbar.make(fragment.getActivity().findViewById(R.id.snackbar_host), "Saved action preset: " + presetName, Snackbar.LENGTH_LONG).show();
            } else { // Don't repopulate presets when it is not possible
                PresetsFragment fragment = (PresetsFragment) getFragmentManager().getPrimaryNavigationFragment();
                if (fragment != null) {
                    Backend.instance().sortActionsPresets();

                    fragment.populatePresets();
                }
            }

            Backend.instance().configChanged();

            dismiss();
        }
    }
}
