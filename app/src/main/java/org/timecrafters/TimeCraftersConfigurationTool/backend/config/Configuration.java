package org.timecrafters.TimeCraftersConfigurationTool.backend.config;

import java.util.Date;

public class Configuration {
    public Date createdAt, updatedAt;
    private int specVersion;
    public int revision;

    public Configuration(Date createdAt, Date updatedAt, int specVersion, int revision) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.specVersion = specVersion;
        this.revision = revision;
    }

    public int getSpecVersion() { return specVersion; }
}