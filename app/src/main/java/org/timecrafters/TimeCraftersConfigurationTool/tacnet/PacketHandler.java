package org.timecrafters.TimeCraftersConfigurationTool.tacnet;

import android.util.Log;

import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;
import org.timecrafters.TimeCraftersConfigurationTool.backend.TAC;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;

import java.io.File;
import java.util.ArrayList;
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
                handleError(packet);
                return;
            }

            case DOWNLOAD_CONFIG: {
                handleDownloadConfig(packet);
                return;
            }

            case UPLOAD_CONFIG: {
                handleUploadConfig(packet);
                return;
            }

            case LIST_CONFIGS: {
                handleListConfigs(packet);
                return;
            }

            case ADD_CONFIG: {
                handleAddConfig(packet);
                return;
            }

            case UPDATE_CONFIG: {
                handleUpdateConfig(packet);
                return;
            }

            case DELETE_CONFIG: {
                handleDeleteConfig(packet);
                return;
            }

//            case CHANGE_ACTION: {
//                handleChangeAction(packet);
//                return;
//            }

            default: {
            }
        }
    }

    // NO-OP
    private void handleHandShake(Packet packet) {}
    // NO-OP
    private void handleHeartBeat(Packet packet) {}
    // NO-OP
    private void handleError(Packet packet) {}

    private void handleUploadConfig(Packet packet) {
        String[] split = packet.getContent().split("\\" + Packet.PROTOCOL_SEPERATOR, 2);
        final String configName = split[0];
        final String json = split[1];

        if (configName.length() == 0 && !Backend.instance().isConfigValid(json)) {
            return;
        }

        final String path = TAC.CONFIGS_PATH + File.separator + configName + ".json";

        Backend.instance().writeToFile(path, json);

        if (Backend.instance().getConfig().getName().equals(configName)) {
            Backend.instance().loadConfig(configName);
        }
    }

    private void handleDownloadConfig(Packet packet) {
        final String configName = packet.getContent();

        Log.i(TAG, "Got request for config: " + packet.getContent());
        Packet pkt;
        if (Backend.instance().configsList().contains(configName)) {
            final String path = TAC.CONFIGS_PATH + File.separator + configName + ".json";

            String content = Backend.instance().readFromFile(path);
            pkt = packetUploadConfig(configName, content);
        } else { // Error
            pkt = packetError("Remote config not found", "The requested config " + configName + " does not exist over here.");
        }

        if (hostIsAConnection) {
            Backend.instance().tacnet().puts(pkt.toString());
        } else {
            Backend.instance().getServer().getActiveClient().puts(pkt.toString());
        }
    }

    // TODO: reply with config_name,456|other_config,10 (config name,revision)
    private void handleListConfigs(Packet packet) {
        if (hostIsAConnection) {
            final String[] remoteConfigs = packet.getContent().split("\\" + Packet.PROTOCOL_SEPERATOR);
            ArrayList<String> diff = Backend.instance().configsList();

            for (String part : remoteConfigs) {
                final String[] configInfo = part.split(",", 2);
                final String name = configInfo[0];
                final int revision = Integer.parseInt(configInfo[1]);

                diff.remove(name);

                File file = new File(Backend.instance().configPath(name));

                if (file.exists()) {
                    final Config config = Backend.instance().loadConfigWithoutMutatingBackend(name);

                    if (config.getConfiguration().revision < revision) {
                        Log.i(TAG, "handleListConfigs: requesting config: " + name + " since local " + config.getName() + " is @ " + config.getConfiguration().revision);
                        Backend.instance().tacnet().puts(PacketHandler.packetDownloadConfig(name).toString());
                    } else if (config.getConfiguration().revision > revision) {
                        Log.i(TAG, "handleListConfigs: sending config: " + name + " since local " + config.getName() + " is @ " + config.getConfiguration().revision);
                        Backend.instance().tacnet().puts(PacketHandler.packetUploadConfig(name, Backend.instance().gsonForConfig().toJson(config)).toString());
                    }
                } else {
                    Log.i(TAG, "handleListConfigs: requesting config: " + name + " since there is no local file with that name");

                    Backend.instance().tacnet().puts( PacketHandler.packetDownloadConfig(name).toString() );
                }
            }

            for (String name : diff) {
                final Config config = Backend.instance().loadConfigWithoutMutatingBackend(name);

                Backend.instance().tacnet().puts(PacketHandler.packetUploadConfig(name, Backend.instance().gsonForConfig().toJson(config)).toString());
            }

        } else {
            Backend.instance().getServer().getActiveClient().puts(PacketHandler.packetListConfigs().toString());
        }
    }

    private void handleSelectConfig(Packet packet) {
        final String configName = packet.getContent();

        Backend.instance().getSettings().config = configName;
        Backend.instance().saveSettings();
        Backend.instance().loadConfig(configName);
    }

    private void handleAddConfig(Packet packet) {
        final String configName = packet.getContent();

        if (Backend.instance().configsList().contains(configName)) {
            if (!hostIsAConnection) {
                Backend.instance().getServer().getActiveClient().puts(
                        packetError("Config already exists!", "A config with the name " +
                                configName + " already exists here.").toString()
                );
            }
        } else {
            Backend.instance().writeNewConfig(configName);
        }
    }

    private void handleUpdateConfig(Packet packet) {
        final String[] split = packet.getContent().split("\\" + Packet.PROTOCOL_SEPERATOR, 2);
        final String oldConfigName = split[0];
        final String newConfigName = split[1];

        if (Backend.instance().configsList().contains(newConfigName)) {
            if (!hostIsAConnection) {
                Backend.instance().getServer().getActiveClient().puts(
                        packetError("Config already exists!", "A config with the name " +
                                newConfigName + " already exists here.").toString()
                );
            }
        } else {
            Backend.instance().moveConfig(oldConfigName, newConfigName);
        }
    }

    private void handleDeleteConfig(Packet packet) {
        final String configName = packet.getContent();

        Backend.instance().deleteConfig(configName);
    }

    private void handleAddGroup(Packet packet) {
        final String[] split = packet.getContent().split("\\" + Packet.PROTOCOL_SEPERATOR, 2);
        final String configName = packet.getContent();
        final String groupName  = packet.getContent();

        if (Backend.instance().getConfig().getName().equals(configName)) {
            if (Group.nameIsUnique(Backend.instance().getConfig().getGroups(), groupName)) {
                Group group = new Group(groupName, new ArrayList<Action>());
                Backend.instance().getConfig().getGroups().add(group);
            } else {
                Backend.instance().getServer().getActiveClient().puts(
                        packetError("Group name collision", "A group with the name " + groupName + " already exists").toString()
                );
            }
        } else {
            Backend.instance().getServer().getActiveClient().puts(
                    packetError("Active config mismatch", "Active config is not " + configName).toString()
            );
        }
    }

    private void handleUpdateGroup(Packet packet) {}

    private void handleDeleteGroup(Packet packet) {}

    private void handleAddAction(Packet packet) {}

    private void handleChangeAction(Packet packet) {
        // TODO: Handle renaming action and updating comment.
    }

    private void handleDeleteAction(Packet packet) {}

    private void handleAddVariable(Packet packet) {}

    private void handleUpdateVariable(Packet packet) {}

    private void handleChangeVariable(Packet packet) {}

    private void handleDeleteVariable(Packet packet) {}

    /**************************************
            PACKET HELPER FUNCTIONS
    **************************************/

    static public Packet packetHandShake(String clientUUID) {
        return Packet.create(Packet.PacketType.HANDSHAKE, clientUUID);
    }

    static public Packet packetHeartBeat() {
        return Packet.create(Packet.PacketType.HEARTBEAT, Packet.PROTOCOL_HEARTBEAT);
    }

    static private Packet packetError(String errorTitle, String errorMessage) {
        return Packet.create(Packet.PacketType.ERROR, errorTitle + Packet.PROTOCOL_SEPERATOR + errorMessage);
    }

    static public Packet packetUploadConfig(String configName, String json) {
        return Packet.create(Packet.PacketType.UPLOAD_CONFIG, configName + Packet.PROTOCOL_SEPERATOR + json);
    }

    static public Packet packetDownloadConfig(String configName) {
        return Packet.create(Packet.PacketType.DOWNLOAD_CONFIG, configName);
    }

    static public Packet packetListConfigs() {
        String configsList = "";
        final ArrayList<String> configs = Backend.instance().configsList();

        int i = 0;
        for (final String configName : configs) {
            final String path = Backend.instance().configPath(configName);
            Config config = Backend.instance().gsonForConfig().fromJson(Backend.instance().readFromFile(path), Config.class);

            configsList += configName + "," + config.getConfiguration().revision;

            if (i != configs.size() - 1) {
                configsList += Packet.PROTOCOL_SEPERATOR;
            }

            i++;
        }

        return Packet.create(Packet.PacketType.LIST_CONFIGS, configsList);
    }

    static public Packet packetSelectConfig(String configName) {
        return Packet.create(Packet.PacketType.SELECT_CONFIG, configName);
    }

    static public Packet packetAddConfig(String configName) {
        return Packet.create(Packet.PacketType.ADD_CONFIG, configName);
    }

    static public Packet packetUpdateConfig(String oldConfigName, String newConfigName) {
        return Packet.create(Packet.PacketType.UPDATE_CONFIG, oldConfigName + Packet.PROTOCOL_SEPERATOR + newConfigName);
    }

    static public Packet packetDeleteConfig(String configName) {
        return Packet.create(Packet.PacketType.DELETE_CONFIG, configName);
    }

    static public Packet packetAddGroup(String configName, String groupName) {
        return Packet.create(Packet.PacketType.ADD_GROUP, configName + Packet.PROTOCOL_SEPERATOR + groupName);
    }

    static public Packet packetUpdateGroup(String configName, String oldGroupName, String newGroupName) {
        return Packet.create(Packet.PacketType.UPDATE_GROUP, configName + Packet.PROTOCOL_SEPERATOR +
                                                                   oldGroupName + Packet.PROTOCOL_SEPERATOR + newGroupName);
    }

    static public Packet packetDeleteGroup(String configName, String groupName) {
        return Packet.create(Packet.PacketType.DELETE_GROUP, configName + Packet.PROTOCOL_SEPERATOR + groupName);
    }

    // TODO
    static public Packet packetAddAction(String configName, String groupName, String actionName) {
        return Packet.create(Packet.PacketType.ADD_ACTION, configName + Packet.PROTOCOL_SEPERATOR + groupName);
    }

    // TODO
    static public Packet packetUpdateAction(String configName, String oldGroupName, String newGroupName) {
        return Packet.create(Packet.PacketType.UPDATE_ACTION, configName + Packet.PROTOCOL_SEPERATOR +
                                                                   oldGroupName + Packet.PROTOCOL_SEPERATOR + newGroupName);
    }

    // TODO
    static public Packet packetDeleteAction(String configName, String groupName) {
        return Packet.create(Packet.PacketType.DELETE_ACTION, configName + Packet.PROTOCOL_SEPERATOR + groupName);
    }

    // TODO
    static public Packet packetAddVariable(String configName, String groupName) {
        return Packet.create(Packet.PacketType.ADD_VARIABLE, configName + Packet.PROTOCOL_SEPERATOR + groupName);
    }

    // TODO
    static public Packet packetUpdateVariable(String configName, String oldGroupName, String newGroupName) {
        return Packet.create(Packet.PacketType.UPDATE_VARIABLE, configName + Packet.PROTOCOL_SEPERATOR +
                                                                   oldGroupName + Packet.PROTOCOL_SEPERATOR + newGroupName);
    }

    static public Packet packetDeleteVariable(String configName, String groupName, String actionName, String variableName) {
        return Packet.create(Packet.PacketType.DELETE_VARIABLE, configName + Packet.PROTOCOL_SEPERATOR + groupName +
                                                                         Packet.PROTOCOL_SEPERATOR + actionName +
                                                                         Packet.PROTOCOL_SEPERATOR + variableName);
    }
}
