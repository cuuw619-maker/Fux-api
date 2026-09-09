package com.cuuw619.fuxapi.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    private static final long START = System.currentTimeMillis();

    @Inject(method = "render", at = @At("HEAD"))
    private void fux$blackBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        g.fill(0, 0, g.guiWidth(), g.guiHeight(), 0xFF030405);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void fux$particles(GuiGraphics g, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        int w = g.guiWidth();
        int h = g.guiHeight();
        float t = (System.currentTimeMillis() - START) / 1000.0F;
        int accent = 0xFF8B5CF6;

        for (int i = 0; i < 58; i++) {
            float seed = i * 19.371F;
            float baseX = fract((float) Math.sin(seed * 12.9898F) * 43758.5453F);
            float baseY = fract((float) Math.sin(seed * 78.233F) * 12741.371F);
            float x = baseX * w + (float) Math.sin(t * (0.35F + i * 0.011F) + seed) * 28.0F;
            float y = h - ((baseY * (h + 80.0F) + t * (9.0F + (i % 6) * 3.0F)) % (h + 80.0F));
            float twinkle = 0.35F + 0.65F * (float) Math.sin(t * (1.1F + i * 0.06F) + seed);
            int alpha = Math.max(18, Math.min(105, (int) (twinkle * 90.0F)));
            int size = i % 13 == 0 ? 3 : (i % 4 == 0 ? 2 : 1);
            int ix = ((int) x % w + w) % w;
            int iy = ((int) y % h + h) % h;
            g.fill(ix, iy, ix + size, iy + size, (alpha << 24) | (accent & 0x00FFFFFF));
        }
    }

    private static float fract(float value) {
        return value - (float) Math.floor(value);
    }
}
