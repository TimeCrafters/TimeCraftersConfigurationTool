package org.timecrafters.TimeCraftersConfigurationTool.dialogs;

import android.os.Bundle;
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

import java.util.regex.Pattern;

public class ConfigurationDialog extends TimeCraftersDialog {
    private static final String TAG = "ConfigurationDialog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        final LinearLayout view = root.findViewById(R.id.dialogContent);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_configuration, null));

        final TextView title = root.findViewById(R.id.dialogTitle);
        final EditText name = view.findViewById(R.id.name);
        final Button cancel = view.findViewById(R.id.cancel);
        final Button mutate = view.findViewById(R.id.mutate);

        title.setText("Create Configuration");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

//        mutate.setText(getResources().getString(R.string.dialog_update));
        mutate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String configName = name.getText().toString();

                if (isValid(configName)) {
                    Backend.instance().writeNewConfig(configName);
                    dismiss();
                } else {
                    Log.d(TAG, "onClick: InValid");
                }
            }
        });

        return root;
    }

    private boolean isValid(String name) {
        return name.length() > 0 && name.matches("^[A-Za-z0-9\\._\\-]+$");
    }
}
