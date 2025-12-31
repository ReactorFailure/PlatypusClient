package net.reactorfailure.platypusclient.modules;

public abstract class AbstractModule implements Module{
    protected final String name;
    protected boolean enabled;

    protected AbstractModule(String name) {
        this.name = name;
        ModuleManager.register(this); // auto-register
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean value) {
        if (this.enabled == value) return;

        this.enabled = value;

        if (value) {
            onEnable();
        } else {
            onDisable();
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void tick() {}
}
