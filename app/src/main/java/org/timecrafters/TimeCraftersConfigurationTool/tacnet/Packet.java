package org.timecrafters.TimeCraftersConfigurationTool.tacnet;

import android.util.Log;

import java.util.Arrays;

public class Packet {
    final static public String PROTOCOL_VERSION = "1";
    final static public String PROTOCOL_HEADER_SEPERATOR = "|";
    final static public String PROTOCOL_HEARTBEAT = "heartbeat";
    private static final String TAG = "TACNET|Packet";


    // NOTE: PacketType is cast to a char, no more than 255 packet types can exist unless
    //       header is updated.
    public enum PacketType {
        HANDSHAKE(0),
        HEARTBEAT(1),
        ERROR(2),

        DOWNLOAD_CONFIG(10),
        UPLOAD_CONFIG(11),

        ADD_GROUP(20),
        UPDATE_GROUP(21),
        DELETE_GROUP(22),

        ADD_ACTION(30),
        UPDATE_ACTION(31),
        DELETE_ACTION(32),

        ADD_VARIABLE(40),
        UPDATE_VARIABLE(41),
        DELETE_VARIABLE(42);

        private int id;
        final public int getId() {
            return id;
        }

        PacketType(int id) {
            this.id = id;
        }
    }

    private String protocolVersion;
    private PacketType packetType;
    private int contentLength;
    private String content;

    String rawMessage;

    Packet(String protocolVersion, PacketType packetType, int contentLength, String content) {
        this.protocolVersion = protocolVersion;
        this.packetType = packetType;
        this.contentLength = contentLength;
        this.content = content;
    }

    static public Packet fromStream(String message) {
        String version;
        PacketType type = null;
        int length;
        String body;

        String[] slice = message.split("\\|", 4);

        if (slice.length < 4) {
            Log.i(TAG, "Failed to split packet along first 4 " + PROTOCOL_HEADER_SEPERATOR + ". Raw return: " + Arrays.toString(slice));
            return null;
        }

        if (!slice[0].equals(PROTOCOL_VERSION)) {
            Log.i(TAG, "Incompatible protocol version received, expected: " + PROTOCOL_VERSION + " got: " + slice[0]);
            return null;
        }

        version = slice[0];
//        type = PacketType.values()[Integer.parseInt(slice[1])];
        length = Integer.parseInt(slice[2]);
        body = slice[slice.length - 1];

        int typeId = Integer.parseInt(slice[1]);
        for (PacketType packetType : PacketType.values()) {
            if (packetType.getId() == typeId) {
                type = packetType;
                break;
            }
        }

        if (type == null) {
            return null;
        }

        return new Packet(version, type, length, body);
    }

    static public Packet create(PacketType packetType, String message) {
        return new Packet(PROTOCOL_VERSION, packetType, message.length(), message);
    }

    public boolean isValid() {
        if (rawMessage == null) {
            return true;
        }

        String[] parts = rawMessage.split(PROTOCOL_HEADER_SEPERATOR);

        return parts[0].equals(PROTOCOL_VERSION) &&
                isPacketTypeValid( Integer.parseInt(parts[1]));
    }

    public boolean isPacketTypeValid(int rawPacketType) {
        return PacketType.values().length >= rawPacketType && PacketType.values()[rawPacketType] != null;
    }

    public String encodeHeader() {
        String string = "";
        string += PROTOCOL_VERSION;
        string += PROTOCOL_HEADER_SEPERATOR;
        string += packetType.getId();
        string += PROTOCOL_HEADER_SEPERATOR;
        string += contentLength;
        string += PROTOCOL_HEADER_SEPERATOR;
        return string;
    }

    public String toString() {
        return ("" + encodeHeader() + content);
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContent() {
        return content;
    }
}
