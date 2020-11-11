package org.timecrafters.TimeCraftersConfigurationTool.tacnet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.timecrafters.TimeCraftersConfigurationTool.backend.TAC;

public class TACNETOnBootReceiver extends BroadcastReceiver {
    private static final String TAG = "TACNETOnBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (TAC.allowAutoServerStart()) {
                Log.i(TAG, "Auto starting TACNET Server Service...");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, TACNETServerService.class));
                } else {
                    context.startService(new Intent(context, TACNETServerService.class));
                }
            } else {
                Log.i(TAG, "Auto starting TACNET Server Service is not permitted. Check TAC.BUILD_ variables.");
            }
        }
    }
}
