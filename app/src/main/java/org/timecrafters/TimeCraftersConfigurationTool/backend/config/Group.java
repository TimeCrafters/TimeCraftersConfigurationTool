package org.timecrafters.TimeCraftersConfigurationTool.backend.config;

import java.util.ArrayList;

public class Group {
    public String name;
    private ArrayList<Action> actions;

    public Group(String name, ArrayList<Action> actions) {
        this.name = name;
        this.actions = actions;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }
}
