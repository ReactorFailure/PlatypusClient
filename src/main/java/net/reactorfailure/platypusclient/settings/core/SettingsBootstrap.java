package net.reactorfailure.platypusclient.settings.core;

import net.reactorfailure.platypusclient.settings.DisableAllSetting;
import net.reactorfailure.platypusclient.settings.EnableAllSetting;

public class SettingsBootstrap {
    public static void init() {
        SettingsManager.register(new EnableAllSetting());
        SettingsManager.register(new DisableAllSetting());
    }
}
