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

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;
import org.timecrafters.TimeCraftersConfigurationTool.ui.editor.ActionsFragment;
import org.timecrafters.TimeCraftersConfigurationTool.ui.editor.GroupsFragment;

import java.util.ArrayList;

public class CloneDialog extends TimeCraftersDialog {
    private static final int HOST_ID = R.id.navigation_editor;
    final String TAG = "CloneDialog";
    private Group group;
    private Action action;
    private TextView nameError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);

        View root = super.onCreateView(inflater, container, savedInstanceState);

        if (getArguments().getBoolean("is_cloning_preset", false)) {
            if (getArguments().getInt("action_index", -1) != -1) {
                if (getArguments().getBoolean("group_is_preset", false)) {
                    this.group = Backend.instance().getConfig().getPresets().getGroups().get(getArguments().getInt("group_index"));
                } else {
                    this.group = Backend.instance().getConfig().getGroups().get(getArguments().getInt("group_index"));
                }

                this.action = Backend.instance().getConfig().getPresets().getActions().get(getArguments().getInt("action_index"));
            } else {
                this.group = Backend.instance().getConfig().getPresets().getGroups().get(getArguments().getInt("group_index"));
            }

        } else {
            this.group = Backend.instance().getConfig().getGroups().get(getArguments().getInt("group_index"));

            if (getArguments().getInt("action_index", -1) != -1) {
                this.action = group.getActions().get(getArguments().getInt("action_index"));
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

        if (action != null) {
            title.setText("Cloning action " + action.name);
            name.setText(action.name);
        } else {
            title.setText("Cloning group " + group.name);
            name.setText(group.name);
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
                final String finalName = name.getText().toString().trim();
                if (validated(finalName)) {
                    if (action != null) {
                        String json = Backend.instance().gsonForConfig().toJson(action);
                        Action actionClone = Backend.instance().gsonForConfig().fromJson(json, Action.class);
                        actionClone.name = finalName;

                        group.getActions().add(actionClone);

                        Backend.instance().configChanged();
                        ActionsFragment fragment = (ActionsFragment) getFragmentManager().getPrimaryNavigationFragment();
                        if (fragment != null) {
                            fragment.populateActions();
                        }
                        dismiss();
                    } else {
                        String json = Backend.instance().gsonForConfig().toJson(group);
                        Group groupClone = Backend.instance().gsonForConfig().fromJson(json, Group.class);
                        groupClone.name = finalName;

                        Backend.instance().getConfig().getGroups().add(groupClone);

                        Backend.instance().configChanged();
                        GroupsFragment fragment = (GroupsFragment) getFragmentManager().getPrimaryNavigationFragment();
                        if (fragment != null) {
                            fragment.populateGroups();
                        }
                        dismiss();
                    }
                }
            }
        });

        return root;
    }

    private boolean validated(String name) {
        String message = "";
        Config config = Backend.instance().getConfig();
        boolean nameUnique = true;

        if (action != null) {
            for (Action a : group.getActions()) {
                if (a.name.equals(name)) {
                    nameUnique = false;
                    break;
                }
            }
        } else {
            for (Group g : config.getGroups()) {
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
}
