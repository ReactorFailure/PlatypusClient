package net.reactorfailure.platypusclient.dashboard;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.reactorfailure.platypusclient.modules.NightVisionModule;
import net.reactorfailure.platypusclient.modules.PersistentSneakModule;
import org.lwjgl.glfw.GLFW;

public class DashboardUI extends Screen {
    private static final int BOX_WIDTH = 260;
    private static final int BOX_HEIGHT = 160;

    public DashboardUI() {
        super(Text.literal("PlatypusClient Dashboard"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        int x = (this.width - BOX_WIDTH) / 2;
        int y = (this.height - BOX_HEIGHT) / 2;

        // Persistent sneak button
        this.addDrawableChild(
                ButtonWidget.builder(
                        persistentSneakText(),
                        button -> {
                            var module = PersistentSneakModule.get();
                            module.setEnabled(!module.isEnabled());
                            button.setMessage(persistentSneakText());
                        }
                ).dimensions(x + 140, y + 45, 90, 20).build()
        );

        // Night Vision button
        this.addDrawableChild(
                ButtonWidget.builder(
                        nightVisionText(),
                        button -> {
                            var module = NightVisionModule.get();
                            module.setEnabled(!module.isEnabled());
                            button.setMessage(nightVisionText());
                        }
                ).dimensions(x + 140, y + 75, 90, 20).build()
        );


    }

    private Text persistentSneakText() {
        return PersistentSneakModule.get().isEnabled()
                ? Text.literal("Enabled").formatted(Formatting.GREEN)
                : Text.literal("Disabled").formatted(Formatting.RED);
    }

    private Text nightVisionText() {
        return NightVisionModule.get().isEnabled()
                ? Text.literal("Enabled").formatted(Formatting.GREEN)
                : Text.literal("Disabled").formatted(Formatting.RED);
    }


    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (keyInput.key() == GLFW.GLFW_KEY_G) {
            this.close(); // toggle close
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int x = (this.width - BOX_WIDTH) / 2;
        int y = (this.height - BOX_HEIGHT) / 2;

        // Background box
        context.fill(
                x, y,
                x + BOX_WIDTH, y + BOX_HEIGHT,
                0xFF1E1E1E
        );

        // Title
        context.drawTextWithShadow(
                this.textRenderer,
                "PlatypusClient Dashboard",
                x + 12,
                y + 10,
                0xFF00FFFF
        );

        // Labels
        context.drawTextWithShadow(
                this.textRenderer,
                "Persistent Sneak",
                x + 12,
                y + 50,
                0xFFFFFFFF
        );

        context.drawTextWithShadow(
                this.textRenderer,
                "Night vision",
                x + 12,
                y + 80,
                0xFFFFFFFF
        );

        context.drawTextWithShadow(
                this.textRenderer,
                "Show Hitbox",
                x + 12,
                y + 110,
                0xFFFFFFFF
        );

        super.render(context, mouseX, mouseY, delta);
    }
}
