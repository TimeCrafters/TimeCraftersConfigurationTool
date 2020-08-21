package org.timecrafters.TimeCraftersConfigurationTool.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;

public class ConfirmationDialog extends TimeCraftersDialog {
    private String title, message;
    private Runnable action;

    public ConfirmationDialog() {}

    public ConfirmationDialog(String title, String message, Runnable action) {
        this.title = title;
        this.message = message;
        this.action = action;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        final TextView title = root.findViewById(R.id.dialogTitle);
        final ConstraintLayout titlebar = root.findViewById(R.id.titlebar);
        final LinearLayout view = root.findViewById(R.id.dialogContent);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_confirmation, null));
        final TextView messageView = root.findViewById(R.id.message);
        final Button cancel = root.findViewById(R.id.cancel);
        final Button confirm = root.findViewById(R.id.confirm);

        titlebar.setBackgroundColor(getResources().getColor(R.color.dialogAlert));
        title.setText(this.title);
        messageView.setText(message);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return root;
    }
}
