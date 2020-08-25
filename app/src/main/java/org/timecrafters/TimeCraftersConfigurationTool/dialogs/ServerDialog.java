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
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersDialog;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.support.ServerStatsSyncHandler;

public class ServerDialog extends TimeCraftersDialog {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);

        final View root = super.onCreateView(inflater, container, savedInstanceState);
        if (Backend.instance().getServer() == null) {
            Backend.instance().startServer();
        }


        final TextView title = root.findViewById(R.id.dialog_title);
        final ConstraintLayout titlebar = root.findViewById(R.id.titlebar);
        final LinearLayout view = root.findViewById(R.id.dialog_content);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_server, null));
        new ServerStatsSyncHandler(view, 1_000);

        title.setText(getResources().getString(R.string.tacnet_server_status));

        final Button stopServer = root.findViewById(R.id.stop_server);
        stopServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Halt server
                Backend.instance().stopServer();
                dismiss();
            }
        });

        return root;
    }
}
