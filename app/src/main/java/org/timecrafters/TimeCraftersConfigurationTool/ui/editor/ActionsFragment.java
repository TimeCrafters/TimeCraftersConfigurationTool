package org.timecrafters.TimeCraftersConfigurationTool.ui.editor;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.AddFromPresetDialog;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.CloneDialog;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.ConfirmationDialog;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.PresetDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialogRunnable;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;

public class ActionsFragment extends TimeCraftersFragment {
    final private String deleteActionKey = "delete_action";
    final private String TAG = "ActionsFragment";

    private Config config;
    private Group group;
    private LinearLayout container;
    private boolean groupIsPreset = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_actions, container, false);
        this.container = root.findViewById(R.id.container);
        final FloatingActionButton actionButton = root.findViewById(R.id.action_button);
        final ScrollView scrollView = root.findViewById(R.id.scrollview);

        this.config = Backend.instance().getConfig();
        this.groupIsPreset = getArguments().getBoolean("group_is_preset", false);
        if (groupIsPreset) {
            this.group = config.getPresets().getGroups().get(getArguments().getInt("group_index"));
        } else {
            this.group = config.getGroups().get(getArguments().getInt("group_index"));
        }
        if (config != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Group: " + group.name);

            populateActions();
        }

        floatingActionButtonAutoHide(actionButton, scrollView);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionDialog dialog = new ActionDialog();
                Bundle bundle = new Bundle();
                if (groupIsPreset) {
                    bundle.putBoolean("group_is_preset", true);
                }
                bundle.putInt("group_index", getArguments().getInt("group_index"));
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), "add_action");
            }
        });

        actionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showAddMenu(actionButton);

                return true;
            }
        });

        return root;
    }

    public void populateActions() {
        container.removeAllViews();

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
                    Backend.instance().configChanged();

                    styleSwitch(buttonView, isChecked);
                }
            });
            final int index = i;
            name.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showActionExtrasMenu(name, index);

                    return true;
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    if (groupIsPreset) {
                        bundle.putBoolean("group_is_preset", true);
                    }
                    bundle.putInt("group_index", getArguments().getInt("group_index"));
                    bundle.putInt("action_index", group.getActions().indexOf(action));
                    Navigation.findNavController(v).navigate(R.id.variables_fragment, bundle);
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
                    ActionDialog dialog = new ActionDialog();
                    Bundle bundle = new Bundle();
                    if (groupIsPreset) {
                        bundle.putBoolean("group_is_preset", true);
                    }
                    bundle.putInt("group_index", getArguments().getInt("group_index"));
                    bundle.putInt("action_index", group.getActions().indexOf(action));
                    dialog.setArguments(bundle);
                    dialog.show(getFragmentManager(), "edit_action");
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmationDialog dialog = new ConfirmationDialog();
                    Bundle bundle = new Bundle();

                    bundle.putString("message", "Delete action " + action.name + "?");
                    bundle.putString("action", deleteActionKey);
                    dialog.setArguments(bundle);
                    final TimeCraftersDialogRunnable actionRunner = new TimeCraftersDialogRunnable() {
                        @Override
                        public void run(TimeCraftersDialog dialog) {
                            group.getActions().remove(action);
                            Backend.instance().configChanged();
                            Backend.getStorage().remove(deleteActionKey);

                            ActionsFragment fragment = (ActionsFragment) dialog.getFragmentManager().getPrimaryNavigationFragment();
                            if (fragment != null) {
                                fragment.populateActions();
                            }
                        }
                    };
                    Backend.getStorage().put(deleteActionKey, actionRunner);

                    dialog.show(getFragmentManager(), deleteActionKey);
                }
            });

            i++;
            container.addView(view);
        }
    }

    private void showActionExtrasMenu(View view, final int action_index) {
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
                        bundle.putInt("group_index", getArguments().getInt("group_index"));
                        bundle.putInt("action_index", action_index);
                        dialog.setArguments(bundle);
                        dialog.show(getFragmentManager(), "clone_dialog");
                        return true;
                    }
                    case R.id.save_as_preset: {
                        PresetDialog dialog = new PresetDialog();
                        Bundle bundle = new Bundle();
                        bundle.putInt("group_index", getArguments().getInt("group_index"));
                        bundle.putInt("action_index", action_index);
                        bundle.putBoolean("is_new_preset", true);
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
                        bundle.putBoolean("show_actions", true);
                        if (groupIsPreset) {
                            bundle.putBoolean("group_is_preset", true);
                        }
                        bundle.putInt("group_index", getArguments().getInt("group_index"));
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
