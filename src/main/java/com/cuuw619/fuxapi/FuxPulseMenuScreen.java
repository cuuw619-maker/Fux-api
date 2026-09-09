package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import com.cuuw619.fuxapi.module.FuxModule;
import com.cuuw619.fuxapi.module.FuxModuleManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Pulse-inspired client cheat menu. */
public final class FuxPulseMenuScreen extends Screen {
    private static final int W = 820, H = 480, SIDE = 168;
    private static final String[] CATS = {"Combat", "Movement", "Player", "Render", "World", "Misc", "Settings"};
    private int x, y, category;
    private boolean dragging;
    private double ox, oy;

    public FuxPulseMenuScreen(Screen parent) { super(Component.literal("Fux API")); }
    @Override protected void init() { x = Math.max(8, (width - W) / 2); y = Math.max(8, (height - H) / 2); }

    @Override public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g, mx, my, pt);
        g.fill(0, 0, width, height, 0x72000000);
        g.fill(x + 8, y + 10, x + W + 8, y + H + 10, 0x66000000);
        g.fill(x, y, x + W, y + H, 0xF00C1016);
        g.fill(x, y, x + W, y + 3, accent());
        drawSide(g, mx, my); drawMain(g, mx, my); super.render(g, mx, my, pt);
    }

    private void drawSide(GuiGraphics g, int mx, int my) {
        g.fill(x, y + 3, x + SIDE, y + H, 0xFF10141B);
        g.drawString(font, "FUX", x + 22, y + 19, 0xFFFFFFFF, false);
        g.drawString(font, "CLIENT", x + 22, y + 36, accent(), false);
        g.drawString(font, "MODULES", x + 22, y + 64, 0xFF626874, false);
        for (int i = 0; i < CATS.length; i++) {
            int cy = y + 78 + i * 44; boolean selected = category == i;
            if (selected) { g.fill(x + 10, cy, x + SIDE - 10, cy + 34, 0xFF28202F); g.fill(x + 10, cy, x + 13, cy + 34, accent()); }
            else if (inside(mx, my, x + 10, cy, SIDE - 20, 34)) g.fill(x + 10, cy, x + SIDE - 10, cy + 34, 0xFF1D2129);
            g.drawString(font, CATS[i], x + 25, cy + 10, selected ? 0xFFFFFFFF : 0xFF9DA1AB, false);
        }
        g.drawString(font, "1.21.1 / NeoForge", x + 22, y + H - 20, 0xFF444954, false);
    }

    private void drawMain(GuiGraphics g, int mx, int my) {
        int left = x + SIDE + 26, top = y + 20, right = x + W - 26;
        g.drawString(font, CATS[category], left, top, 0xFFFFFFFF, false);
        g.drawString(font, category == 6 ? "Interface settings" : "Client modules", left, top + 18, 0xFF666B76, false);
        int close = right - 22;
        g.fill(close, y + 15, close + 22, y + 37, inside(mx, my, close, y + 15, 22, 22) ? 0xFF30232A : 0xFF1E2229);
        g.drawCenteredString(font, Component.literal("×"), close + 11, y + 20, 0xFFE1E2E7);
        int cardY = y + 70, cw = right - left;
        if (category == 6) drawSettings(g, left, cardY, cw, mx, my);
        else drawModules(g, left, cardY, cw, mx, my);
    }

    private void drawModules(GuiGraphics g, int x, int y, int w, int mx, int my) {
        var modules = FuxModuleManager.byCategory(CATS[category]);
        if (modules.isEmpty()) {
            g.fill(x, y, x + w, y + H - 98, 0xFF15181F);
            g.drawString(font, "No modules registered", x + 18, y + 20, 0xFFD9DBE1, false);
            g.drawString(font, "This category is reserved for the next module stage.", x + 18, y + 40, 0xFF666B76, false);
            return;
        }
        for (int i = 0; i < modules.size(); i++) {
            FuxModule module = modules.get(i);
            int cy = y + i * 54;
            boolean hover = inside(mx, my, x, cy, w, 46);
            g.fill(x, cy, x + w, cy + 46, hover ? 0xFF20242D : 0xFF181B22);
            if (module.isEnabled()) g.fill(x, cy, x + 3, cy + 46, accent());
            g.drawString(font, module.getName(), x + 14, cy + 9, 0xFFE2E3E8, false);
            g.drawString(font, description(module), x + 14, cy + 26, 0xFF626773, false);
            int sx = x + w - 48;
            g.fill(sx, cy + 15, sx + 34, cy + 31, module.isEnabled() ? accent() : 0xFF30343D);
            g.fill(module.isEnabled() ? sx + 20 : sx + 2, cy + 17, module.isEnabled() ? sx + 31 : sx + 13, cy + 29, 0xFFEDEEF2);
        }
    }

    private String description(FuxModule module) {
        return switch (module.getName()) {
            case "Sprint" -> "Automatically keeps sprint active while moving forward.";
            case "Fullbright" -> "Raises client gamma while enabled.";
            case "AutoJump" -> "Enables Minecraft automatic jumping.";
            default -> "Client module";
        };
    }

    private void drawSettings(GuiGraphics g, int x, int y, int w, int mx, int my) {
        g.drawString(font, "Interface", x, y, 0xFF8F94A0, false);
        toggle(g, "Animations", FuxSettingsRegistry.ANIMATIONS.get(), x, y + 24, w, mx, my);
        toggle(g, "Blur", FuxSettingsRegistry.BLUR.get(), x, y + 70, w, mx, my);
        toggle(g, "Compact mode", FuxSettingsRegistry.COMPACT_MODE.get(), x, y + 116, w, mx, my);
        g.fill(x, y + 174, x + w, y + 226, 0xFF181B22);
        g.drawString(font, "UI scale", x + 14, y + 184, 0xFFE2E3E8, false);
        g.drawString(font, String.format("%.2f", FuxSettingsRegistry.UI_SCALE.get()), x + w - 48, y + 184, accent(), false);
        int bx = x + 14, by = y + 207, bw = w - 28; g.fill(bx, by, bx + bw, by + 3, 0xFF30343D);
        int fill = (int)(bw * ((FuxSettingsRegistry.UI_SCALE.get() - .75) / .75)); g.fill(bx, by, bx + Math.max(2, Math.min(bw, fill)), by + 3, accent());
        g.drawString(font, "Full settings are available from the Mods menu.", x, y + 246, 0xFF626773, false);
    }

    private void toggle(GuiGraphics g, String name, boolean value, int x, int y, int w, int mx, int my) {
        g.fill(x, y, x + w, y + 38, inside(mx, my, x, y, w, 38) ? 0xFF20242D : 0xFF181B22);
        g.drawString(font, name, x + 14, y + 12, 0xFFE2E3E8, false);
        int sx = x + w - 48; g.fill(sx, y + 10, sx + 34, y + 26, value ? accent() : 0xFF30343D);
        g.fill(value ? sx + 20 : sx + 2, y + 12, value ? sx + 31 : sx + 13, y + 24, 0xFFEDEEF2);
    }

    @Override public boolean mouseClicked(double mx, double my, int button) {
        if (button != 0) return super.mouseClicked(mx, my, button);
        if (inside(mx, my, x + W - 48, y + 15, 22, 22)) { onClose(); return true; }
        if (inside(mx, my, x, y, W, 54)) { dragging = true; ox = mx - x; oy = my - y; return true; }
        for (int i = 0; i < CATS.length; i++) {
            int cy = y + 78 + i * 44;
            if (inside(mx, my, x + 10, cy, SIDE - 20, 34)) { category = i; return true; }
        }
        if (category < 6) {
            var modules = FuxModuleManager.byCategory(CATS[category]);
            int left = x + SIDE + 26, top = y + 70, right = x + W - 26;
            for (int i = 0; i < modules.size(); i++) {
                int cy = top + i * 54;
                if (inside(mx, my, left, cy, right - left, 46)) { modules.get(i).toggle(); return true; }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging && button == 0) { x = clamp((int)(mx - ox), 8, Math.max(8, width - W - 8)); y = clamp((int)(my - oy), 8, Math.max(8, height - H - 8)); return true; }
        return super.mouseDragged(mx, my, button, dx, dy);
    }
    @Override public boolean mouseReleased(double mx, double my, int button) { if (button == 0) dragging = false; return super.mouseReleased(mx, my, button); }
    private int accent() { return switch (FuxSettingsRegistry.ACCENT.get()) { case "BLUE" -> 0xFF4F8CFF; case "GREEN" -> 0xFF45C98A; default -> 0xFF8B5CF6; }; }
    private static boolean inside(double mx, double my, int x, int y, int w, int h) { return mx >= x && mx < x + w && my >= y && my < y + h; }
    private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }
}
