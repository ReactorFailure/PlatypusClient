package net.reactorfailure.platypusclient.modules.m_player;

import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;

public class JesusModule extends AbstractModule {
    private static JesusModule INSTANCE;

    public JesusModule() {
        super("mod_jesus", "Become Jesus", "Walk on water", ModuleCategory.PLAYER);
        INSTANCE = this;
    }

    public static JesusModule get() { return INSTANCE; }
    public static boolean isActive() { return INSTANCE != null && INSTANCE.isEnabled(); }

    @Override
    public void onEnable() { ClientSide.LOGGER.info("Jesus mode enabled"); }

    @Override
    public void onDisable() { ClientSide.LOGGER.info("Jesus mode disabled"); }
}
