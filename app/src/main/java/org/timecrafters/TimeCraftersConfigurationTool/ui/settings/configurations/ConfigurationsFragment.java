package org.timecrafters.TimeCraftersConfigurationTool.ui.settings.configurations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.ConfigurationDialog;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.ConfirmationDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialogRunnable;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.PacketHandler;

public class ConfigurationsFragment extends TimeCraftersFragment {
    final private String deleteActionKey = "delete_configuration";

    private LayoutInflater inflater;
    private LinearLayout configsContainer;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        this.root = inflater.inflate(R.layout.fragment_configuration, container, false);
        final ScrollView scrollview = root.findViewById(R.id.scrollview);
        configsContainer = root.findViewById(R.id.container);
        final FloatingActionButton actionButton = root.findViewById(R.id.action_button);

        floatingActionButtonAutoHide(actionButton, scrollview);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigurationDialog dialog = new ConfigurationDialog();
                dialog.show(getFragmentManager(), "add_configuration");
            }
        });

        populateConfigFiles();

        return root;
    }

    public void populateConfigFiles() {
        configsContainer.removeAllViews();

        int i = 0;
        for (final String configFile : Backend.instance().configsList()) {
            View view = inflater.inflate(R.layout.fragment_part_configuration, null);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            final Button configName = view.findViewById(R.id.name);
            final ImageButton rename = view.findViewById(R.id.rename);
            final ImageButton delete = view.findViewById(R.id.delete);
            configName.setText(configFile);
            configName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Backend.instance().getSettings().config = configFile;
                    Backend.instance().loadConfig(configFile);
                    Backend.instance().saveSettings();

                    View snackbarHost = getActivity().findViewById(R.id.snackbar_host);
                    Snackbar.make(snackbarHost, "Loaded config: " + configFile, Snackbar.LENGTH_LONG).show();

                    if (Backend.instance().tacnet().isConnected()) {
                        Backend.instance().tacnet().puts(PacketHandler.packetSelectConfig(configFile).toString() );
                    }
                }
            });

            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfigurationDialog dialog = new ConfigurationDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("config_name", configFile);
                    dialog.setArguments(bundle);

                    dialog.show(getFragmentManager(), "rename_configuration");
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmationDialog dialog = new ConfirmationDialog();
                    Bundle bundle = new Bundle();

                    bundle.putString("message", "Destroy configuration " + configFile + "?");
                    bundle.putString("action", deleteActionKey);
                    bundle.putBoolean("extreme_danger", true);
                    TimeCraftersDialogRunnable action = new TimeCraftersDialogRunnable() {
                        @Override
                        public void run(TimeCraftersDialog dialog) {
                            Backend.instance().deleteConfig(configFile);
                            Backend.getStorage().remove(deleteActionKey);

                            if (Backend.instance().getConfig().getName().equals(configFile)) {
                                Backend.instance().getSettings().config = "";
                                Backend.instance().saveSettings();
                                Backend.instance().loadConfig("");
                            }

                            ConfigurationsFragment fragment = (ConfigurationsFragment) dialog.getFragmentManager().getPrimaryNavigationFragment();
                            if (fragment != null) {
                                fragment.populateConfigFiles();
                            }

                            if (Backend.instance().tacnet().isConnected()) {
                                Backend.instance().tacnet().puts(PacketHandler.packetDeleteConfig(configFile).toString() );
                            }
                        }
                    };
                    Backend.getStorage().put(deleteActionKey, action);
                    dialog.setArguments(bundle);

                    dialog.show(getFragmentManager(), deleteActionKey);
                }
            });

            i++;
            configsContainer.addView(view);
        }
    }
}
