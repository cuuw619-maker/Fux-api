package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@EventBusSubscriber(modid = FuxApi.MOD_ID, value = Dist.CLIENT)
public final class FuxHudOverlay {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static float smoothYaw;
    private static boolean yawReady;
    private static float targetAlpha;
    private static float targetScale = 1.0F;
    private static LivingEntity currentTarget;
    private FuxHudOverlay() {}

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance(); LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui || mc.screen != null) return;
        GuiGraphics g = event.getGuiGraphics(); int width = g.guiWidth(), height = g.guiHeight(), accent = accent();
        float target = player.getYRot();
        if (!yawReady) { smoothYaw = target; yawReady = true; }
        else smoothYaw = FuxUiAnimation.smoothDamp(smoothYaw, smoothYaw + shortest(target - smoothYaw), 0.11F);

        drawCompass(g, Math.round(FuxHudLayout.data().compassX * width), Math.round(FuxHudLayout.data().compassY * height), width, smoothYaw, accent);
        drawInfo(g, Math.round(FuxHudLayout.data().infoX * width), Math.round(FuxHudLayout.data().infoY * height), mc, accent);
        drawTarget(g, width, height, player, accent);
    }

    private static void drawCompass(GuiGraphics g, int center, int top, int width, float yaw, int accent) {
        int cw = Math.min(720, width - 80), left = center - cw / 2, right = center + cw / 2, height = 42;
        float dpp = 180f / cw, heading = normalize(yaw);
        for (int deg = -180; deg <= 180; deg += 10) {
            float delta = shortest((heading + deg) - heading); int x = center + Math.round(delta / dpp);
            if (x < left || x > right) continue;
            boolean major = deg % 30 == 0; int tick = major ? 11 : 6;
            g.fill(x, top + height - tick, x + 1, top + height, major ? accent : 0xFF666B76);
            if (major) { String label = directionFor(heading + deg); g.drawCenteredString(Minecraft.getInstance().font, label, x, top + 4, isCardinal(label) ? 0xFFFFFFFF : 0xFF9DA2AC); }
        }
        g.fill(center - 1, top + 18, center + 1, top + height, accent);
        g.drawCenteredString(Minecraft.getInstance().font, "▼", center, top + height - 4, 0xFFFFFFFF);
    }

    private static void drawInfo(GuiGraphics g, int right, int top, Minecraft mc, int accent) {
        g.drawString(mc.font, mc.getFps() + " FPS", right - 66, top + 4, 0xFFE8E9ED, false);
        g.drawString(mc.font, LocalTime.now().format(CLOCK), right - 66, top + 20, accent, false);
    }

    private static void drawTarget(GuiGraphics g, int width, int height, LocalPlayer player, int accent) {
        LivingEntity target = findTarget(player);
        targetAlpha = FuxUiAnimation.smoothDamp(targetAlpha, target == null ? 0.0F : 1.0F, 0.16F);
        targetScale = FuxUiAnimation.smoothDamp(targetScale, target == null ? 0.96F : 1.0F, 0.12F);
        if (target == null || targetAlpha < 0.02F) { currentTarget = null; return; }
        currentTarget = target;
        int cx = Math.round(FuxHudLayout.data().targetEspX * width), cy = Math.round(FuxHudLayout.data().targetEspY * height);
        int box = Math.round(42 * targetScale);
        int alpha = Math.max(0, Math.min(255, Math.round(210 * targetAlpha)));
        int col = (alpha << 24) | (accent & 0x00FFFFFF);
        g.fill(cx - box, cy - box, cx - box + 2, cy + box, col);
        g.fill(cx + box - 2, cy - box, cx + box, cy + box, col);
        g.fill(cx - box, cy - box, cx + box, cy - box + 2, col);
        g.fill(cx - box, cy + box - 2, cx + box, cy + box, col);
        String name = target.getName().getString();
        if (name.length() > 18) name = name.substring(0, 18);
        int textAlpha = Math.max(0, Math.min(255, Math.round(235 * targetAlpha)));
        g.drawCenteredString(Minecraft.getInstance().font, name, cx, cy + box + 6, (textAlpha << 24) | 0x00FFFFFF);
    }

    private static LivingEntity findTarget(LocalPlayer player) {
        if (player.level() == null) return null;
        LivingEntity best = null; double bestDistance = 8.0 * 8.0;
        for (Entity entity : player.level().entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living) || living == player || !living.isAlive()) continue;
            double distance = player.distanceToSqr(living);
            if (distance < bestDistance) { bestDistance = distance; best = living; }
        }
        return best;
    }

    private static int accent() { return switch (FuxSettingsRegistry.ACCENT.get()) { case "BLUE" -> 0xFF4F8CFF; case "GREEN" -> 0xFF45C98A; default -> 0xFF8B5CF6; }; }
    private static float normalize(float d) { d %= 360f; return d < 0 ? d + 360 : d; }
    private static float shortest(float d) { d %= 360f; if (d > 180) d -= 360; if (d < -180) d += 360; return d; }
    private static String directionFor(float h) { return switch (Math.floorMod(Math.round(normalize(h) / 45f), 8)) { case 0 -> "S"; case 1 -> "SW"; case 2 -> "W"; case 3 -> "NW"; case 4 -> "N"; case 5 -> "NE"; case 6 -> "E"; default -> "SE"; }; }
    private static boolean isCardinal(String s) { return s.length() == 1; }
}
