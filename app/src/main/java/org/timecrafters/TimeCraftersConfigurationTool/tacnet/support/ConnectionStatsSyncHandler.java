package org.timecrafters.TimeCraftersConfigurationTool.tacnet.support;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.TACNET;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.Client;

public class ConnectionStatsSyncHandler {
    private View view;
    private Handler handler;
    private Runnable runner;
    private long delay;
    private boolean stopped = false;
    private TextView connectionStatus, totalPacketsIn, totalPacketsOut, totalDataIn, totalDataOut;

    public ConnectionStatsSyncHandler(View view, long delay) {
        this.view       = view;
        this.delay      = delay;
        connectionStatus= view.findViewById(R.id.connection_status);
        totalPacketsIn  = view.findViewById(R.id.total_packets_in);
        totalPacketsOut = view.findViewById(R.id.total_packets_out);
        totalDataIn     = view.findViewById(R.id.total_data_in);
        totalDataOut    = view.findViewById(R.id.total_data_out);

        handler = new Handler(Looper.getMainLooper());
        runner = new Runnable() {
            @Override
            public void run() {
                ConnectionStatsSyncHandler.this.run();
            }
        };

        handler.postDelayed(runner, 0);
    }

    public void run() {
        Client client = Backend.instance().tacnet().getClient();

        if (!stopped && client != null) {
            TACNET.Status status = Backend.instance().tacnet().status();
            switch(status) {
                case CONNECTED: {
                    connectionStatus.setText(R.string.tacnet_connected);
                    break;
                }
                case CONNECTING: {
                    connectionStatus.setText(R.string.tacnet_connecting);
                    break;
                }
                case NOT_CONNECTED: {
                    connectionStatus.setText(R.string.tacnet_not_connected);
                    break;
                }
                case CONNECTION_ERROR: {
                    connectionStatus.setText(Backend.instance().tacnet().getConnection().lastSocketError());
                    break;
                }
            }

            if (Backend.instance().tacnet().isConnectionError()) {
                connectionStatus.setTextColor(view.getResources().getColor(R.color.buttonDangerActive));
            } else {
                connectionStatus.setTextColor(view.getResources().getColor(R.color.text_color));
            }

            totalPacketsIn.setText("" + client.getPacketsSent());
            totalPacketsOut.setText("" + client.getPacketsReceived());
            totalDataIn.setText("" + client.getDataSent() + " bytes");
            totalDataOut.setText("" + client.getDataReceived() + " bytes");

            handler.postDelayed(runner, delay);

        } else if (!stopped && client == null) {
            if (Backend.instance().tacnet().isConnectionError()) {
                connectionStatus.setTextColor(view.getResources().getColor(R.color.buttonDangerActive));
            } else {
                connectionStatus.setTextColor(view.getResources().getColor(R.color.text_color));
            }

            connectionStatus.setText(Backend.instance().tacnet().getConnection().lastSocketError());

            handler.postDelayed(runner, delay);
        }
    }

    public void stop() {
        stopped = true;
    }
}
