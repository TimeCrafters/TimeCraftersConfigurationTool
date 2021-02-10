package org.timecrafters.TimeCraftersConfigurationTool.ui.editor;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.AddFromPresetDialog;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.CloneDialog;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.ConfirmationDialog;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.GroupDialog;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.PresetDialog;
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

        actionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showAddMenu(actionButton);

                return true;
            }
        });

        floatingActionButtonAutoHide(actionButton, scrollView);
        if (Backend.instance() != null) {
            if (Backend.instance().getConfig() == null) {
                actionButton.hide();
            }

            this.config = Backend.instance().getConfig();
        }

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
            final Button name = view.findViewById(R.id.name);
            ImageButton rename = view.findViewById(R.id.rename);
            ImageButton delete = view.findViewById(R.id.delete);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            final int index = i;

            name.setText(group.name);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("group_index", config.getGroups().indexOf(group));
                    Navigation.findNavController(v).navigate(R.id.actions_fragment, bundle);
                }
            });
            name.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showActionExtrasMenu(name, index);

                    return true;
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
                            Backend.instance().sortGroups();
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

    private void showActionExtrasMenu(View view, final int group_index) {
        Context context = new ContextThemeWrapper(getActivity(), R.style.PopUpMenu);
        PopupMenu menu = new PopupMenu(context, view);
        menu.getMenuInflater().inflate(R.menu.action_extras_menu, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.clone: {
                        CloneDialog dialog = new CloneDialog();
                        Bundle bundle = new Bundle();
                        bundle.putInt("group_index", group_index);
                        dialog.setArguments(bundle);
                        dialog.show(getFragmentManager(), "clone_dialog");
                        return true;
                    }
                    case R.id.save_as_preset: {
                        PresetDialog dialog = new PresetDialog();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("is_new_preset", true);
                        bundle.putInt("group_index", group_index);
                        dialog.setArguments(bundle);
                        dialog.show(getFragmentManager(), "preset_dialog");
                        return true;
                    }
                    default:
                        return false;
                }
            }
        });

        menu.show();
    }

    private void showAddMenu(View view) {
        Context context = new ContextThemeWrapper(getActivity(), R.style.PopUpMenu);
        PopupMenu menu = new PopupMenu(context, view);
        menu.getMenuInflater().inflate(R.menu.action_add_menu, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_from_preset: {
                        AddFromPresetDialog dialog = new AddFromPresetDialog();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("show_actions", false);
                        dialog.setArguments(bundle);
                        dialog.show(getFragmentManager(), "add_from_preset_dialog");
                        return true;
                    }
                    default:
                        return false;
                }
            }
        });

        menu.show();
    }
}