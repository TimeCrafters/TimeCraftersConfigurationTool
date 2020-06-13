package org.timecrafters.TimeCraftersConfigurationTool.ui.tacnet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.timecrafters.TimeCraftersConfigurationTool.R;

public class TACNETFragment extends Fragment {

    private TACNETViewModel TACNETViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TACNETViewModel =
                ViewModelProviders.of(this).get(TACNETViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tacnet, container, false);
        final TextView textView = root.findViewById(R.id.text_tacnet);
        TACNETViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}