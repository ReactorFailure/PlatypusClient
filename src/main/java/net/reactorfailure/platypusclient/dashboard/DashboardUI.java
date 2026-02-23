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
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.Module;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;
import net.reactorfailure.platypusclient.modules.L_core.ModuleManager;
import net.reactorfailure.platypusclient.modules.L_utils.options.BooleanOption;
import net.reactorfailure.platypusclient.modules.L_utils.options.ModuleOptions;
import net.reactorfailure.platypusclient.settings.core.Settings;
import net.reactorfailure.platypusclient.settings.core.SettingsManager;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class DashboardUI extends Screen {

    private static DashboardUI INSTANCE;

    private final Map<Module, ButtonWidget>   moduleButtons  = new HashMap<>();
    private final Map<Settings, ButtonWidget> settingButtons = new HashMap<>();
    private final Map<ModuleCategory, Boolean> categoryExpanded =
            new HashMap<>(ModuleCategory.class.getModifiers());

    private int     scrollOffset      = 0;
    private int     maxScroll         = 0;
    private boolean draggingScrollbar = false;
    private int     dragOffsetY       = 0;

    private final Screen parentScreen;

    private static final int DASHBOARD_WIDTH  = 240;
    private static final int SETTINGS_WIDTH   = 122;
    private static final int ROW_HEIGHT       = 24;
    private static final int OPTION_ROW_HEIGHT = 18;
    private static final int CATEGORY_HEIGHT  = 22;
    private static final int PANEL_GAP        = 6;
    private static final int SCROLL_SPEED     = 20;

    private static final int CB_SIZE   = 9;
    private static final int CB_INDENT = 20;
    private static final int CB_BG_COLOR = 0xFF252535;


    public DashboardUI() {
        this(null);
    }

    public DashboardUI(Screen parentScreen) {
        super(Text.literal("PlatypusClient Dashboard"));
        this.parentScreen = parentScreen;
        INSTANCE = this;
        loadCategoryState();
    }

    public static DashboardUI getInstance() { return INSTANCE; }

    @Override public boolean shouldPause() { return false; }

    private void loadCategoryState() {
        Map<ModuleCategory, Boolean> saved = ConfigManager.loadCategoryStates();
        for (ModuleCategory cat : ModuleCategory.values()) {
            categoryExpanded.put(cat,
                    (saved != null && saved.containsKey(cat)) ? saved.get(cat) : false);
        }
    }

    private void toggleCategory(ModuleCategory category) {
        categoryExpanded.put(category, !categoryExpanded.getOrDefault(category, false));
        ConfigManager.save(categoryExpanded);
        calculateMaxScroll();
        init();
        playClickSound();
    }

    @Override
    protected void init() {
        this.clearChildren();
        this.moduleButtons.clear();
        this.settingButtons.clear();

        int settingsCount  = SettingsManager.all().size();
        int settingsHeight = settingsCount * ROW_HEIGHT + 40;

        int dashboardX = (this.width - DASHBOARD_WIDTH) / 2;
        int settingsX  = dashboardX + DASHBOARD_WIDTH + PANEL_GAP;
        int settingsY  = (this.height - settingsHeight) / 2;

        int y = settingsY + 30;
        for (Settings setting : SettingsManager.all()) {
            ButtonWidget btn = ButtonWidget.builder(
                    Text.literal(setting.getDisplayName()),
                    button -> setting.onClick()
            ).dimensions(settingsX + 12, y, SETTINGS_WIDTH - 24, 20).build();

            this.addDrawableChild(btn);
            this.settingButtons.put(setting, btn);
            y += ROW_HEIGHT;
        }

        if (parentScreen != null) {
            int btnSize = 20;
            int padding = 4;
            this.addDrawableChild(new DashboardButtonWidget(
                    this.width - btnSize - padding,
                    padding,
                    btnSize,
                    btnSize,
                    button -> this.close()
            ));
        }

        int dashboardY     = 50;
        int dashboardHeight = this.height - 100;
        int doneW = 200, doneH = 20;
        int doneX = dashboardX + (DASHBOARD_WIDTH - doneW) / 2;
        int doneY = dashboardY + dashboardHeight + 10;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                button -> this.close()
        ).dimensions(doneX, doneY, doneW, doneH).build());

        calculateMaxScroll();
    }

    private void calculateMaxScroll() {
        int totalHeight = 0;

        for (ModuleCategory cat : ModuleCategory.values()) {
            totalHeight += CATEGORY_HEIGHT;

            if (categoryExpanded.getOrDefault(cat, false)) {
                List<Module> modules = ModuleManager.getByCategory().get(cat);
                for (Module module : modules) {
                    totalHeight += ROW_HEIGHT;
                    if (module.isEnabled() && module instanceof AbstractModule abs) {
                        totalHeight += abs.getOptions().size() * OPTION_ROW_HEIGHT;
                    }
                }
            }
        }

        int dashboardHeight = this.height - 100;
        maxScroll = Math.max(0, totalHeight - dashboardHeight + 60);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY,
                                 double horizontalAmount, double verticalAmount) {
        int dashboardX = (this.width - DASHBOARD_WIDTH) / 2;
        if (mouseX >= dashboardX && mouseX <= dashboardX + DASHBOARD_WIDTH) {
            scrollOffset -= (int) (verticalAmount * SCROLL_SPEED);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubleClick) {
        if (super.mouseClicked(click, doubleClick)) return true;

        double mouseX = click.x();
        double mouseY = click.y();
        int    button = click.button();

        if (button == 0 && tryStartScrollbarDrag(mouseX, mouseY)) return true;
        return handleDashboardClick(mouseX, mouseY, button);
    }

    private boolean handleDashboardClick(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        int dashboardX = (this.width - DASHBOARD_WIDTH) / 2;
        int dashboardY = 50;
        int currentY   = dashboardY + 30 - scrollOffset;

        Map<ModuleCategory, List<Module>> categorized = ModuleManager.getByCategory();

        for (ModuleCategory cat : ModuleCategory.values()) {
            if (mouseX >= dashboardX + 12 && mouseX <= dashboardX + DASHBOARD_WIDTH - 12
                    && mouseY >= currentY && mouseY <= currentY + CATEGORY_HEIGHT) {
                toggleCategory(cat);
                return true;
            }
            currentY += CATEGORY_HEIGHT;

            if (!categoryExpanded.getOrDefault(cat, false)) continue;

            for (Module module : categorized.get(cat)) {
                int btnX = dashboardX + DASHBOARD_WIDTH - 100;
                if (mouseX >= btnX && mouseX <= btnX + 90
                        && mouseY >= currentY && mouseY <= currentY + 20) {
                    module.setEnabled(!module.isEnabled());

                    calculateMaxScroll();
                    playClickSound();
                    return true;
                }
                currentY += ROW_HEIGHT;

                if (module.isEnabled() && module instanceof AbstractModule abs) {
                    for (ModuleOptions<?> option : abs.getOptions()) {
                        if (option instanceof BooleanOption boolOpt) {
                            int cbX       = dashboardX + CB_INDENT;
                            int labelW    = this.textRenderer.getWidth(boolOpt.getLabel());
                            int clickEndX = cbX + CB_SIZE + 4 + labelW;

                            if (mouseX >= cbX && mouseX <= clickEndX
                                    && mouseY >= currentY && mouseY <= currentY + OPTION_ROW_HEIGHT) {
                                boolOpt.toggle();
                                ConfigManager.save();
                                playClickSound();
                                return true;
                            }
                        }
                        currentY += OPTION_ROW_HEIGHT;
                    }
                }
            }
        }

        return false;
    }

    private boolean tryStartScrollbarDrag(double mouseX, double mouseY) {
        if (maxScroll <= 0) return false;

        int dashboardX    = (this.width - DASHBOARD_WIDTH) / 2;
        int dashboardY    = 50;
        int dashboardHeight = this.height - 100;
        int scissorY      = dashboardY + 30;
        int scissorHeight = dashboardHeight - 30;

        int scrollbarH = Math.max(20, scissorHeight * scissorHeight / (scissorHeight + maxScroll));
        int scrollbarY = scissorY + (int) ((float) scrollOffset / maxScroll * (scissorHeight - scrollbarH));
        int sbX1 = dashboardX + DASHBOARD_WIDTH - 6;
        int sbX2 = dashboardX + DASHBOARD_WIDTH - 2;

        if (mouseX >= sbX1 && mouseX <= sbX2
                && mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarH) {
            draggingScrollbar = true;
            dragOffsetY = (int) mouseY - scrollbarY;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (draggingScrollbar && maxScroll > 0) {
            int dashboardY    = 50;
            int dashboardHeight = this.height - 100;
            int scissorY      = dashboardY + 30;
            int scissorHeight = dashboardHeight - 30;
            int scrollbarH    = Math.max(20, scissorHeight * scissorHeight / (scissorHeight + maxScroll));
            int trackH        = scissorHeight - scrollbarH;
            int relY          = (int) click.y() - scissorY - dragOffsetY;
            relY = Math.max(0, Math.min(relY, trackH));
            scrollOffset = (int) ((float) relY / trackH * maxScroll);
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (click.button() == 0) draggingScrollbar = false;
        return super.mouseReleased(click);
    }


    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (keyInput.key() == GLFW.GLFW_KEY_G) { this.close(); return true; }
        return super.keyPressed(keyInput);
    }

    @Override
    public void close() {
        ConfigManager.save(categoryExpanded);
        if (this.client != null) this.client.setScreen(parentScreen);
        INSTANCE = null;
    }

    private void playClickSound() {
        if (this.client != null) {
            this.client.getSoundManager().play(
                    net.minecraft.client.sound.PositionedSoundInstance.master(
                            net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
        }
    }


    public void refreshModuleButtons() { }

    public void refreshSettingButtons() {
        for (Map.Entry<Settings, ButtonWidget> e : settingButtons.entrySet()) {
            e.getValue().setMessage(Text.literal(e.getKey().getDisplayName()));
        }
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        calculateMaxScroll();
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        int settingsCount  = SettingsManager.all().size();
        int settingsHeight = settingsCount * ROW_HEIGHT + 40;

        int dashboardX    = (this.width - DASHBOARD_WIDTH) / 2;
        int settingsX     = dashboardX + DASHBOARD_WIDTH + PANEL_GAP;
        int dashboardY    = 50;
        int settingsY     = (this.height - settingsHeight) / 2;
        int dashboardHeight = this.height - 100;

        context.fill(dashboardX, dashboardY,
                dashboardX + DASHBOARD_WIDTH, dashboardY + dashboardHeight,
                0xFF1E1E1E);

        context.drawTextWithShadow(this.textRenderer,
                "PlatypusClient Dashboard",
                dashboardX + 12, dashboardY + 10, 0xFF00FFFF);

        int scissorY      = dashboardY + 30;
        int scissorHeight = dashboardHeight - 30;

        context.enableScissor(dashboardX, scissorY,
                dashboardX + DASHBOARD_WIDTH, scissorY + scissorHeight);

        int currentY = dashboardY + 30 - scrollOffset;
        Map<ModuleCategory, List<Module>> categorized = ModuleManager.getByCategory();

        for (ModuleCategory cat : ModuleCategory.values()) {
            List<Module> modules = categorized.get(cat);
            if (modules.isEmpty()) continue;

            boolean expanded = categoryExpanded.getOrDefault(cat, false);

            // Category header
            context.fill(dashboardX + 8, currentY,
                    dashboardX + DASHBOARD_WIDTH - 8, currentY + CATEGORY_HEIGHT,
                    0xFF2A2A2A);

            String arrow     = expanded ? "▼" : "▶";
            int    enabledCt = countEnabled(modules);
            String baseText  = arrow + " " + cat.getCategoryName() + " ";
            int    baseW     = this.textRenderer.getWidth(baseText);
            String countText = "(" + enabledCt + ")";
            int    countCol  = enabledCt > 0 ? 0xFF55FF55 : 0xFFFFFFFF;

            context.drawTextWithShadow(this.textRenderer,
                    baseText, dashboardX + 16, currentY + 7, 0xFFFFFFFF);
            context.drawTextWithShadow(this.textRenderer,
                    countText, dashboardX + 16 + baseW, currentY + 7, countCol);

            currentY += CATEGORY_HEIGHT;

            if (!expanded) continue;

            for (Module module : modules) {
                // Module name + keybind
                MutableText nameText = Text.literal(module.getName());
                String keybindStr = getKeybindText(module);
                if (!keybindStr.isEmpty()) {
                    nameText.append(Text.literal(" (" + keybindStr + ")").formatted(Formatting.GOLD));
                }
                context.drawTextWithShadow(this.textRenderer,
                        nameText, dashboardX + 20, currentY + 5, 0xFFCCCCCC);

                // Toggle button
                int btnX    = dashboardX + DASHBOARD_WIDTH - 100;
                int btnY    = currentY;
                Text btnTxt = module.isEnabled()
                        ? Text.literal("Enabled").formatted(Formatting.GREEN)
                        : Text.literal("Disabled").formatted(Formatting.RED);

                int btnColor = module.isEnabled() ? 0xFF006400 : 0xFF8B0000;
                context.fill(btnX, btnY, btnX + 90, btnY + 20, btnColor);
                // Border
                context.fill(btnX,      btnY,      btnX + 90, btnY + 1,      0xFF000000);
                context.fill(btnX,      btnY + 19, btnX + 90, btnY + 20,     0xFF000000);
                context.fill(btnX,      btnY,      btnX + 1,  btnY + 20,     0xFF000000);
                context.fill(btnX + 89, btnY,      btnX + 90, btnY + 20,     0xFF000000);

                int btnTxtW = this.textRenderer.getWidth(btnTxt);
                context.drawTextWithShadow(this.textRenderer,
                        btnTxt, btnX + (90 - btnTxtW) / 2, btnY + 6, 0xFFFFFFFF);

                currentY += ROW_HEIGHT;

                if (module.isEnabled() && module instanceof AbstractModule abs) {
                    for (ModuleOptions<?> option : abs.getOptions()) {
                        if (!(option instanceof BooleanOption boolOpt)) {
                            currentY += OPTION_ROW_HEIGHT;
                            continue;
                        }

                        context.fill(
                                dashboardX + CB_INDENT - 4, currentY,
                                dashboardX + DASHBOARD_WIDTH - 8, currentY + OPTION_ROW_HEIGHT,
                                CB_BG_COLOR);

                        int cbX = dashboardX + CB_INDENT;
                        int cbY = currentY + (OPTION_ROW_HEIGHT - CB_SIZE) / 2;

                        // Checkbox outer fill
                        context.fill(cbX, cbY, cbX + CB_SIZE, cbY + CB_SIZE, 0xFF555555);
                        // Checkbox border
                        context.fill(cbX,               cbY,               cbX + CB_SIZE, cbY + 1,              0xFFAAAAAA);
                        context.fill(cbX,               cbY + CB_SIZE - 1, cbX + CB_SIZE, cbY + CB_SIZE,        0xFFAAAAAA);
                        context.fill(cbX,               cbY,               cbX + 1,       cbY + CB_SIZE,        0xFFAAAAAA);
                        context.fill(cbX + CB_SIZE - 1, cbY,               cbX + CB_SIZE, cbY + CB_SIZE,        0xFFAAAAAA);

                        // Checkbox inner square
                        if (boolOpt.getValue()) {
                            context.fill(cbX + 2, cbY + 2, cbX + CB_SIZE - 2, cbY + CB_SIZE - 2, 0xFF00FF00);
                        }

                        // Checkbox label
                        context.drawTextWithShadow(this.textRenderer,
                                boolOpt.getLabel(),
                                cbX + CB_SIZE + 4,
                                currentY + (OPTION_ROW_HEIGHT - this.textRenderer.fontHeight) / 2,
                                boolOpt.getValue() ? 0xFFCCCCCC : 0xFF777777);

                        currentY += OPTION_ROW_HEIGHT;
                    }
                }
            }
        }

        context.disableScissor();

        // Scrollbar
        if (maxScroll > 0) {
            int sbH = Math.max(20, scissorHeight * scissorHeight / (scissorHeight + maxScroll));
            int sbY = scissorY + (int) ((float) scrollOffset / maxScroll * (scissorHeight - sbH));
            context.fill(dashboardX + DASHBOARD_WIDTH - 6, sbY,
                    dashboardX + DASHBOARD_WIDTH - 2, sbY + sbH,
                    0xFF888888);
        }

        context.fill(settingsX, settingsY,
                settingsX + SETTINGS_WIDTH, settingsY + settingsHeight,
                0xFF141414);
        context.drawTextWithShadow(this.textRenderer,
                "Settings", settingsX + 12, settingsY + 10, 0xFFAAAAAA);

        int settingY = settingsY + 30;
        for (Settings setting : SettingsManager.all()) {
            String displayName = setting.getDisplayName();
            int    tW          = this.textRenderer.getWidth(displayName);
            int    tX          = settingsX + 12 + (SETTINGS_WIDTH - 24 - tW) / 2;
            context.drawTextWithShadow(this.textRenderer,
                    displayName, tX, settingY + 6, setting.getTextColor());
            settingY += ROW_HEIGHT;
        }

        super.render(context, mouseX, mouseY, delta);
        renderTooltips(context, mouseX, mouseY, dashboardX, dashboardY);
    }


    private void renderTooltips(DrawContext context, int mouseX, int mouseY,
                                int dashboardX, int dashboardY) {
        int currentY = dashboardY + 30 - scrollOffset;
        Map<ModuleCategory, List<Module>> categorized = ModuleManager.getByCategory();

        for (ModuleCategory cat : ModuleCategory.values()) {
            currentY += CATEGORY_HEIGHT;
            if (!categoryExpanded.getOrDefault(cat, false)) continue;

            for (Module module : categorized.get(cat)) {
                String displayName = module.getName();
                String keybindStr  = getKeybindText(module);
                if (!keybindStr.isEmpty()) displayName += " (" + keybindStr + ")";
                int tW = this.textRenderer.getWidth(displayName);

                if (mouseX >= dashboardX + 20 && mouseX <= dashboardX + 20 + tW
                        && mouseY >= currentY + 5
                        && mouseY <= currentY + 5 + this.textRenderer.fontHeight) {
                    drawBorderedTooltip(context, module.getDescription(), mouseX, mouseY);
                    return;
                }

                currentY += ROW_HEIGHT;

                if (module.isEnabled() && module instanceof AbstractModule abs) {
                    currentY += abs.getOptions().size() * OPTION_ROW_HEIGHT;
                }
            }
        }
    }

    private void drawBorderedTooltip(DrawContext context, String description,
                                     int mouseX, int mouseY) {
        if (description == null || description.isEmpty()) return;

        int padding  = 4;
        int tW       = this.textRenderer.getWidth(description);
        int tH       = this.textRenderer.fontHeight;
        int tooltipW = tW + padding * 2;
        int tooltipH = tH + padding * 2;

        int tooltipX = mouseX + 10;
        int tooltipY = mouseY + 6;
        if (tooltipX + tooltipW > this.width) tooltipX = mouseX - tooltipW - 4;
        if (tooltipY < 0)                      tooltipY = 0;
        if (tooltipY + tooltipH > this.height) tooltipY = this.height - tooltipH;

        int x1 = tooltipX, y1 = tooltipY;
        int x2 = tooltipX + tooltipW, y2 = tooltipY + tooltipH;

        context.fill(x1, y1, x2, y2, 0xF0101010);

        int border = 0xFF00FFFF;
        context.fill(x1,     y1,     x2,     y1 + 1, border);
        context.fill(x1,     y2 - 1, x2,     y2,     border);
        context.fill(x1,     y1,     x1 + 1, y2,     border);
        context.fill(x2 - 1, y1,     x2,     y2,     border);

        context.drawTextWithShadow(this.textRenderer,
                description, x1 + padding, y1 + padding, 0xFFFFFFFF);
    }


    private int countEnabled(List<Module> modules) {
        int n = 0;
        for (Module m : modules) if (m.isEnabled()) n++;
        return n;
    }

    private String getKeybindText(Module module) {
        net.minecraft.client.option.KeyBinding kb = module.getKeyBinding();
        return kb.isUnbound() ? "" : kb.getBoundKeyLocalizedText().getString();
    }
}