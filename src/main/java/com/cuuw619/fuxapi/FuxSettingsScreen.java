package com.cuuw619.fuxapi;

import com.cuuw619.fuxapi.config.FuxSettingsRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class FuxSettingsScreen extends Screen {
    private final Screen parent;
    private long openedAt, closingAt;
    private boolean closing;

    public FuxSettingsScreen(Screen parent) { super(Component.literal("Fux API Settings")); this.parent = parent; }
    @Override protected void init() { openedAt = System.currentTimeMillis(); }
    @Override public void tick() { if (closing && System.currentTimeMillis() - closingAt >= 180L) minecraft.setScreen(parent); }

    @Override public void render(GuiGraphics g, int mx, int my, float pt) {
        g.fill(0, 0, width, height, 0xEA000000);
        float open = FuxUiAnimation.easeOutBack(FuxUiAnimation.progress(openedAt, 220));
        float close = closing ? 1.0F - FuxUiAnimation.easeOutCubic(FuxUiAnimation.progress(closingAt, 180)) : 1.0F;
        float v = open * close;
        int w = Math.min(760, width - 32), h = Math.min(470, height - 24);
        int baseX = (width - w) / 2, baseY = (height - h) / 2;
        int x = baseX + (int)((w * (1.0F - open)) / 2.0F), y = baseY + (int)(h * (1.0F - open));
        int accent = accent();
        g.fill(x - 18, y - 12, x + w + 18, y + h + 12, ((int)(30 * v) << 24) | (accent & 0x00FFFFFF));
        g.fill(x + 6, y + 8, x + w + 6, y + h + 8, 0x66000000);
        g.fill(x, y, x + w, y + h, 0xF00F1117);
        g.fill(x, y, x + w, y + 3, accent);
        g.fill(x, y + 3, x + 178, y + h, 0xFF12151C);
        text(g, "FUX", x + 22, y + 20, 0xFFFFFFFF);
        text(g, "API", x + 22, y + 39, accent);
        text(g, "SETTINGS", x + 22, y + 72, 0xFF777C88);
        text(g, "GENERAL", x + 22, y + 98, 0xFFAEB2BC);
        int cx = x + 202, cw = w - 226;
        text(g, "Interface", cx, y + 22, 0xFFFFFFFF);
        text(g, "Fux API presentation and interaction", cx, y + 42, 0xFF707580);
        drawToggle(g, "Animations", "Smooth menu transitions", FuxSettingsRegistry.ANIMATIONS.get(), cx, y + 70, cw, mx, my);
        drawToggle(g, "Compact mode", "Reduce spacing between controls", FuxSettingsRegistry.COMPACT_MODE.get(), cx, y + 124, cw, mx, my);
        drawValue(g, "Zoom camera", "Z key camera FOV", FuxSettingsRegistry.ZOOM_FOV.get(), cx, y + 178, cw, mx, my);
        drawSlider(g, "UI scale", "Interface size", FuxSettingsRegistry.UI_SCALE.get(), cx, y + 232, cw, mx, my);
        drawAccent(g, cx, y + 298, cw, mx, my);
        text(g, "Changes are saved automatically", cx, y + h - 28, 0xFF666B76);
        drawClose(g, x + w - 42, y + 16, mx, my);
        if (v < 1.0F) g.fill(x, y, x + w, y + h, ((int)((1.0F - v) * 80) << 24));
        super.render(g, mx, my, pt);
    }

    private void drawToggle(GuiGraphics g, String name, String desc, boolean value, int x, int y, int w, int mx, int my) {
        g.fill(x, y, x + w, y + 44, inside(mx, my, x, y, w, 44) ? 0xFF20242D : 0xFF181B22);
        text(g, name, x + 14, y + 8, 0xFFE3E5EA); text(g, desc, x + 14, y + 26, 0xFF707580);
        int sx = x + w - 52, sy = y + 13;
        g.fill(sx, sy, sx + 38, sy + 18, value ? accent() : 0xFF30343D);
        g.fill(value ? sx + 22 : sx + 2, sy + 3, value ? sx + 34 : sx + 14, sy + 15, 0xFFEDEEF2);
    }

    private void drawValue(GuiGraphics g, String name, String desc, double value, int x, int y, int w, int mx, int my) {
        g.fill(x, y, x + w, y + 44, inside(mx, my, x, y, w, 44) ? 0xFF20242D : 0xFF181B22);
        text(g, name, x + 14, y + 8, 0xFFE3E5EA); text(g, desc, x + 14, y + 26, 0xFF707580);
        text(g, String.format("%.0f", value), x + w - 42, y + 14, accent());
    }

    private void drawSlider(GuiGraphics g, String name, String desc, double value, int x, int y, int w, int mx, int my) {
        g.fill(x, y, x + w, y + 56, inside(mx, my, x, y, w, 56) ? 0xFF20242D : 0xFF181B22);
        text(g, name, x + 14, y + 8, 0xFFE3E5EA); text(g, desc, x + 14, y + 26, 0xFF707580);
        int bx = x + 180, by = y + 31, bw = w - 236;
        g.fill(bx, by, bx + bw, by + 4, 0xFF30343D);
        int fill = (int)(bw * ((value - 0.75) / 0.75));
        g.fill(bx, by, bx + Math.max(2, Math.min(bw, fill)), by + 4, accent());
        int knob = bx + Math.max(0, Math.min(bw, fill)); g.fill(knob - 4, by - 4, knob + 4, by + 12, 0xFFEDEEF2);
        text(g, String.format("%.2f", value), x + w - 48, y + 8, accent());
    }

    private void drawAccent(GuiGraphics g, int x, int y, int w, int mx, int my) {
        g.fill(x, y, x + w, y + 52, inside(mx, my, x, y, w, 52) ? 0xFF20242D : 0xFF181B22);
        text(g, "Accent", x + 14, y + 8, 0xFFE3E5EA); text(g, "Theme highlight", x + 14, y + 26, 0xFF707580);
        String[] colors = {"PURPLE", "BLUE", "GREEN"};
        for (int i = 0; i < colors.length; i++) { int bx = x + 180 + i * 64; boolean selected = colors[i].equals(FuxSettingsRegistry.ACCENT.get()); g.fill(bx, y + 14, bx + 54, y + 34, selected ? accent() : 0xFF292D36); g.drawCenteredString(font, colors[i], bx + 27, y + 20, selected ? 0xFFFFFFFF : 0xFF888D98); }
    }

    private void drawClose(GuiGraphics g, int x, int y, int mx, int my) { boolean h = inside(mx, my, x, y, 22, 22); g.fill(x, y, x + 22, y + 22, h ? 0xFF30242B : 0xFF20232A); g.drawCenteredString(font, Component.literal("×"), x + 11, y + 5, 0xFFE1E2E7); }

    @Override public boolean mouseClicked(double mx, double my, int button) {
        if (button != 0 || closing) return super.mouseClicked(mx, my, button);
        int w = Math.min(760, width - 32), h = Math.min(470, height - 24), x = (width - w) / 2, y = (height - h) / 2, cx = x + 202, cw = w - 226;
        if (inside(mx, my, x + w - 42, y + 16, 22, 22)) { closeAnimated(); return true; }
        if (inside(mx, my, cx, y + 70, cw, 44)) { FuxSettingsRegistry.ANIMATIONS.set(!FuxSettingsRegistry.ANIMATIONS.get()); save(); return true; }
        if (inside(mx, my, cx, y + 124, cw, 44)) { FuxSettingsRegistry.COMPACT_MODE.set(!FuxSettingsRegistry.COMPACT_MODE.get()); save(); return true; }
        if (inside(mx, my, cx, y + 178, cw, 44)) { double n = FuxSettingsRegistry.ZOOM_FOV.get() + 5.0; if (n > 60) n = 10; FuxSettingsRegistry.ZOOM_FOV.set(n); save(); return true; }
        if (inside(mx, my, cx, y + 232, cw, 56)) { double n = FuxSettingsRegistry.UI_SCALE.get() + 0.1; if (n > 1.5) n = 0.75; FuxSettingsRegistry.UI_SCALE.set(Math.round(n * 100.0) / 100.0); save(); return true; }
        if (inside(mx, my, cx + 180, y + 298, 190, 52)) { String a = FuxSettingsRegistry.ACCENT.get(); FuxSettingsRegistry.ACCENT.set(a.equals("PURPLE") ? "BLUE" : a.equals("BLUE") ? "GREEN" : "PURPLE"); save(); return true; }
        return super.mouseClicked(mx, my, button);
    }

    private void closeAnimated() { if (!closing) { closing = true; closingAt = System.currentTimeMillis(); } }
    @Override public void onClose() { closeAnimated(); }
    private void save() { FuxSettingsRegistry.save(); }
    private void text(GuiGraphics g, String s, int x, int y, int color) { g.pose().pushPose(); g.pose().translate(x, y, 0); g.pose().scale(1.08F, 1.08F, 1.0F); g.drawString(font, s, 0, 0, color, false); g.pose().popPose(); }
    private int accent() { return switch (FuxSettingsRegistry.ACCENT.get()) { case "BLUE" -> 0xFF4F8CFF; case "GREEN" -> 0xFF45C98A; default -> 0xFF8B5CF6; }; }
    private static boolean inside(double mx, double my, int x, int y, int w, int h) { return mx >= x && mx < x + w && my >= y && my < y + h; }
}
