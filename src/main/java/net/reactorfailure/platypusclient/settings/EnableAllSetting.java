package net.reactorfailure.platypusclient.settings;

import net.reactorfailure.platypusclient.dashboard.DashboardUI;
import net.reactorfailure.platypusclient.modules.Module;
import net.reactorfailure.platypusclient.modules.ModuleManager;

public class EnableAllSetting extends AbstractSettings{
    public EnableAllSetting() {
        super("enable_all", "Enable All Modules");
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
