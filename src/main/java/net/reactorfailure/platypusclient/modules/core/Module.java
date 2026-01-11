package net.reactorfailure.platypusclient.modules.core;

public interface Module {
    String getName();
    String getDescription();

    ModuleCategory getCategoryName();

    boolean isEnabled();
    void setEnabled(boolean enabled);
    String getId();

    default void onEnable() {}
    default void onDisable() {}
    default void tick() {}
}
