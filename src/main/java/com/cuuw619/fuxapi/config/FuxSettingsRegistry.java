package com.cuuw619.fuxapi.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FuxSettingsRegistry {
    private static final List<FuxSetting<?>> SETTINGS = new ArrayList<>();

    public static final FuxSetting<Boolean> ANIMATIONS = register(new FuxSetting<>("Animations", true) {});
    public static final FuxSetting<Boolean> BLUR = register(new FuxSetting<>("Blur", true) {});
    public static final FuxSetting<Boolean> COMPACT_MODE = register(new FuxSetting<>("Compact mode", false) {});
    public static final FuxSetting<Double> UI_SCALE = register(new FuxSetting<>("UI scale", 1.0) {});
    public static final FuxSetting<String> ACCENT = register(new FuxSetting<>("Accent", "PURPLE") {});

    private FuxSettingsRegistry() {}

    private static <T extends FuxSetting<?>> T register(T setting) {
        SETTINGS.add(setting);
        return setting;
    }

    public static List<FuxSetting<?>> all() {
        return Collections.unmodifiableList(SETTINGS);
    }

    public static void load() {
        var data = FuxConfig.data();
        ANIMATIONS.set(data.animations);
        BLUR.set(data.blur);
        COMPACT_MODE.set(data.compactMode);
        UI_SCALE.set(data.uiScale);
        ACCENT.set(data.accent);
    }

    public static void save() {
        var data = FuxConfig.data();
        data.animations = ANIMATIONS.get();
        data.blur = BLUR.get();
        data.compactMode = COMPACT_MODE.get();
        data.uiScale = UI_SCALE.get();
        data.accent = ACCENT.get();
        FuxConfig.save();
    }
}
