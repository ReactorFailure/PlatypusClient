package net.reactorfailure.platypusclient.settings.core;

import net.reactorfailure.platypusclient.settings.*;

public class SettingsBootstrap {
    public static void init() {
        registerSettings(
                new EnableAllSetting(),
                new DisableAllSetting(),
                new ToggleAlertHUDSetting()
        );
    }

    private static void registerSettings(Settings ... settings) {
        for (Settings setting : settings) {
            SettingsManager.register(setting);
        }
    }
}
