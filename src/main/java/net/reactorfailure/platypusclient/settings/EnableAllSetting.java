package net.reactorfailure.platypusclient.settings;

import net.reactorfailure.platypusclient.dashboard.DashboardUI;
import net.reactorfailure.platypusclient.modules.L_core.Module;
import net.reactorfailure.platypusclient.modules.L_core.ModuleManager;
import net.reactorfailure.platypusclient.settings.core.AbstractSettings;

public class EnableAllSetting extends AbstractSettings {
    public EnableAllSetting() {
        super("setting_enableAll", "Enable All Modules");
    }

    @Override
    public void onClick() {
        for (Module module : ModuleManager.all()) {
            module.setEnabled(true);
        }

        DashboardUI ui = DashboardUI.getInstance();
        if (ui != null) {
            ui.refreshModuleButtons();
        }
    }
}
