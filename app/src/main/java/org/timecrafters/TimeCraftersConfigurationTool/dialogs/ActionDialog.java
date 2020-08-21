package org.timecrafters.TimeCraftersConfigurationTool.dialogs;

import android.os.Bundle;
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
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Variable;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;

import java.util.ArrayList;

public class ActionDialog extends TimeCraftersDialog {
    final String TAG = "ActionDialog";
    private TextView commentTextView;
    private Switch nameSwitch;
    private Group group;
    private Action action;

    public ActionDialog() {}

    public ActionDialog(Group group) {
        this.group = group;
    }

    public ActionDialog(Action action, Switch nameSwitch, TextView commentTextView) {
        this.action = action;
        this.nameSwitch = nameSwitch;
        this.commentTextView = commentTextView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);

        View root = super.onCreateView(inflater, container, savedInstanceState);

        final TextView title = root.findViewById(R.id.dialogTitle);
        final LinearLayout view = root.findViewById(R.id.dialogContent);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_edit_action, null));
        final EditText name = view.findViewById(R.id.name);
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

        mutate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action != null) {
                    action.name = name.getText().toString();

                    nameSwitch.setText(name.getText().toString());
                    commentTextView.setText(comment.getText().toString());

                    if (comment.getText().toString().length() > 0) {
                        commentTextView.setVisibility(View.VISIBLE);
                    } else {
                        commentTextView.setVisibility(View.GONE);
                    }
                } else {
                    Action action = new Action(name.getText().toString(), comment.getText().toString(), true, new ArrayList<Variable>());

                    group.getActions().add(action);
                }

                Backend.instance().configChanged();
                dismiss();
            }
        });

        return root;
    }
}
