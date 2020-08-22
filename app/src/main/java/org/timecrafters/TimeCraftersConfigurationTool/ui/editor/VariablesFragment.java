package org.timecrafters.TimeCraftersConfigurationTool.ui.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Variable;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.VariableDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;

public class VariablesFragment extends TimeCraftersFragment {
    private Config config;
    private LinearLayout container;
    private Group group;
    private Action action;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_variables, container, false);
        this.container = root.findViewById(R.id.container);
        final FloatingActionButton actionButton = root.findViewById(R.id.actionButton);
        final ScrollView scrollView = root.findViewById(R.id.scrollview);

        this.config = Backend.instance().getConfig();
        this.group = config.getGroups().get(getArguments().getInt("group_index"));
        this.action = group.getActions().get(getArguments().getInt("action_index"));
        if (config != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Action: " + action.name);

            populateVariables();
        }

        floatingActionButtonAutoHide(actionButton, scrollView);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VariableDialog dialog = new VariableDialog(action);
                dialog.show(getFragmentManager(), null);
            }
        });

        return root;
    }

    private void populateVariables() {
        int i = 0;
        for (final Variable variable : action.getVariables()) {
            View view = View.inflate(getContext(), R.layout.fragment_part_variables, null);
            final TextView name = view.findViewById(R.id.name);
            final TextView value = view.findViewById(R.id.value);
            ImageButton rename = view.findViewById(R.id.rename);
            ImageButton delete = view.findViewById(R.id.delete);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            name.setText(variable.name);
            value.setText("" + variable.value());

            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariableDialog dialog = new VariableDialog(variable, name, value);
                    dialog.show(getFragmentManager(), null);
                }
            });

            i++;
            container.addView(view);
        }
    }
}
