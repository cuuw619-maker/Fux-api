package com.cuuw619.fuxapi.module;

public abstract class FuxModule {
    private final String name;
    private final String category;
    private boolean enabled;

    protected FuxModule(String name, String category) {
        this(name, category, false);
    }

    protected FuxModule(String name, String category, boolean enabled) {
        this.name = name;
        this.category = category;
        this.enabled = enabled;
    }

    public final String getName() { return name; }
    public final String getCategory() { return category; }
    public final boolean isEnabled() { return enabled; }

    public final void toggle() {
        setEnabled(!enabled);
    }

    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) onEnable(); else onDisable();
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onClientTick() {}
}
