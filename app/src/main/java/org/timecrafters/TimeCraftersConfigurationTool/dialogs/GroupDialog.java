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
import org.timecrafters.TimeCraftersConfigurationTool.ui.editor.GroupsFragment;

import java.util.ArrayList;

public class GroupDialog extends TimeCraftersDialog {
    private static final int HOST_ID = R.id.navigation_editor;
    final String TAG = "GroupDialog";
    private Group group;
    private TextView nameError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);

        View root = super.onCreateView(inflater, container, savedInstanceState);

        if (getArguments() != null) {
            this.group = Backend.instance().getConfig().getGroups().get(getArguments().getInt("group_index"));
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

        if (group != null) {
            title.setText("Editing " + group.name);
            name.setText(group.name);
            mutate.setText(getResources().getString(R.string.dialog_update));
        } else {
            title.setText("Add Group");
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
                final String groupName = name.getText().toString().trim();
                if (group != null && group.name.equals(groupName)) {
                    dismiss();
                }

                if (validated(groupName)  || (group != null && group.name.equals(groupName))) {
                    if (group != null) {
                        group.name = groupName;
                    } else {
                        Group group = new Group(groupName, new ArrayList<Action>());

                        Backend.instance().getConfig().getGroups().add(group);
                    }

                    Backend.instance().sortGroups();
                    Backend.instance().configChanged();
                    GroupsFragment fragment = (GroupsFragment) getParentFragmentManager().getPrimaryNavigationFragment();
                    if (fragment != null) {
                        fragment.populateGroups();
                    }
                    dismiss();
                }
            }
        });

        return root;
    }

    private boolean validated(String name) {
        String message = "";
        Config config = Backend.instance().getConfig();
        boolean nameUnique = true;

        for (Group g : config.getGroups()) {
            if (g.name.equals(name)) {
                nameUnique = false;
                break;
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
