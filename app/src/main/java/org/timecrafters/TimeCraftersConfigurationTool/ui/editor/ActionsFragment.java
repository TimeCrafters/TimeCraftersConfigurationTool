package org.timecrafters.TimeCraftersConfigurationTool.ui.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.ActionDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;

public class ActionsFragment extends TimeCraftersFragment {
    private Config config;
    private Group group;
    private LinearLayout container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_actions, container, false);
        this.container = root.findViewById(R.id.container);
        final FloatingActionButton actionButton = root.findViewById(R.id.actionButton);
        final ScrollView scrollView = root.findViewById(R.id.scrollview);

        this.config = Backend.instance().getConfig();
        this.group = config.getGroups().get(getArguments().getInt("group_index"));
        if (config != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Group: " + group.name);

            populateActions();
        }

        floatingActionButtonAutoHide(actionButton, scrollView);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionDialog dialog = new ActionDialog(group);
                dialog.show(getFragmentManager(), null);
            }
        });

        return root;
    }

    private void populateActions() {
        int i = 0;
        for (final Action action : group.getActions()) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_actions, null);
            final Switch name = view.findViewById(R.id.name);
            final ImageButton edit = view.findViewById(R.id.edit);
            final ImageButton rename = view.findViewById(R.id.rename);
            final ImageButton delete = view.findViewById(R.id.delete);
            final TextView comment = view.findViewById(R.id.comment);

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
                    Bundle bundle = new Bundle();
                    bundle.putInt("group_index", getArguments().getInt("group_index"));
                    bundle.putInt("action_index", group.getActions().indexOf(action));
                    Navigation.findNavController(v).navigate(R.id.variablesFragment, bundle);
                }
            });

            if (action.comment.length() > 0) {
                comment.setText(action.comment);
            } else {
                comment.setVisibility(View.GONE);
            }

            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionDialog dialog = new ActionDialog(action, name, comment);
                    dialog.show(getFragmentManager(), null);
                }
            });

            i++;
            container.addView(view);
        }
    }
}
