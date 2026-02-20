package net.reactorfailure.platypusclient.modules.m_combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.reactorfailure.platypusclient.modules.core.AbstractModule;
import net.reactorfailure.platypusclient.modules.core.ModuleCategory;

public class InstantRespawnModule extends AbstractModule {
    private static InstantRespawnModule INSTANCE;

    public InstantRespawnModule() {
        super("mod_instRespawn", "Instant Respawn", "Makes you instantly respawn when you die", ModuleCategory.COMBAT);
        INSTANCE = this;
    }

    public static InstantRespawnModule get() {
        return INSTANCE;
    }

    @Override
    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!isEnabled() || client.player == null) return;

        if (client.currentScreen instanceof DeathScreen deathScreen) {
            client.player.requestRespawn();
            client.setScreen(null);
        }
    }
}
