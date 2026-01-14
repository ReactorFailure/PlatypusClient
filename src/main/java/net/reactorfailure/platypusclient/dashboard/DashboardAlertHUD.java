package net.reactorfailure.platypusclient.dashboard;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.reactorfailure.platypusclient.modules.core.ModuleManager;
import net.reactorfailure.platypusclient.settings.ToggleAlertHUDSetting;

public class DashboardAlertHUD {
    public static void register() {
        HudRenderCallback.EVENT.register(DashboardAlertHUD::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (!ToggleAlertHUDSetting.isHudEnabled()) return;
        if (!ModuleManager.anyEnabled()) return;

        String text = "Platypus Client Enabled";

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        int textWidth = client.textRenderer.getWidth(text);

        int x = (screenWidth - textWidth) / 2;
        int y = screenHeight - 60;

        context.drawTextWithShadow(
                client.textRenderer,
                text,
                x,
                y,
                0x91a8a8a8
        );
    }
}