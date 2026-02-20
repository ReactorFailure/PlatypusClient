package net.reactorfailure.platypusclient.modules.m_misc;

import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.core.AbstractModule;
import net.reactorfailure.platypusclient.modules.core.ModuleCategory;

public class TogglePortalSoundsModule extends AbstractModule {
    private static TogglePortalSoundsModule INSTANCE;

    public TogglePortalSoundsModule() {
        super("mod_togPortalSound", "No Portal Sound", "Disables annoying portal ambient sounds", ModuleCategory.MISC);
        INSTANCE = this;
    }

    public static TogglePortalSoundsModule get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Portal Sounds enabled");
    }

    @Override
    public void onDisable() {
        ClientSide.LOGGER.info("Portal Sounds disabled");
    }
}
