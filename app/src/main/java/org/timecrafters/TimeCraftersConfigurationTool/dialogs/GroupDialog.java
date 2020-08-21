package org.timecrafters.TimeCraftersConfigurationTool.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;

import java.util.ArrayList;

public class GroupDialog extends TimeCraftersDialog {
    final String TAG = "GroupDialog";
    private Group group;

    public GroupDialog() {}

    public GroupDialog(Group group) {
        this.group = group;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);

        View root = super.onCreateView(inflater, container, savedInstanceState);

        final TextView title = root.findViewById(R.id.dialogTitle);
        final LinearLayout view = root.findViewById(R.id.dialogContent);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_edit_group, null));
        final EditText name = view.findViewById(R.id.name);

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

        mutate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (group != null) {
                    group.name = name.getText().toString();
                } else {
                    Group group = new Group(name.getText().toString(), new ArrayList<Action>());

                    Backend.instance().getConfig().getGroups().add(group);
                }

                Backend.instance().configChanged();
                dismiss();
            }
        });

        return root;
    }
}
