package net.reactorfailure.platypusclient.modules;

import net.minecraft.client.MinecraftClient;
import net.reactorfailure.platypusclient.modules.core.AbstractModule;

public class PersistentSneakModule extends AbstractModule {
    private static PersistentSneakModule INSTANCE;

    public PersistentSneakModule() {
        super("mod_ps", "Persistent Sneak");
        INSTANCE = this;
    }

    public static PersistentSneakModule get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        client.options.sneakKey.setPressed(true);
    }

    @Override
    public void onDisable() {
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
