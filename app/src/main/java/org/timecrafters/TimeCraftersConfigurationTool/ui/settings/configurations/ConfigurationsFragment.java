package org.timecrafters.TimeCraftersConfigurationTool.ui.settings.configurations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.timecrafters.TimeCraftersConfigurationTool.R;

public class ConfigurationsFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.activity_manage_configurations, container, false);


        LinearLayout v = root.findViewById(R.id.configurations);
        v.setBackgroundColor(getResources().getColor(R.color.list_even));

        View vv = v.inflate(getContext(), R.layout.fragment_configuration, null);
        v.addView(vv);

        return root;
    }
}
