package org.timecrafters.TimeCraftersConfigurationTool.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.timecrafters.TimeCraftersConfigurationTool.LauncherActivity;
import org.timecrafters.TimeCraftersConfigurationTool.MainActivity;
import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.TAC;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;

public class PermissionsRequestDialog extends TimeCraftersDialog {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);

        View root = super.onCreateView(inflater, container, savedInstanceState);

        ((TextView)root.findViewById(R.id.dialog_title)).setText("Storage Permission Required");
        LinearLayout view = root.findViewById(R.id.dialog_content);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_permission_request, null));
        ((TextView)view.findViewById(R.id.message)).setText("Permission is required to write to external storage:\n\n" + TAC.ROOT_PATH);

        Button quitButton = view.findViewById(R.id.quit_button);
        Button continueButton = view.findViewById(R.id.continue_button);

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            dismiss();
            ((MainActivity) getActivity()).close();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ((LauncherActivity) getActivity()).requestStoragePermissions();
            }
        });


        return root;
    }
}
