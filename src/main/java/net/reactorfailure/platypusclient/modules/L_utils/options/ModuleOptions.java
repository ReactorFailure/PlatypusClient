package net.reactorfailure.platypusclient.modules.L_utils.options;

public interface ModuleOptions <T>{
    String getId();
    String getLabel();
    T getValue();
    void setValue(T value);
    Object saveToConfig();
    void loadFromConfig(Object raw);
}
