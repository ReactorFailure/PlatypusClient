package net.reactorfailure.platypusclient.dashboard;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.reactorfailure.platypusclient.modules.core.Module;
import net.reactorfailure.platypusclient.modules.core.ModuleManager;
import net.reactorfailure.platypusclient.settings.core.Settings;
import net.reactorfailure.platypusclient.settings.core.SettingsManager;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class DashboardUI extends Screen {

    private static DashboardUI INSTANCE;

    private final Map<Module, ButtonWidget> moduleButtons = new HashMap<>();

    // Panel sizes
    private static final int DASHBOARD_WIDTH = 220;
    private static final int SETTINGS_WIDTH = 122;
    private static final int ROW_HEIGHT = 28;
    private static final int PANEL_GAP = 6;

    public DashboardUI() {
        super(Text.literal("PlatypusClient Dashboard"));
        INSTANCE = this;
    }

    public static DashboardUI getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        this.clearChildren();
        this.moduleButtons.clear();

        int moduleCount = ModuleManager.all().size();
        int settingsCount = SettingsManager.all().size();

        int dashboardHeight = moduleCount * ROW_HEIGHT + 40;
        int settingsHeight = settingsCount * ROW_HEIGHT + 40;

        int dashboardX = (this.width - DASHBOARD_WIDTH) / 2;
        int settingsX = dashboardX + DASHBOARD_WIDTH + PANEL_GAP;

        int dashboardY = (this.height - dashboardHeight) / 2;
        int settingsY = (this.height - settingsHeight) / 2;

        // module buttons
        int y = dashboardY + 30;
        for (Module module : ModuleManager.all()) {
            addModuleRow(module, dashboardX, y);
            y += ROW_HEIGHT;
        }

        // settings buttons
        y = settingsY + 30;
        for (Settings setting : SettingsManager.all()) {
            this.addDrawableChild(
                    ButtonWidget.builder(
                            Text.literal(setting.getName()),
                            btn -> setting.onClick()
                    ).dimensions(
                            settingsX + 12,
                            y,
                            SETTINGS_WIDTH - 24,
                            20
                    ).build()
            );
            y += ROW_HEIGHT;
        }
    }

    private void addModuleRow(Module module, int x, int y) {
        ButtonWidget button = ButtonWidget.builder(
                getToggleText(module),
                btn -> {
                    module.setEnabled(!module.isEnabled());
                    btn.setMessage(getToggleText(module));
                }
        ).dimensions(
                x + DASHBOARD_WIDTH - 100,
                y,
                90,
                20
        ).build();

        this.addDrawableChild(button);
        moduleButtons.put(module, button);
    }

    public void refreshModuleButtons() {
        for (Map.Entry<Module, ButtonWidget> entry : moduleButtons.entrySet()) {
            entry.getValue().setMessage(getToggleText(entry.getKey()));
        }
    }

    @Override
    public void close() {
        super.close();
        INSTANCE = null;
    }

    private Text getToggleText(Module module) {
        return module.isEnabled()
                ? Text.literal("Enabled").formatted(Formatting.GREEN)
                : Text.literal("Disabled").formatted(Formatting.RED);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (keyInput.key() == GLFW.GLFW_KEY_G) {
            this.close();
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        int moduleCount = ModuleManager.all().size();
        int settingsCount = SettingsManager.all().size();

        int dashboardHeight = moduleCount * ROW_HEIGHT + 40;
        int settingsHeight = settingsCount * ROW_HEIGHT + 40;

        int dashboardX = (this.width - DASHBOARD_WIDTH) / 2;
        int settingsX = dashboardX + DASHBOARD_WIDTH + PANEL_GAP;

        int dashboardY = (this.height - dashboardHeight) / 2;
        int settingsY = (this.height - settingsHeight) / 2;

        // dashboard background
        context.fill(
                dashboardX,
                dashboardY,
                dashboardX + DASHBOARD_WIDTH,
                dashboardY + dashboardHeight,
                0xFF1E1E1E
        );

        context.drawTextWithShadow(
                this.textRenderer,
                "PlatypusClient Dashboard",
                dashboardX + 12,
                dashboardY + 10,
                0xFF00FFFF
        );

        int textY = dashboardY + 30;
        for (Module module : ModuleManager.all()) {
            context.drawTextWithShadow(
                    this.textRenderer,
                    module.getName(),
                    dashboardX + 12,
                    textY + 6,
                    0xFFFFFFFF
            );
            textY += ROW_HEIGHT;
        }

        // settings background
        context.fill(
                settingsX,
                settingsY,
                settingsX + SETTINGS_WIDTH,
                settingsY + settingsHeight,
                0xFF141414
        );

        context.drawTextWithShadow(
                this.textRenderer,
                "Settings",
                settingsX + 12,
                settingsY + 10,
                0xFFAAAAAA
        );

        super.render(context, mouseX, mouseY, delta);
    }
}