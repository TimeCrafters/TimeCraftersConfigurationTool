package org.timecrafters.TimeCraftersConfigurationTool.tacnet.support;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.Server;

import java.util.TimerTask;

public class ServerStatsSyncHandler {
    private View view;
    private Handler handler;
    private Runnable runner;
    private long delay;
    private boolean stopped = false;
    private TextView clientStatus, totalPacketsIn, totalPacketsOut, totalDataIn, totalDataOut;

    public ServerStatsSyncHandler(View view, long delay) {
        this.view       = view;
        this.delay      = delay;
        clientStatus    = view.findViewById(R.id.client_status);
        totalPacketsIn  = view.findViewById(R.id.total_packets_in);
        totalPacketsOut = view.findViewById(R.id.total_packets_out);
        totalDataIn     = view.findViewById(R.id.total_data_in);
        totalDataOut    = view.findViewById(R.id.total_data_out);

        handler = new Handler(Looper.getMainLooper());
        runner = new Runnable() {
            @Override
            public void run() {
                ServerStatsSyncHandler.this.run();
            }
        };

        handler.postDelayed(runner, 0);
    }

    public void run() {
        Server server = Backend.instance().getServer();

        if (!stopped && server != null) {
            if (server.hasActiveClient()) {
                clientStatus.setText("Connected");
            } else {
                clientStatus.setText("Disconnected");
            }

            totalPacketsIn.setText("" + server.getPacketsSent());
            totalPacketsOut.setText("" + server.getPacketsReceived());
            totalDataIn.setText("" + server.getDataSent() + " bytes");
            totalDataOut.setText("" + server.getDataReceived() + " bytes");

            handler.postDelayed(runner, delay);
        }
    }

    public void stop() {
        stopped = true;
    }
}
