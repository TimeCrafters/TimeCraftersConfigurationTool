package org.timecrafters.TimeCraftersConfigurationTool.dialogs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;

public class ConfigurationDialog extends TimeCraftersDialog {
    private static final String TAG = "ConfigurationDialog";
    private String configName;
    private TextView nameError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        final LinearLayout view = root.findViewById(R.id.dialogContent);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_configuration, null));

        final TextView title = root.findViewById(R.id.dialogTitle);
        final EditText name = view.findViewById(R.id.name);
        this.nameError = view.findViewById(R.id.name_error);
        final Button cancel = view.findViewById(R.id.cancel);
        final Button mutate = view.findViewById(R.id.mutate);

        if (getArguments() != null) {
            configName = getArguments().getString("config_name");
            title.setText("Editing " + configName);
            name.setText(configName);
            mutate.setText(getResources().getString(R.string.dialog_update));
        } else {
            title.setText("Create Configuration");
        }

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validated(name.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mutate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newConfigName = name.getText().toString();

                if (newConfigName.equals(configName)) {
                    dismiss();
                    return;
                }

                if (validated(newConfigName)) {
                    if (configName != null) {
                        Backend.instance().moveConfig(configName, newConfigName);
                    } else {
                        Backend.instance().writeNewConfig(newConfigName);
                    }

                    dismiss();
                } else {
                    // TODO: Show friendly error message
                    Log.d(TAG, "onClick: InValid");
                }
            }
        });

        return root;
    }

    private boolean validated(String name) {
        String message = "";
        if (Backend.instance().configsList().contains(name)) {
            message += "Name is not unique!";

        } else if (name.length() <= 0) {
            message += "Name cannot be blank!";

        } else if (!name.matches("^[A-Za-z0-9\\._\\-]+$")) {
            message += "Name can only contain alphanumeric characters, dashes, underscores, periods, and no spaces!";
        }

        if (message.length() > 0) {
            nameError.setVisibility(View.VISIBLE);
            nameError.setText(message);
            return false;
        } else {
            nameError.setVisibility(View.GONE);
            return true;
        }
    }
}
