package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = FuxApi.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class FuxTitleScreenEffects {
    private static final long START = System.currentTimeMillis();
    private FuxTitleScreenEffects() {}

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static void background(ScreenEvent.BackgroundRendered event) {
        if (!(event.getScreen() instanceof TitleScreen)) return;
        GuiGraphics g = event.getGuiGraphics();
        int w = g.guiWidth(), h = g.guiHeight(), accent = accent();
        float t = (System.currentTimeMillis() - START) / 1000.0F;
        float fade = FuxUiAnimation.easeOutQuint(Math.min(1.0F, t / 0.9F));

        // Fully replace the vanilla panorama with a solid dark Fux surface.
        g.fill(0, 0, w, h, 0xFF020204);
        if (!FuxSettingsRegistry.COMPACT_MODE.get()) {
            renderParticles(g, w, h, t, fade, accent);
            renderStreaks(g, w, h, t, fade, accent);
        }
        renderAtmosphere(g, w, h, t, fade, accent);
    }

    private static void renderParticles(GuiGraphics g, int w, int h, float t, float fade, int accent) {
        for (int i = 0; i < 220; i++) {
            float seed = i * 17.9137F;
            float bx = fract((float) Math.sin(seed * 12.9898F) * 43758.5453F);
            float by = fract((float) Math.sin(seed * 78.233F) * 12741.371F);
            float speed = 3.0F + (i % 17) * 0.8F;
            float wave = (float) Math.sin(t * (0.28F + i * 0.004F) + seed) * (5.0F + i % 7 * 2.2F);
            float x = bx * w + wave;
            float y = h - ((by * (h + 120.0F) + t * speed) % (h + 120.0F));
            float pulse = 0.20F + 0.80F * (0.5F + 0.5F * (float) Math.sin(t * (0.75F + i * 0.013F) + seed));
            int alpha = Math.max(7, Math.min(105, Math.round(72.0F * pulse * fade)));
            int size = i % 37 == 0 ? 4 : (i % 9 == 0 ? 2 : 1);
            int ix = ((int) x % w + w) % w, iy = ((int) y % h + h) % h;
            g.fill(ix, iy, ix + size, iy + size, (alpha << 24) | (accent & 0xFFFFFF));
        }
    }

    private static void renderStreaks(GuiGraphics g, int w, int h, float t, float fade, int accent) {
        for (int i = 0; i < 42; i++) {
            float seed = i * 29.31F;
            float x = fract((float) Math.sin(seed) * 43758.5F) * w;
            float y = h - ((fract((float) Math.sin(seed * 2.7F) * 9321.7F) * (h + 70.0F) + t * (18.0F + i * 0.75F)) % (h + 70.0F));
            float pulse = 0.35F + 0.65F * (0.5F + 0.5F * (float) Math.sin(t * 1.7F + i));
            int alpha = Math.round((18.0F + 42.0F * pulse) * fade);
            int len = 4 + i % 13;
            g.fill((int) x, (int) y, (int) x + 1, (int) y + len, (alpha << 24) | (accent & 0xFFFFFF));
        }
    }

    private static void renderAtmosphere(GuiGraphics g, int w, int h, float t, float fade, int accent) {
        int cx = w / 2, cy = h / 2;
        for (int i = 0; i < 36; i++) {
            double a = t * (0.12 + i * 0.003) + i * Math.PI * 2.0 / 36.0;
            float r = 90.0F + (float) Math.sin(t * 0.65F + i * 0.8F) * 18.0F;
            int x = cx + (int) (Math.cos(a) * r);
            int y = cy + (int) (Math.sin(a) * r * 0.58F);
            int alpha = Math.round((14.0F + 24.0F * (0.5F + 0.5F * (float) Math.sin(t * 1.6F + i))) * fade);
            g.fill(x - 1, y - 1, x + 3, y + 3, (alpha << 24) | (accent & 0xFFFFFF));
        }
    }

    private static float fract(float v) { return v - (float) Math.floor(v); }
    private static int accent() { return switch (FuxSettingsRegistry.ACCENT.get()) { case "BLUE" -> 0xFF4F8CFF; case "GREEN" -> 0xFF45C98A; default -> 0xFF8B5CF6; }; }
}
