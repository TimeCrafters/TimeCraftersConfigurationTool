package org.timecrafters.TimeCraftersConfigurationTool.dialogs;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialogRunnable;

public class ConfirmationDialog extends TimeCraftersDialog {
    private static final String TAG = "ConfirmationDialog";
    private String title, message;
    private TimeCraftersDialogRunnable action;
    private ConfirmationDialog confirmationDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        this.confirmationDialog = this;

        if (getArguments() != null) {
            this.title = getArguments().getString("title", "Are you sure?");
            this.message = getArguments().getString("message", "");

            final String actionKey = getArguments().getString("action", null);
            if (actionKey != null && Backend.getStorage().containsKey(actionKey)) {
                Log.d(TAG, "OKAY onCreateView: actionKey: " + actionKey + " getStorage: " + Backend.getStorage().containsKey(actionKey));

                this.action = (TimeCraftersDialogRunnable) Backend.getStorage().get(actionKey);
            } else {
                Log.d(TAG, "FAIL onCreateView: actionKey: " + actionKey + " getStorage: " + Backend.getStorage().containsKey(actionKey));
            }
        }

        final TextView title = root.findViewById(R.id.dialog_title);
        final ConstraintLayout titlebar = root.findViewById(R.id.titlebar);
        final LinearLayout view = root.findViewById(R.id.dialog_content);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_confirmation, null));
        final TextView messageView = root.findViewById(R.id.message);
        final Button cancel = root.findViewById(R.id.cancel);
        final Button confirm = root.findViewById(R.id.confirm);

        if (getArguments() != null && getArguments().getBoolean("extreme_danger", false)) {
            titlebar.setBackgroundColor(getResources().getColor(R.color.dialog_error));
            getDialog().getWindow().setDimAmount(0.8f);
            cancel.setTypeface(cancel.getTypeface(), Typeface.BOLD);
        } else {
            titlebar.setBackgroundColor(getResources().getColor(R.color.dialogAlert));
        }
        title.setText(this.title);
        messageView.setText(message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action != null) {
                    action.run(confirmationDialog);
                }

                dismiss();
            }
        });

        return root;
    }
}
