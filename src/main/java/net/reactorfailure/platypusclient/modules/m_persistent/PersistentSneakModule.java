package net.reactorfailure.platypusclient.modules.m_persistent;

import net.minecraft.client.MinecraftClient;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;

public class PersistentSneakModule extends AbstractModule {
    private static PersistentSneakModule INSTANCE;

    public PersistentSneakModule() {
        super("mod_persistSneak", "Persistent Sneak", "Makes you sneak without pressing the shift key", ModuleCategory.PERSISTENT);
        INSTANCE = this;
    }

    public static PersistentSneakModule get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("PersistentSneakModule enabled");

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        client.options.sneakKey.setPressed(true);
    }

    @Override
    public void onDisable() {
        ClientSide.LOGGER.info("PersistentSneakModule disabled");

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        client.options.sneakKey.setPressed(false);
    }

    @Override
    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!isEnabled() || client.player == null) return;

        // Force sneak every tick (prevents unsneak)
        client.options.sneakKey.setPressed(true);
    }
}
