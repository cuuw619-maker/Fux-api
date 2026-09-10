package com.cuuw619.fuxapi.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {
    private static final long START = System.currentTimeMillis();

    @Inject(method = "render", at = @At("TAIL"))
    private void fux$renderLoader(GuiGraphics g, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        long elapsed = System.currentTimeMillis() - START;
        float t = elapsed / 1000.0f;
        int width = g.guiWidth();
        int height = g.guiHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int accent = 0xFF8B5CF6;

        // The vanilla Mojang loader is rendered first. This final pass completely
        // covers it, including the red background, with an opaque black surface.
        g.fill(0, 0, width, height, 0xFF000000);

        renderBackgroundParticles(g, width, height, t, accent);
        renderGlowParticles(g, centerX, centerY, t, accent);
        renderLoaderRings(g, centerX, centerY, t, accent);
        renderCore(g, centerX, centerY, t, accent);
        renderProgress(g, centerX, centerY, width, t, accent);
    }

    private static void renderBackgroundParticles(GuiGraphics g, int width, int height, float t, int accent) {
        for (int i = 0; i < 72; i++) {
            float seed = i * 37.719f;
            float baseX = fract((float) Math.sin(seed * 12.9898f) * 43758.5453f);
            float baseY = fract((float) Math.sin(seed * 78.233f) * 12741.371f);
            float drift = 8.0f + (i % 9) * 2.0f;
            float x = baseX * width + (float) Math.sin(t * (0.25f + i * 0.009f) + seed) * drift;
            float y = height - ((baseY * (height + 100.0f) + t * (7.0f + (i % 7) * 2.5f)) % (height + 100.0f));
            float pulse = 0.35f + 0.65f * (float) Math.sin(t * (1.0f + i * 0.035f) + seed);
            int alpha = Math.max(12, Math.min(105, (int) (pulse * 82.0f)));
            int size = i % 17 == 0 ? 3 : (i % 4 == 0 ? 2 : 1);
            int px = ((int) x % width + width) % width;
            int py = ((int) y % height + height) % height;
            g.fill(px, py, px + size, py + size, (alpha << 24) | (accent & 0xFFFFFF));
        }
    }

    private static void renderGlowParticles(GuiGraphics g, int cx, int cy, float t, int accent) {
        for (int i = 0; i < 18; i++) {
            double angle = t * (0.45 + i * 0.006) + i * Math.PI * 2.0 / 18.0;
            float radius = 62.0f + (float) Math.sin(t * 1.4f + i) * 7.0f;
            int x = cx + (int) (Math.cos(angle) * radius);
            int y = cy + (int) (Math.sin(angle) * radius);
            int alpha = 35 + (int) (35.0f * (0.5f + 0.5f * (float) Math.sin(t * 2.0f + i)));
            g.fill(x - 1, y - 1, x + 2, y + 2, (alpha << 24) | (accent & 0xFFFFFF));
        }
    }

    private static void renderLoaderRings(GuiGraphics g, int cx, int cy, float t, int accent) {
        int[] radii = {44, 60, 76};
        for (int ring = 0; ring < radii.length; ring++) {
            float direction = ring == 1 ? -1.0f : 1.0f;
            double rotation = t * (0.9 + ring * 0.22) * direction + ring * 1.7;
            for (int i = 0; i < 24; i++) {
                double angle = rotation + i * Math.PI * 2.0 / 24.0;
                float fade = 0.15f + 0.85f * (0.5f + 0.5f * (float) Math.sin(t * 3.0f - i * 0.55f + ring));
                int alpha = Math.max(18, Math.min(150, (int) (fade * (70 + ring * 18))));
                int x = cx + (int) (Math.cos(angle) * radii[ring]);
                int y = cy + (int) (Math.sin(angle) * radii[ring] * 0.72f);
                g.fill(x - 1, y - 1, x + 2, y + 2, (alpha << 24) | (accent & 0xFFFFFF));
            }
        }
    }

    private static void renderCore(GuiGraphics g, int cx, int cy, float t, int accent) {
        float pulse = 1.0f + 0.055f * (float) Math.sin(t * 2.6f);
        g.pose().pushPose();
        g.pose().translate(cx, cy - 10, 0);
        g.pose().scale(pulse, pulse, 1.0f);
        g.drawCenteredString(Minecraft.getInstance().font, "FUX", 0, 0, 0xFFFFFFFF);
        g.pose().popPose();

        float sweep = (t * 1.4f) % 1.0f;
        int dotX = cx - 28 + (int) (56 * sweep);
        g.fill(dotX - 1, cy + 10, dotX + 3, cy + 14, accent);
        g.drawCenteredString(Minecraft.getInstance().font, "CLIENT", cx, cy + 20, accent);
    }

    private static void renderProgress(GuiGraphics g, int cx, int cy, int width, float t, int accent) {
        int barWidth = Math.min(320, Math.max(160, width / 3));
        int barX = cx - barWidth / 2;
        int barY = cy + 52;
        float progress = 0.12f + 0.76f * (0.5f + 0.5f * (float) Math.sin(t * 1.35f));
        int filled = Math.max(2, (int) (barWidth * progress));

        g.fill(barX, barY, barX + barWidth, barY + 2, 0xFF202020);
        g.fill(barX, barY, barX + filled, barY + 2, accent);
        g.drawCenteredString(Minecraft.getInstance().font, "Loading client", cx, barY + 12, 0xFF777777);
    }

    private static float fract(float value) {
        return value - (float) Math.floor(value);
    }
}
