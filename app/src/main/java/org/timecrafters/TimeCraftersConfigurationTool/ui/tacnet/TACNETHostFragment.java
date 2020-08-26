package org.timecrafters.TimeCraftersConfigurationTool.ui.tacnet;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.TACNET;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.ServerDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.support.ConnectionStatsSyncHandler;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.support.ServerStatsSyncHandler;

public class TACNETHostFragment extends TimeCraftersFragment {

    private static final String TAG = "TACNETFragment";
    private ConnectionStatsSyncHandler connectionStatsSyncHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup viewGroup, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_tacnet_host, viewGroup, false);
        final LinearLayout container = (LinearLayout) root;


        if (Backend.instance().tacnet().status() != TACNET.Status.NOT_CONNECTED) {
            inflateTACNETConnectionStatus(container);
        } else {
            inflateTACNET(container);
        }

        return root;
    }

    private void inflateTACNET(final LinearLayout container) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_tacnet);
        final ConstraintLayout root = (ConstraintLayout) getLayoutInflater().inflate(R.layout.fragment_tacnet, null);
        container.removeAllViews();
        container.addView(root);

        final EditText hostname = root.findViewById(R.id.hostname);
        final EditText port = root.findViewById(R.id.port);

        final Button connectButton = root.findViewById(R.id.tacnet_connect);
        final Button startServerButton = root.findViewById(R.id.tacnet_start_server);

        hostname.setText(Backend.instance().getSettings().hostname);
        port.setText(String.valueOf(Backend.instance().getSettings().port));

        hostname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                Backend.instance().getSettings().hostname = hostname.getText().toString();
                Backend.instance().settingsChanged();
            }
        });

        port.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                Backend.instance().getSettings().port = Integer.parseInt(port.getText().toString());
                Backend.instance().settingsChanged();
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backend.instance().saveSettings();

                Backend.instance().tacnet().connect(hostname.getText().toString(), Integer.parseInt(port.getText().toString()));

                root.removeAllViews();
                inflateTACNETConnectionStatus(container);
            }
        });

        startServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerDialog dialog = new ServerDialog();
                dialog.show(getFragmentManager(), null);
            }
        });
    }

    private void inflateTACNETConnectionStatus(final LinearLayout container) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_tacnet_connection_status);
        final ConstraintLayout root = (ConstraintLayout) getLayoutInflater().inflate(R.layout.fragment_tacnet_connection_status, null);
        container.removeAllViews();
        container.addView(root);

        connectionStatsSyncHandler = new ConnectionStatsSyncHandler(root, 1_000);

        Button disconnect = root.findViewById(R.id.tacnet_disconnect);
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backend.instance().tacnet().close();
                Backend.instance().stopErrorSound();
                connectionStatsSyncHandler.stop();

                inflateTACNET(container);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (connectionStatsSyncHandler != null) {
            Log.d(TAG, "onDetach: stopping sync handler");
            connectionStatsSyncHandler.stop();
            connectionStatsSyncHandler = null;
        }
    }
}