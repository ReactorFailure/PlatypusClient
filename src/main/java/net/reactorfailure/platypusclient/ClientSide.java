package net.reactorfailure.platypusclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.reactorfailure.platypusclient.dashboard.DashboardAlertHUD;
import net.reactorfailure.platypusclient.dashboard.DashboardUI;
import net.reactorfailure.platypusclient.modules.ModuleBootstrap;
import net.reactorfailure.platypusclient.modules.ModuleManager;
import net.reactorfailure.platypusclient.modules.NightVisionModule;
import net.reactorfailure.platypusclient.modules.PersistentSneakModule;

import org.lwjgl.glfw.GLFW;

public class ClientSide implements ClientModInitializer {

    public static KeyBinding keyBinding;

    @Override
    public void onInitializeClient() {

        DashboardAlertHUD.register();
        ModuleBootstrap.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ModuleManager.tickAll();
        });


        // Pressing G will open up the mod ui
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.platypusclient.open_dashboard",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }

            while (keyBinding.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new DashboardUI());
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PersistentSneakModule.get().tick();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            PersistentSneakModule.get().onDisable();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            NightVisionModule.get().tick();
        });
    }
}
