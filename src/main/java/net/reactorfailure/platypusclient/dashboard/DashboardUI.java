package net.reactorfailure.platypusclient.dashboard;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.reactorfailure.platypusclient.modules.ModuleManager;
import net.reactorfailure.platypusclient.modules.Module;

import org.lwjgl.glfw.GLFW;

public class DashboardUI extends Screen {
    private static final int BOX_WIDTH = 260;
    private static final int ROW_HEIGHT = 28;

    public DashboardUI() {
        super(Text.literal("PlatypusClient Dashboard"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {

        int boxHeight = ModuleManager.all().size() * ROW_HEIGHT + 40;

        int x = (this.width - BOX_WIDTH) / 2;
        int y = (this.height - boxHeight) / 2 + 30;

        for (Module module : ModuleManager.all()) {
            addModuleRow(module, x, y);
            y += ROW_HEIGHT;
        }
    }


    private void addModuleRow(Module module, int x, int y) {
        // Toggle button
        this.addDrawableChild(
                ButtonWidget.builder(
                        getToggleText(module),
                        button -> {
                            module.setEnabled(!module.isEnabled());
                            button.setMessage(getToggleText(module));
                        }
                ).dimensions(
                        x + BOX_WIDTH - 100,
                        y,
                        90,
                        20
                ).build()
        );
    }

    private Text getToggleText(Module module) {
        return module.isEnabled()
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

        int boxHeight = ModuleManager.all().size() * ROW_HEIGHT + 40;

        int x = (this.width - BOX_WIDTH) / 2;
        int y = (this.height - boxHeight) / 2;

        // Background
        context.fill(
                x, y,
                x + BOX_WIDTH, y + boxHeight,
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

        // Draw module names
        int textY = y + 30;
        for (Module module : ModuleManager.all()) {
            context.drawTextWithShadow(
                    this.textRenderer,
                    module.getName(),
                    x + 12,
                    textY + 6,
                    0xFFFFFFFF
            );
            textY += ROW_HEIGHT;
        }

        super.render(context, mouseX, mouseY, delta);

    }
}
