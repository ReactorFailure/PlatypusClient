package net.reactorfailure.platypusclient.dashboard;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.reactorfailure.platypusclient.config.ConfigManager;
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
    private final Map<Settings, ButtonWidget> settingButtons = new HashMap<>();
    private final Map<ModuleCategory, Boolean> categoryExpanded = new HashMap<>(ModuleCategory.class.getModifiers());

    private int scrollOffset = 0;
    private int maxScroll = 0;

    private boolean draggingScrollbar = false;
    private int dragOffsetY = 0;

    private final Screen parentScreen;

    // Panel sizes
    private static final int DASHBOARD_WIDTH = 240;
    private static final int SETTINGS_WIDTH = 122;
    private static final int ROW_HEIGHT = 24;
    private static final int CATEGORY_HEIGHT = 22;
    private static final int PANEL_GAP = 6;
    private static final int SCROLL_SPEED = 20;


    public DashboardUI() {
        this(null); // Called from in-game (G key)
    }

    public DashboardUI(Screen parentScreen) {
        super(Text.literal("PlatypusClient Dashboard"));
        this.parentScreen = parentScreen;
        INSTANCE = this;

        loadCategoryState();
    }

    public static DashboardUI getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void loadCategoryState() {
        Map<ModuleCategory, Boolean> saved =
                ConfigManager.loadCategoryStates();

        for (ModuleCategory category : ModuleCategory.values()) {
            if (saved != null && saved.containsKey(category)) {
                categoryExpanded.put(category, saved.get(category));
            } else {
                categoryExpanded.put(category, false); // default collapsed
            }
        }
    }

    private void toggleCategory(ModuleCategory category) {
        boolean newState = !categoryExpanded.getOrDefault(category, false);
        categoryExpanded.put(category, newState);

        ConfigManager.save(categoryExpanded);
        calculateMaxScroll();
        init();
        playDownSound();
    }


    @Override
    protected void init() {
        this.clearChildren();
        this.moduleButtons.clear();
        this.settingButtons.clear();

        int settingsCount = SettingsManager.all().size();
        int settingsHeight = settingsCount * ROW_HEIGHT + 40;

        int dashboardX = (this.width - DASHBOARD_WIDTH) / 2;
        int settingsX = dashboardX + DASHBOARD_WIDTH + PANEL_GAP;
        int settingsY = (this.height - settingsHeight) / 2;

        // Settings buttons
        int y = settingsY + 30;
        for (Settings setting : SettingsManager.all()) {
            ButtonWidget btn = ButtonWidget.builder(
                    Text.literal(setting.getDisplayName()),
                    button -> setting.onClick()
            ).dimensions(
                    settingsX + 12,
                    y,
                    SETTINGS_WIDTH - 24,
                    20
            ).build();

            this.addDrawableChild(btn);
            this.settingButtons.put(setting, btn);
            y += ROW_HEIGHT;
        }


        if (parentScreen != null) {
            int buttonSize = 20;
            int padding = 4;

            this.addDrawableChild(new DashboardButtonWidget(
                    this.width - buttonSize - padding,
                    padding,
                    buttonSize,
                    buttonSize,
                    button -> this.close() // Close to return to parent screen
            ));
        }

        int dashboardY = 50;
        int dashboardHeight = this.height - 100;
        int doneButtonWidth = 200;
        int doneButtonHeight = 20;
        int doneButtonX = dashboardX + (DASHBOARD_WIDTH - doneButtonWidth) / 2;
        int doneButtonY = dashboardY + dashboardHeight + 10; // 10px below dashboard

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                button -> this.close()
        ).dimensions(
                doneButtonX,
                doneButtonY,
                doneButtonWidth,
                doneButtonHeight
        ).build());

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

    public boolean handleMouseClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int dashboardX = (this.width - DASHBOARD_WIDTH) / 2;
            int dashboardY = 50;
            int currentY = dashboardY + 30 - scrollOffset;

            Map<ModuleCategory, List<Module>> categorized = ModuleManager.getByCategory();

            for (ModuleCategory category : ModuleCategory.values()) {
                // Check if clicked on category header
                if (mouseX >= dashboardX + 12 && mouseX <= dashboardX + DASHBOARD_WIDTH - 12 &&
                        mouseY >= currentY && mouseY <= currentY + CATEGORY_HEIGHT) {

                    // Toggle category
                    toggleCategory(category);
                    playDownSound();
                    calculateMaxScroll();
                    init(); // Rebuild UI
                    return true;
                }

                currentY += CATEGORY_HEIGHT;

                if (categoryExpanded.getOrDefault(category, false)) {
                    for (Module module : categorized.get(category)) {
                        int buttonX = dashboardX + DASHBOARD_WIDTH - 100;
                        int buttonY = currentY;

                        if (mouseX >= buttonX && mouseX <= buttonX + 90 &&
                                mouseY >= buttonY && mouseY <= buttonY + 20) {

                            module.setEnabled(!module.isEnabled());
                            playDownSound();
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
        if (super.mouseClicked(click, doubleClick)) {
            return true;
        }

        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        if (button == 0 && tryStartScrollbarDrag(mouseX, mouseY)) {
            return true;
        }

        return handleMouseClick(mouseX, mouseY, button);
    }

    private boolean tryStartScrollbarDrag(double mouseX, double mouseY) {
        if (maxScroll <= 0) return false;

        int dashboardX = (this.width - DASHBOARD_WIDTH) / 2;
        int dashboardY = 50;
        int dashboardHeight = this.height - 100;

        int scissorY = dashboardY + 30;
        int scissorHeight = dashboardHeight - 30;

        int scrollbarHeight = Math.max(
                20,
                scissorHeight * scissorHeight / (scissorHeight + maxScroll)
        );

        int scrollbarY = scissorY +
                (int) ((float) scrollOffset / maxScroll * (scissorHeight - scrollbarHeight));

        int scrollbarX1 = dashboardX + DASHBOARD_WIDTH - 6;
        int scrollbarX2 = dashboardX + DASHBOARD_WIDTH - 2;

        if (mouseX >= scrollbarX1 && mouseX <= scrollbarX2 &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {

            draggingScrollbar = true;
            dragOffsetY = (int) mouseY - scrollbarY;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (draggingScrollbar && maxScroll > 0) {
            double mouseY = click.y();

            int dashboardY = 50;
            int dashboardHeight = this.height - 100;

            int scissorY = dashboardY + 30;
            int scissorHeight = dashboardHeight - 30;

            int scrollbarHeight = Math.max(
                    20,
                    scissorHeight * scissorHeight / (scissorHeight + maxScroll)
            );

            int trackHeight = scissorHeight - scrollbarHeight;

            int relativeY = (int) mouseY - scissorY - dragOffsetY;
            relativeY = Math.max(0, Math.min(relativeY, trackHeight));

            scrollOffset = (int) ((float) relativeY / trackHeight * maxScroll);
            return true;
        }

        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (click.button() == 0) {
            draggingScrollbar = false;
        }
        return super.mouseReleased(click);
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

    public void refreshSettingButtons() {
        // Update setting button texts and colors
        for (Map.Entry<Settings, ButtonWidget> entry : settingButtons.entrySet()) {
            Settings setting = entry.getKey();
            ButtonWidget button = entry.getValue();

            button.setMessage(Text.literal(setting.getDisplayName()));
        }
    }

    @Override
    public void close() {
        ConfigManager.save(categoryExpanded);

        if (this.client != null) {
            this.client.setScreen(parentScreen);
        }

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

            context.fill(
                    dashboardX + 8,
                    currentY,
                    dashboardX + DASHBOARD_WIDTH - 8,
                    currentY + CATEGORY_HEIGHT,
                    0xFF2A2A2A
            );


            String arrow = expanded ? "▼" : "▶";
            int enabledCount = getEnabledCount(modules);

            String baseText = arrow + " " + category.getCategoryName() + " ";
            context.drawTextWithShadow(
                    this.textRenderer,
                    baseText,
                    dashboardX + 16,
                    currentY + 7,
                    0xFFFFFFFF
            );

            String countText = "(" + enabledCount + ")";
            int baseWidth = this.textRenderer.getWidth(baseText);

            int countColor = enabledCount > 0
                    ? 0xff55ff55   // green
                    : 0xFFFFFFFF;  // default white

            context.drawTextWithShadow(
                    this.textRenderer,
                    countText,
                    dashboardX + 16 + baseWidth,
                    currentY + 7,
                    countColor
            );

            currentY += CATEGORY_HEIGHT;

            // Draw modules if expanded
            if (expanded) {
                for (Module module : modules) {
                    // Module name with keybind
                    MutableText displayText = Text.literal(module.getName());

                    String keybindText = getKeybindText(module);
                    if (!keybindText.isEmpty()) {
                        displayText.append(
                                Text.literal(" (" + keybindText + ")")
                                        .formatted(Formatting.GOLD)
                        );
                    }

                    context.drawTextWithShadow(
                            this.textRenderer,
                            displayText,
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
                0xFFAAAAAA
        );

        int settingY = settingsY + 30;
        for (Settings setting : SettingsManager.all()) {
            ButtonWidget button = settingButtons.get(setting);
            if (button != null) {
                // Draw custom colored text over the button
                String displayName = setting.getDisplayName();
                int textWidth = this.textRenderer.getWidth(displayName);
                int textX = settingsX + 12 + (SETTINGS_WIDTH - 24 - textWidth) / 2;



                context.drawTextWithShadow(
                        this.textRenderer,
                        displayName,
                        textX,
                        settingY + 6,
                        setting.getTextColor()
                );
            }
            settingY += ROW_HEIGHT;
        }

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
                    // Calculate full display name with keybind
                    String displayName = module.getName();
                    String keybindText = getKeybindText(module);
                    if (!keybindText.isEmpty()) {
                        displayName += " (" + keybindText + ")";
                    }
                    int textWidth = this.textRenderer.getWidth(displayName);

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

    private int getEnabledCount(List<Module> modules) {
        int count = 0;
        for (Module module : modules) {
            if (module.isEnabled()) {
                count++;
            }
        }
        return count;
    }

    private String getKeybindText(Module module) {
        net.minecraft.client.option.KeyBinding keybinding = module.getKeyBinding();
        if (keybinding.isUnbound()) {
            return "";
        }
        return keybinding.getBoundKeyLocalizedText().getString();
    }
}