package net.reactorfailure.platypusclient.settings;

import net.minecraft.text.Text;

public class AbstractSettings implements Settings {
    protected final String id;
    protected final String name;

    protected AbstractSettings(String id, String name) {
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

    public Object saveToConfig() {
        return null;
    }

    public void loadFromConfig(Object data) {}

    @Override
    public void onClick() {}
}
