package net.reactorfailure.platypusclient.modules.core;

import net.minecraft.client.option.KeyBinding;

public interface Module {
    String getName();
    String getDescription();

    ModuleCategory getCategoryName();

    boolean isEnabled();
    void setEnabled(boolean enabled);
    String getId();

    KeyBinding getKeyBinding();

    default void onEnable() {}
    default void onDisable() {}
    default void tick() {}
}
