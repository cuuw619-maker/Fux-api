package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import com.cuuw619.fuxapi.module.FuxModule;
import com.cuuw619.fuxapi.module.FuxModuleManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Responsive Pulse-style Fux client menu. */
public final class FuxPulseMenuScreen extends Screen {
    private static final int W = 900, H = 540, SIDE = 180;
    private static final String[] CATS = {"Combat", "Movement", "Player", "Render", "World", "Misc", "Settings"};
    private int x, y, category;
    private float scale = 1.0F;
    private boolean dragging;
    private double ox, oy;
    private long openedAt;

    public FuxPulseMenuScreen(Screen parent) { super(Component.literal("Fux API")); }

    @Override protected void init() {
        scale = Math.min(1.0F, Math.min((width - 20.0F) / W, (height - 20.0F) / H));
        x = Math.max(10, (int)((width / scale - W) / 2));
        y = Math.max(10, (int)((height / scale - H) / 2));
        openedAt = System.currentTimeMillis();
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float pt) {
        // Deliberately do not call renderBackground(): blur implementations must stay behind this UI.
        g.fill(0, 0, width, height, 0xE9000000);
        g.pose().pushPose();
        g.pose().scale(scale, scale, 1.0F);
        int mx = (int)(mouseX / scale), my = (int)(mouseY / scale);
        float progress = Math.min(1.0F, (System.currentTimeMillis() - openedAt) / 180.0F);
        float eased = 1.0F - (1.0F - progress) * (1.0F - progress);
        int drawX = x + (int)((W * (1.0F - eased)) / 2.0F);
        int drawY = y + (int)(H * (1.0F - eased));
        drawPanel(g, drawX, drawY, mx, my, progress);
        g.pose().popPose();
        super.render(g, mouseX, mouseY, pt);
    }

    private void drawPanel(GuiGraphics g, int px, int py, int mx, int my, float progress) {
        int accent = accent();
        g.fill(px + 8, py + 10, px + W + 8, py + H + 10, 0x55000000);
        g.fill(px, py, px + W, py + H, 0xF0080B10);
        g.fill(px, py, px + W, py + 3, accent);
        g.fill(px, py + 3, px + SIDE, py + H, 0xFF0D1117);
        g.drawString(font, "FUX", px + 22, py + 20, 0xFFFFFFFF, false);
        g.drawString(font, "CLIENT", px + 22, py + 38, accent, false);
        g.drawString(font, "MODULES", px + 22, py + 70, 0xFF626874, false);

        for (int i = 0; i < CATS.length; i++) {
            int cy = py + 84 + i * 48;
            boolean selected = category == i;
            boolean hover = inside(mx, my, px + 10, cy, SIDE - 20, 38);
            if (selected) {
                g.fill(px + 10, cy, px + SIDE - 10, cy + 38, 0xFF28202F);
                g.fill(px + 10, cy, px + 13, cy + 38, accent);
            } else if (hover) g.fill(px + 10, cy, px + SIDE - 10, cy + 38, 0xFF1B2028);
            g.drawString(font, CATS[i], px + 26, cy + 12, selected ? 0xFFFFFFFF : 0xFF9DA1AB, false);
        }
        g.drawString(font, "1.21.1 / NeoForge", px + 22, py + H - 22, 0xFF444954, false);

        int left = px + SIDE + 30, top = py + 22, right = px + W - 28;
        g.drawString(font, CATS[category], left, top, 0xFFFFFFFF, false);
        g.drawString(font, category == 6 ? "Interface settings" : "Client modules", left, top + 19, 0xFF666B76, false);
        int close = right - 22;
        boolean closeHover = inside(mx, my, close, py + 15, 22, 22);
        g.fill(close, py + 15, close + 22, py + 37, closeHover ? 0xFF30232A : 0xFF1E2229);
        g.drawCenteredString(font, Component.literal("×"), close + 11, py + 20, 0xFFE1E2E7);

        if (category == 6) drawSettings(g, left, py + 76, right - left, mx, my);
        else drawModules(g, left, py + 76, right - left, mx, my);
        if (progress < 1.0F) g.fill(px, py, px + W, py + H, ((int)((1.0F - progress) * 90) << 24));
    }

