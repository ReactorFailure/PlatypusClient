package net.reactorfailure.platypusclient.modules.core;

public enum ModuleCategory {
    PLAYER("Player"),
    PERSISTENT("Persistent"),
//    HIGHLIGHT("Highlight"),
    COMBAT("Combat"),
    MISC("Misc");

    private final String categoryName;
    ModuleCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
