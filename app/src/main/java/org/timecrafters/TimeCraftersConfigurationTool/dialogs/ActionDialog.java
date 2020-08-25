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
import android.widget.Switch;
import android.widget.TextView;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Variable;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;
import org.timecrafters.TimeCraftersConfigurationTool.ui.editor.ActionsFragment;
import org.timecrafters.TimeCraftersConfigurationTool.ui.editor.GroupsFragment;

import java.util.ArrayList;

public class ActionDialog extends TimeCraftersDialog {
    final String TAG = "ActionDialog";
    private Group group;
    private Action action;
    private TextView nameError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);

        View root = super.onCreateView(inflater, container, savedInstanceState);

        if (getArguments() != null) {
            this.group = Backend.instance().getConfig().getGroups().get(getArguments().getInt("group_index"));

            if (getArguments().getInt("action_index", -1) != -1) {
                this.action = group.getActions().get(getArguments().getInt("action_index"));
            }
        }

        final TextView title = root.findViewById(R.id.dialog_title);
        final LinearLayout view = root.findViewById(R.id.dialog_content);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_edit_action, null));
        final EditText name = view.findViewById(R.id.name);
        this.nameError = view.findViewById(R.id.name_error);
        final EditText comment = view.findViewById(R.id.comment);

        final Button cancel = view.findViewById(R.id.cancel);
        final Button mutate = view.findViewById(R.id.mutate);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (action != null) {
            title.setText("Editing " + action.name);
            name.setText(action.name);
            comment.setText(action.comment);

            mutate.setText("Update");
        } else {
            title.setText("Add Action");
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
                final String actionName = name.getText().toString().trim();
                final String commentValue = comment.getText().toString();

                if (validated(actionName) || (action != null && action.name.equals(actionName))) {
                    if (action != null) {
                        action.name = actionName;
                        action.comment = commentValue;
                    } else {
                        Action action = new Action(actionName, commentValue, true, new ArrayList<Variable>());

                        group.getActions().add(action);
                    }

                    Backend.instance().configChanged();
                    ActionsFragment fragment = (ActionsFragment) getFragmentManager().getPrimaryNavigationFragment();
                    if (fragment != null) {
                        fragment.populateActions();
                    }
                    dismiss();
                }
            }
        });

        return root;
    }

    private boolean validated(String name) {
        String message = "";
        boolean nameUnique = true;

        for (Action a : group.getActions()) {
            if (a.name.equals(name)) {
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
