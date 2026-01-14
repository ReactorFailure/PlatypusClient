package net.reactorfailure.platypusclient.settings;

import net.reactorfailure.platypusclient.dashboard.DashboardUI;
import net.reactorfailure.platypusclient.modules.core.Module;
import net.reactorfailure.platypusclient.modules.core.ModuleManager;
import net.reactorfailure.platypusclient.settings.core.AbstractSettings;

public class DisableAllSetting extends AbstractSettings {
    public DisableAllSetting() {
        super("setting_disableAll", "Disable All Modules");
    }

    @Override
    public void onClick() {
        for (Module module : ModuleManager.all()) {
            module.setEnabled(false);
        }

        DashboardUI ui = DashboardUI.getInstance();
        if (ui != null) {
            ui.refreshModuleButtons();
        }
    }
}
