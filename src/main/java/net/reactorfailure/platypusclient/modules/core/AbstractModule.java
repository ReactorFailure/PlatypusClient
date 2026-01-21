package net.reactorfailure.platypusclient.modules.core;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public abstract class AbstractModule implements Module {
    private final String name;
    private final String id;
    private final String description;
    private final ModuleCategory categoryName;
    private boolean enabled;
    private final KeyBinding keyBinding;

    protected AbstractModule(String id, String name, String description, ModuleCategory category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryName = category;

        // Create keybinding with UNBOUND default key
        this.keyBinding = new KeyBinding(
                "key.platypusclient.module." + id,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN, // Unbound by default
                net.reactorfailure.platypusclient.PlatypusKeybindCategories.MODULES
        );
    }

    @Override
    public final String getId() {
        return id;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String getDescription() {
        return description;
    }

    @Override
    public final ModuleCategory getCategoryName() {
        return categoryName;
    }

    @Override
    public final KeyBinding getKeyBinding() {
        return keyBinding;
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;

        this.enabled = enabled;

        if (enabled) onEnable();
        else onDisable();
    }

    @Override
    public void tick() {}

    public void onEnable() {}
    public void onDisable() {}

    public Object saveToConfig() {
        return null;
    }

    public void loadFromConfig(Object data) {}
}
