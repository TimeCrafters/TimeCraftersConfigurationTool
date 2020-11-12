package org.timecrafters.TimeCraftersConfigurationTool.backend;

public class Settings {
    public String hostname, config;
    public int port;
    public boolean mobileShowNavigationLabels, mobileDisableLauncherDelay, mobileStartServerAtBoot;

    public Settings(String hostname, int port, String config, boolean mobileShowNavigationLabels,
                    boolean mobileDisableLauncherDelay, boolean mobileStartServerAtBoot) {
        this.hostname = hostname;
        this.port = port;
        this.config = config;

        this.mobileShowNavigationLabels = mobileShowNavigationLabels;
        this.mobileDisableLauncherDelay = mobileDisableLauncherDelay;
        this.mobileStartServerAtBoot = mobileStartServerAtBoot;
    }
}
