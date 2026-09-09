package com.cuuw619.fuxapi;

public final class FuxUiAnimation {
    private FuxUiAnimation() {}

    public static float progress(long startedAt, long duration) {
        if (!FuxUiAnimationEnabled()) return 1.0F;
        return Math.min(1.0F, Math.max(0.0F, (System.currentTimeMillis() - startedAt) / (float) duration));
    }

    public static float easeOutCubic(float t) {
        t = Math.min(1.0F, Math.max(0.0F, t));
        float inv = 1.0F - t;
        return 1.0F - inv * inv * inv;
    }

    public static float easeOutBack(float t) {
        t = Math.min(1.0F, Math.max(0.0F, t));
        float c1 = 1.70158F;
        float c3 = c1 + 1.0F;
        float x = t - 1.0F;
        return 1.0F + c3 * x * x * x + c1 * x * x;
    }

    private static boolean FuxUiAnimationEnabled() {
        return com.cuuw619.fuxapi.config.FuxSettingsRegistry.ANIMATIONS.get();
    }
}
