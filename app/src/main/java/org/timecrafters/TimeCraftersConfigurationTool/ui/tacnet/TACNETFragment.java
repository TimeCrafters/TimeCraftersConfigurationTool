package org.timecrafters.TimeCraftersConfigurationTool.ui.tacnet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.ServerDialog;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;

public class TACNETFragment extends TimeCraftersFragment {

    private static final String TAG = "TACNETFragment";
    private TACNETViewModel TACNETViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TACNETViewModel =
                ViewModelProviders.of(this).get(TACNETViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tacnet, container, false);
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
//                ConnectDialog dialog = new ConnectDialog();
//                dialog.show(getFragmentManager(), null);
                Backend.instance().saveSettings();

                Backend.instance().tacnet().connect(hostname.getText().toString(), Integer.parseInt(port.getText().toString()));
            }
        });

        startServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerDialog dialog = new ServerDialog();
                dialog.show(getFragmentManager(), null);
            }
        });

        TACNETViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        return root;
    }
}