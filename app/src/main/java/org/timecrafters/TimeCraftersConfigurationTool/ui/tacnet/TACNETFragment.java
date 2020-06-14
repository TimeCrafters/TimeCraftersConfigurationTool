package org.timecrafters.TimeCraftersConfigurationTool.ui.tacnet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.dialogs.Dialog;

import static android.view.View.inflate;

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
                Dialog dialog = new Dialog(getContext());
                dialog.show();

                ((TextView)dialog.findViewById(R.id.dialogTitle)).setText("Add Variable");
                LinearLayout view = dialog.findViewById(R.id.dialogContent);
                view.addView(getLayoutInflater().inflate(R.layout.dialog_edit_variable, null));
            }
        });

        return root;
    }
}