package org.timecrafters.TimeCraftersConfigurationTool.dialogs;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import org.timecrafters.TimeCraftersConfigurationTool.R;

public class Dialog extends android.app.Dialog {
    public Dialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_base);

        ImageButton closeButton = findViewById(R.id.dialogCloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        Point point = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getSize(point);
        getWindow().setLayout((int) (point.x * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
