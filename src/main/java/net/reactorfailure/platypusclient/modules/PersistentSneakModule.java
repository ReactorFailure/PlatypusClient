package net.reactorfailure.platypusclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class PersistentSneakModule implements Module {

    private static final PersistentSneakModule INSTANCE = new PersistentSneakModule();
    private boolean enabled;

    private PersistentSneakModule() {}

    public static PersistentSneakModule get() {
        return INSTANCE;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean value) {
        enabled = value;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        KeyBinding sneak = client.options.sneakKey;

        if (!enabled) {
            sneak.setPressed(false); // release sneak when disabled
        }
    }


    public void tick() {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        KeyBinding sneak = client.options.sneakKey;
        KeyBinding jump = client.options.jumpKey;


        sneak.setPressed(!jump.isPressed());
    }


    public void disable() {
        enabled = false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.options.sneakKey.setPressed(false);
        }
    }
}
