package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import com.cuuw619.fuxapi.module.FuxModule;
import com.cuuw619.fuxapi.module.FuxModuleManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

/** Responsive Pulse-inspired client menu with animated transitions and lightweight visuals. */
public final class FuxPulseMenuScreen extends Screen {
    private static final int W = 900, H = 540, SIDE = 180;
    private static final String[] CATS = {"Combat", "Movement", "Player", "Render", "World", "Misc", "Settings"};
    private int x, y, category;
    private int previousCategory;
    private float scale = 1.0F;
    private boolean dragging;
    private boolean closing;
    private double ox, oy;
    private long openedAt;
    private long categoryChangedAt;
    private long closingAt;
    private final Map<String, Long> moduleChangedAt = new HashMap<>();

    public FuxPulseMenuScreen(Screen parent) { super(Component.literal("Fux API")); }

    @Override protected void init() {
        scale = Math.min(1.0F, Math.min((width - 20.0F) / W, (height - 20.0F) / H));
        x = Math.max(10, (int)((width / scale - W) / 2));
        y = Math.max(10, (int)((height / scale - H) / 2));
        openedAt = System.currentTimeMillis();
        categoryChangedAt = openedAt;
    }

    @Override public void tick() {
        if (closing && System.currentTimeMillis() - closingAt >= 180L) minecraft.setScreen(null);
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float pt) {
        g.fill(0, 0, width, height, 0xE9000000);
        g.pose().pushPose();
        g.pose().scale(scale, scale, 1.0F);
        int mx = (int)(mouseX / scale), my = (int)(mouseY / scale);

        float open = FuxUiAnimation.easeOutBack(FuxUiAnimation.progress(openedAt, 220));
        float close = closing ? 1.0F - FuxUiAnimation.easeOutCubic(FuxUiAnimation.progress(closingAt, 180)) : 1.0F;
        float visibility = open * close;
        int drawX = x + (int)((W * (1.0F - open)) / 2.0F);
        int drawY = y + (int)(H * (1.0F - open));
        drawVisualBackground(g, drawX, drawY, visibility);
        drawPanel(g, drawX, drawY, mx, my, visibility);
        g.pose().popPose();
        super.render(g, mouseX, mouseY, pt);
    }

    private void drawVisualBackground(GuiGraphics g, int px, int py, float visibility) {
        int accent = accent();
        float time = (System.currentTimeMillis() % 5000L) / 5000.0F;
        int glow = ((int)(34.0F * visibility) << 24) | (accent & 0x00FFFFFF);
        g.fill(px - 26, py - 20, px + W + 26, py + H + 20, glow);
        if (!FuxSettingsRegistry.COMPACT_MODE.get()) {
            for (int i = 0; i < 18; i++) {
                float wave = (float)Math.sin(time * Math.PI * 2.0 + i * 0.72) * 18.0F;
                int dotX = px + 20 + (i * 53) % (W - 40);
                int dotY = py + 34 + (i * 31) % (H - 68) + (int)wave;
                int alpha = (int)(28 * visibility);
                g.fill(dotX, dotY, dotX + 2, dotY + 2, (alpha << 24) | (accent & 0x00FFFFFF));
            }
        }
    }

    private void drawPanel(GuiGraphics g, int px, int py, int mx, int my, float visibility) {
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
        float catProgress = FuxUiAnimation.easeOutCubic(FuxUiAnimation.progress(categoryChangedAt, 150));
        int catShift = (int)((1.0F - catProgress) * 22.0F);
        g.drawString(font, CATS[category], left + catShift, top, 0xFFFFFFFF, false);
        g.drawString(font, category == 6 ? "Interface settings" : "Client modules", left + catShift, top + 19, 0xFF666B76, false);
        int close = right - 22;
        boolean closeHover = inside(mx, my, close, py + 15, 22, 22);
        g.fill(close, py + 15, close + 22, py + 37, closeHover ? 0xFF30232A : 0xFF1E2229);
        g.drawCenteredString(font, Component.literal("×"), close + 11, py + 20, 0xFFE1E2E7);

        if (category == 6) drawSettings(g, left + catShift, py + 76, right - left, mx, my);
        else drawModules(g, left + catShift, py + 76, right - left, mx, my);
        if (visibility < 1.0F) g.fill(px, py, px + W, py + H, ((int)((1.0F - visibility) * 90) << 24));
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
            float hoverLift = hover && FuxSettingsRegistry.ANIMATIONS.get() ? 2.0F : 0.0F;
            cy -= (int)hoverLift;
            boolean enabled = m.isEnabled();
            long changed = moduleChangedAt.getOrDefault(m.getName(), 0L);
            float toggleProgress = changed == 0L ? 1.0F : FuxUiAnimation.easeOutCubic(FuxUiAnimation.progress(changed, 160));
            g.fill(left, cy, left + w, cy + 52, hover ? 0xFF20252E : 0xFF171B21);
            if (enabled) g.fill(left, cy, left + 4, cy + 52, accent());
            g.drawString(font, m.getName(), left + 16, cy + 11, 0xFFE7E8EC, false);
            g.drawString(font, description(m), left + 16, cy + 30, 0xFF69707B, false);
            int sx = left + w - 52;
            g.fill(sx, cy + 17, sx + 38, cy + 35, enabled ? accent() : 0xFF303640);
            int knobStart = enabled ? sx + 22 : sx + 3;
            int knob = (int)(sx + 3 + (knobStart - (sx + 3)) * toggleProgress);
            g.fill(knob, cy + 19, knob + 12, cy + 33, 0xFFEDEEF2);
        }
    }

    private String description(FuxModule m) {
        return switch (m.getName()) {
            case "Sprint" -> "Automatically sprints while moving forward.";
            case "AutoWalk" -> "Automatically moves forward while active.";
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
        if (inside(mx, my, x + W - 50, y + 15, 24, 24)) { closeAnimated(); return true; }
        if (inside(mx, my, x, y, W, 55)) { dragging = true; ox = mx - x; oy = my - y; return true; }
        for (int i = 0; i < CATS.length; i++) {
            int cy = y + 84 + i * 48;
            if (inside(mx, my, x + 10, cy, SIDE - 20, 38)) {
                if (category != i) { previousCategory = category; category = i; categoryChangedAt = System.currentTimeMillis(); }
                return true;
            }
        }
        if (category < 6) {
            var modules = FuxModuleManager.byCategory(CATS[category]);
            int left = x + SIDE + 30, top = y + 76, w = W - SIDE - 58;
            for (int i = 0; i < modules.size(); i++) {
                if (inside(mx, my, left, top + i * 62, w, 52)) {
                    modules.get(i).toggle();
                    moduleChangedAt.put(modules.get(i).getName(), System.currentTimeMillis());
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void closeAnimated() {
        if (closing) return;
        closing = true;
        closingAt = System.currentTimeMillis();
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (dragging && button == 0 && !closing) {
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
        if (keyCode == 256) { closeAnimated(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public void onClose() { closeAnimated(); }

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
