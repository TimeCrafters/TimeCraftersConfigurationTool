package org.timecrafters.TimeCraftersConfigurationTool.ui.tacnet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.VariableDialog;

public class TACNETFragment extends Fragment {

    private TACNETViewModel TACNETViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TACNETViewModel =
                ViewModelProviders.of(this).get(TACNETViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tacnet, container, false);
        TACNETViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        Button connect = root.findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VariableDialog dialog = new VariableDialog();
                dialog.show(getFragmentManager(), null);
            }
        });

        return root;
    }
}