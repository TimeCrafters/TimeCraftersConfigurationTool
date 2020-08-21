package org.timecrafters.TimeCraftersConfigurationTool.library;

import android.os.Build;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.timecrafters.TimeCraftersConfigurationTool.R;

public class TimeCraftersFragment extends androidx.fragment.app.Fragment {
    public void floatingActionButtonAutoHide(final FloatingActionButton button, final ScrollView view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // API Level 23 (Android 6)
            view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    final int dy = scrollY - oldScrollY;
                    if (dy > 0 && button.getVisibility() == View.VISIBLE) {
                        button.hide();
                    } else if (dy < 0 && button.getVisibility() == View.GONE) {
                        button.show();
                    } else if (!view.canScrollVertically(1)) {
                        button.show();
                    }
                }
            });
        }
    }

    public void styleSwitch(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            buttonView.setBackground(getResources().getDrawable(R.drawable.button));
        } else {
            buttonView.setBackground(getResources().getDrawable(R.drawable.dangerous_button));
        }
    }
}
