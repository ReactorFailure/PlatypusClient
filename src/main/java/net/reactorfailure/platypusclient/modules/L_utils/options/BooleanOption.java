package net.reactorfailure.platypusclient.modules.L_utils.options;

import java.util.function.Consumer;

public class BooleanOption implements ModuleOptions<Boolean> {
    private final String id;
    private final String label;
    private boolean value;
    private final Consumer<Boolean> onChange;

    public BooleanOption(String id, String label, boolean initial, Consumer<Boolean> onChange) {
        this.id       = id;
        this.label    = label;
        this.value    = initial;
        this.onChange = onChange;
    }

    public BooleanOption(String id, String label, boolean initial) {
        this(id, label, initial, null);
    }

    @Override public String getId()    { return id; }
    @Override public String getLabel() { return label; }
    @Override public Boolean getValue() { return value; }

    @Override
    public void setValue(Boolean newValue) {
        if (this.value == newValue) return;
        this.value = newValue;
        if (onChange != null) onChange.accept(newValue);
    }

    public void toggle() {
        setValue(!value);
    }

    @Override
    public Object saveToConfig() {
        return value;
    }

    @Override
    public void loadFromConfig(Object raw) {
        if (raw instanceof Boolean b) {
            setValue(b);
        }
    }
}
