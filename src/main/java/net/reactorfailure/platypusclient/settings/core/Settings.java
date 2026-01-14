package net.reactorfailure.platypusclient.settings.core;

public interface Settings {
    String getId();
    String getName();

    void onClick();

    default String getDisplayName() {
        return getName();
    }

    default int getTextColor() {
        return 0xFFFFFFFF; //White
    }
}
