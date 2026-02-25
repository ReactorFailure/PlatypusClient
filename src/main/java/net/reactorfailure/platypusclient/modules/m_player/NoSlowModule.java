package net.reactorfailure.platypusclient.modules.m_player;

import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;

public class NoSlowModule extends AbstractModule {
    private static NoSlowModule INSTANCE;

    public NoSlowModule() {
        super("mod_noSlow", "No Slow", "Nothing can slow you down :sunglasses:", ModuleCategory.PLAYER);
        INSTANCE = this;
    }

    public static NoSlowModule get() { return INSTANCE; }

    public static boolean isActive() {
        return INSTANCE != null && INSTANCE.isEnabled();
    }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("No Slow enabled");
    }

    @Override
    public void onDisable() {
        ClientSide.LOGGER.info("No Slow disabled");
    }
}
