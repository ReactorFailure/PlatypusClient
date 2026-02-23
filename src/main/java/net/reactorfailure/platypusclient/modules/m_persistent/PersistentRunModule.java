package net.reactorfailure.platypusclient.modules.m_persistent;

import net.minecraft.client.MinecraftClient;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;

public class PersistentRunModule extends AbstractModule {
    private static PersistentRunModule INSTANCE;

    public PersistentRunModule() {
        super("mod_persistRun", "Persistent Run", "Makes you run without pressing the ctrl key", ModuleCategory.PERSISTENT);
        INSTANCE = this;
    }

    public static PersistentRunModule get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("PersistentRunModule enabled");

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        client.options.sprintKey.setPressed(true);
    }

    @Override
    public void onDisable() {
        ClientSide.LOGGER.info("PersistentRunModule disabled");

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
