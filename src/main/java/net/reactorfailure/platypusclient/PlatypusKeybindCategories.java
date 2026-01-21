package net.reactorfailure.platypusclient;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PlatypusKeybindCategories {
    public static final KeyBinding.Category GENERAL = new KeyBinding.Category(
            Identifier.of("platypusclient", "general")
    );

    public static final KeyBinding.Category MODULES = new KeyBinding.Category(
            Identifier.of("platypusclient", "modules")
    );

    // Call this method during initialization to register the categories
    public static void init() {
        // Categories are registered automatically when created
    }
}
