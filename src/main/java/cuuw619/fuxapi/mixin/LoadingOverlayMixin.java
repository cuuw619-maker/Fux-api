package com.cuuw619.fuxapi.mixin;

import com.cuuw619.fuxapi.FuxSettingsRegistry;
import com.cuuw619.fuxapi.FuxUiAnimation;
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

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void fux$replaceVanillaLoader(GuiGraphics g, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        long elapsed = System.currentTimeMillis() - START;
        float t = elapsed / 1000.0F;
        int width = g.guiWidth();
        int height = g.guiHeight();
        int cx = width / 2;
        int cy = height / 2;
        int accent = accent();

        // Do not let the vanilla Mojang red surface render at all.
        g.fill(0, 0, width, height, 0xFF020204);
        renderBackground(g, width, height, t, accent);
        renderRings(g, cx, cy, t, accent);
        renderCore(g, cx, cy, t, accent);
        renderProgress(g, cx, cy, width, t, accent);
        ci.cancel();
    }

    private static void renderBackground(GuiGraphics g, int width, int height, float t, int accent) {
        if (FuxSettingsRegistry.COMPACT_MODE.get()) return;
        for (int i = 0; i < 128; i++) {
            float seed = i * 31.713F;
            float bx = fract((float) Math.sin(seed * 12.9898F) * 43758.5453F);
            float by = fract((float) Math.sin(seed * 78.233F) * 12741.371F);
            float speed = 5.0F + (i % 10) * 1.8F;
            float wave = (float) Math.sin(t * (0.35F + i * 0.006F) + seed) * (8.0F + i % 6 * 2.5F);
            float x = bx * width + wave;
            float y = height - ((by * (height + 120.0F) + t * speed) % (height + 120.0F));
            float pulse = 0.25F + 0.75F * (0.5F + 0.5F * (float) Math.sin(t * (1.1F + i * 0.018F) + seed));
            int alpha = Math.max(8, Math.min(115, (int) (82.0F * pulse)));
            int size = i % 29 == 0 ? 4 : (i % 7 == 0 ? 2 : 1);
            int px = ((int) x % width + width) % width;
            int py = ((int) y % height + height) % height;
            g.fill(px, py, px + size, py + size, (alpha << 24) | (accent & 0xFFFFFF));
        }
    }

    private static void renderRings(GuiGraphics g, int cx, int cy, float t, int accent) {
        int[] radii = {44, 62, 82};
        for (int ring = 0; ring < radii.length; ring++) {
            double rotation = t * (0.75 + ring * 0.18) * (ring == 1 ? -1.0 : 1.0) + ring * 1.4;
            for (int i = 0; i < 28; i++) {
                double a = rotation + i * Math.PI * 2.0 / 28.0;
                float wave = 0.75F + 0.25F * (float) Math.sin(t * 2.4F - i * 0.45F + ring);
                int alpha = (int) ((38 + ring * 16) * wave);
                int x = cx + (int) (Math.cos(a) * radii[ring]);
                int y = cy + (int) (Math.sin(a) * radii[ring] * 0.68F);
                g.fill(x - 1, y - 1, x + 2, y + 2, (alpha << 24) | (accent & 0xFFFFFF));
            }
        }
    }

    private static void renderCore(GuiGraphics g, int cx, int cy, float t, int accent) {
        float pulse = 1.0F + 0.06F * (float) Math.sin(t * 2.5F);
        g.pose().pushPose();
        g.pose().translate(cx, cy - 12, 0);
        g.pose().scale(pulse, pulse, 1.0F);
        g.drawCenteredString(Minecraft.getInstance().font, "FUX", 0, 0, 0xFFFFFFFF);
        g.pose().popPose();
        g.drawCenteredString(Minecraft.getInstance().font, "CLIENT", cx, cy + 20, accent);
    }

    private static void renderProgress(GuiGraphics g, int cx, int cy, int width, float t, int accent) {
        int barWidth = Math.min(340, Math.max(180, width / 3));
        int x = cx - barWidth / 2;
        int y = cy + 50;
        float target = 0.12F + 0.78F * (0.5F + 0.5F * (float) Math.sin(t * 1.15F));
        float progress = FuxUiAnimation.easeOutCubic(target);
        int filled = Math.max(2, Math.round(barWidth * progress));
        g.fill(x, y, x + barWidth, y + 2, 0xFF20242B);
        g.fill(x, y, x + filled, y + 2, accent);
        g.drawCenteredString(Minecraft.getInstance().font, "Loading client", cx, y + 12, 0xFF777D88);
    }

    private static int accent() {
        return switch (FuxSettingsRegistry.ACCENT.get()) {
            case "BLUE" -> 0xFF4F8CFF;
            case "GREEN" -> 0xFF45C98A;
            default -> 0xFF8B5CF6;
        };
    }

    private static float fract(float value) { return value - (float) Math.floor(value); }
}
