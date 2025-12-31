package net.reactorfailure.platypusclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class PersistentSneakModule extends AbstractModule {

    private static final PersistentSneakModule INSTANCE = new PersistentSneakModule();

    private PersistentSneakModule() {
        super("Persistent Sneak");
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
        if (!enabled || client.player == null) return;

        // Force sneak every tick (prevents unsneak)
        client.options.sneakKey.setPressed(true);
    }
}
