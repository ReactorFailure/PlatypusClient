package net.reactorfailure.platypusclient.modules;

import net.minecraft.client.MinecraftClient;
import net.reactorfailure.platypusclient.modules.core.AbstractModule;

public class PersistentRunModule extends AbstractModule {
    private static PersistentRunModule INSTANCE;

    public PersistentRunModule() {
        super("mod_pr", "Persistent run", "Makes you run without pressing the ctrl key");
        INSTANCE = this;
    }

    public static PersistentRunModule get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        client.options.sprintKey.setPressed(true);
    }

    @Override
    public void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        client.options.sprintKey.setPressed(false);
    }

    @Override
    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!isEnabled() || client.player == null) return;

        // Force sprint every tick (prevents stopping)
        client.options.sprintKey.setPressed(true);
    }
}
