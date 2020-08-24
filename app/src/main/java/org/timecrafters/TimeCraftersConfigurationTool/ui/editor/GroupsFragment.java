package org.timecrafters.TimeCraftersConfigurationTool.ui.editor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.ConfirmationDialog;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.GroupDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;

public class GroupsFragment extends TimeCraftersFragment {

    final private String TAG = "EditorFragment";

    private EditorViewModel editorViewModel;
    private Config config;
    private TextView configName;
    private LinearLayout container;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editorViewModel =
                ViewModelProviders.of(this).get(EditorViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_groups, container, false);
        this.configName = root.findViewById(R.id.configuration_name);
        this.container = root.findViewById(R.id.container);
        final FloatingActionButton actionButton = root.findViewById(R.id.actionButton);
        final ScrollView scrollView = root.findViewById(R.id.scrollview);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupDialog dialog = new GroupDialog();
                dialog.show(getFragmentManager(), null);
            }
        });

        floatingActionButtonAutoHide(actionButton, scrollView);

        if (Backend.instance() != null)
            this.config = Backend.instance().getConfig();

        if (config != null) {
            configName.setVisibility(View.GONE);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Config: " + config.getName());

            populateGroups();
        } else {
            Log.d(TAG, "config not set");
        }

        return root;
    }

    private void populateGroups() {
        int i = 0;
        for (final Group group : config.getGroups()) {
            View view = View.inflate(getContext(), R.layout.fragment_part_groups, null);
            Button name = view.findViewById(R.id.name);
            ImageButton rename = view.findViewById(R.id.rename);
            ImageButton delete = view.findViewById(R.id.delete);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            name.setText(group.name);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("group_index", config.getGroups().indexOf(group));
                    Navigation.findNavController(v).navigate(R.id.actionsFragment, bundle);
                }
            });

            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupDialog dialog = new GroupDialog(group);
                    dialog.show(getFragmentManager(), null);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmationDialog dialog = new ConfirmationDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", "Are you sure?");
                    bundle.putString("message", "Delete group " + group.name + "?");
                    dialog.setArguments(bundle);

                    dialog.show(getFragmentManager(), null);
                }
            });

            i++;
            container.addView(view);
        }
    }
}