package net.reactorfailure.platypusclient.modules;

public interface Module {
    String getName();
    boolean isEnabled();
    void setEnabled(boolean enabled);
    String getId();

    default void onEnable() {}
    default void onDisable() {}
    default void tick() {}
}
