package org.timecrafters.TimeCraftersConfigurationTool.backend;

import android.app.Application;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public class TAC {
    public static final int CONFIG_SPEC_VERSION = 2;

    static public boolean allowAutoServerStart() {
        return Backend.instance().getSettings().mobileStartServerAtBoot;
    }
}
