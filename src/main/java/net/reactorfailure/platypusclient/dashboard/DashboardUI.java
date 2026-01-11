package net.reactorfailure.platypusclient.dashboard;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.reactorfailure.platypusclient.modules.core.Module;
import net.reactorfailure.platypusclient.modules.core.ModuleCategory;
import net.reactorfailure.platypusclient.modules.core.ModuleManager;
import net.reactorfailure.platypusclient.settings.core.Settings;
import net.reactorfailure.platypusclient.settings.core.SettingsManager;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class DashboardUI extends Screen {

    private static DashboardUI INSTANCE;

    private final Map<Module, ButtonWidget> moduleButtons = new HashMap<>();
    private final Map<ModuleCategory, Boolean> categoryExpanded = new HashMap<>();

    private int scrollOffset = 0;
    private int maxScroll = 0;

    // Panel sizes
    private static final int DASHBOARD_WIDTH = 240;
    private static final int SETTINGS_WIDTH = 122;
    private static final int ROW_HEIGHT = 24;
    private static final int CATEGORY_HEIGHT = 22;
    private static final int PANEL_GAP = 6;
    private static final int SCROLL_SPEED = 20;

    public DashboardUI() {
        super(Text.literal("PlatypusClient Dashboard"));
        INSTANCE = this;

        // Initialize all categories as collapsed
        for (ModuleCategory category : ModuleCategory.values()) {
            categoryExpanded.put(category, false);
        }
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

        int settingsCount = SettingsManager.all().size();
        int settingsHeight = settingsCount * ROW_HEIGHT + 40;

        int dashboardX = (this.width - DASHBOARD_WIDTH) / 2;
        int settingsX = dashboardX + DASHBOARD_WIDTH + PANEL_GAP;
        int settingsY = (this.height - settingsHeight) / 2;

        // Settings buttons
        int y = settingsY + 30;
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

        // Calculate max scroll
        calculateMaxScroll();
    }

    private void calculateMaxScroll() {
        int totalHeight = 0;

        for (ModuleCategory category : ModuleCategory.values()) {
            totalHeight += CATEGORY_HEIGHT;

            if (categoryExpanded.getOrDefault(category, false)) {
                List<Module> modules = ModuleManager.getByCategory().get(category);
                totalHeight += modules.size() * ROW_HEIGHT;
            }
        }

        int dashboardHeight = this.height - 100;
        maxScroll = Math.max(0, totalHeight - dashboardHeight + 60);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int dashboardX = (this.width - DASHBOARD_WIDTH) / 2;

        // Check if mouse is over the dashboard panel
        if (mouseX >= dashboardX && mouseX <= dashboardX + DASHBOARD_WIDTH) {
            scrollOffset -= (int) (verticalAmount * SCROLL_SPEED);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    // Handle mouse clicks for category toggles and module buttons
    public boolean handleMouseClick(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            int dashboardX = (this.width - DASHBOARD_WIDTH) / 2;
            int dashboardY = 50;
            int currentY = dashboardY + 30 - scrollOffset;

            Map<ModuleCategory, List<Module>> categorized = ModuleManager.getByCategory();

            for (ModuleCategory category : ModuleCategory.values()) {
                // Check if clicked on category header
                if (mouseX >= dashboardX + 12 && mouseX <= dashboardX + DASHBOARD_WIDTH - 12 &&
                        mouseY >= currentY && mouseY <= currentY + CATEGORY_HEIGHT) {

                    // Toggle category
                    categoryExpanded.put(category, !categoryExpanded.getOrDefault(category, false));
                    calculateMaxScroll();
                    init(); // Rebuild UI
                    return true;
                }

                currentY += CATEGORY_HEIGHT;

                if (categoryExpanded.getOrDefault(category, false)) {
                    for (Module module : categorized.get(category)) {
                        // Check if clicked on module toggle button
                        int buttonX = dashboardX + DASHBOARD_WIDTH - 100;
                        int buttonY = currentY;

                        if (mouseX >= buttonX && mouseX <= buttonX + 90 &&
                                mouseY >= buttonY && mouseY <= buttonY + 20) {

                            module.setEnabled(!module.isEnabled());
                            return true;
                        }

                        currentY += ROW_HEIGHT;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubleClick) {
        // Let widgets handle their clicks first (they accept Click + boolean now)
        for (var child : this.children()) {
            if (child.mouseClicked(click, doubleClick)) {
                return true;
            }
        }

        // Extract coords and button from the Click record and use existing logic
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        // Then handle our custom dashboard clicks
        if (handleMouseClick(mouseX, mouseY, button)) {
            return true;
        }

        // Fall back to super (if it does something important)
        return super.mouseClicked(click, doubleClick);
    }

    private void playDownSound() {
        if (this.client != null) {
            this.client.getSoundManager().play(
                    net.minecraft.client.sound.PositionedSoundInstance.master(
                            net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK.value(),
                            1.0F
                    )
            );
        }
    }

    public void refreshModuleButtons() {
        // Force re-render
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
        int settingsCount = SettingsManager.all().size();
        int settingsHeight = settingsCount * ROW_HEIGHT + 40;

        int dashboardX = (this.width - DASHBOARD_WIDTH) / 2;
        int settingsX = dashboardX + DASHBOARD_WIDTH + PANEL_GAP;
        int dashboardY = 50;
        int settingsY = (this.height - settingsHeight) / 2;
        int dashboardHeight = this.height - 100;

        // Dashboard background
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

        // Enable scissor for scrolling area
        int scissorY = dashboardY + 30;
        int scissorHeight = dashboardHeight - 30;

        context.enableScissor(
                dashboardX,
                scissorY,
                dashboardX + DASHBOARD_WIDTH,
                scissorY + scissorHeight
        );

        int currentY = dashboardY + 30 - scrollOffset;
        Map<ModuleCategory, List<Module>> categorized = ModuleManager.getByCategory();

        for (ModuleCategory category : ModuleCategory.values()) {
            List<Module> modules = categorized.get(category);
            if (modules.isEmpty()) continue;

            boolean expanded = categoryExpanded.getOrDefault(category, false);

            // Draw category header with background
            context.fill(
                    dashboardX + 8,
                    currentY,
                    dashboardX + DASHBOARD_WIDTH - 8,
                    currentY + CATEGORY_HEIGHT,
                    0xFF2A2A2A  // Dark gray background for all categories
            );

            // Draw expand/collapse arrow
            String arrow = expanded ? "▼" : "▶";
            context.drawTextWithShadow(
                    this.textRenderer,
                    arrow + " " + category.getCategoryName() + " (" + modules.size() + ")",
                    dashboardX + 16,
                    currentY + 7,
                    0xFFFFFFFF
            );

            currentY += CATEGORY_HEIGHT;

            // Draw modules if expanded
            if (expanded) {
                for (Module module : modules) {
                    // Module name
                    context.drawTextWithShadow(
                            this.textRenderer,
                            module.getName(),
                            dashboardX + 20,
                            currentY + 5,
                            0xFFCCCCCC
                    );

                    // Toggle button
                    int buttonX = dashboardX + DASHBOARD_WIDTH - 100;
                    int buttonY = currentY;
                    Text buttonText = getToggleText(module);

                    // Button background
                    int buttonColor = module.isEnabled() ? 0xFF006400 : 0xFF8B0000;
                    context.fill(buttonX, buttonY, buttonX + 90, buttonY + 20, buttonColor);

                    // Button border (draw manually)
                    context.fill(buttonX, buttonY, buttonX + 90, buttonY + 1, 0xFF000000); // Top
                    context.fill(buttonX, buttonY + 19, buttonX + 90, buttonY + 20, 0xFF000000); // Bottom
                    context.fill(buttonX, buttonY, buttonX + 1, buttonY + 20, 0xFF000000); // Left
                    context.fill(buttonX + 89, buttonY, buttonX + 90, buttonY + 20, 0xFF000000); // Right

                    // Button text
                    int textWidth = this.textRenderer.getWidth(buttonText);
                    context.drawTextWithShadow(
                            this.textRenderer,
                            buttonText,
                            buttonX + (90 - textWidth) / 2,
                            buttonY + 6,
                            0xFFFFFFFF
                    );

                    currentY += ROW_HEIGHT;
                }
            }
        }

        context.disableScissor();

        // Draw scroll indicator if needed
        if (maxScroll > 0) {
            int scrollbarHeight = Math.max(20, scissorHeight * scissorHeight / (scissorHeight + maxScroll));
            int scrollbarY = scissorY + (int) ((float) scrollOffset / maxScroll * (scissorHeight - scrollbarHeight));

            context.fill(
                    dashboardX + DASHBOARD_WIDTH - 6,
                    scrollbarY,
                    dashboardX + DASHBOARD_WIDTH - 2,
                    scrollbarY + scrollbarHeight,
                    0xFF888888
            );
        }

        // Settings background
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
                0xFFFFAA00
        );

        super.render(context, mouseX, mouseY, delta);

        // Render tooltips
        renderTooltips(context, mouseX, mouseY, dashboardX, dashboardY);
    }

    private void renderTooltips(DrawContext context, int mouseX, int mouseY, int dashboardX, int dashboardY) {
        int currentY = dashboardY + 30 - scrollOffset;
        Map<ModuleCategory, List<Module>> categorized = ModuleManager.getByCategory();

        for (ModuleCategory category : ModuleCategory.values()) {
            currentY += CATEGORY_HEIGHT;

            if (categoryExpanded.getOrDefault(category, false)) {
                for (Module module : categorized.get(category)) {
                    int textWidth = this.textRenderer.getWidth(module.getName());

                    if (mouseX >= dashboardX + 20 && mouseX <= dashboardX + 20 + textWidth &&
                            mouseY >= currentY + 5 && mouseY <= currentY + 5 + this.textRenderer.fontHeight) {

                        context.drawTooltip(
                                this.textRenderer,
                                Text.literal(module.getDescription()),
                                mouseX,
                                mouseY
                        );
                        return;
                    }

                    currentY += ROW_HEIGHT;
                }
            }
        }
    }
}