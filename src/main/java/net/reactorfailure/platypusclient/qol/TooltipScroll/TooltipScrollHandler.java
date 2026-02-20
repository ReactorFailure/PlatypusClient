package net.reactorfailure.platypusclient.qol.TooltipScroll;

import net.minecraft.client.MinecraftClient;

public class TooltipScrollHandler {
    public static void register() {

    }

    public static void onScroll(double verticalAmount) {
        MinecraftClient client = MinecraftClient.getInstance();


        if (client.currentScreen != null) {
            TooltipScroll.getInstance().scroll(verticalAmount);
        } else {
            // Reset when not in any screen
            TooltipScroll.getInstance().reset();
        }
    }
}
