package net.reactorfailure.platypusclient.settings;

public class SettingsBootstrap {
    public static void init() {
        SettingsManager.register(new EnableAllSetting());
        SettingsManager.register(new DisableAllSetting());
    }
}
