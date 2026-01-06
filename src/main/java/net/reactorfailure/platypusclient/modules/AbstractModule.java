package net.reactorfailure.platypusclient.modules;

import net.reactorfailure.platypusclient.config.ConfigManager;

public abstract class AbstractModule implements Module{
    private final String name;
    private final String id;
    private boolean enabled;

    protected AbstractModule(String id, String name) {
        this.id = id;
        this.name = name;
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
