package org.timecrafters.TimeCraftersConfigurationTool.ui.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;

public class VariablesFragment extends Fragment {
    private Config config;
    private LinearLayout container;
    private Config.Group group;
    private Config.Action action;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_variables, container, false);
        this.container = root.findViewById(R.id.container);

        this.config = Backend.instance().getConfig();
        this.group = config.getGroups().get(0);
        this.action = group.getActions().get(0);
        if (config != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Action: " + action.name);

            populateVariables();
        }

        return root;
    }

    private void populateVariables() {
        int i = 0;
        for (Config.Variable variable : action.getVariables()) {
            View view = View.inflate(getContext(), R.layout.fragment_part_variables, null);
            TextView name = view.findViewById(R.id.name);
            TextView value = view.findViewById(R.id.value);
            ImageButton rename = view.findViewById(R.id.rename);
            ImageButton delete = view.findViewById(R.id.delete);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            name.setText(variable.name);
            value.setText("" + variable.value());

            i++;
            container.addView(view);
        }
    }
}
