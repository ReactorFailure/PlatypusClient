package net.reactorfailure.platypusclient.settings;

import net.reactorfailure.platypusclient.dashboard.DashboardUI;
import net.reactorfailure.platypusclient.settings.core.AbstractSettings;

public class ToggleAlertHUDSetting extends AbstractSettings {
    private static boolean hudEnabled = true;

    public ToggleAlertHUDSetting() {
        super("setting_toggleHUD", "Toggle Alert HUD");
    }

    @Override
    public void onClick() {
        hudEnabled = !hudEnabled;

        // Refresh the dashboard UI to update button text
        DashboardUI ui = DashboardUI.getInstance();
        if (ui != null) {
            ui.refreshSettingButtons();
        }
    }

    public static boolean isHudEnabled() {
        return hudEnabled;
    }

    public static void setHudEnabled(boolean enabled) {
        hudEnabled = enabled;
    }

    @Override
    public Object saveToConfig() {
        return hudEnabled;
    }

    @Override
    public void loadFromConfig(Object data) {
        if (data instanceof Boolean) {
            hudEnabled = (Boolean) data;
        }
    }

    public String getDisplayName() {
        return hudEnabled ? "Alert HUD: ON" : "Alert HUD: OFF";
    }

    public int getTextColor() {
        return hudEnabled ? 0xFF00FF00 : 0xFFFF0000; //Green, Red
    }
}
