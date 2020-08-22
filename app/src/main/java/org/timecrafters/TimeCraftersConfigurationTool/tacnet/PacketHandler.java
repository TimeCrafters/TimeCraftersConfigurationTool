package org.timecrafters.TimeCraftersConfigurationTool.tacnet;

import android.util.Log;

import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.TAC;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

public class PacketHandler {
    private static final String TAG = "TACNET|PacketHandler";
    private boolean hostIsAConnection = false;

    public PacketHandler(boolean isHostAConnection) {
        this.hostIsAConnection = isHostAConnection;
    }

    public void handle(String message) {
        Packet packet = Packet.fromStream(message);

        if (packet != null && packet.isValid()) {
            Log.i(TAG, "Received packet of type: " + packet.getPacketType());
            handOff(packet);
        } else {
            if (packet == null) {
                Log.i(TAG, "Rejected raw packet: " + message);
            } else {
                Log.i(TAG, "Rejected packet: " + packet.toString());
            }
        }
    }

    public void handOff(Packet packet) {
        switch(packet.getPacketType()) {
            case HANDSHAKE: {
                handleHandShake(packet);
                return;
            }

            case HEARTBEAT: {
                handleHeartBeat(packet);
                return;
            }

            case ERROR: {
//                handleHeartBeat(packet);
//                return;
            }

            case DOWNLOAD_CONFIG: {
                handleDownloadConfig(packet);
                return;
            }

            case UPLOAD_CONFIG: {
                handleUploadConfig(packet);
                return;
            }

//            case CHANGE_ACTION: {
//                handleChangeAction(packet);
//                return;
//            }

            default: {
                return;
            }
        }
    }

    // NO-OP
    private void handleHandShake(Packet packet) {}
    // NO-OP
    private void handleHeartBeat(Packet packet) {}

    private void handleUploadConfig(Packet packet) {
        String[] split = packet.getContent().split("\\" + Packet.PROTOCOL_HEADER_SEPERATOR, 2);
        final String configName = split[0];
        final String json = split[1];
        if (configName.length() == 0 && false) { //!Backend.instance().configIsValid(json)) {
            return;
        }
        if (configName.length() == 0) {
            return;
        }
        final String path = TAC.CONFIGS_PATH + File.separator + configName + ".json";

        Log.i(TAG, "Got valid json: " + packet.getContent());

        Backend.instance().writeToFile(path, json);
    }

    private void handleDownloadConfig(Packet packet) {
        final String configName = packet.getContent();

        Log.i(TAG, "Got request for config: " + packet.getContent());
        Packet pkt;
        if (Backend.instance().configsList().contains("" + configName + ".json")) {
            final String path = TAC.CONFIGS_PATH + File.separator + configName + ".json";

            String content = Backend.instance().readFromFile(path);
            Packet.create(Packet.PacketType.UPLOAD_CONFIG, content);
        } else { // Errored
            final String content = "ERROR";
            Packet.create(Packet.PacketType.ERROR, content);
        }

        if (hostIsAConnection) {
            Backend.instance().tacnet().puts(packet.toString());
        } else {
            Backend.instance().getServer().getActiveClient().puts(packet.toString());
        }
    }

    private void handleChangeAction(Packet packet) {
        // TODO: Handle renaming, deleting, and adding.
    }

    private void handleChangeVariable(Packet packet) {
        // TODO: Handle renaming, deleting, and adding.
    }

    static public Packet packetHandShake(String clientUUID) {
        return Packet.create(Packet.PacketType.HANDSHAKE, clientUUID);
    }

    static public Packet packetHeartBeat() {
        return Packet.create(Packet.PacketType.HEARTBEAT, Packet.PROTOCOL_HEARTBEAT);
    }

//    static public Packet packetDumpConfig(String string) {
//        string = string.replace("\n", " ");
//
//        return Packet.create(Packet.PacketType.DUMP_CONFIG, string);
//    }
}
