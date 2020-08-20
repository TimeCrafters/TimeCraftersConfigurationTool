package org.timecrafters.TimeCraftersConfigurationTool.ui.editor;

import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config.Group;

public class EditorFragment extends Fragment {

    final private String TAG = "EditorFragment";

    private EditorViewModel editorViewModel;
    private Config config;
    private TextView configName;
    private LinearLayout container;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editorViewModel =
                ViewModelProviders.of(this).get(EditorViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_editor, container, false);
        this.configName = root.findViewById(R.id.configuration_name);
        this.container = root.findViewById(R.id.container);

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
        for (Group group : config.getGroups()) {
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
                    Navigation.findNavController(v).navigate(R.id.actionsFragment);
                }
            });

            i++;
            container.addView(view);
        }
    }
}