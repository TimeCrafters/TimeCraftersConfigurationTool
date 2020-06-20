package org.timecrafters.TimeCraftersConfigurationTool.tacnet;

import android.util.Log;

import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
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

            case DUMP_CONFIG: {
                handleDumpConfig(packet);
                return;
            }

            case CHANGE_ACTION: {
                handleChangeAction(packet);
                return;
            }

            default: {
                return;
            }
        }
    }

    // NO-OP
    private void handleHandShake(Packet packet) {}
    // NO-OP
    private void handleHeartBeat(Packet packet) {}

    private void handleDumpConfig(Packet packet) {
        if (
                packet.getContent().length() > 4 && packet.getContent().charAt(0) == "[".toCharArray()[0] &&
                        packet.getContent().charAt(packet.getContent().length() - 1) == "]".toCharArray()[0]
        ) { /* "unless" keyword anyone? */ } else { return; }

        Log.i(TAG, "Got valid json: " + packet.getContent());

        if (hostIsAConnection) {
            // save and reload menu
//            Writer.overwriteConfigFile(packet.getContent());

//            Backend.instance().loadConfig();
        } else {
            // save
//            Writer.overwriteConfigFile(packet.getContent());
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

    static public Packet packetDumpConfig(String string) {
        string = string.replace("\n", " ");

        return Packet.create(Packet.PacketType.DUMP_CONFIG, string);
    }
}