    private void drawModules(GuiGraphics g, int left, int top, int w, int mx, int my) {
        var modules = FuxModuleManager.byCategory(CATS[category]);
        if (modules.isEmpty()) {
            g.fill(left, top, left + w, top + H - 102, 0xFF13171D);
            g.drawString(font, "No modules registered", left + 20, top + 22, 0xFFD9DBE1, false);
            g.drawString(font, "Module slots are ready for the next stage.", left + 20, top + 43, 0xFF666B76, false);
            return;
        }
        for (int i = 0; i < modules.size(); i++) {
            FuxModule m = modules.get(i);
            int cy = top + i * 62;
            boolean hover = inside(mx, my, left, cy, w, 52);
            g.fill(left, cy, left + w, cy + 52, hover ? 0xFF20252E : 0xFF171B21);
            if (m.isEnabled()) g.fill(left, cy, left + 4, cy + 52, accent());
            g.drawString(font, m.getName(), left + 16, cy + 11, 0xFFE7E8EC, false);
            g.drawString(font, description(m), left + 16, cy + 30, 0xFF69707B, false);
            int sx = left + w - 52;
            g.fill(sx, cy + 17, sx + 38, cy + 35, m.isEnabled() ? accent() : 0xFF303640);
            g.fill(m.isEnabled() ? sx + 22 : sx + 3, cy + 19, m.isEnabled() ? sx + 34 : sx + 15, cy + 33, 0xFFEDEEF2);
        }
    }

    private String description(FuxModule m) {
        return switch (m.getName()) {
            case "Sprint" -> "Automatically sprints while moving forward.";
            case "AutoJump" -> "Enables automatic jumping.";
            case "FOV" -> "Sets the client field of view to 100.";
            case "Fullbright" -> "Raises client gamma while enabled.";
            default -> "Client module";
        };
    }

    private void drawSettings(GuiGraphics g, int left, int top, int w, int mx, int my) {
        g.drawString(font, "Interface", left, top, 0xFF8F94A0, false);
        toggle(g, "Animations", FuxSettingsRegistry.ANIMATIONS.get(), left, top + 26, w, mx, my);
        toggle(g, "Blur compatibility", FuxSettingsRegistry.BLUR.get(), left, top + 72, w, mx, my);
        toggle(g, "Compact mode", FuxSettingsRegistry.COMPACT_MODE.get(), left, top + 118, w, mx, my);
        g.fill(left, top + 174, left + w, top + 230, 0xFF181B22);
        g.drawString(font, "UI scale", left + 15, top + 187, 0xFFE2E3E8, false);
        g.drawString(font, String.format("%.2f", FuxSettingsRegistry.UI_SCALE.get()), left + w - 50, top + 187, accent(), false);
    }

    private void toggle(GuiGraphics g, String name, boolean value, int x, int y, int w, int mx, int my) {
        g.fill(x, y, x + w, y + 38, inside(mx, my, x, y, w, 38) ? 0xFF20242D : 0xFF181B22);
        g.drawString(font, name, x + 14, y + 12, 0xFFE2E3E8, false);
        int sx = x + w - 48;
        g.fill(sx, y + 10, sx + 34, y + 26, value ? accent() : 0xFF30343D);
        g.fill(value ? sx + 20 : sx + 2, y + 12, value ? sx + 31 : sx + 13, y + 24, 0xFFEDEEF2);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        double mx = mouseX / scale, my = mouseY / scale;
        if (inside(mx, my, x + W - 50, y + 15, 24, 24)) { onClose(); return true; }
        if (inside(mx, my, x, y, W, 55)) { dragging = true; ox = mx - x; oy = my - y; return true; }
        for (int i = 0; i < CATS.length; i++) {
            int cy = y + 84 + i * 48;
            if (inside(mx, my, x + 10, cy, SIDE - 20, 38)) { category = i; return true; }
        }
        if (category < 6) {
            var modules = FuxModuleManager.byCategory(CATS[category]);
            int left = x + SIDE + 30, top = y + 76, w = W - SIDE - 58;
            for (int i = 0; i < modules.size(); i++) {
                if (inside(mx, my, left, top + i * 62, w, 52)) { modules.get(i).toggle(); return true; }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (dragging && button == 0) {
            double mx = mouseX / scale, my = mouseY / scale;
            x = clamp((int)(mx - ox), 10, Math.max(10, (int)(width / scale) - W - 10));
            y = clamp((int)(my - oy), 10, Math.max(10, (int)(height / scale) - H - 10));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { onClose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private int accent() {
        return switch (FuxSettingsRegistry.ACCENT.get()) {
            case "BLUE" -> 0xFF4F8CFF;
            case "GREEN" -> 0xFF45C98A;
            default -> 0xFF8B5CF6;
        };
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) { return mx >= x && mx < x + w && my >= y && my < y + h; }
    private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }
}
