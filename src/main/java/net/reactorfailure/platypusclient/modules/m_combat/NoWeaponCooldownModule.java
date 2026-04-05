package net.reactorfailure.platypusclient.modules.m_combat;

import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;

public class NoWeaponCooldownModule extends AbstractModule {
    private static NoWeaponCooldownModule INSTANCE;

    public NoWeaponCooldownModule() {
        super("mod_noWeaponCooldown", "No Cooldown", "Removes weapon attack cooldown", ModuleCategory.COMBAT);
        INSTANCE = this;
    }

    public static NoWeaponCooldownModule get() { return INSTANCE; }
    public static boolean isActive() { return INSTANCE != null && INSTANCE.isEnabled(); }

    @Override
    public void onEnable() { ClientSide.LOGGER.info("No Cooldown enabled"); }

    @Override
    public void onDisable() { ClientSide.LOGGER.info("No Cooldown disabled"); }
}
