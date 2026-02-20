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
import net.reactorfailure.platypusclient.modules.core.Module;
import net.reactorfailure.platypusclient.modules.core.ModuleBootstrap;
import net.reactorfailure.platypusclient.modules.core.ModuleManager;
import net.reactorfailure.platypusclient.qol.TooltipScroll.TooltipScrollHandler;
import net.reactorfailure.platypusclient.settings.core.SettingsBootstrap;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSide implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("PlatypusClient");
    public static KeyBinding keyBinding;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing PlatypusClient...");

        DiscordRPCManager.get().connect();

        PlatypusKeybindCategories.init();
        DashboardAlertHUD.register();
        SettingsBootstrap.init();
        ModuleBootstrap.init();
        registerModuleKeybindings();


        TooltipScrollHandler.register();

        ConfigManager.load();

        LOGGER.info("Loaded {} modules", ModuleManager.all().size());

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            DiscordRPCManager.get().updatePresence();
        });

        // Dashboard keybinding
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.platypusclient.open_dashboard",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                PlatypusKeybindCategories.GENERAL
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            DiscordRPCManager.get().tick();

            if (client.player == null) return;

            ModuleManager.tickAll();

            while (keyBinding.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new DashboardUI());
                }
            }

            // Check module keybinds
            for (Module module : ModuleManager.all()) {
                while (module.getKeyBinding().wasPressed()) {
                    module.setEnabled(!module.isEnabled());
                    LOGGER.info("Toggled {} via keybind: {}",
                            module.getName(),
                            module.isEnabled() ? "ON" : "OFF");
                }
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            LOGGER.info("Disconnecting, saving config...");
            ConfigManager.save();

            DiscordRPCManager.get().updatePresence();
        });

        LOGGER.info("PlatypusClient initialized successfully!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ClientSide.LOGGER.info("JVM shutdown detected, cleaning up Discord RPC");
            DiscordRPCManager.get().disconnect();
        }));
    }

    private void registerModuleKeybindings() {
        for (Module module : ModuleManager.all()) {
            KeyBindingHelper.registerKeyBinding(module.getKeyBinding());
            LOGGER.info("Registered keybind for module: {}", module.getName());
        }
    }

}