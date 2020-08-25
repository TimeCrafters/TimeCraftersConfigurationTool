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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.ConfirmationDialog;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.GroupDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialogRunnable;

public class GroupsFragment extends TimeCraftersFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: CREATE");
    }

    final private String TAG = "EditorFragment";
    final private String deleteActionKey = "delete_group";

    private Config config;
    private TextView configName;
    private LinearLayout container;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_groups, container, false);
        this.configName = root.findViewById(R.id.configuration_name);
        this.container = root.findViewById(R.id.container);
        final FloatingActionButton actionButton = root.findViewById(R.id.action_button);
        final ScrollView scrollView = root.findViewById(R.id.scrollview);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupDialog dialog = new GroupDialog();
                dialog.show(getFragmentManager(), "add_group");
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

    public void populateGroups() {
        container.removeAllViews();

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
                    Navigation.findNavController(v).navigate(R.id.actions_fragment, bundle);
                }
            });

            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupDialog dialog = new GroupDialog();
                    Bundle bundle = new Bundle();

                    bundle.putInt("group_index", config.getGroups().indexOf(group));
                    dialog.setArguments(bundle);

                    dialog.show(getFragmentManager(), "rename_group");
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmationDialog dialog = new ConfirmationDialog();
                    Bundle bundle = new Bundle();

                    bundle.putString("message", "Delete group " + group.name + "?");
                    bundle.putString("action", deleteActionKey);
                    dialog.setArguments(bundle);
                    TimeCraftersDialogRunnable action = new TimeCraftersDialogRunnable() {
                        @Override
                        public void run(TimeCraftersDialog dialog) {
                            Backend.instance().getConfig().getGroups().remove(group);
                            Backend.instance().configChanged();
                            Backend.getStorage().remove(deleteActionKey);

                            GroupsFragment fragment = (GroupsFragment) dialog.getFragmentManager().getPrimaryNavigationFragment();
                            if (fragment != null) {
                                fragment.populateGroups();
                            }
                        }
                    };
                    Backend.getStorage().put(deleteActionKey, action);

                    dialog.show(getFragmentManager(), deleteActionKey);
                }
            });

            i++;
            container.addView(view);
        }
    }
}