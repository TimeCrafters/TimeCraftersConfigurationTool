package org.timecrafters.TimeCraftersConfigurationTool.backend.config;


import java.util.ArrayList;

public class Preset {
    private ArrayList<Group> groups;
    private ArrayList<Action> actions;

    public Preset(ArrayList<Group> groups, ArrayList<Action> actions) {
        this.groups = groups;
        this.actions = actions;
    }
}