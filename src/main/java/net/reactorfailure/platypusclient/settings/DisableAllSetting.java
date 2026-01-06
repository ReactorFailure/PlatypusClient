package net.reactorfailure.platypusclient.settings;

import net.reactorfailure.platypusclient.dashboard.DashboardUI;
import net.reactorfailure.platypusclient.modules.Module;
import net.reactorfailure.platypusclient.modules.ModuleManager;

public class DisableAllSetting extends AbstractSettings{
    public DisableAllSetting() {
        super("disable_all", "Disable All Modules");
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
