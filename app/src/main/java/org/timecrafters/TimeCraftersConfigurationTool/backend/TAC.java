package org.timecrafters.TimeCraftersConfigurationTool.backend;

import android.os.Build;
import android.os.Environment;

import java.io.File;

public class TAC {
    // TODO: Update filesystem handling
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "TimeCrafters_Configuration_Tool",
                               CONFIGS_PATH = ROOT_PATH + File.separator + "/configs",
                               SETTINGS_PATH = ROOT_PATH + File.separator + "settings.json";

    public static final int CONFIG_SPEC_VERSION = 2;

    // Set COMPETITION_MODE to true to disable automatic TACNET server start
    public static final boolean BUILD_COMPETITION_MODE = false;
    public static final boolean BUILD_AUTO_START = false;
    public static final String BUILD_AUTO_START_MODEL = "rev hub"; /* LOWERCASE */

    static public boolean allowAutoServerStart() {
        return !TAC.BUILD_COMPETITION_MODE &&
                ((TAC.BUILD_AUTO_START &&
                Build.MODEL.toLowerCase().contains(TAC.BUILD_AUTO_START_MODEL)) ||
                (Backend.instance() != null && Backend.instance().getSettings().mobileStartServerAtBoot));
    }
}
