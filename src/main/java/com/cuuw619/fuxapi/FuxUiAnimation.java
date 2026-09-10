package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSettingsRegistry;

public final class FuxUiAnimation {
    private FuxUiAnimation() {}

    public static float progress(long startedAt, long duration) {
        if (!FuxSettingsRegistry.ANIMATIONS.get()) return 1.0F;
        if (duration <= 0L) return 1.0F;
        return Math.min(1.0F, Math.max(0.0F, (System.currentTimeMillis() - startedAt) / (float) duration));
    }

    public static float easeOutCubic(float t) {
        t = clamp(t);
        float inv = 1.0F - t;
        return 1.0F - inv * inv * inv;
    }

    public static float easeInOutCubic(float t) {
        t = clamp(t);
        return t < 0.5F ? 4.0F * t * t * t : 1.0F - (float) Math.pow(-2.0F * t + 2.0F, 3.0F) / 2.0F;
    }

    public static float easeOutQuint(float t) {
        t = clamp(t);
        return 1.0F - (float) Math.pow(1.0F - t, 5.0F);
    }

    public static float smoothDamp(float current, float target, float factor) {
        factor = Math.max(0.0F, Math.min(1.0F, factor));
        return current + (target - current) * (1.0F - (float) Math.pow(1.0F - factor, 2.0F));
    }

    public static float easeOutBack(float t) {
        t = clamp(t);
        float c1 = 1.70158F;
        float c3 = c1 + 1.0F;
        float x = t - 1.0F;
        return 1.0F + c3 * x * x * x + c1 * x * x;
    }

    private static float clamp(float t) {
        return Math.min(1.0F, Math.max(0.0F, t));
    }
}
