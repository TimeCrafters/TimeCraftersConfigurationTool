package org.timecrafters.TimeCraftersConfigurationTool.ui.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;

public class ActionsFragment extends Fragment {
    private Config config;
    private Config.Group group;
    private LinearLayout container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_actions, container, false);
        this.container = root.findViewById(R.id.container);

        this.config = Backend.instance().getConfig();
        this.group = config.getGroups().get(0);
        if (config != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Group: " + group.name);

            populateActions();
        }
        return root;
    }

    private void populateActions() {
        int i = 0;
        for (final Config.Action action : group.getActions()) {
            View view = View.inflate(getContext(), R.layout.fragment_part_actions, null);
            Switch name = view.findViewById(R.id.name);
            ImageButton edit = view.findViewById(R.id.edit);
            ImageButton rename = view.findViewById(R.id.rename);
            ImageButton delete = view.findViewById(R.id.delete);
            TextView comment = view.findViewById(R.id.comment);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            name.setText(action.name);
            name.setChecked(action.enabled);
            styleSwitch(name, action.enabled);
            name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    action.enabled = isChecked;

                    styleSwitch(buttonView, isChecked);
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate(R.id.variablesFragment);
                }
            });

            if (action.comment.length() > 0) {
                comment.setText(action.comment);
            } else {
                comment.setVisibility(View.GONE);
            }

            i++;
            container.addView(view);
        }
    }

    private void styleSwitch(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            buttonView.setBackground(getResources().getDrawable(R.drawable.button));
        } else {
            buttonView.setBackground(getResources().getDrawable(R.drawable.dangerous_button));
        }
    }
}
