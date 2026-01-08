package net.reactorfailure.platypusclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.reactorfailure.platypusclient.config.ConfigManager;
import net.reactorfailure.platypusclient.dashboard.DashboardAlertHUD;
import net.reactorfailure.platypusclient.dashboard.DashboardUI;
import net.reactorfailure.platypusclient.modules.core.ModuleBootstrap;
import net.reactorfailure.platypusclient.modules.core.ModuleManager;
import net.reactorfailure.platypusclient.settings.core.SettingsBootstrap;
import org.lwjgl.glfw.GLFW;

public class ClientSide implements ClientModInitializer {

    public static KeyBinding keyBinding;

    @Override
    public void onInitializeClient() {

        DashboardAlertHUD.register();
        SettingsBootstrap.init();
        ModuleBootstrap.init();
        ConfigManager.load();

        // Keybinding
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.platypusclient.open_dashboard",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            ModuleManager.tickAll();

            while (keyBinding.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new DashboardUI());
                }
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ConfigManager.save();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((h, c) -> {
            ConfigManager.save();
        });
    }
}