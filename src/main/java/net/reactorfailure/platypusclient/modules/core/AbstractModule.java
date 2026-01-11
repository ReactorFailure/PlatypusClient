package net.reactorfailure.platypusclient.modules.core;

public abstract class AbstractModule implements Module {
    private final String name;
    private final String id;
    private final String description;
    private final ModuleCategory categoryName;
    private boolean enabled;

    protected AbstractModule(String id, String name, String description, ModuleCategory category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryName = category;
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
    public final String getDescription() {return description;}
    @Override
    public final ModuleCategory getCategoryName() {return categoryName;}

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
