package org.timecrafters.TimeCraftersConfigurationTool.library;

import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.timecrafters.TimeCraftersConfigurationTool.R;

public class TimeCraftersDialog extends DialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View v = View.inflate(getContext(), R.layout.dialog_base, null);

        ImageButton closeButton = v.findViewById(R.id.dialogCloseButton);

        if (isCancelable()) {
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } else {
            closeButton.setVisibility(View.GONE);
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
        getDialog().getWindow().setLayout((int) (point.x * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void styleSwitch(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            buttonView.setBackground(getResources().getDrawable(R.drawable.button));
        } else {
            buttonView.setBackground(getResources().getDrawable(R.drawable.dangerous_button));
        }
    }
}
