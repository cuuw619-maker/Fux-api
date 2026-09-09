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
    private void fux$renderBlackLoading(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        long elapsed = System.currentTimeMillis() - START;
        float pulse = 0.5F + 0.5F * (float) Math.sin(elapsed / 420.0D);
        int w = graphics.guiWidth();
        int h = graphics.guiHeight();
        int accent = 0xFF8B5CF6;

        graphics.fill(0, 0, w, h, 0xFF030405);
        renderParticles(graphics, w, h, elapsed, accent);

        int cx = w / 2;
        int cy = h / 2;
        Minecraft mc = Minecraft.getInstance();
        graphics.drawCenteredString(mc.font, "FUX", cx, cy - 34, 0xFFFFFFFF);
        graphics.drawCenteredString(mc.font, "CLIENT", cx, cy - 17, accent);

        int barW = Math.min(260, Math.max(120, w / 3));
        int barX = cx - barW / 2;
        int barY = cy + 18;
        graphics.fill(barX, barY, barX + barW, barY + 3, 0xFF20242B);
        int fill = (int) (barW * (0.18F + pulse * 0.64F));
        graphics.fill(barX, barY, barX + fill, barY + 3, accent);
        graphics.drawCenteredString(mc.font, "Initializing client", cx, barY + 14, 0xFF6E737C);
    }

    private static void renderParticles(GuiGraphics g, int w, int h, long elapsed, int accent) {
        float t = elapsed / 1000.0F;
        for (int i = 0; i < 42; i++) {
            float seed = i * 37.719F;
            float baseX = fract((float) Math.sin(seed * 12.9898F) * 43758.5453F);
            float baseY = fract((float) Math.sin(seed * 78.233F) * 12741.371F);
            float speed = 0.018F + (i % 7) * 0.004F;
            float x = (baseX * w + (float) Math.sin(t * (0.7F + i * 0.013F) + seed) * 22.0F) % w;
            float y = h - ((baseY * h + t * h * speed) % (h + 20.0F));
            float twinkle = 0.45F + 0.55F * (float) Math.sin(t * (1.5F + i * 0.07F) + seed);
            int alpha = Math.max(12, Math.min(80, (int) (twinkle * 70.0F)));
            int size = i % 11 == 0 ? 3 : (i % 3 == 0 ? 2 : 1);
            g.fill((int) x, (int) y, (int) x + size, (int) y + size, (alpha << 24) | (accent & 0x00FFFFFF));
        }
    }

    private static float fract(float value) {
        return value - (float) Math.floor(value);
    }
}
