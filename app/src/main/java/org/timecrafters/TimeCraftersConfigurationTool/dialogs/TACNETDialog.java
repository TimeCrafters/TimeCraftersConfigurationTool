package org.timecrafters.TimeCraftersConfigurationTool.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;

public class TACNETDialog extends TimeCraftersDialog {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);

        View root = super.onCreateView(inflater, container, savedInstanceState);

        root.findViewById(R.id.titlebar).setBackgroundColor(getResources().getColor(R.color.tacnetPrimary));
        ((TextView)root.findViewById(R.id.dialog_title)).setText(getArguments().getString("title", ""));
        LinearLayout view = root.findViewById(R.id.dialog_content);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_tacnet, null));
        ((TextView)view.findViewById(R.id.message)).setText(getArguments().getString("message", ""));

        final Button closeButton = view.findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return root;
    }
}
