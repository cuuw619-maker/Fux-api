package com.cuuw619.fuxapi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@EventBusSubscriber(modid = FuxApi.MOD_ID, value = Dist.CLIENT)
public final class FuxHudOverlay {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");

    private FuxHudOverlay() {}

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui || mc.screen != null) return;

        GuiGraphics g = event.getGuiGraphics();
        int width = g.guiWidth();
        int accent = 0xFF8B5CF6;
        int center = width / 2;

        drawCompass(g, center, 18, width, player.getYRot(), accent);
        drawInfo(g, width - 14, 18, mc, accent);
    }

    private static void drawCompass(GuiGraphics g, int center, int top, int width, float yaw, int accent) {
        int compassWidth = Math.min(720, width - 80);
        int left = center - compassWidth / 2;
        int right = center + compassWidth / 2;
        int height = 48;

        g.fill(left + 4, top + 5, right + 4, top + height + 5, 0x50000000);
        g.fill(left, top, right, top + height, 0xC20A0D12);
        g.fill(left, top, right, top + 2, accent);
        g.fill(center - 1, top + 5, center + 1, top + height - 5, accent);

        float degreesPerPixel = 180.0F / compassWidth;
        float centerHeading = normalize(yaw);
        for (int deg = -180; deg <= 180; deg += 10) {
            float heading = centerHeading + deg;
            float delta = shortest(heading - centerHeading);
            int x = center + Math.round(delta / degreesPerPixel);
            if (x < left + 5 || x > right - 5) continue;
            boolean major = deg % 30 == 0;
            int tick = major ? 12 : 7;
            g.fill(x, top + height - tick - 4, x + 1, top + height - 4, major ? accent : 0xFF59606C);
            if (major) {
                String label = directionFor(heading);
                int color = isCardinal(label) ? 0xFFF1F2F5 : 0xFF9CA2AD;
                g.drawCenteredString(Minecraft.getInstance().font, label, x, top + 8, color);
            }
        }
        g.drawCenteredString(Minecraft.getInstance().font, "▼", center, top + height - 8, 0xFFFFFFFF);
    }

    private static void drawInfo(GuiGraphics g, int right, int top, Minecraft mc, int accent) {
        String fps = mc.getFps() + " FPS";
        String time = LocalTime.now().format(CLOCK);
        int boxW = 108;
        int x = right - boxW;

        g.fill(x + 4, top + 4, right + 4, top + 70, 0x50000000);
        g.fill(x, top, right, top + 66, 0xC20A0D12);
        g.fill(x, top, x + 3, top + 66, accent);
        g.drawString(mc.font, fps, x + 12, top + 12, 0xFFE8E9ED, false);
        g.drawString(mc.font, time, x + 12, top + 31, accent, false);
        g.drawString(mc.font, "SYSTEM", x + 12, top + 49, 0xFF6E7480, false);
    }

    private static float normalize(float degrees) {
        degrees %= 360.0F;
        return degrees < 0 ? degrees + 360.0F : degrees;
    }

    private static float shortest(float degrees) {
        degrees %= 360.0F;
        if (degrees > 180.0F) degrees -= 360.0F;
        if (degrees < -180.0F) degrees += 360.0F;
        return degrees;
    }

    private static String directionFor(float heading) {
        int sector = Math.floorMod(Math.round(normalize(heading) / 45.0F), 8);
        return switch (sector) {
            case 0 -> "S";
            case 1 -> "SW";
            case 2 -> "W";
            case 3 -> "NW";
            case 4 -> "N";
            case 5 -> "NE";
            case 6 -> "E";
            default -> "SE";
        };
    }

    private static boolean isCardinal(String label) {
        return label.length() == 1;
    }
}
